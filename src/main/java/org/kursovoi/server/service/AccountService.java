package org.kursovoi.server.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.kursovoi.server.dto.*;
import org.kursovoi.server.model.Account;
import org.kursovoi.server.model.User;
import org.kursovoi.server.model.constant.OperationType;
import org.kursovoi.server.model.constant.Status;
import org.kursovoi.server.repository.AccountRepository;
import org.kursovoi.server.util.exception.*;
import org.kursovoi.server.util.keycloak.RoleMapping;
import org.kursovoi.server.util.keycloak.TokenUtil;
import org.kursovoi.server.util.mapper.AccountMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@RequiredArgsConstructor
public class AccountService {  //класс для работы со счетами пользователя

    private final AccountRepository accountRepository;
    private final AccountMapper mapper;
    private final TokenUtil tokenUtil;
    private final OperationService operationService;
    private final EmailSenderService emailSenderService;
    private UserService userService;

    @Transactional
    public List<AccountDto> getAllAccounts() {  //метод получения всех счетов
        return accountRepository.findAll().stream().map(mapper::map).collect(Collectors.toList());
    }

    @Transactional
    public AccountDto getSpecificAccountDto(long id) {  //метод получения счета по идентификатору
        var account = mapper.map(getSpecificAccount(id));
        if(tokenUtil.hasRole(RoleMapping.USER)
                && !userService.getUser(account.getHolderId()).getUuid()
                .equals(tokenUtil.getUUIDUser())) {
            throw new AccessDeniedException("Access denied for resource");
        }
        return account;
    }

    @Transactional
    public List<AccountDto> getAccountsOfUser(User user) {  //метод получения счетов определенного пользователя
        return accountRepository.findByHolder(user).stream().map(mapper::map).collect(Collectors.toList());
    }

    @Transactional
    public void createAccount(CreateAccountDto dto) {  //метод для создания счета
        Account newAccount = mapper.map(dto);
        newAccount.setStatus(Status.ACTIVE);
        User user = userService.getUser(dto.getHolderUuid());
        newAccount.setHolder(user);
        accountRepository.saveAndFlush(newAccount);
        logOperation(OperationDescription.NEW_ACCOUNT, OperationType.INFO, user.getId());
    }

    @Transactional
    public void deleteAccount(long idAccount) {  //метод для удаления счета
        accountRepository.delete(getSpecificAccount(idAccount));
    }

    @Transactional
    public void makeTransaction(TransactionDto transaction) {  //метод для проведения транзакции
        Account accountFrom = getSpecificAccount(transaction.getIdFrom());
        Account accountTo = getSpecificAccount(transaction.getIdTo());
        if (!accountFrom.getStatus().equals(Status.ACTIVE) || !accountTo.getStatus().equals(Status.ACTIVE)) {
            throw new AccountInvalidException("One of accounts is not active");
        }
        if (accountFrom.getSum() < transaction.getSum()) {
            throw new TransactionSumTooLargeException("Transaction sum too big");
        }

        accountFrom.setSum(accountFrom.getSum() - transaction.getSum());
        accountTo.setSum(accountTo.getSum() + transaction.getSum());

        accountRepository.save(accountTo);
        accountRepository.save(accountFrom);

        logOperation(OperationDescription.MAKE_TRANSACTION, OperationType.TRANSACTION, accountFrom.getHolder().getId());

        emailSenderService.sendTransactionReceipt(transaction, accountTo.getHolder().getEmail());
    }

    @Transactional
    public void updateStatusOfAccount(UpdateStatusDto dto) {  //метод для обновления статуса счета
        if (Arrays.stream(Status.values()).map(Enum::name).noneMatch(status -> status.equals(dto.getNewStatus()))) {
            throw new IncorrectStatusException("Status is incorrect");
        }
        var account = getSpecificAccount(dto.getId());
        account.setStatus(Status.valueOf(dto.getNewStatus()));
        accountRepository.save(account);
    }

    @Transactional
    Account getSpecificAccount(long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Account with id " + id + " not found!"));
    }

    private void logOperation(OperationDescription description, OperationType type, long idUser) {
        operationService.createOperation(OperationDto.builder()
                .type(type.name())
                .description(description.getMessage())
                .idUser(idUser).build());
    }
}

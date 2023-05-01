package org.kursovoi.server.controller;

import lombok.RequiredArgsConstructor;
import org.kursovoi.server.dto.AccountDto;
import org.kursovoi.server.dto.CreateAccountDto;
import org.kursovoi.server.dto.TransactionDto;
import org.kursovoi.server.dto.UpdateStatusDto;
import org.kursovoi.server.service.AccountService;
import org.kursovoi.server.util.keycloak.RoleMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController { //класс контроллер для счетов

    private final AccountService service; //объект сервиса счетов

    @GetMapping
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<List<AccountDto>> getAllAccounts() {  //метод для получения всех счетов
        var accounts = service.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<AccountDto> getSpecificAccount(@PathVariable long id) { //метод для получения счета по идентификатору
        var account = service.getSpecificAccountDto(id);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PostMapping
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountDto dto) { //метод для создания счета
        service.createAccount(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/transaction")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<?> makeTransaction(@RequestBody TransactionDto dto) { //метод для проведения транзакции
        service.makeTransaction(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<?> deleteAccount(@PathVariable long id) { //метод для удаления счета
        service.deleteAccount(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<?> updateStatusOfAccount(@RequestBody UpdateStatusDto dto) { //метод для обновления статуса счета
        service.updateStatusOfAccount(dto);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}

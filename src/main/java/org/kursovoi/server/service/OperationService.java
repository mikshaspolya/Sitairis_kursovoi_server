package org.kursovoi.server.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.kursovoi.server.dto.OperationDto;
import org.kursovoi.server.model.Operation;
import org.kursovoi.server.model.User;
import org.kursovoi.server.repository.OperationRepository;
import org.kursovoi.server.util.exception.AccessDeniedException;
import org.kursovoi.server.util.exception.ModelNotFoundException;
import org.kursovoi.server.util.keycloak.TokenUtil;
import org.kursovoi.server.util.keycloak.RoleMapping;
import org.kursovoi.server.util.mapper.OperationMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@RequiredArgsConstructor
public class OperationService {

    private final OperationRepository operationRepository;
    private UserService userService;
    private final OperationMapper mapper;
    private final TokenUtil tokenUtil;


    @Transactional
    public List<OperationDto> getAllOperations() {
        return operationRepository.findAll().stream().map(mapper::map).collect(Collectors.toList());
    }

    @Transactional
    public List<OperationDto> findAllOperationsOfUser(User user) {
        return operationRepository.findByUser(user).stream().map(mapper::map).collect(Collectors.toList());
    }

    @Transactional
    public OperationDto getSpecificOperationDto(long id) {
        var operation = mapper.map(getOperation(id));
        if(tokenUtil.hasRole(RoleMapping.USER)
                && !userService.getUser(operation.getIdUser()).getUuid()
                .equals(tokenUtil.getUUIDUser())) {
            throw new AccessDeniedException("Access denied for resource");
        }
        return operation;
    }

    @Transactional
    public void createOperation(OperationDto dto) {
        var user = userService.getUser(dto.getIdUser());
        var operation = mapper.map(dto);
        operation.setUser(user);
        operationRepository.save(operation);
    }

    Operation getOperation(long id) {
        return operationRepository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Operation with id: " + id + " - not found"));
    }
}

package org.kursovoi.server.controller;

import lombok.RequiredArgsConstructor;
import org.kursovoi.server.dto.*;
import org.kursovoi.server.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController { //класс контроллер для пользователей

    private final UserService service;  //объект сервиса пользователей

    @GetMapping
    @RolesAllowed({"ROLE_ADMIN"}) //роли, кому доступен данных метод
    public ResponseEntity<List<UserDto>> getAllUsers() { //метод для получения всех пользователей
        var users = service.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<UserDto> findUserDto(@PathVariable long id) { //метод для получения конкретного пользователя по идентификатору
        var user = service.findUserDto(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("{uuid}/operations")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<List<OperationDto>> findAllOperationsOfUser(@PathVariable String uuid) { //метод для получения истории пользователя
        var operations = service.findAllOperationsOfUser(uuid);
        return new ResponseEntity<>(operations, HttpStatus.OK);
    }

    @GetMapping("/{uuid}/loanOrders")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<List<LoanOrderDto>> findLoansOrdersOfUser(@PathVariable String uuid) { //метод для получения заявок на кредиты пользователя
        var loanOrders = service.findLoansOrdersOfUser(uuid);
        return new ResponseEntity<>(loanOrders, HttpStatus.OK);
    }

    @GetMapping("/{uuid}/depositOrders")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<List<DepositOrderDto>> findDepositOrdersOfUser(@PathVariable String uuid) { //метод для получения заявок на вклады пользователя
        var depositOrders = service.findDepositOrdersOfUser(uuid);
        return new ResponseEntity<>(depositOrders, HttpStatus.OK);
    }

    @GetMapping("/{uuid}/accounts")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<List<AccountDto>> getAccountsOfUser(@PathVariable String uuid) { //метод для получения счетов пользователя
        var accounts = service.getAccountsOfUser(uuid);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @PostMapping
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<?> createUser(@RequestBody CreateUserDto dto) { //метод для создания пользователя
        service.createUser(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{uuid}/status")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<?> updateUserStatus(@PathVariable String uuid, @RequestParam String newStatus) { //метод для обновления статуса пользователя
        UpdateUserStatusDto dto = new UpdateUserStatusDto(newStatus, uuid);
        service.updateUserStatus(dto);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PutMapping("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<?> updateUser(@PathVariable long id, @RequestBody UserDto dto) {  //метод для обновления данных пользователя
        dto.setId(id);
        service.updateUser(dto);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<?> deleteUser(@PathVariable long id) {  //метод для удаления пользователя
        service.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/me")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<UserDto> getMe() { //метод для получения информации о себе
        var user = service.getMe();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}


package org.kursovoi.server.repository;

import org.kursovoi.server.model.Operation;
import org.kursovoi.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    List<Operation> findByUser(User user);


}

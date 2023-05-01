package org.kursovoi.server.util.mapper;

import org.kursovoi.server.dto.OperationDto;
import org.kursovoi.server.model.Operation;
import org.kursovoi.server.model.constant.OperationType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public abstract class OperationMapper {

    @Mapping(target = "type", source = "type", qualifiedByName = "getTypeToString")
    @Mapping(target = "idUser", source = "user.id")
    public abstract OperationDto map(Operation model);

    @Mapping(target = "type", source = "type", qualifiedByName = "getTypeToEnum")
    public abstract Operation map(OperationDto dto);

    @Named("getTypeToString")
    public String getTypeToString(OperationType model) {
        return model.name();
    }

    @Named("getTypeToEnum")
    public OperationType getTypeToEnum(String dto) {
        return OperationType.valueOf(dto);
    }
}

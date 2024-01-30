package com.lsoria.qrsec.domain.dto.mapper;

import com.lsoria.qrsec.domain.dto.JsonUserLoginDTO;
import com.lsoria.qrsec.domain.dto.UserDTO;
import com.lsoria.qrsec.domain.model.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "username", target = "email")
    UserDTO userToUserDTO(User user);

    @Mapping(source = "email", target = "username")
    User userDTOToUser(UserDTO userDTO);

    @Mapping(source = "email", target = "username")
    User jsonUserLoginDTOToUser(JsonUserLoginDTO jsonUserLoginDTO);

    @Mapping(source = "username", target = "email")
    JsonUserLoginDTO userToJSONUserLoginDTO(User user);

}

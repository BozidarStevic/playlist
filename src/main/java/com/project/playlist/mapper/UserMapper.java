package com.project.playlist.mapper;

import com.project.playlist.dto.UserDTO;
import com.project.playlist.dto.UserRequest;
import com.project.playlist.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    public UserDTO toUserDTO(User user);
    public User fromUserRequest(UserRequest userRequest);

}

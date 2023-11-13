package br.com.projectblog.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.projectblog.dtos.UserDTO;
import br.com.projectblog.dtos.UserDetailsDTO;
import br.com.projectblog.dtos.requests.UserRequestDTO;
import br.com.projectblog.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

	@Mapping(target = "userId", source = "entity.userId")
	@Mapping(target = "username", source = "entity.username")
	@Mapping(target = "email", source = "entity.email")
	@Mapping(target = "password", source = "entity.password")
	@Mapping(target = "enabled", source = "entity.enabled")
	@Mapping(target = "role", source = "entity.role")
	@Mapping(target = "dateUpdate", source = "entity.dateUpdate", dateFormat = "dd-MM-yyyy HH:mm:ss")
	@Mapping(target = "dateCreate", source = "entity.dateCreate", dateFormat = "dd-MM-yyyy HH:mm:ss")
	UserDTO userToUserDTO(User entity);

	@Mapping(target = "userId", source = "dto.userId")
	@Mapping(target = "username", source = "dto.username")
	@Mapping(target = "email", source = "dto.email")
	@Mapping(target = "password", source = "dto.password")
	@Mapping(target = "enabled", source = "dto.enabled")
	@Mapping(target = "role", source = "dto.role")
	@Mapping(target = "dateUpdate", source = "dto.dateUpdate", dateFormat = "dd-MM-yyyy HH:mm:ss")	
	@Mapping(target = "dateCreate", source = "dto.dateCreate", dateFormat = "dd-MM-yyyy HH:mm:ss")
	User userDTOtoUser(UserDTO dto);
	
	@Mapping(target = "userId", source = "dto.userId")
	@Mapping(target = "username", source = "dto.username")
	@Mapping(target = "email", source = "dto.email")
	@Mapping(target = "password", source = "dto.password")
	@Mapping(target = "enabled", source = "dto.enabled")
	@Mapping(target = "role", source = "dto.role")
	@Mapping(target = "dateCreate", ignore = true)
	@Mapping(target = "dateUpdate", ignore = true)
	User userRequestDTOtoUser(UserRequestDTO dto);
	
	@Mapping(target = "userId", source = "entity.userId")
	@Mapping(target = "username", source = "entity.username")
	@Mapping(target = "email", source = "entity.email")
	@Mapping(target = "password", source = "entity.password")
	@Mapping(target = "enabled", source = "entity.enabled")
	@Mapping(target = "role", source = "entity.role")
	@Mapping(target = "dateUpdate", source = "entity.dateUpdate", dateFormat = "dd-MM-yyyy HH:mm:ss")
	@Mapping(target = "dateCreate", source = "entity.dateCreate", dateFormat = "dd-MM-yyyy HH:mm:ss")
	@Mapping(target = "authorities", ignore = true)
	UserDetailsDTO userToUserDetailsDTO(User entity);
	
	@Mapping(target = "userId", source = "dto.userId")
	@Mapping(target = "username", source = "dto.username")
	@Mapping(target = "email", source = "dto.email")
	@Mapping(target = "password", source = "dto.password")
	@Mapping(target = "enabled", source = "dto.enabled")
	@Mapping(target = "role", source = "dto.role")
	@Mapping(target = "dateCreate", ignore = true)
	@Mapping(target = "dateUpdate", ignore = true)
	UserDTO userDTOtoUserRequestDTO(UserRequestDTO dto);
	
	@Mapping(target = "userId", source = "dto.userId")
	@Mapping(target = "username", source = "dto.username")
	@Mapping(target = "email", source = "dto.email")
	@Mapping(target = "password", source = "dto.password")
	@Mapping(target = "enabled", source = "dto.enabled")
	@Mapping(target = "role", source = "dto.role")
	@Mapping(target = "dateUpdate", source = "dto.dateUpdate", dateFormat = "dd-MM-yyyy HH:mm:ss")
	@Mapping(target = "dateCreate", source = "dto.dateCreate", dateFormat = "dd-MM-yyyy HH:mm:ss")
	@Mapping(target = "authorities", ignore = true)
	UserDetailsDTO userDTOToUserDetailsDTO(UserDTO dto);
	
	List<UserDTO> listUserToListUserDTO(List<User> listUser);
}
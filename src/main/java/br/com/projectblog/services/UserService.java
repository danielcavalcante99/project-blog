package br.com.projectblog.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import br.com.projectblog.domains.Role;
import br.com.projectblog.dtos.UserDTO;
import br.com.projectblog.dtos.UserDetailsDTO;
import br.com.projectblog.dtos.requests.UserRequestDTO;
import br.com.projectblog.exceptions.BusinessException;
import br.com.projectblog.exceptions.ResourceNotFoundException;
import br.com.projectblog.mappers.UserMapper;
import br.com.projectblog.models.User;
import br.com.projectblog.repositories.UserRepository;
import br.com.projectblog.utils.UserUtils;
import br.com.projectblog.validations.groups.OnCreate;
import br.com.projectblog.validations.groups.OnUpdate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class UserService {
	
	private final UserRepository repository;

	private final UserMapper mapper;

	
	public Optional<UserDTO> findById(@NotNull UUID id) {
		Optional<User> optUser = this.repository.findById(id);
		UserDTO userDTO = this.mapper.userToUserDTO(optUser.orElse(null));
		
		if(!UserUtils.getUsernameLogado().equals(userDTO.getUsername()) && !userDTO.getRole().equals(Role.ADMIN))
			throw new BusinessException("Não é possível consultar outro usuário que não seja o seu, a menos que seja o administrador.");
		
		return Optional.ofNullable(userDTO);
	}
	
	
	public Optional<UserDTO> findByEmail(@NotNull String email) {
		Optional<User> optUser = this.repository.findByEmail(email);
		
		return Optional.ofNullable(this.mapper.userToUserDTO(optUser.orElse(null)));
	}
	
	
	public Optional<UserDTO> findByUsername(@NotNull String email) {
		Optional<User> optUser = this.repository.findByUsername(email);
		
		return Optional.ofNullable(this.mapper.userToUserDTO(optUser.orElse(null)));
	}	
	
	
	public Optional<UserDetailsDTO> findByUsernameUserDetails(@NotNull String username) {
		User user = this.repository.findByUsername(username).orElse(null);
		
		return Optional.ofNullable(this.mapper.userToUserDetailsDTO(user));
	}
	
	
	public Optional<UserDetailsDTO> findByEmailUserDetails(@NotNull String email) {
		User user = this.repository.findByEmail(email).orElse(null);
		
		return Optional.ofNullable(this.mapper.userToUserDetailsDTO(user));
	}
	
	public void deleteById(@NotNull UUID id) {

		User user = this.repository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(String.format("Usuário pelo id %s não existe.", id)));
		
		if(!UserUtils.getUsernameLogado().equals(user.getUsername()) && !user.getRole().equals(Role.ADMIN))
			throw new BusinessException("Não é possível excluir outro usuário que não seja o seu, a menos que seja o administrador.");
		
		this.repository.deleteById(id);
		log.info("Usuário pelo id {} foi excluído com sucesso.", id);
	}
		
	
	@Validated({ OnCreate.class })
	public UserDTO insert(@NotNull @Valid UserRequestDTO dto) {
		log.info("Requisição para criação do usuário: \n {}", dto);	
		UserDTO userDTO = this.mapper.userDTOtoUserRequestDTO(dto);
		
		Boolean userOrEmailExist = this.repository.findByUsernameOrEmail(userDTO.getUsername(), userDTO.getEmail()).isPresent();
		
		if(userOrEmailExist) {
			throw new BusinessException("'email' ou 'username' já estão sendo usado por outro usuário!");
		}
		
		userDTO.setUsername(userDTO.getUsername().trim().toLowerCase());
		userDTO.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
		userDTO.setDateUpdate(LocalDateTime.now());
		userDTO.setDateCreate(LocalDateTime.now());

		User user = this.repository.save(this.mapper.userDTOtoUser(userDTO));
		userDTO.setUserId(user.getUserId());
		
		log.info("Usuário criado com sucesso.");

		return userDTO;
	}	
	
	
	@Validated({ OnUpdate.class })
	public UserDTO update(@NotNull @Valid UserRequestDTO dto) {
		log.info("Requisição para atualização do usuário: \n {}", dto);
		
		UserDTO userDTO = this.mapper.userDTOtoUserRequestDTO(dto);
		
		User userActual = this.repository.findById(userDTO.getUserId()).orElseThrow(() -> 
				new ResourceNotFoundException(String.format("Usuário pelo identificador %s não encontrado para ser atualizado.", dto.getUserId())
		));
		
		if(!UserUtils.getUsernameLogado().equals(userActual.getUsername()) && !userActual.getRole().equals(Role.ADMIN))
			throw new BusinessException("Não é possível alterar informações de outro usuário que não seja o seu, a menos que seja o administrador.");

		if (!userDTO.getUsername().equals(userActual.getUsername()) || !userDTO.getEmail().equals(userActual.getEmail())) {
			Boolean userOrEmailExist = Optional.ofNullable(this.repository.findByUsernameOrEmail(userDTO.getUsername(), userDTO.getEmail())).isPresent();

			if (userOrEmailExist) {
				throw new BusinessException("'email' ou 'username' já estão sendo usado por outro usuário!");
			}
		}
		
		userDTO.setUsername(userDTO.getUsername().trim().toLowerCase());
		userDTO.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
		userDTO.setDateUpdate(LocalDateTime.now());
		userDTO.setDateCreate(userActual.getDateCreate());
		
		this.repository.save(this.mapper.userDTOtoUser(userDTO));
		
		log.info("Usuário atualizado com sucesso.");

		return userDTO;
	}

}
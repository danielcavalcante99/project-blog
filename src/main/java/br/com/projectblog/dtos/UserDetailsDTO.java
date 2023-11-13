package br.com.projectblog.dtos;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import br.com.projectblog.domains.Role;
import br.com.projectblog.validations.groups.OnCreate;
import br.com.projectblog.validations.groups.OnUpdate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsDTO implements UserDetails {
	
	private static final long serialVersionUID = -2546435987590547395L;

	@Null(groups = OnCreate.class, message = "O campo 'userId' não deve ser informado na inclusão.")
	@NotNull(groups = OnUpdate.class, message = "O campo 'userId' é obrigatório ser informado na atualização.")
	private UUID userId;

	@NotEmpty(message = "O campo 'userName' é obrigatório ser informado.")
	@Size(max = 30, message = "O campo 'username' deve conter no máximo 30 caracteres.")
	private String username;

	@Email(message = "O campo 'email' é obrigatório ser informado com formato válido.")
	private String email;

	@NotEmpty(message = "O campo 'password' é obrigatório ser informado.")
	@Size(min = 5, max = 11, message = "O campo 'password' deve conter no mínimo 5 a no máximo 11 caracteres.")
	private String password;

	@NotNull(message = "O campo 'enabled' é obrigatório ser informado.")
	private Boolean enabled;
	
	@NotNull(message = "O campo 'role' é obrigatório ser informado.")
	private Role role;

	@Null(groups = { OnCreate.class, OnUpdate.class }, message = "O campo 'dateUpdate' não deve ser informado na inclusão ou na atualização.")
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dateUpdate;
	
	@Null(groups = { OnCreate.class, OnUpdate.class }, message = "O campo 'dateCreate' não deve ser informado na inclusão ou na atualização.")
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dateCreate;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.role.getAuthorities();
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

}
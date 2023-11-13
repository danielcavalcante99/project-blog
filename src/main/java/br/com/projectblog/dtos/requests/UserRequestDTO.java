package br.com.projectblog.dtos.requests;

import java.io.Serializable;

import br.com.projectblog.domains.Role;
import br.com.projectblog.validations.groups.OnCreate;
import br.com.projectblog.validations.groups.OnUpdate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UserRequestDTO  implements Serializable {

	private static final long serialVersionUID = -7321908945474584313L;

	@Null(groups = OnCreate.class, message = "O campo 'userId' não deve ser informado na inclusão.")
	@NotNull(groups = OnUpdate.class, message = "O campo 'userId' é obrigatório ser informado na atualização.")
	private String userId;

	@NotEmpty(message = "O campo 'username' é obrigatório ser informado.")
	@Size(max = 30, message = "O campo 'username' deve conter no máximo 30 caracteres.")
	private String username;
	
	@NotEmpty(message = "O campo 'email' é obrigatório ser informado.")
	@Email(message = "O campo 'email' está com formato inválido.")
	private String email;

	@NotEmpty(message = "O campo 'password' é obrigatório ser informado.")
	@Size(min = 5, max = 11, message = "O campo 'password' deve conter no mínimo 5 a no máximo 11 caracteres.")
	private String password;

	@NotNull(message = "O campo 'enabled' é obrigatório ser informado.")
	private Boolean enabled;
	
	@NotNull(message = "O campo 'role' é obrigatório ser informado.")
	private Role role;

}

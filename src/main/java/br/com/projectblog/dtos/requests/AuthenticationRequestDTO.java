package br.com.projectblog.dtos.requests;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequestDTO implements Serializable{

	private static final long serialVersionUID = 124619630016458852L;
	
	@NotBlank(message = "O campo 'username' é obrigatório ser informado.")
	private String username;
	@NotBlank(message = "O campo 'password' é obrigatório ser informado.")
	private String password;

}
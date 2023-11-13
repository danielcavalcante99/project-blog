package br.com.projectblog.dtos.requests;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequestDTO implements Serializable{

	private static final long serialVersionUID = 124619630016458852L;
	
	private String username;
	private String password;

}
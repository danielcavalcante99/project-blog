package br.com.projectblog.dtos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.projectblog.domains.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UserDTO implements Serializable {

	private static final long serialVersionUID = -1310022305935554392L;

	private UUID userId;

	private String username;
	
	private String email;

	private String password;

	private Boolean enabled;
	
	private Role role;

	private LocalDateTime dateUpdate;

	private LocalDateTime dateCreate;

}
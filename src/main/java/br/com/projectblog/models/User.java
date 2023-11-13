package br.com.projectblog.models;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.projectblog.domains.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID userId;
	
	@Column(unique = true)
	private String username;
	
	@Column(unique = true)
	private String email;
	
	private Boolean enabled;
	
	private String password;
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	private LocalDateTime dateUpdate;
	
	private LocalDateTime dateCreate;

}

package br.com.projectblog.dtos;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.projectblog.domains.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsDTO implements UserDetails {
	
	private static final long serialVersionUID = -2546435987590547395L;

	private UUID userId;

	private String username;

	private String email;

	private String password;

	private Boolean enabled;
	
	private Role role;

	private LocalDateTime dateUpdate;
	
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
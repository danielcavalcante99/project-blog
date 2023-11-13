package br.com.projectblog.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.projectblog.dtos.UserDetailsDTO;

public class UserUtils {

	public static String getUsernameLogado() {
		final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String userLogado;

		if (principal instanceof UserDetails) {
			userLogado = ((UserDetailsDTO) principal).getUsername();
		} else {
			userLogado = principal.toString();
		}

		return userLogado;
	}

}

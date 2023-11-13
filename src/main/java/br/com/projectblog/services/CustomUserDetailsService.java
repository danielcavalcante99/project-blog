package br.com.projectblog.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.projectblog.dtos.UserDTO;
import br.com.projectblog.mappers.UserMapper;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;
    
    private final UserMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO dto = this.userService.findByUsername(username).orElse(null);
        UserDetails userDetails = this.mapper.userDTOToUserDetailsDTO(dto);;
        return userDetails;
    }
}
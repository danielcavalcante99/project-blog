package br.com.projectblog.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.projectblog.dtos.AuthenticationTokenDTO;
import br.com.projectblog.dtos.requests.AuthenticationRequestDTO;
import br.com.projectblog.services.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "Autenticação")
@RestController
@AllArgsConstructor
@RequestMapping("/v1/auth")
public class AuthenticationController {

	private final JwtService jwtService;

	private final AuthenticationManager authManager;

	@PostMapping("/login")
    @Operation(summary = "Autenticação do usuário")
	@ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
		content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "403", description = "Não autorizado", 
		content = @Content(schema = @Schema(defaultValue = "")))
	public ResponseEntity<AuthenticationTokenDTO> login(@RequestBody AuthenticationRequestDTO dto) {
		UsernamePasswordAuthenticationToken userPasswordToken = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
		authManager.authenticate(userPasswordToken);

		return ResponseEntity.ok(jwtService.generateToken(dto));

	}

};

package br.com.projectblog.controllers;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.projectblog.dtos.UserDTO;
import br.com.projectblog.dtos.requests.UserRequestDTO;
import br.com.projectblog.exceptions.handlers.ApiRequestException;
import br.com.projectblog.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "Usuários")
@RestController
@RequestMapping("/v1/users")
@AllArgsConstructor
public class UserController {

	private final UserService service;

	@GetMapping("/{id}")
	@Operation(summary = "Consultar usuário pelo id", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponse(responseCode = "200", description = "Busca realizada com sucesso", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "403", description = "Não autorizado", content = @Content(schema = @Schema(defaultValue = "")))
	public ResponseEntity<UserDTO> findById(@PathVariable UUID id) {
		UserDTO userDTO = this.service.findById(id).orElse(null);

		Optional<UserDTO> optUserDto = Optional.ofNullable(userDTO);

		return optUserDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
	}

	@PostMapping("/register")
	@Operation(summary = "Cadastro de usuário")
	@ApiResponse(responseCode = "201", description = "Cadastro realizado com sucesso", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(schema = @Schema(implementation = ApiRequestException.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
	public ResponseEntity<UserDTO> insert(@RequestBody UserRequestDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.insert(dto));
	}

	@PutMapping("/update")
	@Operation(summary = "Atualização de usuário", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponse(responseCode = "201", description = "Atualização realizada com sucesso", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "404", description = "Recurso não encotrado", content = @Content(schema = @Schema(implementation = ApiRequestException.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(schema = @Schema(implementation = ApiRequestException.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "403", description = "Não autorizado", content = @Content(schema = @Schema(defaultValue = "")))
	public ResponseEntity<UserDTO> update(@RequestBody UserRequestDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.update(dto));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Excluir usuário pelo id", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponse(responseCode = "204", description = "Exclusão realizado com sucesso")
	@ApiResponse(responseCode = "404", description = "Recurso não encotrado", content = @Content(schema = @Schema(implementation = ApiRequestException.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "403", description = "Não autorizado", content = @Content(schema = @Schema(defaultValue = "")))
	public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
		this.service.deleteById(id);

		return ResponseEntity.noContent().build();
	}

}

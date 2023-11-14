package br.com.projectblog.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.projectblog.dtos.CommentDTO;
import br.com.projectblog.dtos.requests.CommentRequestDTO;
import br.com.projectblog.exceptions.handlers.ApiRequestException;
import br.com.projectblog.services.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "Comentários")
@RestController
@RequestMapping("/v1/comments")
@AllArgsConstructor
public class CommentController {
	
	private final CommentService service;
	
	@PostMapping("/register")
    @Operation(summary = "Incluir comentário no post", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponse(responseCode = "201", description = "Cadastro realizado com sucesso",
		content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "404", description = "Recurso não encontrado", 
		content = @Content(schema = @Schema(implementation = ApiRequestException.class), 
		mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "403", description = "Não autorizado", 
	content = @Content(schema = @Schema(defaultValue = "")))
	public ResponseEntity<CommentDTO> insert(@RequestBody CommentRequestDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.insertCommentInPost(dto));
	}
	
	
	@PutMapping("/update")
    @Operation(summary = "Atualização do comentário no post", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponse(responseCode = "201", description = "Atualização realizado com sucesso",
		content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "404", description = "Recurso não encontrado", 
		content = @Content(schema = @Schema(implementation = ApiRequestException.class), 
		mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "400", description = "Requisição inválida", 
		content = @Content(schema = @Schema(implementation = ApiRequestException.class), 
		mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "403", description = "Não autorizado", 
		content = @Content(schema = @Schema(defaultValue = "")))
	public ResponseEntity<CommentDTO> update(@RequestBody CommentRequestDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.updateCommentInPost(dto));
	}
	
	@DeleteMapping("/{id}")
    @Operation(summary = "Excluir comentário pelo id", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponse(responseCode = "204", description = "Exclusão realizado com sucesso")
	@ApiResponse(responseCode = "404", description = "Recurso não encotrado", 
		content = @Content(schema = @Schema(implementation = ApiRequestException.class), 
		mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "403", description = "Não autorizado", 
		content = @Content(schema = @Schema(defaultValue = "")))
	public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
		this.service.deleteById(id);
		
		return ResponseEntity.noContent().build();
	}

}

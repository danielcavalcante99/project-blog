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

import br.com.projectblog.dtos.PhotoDTO;
import br.com.projectblog.dtos.requests.PhotoRequestDTO;
import br.com.projectblog.exceptions.handlers.ApiRequestException;
import br.com.projectblog.services.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "Fotos")
@RestController
@RequestMapping("/v1/photos")
@AllArgsConstructor
public class PhotoController {
	
	private final PhotoService service;
	
	@PostMapping("/register")
    @Operation(summary = "Incluir fotos no album", 
    		   description = "Permite apenas inserir fotos em album com seu usuário.",
    		   security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponse(responseCode = "201", description = "Cadastro realizado com sucesso",
		content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "404", description = "Recurso não encontrado", 
		content = @Content(schema = @Schema(implementation = ApiRequestException.class), 
		mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "403", description = "Não autorizado", 
		content = @Content(schema = @Schema(defaultValue = "")))
	public ResponseEntity<PhotoDTO> insert(@RequestBody PhotoRequestDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.insertPhotoInAlbum(dto));
	}
	
	
	@PutMapping("/update")
    @Operation(summary = "Atualização de fotos no album", 
 		       description = "Permite apenas o usuário da foto realizar a alteração.",
    		   security = @SecurityRequirement(name = "bearerAuth"))
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
	public ResponseEntity<PhotoDTO> update(@RequestBody PhotoRequestDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.updatePhotoInAlbum(dto));
	}
	
	
	@DeleteMapping("/{id}")
    @Operation(summary = "Excluir fotos pelo id", 
  		       description = "Permite somente o usuário realizar exclusão da sua foto do album pelo id, somente usuários "
  	   		   		+ "com nível de ADM poderão excluir fotos de um album de outros usuários.",
    		   security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponse(responseCode = "204", description = "Exclusão realizado com sucesso")
	@ApiResponse(responseCode = "404", description = "Recurso não encotrado", 
		content = @Content(schema = @Schema(implementation = ApiRequestException.class), 
		mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "403", description = "Não autorizado", 
		content = @Content(schema = @Schema(defaultValue = "")))
	public ResponseEntity<Void> deleteById(@PathVariable String id) {
		this.service.deleteById(UUID.fromString(id));
		return ResponseEntity.noContent().build();
	}
	

}

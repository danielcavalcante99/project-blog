package br.com.projectblog.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.IanaLinkRelations;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.projectblog.dtos.AlbumDTO;
import br.com.projectblog.dtos.filters.FilterAlbumDTO;
import br.com.projectblog.dtos.requests.AlbumRequestDTO;
import br.com.projectblog.exceptions.handlers.ApiRequestException;
import br.com.projectblog.services.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "Albums")
@RestController
@RequestMapping("/v1/albums")
@AllArgsConstructor
public class AlbumController {
	
	private final AlbumService service;
	
	@GetMapping
    @Operation(summary = "Listar albums", 
    		   description = "Consultar albums com ou sem filtros",
    		   security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
		content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "403", description = "Não autorizado", 
		content = @Content(schema = @Schema(defaultValue = "")))
	public ResponseEntity<Page<AlbumDTO>> findAllByFilter(
			@RequestParam(name = "Album Identifier", required = false) String userId, 
			@RequestParam(name = "User Identifier", required = false) String albumId,
			@RequestParam(name = "Name", required = false) String name, 
			@RequestParam(name = "Date Update Start", required = false) @Parameter(example = "2023-08-29T12:02:41") LocalDateTime dateUpdateStart,
			@RequestParam(name = "Date Update End", required = false)   @Parameter(example = "2023-08-30T12:02:41") LocalDateTime dateUpdateEnd,
			@RequestParam(name = "Date Create Start", required = false) @Parameter(example = "2023-08-29T12:02:41") LocalDateTime dateCreateStart,
			@RequestParam(name = "Date Create End", required = false)   @Parameter(example = "2023-08-30T12:02:41") LocalDateTime dateCreateEnd,
			@RequestParam(name = "Size", defaultValue = "10") Integer size,
			@RequestParam(name = "Page", defaultValue = "1") Integer page) {
		
		Pageable pageable = PageRequest.of(page, size);

		FilterAlbumDTO filter = FilterAlbumDTO.builder()
				.userId(userId)
				.albumId(albumId)
				.name(name)
				.dateUpdateStart(dateUpdateStart)
				.dateUpdateEnd(dateUpdateEnd)
				.dateCreateStart(dateCreateStart)
				.dateCreateEnd(dateCreateEnd)
				.build();
		
		List<AlbumDTO> listAlbumDTO = new ArrayList<>();
		this.service.findAllByFilter(filter, pageable).forEach(albumDTO -> {
			albumDTO.add(linkTo(methodOn(AlbumController.class).findById(albumDTO.getAlbumId().toString())).withSelfRel());
			listAlbumDTO.add(albumDTO);
		});
		
		return ResponseEntity.ok(new PageImpl<>(listAlbumDTO, pageable, listAlbumDTO.stream().count()));
	}
	
	
	@GetMapping("/{id}")
    @Operation(summary = "Consultar album pelo id", 
    		   description = "Consultar album pelo id.",
    		   security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "403", description = "Não autorizado", 
		content = @Content(schema = @Schema(defaultValue = "")))
	public ResponseEntity<AlbumDTO> findById(@PathVariable String id) {
		AlbumDTO albumDTO = this.service.findById(UUID.fromString(id)).orElse(null);

		albumDTO.add(linkTo(methodOn(AlbumController.class)
				.findAllByFilter(null, null, null, null, null, null, null, null, null))
				.withRel(IanaLinkRelations.COLLECTION));

		Optional<AlbumDTO> optAlbumDto = Optional.ofNullable(albumDTO);
		
		return optAlbumDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
	}
	
	
	@PostMapping("/register")
    @Operation(summary = "Cadastro de album", 
 		   description = "Permite apenas inserir albums com seu usuário.",
    		   security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponse(responseCode = "201", description = "Cadastro realizado com sucesso",
		content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "404", description = "Recurso não encontrado", 
		content = @Content(schema = @Schema(implementation = ApiRequestException.class), 
		mediaType = MediaType.APPLICATION_JSON_VALUE))
	public ResponseEntity<AlbumDTO> insert(@RequestBody AlbumRequestDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.insert(dto));
	}
	
	
	@PutMapping("/update")
    @Operation(summary = "Atualização de album", 
    		   description = "Permite apenas atualizar os albums com seu usuário.",
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
	public ResponseEntity<AlbumDTO> update(@RequestBody AlbumRequestDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.update(dto));
	}
	
	
	@DeleteMapping("/{id}")
    @Operation(summary = "Excluir album pelo id", 
 		   description = "Permite somente o usuário realizar exclusão do seu album pelo id, somente usuários "
   		   		+ "com nível de ADM poderão excluir album de outros usuários.",
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

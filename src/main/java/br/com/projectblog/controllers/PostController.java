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

import br.com.projectblog.dtos.PostDTO;
import br.com.projectblog.dtos.filters.FilterPostDTO;
import br.com.projectblog.dtos.requests.PostRequestDTO;
import br.com.projectblog.exceptions.handlers.ApiRequestException;
import br.com.projectblog.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;


@Tag(name = "Posts")
@RestController
@RequestMapping("/v1/posts")
@AllArgsConstructor
public class PostController {
	
	private final PostService service;
	
	@GetMapping
    @Operation(summary = "Listar posts", 
			   description = "Consultar posts com ou sem filtros",
    		   security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
		content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "403", description = "Não autorizado", 
		content = @Content(schema = @Schema(defaultValue = "")))
	public ResponseEntity<Page<PostDTO>> findAllByFilter(
			@RequestParam(name = "Post ID", required = false) String postId, 
			@RequestParam(name = "User ID", required = false) String userId,
			@RequestParam(name = "Title", required = false) String title, 
			@RequestParam(name = "Description", required = false) String description,
			@RequestParam(name = "Date Update Start", required = false) @Parameter(example = "2023-08-29T12:02:41") LocalDateTime dateUpdateStart,
			@RequestParam(name = "Date Update End", required = false)   @Parameter(example = "2023-08-30T12:02:41") LocalDateTime dateUpdateEnd,
			@RequestParam(value = "Date Create Start", required = false) @Parameter(example = "2023-08-29T12:02:41") LocalDateTime dateCreateStart,
			@RequestParam(name = "Date Create End", required = false)   @Parameter(example = "2023-08-30T12:02:41") LocalDateTime dateCreateEnd,
			@RequestParam(name = "Size", defaultValue = "10") Integer size,
			@RequestParam(name = "Page", defaultValue = "1") Integer page) {
		
		Pageable pageable = PageRequest.of(page, size);

		FilterPostDTO filter = FilterPostDTO.builder()
				.userId(userId)
				.postId(postId)
				.title(title)
				.description(description)
				.dateUpdateStart(dateUpdateStart)
				.dateUpdateEnd(dateUpdateEnd)
				.dateCreateStart(dateCreateStart)
				.dateCreateEnd(dateCreateEnd)
				.build();
		
		List<PostDTO> listPostDTO = new ArrayList<>();
		this.service.findAllByFilter(filter, pageable).forEach(postDTO -> {
			postDTO.add(linkTo(methodOn(PostController.class).findById(postDTO.getPostId().toString())).withSelfRel());
			listPostDTO.add(postDTO);
		});
		
		return ResponseEntity.ok(new PageImpl<>(listPostDTO, pageable, listPostDTO.stream().count()));
	}
	
	
	@GetMapping("/{id}")
    @Operation(summary = "Consultar post pelo id", 
    		   description = "Consultar post pelo id",
               security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "403", description = "Não autorizado", 
		content = @Content(schema = @Schema(defaultValue = "")))
	public ResponseEntity<PostDTO> findById(@PathVariable String id) {
		PostDTO postDTO = this.service.findById(UUID.fromString(id)).orElse(null);

		postDTO.add(linkTo(methodOn(PostController.class)
				.findAllByFilter(null, null, null, null, null, null, null, null, null, null))
				.withRel(IanaLinkRelations.COLLECTION));

		Optional<PostDTO> optPostDto = Optional.ofNullable(postDTO);
		
		return optPostDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
	}
	
	@PostMapping("/register")
    @Operation(summary = "Cadastro de post", 
    		   description = "Permite o usuário criar seu post apenas",
               security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponse(responseCode = "201", description = "Cadastro realizado com sucesso",
		content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "404", description = "Recurso não encontrado", 
		content = @Content(schema = @Schema(implementation = ApiRequestException.class), 
		mediaType = MediaType.APPLICATION_JSON_VALUE))
	@ApiResponse(responseCode = "403", description = "Não autorizado", 
		content = @Content(schema = @Schema(defaultValue = "")))
	public ResponseEntity<PostDTO> insert(@RequestBody PostRequestDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.insert(dto));
	}
	
	@PutMapping("/update")
    @Operation(summary = "Atualização de post", 
    		  description = "Permite o usuário atualizar seu post apenas",
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
	public ResponseEntity<PostDTO> update(@RequestBody PostRequestDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.update(dto));
	}
	
	@DeleteMapping("/{id}")
    @Operation(summary = "Excluir post pelo id", 
    		   description = "Permite somente o usuário realizar exclusão do seu post pelo id, somente usuários "
    		   		+ "com nível de ADM poderão excluir post de outros usuários.",
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

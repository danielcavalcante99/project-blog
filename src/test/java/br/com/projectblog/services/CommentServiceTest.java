package br.com.projectblog.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.projectblog.dtos.PostDTO;
import br.com.projectblog.dtos.requests.CommentRequestDTO;
import br.com.projectblog.exceptions.ResourceNotFoundException;
import br.com.projectblog.mappers.CommentMapper;
import br.com.projectblog.mappers.CommentMapperImpl;
import br.com.projectblog.mappers.PostMapper;
import br.com.projectblog.mappers.PostMapperImpl;
import br.com.projectblog.repositories.CommentRepository;
import br.com.projectblog.validations.groups.OnCreate;
import br.com.projectblog.validations.groups.OnUpdate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class CommentServiceTest {

    private static final String FIELD_COMMENT_ID = "commentId";
	private static final String FIELD_USER_ID = "userId";
	private static final String FIELD_POST_ID = "postId";
	
	@Mock private CommentRepository repository;
	
	@Mock private PostService postService;
	
	@Mock private UserService userService;
	
	private CommentService service;
	
	private PostMapper postMapper;
	
	private CommentMapper mapper;
	
	private Validator validator;


	@BeforeEach
    void initMockss() {
       MockitoAnnotations.openMocks(this);
       this.postMapper =  new PostMapperImpl();
       this.mapper =  new CommentMapperImpl();
       this.service = new CommentService(this.repository, this.postService, this.userService, this.postMapper, this.mapper);
       this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

	
    private Set<ConstraintViolation<CommentRequestDTO>> validarDadosDeEntradaCriacao(CommentRequestDTO dto) {

    	Set<ConstraintViolation<CommentRequestDTO>> violations = validator.validate(dto, OnCreate.class);
    
        violations.forEach(action -> { 
        	if(action.getPropertyPath().toString().equals(FIELD_POST_ID)) { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'postId' é obrigatório.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_POST_ID); 
	            
        	} else if(action.getPropertyPath().toString().equals(FIELD_USER_ID)) { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'userId' é obrigatório.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_USER_ID); 
	            
        	} else {
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'commentId' não deve ser informado na inclusão.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_COMMENT_ID); 
        	}
        });  
        
        return violations;
    }
       
    
    private Set<ConstraintViolation<CommentRequestDTO>> validarDadosDeEntradaAtualizacao(CommentRequestDTO dto) {
    	
    	Set<ConstraintViolation<CommentRequestDTO>> violations = validator.validate(dto, OnUpdate.class);

        violations.forEach(action -> { 
        	if(action.getPropertyPath().toString().equals(FIELD_POST_ID)) { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'postId' é obrigatório.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_POST_ID); 
	            
        	} else if(action.getPropertyPath().toString().equals(FIELD_USER_ID)) { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'userId' é obrigatório.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_USER_ID); 
	            
        	} else {
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'commentId' é obrigatório ser informado na atualização.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_COMMENT_ID); 
        	}
        });  
        
        return violations;
    }
    
    @Test
    @DisplayName("Teste - validar dados de entrada")
    void validarDadosDeEntrada() {

    	CommentRequestDTO dto = CommentRequestDTO.builder().build();
    	dto.setCommentId(UUID.randomUUID().toString());
    	dto.setPostId(null);
    	dto.setUserId(null);
    	
        Set<ConstraintViolation<CommentRequestDTO>> validarDadosDeEntradaCriacao = this.validarDadosDeEntradaCriacao(dto);
        assertThat(validarDadosDeEntradaCriacao.size()).isEqualTo(3);
        
    	dto.setCommentId(null);
    	dto.setPostId(null);
    	dto.setUserId(null);
    	
        Set<ConstraintViolation<CommentRequestDTO>> validarDadosDeEntradaAtualizacao = this.validarDadosDeEntradaAtualizacao(dto);
        assertThat(validarDadosDeEntradaAtualizacao.size()).isEqualTo(3);
    }
    
    
    @Test
    @DisplayName("Teste - tentar comentar post que não existe.")
    void testeComentandoPostInesistente() {
    	
    	CommentRequestDTO dto = CommentRequestDTO.builder().build();
    	dto.setCommentId(null);
    	dto.setPostId(UUID.randomUUID().toString());
    	dto.setUserId(UUID.randomUUID().toString());
    	
        Set<ConstraintViolation<CommentRequestDTO>> validarDadosDeEntradaCriacao = this.validarDadosDeEntradaCriacao(dto);
    	
    	when(this.postService.findById(UUID.fromString(dto.getPostId()))).thenReturn(Optional.empty());
		    	
		try {
			this.service.insertCommentInPost(dto);
			
		} catch (ResourceNotFoundException e) {
			 assertThat(validarDadosDeEntradaCriacao.size()).isEqualTo(0);
			assertEquals(String.format("Post pelo identificador %s não encontrado para ser atualizado.", dto.getPostId()), e.getLocalizedMessage());
		}
    }
    
    
    @Test
    @DisplayName("Teste - tentar comentar post que não existe.")
    void testeComentandoPostComUsuarioInesistente() {
    	
    	CommentRequestDTO dto = CommentRequestDTO.builder().build();
    	dto.setCommentId(null);
    	dto.setPostId(UUID.randomUUID().toString());
    	dto.setUserId(UUID.randomUUID().toString());
    	
        Set<ConstraintViolation<CommentRequestDTO>> validarDadosDeEntradaCriacao = this.validarDadosDeEntradaCriacao(dto);
    	
    	when(this.postService.findById(UUID.fromString(dto.getPostId())))
    	.thenReturn(Optional.of(PostDTO.builder().postId((UUID.fromString(dto.getPostId()))).build()));
    	when(this.userService.findById(UUID.fromString(dto.getUserId()))).thenReturn(Optional.empty());
		    	
		try {
			this.service.insertCommentInPost(dto);
			
		} catch (ResourceNotFoundException e) {
			 assertThat(validarDadosDeEntradaCriacao.size()).isEqualTo(0);
			assertEquals(String.format("Usuário pelo identificador %s não encontrado.", dto.getUserId()), e.getLocalizedMessage());
		}
    }
    
    
    @Test
    @DisplayName("Teste - tentar atualizar comentário que não existe.")
    void testeAtualizandoComentantarioInesistente() {
    	
    	CommentRequestDTO dto = CommentRequestDTO.builder().build();
    	dto.setCommentId(UUID.randomUUID().toString());
    	dto.setPostId(UUID.randomUUID().toString());
    	dto.setUserId(UUID.randomUUID().toString());
    	
        Set<ConstraintViolation<CommentRequestDTO>> validarDadosDeEntradaAtualizacao = this.validarDadosDeEntradaAtualizacao(dto);
    	
    	when(this.repository.findById(UUID.fromString(dto.getCommentId()))).thenReturn(Optional.empty());
		    	
		try {
			this.service.updateCommentInPost(dto);
			
		} catch (ResourceNotFoundException e) {
			 assertThat(validarDadosDeEntradaAtualizacao.size()).isEqualTo(0);
			assertEquals(String.format("Comment pelo identificador %s não encontrado para ser atualizado.", dto.getCommentId()), e.getLocalizedMessage());
		}
    }
    
    
    @Test
    @DisplayName("Teste - tentar atualizar comentário de um post que não existe.")
    void testeAtualizandoComentantarioPostInesistente() {
    	
    	CommentRequestDTO dto = CommentRequestDTO.builder().build();
    	dto.setCommentId(UUID.randomUUID().toString());
    	dto.setPostId(UUID.randomUUID().toString());
    	dto.setUserId(UUID.randomUUID().toString());
    	
        Set<ConstraintViolation<CommentRequestDTO>> validarDadosDeEntradaAtualizacao = this.validarDadosDeEntradaAtualizacao(dto);
    	
    	when(this.postService.findById(UUID.fromString(dto.getPostId()))).thenReturn(Optional.empty());
    	
		try {
			this.service.insertCommentInPost(dto);
			
		} catch (ResourceNotFoundException e) {
			 assertThat(validarDadosDeEntradaAtualizacao.size()).isEqualTo(0);
			assertEquals(String.format("Post pelo identificador %s não encontrado para ser atualizado.", dto.getPostId()), e.getLocalizedMessage());
		}
    }
    
    
    @Test
    @DisplayName("Teste - tentar atualizar comentário com usuário que não existe.")
    void testeAtualizandoComentantarioComUsuarioInesistente() {
    	
    	CommentRequestDTO dto = CommentRequestDTO.builder().build();
    	dto.setCommentId(UUID.randomUUID().toString());
    	dto.setPostId(UUID.randomUUID().toString());
    	dto.setUserId(UUID.randomUUID().toString());
    	
        Set<ConstraintViolation<CommentRequestDTO>> validarDadosDeEntradaAtualizacao = this.validarDadosDeEntradaAtualizacao(dto);
    	
        when(this.repository.findById(UUID.fromString(dto.getCommentId()))).thenReturn(Optional.of(this.mapper.commentRequestDTOToComment(dto)));
    	when(this.postService.findById(UUID.fromString(dto.getPostId())))
    	.thenReturn(Optional.of(PostDTO.builder().postId((UUID.fromString(dto.getPostId()))).build()));
    	
    	when(this.userService.findById(UUID.fromString(dto.getUserId()))).thenReturn(Optional.empty());
    	
		try {
			this.service.insertCommentInPost(dto);
			
		} catch (ResourceNotFoundException e) {
			 assertThat(validarDadosDeEntradaAtualizacao.size()).isEqualTo(0);
			assertEquals(String.format("Usuário pelo identificador %s não encontrado.", dto.getUserId()), e.getLocalizedMessage());
		}
    }
    
    
    @Test
    @DisplayName("Teste - tentar excluir comentário que não existe.")
    void testeExcluindoComentantarioInesistente() {
    	
    	UUID id = UUID.randomUUID();
    	    	
        when(this.repository.findById(UUID.fromString(id.toString()))).thenReturn(Optional.empty());

		try {
			this.service.deleteById(id);
			
		} catch (ResourceNotFoundException e) {
			assertEquals(String.format("Comment pelo id %s não existe.", id), e.getLocalizedMessage());
		}
    }
    
}

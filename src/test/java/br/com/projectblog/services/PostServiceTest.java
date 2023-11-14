package br.com.projectblog.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Base64;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.projectblog.dtos.requests.PostRequestDTO;
import br.com.projectblog.exceptions.ResourceNotFoundException;
import br.com.projectblog.mappers.CommentMapper;
import br.com.projectblog.mappers.CommentMapperImpl;
import br.com.projectblog.mappers.PostMapper;
import br.com.projectblog.mappers.PostMapperImpl;
import br.com.projectblog.mappers.UserMapper;
import br.com.projectblog.mappers.UserMapperImpl;
import br.com.projectblog.repositories.PostRepository;
import br.com.projectblog.validations.groups.OnCreate;
import br.com.projectblog.validations.groups.OnUpdate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class PostServiceTest {
	
	private static final String FIELD_IMAGE_ENCODE_BASE64 = "imageEncodeBase64";
	private static final String FIELD_USER_ID = "userId";
	private static final String FIELD_POST_ID = "postId";
	private static final String FIELD_TITLE = "title";
	private static final String FIELD_DESCRIPTION = "description";

	private PostService service;
	
	@Mock private UserService UserService;
	
	@Mock PostRepository repository;
	
	private PostMapper postMapper;
	
	private CommentMapper commentMapper;
	
	private UserMapper userMapper;
	
	private Validator validator;	
	
    @BeforeEach
    void initMockss() {
       MockitoAnnotations.openMocks(this);
       this.postMapper =  new PostMapperImpl();
       this.commentMapper =  new CommentMapperImpl();
       this.userMapper =  new UserMapperImpl();
       this.service = new PostService(this.repository, this.UserService, this.userMapper, this.commentMapper, this.postMapper);
       this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
    
    
    private Set<ConstraintViolation<PostRequestDTO>> validarDadosDeEntradaPadrao(PostRequestDTO dto) {
    	Set<ConstraintViolation<PostRequestDTO>> violations = this.validator.validate(dto);
    		
        violations.forEach(action -> {
        	if(action.getPropertyPath().toString().equals(FIELD_TITLE)) { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'title' é obrigatório ser informado.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_TITLE);
	            
        	} else { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'description' é obrigatório ser informado.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_DESCRIPTION);	            
        	} 
        });
        
        return violations;
    }
    
    
    private Set<ConstraintViolation<PostRequestDTO>> validarDadosDeEntradaCriacao(PostRequestDTO dto) {

    	Set<ConstraintViolation<PostRequestDTO>> violations = validator.validate(dto, OnCreate.class);
    
        violations.forEach(action -> { 
        	if(action.getPropertyPath().toString().equals(FIELD_POST_ID)) { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'postId' não deve ser informado na inclusão.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_POST_ID); 
	            
        	} else if(action.getPropertyPath().toString().equals(FIELD_IMAGE_ENCODE_BASE64)) { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'imageEncodeBase64' é obrigatório ser informado na inclusão.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_IMAGE_ENCODE_BASE64); 
	            
        	} else {
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'userId' é obrigatório ser informado na atualização e inclusão.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_USER_ID); 
        	}
        });  
        
        return violations;
    }
       
    
    private Set<ConstraintViolation<PostRequestDTO>> validarDadosDeEntradaAtualizacao(PostRequestDTO dto) {
    	
    	Set<ConstraintViolation<PostRequestDTO>> violations = validator.validate(dto, OnUpdate.class);

        violations.forEach(action -> {
        	if(action.getPropertyPath().toString().equals(FIELD_POST_ID)) { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'postId' é obrigatório ser informado na atualização.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_POST_ID); 
	            
        	} else if(action.getPropertyPath().toString().equals(FIELD_IMAGE_ENCODE_BASE64)) { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'imageEncodeBase64' não deve ser informado na atualização.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_IMAGE_ENCODE_BASE64); 
	            
        	} else {
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'userId' é obrigatório ser informado na atualização e inclusão.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_USER_ID); 
        	}
        }); 
        
        return violations;
    }
    
    
    @Test
    @DisplayName("Teste - validar dados de entrada")
    void validarDadosDeEntrada() {

    	PostRequestDTO dto = PostRequestDTO.builder().build();
        
        Set<ConstraintViolation<PostRequestDTO>> validarDadosDeEntradaPadrao = this.validarDadosDeEntradaPadrao(dto);
        assertThat(validarDadosDeEntradaPadrao.size()).isEqualTo(2);
        
    	dto.setPostId(UUID.randomUUID().toString());
    	dto.setUserId(null);
    	dto.setImageEncodeBase64(null);
    	dto.setTitle("Maior País da Europa");
    	dto.setDescription("Em termos de superfície, a França é o maior país da UE e Malta o mais pequeno.");
        
        Set<ConstraintViolation<PostRequestDTO>> validarDadosDeEntradaCriacao = this.validarDadosDeEntradaCriacao(dto);
        assertThat(validarDadosDeEntradaCriacao.size()).isEqualTo(3);
        
    	dto.setPostId(null);
    	dto.setUserId(null);
    	dto.setTitle(UUID.randomUUID().toString());
    	dto.setDescription("Em termos de superfície, a França é o maior país da UE e Malta o mais pequeno.");
        this.validarDadosDeEntradaAtualizacao(dto);
        
        Set<ConstraintViolation<PostRequestDTO>> validarDadosDeEntradaAtualizacao = this.validarDadosDeEntradaAtualizacao(dto);
        assertThat(validarDadosDeEntradaAtualizacao.size()).isEqualTo(2);
    }

    
    @Test
    @DisplayName("Teste - tentar atualizar post que não existe.")
    void testeAtualizandoPostInesistente() {
    	
    	PostRequestDTO dto = PostRequestDTO.builder()
    	    	.postId(UUID.randomUUID().toString())
    	    	.userId(UUID.randomUUID().toString())
    	    	.title("Maior País da Europa")
    	    	.description("Em termos de superfície, a França é o maior país da UE e Malta o mais pequeno.")
        	 	.build();
    	
    	Set<ConstraintViolation<PostRequestDTO>> validarDadosDeEntradaAtualizacao = this.validarDadosDeEntradaAtualizacao(dto);
    	
    	when(this.repository.findById(any())).thenReturn(Optional.empty());
		    	
		try {
			this.service.update(dto);
			
		} catch (ResourceNotFoundException e) {
			assertThat(validarDadosDeEntradaAtualizacao.size()).isEqualTo(0);
			assertEquals(String.format("Post pelo identificador %s não encontrado para ser atualizado.", dto.getPostId()), e.getLocalizedMessage());
		}
    }
    
    
    @Test
    @DisplayName("Teste - tentar atualizar post em que não existe usuário.")
    void testeAtualizandoPostComUsuarioInesistente() {
    	
    	PostRequestDTO dto = PostRequestDTO.builder()
    	    	.postId(UUID.randomUUID().toString())
    	    	.userId(UUID.randomUUID().toString())
    	    	.imageEncodeBase64(null)
    	    	.title("Maior País da Europa")
    	    	.description("Em termos de superfície, a França é o maior país da UE e Malta o mais pequeno.")
        	 	.build();
    	
    	Set<ConstraintViolation<PostRequestDTO>> validarDadosDeEntradaAtualizacao = this.validarDadosDeEntradaAtualizacao(dto);
    	when(this.repository.findById(any())).thenReturn(Optional.ofNullable(this.postMapper.postRequestDTOToPost(dto)));
    	when(this.UserService.findById(any())).thenReturn(Optional.empty());
		    	
		try {
			this.service.update(dto);
			
		} catch (ResourceNotFoundException e) {
			assertThat(validarDadosDeEntradaAtualizacao.size()).isEqualTo(0);
			assertEquals(String.format("Usuário pelo identificador %s não encontrado.", dto.getUserId()), e.getLocalizedMessage());
		}
    }
    
    
    @Test
    @DisplayName("Teste - tentar criar um post com usuário que não existe.")
    void testeCriandoPostComUsuarioInesistente() {
    	
    	PostRequestDTO dto = PostRequestDTO.builder()
    	    	.postId(UUID.randomUUID().toString())
    	    	.userId(UUID.randomUUID().toString())
    	    	.imageEncodeBase64(Base64.getEncoder().encodeToString(new byte[7]))
    	    	.title("Maior País da Europa")
    	    	.description("Em termos de superfície, a França é o maior país da UE e Malta o mais pequeno.")
        	 	.build();

    	Set<ConstraintViolation<PostRequestDTO>> validarDadosDeEntradaCriacao = this.validarDadosDeEntradaCriacao(dto);
    	when(this.UserService.findById(UUID.fromString(dto.getUserId()))).thenReturn(Optional.empty());

		try {
			this.service.insert(dto);

		} catch (ResourceNotFoundException e) {
			assertThat(validarDadosDeEntradaCriacao.size()).isEqualTo(1);
			assertEquals(String.format("Usuário pelo identificador %s não encontrado.", dto.getUserId()), e.getLocalizedMessage());
		}
	}

}

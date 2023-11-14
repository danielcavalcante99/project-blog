package br.com.projectblog.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import br.com.projectblog.domains.Role;
import br.com.projectblog.dtos.requests.AlbumRequestDTO;
import br.com.projectblog.exceptions.BusinessException;
import br.com.projectblog.exceptions.ResourceNotFoundException;
import br.com.projectblog.mappers.AlbumMapper;
import br.com.projectblog.mappers.AlbumMapperImpl;
import br.com.projectblog.mappers.PhotoMapper;
import br.com.projectblog.mappers.PhotoMapperImpl;
import br.com.projectblog.mappers.UserMapper;
import br.com.projectblog.mappers.UserMapperImpl;
import br.com.projectblog.models.Album;
import br.com.projectblog.models.User;
import br.com.projectblog.repositories.AlbumRepository;
import br.com.projectblog.utils.UserUtils;
import br.com.projectblog.validations.groups.OnCreate;
import br.com.projectblog.validations.groups.OnUpdate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class AlbumServiceTest {
	
	private static final String FIELD_ALBUM_ID = "albumId";
	private static final String FIELD_USER_ID = "userId";
	private static final String FIELD_NAME = "name";

	@Mock private  AlbumRepository repository;
	
	@Mock private UserService userService;
	
	private AlbumService service;
	
	private AlbumMapper mapper;
	
	private PhotoMapper photoMapper;
	
	private UserMapper userMapper;
	
	private Validator validator;
	
	private MockedStatic<UserUtils> mockedStatic;
	
	
    @BeforeEach
    void initMockss() {
       MockitoAnnotations.openMocks(this);
       this.photoMapper =  new PhotoMapperImpl();
       this.mapper =  new AlbumMapperImpl();
       this.userMapper =  new UserMapperImpl();
       this.mockedStatic = mockStatic(UserUtils.class);
       this.service = new AlbumService(repository, mapper, photoMapper, userMapper, userService);
       this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
    
    @AfterEach
    public void cleanup() {
      mockedStatic.close();
    }
    
    private Set<ConstraintViolation<AlbumRequestDTO>> validarDadosDeEntradaPadrao(AlbumRequestDTO dto) {
    	Set<ConstraintViolation<AlbumRequestDTO>> violations = this.validator.validate(dto);
    		
        violations.forEach(action -> {
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'name' é obrigatório ser informado.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_NAME);
        });
        
        return violations;
          
    }
    
    
    private Set<ConstraintViolation<AlbumRequestDTO>> validarDadosDeEntradaCriacao(AlbumRequestDTO dto) {

    	Set<ConstraintViolation<AlbumRequestDTO>> violations = validator.validate(dto, OnCreate.class);
    	 
        violations.forEach(action -> { 
        	if(action.getPropertyPath().toString().equals(FIELD_ALBUM_ID)) { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'albumId' não deve ser informado na inclusão.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_ALBUM_ID); 
	            
        	} else {
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'userId' é obrigatório ser informado na atualização e inclusão.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_USER_ID); 
        	}
        });
        
        return violations;
    }
       
    
    private Set<ConstraintViolation<AlbumRequestDTO>> validarDadosDeEntradaAtualizacao(AlbumRequestDTO dto) {
    	
    	Set<ConstraintViolation<AlbumRequestDTO>> violations = validator.validate(dto, OnUpdate.class);
    	
        violations.forEach(action -> { 
        	if(action.getPropertyPath().toString().equals(FIELD_ALBUM_ID)) { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'albumId' é obrigatório ser informado na atualização.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_ALBUM_ID); 
	            
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

    	AlbumRequestDTO dto = AlbumRequestDTO.builder().build();
        
        Set<ConstraintViolation<AlbumRequestDTO>> validarDadosDeEntradaPadrao = this.validarDadosDeEntradaPadrao(dto);
        assertThat(validarDadosDeEntradaPadrao.size()).isEqualTo(1); 
        
    	dto.setAlbumId(UUID.randomUUID().toString());
    	dto.setUserId(null);
    	dto.setName("Melhor album do momento");
        Set<ConstraintViolation<AlbumRequestDTO>> validarDadosDeEntradaCriacao = this.validarDadosDeEntradaCriacao(dto);
        assertThat(validarDadosDeEntradaCriacao.size()).isEqualTo(2);
        
    	dto.setAlbumId(null);
    	dto.setName("Melhor album do momento");
        Set<ConstraintViolation<AlbumRequestDTO>> validarDadosDeEntradaAtualizacao = this.validarDadosDeEntradaAtualizacao(dto);
        assertThat(validarDadosDeEntradaAtualizacao.size()).isEqualTo(2);
    }
    
    
    @Test
    @DisplayName("Teste - tentar atualizar album que não existe.")
    void testeAtualizandoAlbumInesistente() {
    	
    	AlbumRequestDTO dto = AlbumRequestDTO.builder()
    	    	.albumId(UUID.randomUUID().toString())
    	    	.userId(UUID.randomUUID().toString())
    	    	.name("Melhor album do momento")
        	 	.build();
    	
        Set<ConstraintViolation<AlbumRequestDTO>> validarDadosDeEntradaAtualizacao = this.validarDadosDeEntradaAtualizacao(dto);
    	
        when(this.repository.findById(any())).thenReturn(Optional.empty());
		    	
		try {
			this.service.update(dto);
			
		} catch (ResourceNotFoundException e) {
			assertThat(validarDadosDeEntradaAtualizacao.size()).isEqualTo(0);
			assertEquals(String.format("Album pelo identificador %s não encontrado para ser atualizado.", dto.getAlbumId()), e.getLocalizedMessage());
		}
    }
    
    
    @Test
    @DisplayName("Teste - tentar atualizar album em que não existe usuário.")
    void testeAtualizandoUsuarioInesistente() {
    	
    	AlbumRequestDTO dto = AlbumRequestDTO.builder()
    	    	.albumId(UUID.randomUUID().toString())
    	    	.userId(UUID.randomUUID().toString())
    	    	.name("Melhor album do momento")
        	 	.build();
    	
        Set<ConstraintViolation<AlbumRequestDTO>> validarDadosDeEntradaAtualizacao = this.validarDadosDeEntradaAtualizacao(dto);

    	when(this.repository.findById(any())).thenReturn(Optional.ofNullable(this.mapper.albumRequestDTOToAlbum(dto)));
    	when(this.userService.findById(any())).thenReturn(Optional.empty());
		    	
		try {
			this.service.update(dto);
			
		} catch (ResourceNotFoundException e) {
			assertThat(validarDadosDeEntradaAtualizacao.size()).isEqualTo(0);
			assertEquals(String.format("Usuário pelo identificador %s não encontrado.", dto.getUserId()), e.getLocalizedMessage());
		}
    }
    
    
    @Test
    @DisplayName("Teste - tentar atualizar um album utilizando outro usuário.")
    void testeAtualizacaoAlbumComOutroUsuario() {
    	
    	AlbumRequestDTO dto = AlbumRequestDTO.builder()
    	    	.albumId(UUID.randomUUID().toString())
    	    	.userId(UUID.randomUUID().toString())
    	    	.name("Melhor album do momento")
        	 	.build();
    	
    	User user = new User();
    	user.setUserId(UUID.randomUUID());
    	user.setUsername("daniel");
    	user.setRole(Role.USER);
    	
    	Set<ConstraintViolation<AlbumRequestDTO>> validarDadosDeEntradaAtualizacao = this.validarDadosDeEntradaAtualizacao(dto);
    	
    	mockedStatic.when(UserUtils::getUsernameLogado).thenReturn("marcos");
        
    	when(this.repository.findById(UUID.fromString(dto.getAlbumId()))).thenReturn(Optional.ofNullable(this.mapper.albumRequestDTOToAlbum(dto)));
    	when(this.userService.findById(UUID.fromString(dto.getUserId()))).thenReturn(Optional.of(this.userMapper.userToUserDTO(user)));

		try {
			this.service.update(dto);

		} catch (BusinessException e) {
			assertThat(validarDadosDeEntradaAtualizacao.size()).isEqualTo(0);
			assertEquals("Não é possível atualizar album utilizando outro usuário.", e.getLocalizedMessage());
		}
	}
    
    
    @Test
    @DisplayName("Teste - tentar criar um album com usuário que não existe.")
    void testeCriandoAlbumComUsuarioInesistente() {
    	
    	AlbumRequestDTO dto = AlbumRequestDTO.builder()
    	    	.albumId(null)
    	    	.userId(UUID.randomUUID().toString())
    	    	.name("Melhor album do momento")
        	 	.build();
    	
        Set<ConstraintViolation<AlbumRequestDTO>> validarDadosDeEntradaCriacao = this.validarDadosDeEntradaCriacao(dto);
        
    	when(this.userService.findById(any())).thenReturn(Optional.empty());

		try {
			this.service.insert(dto);

		} catch (ResourceNotFoundException e) {
			assertThat(validarDadosDeEntradaCriacao.size()).isEqualTo(0);
			assertEquals(String.format("Usuário pelo identificador %s não encontrado.", dto.getUserId()), e.getLocalizedMessage());
		}
	}
    
    
    @Test
    @DisplayName("Teste - tentar criar um album utilizando outro usuário.")
    void testeCriarAlbumComOutroUsuario() {
    	
    	AlbumRequestDTO dto = AlbumRequestDTO.builder()
    	    	.albumId(null)
    	    	.userId(UUID.randomUUID().toString())
    	    	.name("Melhor album do momento")
        	 	.build();
    	
    	User user = new User();
    	user.setUserId(UUID.randomUUID());
    	user.setUsername("daniel");
    	user.setRole(Role.USER);
    	
    	Set<ConstraintViolation<AlbumRequestDTO>> validarDadosDeEntradaCriacao = this.validarDadosDeEntradaCriacao(dto);
    	
    	mockedStatic.when(UserUtils::getUsernameLogado).thenReturn("marcos");
        
    	when(this.userService.findById(UUID.fromString(dto.getUserId()))).thenReturn(Optional.of(this.userMapper.userToUserDTO(user)));

		try {
			this.service.insert(dto);

		} catch (BusinessException e) {
			assertThat(validarDadosDeEntradaCriacao.size()).isEqualTo(0);
			assertEquals("Não é possível inserir album utilizando outro usuário.", e.getLocalizedMessage());
		}
	}
    
    
    @Test
    @DisplayName("Teste - tentar excluir um album com usuário que não existe.")
    void testeExcluirAlbumComUsuarioInesistente() {
    	
    	UUID id = UUID.randomUUID();
        
    	when(this.repository.findById(id)).thenReturn(Optional.empty());

		try {
			this.service.deleteById(id);

		} catch (ResourceNotFoundException e) {
			assertEquals(String.format("Album pelo id %s não existe.", id), e.getLocalizedMessage());
		}
	}
    
    
    @Test
    @DisplayName("Teste - tentar excluir um album utilizando outro usuário.")
    void testeExcluirAlbumComOutroUsuario() {
    	
    	User user = new User();
    	user.setUserId(UUID.randomUUID());
    	user.setUsername("daniel");
    	user.setRole(Role.USER);
    	
    	Album album = new Album();
    	album.setAlbumId(UUID.randomUUID());
    	album.setUser(user); 
    	
    	mockedStatic.when(UserUtils::getUsernameLogado).thenReturn("marcos");
        
    	when(this.repository.findById(album.getAlbumId())).thenReturn(Optional.of(album));
    	when(this.userService.findById(album.getUser().getUserId())).thenReturn(Optional.of(this.userMapper.userToUserDTO(user)));

		try {
			this.service.deleteById(album.getAlbumId());

		} catch (BusinessException e) {
			assertEquals("Não é possível excluir album utilizando outro usuário, a menos que seja o administrador.", e.getLocalizedMessage());
		}
	}

}

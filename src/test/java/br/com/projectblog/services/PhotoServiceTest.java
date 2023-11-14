package br.com.projectblog.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import br.com.projectblog.domains.Role;
import br.com.projectblog.dtos.requests.PhotoRequestDTO;
import br.com.projectblog.exceptions.BusinessException;
import br.com.projectblog.exceptions.ResourceNotFoundException;
import br.com.projectblog.mappers.AlbumMapper;
import br.com.projectblog.mappers.AlbumMapperImpl;
import br.com.projectblog.mappers.PhotoMapper;
import br.com.projectblog.mappers.PhotoMapperImpl;
import br.com.projectblog.models.Album;
import br.com.projectblog.models.Photo;
import br.com.projectblog.models.User;
import br.com.projectblog.repositories.PhotoRepository;
import br.com.projectblog.utils.UserUtils;
import br.com.projectblog.validations.groups.OnCreate;
import br.com.projectblog.validations.groups.OnUpdate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class PhotoServiceTest {

    private static final String FIELD_PHOTO_ID = "photoId";
	private static final String FIELD_ALBUM_ID = "albumId";
	private static final String FIELD_IMAGE = "image";
	
	@Mock private PhotoRepository repository;
	
	@Mock private AlbumService albumService;
	
	private PhotoService service;
	
	private PhotoMapper mapper;
	
	private AlbumMapper albumMapper;
	
	private Validator validator;
	
	private MockedStatic<UserUtils> mockedStatic;


	@BeforeEach
    void initMockss() {
       MockitoAnnotations.openMocks(this);
       this.mockedStatic = mockStatic(UserUtils.class);
       this.mapper =  new PhotoMapperImpl();
       this.albumMapper =  new AlbumMapperImpl();
       this.service = new PhotoService(this.repository, this.albumService, this.mapper);
       this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
	
    @AfterEach
    public void cleanup() {
      mockedStatic.close();
    }
	
    private Set<ConstraintViolation<PhotoRequestDTO>> validarDadosDeEntradaPadrao(PhotoRequestDTO dto) {
    	Set<ConstraintViolation<PhotoRequestDTO>> violations = this.validator.validate(dto);
    		 
        violations.forEach(action -> {
            if(action.getPropertyPath().toString().equals(FIELD_IMAGE)) { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'image' é obrigatório ser informado.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_IMAGE);
	            
        	}
        });  
        
        return violations;
    }

	
    private Set<ConstraintViolation<PhotoRequestDTO>> validarDadosDeEntradaCriacao(PhotoRequestDTO dto) {

    	Set<ConstraintViolation<PhotoRequestDTO>> violations = validator.validate(dto, OnCreate.class);
    
        violations.forEach(action -> { 
        	if(action.getPropertyPath().toString().equals(FIELD_PHOTO_ID)) { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'photoId' não deve ser informado na inclusão.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_PHOTO_ID); 
	            
        	} else {
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'albumId' é obrigatório.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_ALBUM_ID); 
        	}
        });  
        
        return violations;
    }
       
    
    private Set<ConstraintViolation<PhotoRequestDTO>> validarDadosDeEntradaAtualizacao(PhotoRequestDTO dto) {
    	
    	Set<ConstraintViolation<PhotoRequestDTO>> violations = validator.validate(dto, OnUpdate.class);

        violations.forEach(action -> { 
        	if(action.getPropertyPath().toString().equals(FIELD_PHOTO_ID)) { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'photoId' é obrigatório ser informado na atualização.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_PHOTO_ID); 
	            
        	}  else {
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'albumId' é obrigatório.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_ALBUM_ID); 
        	}
        });  
        
        return violations;
    }
    
    
    @Test
    @DisplayName("Teste - validar dados de entrada")
    void validarDadosDeEntrada() {

    	PhotoRequestDTO dto = PhotoRequestDTO.builder().build();
    	
    	Set<ConstraintViolation<PhotoRequestDTO>> validarDadosDeEntradaPadrao = this.validarDadosDeEntradaPadrao(dto);
    	assertThat(validarDadosDeEntradaPadrao.size()).isEqualTo(1);
    	
    	dto.setPhotoId(UUID.randomUUID().toString());
    	dto.setImageEncodeBase64(Base64.encodeBase64String(new byte[7]));
    	dto.setAlbumId(null);
    	
        Set<ConstraintViolation<PhotoRequestDTO>> validarDadosDeEntradaCriacao = this.validarDadosDeEntradaCriacao(dto);
        assertThat(validarDadosDeEntradaCriacao.size()).isEqualTo(2);
        
    	dto.setPhotoId(null);
    	
        Set<ConstraintViolation<PhotoRequestDTO>> validarDadosDeEntradaAtualizacao = this.validarDadosDeEntradaAtualizacao(dto);
        assertThat(validarDadosDeEntradaAtualizacao.size()).isEqualTo(2);
    }
    
    
    @Test
    @DisplayName("Teste - tentar incluir foto em um album que não existe.")
    void testeIncluindoPhotoAlbumInesistente() {
    	
    	PhotoRequestDTO dto = PhotoRequestDTO.builder().build();
    	dto.setPhotoId(null);
    	dto.setImageEncodeBase64(Base64.encodeBase64String(new byte[7]));
    	dto.setAlbumId(UUID.randomUUID().toString());
    	
        Set<ConstraintViolation<PhotoRequestDTO>> validarDadosDeEntradaCriacao = this.validarDadosDeEntradaCriacao(dto);
    	
    	when(this.albumService.findById(UUID.fromString(dto.getAlbumId()))).thenReturn(Optional.empty());
		    	
		try {
			this.service.insertPhotoInAlbum(dto);
			
		} catch (ResourceNotFoundException e) {
			 assertThat(validarDadosDeEntradaCriacao.size()).isEqualTo(0);
			assertEquals(String.format("Album pelo identificador %s não encontrado para ser atualizado.", dto.getAlbumId()), e.getLocalizedMessage());
		}
    }
    
    
    @Test
    @DisplayName("Teste - tentar incluir foto em um album utilizando outro usuário.")
    void testeIncluindoPhotoAlbumComOutroUsuario() {
    	
    	PhotoRequestDTO dto = PhotoRequestDTO.builder().build();
    	dto.setPhotoId(null);
    	dto.setImageEncodeBase64(Base64.encodeBase64String(new byte[7]));
    	dto.setAlbumId(UUID.randomUUID().toString());
    	
    	User user = new User();
    	user.setUsername("daniel");
    	user.setRole(Role.USER);
    	
    	Album album = new Album();
    	album.setUser(user);
    	
    	mockedStatic.when(UserUtils::getUsernameLogado).thenReturn("marcos");
    	
        Set<ConstraintViolation<PhotoRequestDTO>> validarDadosDeEntradaCriacao = this.validarDadosDeEntradaCriacao(dto);
    	
        when(this.albumService.findByIdAlbum(UUID.fromString(dto.getAlbumId()))).thenReturn(Optional.of(album));
    	
		try {
			this.service.insertPhotoInAlbum(dto);
			
		} catch (BusinessException e) {
			 assertThat(validarDadosDeEntradaCriacao.size()).isEqualTo(0);
			assertEquals("Não é possível inserir foto no album utilizando outro usuário.", e.getLocalizedMessage());
		}
    }
    
    
    @Test
    @DisplayName("Teste - tentar atualizar foto que não existe.")
    void testeAtualizandoPhotoInesistente() {
    	
    	PhotoRequestDTO dto = PhotoRequestDTO.builder().build();
    	dto.setPhotoId((UUID.randomUUID().toString()));
    	dto.setImageEncodeBase64(Base64.encodeBase64String(new byte[7]));
    	dto.setAlbumId(UUID.randomUUID().toString());
    	
        Set<ConstraintViolation<PhotoRequestDTO>> validarDadosDeEntradaAtualizacao = this.validarDadosDeEntradaAtualizacao(dto);
    	
    	when(this.repository.findById(UUID.fromString(dto.getPhotoId()))).thenReturn(Optional.empty());
		    	
		try {
			this.service.updatePhotoInAlbum(dto);
			
		} catch (ResourceNotFoundException e) {
			 assertThat(validarDadosDeEntradaAtualizacao.size()).isEqualTo(0);
			assertEquals(String.format("Photo pelo identificador %s não encontrado para ser atualizado.", dto.getPhotoId()), e.getLocalizedMessage());
		}
    }
    
    
    @Test
    @DisplayName("Teste - tentar atualizar foto de um album que não existe.")
    void testeAtualizandoPhotoAlbumInesistente() {
    	
    	PhotoRequestDTO dto = PhotoRequestDTO.builder().build();
    	dto.setPhotoId((UUID.randomUUID().toString()));
    	dto.setImageEncodeBase64(Base64.encodeBase64String(new byte[7]));
    	dto.setAlbumId(UUID.randomUUID().toString());
    	
        Set<ConstraintViolation<PhotoRequestDTO>> validarDadosDeEntradaAtualizacao = this.validarDadosDeEntradaAtualizacao(dto);
    	
        when(this.albumService.findById(UUID.fromString(dto.getAlbumId()))).thenReturn(Optional.empty());
        when(this.repository.findById(UUID.fromString(dto.getPhotoId()))).thenReturn(Optional.of(new Photo()));
    	
		try {
			this.service.updatePhotoInAlbum(dto);
			
		} catch (ResourceNotFoundException e) {
			assertThat(validarDadosDeEntradaAtualizacao.size()).isEqualTo(0);
			assertEquals(String.format("Album pelo identificador %s não encontrado.", dto.getAlbumId()), e.getLocalizedMessage());
		}
    }
    
    
    @Test
    @DisplayName("Teste - tentar atualizar foto de um album utilizando outro usuário.")
    void testeAtualizandoPhotoAlbumComOutroUsuario() {
    	
    	PhotoRequestDTO dto = PhotoRequestDTO.builder().build();
    	dto.setPhotoId((UUID.randomUUID().toString()));
    	dto.setImageEncodeBase64(Base64.encodeBase64String(new byte[7]));
    	dto.setAlbumId(UUID.randomUUID().toString());
    	
    	User user = new User();
    	user.setUsername("daniel");
    	user.setRole(Role.USER);
    	
    	Album album = new Album();
    	album.setUser(user);

    	Photo photo = new Photo();
    	photo.setPhotoId(UUID.randomUUID());
    	photo.setDateCreate(LocalDateTime.now());
    	photo.setDateUpdate(LocalDateTime.now());
    	photo.setAlbum(album);
    	
    	mockedStatic.when(UserUtils::getUsernameLogado).thenReturn("marcos");
    	
        Set<ConstraintViolation<PhotoRequestDTO>> validarDadosDeEntradaAtualizacao = this.validarDadosDeEntradaAtualizacao(dto);
    	
        when(this.albumService.findById(UUID.fromString(dto.getAlbumId()))).thenReturn(Optional.of(this.albumMapper.albumToAlbumDTO(album)));
        when(this.repository.findById(UUID.fromString(dto.getPhotoId()))).thenReturn(Optional.of(photo));
    	
		try {
			this.service.updatePhotoInAlbum(dto);
			
		} catch (BusinessException e) {
			assertThat(validarDadosDeEntradaAtualizacao.size()).isEqualTo(0);
			assertEquals("Não é possível atualizar foto do album utilizando outro usuário.", e.getLocalizedMessage());
		}
    }
    
    
    @Test
    @DisplayName("Teste - tentar excluir foto que não existe.")
    void testeExcluindoPhotoInesistente() {
    	
    	UUID id = UUID.randomUUID();
    	    	
        when(this.repository.findById(UUID.fromString(id.toString()))).thenReturn(Optional.empty());

		try {
			this.service.deleteById(id);
			
		} catch (ResourceNotFoundException e) {
			assertEquals(String.format("Photo pelo id %s não existe.", id), e.getLocalizedMessage());
		}
    }
    
    
    @Test
    @DisplayName("Teste - tentar excluir foto do album utilizando outro usuário.")
    void testeExcluindoPhotoAlbumOutroUsuario() {
    	
    	User user = new User();
    	user.setUsername("daniel");
    	user.setRole(Role.USER);
    	
    	Album album = new Album();
    	album.setUser(user);

    	Photo photo = new Photo();
    	photo.setPhotoId(UUID.randomUUID());
    	photo.setDateCreate(LocalDateTime.now());
    	photo.setDateUpdate(LocalDateTime.now());
    	photo.setAlbum(album);
    	
    	mockedStatic.when(UserUtils::getUsernameLogado).thenReturn("marcos");

		when(this.repository.findById(photo.getPhotoId()))
			    .thenReturn(Optional.of(photo));
		    	
		try {
			this.service.deleteById(photo.getPhotoId());
			
		} catch (BusinessException e) {
			assertEquals("Não é possível excluir foto do album utilizando outro usuário, a menos que seja o administrador.", e.getLocalizedMessage());
		}
    }
    

}

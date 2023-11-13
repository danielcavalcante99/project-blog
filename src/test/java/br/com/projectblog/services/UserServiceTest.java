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
import br.com.projectblog.dtos.requests.UserRequestDTO;
import br.com.projectblog.exceptions.BusinessException;
import br.com.projectblog.exceptions.ResourceNotFoundException;
import br.com.projectblog.mappers.UserMapper;
import br.com.projectblog.mappers.UserMapperImpl;
import br.com.projectblog.respositories.UserRepository;
import br.com.projectblog.utils.UserUtils;
import br.com.projectblog.validations.groups.OnCreate;
import br.com.projectblog.validations.groups.OnUpdate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class UserServiceTest {
	
	private static final String FIELD_USER_ID = "userId";

	private static final String FIELD_ENABLED = "enabled";

	private static final String FIELD_ROLE = "role";

	private static final String FIELD_PASSWORD = "password";

	private static final String FIELD_EMAIL = "email";

	private static final String FIELD_USERNAME = "username";

	private UserService service;
	
	private UserMapper mapper;
	
	private Validator validator;	

    private MockedStatic<UserUtils> mockedStatic;
    
    @Mock private UserRepository repository;
 

    @BeforeEach
    void initMockss() {
       MockitoAnnotations.openMocks(this);
       mockedStatic = mockStatic(UserUtils.class);
       this.mapper =  new UserMapperImpl();      
       this.service = new UserService(this.repository, this.mapper);
       this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
    
    @AfterEach
    public void cleanup() {
      mockedStatic.close();
    }

    private void validarDadosDeEntradaPadrao(UserRequestDTO dto) {
    	Set<ConstraintViolation<UserRequestDTO>> violations = this.validator.validate(dto);
    		
        assertThat(violations.size()).isEqualTo(5); 
        violations.forEach(action -> {
            if(action.getPropertyPath().toString().equals(FIELD_USERNAME)) { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'username' é obrigatório ser informado.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_USERNAME);
	            
        	} else if(action.getPropertyPath().toString().equals(FIELD_EMAIL)) { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'email' é obrigatório ser informado.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_EMAIL);
	            
        	} else if(action.getPropertyPath().toString().equals(FIELD_PASSWORD)) { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'password' é obrigatório ser informado.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_PASSWORD);
	            
        	} else if(action.getPropertyPath().toString().equals(FIELD_ROLE)) { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'role' é obrigatório ser informado.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_ROLE);
	            
        	} else { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'enabled' é obrigatório ser informado.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_ENABLED);	            
        	} 
        });   
    }
    
    
    private void validarDadosDeEntradaCriacao(UserRequestDTO dto) {
    	dto.setUserId(UUID.randomUUID().toString());
    	dto.setUsername("daniel");
    	dto.setEmail("daniel@gmail.com");
    	dto.setEnabled(true);
    	dto.setPassword("12345678");
    	dto.setRole(Role.ADMIN);
    	
    	Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto, OnCreate.class);
    	
        assertThat(violations.size()).isEqualTo(1); 
        violations.forEach(action -> { 
	            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'userId' não deve ser informado na inclusão.");
	            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_USER_ID);  
        });   
    }
       
    
    private void validarDadosDeEntradaAtualizacao(UserRequestDTO dto) {
    	dto.setUserId(null);
    	dto.setUsername("daniel");
    	dto.setEmail("daniel@gmail.com");
    	dto.setEnabled(true);
    	dto.setPassword("12345678");
    	dto.setRole(Role.ADMIN);
    	
    	Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto, OnUpdate.class);
    	
        assertThat(violations.size()).isEqualTo(1); 
        violations.forEach(action -> {
            assertThat(action.getMessageTemplate()).isEqualTo("O campo 'userId' é obrigatório ser informado na atualização.");
            assertThat(action.getPropertyPath().toString()).isEqualTo(FIELD_USER_ID);  
        });    
    }
    
    
    @Test
    @DisplayName("Teste - validar dados de entrada")
    void validarDadosDeEntrada() {

    	UserRequestDTO dto = UserRequestDTO.builder().build();
        
        this.validarDadosDeEntradaPadrao(dto);
        this.validarDadosDeEntradaCriacao(dto);
        this.validarDadosDeEntradaAtualizacao(dto);
    }
    
    
    @Test
    @DisplayName("Teste - tentar atualizar usuário que não existe.")
    void testeAtualizandoUsuarioInesistente() {

    	UserRequestDTO dto = UserRequestDTO.builder()
		    		.userId(UUID.randomUUID().toString())	
		    	 	.username("daniel")
		    	 	.email("test@gmail.com")
		    	 	.enabled(true)
		    	 	.password("asdasdasda")
		    	 	.role(Role.ADMIN)
		    	 	.build();
    	
    	when(this.repository.findById(UUID.fromString(dto.getUserId()))).thenReturn(Optional.empty());
		    	
		try {
			this.service.update(dto);
			
		} catch (ResourceNotFoundException e) {
			assertEquals(String.format("Usuário pelo identificador %s não encontrado para ser atualizado.", dto.getUserId()), e.getLocalizedMessage());
		}
    }
    
    
    @Test
    @DisplayName("Teste - tentar atualizar outro usuário com perfil comum.")
    void testeAtualizandoOutroUsuarioComPerfilComum() {

    	UserRequestDTO dto = UserRequestDTO.builder()
		    		.userId(UUID.randomUUID().toString())	
		    	 	.username("daniel")
		    	 	.email("test@gmail.com")
		    	 	.enabled(true)
		    	 	.password("asdasdasda")
		    	 	.role(Role.USER)
		    	 	.build();
    	
    	
    	mockedStatic.when(UserUtils::getUsernameLogado).thenReturn("marcos");
    	
		when(this.repository.findById(UUID.fromString(dto.getUserId())))
				.thenReturn(Optional.of(this.mapper.userRequestDTOtoUser(dto)));
		    	
		try {
			this.service.update(dto);
			
		} catch (BusinessException e) {
			assertEquals("Não é possível alterar informações de outro usuário que não seja o seu, a menos que seja o administrador.", e.getLocalizedMessage());
		}
    }
    
 
    @Test
    @DisplayName("Teste - tentar atualizar email ou username que outro usuário já utiliza.")
    void testeAtualizandoUsuarioComUsernameOuEmailJaCadastrado() {
    	UserRequestDTO newUserDto = UserRequestDTO.builder()
        	 	.username("daniel")
        	 	.email("test.alfa@gmail.com")
        	 	.enabled(true)
        	 	.password("asdasdasda")
        	 	.role(Role.ADMIN)
        	 	.build();
    	
    	UserRequestDTO actualUserDto = UserRequestDTO.builder()
        	 	.username("danielhen")
        	 	.email("test@gmail.com")
        	 	.enabled(true)
        	 	.password("asdasdasda")
        	 	.role(Role.ADMIN)
        	 	.build();
    	
    	mockedStatic.when(UserUtils::getUsernameLogado).thenReturn(actualUserDto.getUsername());
		when(this.repository.findById(any())).thenReturn(Optional.of(this.mapper.userRequestDTOtoUser(actualUserDto)));
		when(this.repository.findByUsernameOrEmail(newUserDto.getUsername(), newUserDto.getEmail()))
				.thenReturn(Optional.ofNullable(any()));

		try {
			this.service.update(newUserDto);

		} catch (BusinessException e) {
			assertEquals("'email' ou 'username' já estão sendo usado por outro usuário!", e.getLocalizedMessage());
		}
	}
    
    
    @Test
    @DisplayName("Teste - tentar criar um usuário com email e username já usado por outro usuário.")
    void testeCriandoUsuarioComUsernameOuEmailJaCadastrados() {
    	UserRequestDTO newUserDto = UserRequestDTO.builder()
        	 	.username("daniel")
        	 	.email("test.alfa@gmail.com")
        	 	.enabled(true)
        	 	.password("asdasdasda")
        	 	.role(Role.ADMIN)
        	 	.build();

		when(this.repository.findByUsernameOrEmail(newUserDto.getUsername(), newUserDto.getEmail()))
				.thenReturn(Optional.ofNullable(this.mapper.userRequestDTOtoUser(newUserDto)));

		try {
			this.service.insert(newUserDto);

		} catch (BusinessException e) {
			assertEquals("'email' ou 'username' já estão sendo usado por outro usuário!", e.getLocalizedMessage());
		}
	}
   
    
    @Test
    @DisplayName("Teste - tentar excluir usuário que não existe.")
    void testeExcluindoUsuarioInesistente() {
        
    	UUID id = UUID.fromString(UUID.randomUUID().toString());
    	when(this.repository.findById(id)).thenReturn(Optional.empty());
		    	
		try {
			this.service.deleteById(id);
			
		} catch (ResourceNotFoundException e) {
			assertEquals(String.format("Usuário pelo id %s não existe.", id), e.getLocalizedMessage());
		}
    }
    
    
    @Test
    @DisplayName("Teste - tentar excluir outro usuário com perfil comum.")
    void testeExcluindoOutroUsuarioComPerfilComum() {

    	UserRequestDTO dto = UserRequestDTO.builder()
		    		.userId(UUID.randomUUID().toString())	
		    	 	.username("daniel")
		    	 	.email("test@gmail.com")
		    	 	.enabled(true)
		    	 	.password("asdasdasda")
		    	 	.role(Role.USER)
		    	 	.build();
    	
    	
    	mockedStatic.when(UserUtils::getUsernameLogado).thenReturn("marcos");
    	
		when(this.repository.findById(UUID.fromString(dto.getUserId())))
				.thenReturn(Optional.of(this.mapper.userRequestDTOtoUser(dto)));
		    	
		try {
			this.service.deleteById(UUID.fromString(dto.getUserId()));
			
		} catch (BusinessException e) {
			assertEquals("Não é possível excluir outro usuário que não seja o seu, a menos que seja o administrador.", e.getLocalizedMessage());
		}
    }

}

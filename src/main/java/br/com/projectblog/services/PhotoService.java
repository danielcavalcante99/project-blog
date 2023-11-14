package br.com.projectblog.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import br.com.projectblog.domains.Role;
import br.com.projectblog.dtos.PhotoDTO;
import br.com.projectblog.dtos.requests.PhotoRequestDTO;
import br.com.projectblog.exceptions.BusinessException;
import br.com.projectblog.exceptions.ResourceNotFoundException;
import br.com.projectblog.mappers.PhotoMapper;
import br.com.projectblog.models.Album;
import br.com.projectblog.models.Photo;
import br.com.projectblog.repositories.PhotoRepository;
import br.com.projectblog.utils.UserUtils;
import br.com.projectblog.validations.groups.OnCreate;
import br.com.projectblog.validations.groups.OnUpdate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class PhotoService {
	
	private final PhotoRepository repository;
	
	private final AlbumService albumService;
	
	private final PhotoMapper mapper;
	
	
	@Validated({ OnCreate.class })
	public PhotoDTO insertPhotoInAlbum(@Valid PhotoRequestDTO dto) {
		log.info("Requisição para criação do album: \n {}", dto);
		
		Album album = this.albumService.findByIdAlbum(UUID.fromString(dto.getAlbumId())).orElseThrow(() -> 
			new ResourceNotFoundException(String.format("Album pelo identificador %s não encontrado para ser atualizado.", dto.getAlbumId())
		));
		
		if(!UserUtils.getUsernameLogado().equals(album.getUser().getUsername()))
			throw new BusinessException("Não é possível inserir foto no album utilizando outro usuário.");
		
		Photo entity = this.mapper.photoRequestDTOTophoto(dto);
		entity.setAlbum(album);
		entity.setImage(Base64.decodeBase64(dto.getImageEncodeBase64()));
		entity.setDateCreate(LocalDateTime.now());
		entity.setDateUpdate(LocalDateTime.now());
		
		this.repository.save(entity);
		
		log.info("Photo criado com sucesso.");
		
		return this.mapper.photoTophotoDTO(entity);
	}
	
	
	@Validated({ OnUpdate.class })
	public PhotoDTO updatePhotoInAlbum(@Valid PhotoRequestDTO dto) {
		log.info("Requisição para atualização do photo: \n {}", dto);
		
		Photo actualEntity = this.repository.findById(UUID.fromString(dto.getPhotoId())).orElseThrow(() -> 
			new ResourceNotFoundException(String.format("Photo pelo identificador %s não encontrado para ser atualizado.", dto.getPhotoId())
		));	
		
		this.albumService.findById(UUID.fromString(dto.getAlbumId())).orElseThrow(() -> 
			new ResourceNotFoundException(String.format("Album pelo identificador %s não encontrado.", dto.getAlbumId())
		));
		
		if(!UserUtils.getUsernameLogado().equals(actualEntity.getAlbum().getUser().getUsername()))
			throw new BusinessException("Não é possível atualizar foto do album utilizando outro usuário.");
	
		Photo entity = this.mapper.photoRequestDTOTophoto(dto);
		entity.setAlbum(actualEntity.getAlbum());
		entity.setImage(actualEntity.getImage());
		entity.setDateUpdate(LocalDateTime.now());
		entity.setDateCreate(actualEntity.getDateCreate());
		
		this.repository.save(entity);
		
		log.info("Photo atualizado com sucesso.");
		
		return this.mapper.photoTophotoDTO(entity);
	}
	
	
	public void deleteById(@NotNull UUID id) {
		Photo photo = this.repository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(String.format("Photo pelo id %s não existe.", id)));
		
		if(!UserUtils.getUsernameLogado().equals(photo.getAlbum().getUser().getUsername()) && !photo.getAlbum().getUser().getRole().equals(Role.ADMIN))
			throw new BusinessException("Não é possível excluir foto do album utilizando outro usuário, a menos que seja o administrador.");
		
		this.repository.deleteById(id);
		log.info("Photo pelo id {} foi excluído com sucesso.", id);
	}

}

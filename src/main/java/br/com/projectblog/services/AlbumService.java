package br.com.projectblog.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import br.com.projectblog.domains.Role;
import br.com.projectblog.dtos.AlbumDTO;
import br.com.projectblog.dtos.PhotoDTO;
import br.com.projectblog.dtos.UserDTO;
import br.com.projectblog.dtos.filters.FilterAlbumDTO;
import br.com.projectblog.dtos.requests.AlbumRequestDTO;
import br.com.projectblog.exceptions.BusinessException;
import br.com.projectblog.exceptions.ResourceNotFoundException;
import br.com.projectblog.mappers.AlbumMapper;
import br.com.projectblog.mappers.PhotoMapper;
import br.com.projectblog.mappers.UserMapper;
import br.com.projectblog.models.Album;
import br.com.projectblog.repositories.AlbumRepository;
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
public class AlbumService {
	
	private final AlbumRepository repository;
	
	private final AlbumMapper mapper;
	
	private final PhotoMapper photoMapper;
	
	private final UserMapper userMapper;
	
	private final UserService userService;
	
	
	public Optional<Album> findByIdAlbum(@NotNull UUID id) {
		return this.repository.findById(id);
	}
	
	public Optional<AlbumDTO> findById(@NotNull UUID id) {
		Album album = this.repository.findById(id).orElse(null);
		
		List<PhotoDTO> photosDTO = new ArrayList<>();
		album.getPhotos().forEach(photo -> {
			
			PhotoDTO photoDTO =	PhotoDTO.builder()
					.photoId(photo.getPhotoId())
					.albumId(album.getAlbumId())
					.image(photo.getImage())
					.dateCreate(LocalDateTime.now())
					.dateUpdate(LocalDateTime.now())
					.build();
			
			photosDTO.add(photoDTO);
		});
		
		AlbumDTO albumDTO = AlbumDTO.builder()
				.albumId(album.getAlbumId())	
				.userId(album.getUser().getUserId())
				.name(album.getName())
				.photosDTO(photosDTO)
				.dateCreate(album.getDateCreate())
				.dateUpdate(album.getDateUpdate()).build();
		
		return Optional.ofNullable(albumDTO);
	}
	
	
	public Page<AlbumDTO> findAllByFilter(FilterAlbumDTO filter, Pageable pageable) {
		List<Album> listAlbum = this.repository.findAllByFilter(filter, pageable).getContent();
		List<AlbumDTO> listAlbumDTO = new ArrayList<>();
		
		listAlbum.forEach(album -> {
			
			AlbumDTO albumDTO = AlbumDTO.builder()
				.albumId(album.getAlbumId())	
				.userId(album.getUser().getUserId())
				.name(album.getName())
				.photosDTO(this.photoMapper.listPhotoToListPhotoDTO(album.getPhotos()))
				.dateCreate(album.getDateCreate())
				.dateUpdate(album.getDateUpdate()).build();
			
			listAlbumDTO.add(albumDTO);
		});

		return new PageImpl<>(listAlbumDTO, pageable, listAlbumDTO.stream().count());
	}
	
	
	public void deleteById(@NotNull UUID id) {
		Album album = this.repository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(String.format("Album pelo id %s não existe.", id)));
		
		UserDTO userDTO = this.userService.findById(album.getUser().getUserId()).orElse(null);
		
		if(!UserUtils.getUsernameLogado().equals(userDTO.getUsername()) && !userDTO.getRole().equals(Role.ADMIN))
			throw new BusinessException("Não é possível excluir album utilizando outro usuário, a menos que seja o administrador.");
		
		this.repository.deleteById(id);
		log.info("Album pelo id {} foi excluído com sucesso.", id);
	}
	
	
	@Validated({ OnCreate.class })
	public AlbumDTO insert(@NotNull @Valid AlbumRequestDTO dto) {
		log.info("Requisição para criação do album: \n {}", dto);	
		AlbumDTO albumDTO = this.mapper.albumRequestDTOToAlbumDTO(dto);
		
		UserDTO userDTO = this.userService.findById(albumDTO.getUserId()).orElseThrow(() -> 
			new ResourceNotFoundException(String.format("Usuário pelo identificador %s não encontrado.", albumDTO.getUserId())
		));
		
		if(!UserUtils.getUsernameLogado().equals(userDTO.getUsername()))
			throw new BusinessException("Não é possível inserir album utilizando outro usuário.");
		
		albumDTO.setDateUpdate(LocalDateTime.now());
		albumDTO.setDateCreate(LocalDateTime.now());
		albumDTO.setPhotosDTO(List.of());
		
		Album entity = this.mapper.albumDTOToAlbum(albumDTO);
		entity.setUser(this.userMapper.userDTOtoUser(userDTO));
		
		entity = this.repository.save(entity);
		albumDTO.setAlbumId(entity.getAlbumId());
		
		log.info("Album criado com sucesso.");

		return albumDTO;
	}
	
	
	@Validated({ OnUpdate.class })
	public AlbumDTO update(@NotNull @Valid AlbumRequestDTO dto) {
		log.info("Requisição para atualização do album: \n {}", dto);
		
		Album album = this.repository.findById(UUID.fromString(dto.getAlbumId())).orElseThrow(() -> 
				new ResourceNotFoundException(String.format("Album pelo identificador %s não encontrado para ser atualizado.", dto.getAlbumId())
		));
		
		AlbumDTO albumDTO = this.mapper.albumToAlbumDTO(album);
		
		UserDTO userDTO = this.userService.findById(albumDTO.getUserId()).orElseThrow(() -> 
			new ResourceNotFoundException(String.format("Usuário pelo identificador %s não encontrado.", albumDTO.getUserId())
		));
		
		if(!UserUtils.getUsernameLogado().equals(userDTO.getUsername()))
			throw new BusinessException("Não é possível atualizar album utilizando outro usuário.");
		
		albumDTO.setName(dto.getName());
		albumDTO.setDateUpdate(LocalDateTime.now());
		
		Album entity = this.mapper.albumDTOToAlbum(albumDTO);
		entity.setUser(this.userMapper.userDTOtoUser(userDTO));
		
		this.repository.save(entity);
		
		log.info("Post atualizado com sucesso.");

		return albumDTO;
	}

}

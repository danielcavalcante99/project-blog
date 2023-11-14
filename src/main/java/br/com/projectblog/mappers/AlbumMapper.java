package br.com.projectblog.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.projectblog.dtos.AlbumDTO;
import br.com.projectblog.dtos.requests.AlbumRequestDTO;
import br.com.projectblog.models.Album;

@Mapper(componentModel = "spring")
public interface AlbumMapper {
	
	@Mapping(target = "albumId", source = "entity.albumId")
	@Mapping(target = "userId", source = "entity.user.userId")
	@Mapping(target = "name", source = "entity.name")
	@Mapping(target = "dateUpdate", source = "entity.dateUpdate", dateFormat = "dd-MM-yyyy HH:mm:ss")
	@Mapping(target = "dateCreate", source = "entity.dateCreate", dateFormat = "dd-MM-yyyy HH:mm:ss")
	@Mapping(target = "photosDTO", ignore = true)
	AlbumDTO albumToAlbumDTO(Album entity);

	@Mapping(target = "albumId", source = "dto.albumId")
	@Mapping(target = "user.userId", source = "dto.userId")
	@Mapping(target = "name", source = "dto.name")
	@Mapping(target = "dateUpdate", source = "dto.dateUpdate", dateFormat = "dd-MM-yyyy HH:mm:ss")
	@Mapping(target = "dateCreate", source = "dto.dateCreate", dateFormat = "dd-MM-yyyy HH:mm:ss")
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "photos", ignore = true)
	Album albumDTOToAlbum(AlbumDTO dto);
	
	@Mapping(target = "albumId", source = "dto.albumId")
	@Mapping(target = "userId", source = "dto.userId")
	@Mapping(target = "name", source = "dto.name")
	AlbumRequestDTO albumDTOToAlbumRequestDTO(AlbumDTO dto);
	
	@Mapping(target = "albumId", source = "dto.albumId")
	@Mapping(target = "userId", source = "dto.userId")
	@Mapping(target = "name", source = "dto.name")
	@Mapping(target = "photosDTO", ignore = true)
	@Mapping(target = "dateUpdate", ignore = true)
	@Mapping(target = "dateCreate", ignore = true)
	AlbumDTO albumRequestDTOToAlbumDTO(AlbumRequestDTO dto);
	
	@Mapping(target = "albumId", source = "dto.albumId")
	@Mapping(target = "user.userId", source = "dto.userId")
	@Mapping(target = "name", source = "dto.name")
	@Mapping(target = "photos", ignore = true)
	@Mapping(target = "dateUpdate", ignore = true)
	@Mapping(target = "dateCreate", ignore = true)
	Album albumRequestDTOToAlbum(AlbumRequestDTO dto);
	
	List<AlbumDTO> listAlbumToListAlbumDTO(List<Album> listPost);

}

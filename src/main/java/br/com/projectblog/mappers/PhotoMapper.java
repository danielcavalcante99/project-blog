package br.com.projectblog.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.projectblog.dtos.PhotoDTO;
import br.com.projectblog.dtos.requests.PhotoRequestDTO;
import br.com.projectblog.models.Photo;

@Mapper(componentModel = "spring")
public interface PhotoMapper {
	
	@Mapping(target = "photoId", source = "dto.photoId")
	@Mapping(target = "album.albumId", source = "dto.albumId")
	@Mapping(target = "image", source = "dto.image")
	@Mapping(target = "dateUpdate", source = "dto.dateUpdate", dateFormat = "dd-MM-yyyy HH:mm:ss")
	@Mapping(target = "dateCreate", source = "dto.dateCreate", dateFormat = "dd-MM-yyyy HH:mm:ss")
	Photo photoDTOTophoto(PhotoDTO dto);
	
	@Mapping(target = "photoId", source = "entity.photoId")
	@Mapping(target = "albumId", source = "entity.album.albumId")
	@Mapping(target = "image", source = "entity.image")
	@Mapping(target = "dateUpdate", source = "entity.dateUpdate", dateFormat = "dd-MM-yyyy HH:mm:ss")
	@Mapping(target = "dateCreate", source = "entity.dateCreate", dateFormat = "dd-MM-yyyy HH:mm:ss")
	PhotoDTO photoTophotoDTO(Photo entity);
	
	@Mapping(target = "photoId", source = "dto.photoId")
	@Mapping(target = "album.albumId", source = "dto.albumId")
	@Mapping(target = "image", ignore = true)
	@Mapping(target = "dateUpdate", ignore = true)
	@Mapping(target = "dateCreate", ignore = true)
	Photo photoRequestDTOTophoto(PhotoRequestDTO dto);
	
	List<Photo> listPhotoDTOToListPhoto(List<PhotoDTO> listCommentDTO);
	
	List<PhotoDTO> listPhotoToListPhotoDTO(List<Photo> listComment);

}

package br.com.projectblog.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.projectblog.dtos.PostDTO;
import br.com.projectblog.dtos.requests.PostRequestDTO;
import br.com.projectblog.models.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {
	
	@Mapping(target = "postId", source = "entity.postId")
	@Mapping(target = "userId", source = "entity.user.userId")
	@Mapping(target = "title", source = "entity.title")
	@Mapping(target = "description", source = "entity.description")
	@Mapping(target = "image", source = "entity.image")
	@Mapping(target = "dateUpdate", source = "entity.dateUpdate", dateFormat = "dd-MM-yyyy HH:mm:ss")
	@Mapping(target = "dateCreate", source = "entity.dateCreate", dateFormat = "dd-MM-yyyy HH:mm:ss")
	@Mapping(target = "commentsDTO", ignore = true)
	PostDTO postToPostDTO(Post entity);

	@Mapping(target = "postId", source = "dto.postId")
	@Mapping(target = "user.userId", source = "dto.userId")
	@Mapping(target = "title", source = "dto.title")
	@Mapping(target = "description", source = "dto.description")
	@Mapping(target = "image", source = "dto.image")
	@Mapping(target = "dateUpdate", source = "dto.dateUpdate", dateFormat = "dd-MM-yyyy HH:mm:ss")
	@Mapping(target = "dateCreate", source = "dto.dateCreate", dateFormat = "dd-MM-yyyy HH:mm:ss")
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "comments", ignore = true)
	Post postDTOToPost(PostDTO dto);
	
	@Mapping(target = "postId", source = "dto.postId")
	@Mapping(target = "userId", source = "dto.userId")
	@Mapping(target = "title", source = "dto.title")
	@Mapping(target = "description", source = "dto.description")
	@Mapping(target = "imageEncodeBase64", ignore = true)
	PostRequestDTO postDTOtoPostRequestDTO(PostDTO dto);
	
	@Mapping(target = "postId", source = "dto.postId")
	@Mapping(target = "userId", source = "dto.userId")
	@Mapping(target = "title", source = "dto.title")
	@Mapping(target = "description", source = "dto.description")
	@Mapping(target = "dateUpdate", ignore = true)
	@Mapping(target = "dateCreate", ignore = true)
	@Mapping(target = "commentsDTO", ignore = true)
	@Mapping(target = "image", ignore = true)
	PostDTO postRequestDTOtoPostDTO(PostRequestDTO dto);
	
	@Mapping(target = "postId", source = "dto.postId")
	@Mapping(target = "user.userId", source = "dto.userId")
	@Mapping(target = "title", source = "dto.title")
	@Mapping(target = "description", source = "dto.description")
	@Mapping(target = "dateUpdate", ignore = true)
	@Mapping(target = "dateCreate", ignore = true)
	@Mapping(target = "comments", ignore = true)
	@Mapping(target = "image", ignore = true)
	Post postRequestDTOToPost(PostRequestDTO dto);
	
	List<PostDTO> listPostToListPostDTO(List<Post> listPost);

}

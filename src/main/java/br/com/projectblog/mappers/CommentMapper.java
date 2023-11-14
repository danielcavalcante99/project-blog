package br.com.projectblog.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.projectblog.dtos.CommentDTO;
import br.com.projectblog.dtos.requests.CommentRequestDTO;
import br.com.projectblog.models.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
	
	@Mapping(target = "commentId", source = "dto.commentId")
	@Mapping(target = "observation", source = "dto.observation")
	@Mapping(target = "user.userId", source = "dto.userId")
	@Mapping(target = "post", ignore = true)
	@Mapping(target = "dateUpdate", ignore = true)
	@Mapping(target = "dateCreate", ignore = true)
	Comment commentDTOTocomment(CommentDTO dto);
	
	@Mapping(target = "commentId", source = "entity.commentId")
	@Mapping(target = "observation", source = "entity.observation")
	@Mapping(target = "userId", source = "entity.user.userId")
	CommentDTO commentTocommentDTO(Comment entity);
	
	@Mapping(target = "commentId", source = "dto.commentId")
	@Mapping(target = "observation", source = "dto.observation")
	@Mapping(target = "user.userId", source = "dto.userId")
	@Mapping(target = "post", ignore = true)
	@Mapping(target = "dateUpdate", ignore = true)
	@Mapping(target = "dateCreate", ignore = true)
	Comment commentRequestDTOToComment(CommentRequestDTO dto);
	
	List<Comment> listCommentDTOToListComment(List<CommentDTO> listCommentDTO);
	
	List<CommentDTO> listCommentToListCommentDTO(List<Comment> listComment);

}

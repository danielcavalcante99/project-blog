package br.com.projectblog.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import br.com.projectblog.dtos.CommentDTO;
import br.com.projectblog.dtos.PostDTO;
import br.com.projectblog.dtos.requests.CommentRequestDTO;
import br.com.projectblog.exceptions.ResourceNotFoundException;
import br.com.projectblog.mappers.CommentMapper;
import br.com.projectblog.mappers.PostMapper;
import br.com.projectblog.models.Comment;
import br.com.projectblog.repositories.CommentRepository;
import br.com.projectblog.validations.groups.OnCreate;
import br.com.projectblog.validations.groups.OnUpdate;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class CommentService {
	
	private CommentRepository repository;
	
	private PostService postService;
	
	private UserService userService;
	
	private PostMapper postMapper;
	
	private CommentMapper mapper;
	
	
	@Validated({ OnCreate.class })
	public CommentDTO insertCommentInPost(CommentRequestDTO dto) {
		log.info("Requisição para criação do comment: \n {}", dto);
		
		PostDTO postDTO = this.postService.findById(UUID.fromString(dto.getPostId())).orElseThrow(() -> 
			new ResourceNotFoundException(String.format("Post pelo identificador %s não encontrado para ser atualizado.", dto.getPostId())
		));
		
		this.userService.findById(UUID.fromString(dto.getUserId())).orElseThrow(() -> 
			new ResourceNotFoundException(String.format("Usuário pelo identificador %s não encontrado.", dto.getUserId())
		));
		
		Comment entity = this.mapper.commentRequestDTOToComment(dto);
		entity.setPost(this.postMapper.postDTOToPost(postDTO));
		entity.setDateCreate(LocalDateTime.now());
		entity.setDateUpdate(LocalDateTime.now());
		
		this.repository.save(entity);
		
		log.info("Comment criado com sucesso.");
		
		return this.mapper.commentTocommentDTO(entity);
	}
	
	
	@Validated({ OnUpdate.class })
	public CommentDTO updateCommentInPost(CommentRequestDTO dto) {
		log.info("Requisição para atualização do comment: \n {}", dto);
		
		Comment actualEntity = this.repository.findById(UUID.fromString(dto.getCommentId())).orElseThrow(() -> 
			new ResourceNotFoundException(String.format("Comment pelo identificador %s não encontrado para ser atualizado.", dto.getCommentId())
		));	
		
		PostDTO postDTO = this.postService.findById(UUID.fromString(dto.getPostId())).orElseThrow(() -> 
			new ResourceNotFoundException(String.format("Post pelo identificador %s não encontrado para ser atualizado.", dto.getPostId())
		));
		
		this.userService.findById(UUID.fromString(dto.getUserId())).orElseThrow(() -> 
			new ResourceNotFoundException(String.format("Usuário pelo identificador %s não encontrado.", dto.getUserId())
		));		
		
		Comment entity = this.mapper.commentRequestDTOToComment(dto);
		entity.setPost(postMapper.postDTOToPost(postDTO));
		entity.setDateUpdate(LocalDateTime.now());
		entity.setDateCreate(actualEntity.getDateCreate());
		
		this.repository.save(entity);
		
		log.info("Comment atualizado com sucesso.");
		
		return this.mapper.commentTocommentDTO(entity);
	}
	
	
	public void deleteById(@NotNull UUID id) {
		this.repository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(String.format("Comment pelo id %s não existe.", id)));
		
		this.repository.deleteById(id);
		log.info("Comment pelo id {} foi excluído com sucesso.", id);
	}

}

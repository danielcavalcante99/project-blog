package br.com.projectblog.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import br.com.projectblog.dtos.PostDTO;
import br.com.projectblog.dtos.UserDTO;
import br.com.projectblog.dtos.filters.FilterPostDTO;
import br.com.projectblog.dtos.requests.PostRequestDTO;
import br.com.projectblog.exceptions.ResourceNotFoundException;
import br.com.projectblog.mappers.CommentMapper;
import br.com.projectblog.mappers.PostMapper;
import br.com.projectblog.mappers.UserMapper;
import br.com.projectblog.models.Post;
import br.com.projectblog.repositories.PostRepository;
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
public class PostService {
	
	private final PostRepository repository;
	
	private final UserService userService;

	private final UserMapper userMapper;
	
	private final CommentMapper commentMapper;
	
	private final PostMapper mapper;
	
	
	public Optional<PostDTO> findById(@NotNull UUID id) {
		Optional<Post> optPost = this.repository.findById(id);
		PostDTO postDTO = this.mapper.postToPostDTO(optPost.orElse(null));	
		
		return Optional.ofNullable(postDTO);
	}
	
	
	public Page<PostDTO> findAllByFilter(FilterPostDTO filter, Pageable pageable) {
		List<Post> listPost = this.repository.findAllByFilter(filter, pageable).getContent();
		List<PostDTO> listPostDTO = new ArrayList<>();
		
		listPost.forEach(post -> {
			
			PostDTO postDTO =PostDTO.builder()
				.postId(post.getPostId())
				.userId(post.getUser().getUserId())
				.title(post.getTitle())
				.description(post.getDescription())
				.commentsDTO(this.commentMapper.listCommentToListCommentDTO(post.getComments()))
				.dateCreate(post.getDateCreate())
				.dateUpdate(post.getDateUpdate()).build();
			
			listPostDTO.add(postDTO);
		});

		return new PageImpl<>(listPostDTO, pageable, listPostDTO.stream().count());
	}
	
	
	public void deleteById(@NotNull UUID id) {
		this.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(String.format("Post pelo id %s não existe.", id)));
		
		this.repository.deleteById(id);
		log.info("Post pelo id {} foi excluído com sucesso.", id);
	}
	
	
	@Validated({ OnCreate.class })
	public PostDTO insert(@NotNull @Valid PostRequestDTO dto) {
		log.info("Requisição para criação do post: \n {}", dto);	
		PostDTO postDTO = this.mapper.postRequestDTOtoPostDTO(dto);
		
		UserDTO userDTO = this.userService.findById(postDTO.getUserId()).orElseThrow(() -> 
			new ResourceNotFoundException(String.format("Usuário pelo identificador %s não encontrado.", postDTO.getUserId())
		));
		
		postDTO.setDateUpdate(LocalDateTime.now());
		postDTO.setDateCreate(LocalDateTime.now());
		postDTO.setCommentsDTO(List.of());
		postDTO.setImage(Base64.getDecoder().decode(dto.getImageEncodeBase64()));
		
		Post entity = this.mapper.postDTOToPost(postDTO);
		entity.setUser(this.userMapper.userDTOtoUser(userDTO));
		
		entity = this.repository.save(entity);
		postDTO.setPostId(entity.getPostId());
		
		log.info("Post criado com sucesso.");

		return postDTO;
	}	
	
	
	@Validated({ OnUpdate.class })
	public PostDTO update(@NotNull @Valid PostRequestDTO dto) {
		log.info("Requisição para atualização do post: \n {}", dto);
		
		PostDTO postDTO = this.findById(UUID.fromString(dto.getPostId())).orElseThrow(() -> 
				new ResourceNotFoundException(String.format("Post pelo identificador %s não encontrado para ser atualizado.", dto.getPostId())
		));
		
		UserDTO userDTO = this.userService.findById(UUID.fromString(dto.getUserId())).orElseThrow(() -> 
			new ResourceNotFoundException(String.format("Usuário pelo identificador %s não encontrado.", dto.getUserId())
		));
		
		postDTO.setTitle(dto.getTitle());
		postDTO.setDescription(dto.getDescription());
		postDTO.setDateUpdate(LocalDateTime.now());
		
		Post entity = this.mapper.postDTOToPost(postDTO);
		entity.setUser(this.userMapper.userDTOtoUser(userDTO));
		
		this.repository.save(entity);
		
		log.info("Post atualizado com sucesso.");

		return postDTO;
	}

}

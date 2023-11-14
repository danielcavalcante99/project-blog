package br.com.projectblog.dtos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.hateoas.RepresentationModel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostDTO extends RepresentationModel<PostDTO> implements Serializable {

	private static final long serialVersionUID = -8603398090087591062L;

	private UUID postId;

	private UUID userId;
	
	private String title;

	private String description;
	
	private List<CommentDTO> commentsDTO;
	
	private byte[] image;  
	
	private LocalDateTime dateUpdate;
	
	private LocalDateTime dateCreate;

}

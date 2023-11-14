package br.com.projectblog.dtos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentDTO implements Serializable {

	private static final long serialVersionUID = -5713449264855604974L;
	
	private UUID commentId;
	
	private UUID userId;
	
	private String observation;
	
	private LocalDateTime dateUpdate;
	
	private LocalDateTime dateCreate;

}

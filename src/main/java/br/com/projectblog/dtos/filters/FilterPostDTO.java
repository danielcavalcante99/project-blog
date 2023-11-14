package br.com.projectblog.dtos.filters;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FilterPostDTO implements Serializable{

	private static final long serialVersionUID = -866392521178817403L;
	
	private UUID postId;

	private UUID userId;
	
	private String title;
	
	private String description;
	
	private LocalDateTime dateUpdateStart;
	
	private LocalDateTime dateUpdateEnd;

	private LocalDateTime dateCreateStart;
	
	private LocalDateTime dateCreateEnd;

}

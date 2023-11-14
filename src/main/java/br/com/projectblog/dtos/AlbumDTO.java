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
public class AlbumDTO extends RepresentationModel<AlbumDTO> implements Serializable {

	private static final long serialVersionUID = 7967375142993955824L;
	
	private UUID albumId;
	
	private UUID userId;
	
	private String name;
	
	private List<PhotoDTO> photosDTO;
	
	private LocalDateTime dateUpdate;
	
	private LocalDateTime dateCreate;	
	
}

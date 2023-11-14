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
public class PhotoDTO implements Serializable {
	
	private static final long serialVersionUID = -464924947567182360L;
	
	private UUID photoId;

	private UUID albumId;
	
	private byte[] image;
	
	private LocalDateTime dateUpdate;
	
	private LocalDateTime dateCreate;

}

package br.com.projectblog.dtos.filters;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FilterAlbumDTO implements Serializable {

	private static final long serialVersionUID = -9121944014665134334L;

	private String albumId;

	private String userId;
	
	private String name;

	private LocalDateTime dateUpdateStart;

	private LocalDateTime dateUpdateEnd;

	private LocalDateTime dateCreateStart;

	private LocalDateTime dateCreateEnd;

}

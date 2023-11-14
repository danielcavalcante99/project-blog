package br.com.projectblog.dtos.requests;

import java.io.Serializable;

import br.com.projectblog.validations.groups.OnCreate;
import br.com.projectblog.validations.groups.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PhotoRequestDTO implements Serializable {

	private static final long serialVersionUID = 7420803942790161610L;
	
	@Null(groups = OnCreate.class, message = "O campo 'photoId' não deve ser informado na inclusão.")
	@NotBlank(groups = OnUpdate.class, message = "O campo 'photoId' é obrigatório ser informado na atualização.")
	private String photoId;
	
	@NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "O campo 'albumId' é obrigatório.")
	private String albumId;
	
	@NotBlank(message = "O campo 'imageEncodeBase64' é obrigatório ser informado.")
	private String imageEncodeBase64;

}

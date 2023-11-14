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
public class PostRequestDTO implements Serializable{
	
	private static final long serialVersionUID = -5180473080715626713L;

	@Null(groups = OnCreate.class, message = "O campo 'postId' não deve ser informado na inclusão.")
	@NotBlank(groups = OnUpdate.class, message = "O campo 'postId' é obrigatório ser informado na atualização.")
	private String postId;

	@NotBlank(groups = { OnUpdate.class, OnCreate.class }, message = "O campo 'userId' é obrigatório ser informado na atualização e inclusão.")
	private String userId;
	
	@NotBlank(message = "O campo 'title' é obrigatório ser informado.")
	private String title;
	
	@NotBlank(message = "O campo 'description' é obrigatório ser informado.")
	private String description;
	
	@Null(groups = OnUpdate.class, message = "O campo 'imageEncodeBase64' não deve ser informado na atualização.")
	@NotBlank(groups = OnCreate.class, message = "O campo 'imageEncodeBase64' é obrigatório ser informado na inclusão.")
	private String imageEncodeBase64;

}

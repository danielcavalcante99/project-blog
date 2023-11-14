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
public class CommentRequestDTO implements Serializable{

	private static final long serialVersionUID = 1346226928441358809L;
	
	@Null(groups = OnCreate.class, message = "O campo 'commentId' não deve ser informado na inclusão.")
	@NotBlank(groups = OnUpdate.class, message = "O campo 'commentId' é obrigatório ser informado na atualização.")
	private String commentId;

	@NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "O campo 'postId' é obrigatório.")
	private String postId;
	
	@NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "O campo 'userId' é obrigatório.")
	private String userId;
	
	private String observation;

}

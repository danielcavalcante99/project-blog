package br.com.projectblog.dtos.requests;

import java.io.Serializable;

import br.com.projectblog.validations.groups.OnCreate;
import br.com.projectblog.validations.groups.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AlbumRequestDTO implements Serializable {

	private static final long serialVersionUID = 2093841348597887400L;
	
	@Null(groups = OnCreate.class, message = "O campo 'albumId' não deve ser informado na inclusão.")
	@NotBlank(groups = OnUpdate.class, message = "O campo 'albumId' é obrigatório ser informado na atualização.")
	private String albumId;
	
	@NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "O campo 'userId' é obrigatório ser informado na atualização e inclusão.")
	private String userId;
	
	@NotBlank(message = "O campo 'name' é obrigatório ser informado.")
	@Size(max = 40, message = "O campo 'name' pode conter no máximo 40 caracteres.")
	private String name;

}

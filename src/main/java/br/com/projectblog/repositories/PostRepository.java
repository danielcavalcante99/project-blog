package br.com.projectblog.repositories;


import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import br.com.projectblog.models.Post;
import br.com.projectblog.repositories.impl.PostRepositoryCustom;

public interface PostRepository extends CrudRepository<Post, UUID>, PostRepositoryCustom{

}

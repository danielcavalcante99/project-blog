package br.com.projectblog.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import br.com.projectblog.models.Comment;

public interface CommentRepository extends CrudRepository<Comment, UUID>{

}

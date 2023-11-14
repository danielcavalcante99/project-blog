package br.com.projectblog.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import br.com.projectblog.models.Photo;

public interface PhotoRepository extends CrudRepository<Photo, UUID>{

}

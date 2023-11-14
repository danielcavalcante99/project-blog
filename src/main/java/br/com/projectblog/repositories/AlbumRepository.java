package br.com.projectblog.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import br.com.projectblog.models.Album;
import br.com.projectblog.repositories.impl.AlbumRepositoryCustom;

public interface AlbumRepository extends CrudRepository<Album, UUID>, AlbumRepositoryCustom{

}

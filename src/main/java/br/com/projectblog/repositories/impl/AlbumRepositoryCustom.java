package br.com.projectblog.repositories.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.projectblog.dtos.filters.FilterAlbumDTO;
import br.com.projectblog.models.Album;

public interface AlbumRepositoryCustom {

	Page<Album> findAllByFilter(FilterAlbumDTO filter, Pageable pageable);
}

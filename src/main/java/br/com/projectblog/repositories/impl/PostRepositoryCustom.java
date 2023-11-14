package br.com.projectblog.repositories.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.projectblog.dtos.filters.FilterPostDTO;
import br.com.projectblog.models.Post;

public interface PostRepositoryCustom {

	Page<Post> findAllByFilter(FilterPostDTO filter, Pageable pageable);
}

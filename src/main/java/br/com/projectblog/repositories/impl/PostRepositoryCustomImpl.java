package br.com.projectblog.repositories.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.projectblog.dtos.filters.FilterPostDTO;
import br.com.projectblog.models.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Page<Post> findAllByFilter(FilterPostDTO filter, Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Post> query = cb.createQuery(Post.class);
		Root<Post> root = query.from(Post.class);

		query.select(root).where(this.getWhere(filter, root).stream().toArray(Predicate[]::new));
		List<Post> listPost = entityManager.createQuery(query).getResultList();
		
		return new PageImpl<>(listPost, pageable, listPost.stream().count());
	}

	private List<Predicate> getWhere(FilterPostDTO filter, Root<Post> root) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		
		List<Predicate> predicates = new ArrayList<Predicate>();

		if (filter == null) {
			return predicates;
		}

		if (filter.getUserId() != null) {
			predicates.add(cb.equal(root.get("user").get("userId"), UUID.fromString(filter.getUserId())));
		}
		
		if (filter.getPostId() != null) {
			predicates.add(cb.equal(root.get("postId"), UUID.fromString(filter.getPostId())));
		}

		if (filter.getTitle() != null) {
			predicates.add(cb.like(cb.upper(root.get("title")), filter.getTitle().toUpperCase().concat("%") ));
		}

		if (filter.getDescription() != null) {
			predicates.add(cb.like(cb.upper(root.get("description")), filter.getDescription().toUpperCase().concat("%") ));
		}
		
		if (filter.getDateUpdateStart() != null && filter.getDateUpdateEnd() == null) {
			predicates.add(cb.greaterThanOrEqualTo(root.get("dateUpdate"), filter.getDateUpdateStart()));
		}
		
		if (filter.getDateUpdateStart() == null && filter.getDateUpdateEnd() != null) {
			predicates.add(cb.lessThanOrEqualTo(root.get("dateUpdate"), filter.getDateUpdateEnd()));
		}
		
		if (filter.getDateUpdateStart() != null && filter.getDateUpdateEnd() != null) {
			predicates.add(cb.between(root.get("dateUpdate"), filter.getDateUpdateStart(), filter.getDateUpdateEnd()));
		}
		
		if (filter.getDateCreateStart() != null && filter.getDateCreateEnd() == null) {
			predicates.add(cb.greaterThanOrEqualTo(root.get("dateCreate"), filter.getDateCreateStart()));
		}
		
		if (filter.getDateCreateStart() == null && filter.getDateCreateEnd() != null) {
			predicates.add(cb.lessThanOrEqualTo(root.get("dateCreate"), filter.getDateCreateStart()));
		}
		
		if (filter.getDateCreateStart() != null && filter.getDateCreateEnd() != null) {
			predicates.add(cb.between(root.get("dateCreate"), filter.getDateCreateStart(), filter.getDateCreateEnd()));
		}

		return predicates;
	}

}

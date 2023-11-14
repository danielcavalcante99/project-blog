package br.com.projectblog.repositories.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.projectblog.dtos.filters.FilterAlbumDTO;
import br.com.projectblog.models.Album;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class AlbumRepositoryCustomImpl implements AlbumRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Page<Album> findAllByFilter(FilterAlbumDTO filter, Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Album> query = cb.createQuery(Album.class);
		Root<Album> root = query.from(Album.class);

		query.select(root).where(this.getWhere(filter, root).stream().toArray(Predicate[]::new));
		List<Album> listAlbum = entityManager.createQuery(query).getResultList();
		
		return new PageImpl<>(listAlbum, pageable, listAlbum.stream().count());
	}

	private List<Predicate> getWhere(FilterAlbumDTO filter, Root<Album> root) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		
		List<Predicate> predicates = new ArrayList<Predicate>();

		if (filter == null) {
			return predicates;
		}

		if (filter.getUserId() != null) {
			predicates.add(cb.equal(root.get("user").get("userId"), UUID.fromString(filter.getUserId())));
		}
		
		if (filter.getAlbumId() != null) {
			predicates.add(cb.equal(root.get("albumId"), UUID.fromString(filter.getAlbumId())));
		}
		
		if (filter.getName() != null) {
			predicates.add(cb.equal(root.get("name"), filter.getName()));
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

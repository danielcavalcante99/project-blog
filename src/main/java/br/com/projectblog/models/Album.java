package br.com.projectblog.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "albuns")
public class Album {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID albumId;
	
	@Column(nullable = false)
	private String name;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	@OneToMany(mappedBy = "album")
	private List<Photo> photos;
	
	@Column(nullable = false)
	private LocalDateTime dateUpdate;
	
	@Column(nullable = false)
	private LocalDateTime dateCreate;

}

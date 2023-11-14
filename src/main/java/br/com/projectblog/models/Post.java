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
@Table(name = "posts")
public class Post {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID postId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;
	
	@OneToMany(mappedBy = "post")
	private List<Comment> comments;
	
	@Column(nullable = false, columnDefinition = "TEXT")
	private byte[] image;  
	
	@Column(nullable = false)
	private LocalDateTime dateUpdate;
	
	@Column(nullable = false)
	private LocalDateTime dateCreate;

}
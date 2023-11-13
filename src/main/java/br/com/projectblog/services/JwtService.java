package br.com.projectblog.services;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import br.com.projectblog.dtos.AuthenticationTokenDTO;
import br.com.projectblog.dtos.requests.AuthenticationRequestDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${application.security.jwt.secret-key}")
	private String secretKey;
	@Value("${application.security.jwt.expiration}")
	private long jwtExpiration;
	@Value("${application.security.jwt.refresh-token.expiration}")
	private long refreshExpiration;

	
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	

	public AuthenticationTokenDTO generateToken(AuthenticationRequestDTO dto) {	
		String acesstoken = generateToken(new HashMap<>(), dto);
		String refreshToken = generateRefreshToken(dto);
		
		return AuthenticationTokenDTO.builder()
				.accessToken(acesstoken)
				.expiresInMl(this.jwtExpiration)
				.refreshToken(refreshToken)
				.refreshExpiresInMl(this.refreshExpiration)
				.build();
	}
	

	public String generateToken(Map<String, Object> extraClaims, AuthenticationRequestDTO dto) {
		return buildToken(extraClaims, dto, jwtExpiration);
	}

	
	public String generateRefreshToken(AuthenticationRequestDTO dto) {
		return buildToken(new HashMap<>(), dto, refreshExpiration);
	}

	
	private String buildToken(Map<String, Object> extraClaims, AuthenticationRequestDTO dto, long expiration) {
		return Jwts.builder()
				.setClaims(extraClaims)
				.setSubject(dto.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(this.getSignInKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = this.extractUsername(token);
		return (username.equals(userDetails.getUsername())) && !this.isTokenExpired(token);
	}

	
	public boolean isTokenExpired(String token) {
			return this.extractExpiration(token).before(new Date());
	}

	
	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(this.getSignInKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	
	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
}

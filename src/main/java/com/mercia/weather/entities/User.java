package com.mercia.weather.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
public class User {

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", email=" + email + ", password=" + password + ", role="
				+ role + ", createdAt=" + createdAt + "]";
	}
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	public User(String username,String email,String password,Role role,LocalDateTime createdAt)
	{
		this.username=username;
		this.email = email;
		this.password = password;
		this.role = role;
		this.createdAt = createdAt;
	}
	@NotBlank
	private String username;
	
	
	private  String email;
	
	private String password;
	
	private Role role;
	
	private LocalDateTime createdAt;

}

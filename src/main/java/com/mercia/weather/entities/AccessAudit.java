package com.mercia.weather.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="access_audit")
@Getter
@Setter
@NoArgsConstructor
public class AccessAudit {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private Long userId;
	
	private String userName;
	
	private String userEmail;
	
	private String endpoint;
	
	@Enumerated(EnumType.STRING)
	private Status status;
	
	private LocalDateTime timeStamp;
	
	public AccessAudit(Long userId,String userName,String userEmail,String endpoint,Status status,LocalDateTime timeStamp)
	{
		this.userId = userId;
		this.userName = userName;
		this.userEmail = userEmail;
		this.endpoint = endpoint;
		this.status = status;
		this.timeStamp = timeStamp;
	}
	
	
}

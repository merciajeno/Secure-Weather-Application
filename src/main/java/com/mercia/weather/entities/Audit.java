package com.mercia.weather.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="audits")
@Getter
@Setter
@NoArgsConstructor
public class Audit {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private Long cityId;
	
	private Long userId;
	
	private Action action;
	
	private String endpoint;
	
	private Status status;
	
	private LocalDateTime timestamp;
	
	
}

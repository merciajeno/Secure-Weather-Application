package com.mercia.weather.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
@Table(name = "change_audit")
@Getter
@Setter
@NoArgsConstructor
public class ChangeAudit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private ResourceType resourceType;

	private LocalDateTime updatedAt;

	@JdbcTypeCode(SqlTypes.JSON)
	private String oldValue;

	@JdbcTypeCode(SqlTypes.JSON)
	private String newValue;

	@Enumerated(EnumType.STRING)
	private Action action;

}

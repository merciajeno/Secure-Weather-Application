package com.mercia.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mercia.weather.entities.Audit;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long>{

}

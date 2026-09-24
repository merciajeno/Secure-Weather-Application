package com.mercia.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mercia.weather.entities.ChangeAudit;

@Repository
public interface ChangeAuditRepository extends JpaRepository<ChangeAudit, Long>{

}

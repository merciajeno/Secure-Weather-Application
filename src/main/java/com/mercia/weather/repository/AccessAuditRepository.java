package com.mercia.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mercia.weather.entities.AccessAudit;

@Repository
public interface AccessAuditRepository extends JpaRepository<AccessAudit, Long>{

}

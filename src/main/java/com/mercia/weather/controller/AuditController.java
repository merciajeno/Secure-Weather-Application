
package com.mercia.weather.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mercia.weather.entities.AccessAudit;
import com.mercia.weather.entities.ChangeAudit;
import com.mercia.weather.repository.AccessAuditRepository;
import com.mercia.weather.repository.ChangeAuditRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/audit")
@Tag(name="Audits of the application")
public class AuditController {

	private final ChangeAuditRepository changeAuditRepo;
	private final AccessAuditRepository accessAuditRepo;

	public AuditController(ChangeAuditRepository changeAuditRepo, AccessAuditRepository accessAuditRepo) {
		this.accessAuditRepo = accessAuditRepo;
		this.changeAuditRepo = changeAuditRepo;
	}

	@GetMapping("/access")
	public ResponseEntity<List<AccessAudit>> getAccessAudits() {
		return ResponseEntity.ok(accessAuditRepo.findAll());
	}

	@GetMapping("/changing")
	public ResponseEntity<List<ChangeAudit>> getChangeAudits() {
		return ResponseEntity.ok(changeAuditRepo.findAll());
	}
}

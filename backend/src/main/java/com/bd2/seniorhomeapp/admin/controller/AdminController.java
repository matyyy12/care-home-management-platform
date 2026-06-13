package com.bd2.seniorhomeapp.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bd2.seniorhomeapp.admin.dto.response.AdminBackupResponse;
import com.bd2.seniorhomeapp.admin.dto.response.AdminConfigResponse;
import com.bd2.seniorhomeapp.admin.dto.response.AdminHealthResponse;
import com.bd2.seniorhomeapp.admin.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/health")
    public ResponseEntity<AdminHealthResponse> getHealth() {
        return ResponseEntity.ok(adminService.getHealth());
    }

    @GetMapping("/config")
    public ResponseEntity<AdminConfigResponse> getConfig() {
        return ResponseEntity.ok(adminService.getConfig());
    }

    @PostMapping("/backup")
    @PreAuthorize("hasRole('ADMIN_TECH')")
    public ResponseEntity<AdminBackupResponse> createBackup() {
        return ResponseEntity.ok(adminService.createBackup());
    }
}

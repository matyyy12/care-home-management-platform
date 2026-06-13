package com.bd2.seniorhomeapp.admin.service;

import com.bd2.seniorhomeapp.admin.dto.response.AdminBackupResponse;
import com.bd2.seniorhomeapp.admin.dto.response.AdminConfigResponse;
import com.bd2.seniorhomeapp.admin.dto.response.AdminHealthResponse;

public interface AdminService {

    AdminHealthResponse getHealth();

    AdminConfigResponse getConfig();

    AdminBackupResponse createBackup();
}

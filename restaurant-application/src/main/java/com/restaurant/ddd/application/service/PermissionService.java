package com.restaurant.ddd.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.ddd.application.model.user.PermissionAction;
import com.restaurant.ddd.application.model.user.PermissionFunction;
import com.restaurant.ddd.application.model.user.PermissionModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.*;

/**
 * Service để đọc và xử lý permission.json
 */
@Service
@Slf4j
public class PermissionService {

    private static final String PERMISSION_FILE = "permission.json";
    private List<PermissionModel> permissions;
    private Map<String, List<String>> actionKeyToPathsMap; // Map action key -> list of paths

    @PostConstruct
    public void init() {
        loadPermissions();
        buildActionKeyToPathsMap();
    }

    /**
     * Load permissions từ file JSON
     */
    private void loadPermissions() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource(PERMISSION_FILE);
            InputStream inputStream = resource.getInputStream();
            permissions = objectMapper.readValue(inputStream, new TypeReference<List<PermissionModel>>() {});
            log.info("Loaded {} permission modules", permissions.size());
        } catch (Exception e) {
            log.error("Error loading permission.json", e);
            permissions = new ArrayList<>();
        }
    }

    /**
     * Build map từ action key -> list of paths (patchRequire)
     */
    private void buildActionKeyToPathsMap() {
        actionKeyToPathsMap = new HashMap<>();
        if (permissions == null) {
            return;
        }

        for (PermissionModel permission : permissions) {
            // Xử lý actions ở level permission
            if (permission.getActions() != null) {
                for (PermissionAction action : permission.getActions()) {
                    if (action.getKey() != null && action.getPatchRequire() != null) {
                        actionKeyToPathsMap.put(action.getKey(), action.getPatchRequire());
                    }
                }
            }

            // Xử lý actions trong functions
            if (permission.getFunctions() != null) {
                for (PermissionFunction function : permission.getFunctions()) {
                    if (function.getActions() != null) {
                        for (PermissionAction action : function.getActions()) {
                            if (action.getKey() != null && action.getPatchRequire() != null) {
                                actionKeyToPathsMap.put(action.getKey(), action.getPatchRequire());
                            }
                        }
                    }
                }
            }
        }

        log.info("Built action key to paths map with {} entries", actionKeyToPathsMap.size());
    }

    /**
     * Lấy danh sách permissions (toàn bộ cấu trúc)
     */
    public List<PermissionModel> getPermissions() {
        return permissions != null ? new ArrayList<>(permissions) : new ArrayList<>();
    }

    /**
     * Lấy danh sách paths (patchRequire) từ danh sách action keys
     * 
     * @param actionKeys Danh sách action keys của user
     * @return Danh sách paths mà user được phép truy cập
     */
    public List<String> getPathsFromActionKeys(List<String> actionKeys) {
        if (actionKeys == null || actionKeys.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> allPaths = new HashSet<>();
        for (String actionKey : actionKeys) {
            List<String> paths = actionKeyToPathsMap.get(actionKey);
            if (paths != null && !paths.isEmpty()) {
                allPaths.addAll(paths);
            }
        }

        return new ArrayList<>(allPaths);
    }

    /**
     * Kiểm tra xem user có quyền truy cập path không
     * 
     * @param actionKeys Danh sách action keys của user
     * @param requestPath Path của request (ví dụ: /api/management/users/get)
     * @return true nếu user có quyền, false nếu không
     */
    public boolean hasPermissionForPath(List<String> actionKeys, String requestPath) {
        if (actionKeys == null || actionKeys.isEmpty() || requestPath == null) {
            return false;
        }

        List<String> allowedPaths = getPathsFromActionKeys(actionKeys);
        
        // Kiểm tra exact match hoặc prefix match
        for (String allowedPath : allowedPaths) {
            if (requestPath.equals(allowedPath) || requestPath.startsWith(allowedPath + "/")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Reload permissions từ file (dùng khi cần refresh)
     */
    public void reload() {
        loadPermissions();
        buildActionKeyToPathsMap();
    }
}


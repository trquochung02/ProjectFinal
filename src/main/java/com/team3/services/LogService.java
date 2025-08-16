package com.team3.services;

import com.team3.entities.User;

public interface LogService {

    void logAction(String action, String entityType, Long entityId, String description);

}

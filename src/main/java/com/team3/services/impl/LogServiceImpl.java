package com.team3.services.impl;

import com.team3.entities.Log;
import com.team3.entities.User;
import com.team3.repositories.LogRepository;
import com.team3.services.LogService;
import com.team3.utils.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public void logAction(String action, String entityType, Long entityId, String description) {
        User currentUser = currentUserUtil.getCurrentUser();

        Log log = new Log();
        log.setAction(action);
        log.setUser(currentUser);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);
        log.setTimestamp(LocalDateTime.now());

        logRepository.save(log);
    }
}

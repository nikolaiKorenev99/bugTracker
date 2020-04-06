package tss.t5.controller.impl;

import org.apache.log4j.Logger;
import tss.t5.controller.SessionSecurity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contains security methods.
 */
public class SessionSecurityController implements SessionSecurity, Runnable {
    private Logger logger = Logger.getLogger(MainProjectController.class);
    private static ConcurrentHashMap<Integer, Integer> tokenStorage = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, LocalDateTime> timeMap = new ConcurrentHashMap<>();
    private static final int TIME_FOR_SESSION_IN_MINUTES = 10;
    private static final int TIME_INTERVAL_FOR_CHECKING_IN_MINUTES = 5;

    @Override
    public String putToStorageUserSessionKey(int id) {
        int key = generateUserSessionActiveKey();
        tokenStorage.put(key, id);
        timeMap.put(key, LocalDateTime.now());
        return String.valueOf(key);
    }

    @Override
    public void removeFromStorageUserSessionKey(String key) {
        int intKey = Integer.parseInt(key);
        tokenStorage.remove(intKey);
        timeMap.remove(intKey);
    }

    @Override
    public int getUserIdFromStorage(String key) {
        int intKey = Integer.parseInt(key);
        timeMap.put(intKey, LocalDateTime.now());
        return tokenStorage.get(intKey);
    }

    @Override
    public boolean checkKeyInStorage(String key) {
        int intKey = Integer.parseInt(key);
        timeMap.put(intKey, LocalDateTime.now());
        return tokenStorage.containsKey(intKey);
    }

    @Override
    public boolean checkValidSessionKey(String value) {
        if (value != null && (!checkKeyInStorage(value))) {
            logger.error("ATTACK !!!!!! ATTACK " + value);
            return false;
        } else if (value != null && checkKeyInStorage(value)) {
            logger.error("USER " + getUserIdFromStorage(value));
            return true;
        } else {
            return false;
        }

    }

    private int generateUserSessionActiveKey() {
        int random = 0;
        do {
            random = (int) (Math.random() * Integer.MAX_VALUE);
        } while (tokenStorage.containsKey(random));
        return random;
    }

    @Override
    public void run() {
        LocalDateTime lastTime = LocalDateTime.now();
        while (true) {
            if(lastTime.plusMinutes(TIME_INTERVAL_FOR_CHECKING_IN_MINUTES).isBefore(LocalDateTime.now())) {
                lastTime = LocalDateTime.now();
                for (Map.Entry<Integer, LocalDateTime> entry : timeMap.entrySet()) {
                    if (entry.getValue().plusMinutes(TIME_FOR_SESSION_IN_MINUTES).isBefore(LocalDateTime.now())) {
                        logger.info("auto log out " + entry.getKey());
                        removeFromStorageUserSessionKey(entry.getKey().toString());
                    }
                }
            }
        }
    }
}

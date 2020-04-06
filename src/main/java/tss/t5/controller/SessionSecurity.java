package tss.t5.controller;

/**
 * Describe security`s methods for session.
 */
public interface SessionSecurity {
    /**
     * Put to storage user id and generated session key.
     *
     * @param id User id.
     * @return Generated session key.
     */
    String putToStorageUserSessionKey(int id);

    /**
     * Remove session key from storage.
     *
     * @param key Session key.
     */
    void removeFromStorageUserSessionKey(String key);

    /**
     * Get user id from storage by this session key.
     *
     * @param key Session key.
     * @return User id.
     */
    int getUserIdFromStorage(String key);

    /**
     * Check if storage contains user id by this session key.
     *
     * @param key session key.
     * @return true if storage contains user id by this session key.
     * false - if not.
     */
    boolean checkKeyInStorage(String key);

    /**
     * Check valid session key.
     *
     * @param val session key.
     * @return true if session key is valid.
     * false if not.
     */
    boolean checkValidSessionKey(String val);
}

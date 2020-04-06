package tss.t5.controller;

/**
 * Describe security`s methods for mail.
 */
public interface MailSecurity {
    /**
     * Get code for mail.
     *
     * @param email email
     * @return code
     */
    Integer getCodeForVerify(String email);

    /**
     * Get new code for mail.
     *
     * @param email email
     * @return code
     */
    Integer getNewCodeForVerify(String email);

    /**
     * Remove email from storage.
     *
     * @param email email
     * @return true or false
     */
    boolean removeFromStorage(String email);
}

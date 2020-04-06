package tss.t5.service;

/**
 * Interface for notification.
 */
public interface MailNotification {
    /**
     * Send notification
     * @param address address
     * @param subject subject
     * @param text text
     * @return status, true or false
     */
    boolean sendNotification(String address, String subject, String text);
}

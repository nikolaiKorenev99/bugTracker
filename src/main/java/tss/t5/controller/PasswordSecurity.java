package tss.t5.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Interface describe methods for encrypt password.
 */
public interface PasswordSecurity {
    /**
     * Encrypt password.
     * @param password password
     * @param salt salt
     * @return encrypted password
     * @throws NoSuchAlgorithmException if algorithm is not correct
     * @throws InvalidKeySpecException if key is invalid
     */
    String getEncryptedPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException;

    /**
     * Get new salt.
     * @return salt
     * @throws NoSuchAlgorithmException if algorithm is not correct
     */
    String getNewSalt() throws NoSuchAlgorithmException;

    /**
     * Generate new password.
     * @return password.
     */
    String generateNewPassword();
}

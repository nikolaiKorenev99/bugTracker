package tss.t5.controller.impl;

import tss.t5.controller.MailSecurity;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains security method for work with email.
 */
public class MailSecurityController implements MailSecurity {
    private Map<String, Integer> mailStorage = new HashMap();

    @Override
    public Integer getCodeForVerify(String email) {
        if (mailStorage.containsKey(email)) {
            return mailStorage.get(email);
        }
        return null;
    }

    @Override
    public Integer getNewCodeForVerify(String email) {
        removeFromStorage(email);
        int key = generateKey();
        mailStorage.put(email, key);
        return key;
    }

    @Override
    public boolean removeFromStorage(String email) {
        if (mailStorage.containsKey(email)) {
            mailStorage.remove(email);
            return true;
        }
        return false;
    }

    private int generateKey() {
        int random = 0;
        do {
            random = (int) (Math.random() * Integer.MAX_VALUE / 10000) + 10000;
        } while (mailStorage.containsValue(random));
        return random;
    }
}

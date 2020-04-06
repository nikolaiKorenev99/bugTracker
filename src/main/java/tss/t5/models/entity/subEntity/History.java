package tss.t5.models.entity.subEntity;

import java.time.LocalDateTime;

/**
 * Class History.
 */
public class History {
    private String userName;
    private String filedName;
    private LocalDateTime changedDate;
    private String oldValue;
    private String newValue;

    public History(String userName, String filedName, LocalDateTime changedDate, String oldValue, String newValue) {
        this.userName = userName;
        this.filedName = filedName;
        this.changedDate = changedDate;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getUserName() {
        return userName;
    }

    public String getFiledName() {
        return filedName;
    }

    public LocalDateTime getChangedDate() {
        return changedDate;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }
}

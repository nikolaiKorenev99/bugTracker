package tss.t5.models.enums;

/**
 * Describe service methods for work with Priority, Severity and BugStatus.
 */
public class EnumService {
    /**
     * Get BugStatus enum from string value.
     *
     * @param stringValue Value
     * @return BugStatus or null if impossible to get it
     */
    public static BugStatus getBugStatusFromString(String stringValue) {
        for (BugStatus bugStatus : BugStatus.values()) {
            if (bugStatus.toString().equals(stringValue.toUpperCase().trim().replace(" ", "_"))) {
                return bugStatus;
            }
        }
        return null;
    }

    /**
     * Get Priority enum from string value.
     *
     * @param stringValue Value
     * @return Priority or null if impossible to get it
     */
    public static Priority getPriorityFromString(String stringValue) {
        for (Priority priority : Priority.values()) {
            if (priority.toString().equals(stringValue.toUpperCase().trim())) {
                return priority;
            }
        }
        return null;
    }

    /**
     * Get Severity enum from string value.
     *
     * @param stringValue Value
     * @return Severity or null if impossible to get it
     */
    public static Severity getSeverityFromString(String stringValue) {
        for (Severity severity : Severity.values()) {
            if (severity.toString().equals(stringValue.toUpperCase().trim())) {
                return severity;
            }
        }
        return null;
    }
}

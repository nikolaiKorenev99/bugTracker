package tss.t5.models.entity;

import tss.t5.models.enums.BugStatus;
import tss.t5.models.enums.Priority;
import tss.t5.models.enums.Severity;

import java.time.LocalDateTime;

/**
 * Class ticket.
 */
public class Ticket {
    private int id;
    private BugStatus bugStatus;
    private Severity severity;
    private Priority priority;
    private String name;
    private String summary;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String steptsToReproduce;
    private String expectedResult;
    private String actualResult;

    /**
     * Constructor
     *
     * @param id                Id
     * @param bugStatus         Bug status
     * @param severity          Severity
     * @param priority          Priority
     * @param name              Name
     * @param summary           Summary
     * @param createdDate       Data create
     * @param modifiedDate      Data modified
     * @param steptsToReproduce Steps to reproduce
     * @param expectedResult    Expected result
     * @param actualResult      Actual result
     */
    public Ticket(int id, BugStatus bugStatus, Severity severity,
                  Priority priority, String name, String summary,
                  LocalDateTime createdDate, LocalDateTime modifiedDate,
                  String steptsToReproduce, String expectedResult,
                  String actualResult) {
        this.id = id;
        this.bugStatus = bugStatus;
        this.severity = severity;
        this.priority = priority;
        this.name = name;
        this.summary = summary;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.steptsToReproduce = steptsToReproduce;
        this.expectedResult = expectedResult;
        this.actualResult = actualResult;
    }

    /**
     * Get id fro ticket.
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Get name for ticket.
     *
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Get severity for ticket.
     *
     * @return Severity
     */
    public Severity getSeverity() {
        return severity;
    }

    /**
     * Get priority for ticket.
     *
     * @return Priority
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Get bug status for ticket.
     *
     * @return BugStatus
     */
    public BugStatus getBugStatus() {
        return bugStatus;
    }

    /**
     * Get ticket summary
     *
     * @return Summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Get data create.
     *
     * @return Data create
     */
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * Get data modified.
     *
     * @return Data modified
     */
    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    /**
     * Get steps to reproduce.
     *
     * @return steps to reproduce
     */
    public String getSteptsToReproduce() {
        return steptsToReproduce;
    }

    /**
     * Get expected results.
     *
     * @return expected results
     */
    public String getExpectedResult() {
        return expectedResult;
    }

    /**
     * Get actual results.
     *
     * @return actual results
     */
    public String getActualResult() {
        return actualResult;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", bugStatus=" + bugStatus +
                ", severity=" + severity +
                ", priority=" + priority +
                ", name='" + name + '\'' +
                ", summary='" + summary + '\'' +
                ", createdDate=" + createdDate +
                ", modifiedDate=" + modifiedDate +
                ", steptsToReproduce='" + steptsToReproduce + '\'' +
                ", expectedResult='" + expectedResult + '\'' +
                ", actualResult='" + actualResult + '\'' +
                '}';
    }
}

package tss.t5.models.entity.subEntity;

import tss.t5.models.enums.BugStatus;

/**
 * Temp entity contains short information about ticket.
 */
public class SubTicket {
    private int id;
    private String name;
    private BugStatus bugStatus;
    private String summary;

    /**
     * Constructor.
     *
     * @param id        Id
     * @param name      Name
     * @param bugStatus Bug status
     * @param summary   Summary
     */
    public SubTicket(int id, String name, BugStatus bugStatus, String summary) {
        this.id = id;
        this.name = name;
        this.bugStatus = bugStatus;
        this.summary = summary;
    }

    /**
     * Get ticket id.
     *
     * @return ticket id
     */
    public int getId() {
        return id;
    }

    /**
     * Get ticket name.
     *
     * @return ticket name
     */
    public String getName() {
        return name;
    }

    /**
     * Get ticket status.
     *
     * @return Bug status
     */
    public BugStatus getBugStatus() {
        return bugStatus;
    }

    /**
     * Get ticket summary
     *
     * @return ticket summary
     */
    public String getSummary() {
        return summary;
    }

    @Override
    public String toString() {
        return "SubTicket{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", bugStatus=" + bugStatus +
                ", summary='" + summary + '\'' +
                '}';
    }
}

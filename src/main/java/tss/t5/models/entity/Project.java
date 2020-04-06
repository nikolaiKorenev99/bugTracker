package tss.t5.models.entity;

/**
 * Class project.
 */
public class Project {
    private int projectId;
    private String projectName;

    /**
     * Constructor.
     *
     * @param projectId   id for project
     * @param projectName name for project
     */
    public Project(int projectId, String projectName) {
        this.projectId = projectId;
        this.projectName = projectName;
    }

    /**
     * Get project id.
     *
     * @return Id
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * Get project name.
     *
     * @return Name
     */
    public String getProjectName() {
        return projectName;
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                '}';
    }
}

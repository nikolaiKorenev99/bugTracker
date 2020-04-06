package tss.t5.models.entity;

/**
 * Class comment.
 */
public class Comment {
    private int id;
    private String value;

    /**
     * Constructor.
     *
     * @param id    Id
     * @param value Value
     */
    public Comment(int id, String value) {
        this.id = id;
        this.value = value;
    }

    /**
     * Get id current ticket.
     *
     * @return Id
     */
    public int getId() {
        return id;
    }

    /**
     * Get comment value current ticket.
     *
     * @return Value comment
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }
}

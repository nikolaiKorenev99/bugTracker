package tss.t5.models.entity;

import java.util.Objects;

/**
 * Class User.
 */
public class User {
    private int id;
    private String userName;
    private String email;
    private String password;
    private String solt;
    /**
     * Constructor.
     *
     * @param userName Username
     * @param email    Email
     * @param password Password
     * @param solt sol
     */
    public User(String userName, String email, String password,String solt) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.solt = solt;
    }

    /**
     * Constructor.
     *
     * @param id       Id
     * @param userName Username
     * @param email    Email
     */
    public User(int id, String userName, String email) {
        this.id = id;
        this.userName = userName;
        this.email = email;
    }

    /**
     * Constructor
     *
     * @param id       Id
     * @param userName Name
     */
    public User(int id, String userName) {
        //null
        this.id = id;
        this.userName = userName;
    }

    /**
     * Get user id.
     *
     * @return User id
     */
    public int getId() {
        return id;
    }

    /**
     * Get username.
     *
     * @return User name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Get user email.
     *
     * @return User email
     */
    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                Objects.equals(userName, user.userName) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

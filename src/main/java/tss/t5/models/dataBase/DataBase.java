package tss.t5.models.dataBase;

import tss.t5.models.entity.Comment;
import tss.t5.models.entity.Project;
import tss.t5.models.entity.Ticket;
import tss.t5.models.entity.User;
import tss.t5.models.entity.subEntity.History;
import tss.t5.models.entity.subEntity.SubTicket;
import tss.t5.models.enums.BugStatus;
import tss.t5.models.enums.Priority;
import tss.t5.models.enums.Severity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Interface describe methods for working with database.
 */
public interface DataBase {
    /***
     * Get ticket by id.
     * @param id Id ticket
     * @param userId user id
     * @return Ticket
     */
    Ticket getTicket(int id, int userId);

    /**
     * Get list of SubTicket current project.
     *
     * @param ptojectId Id project
     * @param userId    user id
     * @return List of SubTicket
     */
    List<SubTicket> getSubTickets(int ptojectId, int userId);

    /**
     * Get list of SubTicket current project.
     *
     * @param name      Like name
     * @param summary  summary
     * @param projectId Id project
     * @param userId    user id
     * @return List of SubTicket
     */

    List<SubTicket> getSubTickets(String name, boolean summary, int projectId, int userId);

    /**
     * Get all users
     *
     * @return List of users
     */
    List<User> getAllusers();

    /**
     * Get author for ticket.
     *
     * @param ticketId Ticket id
     * @param userId   user id
     * @return Author
     */
    User getUserAutorForTicket(int ticketId, int userId);

    /**
     * Method for updating ticket.
     *
     * @param userId            user id
     * @param ticketId          Ticket id
     * @param name              Name
     * @param summary           Summary
     * @param steptsToReproduce Steps to reproduce
     * @param expectedResult    Expected result
     * @param actualResult      Actual result
     * @param status            Status
     * @param priority          Priority
     * @param severity          Severity
     * @param fixerList         List of fixer user
     * @return Return true if ticket is updated.
     * Return false if the ticket isn`t updated
     */
    boolean updateTicket(int userId, int ticketId, String name, String summary, String steptsToReproduce,
                         String expectedResult, String actualResult, BugStatus status,
                         Priority priority, Severity severity, List<String> fixerList);

    /**
     * Method for creating ticket.
     *
     * @param name              Name
     * @param summary           Summary
     * @param steptsToReproduce Steps to reproduce
     * @param expectedResult    Expected result
     * @param actualResult      Actual result
     * @param status            Status
     * @param priority          Priority
     * @param severity          Severity
     * @param fixerList         List of fixer user
     * @param authorId          Author Id
     * @param ptojectId         Project id
     * @return Return Map with key status.
     * If ticket is created - key true.
     * Map with key true consist id created ticket.
     * If ticket isn`t created - key false.
     * Map with key false consist NULL.
     */
    Map<Boolean, Integer> createTicket(String name, String summary, String steptsToReproduce,
                                       String expectedResult, String actualResult, BugStatus status,
                                       Priority priority, Severity severity, List<String> fixerList,
                                       int authorId, int ptojectId);

    /**
     * Get list of fixer users for current ticket.
     *
     * @param ticketId Ticket id
     * @param userId   user id
     * @return List of users
     */
    List<User> getUsersFixer(int ticketId, int userId);

    /**
     * Get list of users for current project.
     *
     * @param projectId Project id
     * @param userId    user id
     * @return List of Users
     */
    List<User> getUsersForProject(int projectId, int userId);

    /**
     * Get user by login and password.
     *
     * @param login    Login
     * @param password Password
     * @return User
     */
    User getUser(String login, String password);

    /**
     * Get user by id.
     *
     * @param id Id
     * @return User
     */
    User getUser(int id);

    /**
     * Get list of commentary for current ticket.
     *
     * @param ticketId Ticket id
     * @param userId   user id
     * @return Map with key LocalDateTime.It is a time created comment.
     * And Map consist as value inner Map.
     * Inner Map which as key has user name and as value comment.
     */

    Map<LocalDateTime, Map<String, Comment>> getCommentList(int ticketId, int userId);

    /**
     * Get comment by id.
     *
     * @param commentId Comment id
     * @return Map with key time of created comment and value this comment
     */
    Map<LocalDateTime, Comment> getComment(int commentId);

    /**
     * Add comment to ticket.
     *
     * @param userId   User id
     * @param ticketId Ticket id
     * @param comment  Comment value
     * @return Id created comment
     */
    int addComment(int userId, int ticketId, String comment);

    /**
     * Get project by project id.
     *
     * @param projectId Project id
     * @param userId user id
     * @return Project
     */

    Project getProject(int projectId, int userId);

    /**
     * Get all available projects for user.
     *
     * @param userId user id
     * @return list with projects
     */
    List<Project> getAllProjects(int userId);

    /**
     * Get project where user is a author
     *
     * @param userId user id
     * @return list with project
     */
    List<Project> getAllAuthorProjects(int userId);

    /**
     * Get history for ticket.
     *
     * @param ticketId ticket id
     * @param userId   user id
     * @return history list
     */
    List<History> getHistoryForTicket(int ticketId, int userId);

    /**
     * Check unique username.
     *
     * @param userName username
     * @return true or false
     */
    boolean checkNotExistUserName(String userName);

    /**
     * Check unique email.
     *
     * @param email email
     * @return true or false
     */
    boolean checkNotExistEmail(String email);

    /**
     * Create new user.
     *
     * @param userName userName
     * @param email    email
     * @param password password
     * @param salt     salt
     * @return user id or null
     */
    Integer createUser(String userName, String email, String password, String salt);

    /**
     * Get password by email.
     *
     * @param email email
     * @return password
     */
    String getPasswordByEmail(String email);

    /**
     * Check grands to update ticket.
     *
     * @param ticketId ticket id
     * @param userId   user id
     * @return true or false
     */
    boolean canUserUpdateTicket(int ticketId, int userId);

    /**
     * Check grands to create ticket.
     *
     * @param projectId project id
     * @param userId    user id
     * @return true or false
     */
    boolean canUseCreateTicket(int projectId, int userId);

    /**
     * Create new project.
     *
     * @param userId      user id
     * @param name        name
     * @param projectType private or not project
     * @param memberList  list members
     * @return id new project or null
     */
    Integer createProject(int userId, String name, int projectType, List<String> memberList);

    /**
     * Check unique project name.
     *
     * @param name name
     * @return unique or not, true or false
     */
    boolean checkNotExistProjectName(String name);

    /**
     * Get salt for user.
     *
     * @param username user name
     * @return salt
     */
    String getUserSalt(String username);

    /**
     * Update password to User by email.
     *
     * @param email    email
     * @param password password
     * @param salt     salt
     * @return update status
     */
    boolean updatePassword(String email, String password, String salt);
}

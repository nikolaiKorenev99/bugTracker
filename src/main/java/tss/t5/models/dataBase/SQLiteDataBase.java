package tss.t5.models.dataBase;

import org.apache.log4j.Logger;
import tss.t5.models.entity.Comment;
import tss.t5.models.entity.Project;
import tss.t5.models.entity.Ticket;
import tss.t5.models.entity.User;
import tss.t5.models.entity.subEntity.History;
import tss.t5.models.entity.subEntity.SubTicket;
import tss.t5.models.enums.BugStatus;
import tss.t5.models.enums.EnumService;
import tss.t5.models.enums.Priority;
import tss.t5.models.enums.Severity;
import tss.t5.service.Property;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Class describe work with Database SQLite.
 */
public class SQLiteDataBase implements DataBase {
    private Logger logger = Logger.getLogger(SQLiteDataBase.class);
    private String path = Property.getPath();
    private String dataBaseName = Property.getDataBaseName();
    private String url = path + dataBaseName;
    private Connection connection;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructor.
     *
     * @throws SQLException           SQLException
     * @throws ClassNotFoundException if data base does not exist
     */
    public SQLiteDataBase() throws SQLException, ClassNotFoundException {
        logger.info("Path to database " + url);
        connection = null;
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("JDBC:sqlite:" + url);
    }


    @Override
    public Ticket getTicket(int id, int userId) {
        logger.info(" getTicket(int id) id = " + id);
        Ticket ticket = null;
        String ticketQuery = "select b.bug_id, b.user_id, b.project_id, st.status_value,\n" +
                "sev.severity_value, p.priority_value, b.bug_name, b.bug_summary,\n" +
                "b.created_at, b.modified_at, b.steps_to_reproduce,\n" +
                "b.expected_result, b.actual_result\n" +
                "from Bug_report b, Bug_Report_Status st, Bug_Priority p, Bug_Severity sev \n" +
                "where b.bug_report_status_id = st.bug_report_status_id \n" +
                "and b.bug_priority_id = p.bug_priority_id \n" +
                "and b.bug_severity_id = sev.bug_severity_id \n" +
                "and b.bug_id = ?" +
                "and ((select is_private from project where project_id =(select project_id from bug_report where bug_id = ?))=0 " +
                "or ? in (select user_id from project_member where project_id = " +
                "(select project_id from bug_report where bug_id = ?)))";
        ResultSet resultSet = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(ticketQuery)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, id);
            preparedStatement.setInt(3, userId);
            preparedStatement.setInt(4, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                LocalDateTime created_at = LocalDateTime.parse(resultSet.getString(9).trim(), formatter);
                LocalDateTime modified_at = LocalDateTime.parse(resultSet.getString(10).trim(), formatter);

                ticket = new Ticket(resultSet.getInt(1),
                        EnumService.getBugStatusFromString(resultSet.getString(4)),
                        EnumService.getSeverityFromString(resultSet.getString(5)),
                        EnumService.getPriorityFromString(resultSet.getString(6)),
                        resultSet.getString(7), resultSet.getString(8), created_at, modified_at,
                        resultSet.getString(11), resultSet.getString(12),
                        resultSet.getString(13));
            }
        } catch (SQLException ex) {
            logger.error(ex);
        }
        if (ticket != null) {
            logger.info("ticket " + ticket.toString());
        } else {
            logger.info("ticket is null id " + id);
        }
        return ticket;
    }

    @Override
    public List<SubTicket> getSubTickets(int projectId, int userId) {
        String query = "Select bug_id, bug_name,status_value, bug_summary, created_at " +
                "from Bug_report r, Bug_Report_Status s,Project p " +
                "where r.bug_report_status_id = s.bug_report_status_id " +
                "and p.project_id = r.project_id and p.project_id =? " +
                "and (p.is_private = 0" +
                " or p.project_id in (" +
                " select project_id from project_member where user_id = ?" +
                " )) order by created_at desc;";
        return getSubticketsList(query, null, false, projectId, userId);
    }

    private List<SubTicket> getSubticketsList(String query, String name, boolean summary,
                                              int projectId, int userId) {
        logger.info("getSubTickets() " + query + "name " + name + "summary " + summary);
        List<SubTicket> tickets = new ArrayList<>();
        ResultSet resultSet = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, projectId);
            preparedStatement.setInt(2, userId);
            if (name != null) {
                preparedStatement.setString(3, "%" + replaceTag(name) + "%");
                if (summary) {
                    preparedStatement.setString(4, "%" + replaceTag(name) + "%");
                }

            }

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                tickets.add(new SubTicket(resultSet.getInt(1), resultSet.getString(2),
                        EnumService.getBugStatusFromString(resultSet.getString(3)),
                        resultSet.getString(4)));
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.info("getSubTickets size " + tickets.size());
        return tickets;

    }


    @Override
    public List<SubTicket> getSubTickets(String name, boolean summary, int projectId, int userId) {
        if (summary) {
            String query = "Select bug_id, bug_name,status_value, bug_summary, created_at " +
                    "from Bug_report r, Bug_Report_Status s,Project p " +
                    "where r.bug_report_status_id = s.bug_report_status_id " +
                    "and p.project_id = r.project_id and p.project_id =?" +
                    "and (p.is_private = 0" +
                    " or p.project_id in (" +
                    " select project_id from project_member where user_id = ?" +
                    " ))" +
                    "and (LOWER(r.bug_name)" +
                    "like LOWER (?) or LOWER(r.bug_summary) like LOWER (?)) order by created_at desc";
            return getSubticketsList(query, name, true, projectId, userId);
        } else {
            String query = "Select bug_id, bug_name,status_value, bug_summary, created_at " +
                    "from Bug_report r, Bug_Report_Status s,Project p " +
                    "where r.bug_report_status_id = s.bug_report_status_id " +
                    "and p.project_id = r.project_id and p.project_id =? " +
                    "and (p.is_private = 0" +
                    " or p.project_id in (" +
                    " select project_id from project_member where user_id = ?" +
                    " ))" +
                    "and LOWER(r.bug_name)" +
                    " like LOWER (?) order by created_at desc";
            return getSubticketsList(query, name, false, projectId, userId);
        }
    }

    @Override
    public User getUserAutorForTicket(int ticketId, int userId) {
        logger.info("getUserAutorForTicket() ticketId" + ticketId);
        String userQuery = "Select user_id, user_name from Users where user_id = (select user_id from Bug_Report" +
                " where bug_id = ?)" +
                "and ((select is_private from project where project_id =(select project_id from bug_report where bug_id = ?))=0 " +
                "or ? in (select user_id from project_member where project_id = " +
                "(select project_id from bug_report where bug_id = ?)))";
        ResultSet resultSet = null;
        User user = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(userQuery)) {
            preparedStatement.setInt(1, ticketId);
            preparedStatement.setInt(2, ticketId);
            preparedStatement.setInt(3, userId);
            preparedStatement.setInt(4, ticketId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User(resultSet.getInt(1), resultSet.getString(2));
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        if (user == null) {
            logger.error("getUserAutorForTicket user null ticketId " + ticketId);
        } else {
            logger.info("getUserAutorForTicket user  " + user.toString());
        }
        return user;
    }

    @Override
    public List<User> getAllusers() {
        logger.info("getAllusers()");
        String userQuery = "Select user_id,user_name from Users ";
        List<User> userList = new ArrayList<>();
        ResultSet resultSet = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(userQuery)) {
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userList.add(new User(resultSet.getInt(1), resultSet.getString(2)));
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.info("size " + userList.size());
        return userList;
    }

    @Override
    public List<User> getUsersFixer(int ticketId, int userId) {
        String fixerQuery = " Select user_id,user_name from Users where user_id" +
                " in (Select user_id from Fixer where bug_id = ?)" +
                "and ((select is_private from project where project_id =(select project_id from bug_report where bug_id = ?))=0 " +
                "or ? in (select user_id from project_member where project_id = " +
                "(select project_id from bug_report where bug_id = ?)))";
        return privateGetUsers(fixerQuery, ticketId, userId);
    }

    private List<User> privateGetUsers(String userQuery, int id, int userId) {
        logger.info("privateGetUsers() " + userQuery + "id " + id);
        List<User> userList = new ArrayList<>();
        ResultSet resultSet = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(userQuery)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, id);
            preparedStatement.setInt(3, userId);
            preparedStatement.setInt(4, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userList.add(new User(resultSet.getInt(1), resultSet.getString(2)));
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.info("size " + userList.size());
        return userList;
    }

    @Override
    public List<User> getUsersForProject(int projectId, int userId) {
        String userQuery = "Select user_id,user_name,email from Users where user_id in (select user_id from Project_Member" +
                " where project_id = ?)" +
                "and ((select is_private from project where project_id =?)=0 " +
                "or ? in (select user_id from project_member where project_id =?))";
        return privateGetUsers(userQuery, projectId, userId);
    }

    @Override
    public User getUser(String login, String password) {
        String userQuery = "Select user_id, user_name, email from Users where user_name = ? and password = ?;";
        return privateGetUser(userQuery, replaceTag(login), password, 0);
    }

    private User privateGetUser(String userQuery, String login, String password, int id) {
        logger.info("privateGetUser()" + login + " id " + id);
        ResultSet resultSet = null;
        User user = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(userQuery)) {
            if (login != null && password != null) {
                preparedStatement.setString(1, replaceTag(login));
                preparedStatement.setString(2, password);
            } else {
                preparedStatement.setInt(1, id);
            }
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3));
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        if (user != null) {
            logger.info(user.toString());
        } else {
            logger.info("user is null");
        }
        return user;
    }

    @Override
    public User getUser(int id) {
        String userQuery = "Select user_id, user_name, email from Users where user_id =?";
        return privateGetUser(userQuery, null, null, id);
    }

    @Override
    public Map<LocalDateTime, Map<String, Comment>> getCommentList(int ticketId, int userId) {
        logger.info("getCommentList() ticket id " + ticketId);
        Map<LocalDateTime, Map<String, Comment>> userComments = new TreeMap<>();
        String commentQuery = "Select created_at, u.user_id,user_name,comment_id,comment_value from Comments c, Users u " +
                "where c.user_id = u.user_id and bug_id =?" +
                "and ((select is_private from project where project_id =(select project_id from bug_report where bug_id = ?))=0 " +
                "or ? in (select user_id from project_member where project_id = " +
                "(select project_id from bug_report where bug_id = ?)))";
        ResultSet resultSet = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(commentQuery)) {
            preparedStatement.setInt(1, ticketId);
            preparedStatement.setInt(2, ticketId);
            preparedStatement.setInt(3, userId);
            preparedStatement.setInt(4, ticketId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                LocalDateTime created_at = LocalDateTime.parse(resultSet.getString(1).trim(), formatter);
                User user = new User(resultSet.getInt(2), resultSet.getString(3));
                Comment comment = new Comment(resultSet.getInt(4), resultSet.getString(5));
                if (userComments.containsKey(created_at)) {
                    userComments.get(created_at).put(user.getUserName(), comment);
                } else {
                    Map<String, Comment> map = new HashMap<>();
                    map.put(user.getUserName(), comment);
                    userComments.put(created_at, map);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.info("size for comment " + userComments.size());
        return userComments;
    }

    @Override
    public Map<LocalDateTime, Comment> getComment(int commentId) {
        logger.info("getComment() commentId " + commentId);
        Map<LocalDateTime, Comment> map = new HashMap<>();
        String commentQuery = "Select created_at, comment_id,comment_value from comments where comment_id = ?";
        ResultSet resultSet = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(commentQuery)) {
            preparedStatement.setInt(1, commentId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                LocalDateTime created_at = LocalDateTime.parse(resultSet.getString(1).trim(), formatter);
                Comment comment = new Comment(resultSet.getInt(2), resultSet.getString(3));
                map.put(created_at, comment);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.info("size for comment " + map.size());
        return map;
    }

    @Override
    public int addComment(int userId, int ticketId, String comment) {
        logger.info("addComment() userId " + userId + " ticketId " + ticketId + " comment " + comment);
        String insertQuery = "Insert into comments (user_id, comment_value, bug_id, created_at) " +
                "select ?,?,?,?  " +
                "where ((select is_private from project where project_id =(select project_id from bug_report where bug_id = ?))=0 " +
                "or ? in (select user_id from project_member where project_id = " +
                "(select project_id from bug_report where bug_id = ?)))";
        int key = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery,
                Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, replaceTag(comment));
            preparedStatement.setInt(3, ticketId);
            String currentTime = LocalDateTime.now().format(formatter).toString().replace("T", " ");
            preparedStatement.setString(4, currentTime);
            preparedStatement.setInt(5, ticketId);
            preparedStatement.setInt(6, userId);
            preparedStatement.setInt(7, ticketId);
            int row = preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    key = generatedKeys.getInt(1);
                    logger.info("Key " + key);
                } else {
                    throw new SQLException("Creating comment failed, no ID obtained.");
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            logger.error(e);
        }
        return key;
    }

    @Override
    public Project getProject(int projectId, int userId) {
        logger.info("getProject()" + projectId);
        String projectQuery = "Select project_id,project_name from project " +
                "where project_id=? or project_id in (" +
                " select project_id from project_member where user_id = ?" +
                " )";
        Project project = null;
        ResultSet resultSet = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(projectQuery)) {
            preparedStatement.setInt(1, projectId);
            preparedStatement.setInt(2, userId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                project = new Project(resultSet.getInt(1), resultSet.getString(2));
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        if (project != null) {
            logger.info(project.toString());
        } else {
            logger.info("project is null");
        }
        return project;

    }

    @Override
    public List<Project> getAllProjects(int userId) {
        logger.info("getAllProjects");
        String projectQuery = "Select project_id,project_name from project" +
                " where is_private = 0" +
                " or project_id in ( " +
                " select project_id from project_member where user_id = ?);";
        return getProjects(projectQuery, userId);
    }

    private List<Project> getProjects(String query, int userId) {
        List<Project> projectList = new ArrayList<>();
        ResultSet resultSet = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                projectList.add(new Project(resultSet.getInt(1), resultSet.getString(2)));
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.info("size " + projectList.size());
        return projectList;
    }

    @Override
    public List<Project> getAllAuthorProjects(int userId) {
        String projectQuery = "Select project_id,project_name from project" +
                " where user_id = ?";
        return getProjects(projectQuery, userId);
    }

    @Override
    public boolean updateTicket(int userId, int ticketId, String name, String summary, String steptsToReproduce,
                                String expectedResult, String actualResult, BugStatus status, Priority priority,
                                Severity severity, List<String> fixerList) {
        String updateQuery = "UPDATE bug_report  SET bug_name = ?, bug_summary = ?, modified_at = ?, " +
                "steps_to_reproduce = ?,expected_result =?,actual_result = ?," +
                "bug_report_status_id = (select bug_report_status_id from Bug_Report_Status where status_value = ?)," +
                "bug_priority_id = (select bug_priority_id from Bug_Priority where priority_value = ?)," +
                "bug_severity_id = (select bug_severity_id from Bug_Severity where severity_value = ?)" +
                " where bug_id =? " +
                "and ? in (select user_id from project_member where project_id = " +
                "(select project_id from bug_report where bug_id = ?))";
        ;

        String deleteFromTemp = "Delete from Temp_User_History;";
        String insertIntoTemp = "Insert into Temp_User_History values(?);";

        logger.info("updateTicket() " + "userId " + userId + " ticketId " + ticketId + " name " + name + " summary " + summary
                + " steptsToReproduce " + steptsToReproduce + " expectedResult " + expectedResult
                + " actualResult " + actualResult
                + "status " + status + "priority " + priority + "severity " + severity + "fixerList " + fixerList.size());

        StringBuilder stringBuilder = new StringBuilder(fixerList.size());
        stringBuilder.append("Insert into Fixer Select user_id,? from Users where user_name in (");

        for (int i = 0; i < fixerList.size(); i++) {
            if (i > 0) stringBuilder.append(",");
            stringBuilder.append("?");
        }
        stringBuilder.append(") and user_id in(Select user_id from Project_Member where project_id = " +
                "(Select project_id from Bug_Report where bug_id = ?));");

        String deleteFromFixer = "delete from Fixer where bug_id = ?;";

        try (PreparedStatement preparedStatementUpdate = connection.prepareStatement(updateQuery);
             PreparedStatement preparedStatementDelete = connection.prepareStatement(deleteFromFixer);
             PreparedStatement pst = connection.prepareStatement(stringBuilder.toString());
             PreparedStatement tempDel = connection.prepareStatement(deleteFromTemp);
             PreparedStatement tempInsert = connection.prepareStatement(insertIntoTemp)) {

            connection.setAutoCommit(false);

            tempDel.executeUpdate();

            tempInsert.setInt(1, userId);
            tempInsert.executeUpdate();

            preparedStatementUpdate.setString(1, replaceTag(name));
            preparedStatementUpdate.setString(2, replaceTag(summary));
            String currentTime = LocalDateTime.now().format(formatter).toString().replace("T", " ");
            preparedStatementUpdate.setString(3, currentTime);
            preparedStatementUpdate.setString(4, replaceTag(steptsToReproduce));
            preparedStatementUpdate.setString(5, replaceTag(expectedResult));
            preparedStatementUpdate.setString(6, replaceTag(actualResult));
            preparedStatementUpdate.setString(7, status.toString().replace("_", " "));
            preparedStatementUpdate.setString(8, priority.toString());
            preparedStatementUpdate.setString(9, severity.toString());
            preparedStatementUpdate.setInt(10, ticketId);
            preparedStatementUpdate.setInt(11, userId);
            preparedStatementUpdate.setInt(12, ticketId);
            int rowupdate = preparedStatementUpdate.executeUpdate();

            preparedStatementDelete.setInt(1, ticketId);
            preparedStatementDelete.execute();

            pst.setInt(1, ticketId);

            for (int i = 0; i < fixerList.size(); i++) {
                pst.setString(i + 2, replaceTag(fixerList.get(i)));
            }
            pst.setInt(fixerList.size() + 2, ticketId);
            int fixerUdatedCount = pst.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);

            if (rowupdate == 1 && fixerUdatedCount == fixerList.size()) {
                logger.info("updateTicket true");
                return true;
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.error("updateTicket false");
        return false;
    }

    @Override
    public Map<Boolean, Integer> createTicket(String name, String summary, String steptsToReproduce, String expectedResult,
                                              String actualResult, BugStatus status, Priority priority, Severity severity,
                                              List<String> fixerList, int authorId, int ptojectId) {
        logger.info("createTicket() " + " name " + name + " summary " + summary
                + " steptsToReproduce " + steptsToReproduce + " expectedResult " + expectedResult + " actualResult "
                + actualResult + "status " + status + "priority " + priority + "severity " + severity + "fixerList "
                + fixerList.size() + "authorId " + authorId + " projectid " + ptojectId);

        String insertQuery = "insert into Bug_Report(user_id, project_id,bug_report_status_id,bug_severity_id, " +
                "bug_priority_id,bug_name,bug_summary,created_at,modified_at,\n" +
                "steps_to_reproduce,expected_result,actual_result) \n" +
                "select ?,?,(select bug_report_status_id from Bug_Report_Status where status_value = ?),\n" +
                "(select bug_severity_id from Bug_Severity where severity_value = ?),\n" +
                "(select bug_priority_id from Bug_Priority where priority_value = ?),\n" +
                "?,?,?,?,?,?,? " +
                "where ? in (select user_id from project_member where project_id = ?)";

        int key = 0;
        Map<Boolean, Integer> map = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder(fixerList.size());
        stringBuilder.append("Insert into Fixer Select user_id,? from Users where user_name in (");

        for (int i = 0; i < fixerList.size(); i++) {
            if (i > 0) stringBuilder.append(",");
            stringBuilder.append("?");
        }
        stringBuilder.append(") and user_id in(Select user_id from Project_Member where project_id = " +
                "(Select project_id from Bug_Report where bug_id = ?));");

        String deleteFromFixer = "delete from Fixer where bug_id = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement preparedStatementDelete = connection.prepareStatement(deleteFromFixer);
             PreparedStatement pst = connection.prepareStatement(stringBuilder.toString())) {
            connection.setAutoCommit(false);
            preparedStatement.setInt(1, authorId);
            preparedStatement.setInt(2, ptojectId);
            preparedStatement.setString(3, status.toString().replace("_", " "));
            preparedStatement.setString(4, severity.toString());
            preparedStatement.setString(5, priority.toString());
            preparedStatement.setString(6, replaceTag(name));
            preparedStatement.setString(7, replaceTag(summary));
            String currentTime = LocalDateTime.now().format(formatter).toString().replace("T", " ");
            preparedStatement.setString(8, currentTime);
            preparedStatement.setString(9, currentTime);
            preparedStatement.setString(10, replaceTag(steptsToReproduce));
            preparedStatement.setString(11, replaceTag(expectedResult));
            preparedStatement.setString(12, replaceTag(actualResult));
            preparedStatement.setInt(13, authorId);
            preparedStatement.setInt(14, ptojectId);
            int rowUpdate = preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    key = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating ticket failed, no ID obtained.");
                }
            }

            preparedStatementDelete.setInt(1, key);
            preparedStatementDelete.execute();

            pst.setInt(1, key);

            for (int i = 0; i < fixerList.size(); i++) {
                pst.setString(i + 2, replaceTag(fixerList.get(i)));
            }
            pst.setInt(fixerList.size() + 2, key);
            int fixerSize = pst.executeUpdate();
            connection.commit();

            if (rowUpdate == 1 && fixerList.size() == fixerSize) {
                logger.info(" map.put(true, key);");
                map.put(true, key);
                return map;
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.info("map.put(false,0);");
        map.put(false, null);
        return map;
    }

    @Override
    public List<History> getHistoryForTicket(int ticketId, int userId) {
        logger.info("getHistoryForTicket() ticketId " + ticketId);
        String historyQuery = "Select user_name, field_name, modified_at, old_value, new_value" +
                " from history h, Users u where h.user_id = u.user_id and h.bug_id = ? " +
                "and ((select is_private from project where project_id =(select project_id from bug_report where bug_id = ?))=0 " +
                "or ? in (select user_id from project_member where project_id = " +
                "(select project_id from bug_report where bug_id = ?))) order by 3 desc;";
        List<History> historyList = new ArrayList<>();
        ResultSet resultSet = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(historyQuery)) {
            preparedStatement.setInt(1, ticketId);
            preparedStatement.setInt(2, ticketId);
            preparedStatement.setInt(3, userId);
            preparedStatement.setInt(4, ticketId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                LocalDateTime modified_at = LocalDateTime.parse(resultSet.getString(3).trim(), formatter);
                historyList.add(new History(resultSet.getString(1), resultSet.getString(2),
                        modified_at, resultSet.getString(4), resultSet.getString(5)));
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.info("historyList size " + historyList.size());
        return historyList;
    }

    @Override
    public boolean checkNotExistUserName(String userName) {
        String checkUserName = " select 1 from Users where user_name = ?";
        return checkNotExist(checkUserName, replaceTag(userName));
    }

    @Override
    public boolean checkNotExistEmail(String email) {
        String checkEmail = " select 1 from Users where user_name = ?";
        return checkNotExist(checkEmail, replaceTag(email));
    }

    @Override
    public Integer createUser(String userName, String email, String password, String salt) {
        String insertQuery = "insert into Users values(null,?,?,?,?)";
        int key;
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, replaceTag(userName));
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, replaceTag(email));
            preparedStatement.setString(4, salt);
            int rowUpdate = preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    key = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed with id");
                }
            }

            if (rowUpdate == 1) {
                return key;
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }

    @Override
    public String getPasswordByEmail(String email) {
        String selectPassword = "select password from Users where email =?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectPassword)) {
            preparedStatement.setString(1, replaceTag(email));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        logger.error("getPasswordByEmail null " + email);
        return null;
    }

    @Override
    public boolean canUserUpdateTicket(int ticketId, int userId) {
        String checkSelectQuery = "select 1 " +
                " where ? in (select user_id from project_member where project_id = " +
                "(select project_id from bug_report where bug_id = ?))";

        return canUpdateCreate(checkSelectQuery, ticketId, userId);
    }

    @Override
    public boolean canUseCreateTicket(int projectId, int userId) {
        String checkSelectQuery = "select 1 " +
                " where ? in (select user_id from project_member where project_id =?)";
        return canUpdateCreate(checkSelectQuery, projectId, userId);

    }

    @Override
    public Integer createProject(int userId, String name, int projectType, List<String> memberList) {
        String createProject = "insert into Project select null, ?,?,?; ";
        String sqlAuthor = "Insert into Project_Member values(?,?,1)";
        int key = 0;
        StringBuilder stringBuilder = new StringBuilder(memberList.size());
        stringBuilder.append("Insert into Project_Member Select user_id,?,1 from Users where user_name in (");

        for (int i = 0; i < memberList.size(); i++) {
            if (i > 0) stringBuilder.append(",");
            stringBuilder.append("?");
        }
        stringBuilder.append(")");
        try (PreparedStatement preparedStatement = connection.prepareStatement(createProject,
                Statement.RETURN_GENERATED_KEYS);
             PreparedStatement pst = connection.prepareStatement(stringBuilder.toString())) {
            connection.setAutoCommit(false);
            preparedStatement.setString(1, replaceTag(name));
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, projectType);
            int row = preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    key = generatedKeys.getInt(1);
                    logger.info("Key " + key);
                } else {
                    throw new SQLException("Creating comment failed, no ID obtained.");
                }
            }
            pst.setInt(1, key);
            for (int i = 0; i < memberList.size(); i++) {
                pst.setString(i + 2, replaceTag(memberList.get(i)));
            }
            int memberCount = pst.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);
            logger.info("row " + row + " memberCount " + memberCount);
        } catch (SQLException e) {
            logger.error(e);
        }

        try (PreparedStatement authorInsert = connection.prepareStatement(sqlAuthor)) {
            authorInsert.setInt(1, userId);
            authorInsert.setInt(2, key);
            authorInsert.executeUpdate();
        } catch (SQLException e) {
            logger.info("userd added early ");
        }
        logger.error("create project false");
        if (key != 0) {
            return key;
        }
        return null;
    }

    @Override
    public boolean checkNotExistProjectName(String name) {
        String sheckProjectName = "select 1 from Project where project_name = ?";
        return checkNotExist(sheckProjectName, replaceTag(name));
    }

    @Override
    public String getUserSalt(String username) {
        //   String query = "Select salt from users where user_name = '" + replaceTag(username)+"'";
        String query = "Select salt from users where user_name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, replaceTag(username));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }

    @Override
    public boolean updatePassword(String email, String password, String salt) {
        String updatePasSalt = "update users " +
                "set password = ?, salt = ? " +
                "where email = ?";
        try (PreparedStatement preparedStatementUpdate = connection.prepareStatement(updatePasSalt)) {
            preparedStatementUpdate.setString(1, password);
            preparedStatementUpdate.setString(2, salt);
            preparedStatementUpdate.setString(3, replaceTag(email));
            int rowupdate = preparedStatementUpdate.executeUpdate();
            if (rowupdate == 1) {
                logger.info("updatePassword for mail " + email + " password updated in db");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        logger.error("updatePassword false mail " + email);
        return false;
    }

    private boolean canUpdateCreate(String query, int value, int userId) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, value);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return false;
    }

    private boolean checkNotExist(String query, String value) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, replaceTag(value));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return false;
    }

    private String replaceTag(String s) {
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
                if (c == '&' && s.charAt(i + 1) == '#') {
                    out.append(c);
                } else {
                    out.append("&#");
                    out.append((int) c);
                    out.append(';');
                }
            } else {
                out.append(c);
            }
        }
        return out.toString();
        //  return escapeHtml4(s);
        //return s;
    }
}

package tss.t5.controller.impl;

import io.javalin.Javalin;
import org.apache.log4j.Logger;
import tss.t5.controller.BaseController;
import tss.t5.controller.SessionSecurity;
import tss.t5.models.dataBase.DataBase;
import tss.t5.models.entity.Project;
import tss.t5.models.entity.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class project.
 */
public class ProjectController implements BaseController {
    private Logger logger = Logger.getLogger(TicketController.class);
    private Javalin app;
    private DataBase dataBase;
    private SessionSecurity sessionSecurity;

    /**
     * Constructor.
     *
     * @param app             Javalin app
     * @param dataBase        implementation interface Database
     * @param sessionSecurity implementation interface Security
     */
    public ProjectController(Javalin app, DataBase dataBase, SessionSecurity sessionSecurity) {
        this.app = app;
        this.dataBase = dataBase;
        this.sessionSecurity = sessionSecurity;
    }

    @Override
    public void execute() {
        app.post("/project/allProjects", context -> {
            logger.info("post /project/allProjects");
            int userId = 0;
            String userSessionActiveKey = context.sessionAttribute("userSessionActiveKey");
            if (sessionSecurity.checkValidSessionKey(userSessionActiveKey)) {
                logger.info("user user " + userSessionActiveKey);
                userId = sessionSecurity.getUserIdFromStorage(userSessionActiveKey);

            }
            List<Project> projectList = dataBase.getAllProjects(userId);
            if (projectList != null && projectList.size() > 0) {
                logger.info("projectList  size " + projectList.size());
                context.json(projectList);
            } else {
                context.result("false");
            }
        });

        app.post("/project/authorProject", context -> {
            logger.info("post /project/authorProject");
            String userSessionActiveKey = context.sessionAttribute("userSessionActiveKey");
            if (sessionSecurity.checkValidSessionKey(userSessionActiveKey)) {
                int userId = sessionSecurity.getUserIdFromStorage(userSessionActiveKey);
                List<Project> projectList = dataBase.getAllAuthorProjects(userId);
                if (projectList != null && projectList.size() > 0) {
                    logger.info("projectList  size " + projectList.size());
                    context.json(projectList);
                } else {
                    context.result("false");
                }
            } else {
                context.result("false");
            }
        });
        app.post("/project/project", context -> {
            String value = context.formParam("id");
            if (value == null) {
                value = context.queryParam("id");
            }
            int userId = 0;
            String userSessionActiveKey = context.sessionAttribute("userSessionActiveKey");
            if (sessionSecurity.checkValidSessionKey(userSessionActiveKey)) {
                logger.info("user user " + userSessionActiveKey);
                userId = sessionSecurity.getUserIdFromStorage(userSessionActiveKey);

            }
            logger.info("post /project/project value " + value + " userId  " + userId);
            int projectId = Integer.parseInt(value);
            Project project = dataBase.getProject(projectId, userId);
            if (project != null) {
                logger.info("project != null " + project.toString());
                context.json(project);
            } else {
                context.result("false");
            }
        });
        app.post("/project/create", context -> {
            logger.info("post  /project/create");
            String userSessionActiveKey = context.sessionAttribute("userSessionActiveKey");
            if (sessionSecurity.checkValidSessionKey(userSessionActiveKey)) {
                int userId = sessionSecurity.getUserIdFromStorage(userSessionActiveKey);
                String name = context.formParam("name");
                if (name == null) {
                    name = context.queryParam("name");
                }
                String members = context.formParam("members");
                if (members == null) {
                    members = context.queryParam("members");
                }
                String projectValue = context.formParam("type");
                if (projectValue == null) {
                    projectValue = context.queryParam("type");
                }
                int projectType = 0;
                boolean booleanType = Boolean.valueOf(projectValue);
                if (booleanType) {
                    projectType = 1;
                }
                logger.info("/project/create name  " + name + " members " + members);
                List<String> membersList = getNamesFromJson(members);
                if (trimString(name) && membersList.size() > 0) {
                    logger.info("trimStrings true");
                    Integer key = dataBase.createProject(userId, name, projectType, membersList);
                    if (key != null) {
                        context.result(String.valueOf(key));
                    } else {
                        context.result("false");
                    }
                }
            } else {
                context.result("false");
            }
        });
        app.post("/project/checkName", context -> {
            String userSessionActiveKey = context.sessionAttribute("userSessionActiveKey");
            if (sessionSecurity.checkValidSessionKey(userSessionActiveKey)) {
                String name = context.formParam("name");
                if (name == null) {
                    name = context.queryParam("name");
                }
                boolean result = dataBase.checkNotExistProjectName(name);
                logger.info(" checkName name " + name + " result " + result);
                context.result(String.valueOf(result));
            } else {
                context.result("false");
            }
        });

        app.post("/project/memberList", context -> {
            String userSessionActiveKey = context.sessionAttribute("userSessionActiveKey");
            if (sessionSecurity.checkValidSessionKey(userSessionActiveKey)) {
                int userId = sessionSecurity.getUserIdFromStorage(userSessionActiveKey);
                String value = context.formParam("id");
                if (value == null) {
                    value = context.queryParam("id");
                }
                logger.info("/project/memberList id = " + value + " userId " + userId);
                int id = Integer.parseInt(value);
                List<User> userList = dataBase.getUsersForProject(id, userId);
                List<String> userNames = new ArrayList<>();
                userList.forEach(x -> userNames.add(x.getUserName()));
                if (userNames.size() > 0) {
                    logger.info("userNames  size " + userNames.size());
                    context.json(userNames);
                } else {
                    logger.info("user list empty. Project_id " + id);
                    context.result("false");
                }

            } else {
                context.result("false");
            }
        });
    }

    private boolean trimString(String value) {
        if (value.trim() != null) {
            return true;
        }
        return false;
    }

    private List<String> getNamesFromJson(String str) {
        String newStr = str.replace("[", " ");
        newStr = newStr.replace("]", " ");
        newStr = newStr.replace("\"", "");
        newStr = newStr.trim();
        return Arrays.asList(newStr.split("\\s*,\\s*"));
    }
}

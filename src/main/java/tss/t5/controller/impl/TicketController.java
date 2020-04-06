package tss.t5.controller.impl;

import io.javalin.Javalin;
import org.apache.log4j.Logger;
import tss.t5.controller.BaseController;
import tss.t5.controller.SessionSecurity;
import tss.t5.models.dataBase.DataBase;
import tss.t5.models.entity.Comment;
import tss.t5.models.entity.Ticket;
import tss.t5.models.entity.User;
import tss.t5.models.entity.subEntity.History;
import tss.t5.models.enums.BugStatus;
import tss.t5.models.enums.EnumService;
import tss.t5.models.enums.Priority;
import tss.t5.models.enums.Severity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Contains methods to work with ticket page.
 */
public class TicketController implements BaseController {
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
    public TicketController(Javalin app, DataBase dataBase, SessionSecurity sessionSecurity) {
        this.app = app;
        this.dataBase = dataBase;
        this.sessionSecurity = sessionSecurity;
    }

    @Override
    public void execute() {
        app.post("/ticket/ticket", context -> {
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
            logger.info("post /ticket/ticket id = " + value + " userId " + userId);
            int id = Integer.parseInt(value);
            Ticket ticket = dataBase.getTicket(id, userId);
            if (ticket != null) {
                logger.info(ticket.toString());
                context.json(ticket);
            } else {
                context.result("false");
            }
        });

        app.post("/ticket/comments", context -> {
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
            logger.info("post /ticket/comments id " + value + " userId" + userId);
            int id = Integer.parseInt(value);
            Map<LocalDateTime, Map<String, Comment>> comments = dataBase.getCommentList(id, userId);
            if (comments != null && comments.size() > 0) {
                logger.info("comments  size " + comments.size());
                context.json(comments);
            } else {
                context.result("false");
            }
        });

        app.post("/ticket/autor", context -> {
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
            logger.info("post /ticket/autor id = " + value + " userId " + userId);
            int id = Integer.parseInt(value);
            User user = dataBase.getUserAutorForTicket(id, userId);
            if (user != null) {
                logger.info("autor = " + user.getUserName());
                context.json(user);
            } else {
                logger.info("autor is null for id " + id);
                context.result("false");
            }
        });

        app.post("/ticket/fixer", context -> {
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
            logger.info("post /ticket/fixer id = " + value + " userId " + userId);
            int id = Integer.parseInt(value);
            List<User> fixerList = dataBase.getUsersFixer(id, userId);
            List<String> fixerNames = new ArrayList<>();
            fixerList.forEach(x -> fixerNames.add(x.getUserName()));
            if (fixerNames.size() > 0) {
                logger.info("fixerNames  size " + fixerNames.size());
                context.json(fixerNames);
            } else {
                logger.info("fixer is not exist" + id);
                context.result("false");
            }
        });
        app.post("/ticket/userList", context -> {
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
            logger.info("post /ticket/userList id = " + value + " userId " + userId);
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
        });

        app.post("/ticket/comments/add", context -> {
            String userSessionActiveKey = context.sessionAttribute("userSessionActiveKey");
            if (sessionSecurity.checkValidSessionKey(userSessionActiveKey)) {
                int id = sessionSecurity.getUserIdFromStorage(userSessionActiveKey);
                String comment = context.formParam("comment");
                String ticketIdStringValue = context.formParam("ticketId");
                if (comment == null) {
                    comment = context.queryParam("comment");
                }
                if (ticketIdStringValue == null) {
                    ticketIdStringValue = context.queryParam("ticketId");
                }
                int ticketId = Integer.parseInt(ticketIdStringValue);
                logger.info("id " + id + "comment " + comment + "ticketId " + ticketId);
                if (comment.trim() != null) {
                    int key = dataBase.addComment(id, ticketId, comment);
                    Map<LocalDateTime, Comment> commentMap = dataBase.getComment(key);
                    if (commentMap.size() > 0) {
                        context.json(commentMap);
                    } else {
                        logger.error("id " + id + " Comment get error by key " + key);
                        context.result("false");
                    }
                }
            } else {
                context.result("false");
            }
        });

        app.post("/ticket/update", context -> {
            String userSessionActiveKey = context.sessionAttribute("userSessionActiveKey");
            if (sessionSecurity.checkValidSessionKey(userSessionActiveKey)) {
                int userId = sessionSecurity.getUserIdFromStorage(userSessionActiveKey);
                String value = context.formParam("id");
                if (value == null) {
                    value = context.queryParam("id");
                }
                int ticketId = Integer.parseInt(value);

                String name = context.formParam("name");
                if (name == null) {
                    name = context.queryParam("name");
                }
                String summary = context.formParam("summary");
                if (summary == null) {
                    summary = context.queryParam("summary");
                }
                String steptsToReproduce = context.formParam("steptsToReproduce");
                if (steptsToReproduce == null) {
                    steptsToReproduce = context.queryParam("steptsToReproduce");
                }
                String expectedResult = context.formParam("expectedResult");
                if (expectedResult == null) {
                    expectedResult = context.queryParam("expectedResult");
                }
                String actualResult = context.formParam("actualResult");
                if (actualResult == null) {
                    actualResult = context.queryParam("actualResult");
                }
                String status = context.formParam("status");
                if (status == null) {
                    status = context.queryParam("status");
                }
                String priority = context.formParam("priority");
                if (priority == null) {
                    priority = context.queryParam("priority");
                }
                String severity = context.formParam("severity");
                if (severity == null) {
                    severity = context.queryParam("severity");
                }
                String fixers = context.formParam("fixers");
                if (fixers == null) {
                    fixers = context.queryParam("fixers");
                }
                logger.info("ticket/update userId " + userId + " id " + value +
                        " name  " + name + " summary " + summary + " steptsToReproduce  " + steptsToReproduce
                        + " expectedResult " + expectedResult + " actualResult " + actualResult + " status " + status
                        + " priority " + priority + "severity " + severity + " fixers " + fixers);
                List<String> fixerList = getNamesFromJson(fixers);
                if (trimString(name) && trimString(summary) && trimString(status) && trimString(priority) && trimString(severity) && fixerList.size() > 0) {
                    logger.info("trimStrings true");
                    BugStatus statusEnum = EnumService.getBugStatusFromString(status);
                    Priority priorityEnum = EnumService.getPriorityFromString(priority);
                    Severity severityEnum = EnumService.getSeverityFromString(severity);
                    if (trimString(statusEnum.toString()) && trimString(priorityEnum.toString()) && trimString(severityEnum.toString())) {
                        logger.info("enum true");
                        if (dataBase.updateTicket(userId, ticketId, name, summary, steptsToReproduce, expectedResult, actualResult, statusEnum, priorityEnum, severityEnum, fixerList)) {
                            logger.info("dataBase.updateTicket " + true);
                            context.result("true");
                        } else {
                            logger.info("dataBase.updateTicket " + false);
                            context.result("false");
                        }
                    }
                }
            } else {
                context.result("false");
            }
        });

        app.post("/ticket/history", context -> {
            String userSessionActiveKey = context.sessionAttribute("userSessionActiveKey");
            if (sessionSecurity.checkValidSessionKey(userSessionActiveKey)) {
                int userId = sessionSecurity.getUserIdFromStorage(userSessionActiveKey);
                String ticketIdStringValue = context.formParam("ticketId");
                if (ticketIdStringValue == null) {
                    ticketIdStringValue = context.queryParam("ticketId");
                }
                int ticketId = Integer.parseInt(ticketIdStringValue);
                logger.info("ticket/history ticketId  " + ticketId);

                List<History> historyList = dataBase.getHistoryForTicket(ticketId, userId);
                if (historyList != null && historyList.size() > 0) {
                    context.json(historyList);
                } else {
                    context.result("false");
                }
            } else {
                context.result("false");
            }
        });

        app.post("/ticket/create", context -> {
            String userSessionActiveKey = context.sessionAttribute("userSessionActiveKey");
            if (sessionSecurity.checkValidSessionKey(userSessionActiveKey)) {
                int userId = sessionSecurity.getUserIdFromStorage(userSessionActiveKey);
                String value = context.formParam("id");
                if (value == null) {
                    value = context.queryParam("id");
                }
                System.out.println("sccsscsc " + value);
                int projectId = Integer.parseInt(value);

                String name = context.formParam("name");
                if (name == null) {
                    name = context.queryParam("name");
                }
                String summary = context.formParam("summary");
                if (summary == null) {
                    summary = context.queryParam("summary");
                }
                String steptsToReproduce = context.formParam("steptsToReproduce");
                if (steptsToReproduce == null) {
                    steptsToReproduce = context.queryParam("steptsToReproduce");
                }
                String expectedResult = context.formParam("expectedResult");
                if (expectedResult == null) {
                    expectedResult = context.queryParam("expectedResult");
                }
                String actualResult = context.formParam("actualResult");
                if (actualResult == null) {
                    actualResult = context.queryParam("actualResult");
                }
                String status = context.formParam("status");
                if (status == null) {
                    status = context.queryParam("status");
                }
                String priority = context.formParam("priority");
                if (priority == null) {
                    priority = context.queryParam("priority");
                }
                String severity = context.formParam("severity");
                if (severity == null) {
                    severity = context.queryParam("severity");
                }
                String fixers = context.formParam("fixers");
                if (fixers == null) {
                    fixers = context.queryParam("fixers");
                }
                logger.info("ticket/create " + "userId " + userId + " projectId " + projectId
                        + " name  " + name + " summary " + summary + " steptsToReproduce  " + steptsToReproduce
                        + " expectedResult " + expectedResult + " actualResult " + actualResult + " status " + status
                        + " priority " + priority + "severity " + severity + " fixers " + fixers);
                logger.info("fixers" + fixers);
                List<String> fixerList = getNamesFromJson(fixers);
                if (trimString(name) && trimString(summary) && trimString(status) && trimString(priority) && trimString(severity) && fixerList.size() > 0) {
                    logger.info("trimStrings true");
                    BugStatus statusEnum = EnumService.getBugStatusFromString(status);
                    Priority priorityEnum = EnumService.getPriorityFromString(priority);
                    Severity severityEnum = EnumService.getSeverityFromString(severity);
                    if (trimString(statusEnum.toString()) && trimString(priorityEnum.toString()) && trimString(severityEnum.toString())) {
                        logger.info("enum true");
                        Map<Boolean, Integer> ticketMap = dataBase.createTicket(name, summary, steptsToReproduce, expectedResult, actualResult, statusEnum, priorityEnum,
                                severityEnum, fixerList, userId, projectId);
                        if (ticketMap.containsKey(true)) {
                            int id = ticketMap.get(true);
                            logger.info("int id = ticketMap.get(true); " + id);
                            context.result(String.valueOf(id));
                        } else {
                            logger.info("dataBase.createTicket " + false);
                            context.result("false");
                        }
                    }
                }
            } else {
                context.result("false");
            }

        });
        app.post("/ticket/canUpdateTicket", context -> {
            String userSessionActiveKey = context.sessionAttribute("userSessionActiveKey");
            if (sessionSecurity.checkValidSessionKey(userSessionActiveKey)) {
                int userId = sessionSecurity.getUserIdFromStorage(userSessionActiveKey);
                String value = context.formParam("id");
                if (value == null) {
                    value = context.queryParam("id");
                }
                int id = Integer.parseInt(value);
                boolean result = dataBase.canUserUpdateTicket(id, userId);
                logger.info(" /ticket/canUpdateTicket userId " + userId + " ticketId " + id + " result " + result);
                context.result(String.valueOf(result));
            } else {
                context.result("false");
            }
        });
        app.post("/ticket/canCreateTicket", context -> {
            String userSessionActiveKey = context.sessionAttribute("userSessionActiveKey");
            if (sessionSecurity.checkValidSessionKey(userSessionActiveKey)) {
                int userId = sessionSecurity.getUserIdFromStorage(userSessionActiveKey);
                String value = context.formParam("id");
                if (value == null) {
                    value = context.queryParam("id");
                }
                int id = Integer.parseInt(value);
                boolean result = dataBase.canUseCreateTicket(id, userId);
                logger.info(" /ticket/canCreateTicket userId " + userId + " ticketId " + id + " result " + result);
                context.result(String.valueOf(result));
            } else {
                context.result("false");
            }
        });
        app.post("/ticket/status", context -> {
            logger.info("post /ticket/status");
            List<BugStatus> enumList = Arrays.asList(BugStatus.class.getEnumConstants());
            context.json(enumList);
        });
        app.post("/ticket/prority", context -> {
            logger.info("post /ticket/prority");
            List<Priority> enumList = Arrays.asList(Priority.class.getEnumConstants());
            context.json(enumList);
        });
        app.post("/ticket/severity", context -> {
            logger.info("post /ticket/severity");
            List<Severity> enumList = Arrays.asList(Severity.class.getEnumConstants());
            context.json(enumList);
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

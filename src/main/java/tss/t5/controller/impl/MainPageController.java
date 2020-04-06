package tss.t5.controller.impl;

import io.javalin.Javalin;
import org.apache.log4j.Logger;
import tss.t5.controller.SessionSecurity;
import tss.t5.models.entity.subEntity.SubTicket;
import tss.t5.controller.BaseController;
import tss.t5.models.dataBase.DataBase;

import java.util.List;

/**
 * Contains methods to work with main page.
 */
public class MainPageController implements BaseController {
    private Logger logger = Logger.getLogger(MainPageController.class);
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
    public MainPageController(Javalin app, DataBase dataBase, SessionSecurity sessionSecurity) {
        this.app = app;
        this.dataBase = dataBase;
        this.sessionSecurity = sessionSecurity;
    }

    @Override
    public void execute() {
        app.post("/mainPage/allTickets", context -> {
            logger.info("post /mainPage/allTickets");
            String value = context.formParam("id");
            if (value == null) {
                value = context.queryParam("id");
            }
            int projectId = Integer.parseInt(value);
            int userId = 0;
            String userSessionActiveKey = context.sessionAttribute("userSessionActiveKey");
            if (sessionSecurity.checkValidSessionKey(userSessionActiveKey)) {
                logger.info("user user " + userSessionActiveKey);
                userId = sessionSecurity.getUserIdFromStorage(userSessionActiveKey);

            }
            List<SubTicket> tickets = dataBase.getSubTickets(projectId, userId);
            if (tickets != null && tickets.size() > 0) {
                logger.info("tickets != null size + " + tickets.size());
                context.json(tickets);
            } else {
                context.result("false");
            }
        });

        app.post("/mainPage/allTicketsByName", context -> {
            logger.info("post /mainPage/allTicketsByName");
            String value = context.formParam("id");
            if (value == null) {
                value = context.queryParam("id");
            }
            int projectId = Integer.parseInt(value);

            String name = context.formParam("name");
            if (name == null) {
                name = context.queryParam("name");
            }
            String summaryValue = context.formParam("summary");
            if (summaryValue == null) {
                summaryValue = context.queryParam("summary");
            }
            System.out.println(summaryValue);
            boolean summary = Boolean.valueOf(summaryValue);
            System.out.println(summary);

            int userId = 0;
            String userSessionActiveKey = context.sessionAttribute("userSessionActiveKey");
            if (sessionSecurity.checkValidSessionKey(userSessionActiveKey)) {
                logger.info("user user " + userSessionActiveKey);
                userId = sessionSecurity.getUserIdFromStorage(userSessionActiveKey);

            }
            List<SubTicket> tickets = dataBase.getSubTickets(name, summary, projectId, userId);
            if (tickets != null && tickets.size() > 0) {
                logger.info("tickets != null size + " + tickets.size());
                context.json(tickets);
            } else {
                context.result("false");
            }
        });


    }
}

package tss.t5.controller.impl;

import io.javalin.Javalin;
import org.apache.log4j.Logger;
import tss.t5.controller.BaseController;
import tss.t5.controller.MailSecurity;
import tss.t5.controller.PasswordSecurity;
import tss.t5.controller.SessionSecurity;
import tss.t5.models.dataBase.DataBase;
import tss.t5.service.MailNotification;

import java.util.ArrayList;
import java.util.List;

/**
 * Main controller for work with another application controller.
 */
public class MainProjectController {
    private Logger logger = Logger.getLogger(MainProjectController.class);
    private Javalin app;
    private DataBase dataBase;
    private List<BaseController> controllerInterfaceList = new ArrayList<>();
    private SessionSecurity sessionSecurity;
    private MailNotification mailNotification;
    private MailSecurity mailSecurity;
    private PasswordSecurity passwordSecurity;
    private Runnable sessionSecurityTracker;

    /**
     * Constructor.
     *
     * @param javalin                Javalin app
     * @param dataBase               implementation interface Database
     * @param sessionSecurity        implementation interface Security
     * @param mailNotification       implementation interface Security
     * @param mailSecurity           implementation interface mailSecurity
     * @param passwordSecurity       implementation interface passwordSecurity
     * @param sessionSecurityTracker implementation interface sessionSecurityTracker
     */
    public MainProjectController(Javalin javalin, DataBase dataBase, SessionSecurity sessionSecurity,
                                 MailNotification mailNotification, MailSecurity mailSecurity,
                                 PasswordSecurity passwordSecurity, Runnable sessionSecurityTracker) {
        this.app = javalin;
        this.dataBase = dataBase;
        this.sessionSecurity = sessionSecurity;
        this.mailNotification = mailNotification;
        this.mailSecurity = mailSecurity;
        this.passwordSecurity = passwordSecurity;
        this.sessionSecurityTracker = sessionSecurityTracker;
    }

    /**
     * Methods for start application.
     */
    public void startApp() {
        app.get("/", context -> {
            logger.info("app.get '/', pages/index.html");
            context.render("pages/index.html");
        });
        app.error(404, ctx -> {
            logger.info("page not found");
            ctx.render("pages/404.html");

        });
        app.error(500, ctx -> {
            logger.error("internal error");
            ctx.render("pages/500.html");
        });
        try {
            createControllers();
            sessionTracker();
            executeControllers();
        } catch (Exception ex) {
            logger.error(ex.getStackTrace());
        }
    }

    private void createControllers() {
        controllerInterfaceList.add(new MainPageController(app, dataBase, sessionSecurity));
        controllerInterfaceList.add(new TicketController(app, dataBase, sessionSecurity));
        controllerInterfaceList.add(new ProjectController(app, dataBase, sessionSecurity));
        controllerInterfaceList.add(new UserController(app, dataBase, sessionSecurity,
                mailNotification, mailSecurity, passwordSecurity));
    }

    private void executeControllers() {
        for (BaseController controllerInterface : controllerInterfaceList) {
            controllerInterface.execute();
        }
    }

    private void sessionTracker() {
        Thread thread = new Thread(sessionSecurityTracker);
        thread.start();
    }
}

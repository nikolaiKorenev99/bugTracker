package tss.t5;

import io.javalin.Javalin;
import org.apache.log4j.Logger;
import tss.t5.controller.MailSecurity;
import tss.t5.controller.PasswordSecurity;
import tss.t5.controller.SessionSecurity;
import tss.t5.controller.impl.MailSecurityController;
import tss.t5.controller.impl.MainProjectController;
import tss.t5.controller.impl.PasswordSecurityController;
import tss.t5.models.dataBase.DataBase;
import tss.t5.models.dataBase.SQLiteDataBase;
import tss.t5.service.MailNotification;
import tss.t5.service.MailNotificationImpl;
import tss.t5.service.Property;
import tss.t5.controller.impl.SessionSecurityController;

import java.sql.SQLException;

/**
 * Main class.
 */
public class App {
    private static Logger logger = Logger.getLogger(App.class);

    /**
     * Main methods.
     *
     * @param args system arguments
     */
    public static void main(String[] args) {
        logger.info("START");
        Javalin app = Javalin.create();
        app.start(Property.getPort());

        app.config.addStaticFiles("pages");

        DataBase dataBase = null;
        try {
            dataBase = new SQLiteDataBase();
        } catch (SQLException | ClassNotFoundException e) {
            logger.error(e);
        }
        SessionSecurityController sessionSecurity = new SessionSecurityController();
        MailSecurity mailSecurity = new MailSecurityController();
        MailNotification mailNotification = new MailNotificationImpl(Property.getEmailUsername(),
                Property.getEmailPassword());
        PasswordSecurity passwordSecurity = new PasswordSecurityController();
        MainProjectController mainProjectController = new MainProjectController(app,
                dataBase, sessionSecurity, mailNotification, mailSecurity,passwordSecurity,
                sessionSecurity);
        mainProjectController.startApp();
    }
}

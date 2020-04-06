package tss.t5.controller.impl;

import io.javalin.Javalin;
import org.apache.log4j.Logger;
import tss.t5.controller.MailSecurity;
import tss.t5.controller.PasswordSecurity;
import tss.t5.controller.SessionSecurity;
import tss.t5.models.entity.User;
import tss.t5.controller.BaseController;
import tss.t5.models.dataBase.DataBase;
import tss.t5.service.MailNotification;
import tss.t5.service.Property;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains methods to work with authentication user.
 */
public class UserController implements BaseController {
    private Logger logger = Logger.getLogger(UserController.class);
    private Javalin app;
    private DataBase dataBase;
    private SessionSecurity sessionSecurity;
    private MailNotification mailNotification;
    private MailSecurity mailSecurity;
    private PasswordSecurity passwordSecurity;

    /**
     * Constructor.
     *
     * @param app              Javalin app
     * @param dataBase         implementation interface Database
     * @param sessionSecurity  implementation interface Security
     * @param mailNotification implementation interface Security
     * @param mailSecurity     implementation interface mailSecurity
     * @param passwordSecurity implementation interface passwordSecurity
     */
    public UserController(Javalin app, DataBase dataBase, SessionSecurity sessionSecurity,
                          MailNotification mailNotification, MailSecurity mailSecurity,
                          PasswordSecurity passwordSecurity) {
        this.app = app;
        this.dataBase = dataBase;
        this.sessionSecurity = sessionSecurity;
        this.mailNotification = mailNotification;
        this.mailSecurity = mailSecurity;
        this.passwordSecurity = passwordSecurity;
    }

    @Override
    public void execute() {
        app.post("/user/login", context -> {
            String userSessionActiveKey = context.sessionAttribute("userSessionActiveKey");
            if (userSessionActiveKey != null && sessionSecurity.checkKeyInStorage(userSessionActiveKey)) {
                logger.info("Already loggined" + userSessionActiveKey + "id " +
                        sessionSecurity.getUserIdFromStorage(userSessionActiveKey));
                context.result("false");
                return;
            }
            String username = context.formParam("username");
            String password = context.formParam("password");
            if (username == null) {
                username = context.queryParam("username");
            }
            if (password == null) {
                password = context.queryParam("password");
            }
            logger.info("post /user/login usernameOrEmail" + username);
            try {
                String salt = dataBase.getUserSalt(username);
                String calculatedHash = passwordSecurity.getEncryptedPassword(password, salt);
                User user = dataBase.getUser(username, calculatedHash);
                if (user != null) {
                    context.sessionAttribute("userSessionActiveKey", sessionSecurity.putToStorageUserSessionKey(user.getId()));
                    logger.info("user found");
                    context.result("true");
                } else {
                    throw new NullPointerException("user user is null");
                }
            } catch (GeneralSecurityException | NullPointerException e) {
                logger.error(e);
                logger.info("User is not exist");
                context.result("false");
            }
        });

        app.post("/user/logout", context -> {
            String userSessionActiveKey = context.sessionAttribute("userSessionActiveKey");
            if (sessionSecurity.checkValidSessionKey(userSessionActiveKey)) {
                logger.info("user logout " + sessionSecurity.getUserIdFromStorage(userSessionActiveKey));
                sessionSecurity.removeFromStorageUserSessionKey(userSessionActiveKey);
                context.sessionAttribute("userSessionActiveKey", null);
                context.result("true");
            } else {
                context.result("false");
            }
        });

        app.post("/user/user", context -> {
            String userSessionActiveKey = context.sessionAttribute("userSessionActiveKey");
            if (sessionSecurity.checkValidSessionKey(userSessionActiveKey)) {
                logger.info("user user " + userSessionActiveKey);
                int id = sessionSecurity.getUserIdFromStorage(userSessionActiveKey);
                User user = dataBase.getUser(id);
                if (user != null) {
                    logger.info("user " + user.toString());
                    context.json(user);
                }
            } else {
                logger.info("user does not exist");
                context.result("false");
            }
        });

        app.post("/session/status", context -> {
            String userSessionActiveKey = context.sessionAttribute("userSessionActiveKey");
            if (sessionSecurity.checkValidSessionKey(userSessionActiveKey)) {
                logger.info("/session/status " + sessionSecurity.getUserIdFromStorage(userSessionActiveKey) + " true");
                context.result("true");
            } else {
                logger.info("/session/status " + userSessionActiveKey + " false");
                context.result("false");
            }
        });

        app.post("/user/username", context -> {
            logger.info("/user/username");
            String username = context.formParam("username");
            if (username == null) {
                username = context.queryParam("username");
            }
            logger.info("username " + username);
            boolean statusUsername = dataBase.checkNotExistUserName(username);
            logger.info(statusUsername);
            context.result(String.valueOf(statusUsername));
        });
        app.post("/user/email", context -> {
            logger.info("/user/email");
            String email = context.formParam("email");
            if (email == null) {
                email = context.queryParam("email");
            }
            logger.info("email " + email);
            boolean statusEmail = dataBase.checkNotExistEmail(email);
            logger.info(statusEmail);
            context.result(String.valueOf(statusEmail));
        });


        app.post("/user/sentVerifyMail", context -> {
            logger.info("/user/sentVerifyMail");
            String email = context.formParam("email");
            if (email == null) {
                email = context.queryParam("email");
            }
            String subject = Property.getEmailSubject();
            String text = Property.getEmailTextRegistration();
            Integer code = mailSecurity.getNewCodeForVerify(email);
            if (code != null) {
                boolean result = mailNotification.sendNotification(email, subject, text + code);
                logger.info("email " + email + " result " + result);
                context.result(String.valueOf(result));
            } else {
                logger.info("Code already isset");
                context.result("false");
            }

        });
        app.post("/user/code", context -> {
            String email = context.formParam("email");
            if (email == null) {
                email = context.queryParam("email");
            }
            String stringCode = context.formParam("code");
            if (stringCode == null) {
                stringCode = context.queryParam("code");
            }
            int code = Integer.parseInt(stringCode);
            if (mailSecurity.getCodeForVerify(email) != null &&
                    mailSecurity.getCodeForVerify(email).equals(code)) {
                context.result("true");
            } else {
                context.result("false");
            }
        });
        app.post("/user/create", context -> {
            logger.info("/user/create");
            String username = context.formParam("username");
            if (username == null) {
                username = context.queryParam("username");
            }
            String password = context.formParam("password");
            if (password == null) {
                password = context.queryParam("password");
            }
            String email = context.formParam("email");
            if (email == null) {
                email = context.queryParam("email");
            }
            String stringCode = context.formParam("code");
            if (stringCode == null) {
                stringCode = context.queryParam("code");
            }
            logger.info("username " + username + " email " + email + " code " + stringCode);
            if (stringCode.trim().length() > 1) {
                int code = Integer.parseInt(stringCode); // try !!!!!!!!
                if (username.trim().length() > 0 && password.trim().length() > 0 &&
                        mailSecurity.getCodeForVerify(email) != null &&
                        mailSecurity.getCodeForVerify(email).equals(code)) {
                    String solt = passwordSecurity.getNewSalt();
                    password = passwordSecurity.getEncryptedPassword(password, solt);
                    int userId = dataBase.createUser(username, email, password, solt);
                    User user = dataBase.getUser(userId);
                    if (user != null) {
                        context.sessionAttribute("userSessionActiveKey", sessionSecurity.putToStorageUserSessionKey(user.getId()));
                        logger.info("user found id " + user.getId());
                        logger.info("remove from storage " + mailSecurity.removeFromStorage(email));
                        context.result("true");

                    } else {
                        logger.error("/user/create error");
                        context.result("false");
                    }
                }
            } else if (stringCode.trim().length() < 2) {
                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    String solt = passwordSecurity.getNewSalt();
                    password = passwordSecurity.getEncryptedPassword(password, solt);
                    int userId = dataBase.createUser(username, email, password, solt);
                    User user = dataBase.getUser(userId);
                    if (user != null) {
                        context.sessionAttribute("userSessionActiveKey", sessionSecurity.putToStorageUserSessionKey(user.getId()));
                        logger.info("user found id " + user.getId());
                        logger.info("remove from storage " + mailSecurity.removeFromStorage(email));
                        context.result("true");

                    } else {
                        logger.error("/user/create error");
                        context.result("false");
                    }
                }
            } else {
                logger.error("/user/create error");
                context.result("false");
            }
        });
        app.post("/user/restore", context -> {
            logger.info("/user/restore");

            String email = context.formParam("email");
            if (email == null) {
                email = context.queryParam("email");
            }
            //String password = dataBase.getPasswordByEmail(email);
            String password = passwordSecurity.generateNewPassword();
            if (updatePassword(email, password)) {
                String subject = Property.getEmailSubject();
                String text = Property.getEmailRestoreText();
                if (mailNotification.sendNotification(email, subject, text + password)) {
                    context.result("true");
                } else {
                    context.result("false");
                }
            } else {
                context.result("false");
            }
        });

        app.post("/user/allUsers", context -> {
            logger.info("/user/allUsers");
            List<User> userList = dataBase.getAllusers();
            List<String> userNames = new ArrayList<>();
            userList.forEach(x -> userNames.add(x.getUserName()));
            if (userNames.size() > 0) {
                logger.info("userNames  size " + userNames.size());
                context.json(userNames);
            } else {
                logger.info("userNames is not exist");
                context.result("false");
            }
        });

    }

    private boolean updatePassword(String email, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String solt = passwordSecurity.getNewSalt();
        password = passwordSecurity.getEncryptedPassword(password, solt);
        return dataBase.updatePassword(email, password, solt);
    }
}

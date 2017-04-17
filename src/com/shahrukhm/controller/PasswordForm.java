package com.shahrukhm.controller;

import com.shahrukhm.MyJob;
import com.shahrukhm.model.Authentication;
import com.shahrukhm.model.BackgroundRequestStream;
import com.shahrukhm.model.ObservableProperties;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * A class that is the initial GUI which handles user credentials.
 */
public class PasswordForm extends JDialog {
    private static final String LIB_PATH = "lib/";
    private static final String PROPERTIES_FILE = "SpeedwayDatabaseTool.properties";

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPasswordField dbPasswordField;
    private JPasswordField serverPasswordField;
    private JTextField dbUserTextField;
    private JTextField serverUserTextField;
    private JTextField serverDBUserTextField;
    private JPasswordField serverDBPasswordField;

    final static Logger logger = Logger.getLogger(PasswordForm.class.getName());

    // Gui form for user credentials.
    public PasswordForm() {
        setContentPane(contentPane);
        setSize(400,300);
        setLocationRelativeTo(null);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setVisible(true);
    }

    // Setup authentication objects after user submits credentials and then proceed to application home.
    private void onOK() {
        // add your code here
        String dbUser = dbUserTextField.getText();
        char[] dbPasswordArray = dbPasswordField.getPassword();
        String serverUser = serverUserTextField.getText();
        char[] serverPasswordArray = serverPasswordField.getPassword();
        String serverDBUser = serverDBUserTextField.getText();
        char[] serverDBPasswordArray = serverDBPasswordField.getPassword();
        Authentication dbAuthentication = new Authentication(dbUser, dbPasswordArray);
        Authentication serverAuthentication = new Authentication(serverUser, serverPasswordArray);
        Authentication serverDBAuthentication = new Authentication(serverDBUser, serverDBPasswordArray);

        try {
            ObservableProperties observableProperties = new ObservableProperties(LIB_PATH + PROPERTIES_FILE);
            DAOIdeal daoIdeal = new DAOIdeal(observableProperties, dbAuthentication);
            DAOMagento daoMagento = new DAOMagento(observableProperties, serverDBAuthentication);
            daoIdeal.connect();
            daoMagento.connect();
            Upload upload = new Upload(observableProperties, serverAuthentication);
            BackgroundRequestStream backgroundRequestStream = new BackgroundRequestStream(daoIdeal, daoMagento, upload);

            observableProperties.addObserver(daoIdeal);
            observableProperties.addObserver(upload);
            new MainApp(backgroundRequestStream, observableProperties,
                    dbAuthentication, serverAuthentication, serverDBAuthentication);

            try {
                SchedulerFactory schedulerFactory = new StdSchedulerFactory();
                Scheduler scheduler = schedulerFactory.getScheduler();
                scheduler.getContext().put("backgroundRequestStream", backgroundRequestStream);
                JobDetail jobDetail = JobBuilder.newJob(MyJob.class)
                        .withIdentity("job1", "group1")
                        .build();

                CronTrigger trigger = TriggerBuilder
                        .newTrigger()
                        .withIdentity("trigger1", "group1")
                        .withSchedule(CronScheduleBuilder.cronSchedule("0 59 13 * * ?"))
                        .build();

                scheduler.scheduleJob(jobDetail, trigger);
                scheduler.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

            daoIdeal.connect();

        } catch (IOException e) {
            logger.debug("PasswordForm: Unable to get properties due to IOException.");
        } catch (SQLException e) {
            e.printStackTrace();
            logger.debug("PasswordForm: Unable to connect due to SQLException.");
        }
        dispose();
    }

    // Close window.
    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        PasswordForm dialog = new PasswordForm();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}

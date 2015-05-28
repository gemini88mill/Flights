package net.raphaelmiller;

import com.googlecode.lanterna.gui.*;
import com.googlecode.lanterna.gui.component.*;
import com.googlecode.lanterna.gui.layout.LayoutParameter;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;


/**
 * Created by raphael on 5/27/15.
 */
public class GUIWindow extends Window {

    private String uiUser;
    private String uiPassword;

    Panel horizontalPanel;
    Panel leftPanel;

    Button quit;

    public GUIWindow(String title, FlightsClient flc) {
        super(title);
        horizontalPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
        leftPanel = new Panel(new Border.Bevel(true), Panel.Orientation.HORISONTAL);
        //Panel rightPanel = new Panel(new Border.Bevel(true), Panel.Orientation.VERTICAL);
        //Panel middlePanel = new Panel(new Border.Bevel(true), Panel.Orientation.VERTICAL);

        quit = new Button("QUIT", new Action() {
            @Override
            public void doAction() {
                System.exit(0);
            }
        });

        addComponent(leftPanel);
        //horizontalPanel.addComponent(middlePanel);
        //horizontalPanel.addComponent(rightPanel);



        addComponent(new Label("Welcome To Flights(Alpha)"));
        //lanternaLogin();
        lanternaStartMenu(flc);



        addComponent(horizontalPanel);
    }

    private void lanternaLogin() {
        final TextBox username = new TextBox("Username", 400);
        final PasswordBox password = new PasswordBox(null, 400);

        addComponent(new Label("", Terminal.Color.BLACK));
        addComponent(username);
        addComponent(new Label("Password:", Terminal.Color.BLACK));
        addComponent(password);

        addComponent(new Button("ENTER", new Action() {
            @Override
            public void doAction() {
                uiUser = username.getText();
                uiPassword = password.getText();

                System.out.println(uiUser + " " + uiPassword);

                //lanternaStartMenu();
            }
        }));
        addComponent(new Button("Quit", new Action() {
            @Override
            public void doAction() {
                System.exit(0);
            }
        }));
    }

    private void lanternaStartMenu(FlightsClient flc) {

        final TextBox departureDest = new TextBox(null, 400);
        final TextBox arrivalDest = new TextBox(null, 400);
        final TextBox dateOfDepart = new TextBox(null, 400);

        final String inputString[] = new String[3];


        addComponent(new Label("Departing From: (IATA CODE)", Terminal.Color.RED, true));
        addComponent(departureDest);
        addComponent(new Label("Arrival Destination", Terminal.Color.RED, true));
        addComponent(arrivalDest);
        addComponent(new Label("Date of Departure", Terminal.Color.RED, true));
        addComponent(dateOfDepart);

        addComponent(new Button("ENTER", new Action() {
            @Override
            public void doAction() {
                inputString[0] = departureDest.getText();
                inputString[1] = arrivalDest.getText();
                inputString[2] = dateOfDepart.getText();

                info();
            }
        }));




        flc.setDepartureIATA(inputString[0]);
        flc.setArrivalIATA(inputString[1]);
        flc.setDateOfDeparture(inputString[2]);

        addComponent(quit);
    }

    private void info() {
        TextBox info = new TextBox("test", 400);  //formatted information from displayValues should go here 

        Panel panel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
        panel.setVisible(true);
        addComponent(panel);
        panel.addComponent(info);
    }


}
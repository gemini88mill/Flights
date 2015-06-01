package net.raphaelmiller;

import com.googlecode.lanterna.gui.*;
import com.googlecode.lanterna.gui.component.*;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;


/**
 * Created by raphael on 5/27/15.
 */
public class GUIWindow extends Window {

    private String uiUser;
    private String uiPassword;

    Panel horizontalPanel, leftPanel, rightPanel, middlePanel;

    FlightsClient flc;
    Button quit;

    public GUIWindow(String title, FlightsClient flc) {
        super(title);

        this.flc = flc;

        horizontalPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
        leftPanel = new Panel(new Border.Invisible(), Panel.Orientation.VERTICAL);
        middlePanel = new Panel(new Border.Invisible(), Panel.Orientation.VERTICAL);
        rightPanel = new Panel(new Border.Invisible(), Panel.Orientation.VERTICAL);

        horizontalPanel.addComponent(leftPanel);
        horizontalPanel.addComponent(middlePanel);
        horizontalPanel.addComponent(rightPanel);
        addComponent(horizontalPanel);
    }

    //generic quit button that leaves the GUI and shuts down the program whenever pressed
    public void quitButton(){
        addComponent(new Button("QUIT", new Action() {
            @Override
            public void doAction() {
                System.exit(0);
            }
        }));
    }

    public void enterButton(final GUIScreen guiScreen, final GUIWindow guiOutput, final TextBox destinationBox,
                            final TextBox departureLocationBox, final TextBox dateOfDepartureBox){
        final String[] input = new String[3];

        addComponent(new Button("ENTER", new Action() {
            @Override
            public void doAction() {
                input[0] = destinationBox.getText();
                input[1] = departureLocationBox.getText();
                input[2] = dateOfDepartureBox.getText();

                flc.setDateOfDeparture(input[2]);
                flc.setDepartureIATA(input[1]);
                flc.setArrivalIATA(input[0]);

                guiScreen.showWindow(guiOutput, GUIScreen.Position.FULL_SCREEN);

            }
        }));




        String algoOut = null;
        for (int x = 0; x < input.length; x++){
            algoOut += "\n" + input[x];
        }

        guiOutput.quitButton();
        guiOutput.horizontalPanel.addComponent(new TextArea(new TerminalSize(400, 300), algoOut));
        //variable text area, modify to store data from display values
    }


    //attempts at second page, left in for reference ----------------------------------
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

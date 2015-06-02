package net.raphaelmiller;

import com.google.api.services.qpxExpress.model.TripOption;
import com.googlecode.lanterna.gui.*;
import com.googlecode.lanterna.gui.component.*;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.util.List;


/**
 * Created by raphael on 5/27/15.
 *
 * GuiWindow Class -- extends Window (Lanterna)
 *
 * Houses all functions for Gui functionality, also houses common buttons and information for said Windows.
 */
public class GUIWindow extends Window {

    private String uiUser;
    private String uiPassword;

    Panel horizontalPanel, leftPanel, rightPanel, middlePanel;

    FlightsClient flc;
    Button quit;

    /**
     * Constructor for GUIWindow, creates gui framework, and allows for modulation.
     * @param title
     * @param flc
     */
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

    /**
     * quitButton() method
     *
     * generic quit button that leaves the GUI and shuts down the program whenever pressed
     *
     */
    public void quitButton(){
        addComponent(new Button("QUIT", new Action() {
            @Override
            public void doAction() {
                System.exit(0);
            }
        }));
    }

    /**
     * enterButton() method
     *
     * Gives action to what happens when the enter button is pressed.
     * @param guiScreen
     * @param guiOutput
     * @param destinationBox
     * @param departureLocationBox
     * @param dateOfDepartureBox
     */
    public void enterButton(final GUIScreen guiScreen, final GUIWindow guiOutput, final TextBox destinationBox,
                            final TextBox departureLocationBox, final TextBox dateOfDepartureBox){

        final TextArea results = new TextArea(new TerminalSize(400, 300), null);
        final String[] input = new String[3];
        final String[] textValues = {null};

        addComponent(new Button("ENTER", new Action() {
            @Override
            public void doAction() {
                input[0] = destinationBox.getText();
                input[1] = departureLocationBox.getText();
                input[2] = dateOfDepartureBox.getText();

                flc.setDateOfDeparture(input[2]);
                flc.setDepartureIATA(input[1]);
                flc.setArrivalIATA(input[0]);

                //sends information to googleCommunicate() in FlightsClient...
                textValues[0] = sendToGoogle(input);

                //prints Data line by line ()
                results.appendLine(textValues[0]);

                guiScreen.showWindow(guiOutput, GUIScreen.Position.FULL_SCREEN);

            }
        }));

        drawPage(guiOutput, results);

        System.out.println(input[0]);


        //variable text area, modify to store data from display values
    }

    /**
     * sendToGoogle() method
     *
     * sends information to QPX Express from gui.
     * @param input
     * @return
     */
    private String sendToGoogle(String[] input) {
        //connection established with doAction()
        //System.out.println(input[1]);

        //sending firstPage Data to googleCommunicate...
        List<TripOption> tripResults = flc.googleCommunicate(input);

        String textValues = UIInterface.displayValues(tripResults, flc.tripData, flc.aircraftData, flc.carrierData, flc.airportData);
        return textValues;

    }

    /**
     * drawPage() method
     *
     * opens up new window for data stream from QPX
     * @param guiOutput
     * @param results
     */
    private void drawPage(GUIWindow guiOutput, TextArea results) {

        guiOutput.quitButton();
        guiOutput.horizontalPanel.addComponent(results);
        //results.appendLine(textValues);
    }

    //--------------------methods not in use------------------------------------------
    //--------------------------------------------------------------------------------


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

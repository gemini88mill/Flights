package net.raphaelmiller;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.qpxExpress.model.TripOption;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.*;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.text.ParseException;
import java.util.List;

/**
 * Created by raphael on 6/26/15.
 */
public class Buttons extends Thread {

    ErrorHandler eh = new ErrorHandler();
    DepartureFlight df = new DepartureFlight();

    /**
     * backButton() - method
     *
     * creates a back button (resets the program and brings it to the beginning)
     *
     * @param guiScreen GuiScreen
     * @param guiWindow GUIWindow
     */
    public void backButton(GUIScreen guiScreen, GUIWindow guiWindow) {
        guiWindow.addComponent(new Button("BACK", () -> {
            LanternaHandler lanternaHandler = new LanternaHandler();
            guiScreen.getScreen().stopScreen();
            try {
                lanternaHandler.LanternaTerminal(new FlightsClient(null, null, null, null));
            } catch (GoogleJsonResponseException e) {
                e.printStackTrace();
            }
        }));
    }

    /**
     * guiInputEnterButton() method
     * <p>
     * Gives action to what happens when the enter button is pressed.
     * @param guiScreen            GUIScreen
     * @param guiOutput            GUIWindow
     * @param guiInboundFlight
     * @param destinationBox       TextBox
     * @param departureLocationBox TextBox
     * @param dateOfDepartureBox   TextBox
     * @param passengerBox         TextBox
     * @param progressBar          ProgressBar
     * @param guiError             GuiWindow
     * @param guiLoad              GUIWindow
     * @param guiWindow            GUIWindow
     */
    public void guiInputEnterButton(final GUIScreen guiScreen, final GUIWindow guiOutput, GUIWindow guiInboundFlight, final TextBox destinationBox,
                                    final TextBox departureLocationBox, final TextBox dateOfDepartureBox, TextBox passengerBox,
                                    ProgressBar progressBar, GUIWindow guiError, GUIWindow guiLoad, GUIWindow guiWindow)  {

        final TextArea results = new TextArea(new TerminalSize(400, 300), null);
        final String[] input = new String[4];
        final boolean[] test = {false};

        // enter button start, creates enter button and creates eventhandlers for said enter button.
        guiWindow.addComponent(new Button("ENTER", () -> {
            //progressBar.setVisible(true);

            //loading window thread. starts loading bar window while, QPX is working in the background
            Thread thread = new Thread(){
                public void run(){
                    System.out.println("thread running");
                    guiLoad.horizontalPanel.addComponent(new Label("Loading...", Terminal.Color.BLACK, true));
                    guiLoad.guiScreen.showWindow(guiLoad, GUIScreen.Position.CENTER);
                }
            };   thread.start();

            //sets date to test for date entered to be a acceptable value
            String date = guiWindow.setFlightsClient(input, destinationBox, departureLocationBox, dateOfDepartureBox,
                    passengerBox);
            //List<TripOption> initializer
            List<TripOption> tripOptions = null;

            //try/catch statement for values entered.
            try {

                tripOptions = guiWindow.attemptTransfer(input);
                test[0] = guiWindow.dateTester(date);

                //df.departureFlightWindow(guiWindow, tripOptions, results, guiScreen, guiOutput, guiLoad);
                guiWindow.formatToScreen(tripOptions, guiWindow.flc.tripData, guiWindow.flc.aircraftData,
                        guiWindow.flc.carrierData, guiWindow.flc.airportData, results);

                guiWindow.drawPage(guiOutput, results, guiScreen, guiInboundFlight);
                guiScreen.showWindow(guiOutput, GUIScreen.Position.FULL_SCREEN);

            } catch (IllegalAccessException | InstantiationException | GoogleJsonResponseException | ParseException |
                    NullPointerException e) {

                eh.errorWindow(guiError, guiScreen, this);
                e.printStackTrace();
            }


        }));

        System.out.println(input[0]);
        //variable text area, modify to store data from display values
    }

    public void guiOutputEnterButton(GUIWindow guiOutput, TextBox flightNo, String outbound, String inbound,
                                     String date, GUIWindow guiInboundFlight){
        guiOutput.addComponent(new Button("ENTER", new Action() {
            @Override
            public void doAction() {
                enterLogic(flightNo, outbound, inbound, guiOutput, date, guiInboundFlight);
            }
        }));

    }

    private void enterLogic(TextBox flightNo, String outbound, String inbound, GUIWindow guiOutput, String date,
                            GUIWindow guiInboundFlight) {

        TextArea results = new TextArea(new TerminalSize(400, 300), null);



        String flightNoText = flightNo.getText();
        System.out.println(flightNoText);
        System.out.println(outbound);
        System.out.println(inbound);

        List<TripOption> tripOption;

        String input[] = new String[3];

        input[1] = outbound;
        input[0] = inbound;
        input[2] = date;

        try {
            tripOption = guiInboundFlight.attemptTransfer(input);

            guiInboundFlight.formatToScreen(tripOption, guiOutput.flc.getTripData(), guiOutput.flc.getAircraftData(),
                    guiOutput.flc.getCarrierData(), guiOutput.flc.airportData, results);
            guiInboundFlight.buttons.backButton(guiInboundFlight.guiScreen, guiOutput);
            guiInboundFlight.buttons.quitButton(guiInboundFlight);
            guiInboundFlight.horizontalPanel.addComponent(results);
            guiInboundFlight.guiScreen.showWindow(guiInboundFlight, GUIScreen.Position.FULL_SCREEN);

        } catch (IllegalAccessException | GoogleJsonResponseException | InstantiationException e) {
            e.printStackTrace();
        }

    }

    /**
     * quitButton() method
     * <p>
     * generic quit button that leaves the GUI and shuts down the program whenever pressed
     * @param guiWindow GUIWindow
     */
    public void quitButton(GUIWindow guiWindow) {
        guiWindow.addComponent(new Button("QUIT", () -> System.exit(0)));
    }
}

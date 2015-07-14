package net.raphaelmiller;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.qpxExpress.model.TripOption;
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
     * @param guiInboundFlight     GuiWindow
     * @param destinationBox       TextBox
     * @param passengerBox         TextBox
     * @param progressBar          ProgressBar
     * @param guiLoad              GUIWindow
     * @param guiWindow            GUIWindow
     * @param guiItenerary         GuiWindow
     * @param guiWindows
     * @param boxes
     */
    public void guiInputEnterButton(LanternaHandler guiWindows, LanternaHandler boxes)  {

        final TextArea results = new TextArea(new TerminalSize(400, 300), null);
        final String[] input = new String[4];
        final boolean[] test = {false};

        GUIWindow loadingWindow = guiWindows.getGuiLoad();
        GUIWindow outboundWindow = guiWindows.getGuiOutboundFlight();
        GUIWindow errorWindow = guiWindows.getGuiError();
        GUIWindow itinerary = guiWindows.getGuiItenerary();
        GUIWindow inboundWindow = guiWindows.getGuiInboundFlight();
        GUIWindow inputWindow = guiWindows.getGuiInput();

        GUIScreen screen = guiWindows.getGuiScreen();

        TextBox destination = boxes.getDestinationBox();
        TextBox departureLocation = boxes.getDepartureLocationBox();
        TextBox departureDate = boxes.getDateOfDepartureBox();
        TextBox passengers = boxes.getPassengerBox();
        TextBox returnDate = boxes.getDateOfReturnBox();

        TextArea resultsArea = boxes.getResultsArea();


        // enter button start, creates enter button and creates eventhandlers for said enter button.
        inputWindow.addComponent(new Button("ENTER", () -> {
            //progressBar.setVisible(true);

            //loading window thread. starts loading bar window while, QPX is working in the background
            Thread thread = new Thread() {
                public void run() {
                    System.out.println("thread running");
                    loadingWindow.horizontalPanel.addComponent(new Label("Loading...", Terminal.Color.BLACK, true));
                    loadingWindow.guiScreen.showWindow(loadingWindow, GUIScreen.Position.CENTER);
                }
            };
            thread.start();

            //sets date to test for date entered to be a acceptable value


            String date = outboundWindow.setFlightsClient(input, destination, departureLocation, departureDate,
                    passengers);

            //List<TripOption> initializer
            List<TripOption> tripOptions = null;

            //try/catch statement for values entered.
            try {

                tripOptions = outboundWindow.attemptTransfer(input, date);
                test[0] = outboundWindow.dateTester(date);

                //df.departureFlightWindow(guiWindow, tripOptions, results, guiScreen, guiOutput, guiLoad);
                outboundWindow.formatToScreen(tripOptions, outboundWindow.flc.tripData, outboundWindow.flc.aircraftData,
                        outboundWindow.flc.carrierData, outboundWindow.flc.airportData, resultsArea);

                outboundWindow.drawPage(resultsArea, tripOptions, guiWindows, boxes);
                outboundWindow.guiScreen.showWindow(outboundWindow, GUIScreen.Position.FULL_SCREEN);

            } catch (IllegalAccessException | InstantiationException | GoogleJsonResponseException | ParseException |
                    NullPointerException e) {

                eh.errorWindow(errorWindow, screen, this);
                e.printStackTrace();
            }


        }));

        System.out.println(input[0]);
        //variable text area, modify to store data from display values
    }

    /**
     * Gui Output Enter Button, function that handles the gui output enter event.
     * @param guiOutput         GuiWindow
     * @param guiInboundFlight  GuiWindow
     * @param guiLoad           GuiWindow
     * @param guiItinerary      GuiWindow
     * @param dateOfReturnBox
     * @param outbound          String
     * @param inbound           String
     * @param date              String
     * @param tripOptions       List<TripOption>
     * @param guiWindows
     * @param boxes
     */
    public void guiOutputEnterButton(String outbound, String inbound, String date, List<TripOption> tripOptions,
                                     LanternaHandler guiWindows, LanternaHandler boxes){

        TextBox flightChoice = boxes.getFlightChoiceBox();
        TextBox returnDate = boxes.getDateOfReturnBox();

        GUIWindow outboundWindow = guiWindows.getGuiOutboundFlight();
        GUIWindow inboundFlight = guiWindows.getGuiInboundFlight();
        GUIWindow loadingWindow = guiWindows.getGuiLoad();
        GUIWindow itinerary = guiWindows.getGuiItenerary();

        outboundWindow.addComponent(new Button("ENTER", () -> {

            Thread thread = new Thread() {
                public void run() {
                    System.out.println("thread running");
                    loadingWindow.guiScreen.showWindow(loadingWindow, GUIScreen.Position.CENTER);
                }
            };
            thread.start();

            guiOutboundEnterLogic(flightChoice, outbound, inbound, date, tripOptions,
                    guiWindows, boxes);
        }));

    }


    /**
     * Gui Output Enter Button Logic. handles extra logic, different method because new Action Interface is making me
     * declare everything final and although I dont receive any errors, I feel that there is something wrong with the
     * way I would be writing it, therefore, new method.
     * @param guiOutput             GuiWindow
     * @param guiInboundFlight      String
     * @param guiLoad               GuiWindow
     * @param guiItenerary          GuiWindow
     * @param flightNo              TextBox
     * @param outbound              String
     * @param inbound               String
     * @param date                  String
     * @param tripOptions           List<TripOption>
     * @param guiWindows
     * @param boxes
     */
    private void guiOutboundEnterLogic(TextBox flightNo, String outbound, String inbound, String date,
                                       List<TripOption> tripOptions, LanternaHandler guiWindows, LanternaHandler boxes) {

        GUIWindow inboundWindow = guiWindows.getGuiInboundFlight();
        GUIWindow outboundWindow = guiWindows.getGuiOutboundFlight();
        GUIWindow loadingWindow = guiWindows.getGuiLoad();
        GUIWindow itinerary = guiWindows.getGuiItenerary();

        TextArea results = new TextArea(new TerminalSize(400, 300), null);
        TextArea resultArea = boxes.getResultsArea();

        TextBox flightChoice = boxes.getFlightChoiceBox();
        TextBox returnDateBox = boxes.getDateOfReturnBox();

        TripOption flightChoiceInbound;

        String selection = flightChoice.getText();
        String returnDate = returnDateBox.getText();

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
            tripOption = inboundWindow.attemptTransfer(input, returnDate);

            flightChoiceInbound = tripOptions.get(Integer.parseInt(selection));
            System.out.println(flightChoiceInbound);

            inboundWindow.drawGuiInbound(tripOption,
                    flightChoiceInbound, guiWindows, boxes);

        } catch (IllegalAccessException | GoogleJsonResponseException | InstantiationException e) {
            e.printStackTrace();
        }

    }


    /**
     * Gui Inbound enter Button - event listener for enter button selected and pressed.
     * @param guiInboundFlight      GuiWindow
     * @param flightChoiceInbound  TripOption
     * @param flightNo              String
     * @param tripOption            List
     * @param guiLoad               GuiWindow
     * @param guiItenerary          GuiWindow
     * @param guiOutput             GuiWindow
     */
    public void guiInboundEnterButton(GUIWindow guiInboundFlight, TripOption flightChoiceInbound, TextBox flightNo,
                                      List<TripOption> tripOption, GUIWindow guiLoad, GUIWindow guiItenerary,
                                      GUIWindow guiOutput) {
        guiInboundFlight.addComponent(new Button("ENTER", () -> {

            Thread thread = new Thread(){
                public void run(){
                    System.out.println("thread running");
                    guiLoad.guiScreen.showWindow(guiLoad, GUIScreen.Position.CENTER);
                }
            };   thread.start();

            guiInboundEnterLogic(flightNo, flightChoiceInbound, tripOption, guiItenerary, guiOutput);
        }));
    }


    /**
     * Gui Inbound Enter Button Logic. handles extra logic, different method because new Action Interface is making me
     * declare everything final and although I dont receive any errors, I feel that there is something wrong with the
     * way I would be writing it, therefore, new method.
     * @param flightNo
     * @param flightChoiceInbound
     * @param tripOption
     * @param guiItenerary
     * @param guiOutput
     */
    private void guiInboundEnterLogic(TextBox flightNo, TripOption flightChoiceInbound, List<TripOption> tripOption, GUIWindow guiItenerary, GUIWindow guiOutput) {

        TripOption flightChoiceOutBound;

        //loading new text area for next page
        TextArea results = new TextArea(new TerminalSize(400, 300), null);

        //collects flightNo textBox input and stores it use future use.
        String selection = flightNo.getText();

        //stores flight choice from previous screen
        flightChoiceOutBound = tripOption.get(Integer.parseInt(selection));

        guiItenerary.drawGuiItinerary(results, flightChoiceInbound, guiItenerary, guiOutput, flightChoiceOutBound);
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

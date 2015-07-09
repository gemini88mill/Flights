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

                guiWindow.drawPage(guiOutput, results, guiScreen, guiInboundFlight, guiLoad, tripOptions);
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

    /**
     *  Gui Output Enter Button, function that handles the gui output enter event.
     *
     * @param guiOutput GuiWindow
     * @param flightNo TextBox
     * @param outbound String
     * @param inbound String
     * @param date String
     * @param guiInboundFlight GuiWindow
     * @param guiLoad GuiWindow
     * @param tripOptions List<TripOption>
     */
    public void guiOutputEnterButton(GUIWindow guiOutput, TextBox flightNo, String outbound, String inbound,
                                     String date, GUIWindow guiInboundFlight, GUIWindow guiLoad, List<TripOption> tripOptions){
        guiOutput.addComponent(new Button("ENTER", () -> {

            Thread thread = new Thread(){
                public void run(){
                    System.out.println("thread running");
                    guiLoad.guiScreen.showWindow(guiLoad, GUIScreen.Position.CENTER);
                }
            };   thread.start();

            guiOutputEnterLogic(flightNo, outbound, inbound, guiOutput, date, guiInboundFlight, tripOptions, guiLoad);
        }));

    }


    /**
     * Gui Output Enter Button Logic. handles extra logic, different method because new Action Interface is making me
     * declare everything final and although I dont receive any errors, I feel that there is something wrong with the
     * way I would be writing it, therefore, new method.
     *
     * @param flightNo TextBox
     * @param outbound String
     * @param inbound String
     * @param guiOutput GuiWindow
     * @param date String
     * @param guiInboundFlight String
     * @param tripOptions List<TripOption>
     * @param guiLoad GuiWindow
     */
    private void guiOutputEnterLogic(TextBox flightNo, String outbound, String inbound, GUIWindow guiOutput, String date,
                                     GUIWindow guiInboundFlight, List<TripOption> tripOptions, GUIWindow guiLoad) {

        TextArea results = new TextArea(new TerminalSize(400, 300), null);

        TripOption flightChoice;
        String selection = flightNo.getText();

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

            flightChoice = tripOptions.get(Integer.parseInt(selection));
            System.out.println(flightChoice);

            guiInboundFlight.drawGuiInbound(guiInboundFlight, tripOption, guiOutput, flightNo, results, flightChoice, guiLoad);

        } catch (IllegalAccessException | GoogleJsonResponseException | InstantiationException e) {
            e.printStackTrace();
        }

    }


    /**
     * Gui Inbound enter Button - event listener for enter button selected and pressed.
     *
     * @param guiInboundFlight
     * @param flightChoiceOutbound
     * @param flightNo
     * @param tripOption
     * @param guiLoad
     */
    public void guiInboundEnterButton(GUIWindow guiInboundFlight, TripOption flightChoiceOutbound, TextBox flightNo,
                                      List<TripOption> tripOption, GUIWindow guiLoad) {
        guiInboundFlight.addComponent(new Button("ENTER", () -> {

            Thread thread = new Thread(){
                public void run(){
                    System.out.println("thread running");
                    guiLoad.guiScreen.showWindow(guiLoad, GUIScreen.Position.CENTER);
                }
            };   thread.start();

            guiInboundEnterLogic(flightNo, flightChoiceOutbound, tripOption);
        }));
    }


    /**
     * Gui Inbound Enter Button Logic. handles extra logic, different method because new Action Interface is making me
     * declare everything final and although I dont receive any errors, I feel that there is something wrong with the
     * way I would be writing it, therefore, new method.
     *
     * @param flightNo
     * @param flightChoiceOutbound
     * @param tripOption
     */
    private void guiInboundEnterLogic(TextBox flightNo, TripOption flightChoiceOutbound, List<TripOption> tripOption) {

        TextArea results = new TextArea(new TerminalSize(400, 300), null);

        String selection = flightNo.getText();

        flightChoiceOutbound = tripOption.get(Integer.parseInt(selection));
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

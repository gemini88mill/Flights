package net.raphaelmiller;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.qpxExpress.model.*;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Border;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.*;
import com.googlecode.lanterna.terminal.Terminal;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.googlecode.lanterna.gui.GUIScreen.Position.CENTER;


/**
 * Created by raphael on 5/27/15.
 * <p>
 * GuiWindow Class -- extends Window (Lanterna)
 * <p>
 * Houses all functions for Gui functionality, also houses common buttons and information for said Windows.
 */
public class GUIWindow extends Window {

    private String uiUser;
    private String uiPassword;

    Panel horizontalPanel, leftPanel, rightPanel, middlePanel;
    ErrorHandler eh;

    GUIScreen guiScreen;
    FlightsClient flc;
    DataLoader dl = null;
    Buttons buttons = new Buttons();
    Button quit;

    /**
     * Constructor for GUIWindow, creates gui framework, and allows for modulation.
     *
     * @param title String
     * @param flc FlightsClient
     */
    public GUIWindow(String title, FlightsClient flc, GUIScreen guiScreen) {
        super(title);

        this.flc = flc;
        this.guiScreen = guiScreen;

        horizontalPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
        leftPanel = new Panel(new Border.Invisible(), Panel.Orientation.VERTICAL);
        middlePanel = new Panel(new Border.Invisible(), Panel.Orientation.VERTICAL);
        rightPanel = new Panel(new Border.Invisible(), Panel.Orientation.VERTICAL);



        horizontalPanel.addComponent(leftPanel);
        horizontalPanel.addComponent(middlePanel);
        horizontalPanel.addComponent(rightPanel);
        addComponent(horizontalPanel);
    }

    public List<TripOption> attemptTransfer(String[] input)
            throws IllegalAccessException, InstantiationException, GoogleJsonResponseException, NullPointerException {
        return sendToGoogle(input);
    }

    public String setFlightsClient(String[] input, TextBox destinationBox, TextBox departureLocationBox,
                                   TextBox dateOfDepartureBox, TextBox passengerBox) {
        input[0] = destinationBox.getText();
        input[1] = departureLocationBox.getText();
        input[2] = dateOfDepartureBox.getText();
        input[3] = passengerBox.getText();

        flc.setDateOfDeparture(input[2]);
        flc.setDepartureIATA(input[1]);
        flc.setArrivalIATA(input[0]);
        flc.setPassengers(input[3]);

        String date = flc.getDateOfDeparture();

        return date;
    }

    private void drawLoadingWindow(GUIWindow guiPane, GUIScreen guiScreen) {
        guiPane.horizontalPanel.addComponent(new Label("LOADING..."));
    }


    /**
     * drawGuiError() - method
     *
     * draws and creates an error message in case of date of departure exception
     *
     * @param guiError GuiWindow
     * @param guiScreen GUIScreen
     */
    public void drawGuiError(GUIWindow guiError, GUIScreen guiScreen) {
        guiError.addComponent(new Label("An Error has Occurred. Please check inputs and try again", Terminal.Color.RED));
        guiError.addComponent(new Button("OK", () ->{
            LanternaHandler lanternaHandler = new LanternaHandler();
            guiScreen.getScreen().stopScreen();
            try {
                lanternaHandler.LanternaTerminal(new FlightsClient(null, null, null, null));
            } catch (GoogleJsonResponseException e) {
                e.printStackTrace();
            }

        }));
        guiScreen.showWindow(guiError, CENTER);
    }

    /**
     * dateTester() - method
     *
     * returns true if date entered is after today's date.
     *
     * @param dateOfDepart String
     * @return boolean
     * @throws ParseException
     */
    public boolean dateTester(String dateOfDepart) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        sdf.format(currentDate);

        boolean result = currentDate.before(sdf.parse(dateOfDepart));
        System.out.println(result);
        return result;
    }

    /**
     * formatToScreen() - method
     * <p>
     * private functions that mimics the same method as displayValues() to be modified to allow for it to be displayed
     * in the Lanterna GUI. See UIInterface.displayValues() for more information.
     *
     * @param tripOptions  List
     * @param tripData     List
     * @param aircraftData List
     * @param carrierData  List
     * @param airportData  List
     * @param results      TextArea
     */
    public void formatToScreen(List<TripOption> tripOptions, List<CityData> tripData, List<AircraftData> aircraftData,
                               List<CarrierData> carrierData, List<AirportData> airportData, TextArea results) {
        DecimalFormat df = new DecimalFormat("#.##");
        dl = new DataLoader();

        for (int i = 0; i < tripOptions.size(); i++) {
            List<SliceInfo> sliceInfo = dl.getID(i, tripOptions, results);
            for (int j = 0; j < sliceInfo.size(); j++) {
                List<SegmentInfo> segInfo = dl.getSliceInfo(j, sliceInfo, results, df);
                for (int k = 0; k < segInfo.size(); k++) {
                    List<LegInfo> leg = dl.getFlightInfo(segInfo, k, carrierData, results);
                    for (int l = 0; l < leg.size(); l++) {
                        List<String> legInfo = dl.getLegInfo(leg, l, aircraftData, this);

                        String origin = leg.get(l).getOrigin();
                        String destination = leg.get(l).getDestination();

                        for (int n = 0; n < airportData.size(); n++) {
                            origin = dl.getOriginName(airportData, n, origin, tripData);
                        }
                        for (int o = 0; o < airportData.size(); o++) {
                            destination = dl.getDestinationName(airportData, destination, o, tripData);
                        }

                        int durationLeg = leg.get(l).getDuration();
                        double durationLeginHours = durationLeg / 60;

                        dl.printToGui(results, df, durationLeginHours, legInfo, origin, destination);
                    }
                }
            }
            dl.getPricingInfo(tripOptions, results, i);
        }
    }

    /**
     * sendToGoogle() method
     * <p>
     * sends information to QPX Express from gui.
     *
     * @param input String[]
     * */
    private List<TripOption> sendToGoogle(String[] input) throws IllegalAccessException, InstantiationException, GoogleJsonResponseException {
        //connection established with doAction()
        //System.out.println(input[1]);

        //sending firstPage Data to googleCommunicate...
        List<TripOption> tripResults = null;
        tripResults = flc.googleCommunicate(input);
        //String textValues = UIInterface.displayValues(tripResults, flc.tripData, flc.aircraftData, flc.carrierData, flc.airportData);
        return tripResults;

    }

    /**
     * drawPage() method
     * <p>
     * opens up new window for data stream from QPX
     *  @param guiOutput GUIWindow
     * @param results   TextArea
     * @param guiLoad
     */
    public void drawPage(GUIWindow guiOutput, TextArea results, GUIScreen guiScreen, GUIWindow guiLoad) {
        final int[] flightSelection = new int[]{0};
        final String[] input = new String[3];

        buttons = new Buttons();
        TextBox flightNo = new TextBox(null, 10);
        UIInterface ui = new UIInterface();
        GUIWindow returningFlight = new GUIWindow("Return Flight", flc, guiScreen);
        TextArea returnFlightResults = new TextArea();


        guiOutput.buttons.backButton(guiScreen, guiOutput);
        guiOutput.buttons.quitButton(guiOutput);
        guiOutput.horizontalPanel.addComponent(results);
        guiOutput.leftPanel.addComponent(new Label("Choose Flight No."));
        guiOutput.leftPanel.addComponent(flightNo);
        guiOutput.leftPanel.addComponent(new Button("Enter", new Action() {
            @Override
            public void doAction() {
                //collect text from textbox to select flight requested.
                flightSelection[0] = Integer.parseInt(flightNo.getText());
                input[2] = flc.getDateOfDeparture();
                input[0] = flc.getDepartureIATA();
                input[1 ] = flc.getArrivalIATA();

                List<TripOption> options;
                TripOption choice;

                options = flc.getTripResults();

                choice = options.get(flightSelection[0] - 1);
                flc.setOutboundFlightChoice(choice);

                /*Thread thread = new Thread(){
                    public void run(){
                        System.out.println("thread running");
                        guiLoad.horizontalPanel.addComponent(new Label("Loading...", Terminal.Color.BLACK, true));
                        guiLoad.guiScreen.showWindow(guiLoad, GUIScreen.Position.CENTER);
                    }
                };   thread.start();
                */

                try {
                    flc.googleCommunicate(input);

                    returningFlight.leftPanel.addComponent(new Button("Enter", new Action() {
                        @Override
                        public void doAction() {

                        }
                    }));
                    returnFlightResults.appendLine(ui.displayValues(options, flc.tripData, flc.aircraftData, flc.carrierData, flc.airportData));

                    returningFlight.leftPanel.addComponent(flightNo);
                    returningFlight.buttons.backButton(guiScreen, guiOutput);
                    returningFlight.buttons.quitButton(guiOutput);
                    returningFlight.horizontalPanel.addComponent(returnFlightResults);

                } catch (IllegalAccessException | GoogleJsonResponseException | InstantiationException e) {
                    e.printStackTrace();
                }



            }
        }));
        //results.appendLine(textValues);
    }


}

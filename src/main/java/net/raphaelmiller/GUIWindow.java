package net.raphaelmiller;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.qpxExpress.model.*;
import com.googlecode.lanterna.gui.Border;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.*;
import com.googlecode.lanterna.terminal.Terminal;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
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
    LanternaHandler lanternaHandler = new LanternaHandler();

    GUIScreen guiScreen;
    FlightsClient flc;
    DataLoader dl = null;
    Buttons buttons = new Buttons();


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

    public List<TripOption> attemptTransfer(String[] input, String returnDate)
            throws IllegalAccessException, InstantiationException, GoogleJsonResponseException, NullPointerException {
        return sendToGoogle(input, returnDate);
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
     *  @param input String[] */
    private List<TripOption> sendToGoogle(String[] input, String date) throws IllegalAccessException, InstantiationException, GoogleJsonResponseException {
        //connection established with doAction()
        //System.out.println(input[1]);

        //sending firstPage Data to googleCommunicate...
        List<TripOption> tripResults = null;
        tripResults = flc.googleCommunicate(input, date);
        //String textValues = UIInterface.displayValues(tripResults, flc.tripData, flc.aircraftData, flc.carrierData, flc.airportData);
        return tripResults;

    }

    /**
     * drawPage() method
     * <p>
     * opens up new window for data stream from QPX
     * @param results       TextArea
     * @param tripOptions   List
     * @param guiWindows
     * @param boxes
     */
    public void drawPage(TextArea results, List<TripOption> tripOptions, LanternaHandler guiWindows, LanternaHandler boxes) {

        buttons = new Buttons();
        TextBox flightNo = new TextBox(null, 10);

        GUIWindow outboundWindow = guiWindows.getGuiOutboundFlight();
        GUIWindow inboundWindow = guiWindows.getGuiInboundFlight();
        GUIWindow loadingWindow = guiWindows.getGuiLoad();
        GUIWindow itinerary = guiWindows.getGuiItenerary();

        TextBox returnDate = boxes.getDateOfReturnBox();
        TextBox flightChoice = boxes.getOutboundFlightChoiceBox();

        TextArea resultsArea = boxes.getResultsArea();

        drawGuiOutbound(tripOptions, guiWindows, boxes);

    }


    /**
     * @param tripOptions
     * @param guiWindows
     * @param boxes
     */
    private void drawGuiOutbound(List<TripOption> tripOptions, LanternaHandler guiWindows, LanternaHandler boxes) {

        String outbound = flc.getArrivalIATA();
        String inbound = flc.getDepartureIATA();
        String date = flc.getDateOfDeparture();

        GUIWindow inboundWindow = guiWindows.getGuiInboundFlight();
        GUIWindow outBoundWindow = guiWindows.getGuiOutboundFlight();
        GUIWindow loadingWindow = guiWindows.getGuiLoad();
        GUIWindow itinerary = guiWindows.getGuiItenerary();

        GUIScreen screen = guiWindows.getGuiScreen();

        TextBox returnDate = boxes.getDateOfReturnBox();
        TextBox flightChoice = boxes.getOutboundFlightChoiceBox();
        TextArea resultsArea = boxes.getResultsArea();

        outBoundWindow.buttons.guiOutputEnterButton(outbound, inbound, date,
                tripOptions, guiWindows, boxes);
        outBoundWindow.buttons.backButton(screen, outBoundWindow);
        outBoundWindow.buttons.quitButton(outBoundWindow);
        outBoundWindow.horizontalPanel.addComponent(resultsArea);
        outBoundWindow.leftPanel.addComponent(new Label("Choose Flight No."));
        outBoundWindow.leftPanel.addComponent(flightChoice);
    }

    /**
     * @param tripOption
     * @param flightChoiceInbound
     * @param guiWindows
     * @param boxes
     */
    public void drawGuiInbound(List<TripOption> tripOption, TripOption flightChoiceInbound, LanternaHandler guiWindows,
                               LanternaHandler boxes){

        GUIWindow outboundWindow = guiWindows.getGuiOutboundFlight();
        GUIWindow inboundWindow = guiWindows.getGuiInboundFlight();
        GUIWindow loadingWindow = guiWindows.getGuiLoad();
        GUIWindow itinerary = guiWindows.getGuiItenerary();

        GUIScreen screen = guiWindows.getGuiScreen();

        TextBox flightChoice = boxes.getInboundFlightChoiceBox();
        TextArea resultArea = boxes.getResultsArea();



        inboundWindow.formatToScreen(tripOption, inboundWindow.flc.getTripData(), inboundWindow.flc.getAircraftData(),
                inboundWindow.flc.getCarrierData(), inboundWindow.flc.airportData, resultArea);
        inboundWindow.buttons.guiInboundEnterButton(flightChoiceInbound, tripOption, guiWindows, boxes );
        inboundWindow.buttons.backButton(screen, inboundWindow);
        inboundWindow.buttons.quitButton(inboundWindow);
        inboundWindow.horizontalPanel.addComponent(resultArea);
        inboundWindow.leftPanel.addComponent(new Label("Choose Flight No."));
        inboundWindow.leftPanel.addComponent(flightChoice);
        inboundWindow.guiScreen.showWindow(inboundWindow, GUIScreen.Position.FULL_SCREEN);

    }

    /**
     * @param flightChoiceInbound
     * @param flightChoiceOutBound
     * @param guiWindows
     * @param boxes
     */
    public void drawGuiItinerary(TripOption flightChoiceInbound, TripOption flightChoiceOutBound,
                                 LanternaHandler guiWindows, LanternaHandler boxes){



        DataLoader dl = new DataLoader();
        List<TripOption> outbound = new ArrayList<>();

        GUIWindow itinerary = guiWindows.getGuiItenerary();
        GUIWindow outboundWindow = guiWindows.getGuiOutboundFlight();
        GUIWindow inboundWindow = guiWindows.getGuiInboundFlight();

        TextArea resultsArea = boxes.getResultsArea();


        List<CityData> outBoundCityData = outboundWindow.flc.getOutboundCityData();
        List<CarrierData> outboundCarrierData = outboundWindow.flc.getOutBoundCarrierData();
        List<AircraftData> outBoundAircraftData = outboundWindow.flc.getOutBoundAircraftData();
        List<AirportData> outBoundAirportData = outboundWindow.flc.getOutboundAirportData();

        List<CityData> inboundCityData = inboundWindow.flc.getInboundCityData();
        List<CarrierData> inboundCarrierData = inboundWindow.flc.getInboundCarrierData();
        List<AircraftData> inboundAircraftData = inboundWindow.flc.getInboundAircraftData();
        List<AirportData> inboundAirportData = inboundWindow.flc.getInboundAirportData();

        List<CityData> jointCityData = new ArrayList<>();
        List<CarrierData> jointCarrierData = new ArrayList<>();
        List<AircraftData> jointAircraftData = new ArrayList<>();
        List<AirportData> jointAirportData = new ArrayList<>();

        System.out.println(inboundCityData);
        System.out.println(outBoundCityData);

        jointCityData.addAll(inboundCityData);
        jointCityData.addAll(outBoundCityData);
        jointCarrierData.addAll(inboundCarrierData);
        jointCarrierData.addAll(outboundCarrierData);
        jointAircraftData.addAll(inboundAircraftData);
        jointAircraftData.addAll(outBoundAircraftData);
        jointAirportData.addAll(inboundAirportData);
        jointAirportData.addAll(outBoundAirportData);

        outbound.add(0, flightChoiceInbound);
        outbound.add(1, flightChoiceOutBound);


        itinerary.formatToScreen(outbound, jointCityData, jointAircraftData, jointCarrierData, jointAirportData,
                resultsArea);
        resultsArea.insertLine(100, "hello");   
        itinerary.buttons.quitButton(itinerary);
        itinerary.horizontalPanel.addComponent(resultsArea);
        itinerary.guiScreen.showWindow(itinerary, GUIScreen.Position.FULL_SCREEN);

    }


    /**
     * drawGuiInput() method
     *
     * method creating the positions and order for the gui
     * @param guiScreen         GUIScreen
     * @param lh
     * @param guiWindows
     */
    public void drawGuiInput(GUIScreen guiScreen, LanternaHandler lh, LanternaHandler guiWindows)
            throws GoogleJsonResponseException {
        //objects used for input capture

        lanternaHandler.setDateOfDepartureBox(new TextBox(null, 125));
        lanternaHandler.setDepartureLocationBox(new TextBox(null, 125));
        lanternaHandler.setDestinationBox(new TextBox(null, 125));
        lanternaHandler.setPassengerBox(new TextBox(null, 100));
        lanternaHandler.setDateOfReturnBox(new TextBox(null, 125));

        TextBox passengers = lh.getPassengerBox();
        TextBox departure = lh.getDepartureLocationBox();
        TextBox destination = lh.getDestinationBox();
        TextBox departureDestination = lh.getDateOfDepartureBox();
        TextBox returnDateBox = lh.getDateOfReturnBox();

        ProgressBar progressBar = new ProgressBar(100);

        GUIWindow guiInputWindow = guiWindows.getGuiInput();

        //methods for panel drawing
        lanternaHandler.leftPanel(this, passengers, destination);
        lanternaHandler.middlePanel(this, departure);
        lanternaHandler.rightPanel(this, departureDestination, returnDateBox);
        lanternaHandler.buttons(guiWindows, progressBar, lh);

        guiScreen.showWindow(this, CENTER);
    }
}

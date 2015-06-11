package net.raphaelmiller;

import com.google.api.services.qpxExpress.model.*;
import com.googlecode.lanterna.gui.*;
import com.googlecode.lanterna.gui.component.*;
import com.googlecode.lanterna.gui.dialog.DialogButtons;
import com.googlecode.lanterna.gui.dialog.DialogResult;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.SGR;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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

    FlightsClient flc;
    Button quit;

    /**
     * Constructor for GUIWindow, creates gui framework, and allows for modulation.
     *
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
     * <p>
     * generic quit button that leaves the GUI and shuts down the program whenever pressed
     */
    public void quitButton() {
        addComponent(new Button("QUIT", () -> System.exit(0)));
    }

    /**
     * enterButton() method
     * <p>
     * Gives action to what happens when the enter button is pressed.
     *
     * @param guiScreen            GUIScreen
     * @param guiOutput            GUIWindow
     * @param destinationBox       TextBox
     * @param departureLocationBox TextBox
     * @param dateOfDepartureBox   TextBox
     * @param passengerBox         TextBox
     * @param progressBar          ProgressBar
     */
    public void enterButton(final GUIScreen guiScreen, final GUIWindow guiOutput, final TextBox destinationBox,
                            final TextBox departureLocationBox, final TextBox dateOfDepartureBox, TextBox passengerBox,
                            ProgressBar progressBar) {

        final TextArea results = new TextArea(new TerminalSize(400, 300), null);
        final String[] input = new String[4];
        final String[] textValues = {null};
        TextGraphics textGraphics = null;
        ErrorHandler eh = new ErrorHandler();


        // lambdas :)
        addComponent(new Button("ENTER", () -> {
            progressBar.setVisible(true);

            input[0] = destinationBox.getText();
            input[1] = departureLocationBox.getText();
            input[2] = dateOfDepartureBox.getText();
            input[3] = passengerBox.getText();

            flc.setDateOfDeparture(input[2]);
            flc.setDepartureIATA(input[1]);
            flc.setArrivalIATA(input[0]);
            flc.setPassengers(input[3]);

            //sends information to googleCommunicate() in FlightsClient...
            List<TripOption> tripOptions = sendToGoogle(input);

            formatToScreen(tripOptions, flc.tripData, flc.aircraftData, flc.carrierData, flc.airportData, results);

            //results.appendLine(textValues[0] + "\n");

            guiScreen.showWindow(guiOutput, GUIScreen.Position.FULL_SCREEN);
        }));

        drawPage(guiOutput, results, guiScreen);

        System.out.println(input[0]);
        //variable text area, modify to store data from display values
    }

    private boolean dateTester(String dateofDepart) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        String today = sdf.format(currentDate);

        boolean result = currentDate.before(sdf.parse(dateofDepart));
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
    private void formatToScreen(List<TripOption> tripOptions, List<CityData> tripData, List<AircraftData> aircraftData,
                                List<CarrierData> carrierData, List<AirportData> airportData, TextArea results) {
        DecimalFormat df = new DecimalFormat("#.##");

        String id = null;
        for (int i = 0; i < tripOptions.size(); i++) {
            List<SliceInfo> sliceInfo = getID(id, i, tripOptions, results);
            for (int j = 0; j < sliceInfo.size(); j++) {
                List<SegmentInfo> segInfo = getSliceInfo(j, sliceInfo, results, df);
                for (int k = 0; k < segInfo.size(); k++) {
                    List<LegInfo> leg = getFlightInfo(segInfo, k, carrierData, results);
                    for (int l = 0; l < leg.size(); l++) {
                        List<String> legInfo = getLegInfo(leg, l, airportData, tripData, aircraftData);

                        String origin = leg.get(l).getOrigin();
                        String destination = leg.get(l).getDestination();

                        for (int n = 0; n < airportData.size(); n++) {
                            origin = getOriginName(airportData, n, origin, tripData);
                        }
                        for (int o = 0; o < airportData.size(); o++) {
                            destination = getDestinationName(airportData, destination, o, tripData);
                        }

                        int durationLeg = leg.get(l).getDuration();
                        double durationLeginHours = durationLeg / 60;

                        printToGui(results, df, durationLeginHours, legInfo, origin, destination);
                    }
                }
            }
            getPricingInfo(tripOptions, results, i);
        }
    }

    /**
     * getPricingInfo
     * <p>
     * displays pricing info for total flight, prints results to GUI
     *
     * @param tripOptions List
     * @param results     TextArea
     * @param i           int
     */
    private void getPricingInfo(List<TripOption> tripOptions, TextArea results, int i) {
        List<PricingInfo> priceInfo = tripOptions.get(i).getPricing();
        for (int p = 0; p < priceInfo.size(); p++) {
            String price = priceInfo.get(p).getSaleTotal();
            results.appendLine("Price: " + price + "\n\n");
            results.appendLine("");
        }
    }

    /**
     * printToGui()
     * <p>
     * prints results to GUI
     *
     * @param results            TextArea
     * @param df                 DecimalFormat
     * @param durationLeginHours double
     * @param legInfo            List
     * @param origin             String
     * @param destination        String
     */
    private void printToGui(TextArea results, DecimalFormat df, double durationLeginHours, List<String> legInfo,
                            String origin, String destination) {
        results.appendLine("Leg Duration: " + df.format(durationLeginHours) + " hrs\n");
        results.appendLine("Aircraft \t\t\t Arrival \t\t\t\t\t Departure \t\t\t\t\t Meal?\n");
        results.appendLine(legInfo.get(0) + "\t\t\t" + legInfo.get(1) + "\t\t" + legInfo.get(2) + "\t\t" + legInfo.get(3) + "\n");
        results.appendLine("Leg: " + origin + " to\n " + destination + "\n");
    }

    /**
     * getLegInfo()
     * <p>
     * gets list from leg and converts to string list, ready to be displayed to GUI
     *
     * @param leg          List<LegInfo>
     * @param l            int
     * @param airportData  List<AirportData>
     * @param tripData     List<tripData>
     * @param aircraftData List<AircraftData>
     * @return result
     */
    private List<String> getLegInfo(List<LegInfo> leg, int l, List<AirportData> airportData, List<CityData> tripData,
                                    List<AircraftData> aircraftData) {
        List<String> result = new ArrayList<>();

        String aircraft = leg.get(l).getAircraft();
        aircraft = getAircraftname(aircraftData, aircraft);

        String arrivalTime = leg.get(l).getArrivalTime();
        String departureTime = leg.get(l).getDepartureTime();
        String meal = leg.get(l).getMeal();

        result.add(aircraft);
        result.add(arrivalTime);
        result.add(departureTime);
        result.add(meal);

        return result;
    }

    /**
     * getAircraftname()
     * <p>
     * compares aircraft data codes with aircraft on file in QPX in order to get a more user friendly version of the name
     *
     * @param aircraftData List<AircraftData>
     * @param aircraft     String
     * @return aircraft
     */
    private String getAircraftname(List<AircraftData> aircraftData, String aircraft) {
        for (int r = 0; r < aircraftData.size(); r++) {
            if (aircraftData.get(r).getCode().equals(aircraft)) {
                aircraft = aircraftData.get(r).getName();
            }
        }
        return aircraft;
    }

    /**
     * getDestinationNames()
     * <p>
     * gets IATA code for city destination and converts to full city name
     *
     * @param airportData List<AirportData>
     * @param destination String
     * @param o           int
     * @param tripData    List<CityData>
     * @return destination String
     */
    private String getDestinationName(List<AirportData> airportData, String destination, int o, List<CityData> tripData) {
        if (airportData.get(o).getCode().equals(destination)) {
            destination = airportData.get(o).getName();
            for (int p = 0; p < tripData.size(); p++) {
                if (tripData.get(p).getCode().equals(airportData.get(o).getCity())) {
                    destination = destination + ", " + tripData.get(p).getName();
                }
            }
        }
        return destination;
    }

    /**
     * getOriginName()
     * <p>
     * gets IATA code for origin city and converts it to full name for city.
     *
     * @param airportData List<AirportData>
     * @param n           int
     * @param origin      String
     * @param tripData    List<CityData>
     * @return origin
     */
    private String getOriginName(List<AirportData> airportData, int n, String origin, List<CityData> tripData) {
        if (airportData.get(n).getCode().equals(origin)) {
            origin = airportData.get(n).getName();
            for (int q = 0; q < tripData.size(); q++) {
                if (tripData.get(q).getCode().equals(airportData.get(n).getCity())) {
                    origin = origin + ", " + tripData.get(q).getName();
                }
            }
        }
        return origin;
    }

    /**
     * getFlightInfo()
     * <p>
     * gets flight info, checks flight IATA code and compares to Carrier name from QPX, converts to user friendly name
     * returns rest of leg information.
     *
     * @param segInfo     List<SegmentInfo>
     * @param k           int
     * @param carrierData List<CarrierData>
     * @param results     TextArea
     * @return leg
     */
    private List<LegInfo> getFlightInfo(List<SegmentInfo> segInfo, int k, List<CarrierData> carrierData, TextArea results) {
        FlightInfo flightInfo = segInfo.get(k).getFlight();
        String flightCarr = flightInfo.getCarrier();
        String flightNum = flightInfo.getNumber();
        for (int m = 0; m < carrierData.size(); m++) {
            if (carrierData.get(m).getCode().equals(flightCarr)) {
                flightCarr = carrierData.get(m).getName();
            }
        }
        results.appendLine("Carrier: " + flightCarr + "\t Flight No: " + flightNum);
        List<LegInfo> leg = segInfo.get(k).getLeg();
        return leg;
    }

    /**
     * getSliceInfo
     * <p>
     * sets up slice info for parse, gets total duration of flight, returns seg info for rest of chain
     *
     * @param j         int
     * @param sliceInfo List<SliceInfo>
     * @param results   TextArea
     * @param df        DecimalFormat
     * @return segInfo
     */
    private List<SegmentInfo> getSliceInfo(int j, List<SliceInfo> sliceInfo, TextArea results, DecimalFormat df) {
        int duration = sliceInfo.get(j).getDuration();
        double durationInHours = duration / 60;
        results.appendLine("Duration: " + df.format(durationInHours) + " hrs");
        List<SegmentInfo> segInfo = sliceInfo.get(j).getSegment();
        return segInfo;
    }

    /**
     * getID()
     * <p>
     * gets ID for flight number and prints to GUI, returns slice info for rest of chain.
     *
     * @param id          String
     * @param i           int
     * @param tripOptions List<tripOption>
     * @param results     TextArea
     * @return sliceInfo
     */
    private List<SliceInfo> getID(String id, int i, List<TripOption> tripOptions, TextArea results) {
        id = tripOptions.get(i).getId();
        results.appendLine(id);
        List<SliceInfo> sliceInfo = tripOptions.get(i).getSlice();
        return sliceInfo;
    }

    /**
     * sendToGoogle() method
     * <p>
     * sends information to QPX Express from gui.
     *
     * @param input String[]
     * @return tripResults
     */
    private List<TripOption> sendToGoogle(String[] input) {
        //connection established with doAction()
        //System.out.println(input[1]);

        //sending firstPage Data to googleCommunicate...
        List<TripOption> tripResults = flc.googleCommunicate(input);

        //String textValues = UIInterface.displayValues(tripResults, flc.tripData, flc.aircraftData, flc.carrierData, flc.airportData);
        return tripResults;

    }

    /**
     * drawPage() method
     * <p>
     * opens up new window for data stream from QPX
     *
     * @param guiOutput GUIWindow
     * @param results   TextArea
     */
    private void drawPage(GUIWindow guiOutput, TextArea results, GUIScreen guiScreen) {

        guiOutput.backButton(guiScreen);
        guiOutput.quitButton();
        guiOutput.horizontalPanel.addComponent(results);
        //results.appendLine(textValues);
    }

    private void backButton(GUIScreen guiScreen) {
        addComponent(new Button("BACK", () -> {
            LanternaHandler lanternaHandler = new LanternaHandler();
            guiScreen.getScreen().stopScreen();
            lanternaHandler.LanternaTerminal(new FlightsClient(null, null, null, null));
        }));
    }


}

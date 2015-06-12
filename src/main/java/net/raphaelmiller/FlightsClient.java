package net.raphaelmiller;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.qpxExpress.QPXExpress;
import com.google.api.services.qpxExpress.QPXExpressRequestInitializer;
import com.google.api.services.qpxExpress.model.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class FlightsClient {

    //Constants for google developers console
    private static final String APPLICATION_NAME = "Airline";
    private static final String API_KEY = "AIzaSyDBglpQwcHNv7KuZu1CChuzaCdJr0sO-V4";

    //Global Instance of HTTP Transport
    private static HttpTransport httpTransport;

    //global instance of JSON factory
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    //user manipulated objects
    private String arrivalIATA;
    private String departureIATA;
    private String dateOfDeparture;
    private String passengers;

    //QPX Express API defined lists
    List<CityData> tripData = null;
    List<AircraftData> aircraftData = null;
    List<CarrierData> carrierData = null;
    List<AirportData> airportData = null;
    List<TripOption> tripResults = null;

    //Main Class Constructor
    public FlightsClient(String arrivalIATA, String departureIATA, String dateOfDeparture, String passengers) {
        this.arrivalIATA = arrivalIATA;
        this.departureIATA = departureIATA;
        this.dateOfDeparture = dateOfDeparture;
        this.passengers = passengers;
    }

    /**
     * main method - starts program and maintains program chronological structure.
     *
     * psuedo code statements:
     *      declare necessary class objects (Main class, UIInterface class)
     *      send program to UImain method (see UImain())
     *      send program to displayValues method (see displayValues())
     * @param args
     */
    public static void main(String[] args) {
	// write your code here
        FlightsClient flc = new FlightsClient(null, null, null, null);
        UIInterface ui = new UIInterface();
        LanternaHandler lh = new LanternaHandler();


        //sends information to GUI, acts as main class (to add threading later...)
        lh.LanternaTerminal(flc);
        //go to UIInterface -> UImain() method.
        ui.UImain(flc);
        //System.out.println(main.getArrivalIATA() + "\n" + main.getDateOfDeparture() + "\n" + main.getDepartureIATA());




    } //end of main


    /**
     * googleCommunicate() method -
     * method call uses OPX Express objects and api.clent objects to create a connection for QPX. Additionally, method
     * creates a json element to send to QPX Express and returns List<TripOption> in order to collect data for program.
     *
     * accepts a String array, and returns a List<TripOption>
     *
     * @return List <TripOption> tripResults
     *
     * @param input    */
    public List<TripOption> googleCommunicate(String[] input) {


        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            //sends a generic httptransport to QPX API

            int numberOfPassengers = 1;

            PassengerCounts passengers = new PassengerCounts();
            passengers.setAdultCount(numberOfPassengers);
            //sets passengers for passenger method to 1 adult, change here if you want to change number of travellers

            List<SliceInput> slices = new ArrayList<SliceInput>();
            //List<CityData> cities = new ArrayList<CityData>();
            //array list that sends information requested to google.

            //String origin = getArrivalIATA();
            //String destination = getDepartureIATA();
            //String date = getDateOfDeparture();
            //user input

            SliceInput slice = new SliceInput();
            slice.setOrigin(input[1]);
            slice.setDestination(input[0]);
            slice.setDate(input[2]);
            slices.add(slice);

            //former data sent in from UIInterface.UIMain method, used for raw terminal construction...
            //slice.setOrigin(origin);
            //slice.setDestination(destination);
            //slice.setDate(date);


            //list slice I am assuming sends that information and prepares to format data requested to JSON

            TripOptionsRequest request = new TripOptionsRequest();
            request.setSolutions(10);
            request.setPassengers(passengers);
            request.setSlice(slices);

            //trip "package" bringing last three blocks together to form a request to QPX

            TripsSearchRequest parameters = new TripsSearchRequest();
            parameters.setRequest(request);
            QPXExpress.Builder qpxBuilder = new QPXExpress.Builder(httpTransport, JSON_FACTORY, null);
            qpxBuilder.setApplicationName(APPLICATION_NAME);
            QPXExpress qpxExpress = qpxBuilder.setGoogleClientRequestInitializer(new QPXExpressRequestInitializer(API_KEY)).build();
            System.out.println("sending request to QPX Express");

            //builds the parameters for the search request and sends that information to QPX API via QPXExpress class

            TripsSearchResponse list = qpxExpress.trips().search(parameters).execute();
            //gets the response from qpx api
            tripResults = list.getTrips().getTripOption();
            tripData = list.getTrips().getData().getCity();
            aircraftData = list.getTrips().getData().getAircraft();
            carrierData = list.getTrips().getData().getCarrier();
            airportData = list.getTrips().getData().getAirport();
            //gets trip options to list.

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tripResults;
    }


    //getters and setters for main class. ----------------------
    public String getArrivalIATA() {
        return arrivalIATA;
    }

    public void setArrivalIATA(String arrivalIATA) {
        this.arrivalIATA = arrivalIATA;
    }

    public String getDepartureIATA() {
        return departureIATA;
    }

    public void setDepartureIATA(String departureIATA) {
        this.departureIATA = departureIATA;
    }

    public String getDateOfDeparture() {
        return dateOfDeparture;
    }

    public void setDateOfDeparture(String dateOfDeparture) {
        this.dateOfDeparture = dateOfDeparture;
    }

    public String getPassengers() {
        return passengers;
    }

    public void setPassengers(String passengers) {
        this.passengers = passengers;
    }
}

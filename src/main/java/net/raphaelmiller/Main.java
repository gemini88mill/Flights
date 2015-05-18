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

public class Main {

    private static final String APPLICATION_NAME = "Airline";
    private static final String API_KEY = "AIzaSyDBglpQwcHNv7KuZu1CChuzaCdJr0sO-V4";

    //Global Instance of HTTP Transport
    private static HttpTransport httpTransport;

    //global instance of JSON factory
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private String arrivalIATA;
    private String departureIATA;
    private String dateOfDeparture;

    public Main(String arrivalIATA, String departureIATA, String dateOfDeparture) {
        this.arrivalIATA = arrivalIATA;
        this.departureIATA = departureIATA;
        this.dateOfDeparture = dateOfDeparture;
    }



    public static void main(String[] args) {
	// write your code here
        Main main = new Main(null, null, null);
        UIInterface ui = new UIInterface();

        ui.UImain(main);
        //System.out.println(main.getArrivalIATA() + "\n" + main.getDateOfDeparture() + "\n" + main.getDepartureIATA());


        List<TripOption> tripOption = main.googleCommunicate();
        UIInterface.displayValues(tripOption);


    }



    private List<TripOption> googleCommunicate() {
        List<TripOption> tripResults = null;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            //sends a generic httptransport to QPX API

            PassengerCounts passengers = new PassengerCounts();
            passengers.setAdultCount(1);
            //sets passengers for passenger method to 1 adult, change here if you want to change number of travellers

            List<SliceInput> slices = new ArrayList<SliceInput>();
            //array list that sends information requested to google.

            String origin = getArrivalIATA();
            String destination = getDepartureIATA();
            String date = getDateOfDeparture();
            //user input

            SliceInput slice = new SliceInput();
            slice.setOrigin(origin);
            slice.setDestination(destination);
            slice.setDate(date);
            slices.add(slice);
            //list slice I am assuming sends that information and prepares to format data requested to JSON

            TripOptionsRequest request = new TripOptionsRequest();
            request.setSolutions(10);
            request.setPassengers(passengers);
            request.setSlice(slices);
            //trip "package" bringing last three blocks together to form a request to QPX

            TripsSearchRequest parameters = new TripsSearchRequest();
            parameters.setRequest(request);
            QPXExpress qpxExpress = new QPXExpress.Builder(httpTransport, JSON_FACTORY, null)
                    .setGoogleClientRequestInitializer(new QPXExpressRequestInitializer(API_KEY)).build();
            //builds the parameters for the search request and sends that information to QPX API via QPXExpress class

            TripsSearchResponse list = qpxExpress.trips().search(parameters).execute();
            //gets the response from qpx api
            tripResults = list.getTrips().getTripOption();
            //gets trip options to list.


        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tripResults;
    }

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
}

package net.raphaelmiller;

import com.google.api.services.qpxExpress.model.*;

import java.util.List;
import java.util.Scanner;

/**
 * UIInterface Class - all methods within clas are used to use basic I/O functions for the user.
 * UImain - main method that asks for value from user
 * userInput - simple input interface for the user
 * displayValues - displays values for user
 * Created by raphael on 5/17/15.
 */
public class UIInterface {

    private final int IATA_LENGTH = 3;

    /**
     * UImain() - asks user for 3 strings to input for QPX json
     * @param main
     */
    public void UImain(Main main){

        String[] userResponse = new String[3];

        System.out.println("Enter departure location:(IATA Code)");
        userResponse[1] = userInput();
        System.out.println("Enter Arrival Location(IATA Code)");
        userResponse[0] = userInput();
        System.out.println("Enter Date of Departure:(YYYY-MM-DD)");
        userResponse[2] = userInput();

        main.setDateOfDeparture(userResponse[2].toUpperCase());
        main.setArrivalIATA(userResponse[1].toUpperCase());
        main.setDepartureIATA(userResponse[0].toUpperCase());

    }

    /**
     * userInput() - simple input method that accepts scanner object and sends data back through call stack
     * @return scan.nextLine (String)
     */
    private String userInput(){
        Scanner scan = new Scanner(System.in);
        return scan.nextLine();
    }

    /**
     * uses List <TripOption> and parses data collect in form viewable to user. (to be modified later for better UI).
     * @param tripResults
     */
    public static void displayValues(List<TripOption> tripResults) {
        String id;

        for (int i = 0; i < tripResults.size(); i++) {
            //Trip Option ID
            id = tripResults.get(i).getId();
            System.out.println("id " + id);
            List<SliceInfo> sliceInfo = tripResults.get(i).getSlice();
            for (int j = 0; j < sliceInfo.size(); j++){
                int duration = sliceInfo.get(j).getDuration();
                System.out.print("Duration: " + duration + " mins\n");
                List<SegmentInfo> segInfo = sliceInfo.get(j).getSegment();
                for(int k = 0; k < segInfo.size(); k++){
                    FlightInfo flightInfo = segInfo.get(k).getFlight();
                    String flightCarr = flightInfo.getCarrier();
                    String flightNum = flightInfo.getNumber();
                    System.out.println("Carrier: " + flightCarr + "\t Flight No: " + flightNum);
                    List<LegInfo> leg = segInfo.get(k).getLeg();
                    for (int l = 0; l < leg.size(); l++){
                        String aircraft = leg.get(l).getAircraft();
                        String arrivalTime = leg.get(l).getArrivalTime();
                        String departureTime = leg.get(l).getDepartureTime();
                        String meal = leg.get(l).getMeal();

                        String origin = leg.get(l).getOrigin();
                        String destination = leg.get(l).getDestination();


                        int durationLeg = leg.get(l).getDuration();

                        System.out.print("Leg Duration: " + durationLeg + " mins\n");
                        System.out.print("Aircraft \t\t Arrival \t\t\t Depart \t\t\t\t\t Meal?\n");
                        System.out.print(aircraft + "\t\t\t" + arrivalTime + "\t\t" + departureTime + "\t\t" + meal + "\n" );
                        System.out.println("Leg: " + origin + " to " + destination);

                    }
                }
            }
            List<PricingInfo> priceInfo = tripResults.get(i).getPricing();
            for (int p = 0; p < priceInfo.size(); p++) {
                String price = priceInfo.get(p).getSaleTotal();
                System.out.println("Price " + price + "\n\n");
            }


        }


    }
}

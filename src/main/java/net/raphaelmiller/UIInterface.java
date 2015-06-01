package net.raphaelmiller;

import com.google.api.services.qpxExpress.model.*;


import java.text.DecimalFormat;
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
    private static final int HR_CONVERT = 60;

    /**
     * UImain() - asks user for 3 strings to input for QPX json
     * @param flightsClient
     */
    public void UImain(FlightsClient flightsClient){

        String[] userResponse = new String[3];

        //region Input Fields
        System.out.println("Enter Departure location:(IATA Code)");
        userResponse[1] = userInput();
        System.out.println("Enter Arrival Location(IATA Code)");
        userResponse[0] = userInput();
        System.out.println("Enter Date of Departure:(YYYY-MM-DD)");
        userResponse[2] = userInput();
        //endregion

        flightsClient.setDateOfDeparture(userResponse[2].toUpperCase());
        flightsClient.setArrivalIATA(userResponse[1].toUpperCase());
        flightsClient.setDepartureIATA(userResponse[0].toUpperCase());

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
     * uses List <TripOption> and parses data collect in form viewable to user. Matches information from other QPX lists
     * gathered to give a more readable interpretation of data for the average user. tripData <CityData> containing city
     * information, aircraftData, carrierData (Airline), and airportData, for airports.
     * @param tripResults
     * @param tripData
     * @param aircraftData
     * @param carrierData
     * @param airportData
     */
    public static String displayValues(List<TripOption> tripResults, List<CityData> tripData, List<AircraftData>
            aircraftData, List<CarrierData> carrierData, List<AirportData> airportData) {
        String id;
        String result = null;

        DecimalFormat df = new DecimalFormat("#.##");

        for (int i = 0; i < tripResults.size(); i++) {
            //Trip Option ID
            id = tripResults.get(i).getId();
            System.out.println("id " + id);
            List<SliceInfo> sliceInfo = tripResults.get(i).getSlice();
            for (int j = 0; j < sliceInfo.size(); j++){
                int duration = sliceInfo.get(j).getDuration();
                double durationInHrs = duration / HR_CONVERT;
                System.out.print("Duration: " + df.format(durationInHrs) + " hrs\n");
                List<SegmentInfo> segInfo = sliceInfo.get(j).getSegment();
                for(int k = 0; k < segInfo.size(); k++){
                    FlightInfo flightInfo = segInfo.get(k).getFlight();
                    String flightCarr = flightInfo.getCarrier();
                    String flightNum = flightInfo.getNumber();

                    for (int m = 0; m < carrierData.size(); m++){
                        if(carrierData.get(m).getCode().equals(flightCarr)){
                            flightCarr = carrierData.get(m).getName();
                        }
                    }

                    System.out.println("Carrier: " + flightCarr + "\t Flight No: " + flightNum);
                    List<LegInfo> leg = segInfo.get(k).getLeg();
                    for (int l = 0; l < leg.size(); l++){
                        String aircraft = leg.get(l).getAircraft();
                        for (int r = 0; r < aircraftData.size(); r++){
                            if (aircraftData.get(r).getCode().equals(aircraft)){
                                aircraft = aircraftData.get(r).getName();
                            }
                        }
                        String arrivalTime = leg.get(l).getArrivalTime();
                        String departureTime = leg.get(l).getDepartureTime();
                        String meal = leg.get(l).getMeal();

                        String origin = leg.get(l).getOrigin();
                        String destination = leg.get(l).getDestination();

                        for (int n = 0; n < airportData.size(); n++){
                            if (airportData.get(n).getCode().equals(origin)){
                                origin = airportData.get(n).getName();
                                for (int q = 0; q < tripData.size(); q++){
                                    if(tripData.get(q).getCode().equals(airportData.get(n).getCity())){
                                        origin = origin + ", " + tripData.get(q).getName();
                                    }
                                }
                            }
                        }

                        for (int o = 0; o < airportData.size(); o++){
                            if (airportData.get(o).getCode().equals(destination)){
                                destination = airportData.get(o).getName();
                                for (int p = 0; p < tripData.size(); p++){
                                    if(tripData.get(p).getCode().equals(airportData.get(o).getCity())){
                                        destination = destination + ", " + tripData.get(p).getName();
                                    }
                                }
                            }
                        }

                        int durationLeg = leg.get(l).getDuration();
                        double durationLegInHrs = durationLeg / HR_CONVERT;



                        result = "Leg Duration: " + df.format(durationLegInHrs) + " hrs\n" + "Aircraft \t\t\t Arrival" +
                                " \t\t\t\t\t Departure \t\t\t\t\t Meal?\n" + aircraft + "\t\t\t" + arrivalTime + "\t\t"
                                + departureTime + "\t\t" + meal + "\n" + "Leg: " + origin + " to\n " + destination + "\n";


                        System.out.println(result);
                        //System.out.print("Leg Duration: " + df.format(durationLegInHrs) + " hrs\n");
                        //System.out.print("Aircraft \t\t\t Arrival \t\t\t\t\t Departure \t\t\t\t\t Meal?\n");
                        //System.out.print(aircraft + "\t\t\t" + arrivalTime + "\t\t" + departureTime + "\t\t" + meal + "\n" );
                        //System.out.println("Leg: " + origin + " to\n " + destination + "\n");
                    }
                }
            }
            List<PricingInfo> priceInfo = tripResults.get(i).getPricing();
            for (int p = 0; p < priceInfo.size(); p++) {
                String price = priceInfo.get(p).getSaleTotal();
                System.out.println("Price: " + price + "\n\n");
            }
        }

        return result;
    }

    /**
     * Method used to manipulate Laterna TextUI
     */

}

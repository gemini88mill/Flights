package net.raphaelmiller;

import com.google.api.services.qpxExpress.model.*;

import java.util.List;
import java.util.Scanner;

/**
 * Created by raphael on 5/17/15.
 */
public class UIInterface {

    private final int IATA_LENGTH = 3;

    public void UImain(Main main){

        String[] userResponse = new String[3];

        System.out.println("Enter departure location:(IATA Code)");
        userResponse[0] = userInput();
        System.out.println("Enter Arrival Location(IATA Code)");
        userResponse[1] = userInput();
        System.out.println("Enter Date of Departure:(YYYY-MM-DD)");
        userResponse[2] = userInput();

        main.setDateOfDeparture(userResponse[2].toUpperCase());
        main.setArrivalIATA(userResponse[1].toUpperCase());
        main.setDepartureIATA(userResponse[0].toUpperCase());

    }

    private String userInput(){
        Scanner scan = new Scanner(System.in);
        return scan.nextLine();
    }

    public static void displayValues(List<TripOption> tripResults) {
        String id;

        for (int i = 0; i < tripResults.size(); i++) {
            //Trip Option ID
            id = tripResults.get(i).getId();
            System.out.println("id " + id);

            //Slice
            List<SliceInfo> sliceInfo = tripResults.get(i).getSlice();
            for (int j = 0; j < sliceInfo.size(); j++) {
                int duration = sliceInfo.get(j).getDuration();
                System.out.println("duration " + duration);
                List<SegmentInfo> segInfo = sliceInfo.get(j).getSegment();
                for (int k = 0; k < segInfo.size(); k++) {
                    String bookingCode = segInfo.get(k).getBookingCode();
                    System.out.println("bookingCode " + bookingCode);
                    FlightInfo flightInfo = segInfo.get(k).getFlight();
                    String flightNum = flightInfo.getNumber();
                    System.out.println("flightNum " + flightNum);
                    String flightCarrier = flightInfo.getCarrier();
                    System.out.println("flightCarrier " + flightCarrier);
                    List<LegInfo> leg = segInfo.get(k).getLeg();
                    for (int l = 0; l < leg.size(); l++) {
                        String aircraft = leg.get(l).getAircraft();
                        System.out.println("aircraft " + aircraft);
                        String arrivalTime = leg.get(l).getArrivalTime();
                        System.out.println("arrivalTime " + arrivalTime);
                        String departTime = leg.get(l).getDepartureTime();
                        System.out.println("departTime " + departTime);
                        String dest = leg.get(l).getDestination();
                        System.out.println("Destination " + dest);
                        String destTer = leg.get(l).getDestinationTerminal();
                        System.out.println("DestTer " + destTer);
                        String origin = leg.get(l).getOrigin();
                        System.out.println("origun " + origin);
                        String originTer = leg.get(l).getOriginTerminal();
                        System.out.println("OriginTer " + originTer);
                        int durationLeg = leg.get(l).getDuration();
                        System.out.println("durationleg " + durationLeg);
                        int mil = leg.get(l).getMileage();
                        System.out.println("Milleage " + mil);

                    }
                }
            }
            List<PricingInfo> priceInfo = tripResults.get(i).getPricing();
            for (int p = 0; p < priceInfo.size(); p++) {
                String price = priceInfo.get(p).getSaleTotal();
                System.out.println("Price " + price);
            }
        }


    }
}

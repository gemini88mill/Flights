package net.raphaelmiller;

import com.google.api.services.qpxExpress.model.*;
import com.googlecode.lanterna.gui.component.TextArea;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by raphael on 6/26/15.
 */
public class DataLoader {

    private TextArea results;

    /**
     * printToGui()
     * <p>
     * prints results to GUI
     *  @param results            TextArea
     * @param df                 DecimalFormat
     * @param durationLegInHours double
     * @param legInfo            List
     * @param origin             String
     * @param destination        String
     */
    void printToGui(TextArea results, DecimalFormat df, double durationLegInHours, List<String> legInfo,
                    String origin, String destination) {
        results.appendLine("Leg Duration: " + df.format(durationLegInHours) + " hrs\n");
        results.appendLine("Aircraft \t\t\t Arrival \t\t\t\t\t Departure \t\t\t\t\t Meal?\n");
        results.appendLine(legInfo.get(0) + "\t\t\t" + legInfo.get(1) + "\t\t" + legInfo.get(2) + "\t\t" + legInfo.get(3) + "\n");
        results.appendLine("Leg: " + origin + " to\n " + destination + "\n");
    }

    /**
     * getPricingInfo
     * <p>
     * displays pricing info for total flight, prints results to GUI
     *  @param tripOptions List
     * @param results     TextArea
     * @param i           int
     */
    void getPricingInfo(List<TripOption> tripOptions, TextArea results, int i) {
        List<PricingInfo> priceInfo = tripOptions.get(i).getPricing();
        for (PricingInfo aPriceInfo : priceInfo) {
            String price = aPriceInfo.getSaleTotal();
            results.appendLine("Price: " + price + "\n\n");
            results.appendLine("");
        }
    }

    /**
     * getLegInfo()
     * <p>
     * gets list from leg and converts to string list, ready to be displayed to GUI
     *
     * @param leg          List<LegInfo>
     * @param l            int
     * @param aircraftData List<AircraftData>
     * @param guiWindow
     * @return result
     */
    List<String> getLegInfo(List<LegInfo> leg, int l,
                            List<AircraftData> aircraftData, GUIWindow guiWindow) {
        List<String> result = new ArrayList<>();

        String aircraft = leg.get(l).getAircraft();
        aircraft = guiWindow.dl.getAircraftName(aircraftData, aircraft);

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
     * getAircraftName()
     * <p>
     * compares aircraft data codes with aircraft on file in QPX in order to get a more user friendly version of the name
     *
     * @param aircraftData List<AircraftData>
     * @param aircraft     String
     * @return aircraft
     */
    public String getAircraftName(List<AircraftData> aircraftData, String aircraft) {
        for (AircraftData anAircraftData : aircraftData) {
            if (anAircraftData.getCode().equals(aircraft)) {
                aircraft = anAircraftData.getName();
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
    String getDestinationName(List<AirportData> airportData, String destination, int o, List<CityData> tripData) {
        if (airportData.get(o).getCode().equals(destination)) {
            destination = airportData.get(o).getName();
            for (CityData aTripData : tripData) {
                if (aTripData.getCode().equals(airportData.get(o).getCity())) {
                    destination = destination + ", " + aTripData.getName();
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
    String getOriginName(List<AirportData> airportData, int n, String origin, List<CityData> tripData) {
        if (airportData.get(n).getCode().equals(origin)) {
            origin = airportData.get(n).getName();
            for (CityData aTripData : tripData) {
                if (aTripData.getCode().equals(airportData.get(n).getCity())) {
                    origin = origin + ", " + aTripData.getName();
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
    List<LegInfo> getFlightInfo(List<SegmentInfo> segInfo, int k, List<CarrierData> carrierData, TextArea results) {
        FlightInfo flightInfo = segInfo.get(k).getFlight();
        String flightCarr = flightInfo.getCarrier();
        String flightNum = flightInfo.getNumber();
        for (CarrierData aCarrierData : carrierData) {
            if (aCarrierData.getCode().equals(flightCarr)) {
                flightCarr = aCarrierData.getName();
            }
        }
        results.appendLine("Carrier: " + flightCarr + "\t Flight No: " + flightNum);
        return segInfo.get(k).getLeg();
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
    List<SegmentInfo> getSliceInfo(int j, List<SliceInfo> sliceInfo, TextArea results, DecimalFormat df) {
        int duration = sliceInfo.get(j).getDuration();
        double durationInHours = duration / 60;
        results.appendLine("Duration: " + df.format(durationInHours) + " hrs");
        return sliceInfo.get(j).getSegment();
    }

    /**
     * getID()
     * <p>
     * gets ID for flight number and prints to GUI, returns slice info for rest of chain.
     *
     * @param i           int
     * @param tripOptions List<tripOption>
     * @param results     TextArea
     * @return sliceInfo
     */
    List<SliceInfo> getID(int i, List<TripOption> tripOptions, TextArea results) {
        String id;
        id = tripOptions.get(i).getId();
        results.appendLine((i + 1) + ": " +id);
        return tripOptions.get(i).getSlice();

    }
}

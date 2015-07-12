package net.raphaelmiller;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.EmptySpace;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.ProgressBar;
import com.googlecode.lanterna.gui.component.TextBox;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by raphael on 5/26/15.
 *
 * LanternaHandler Class -
 *      Lanterna Handler handles all functions used to manipulate the Lanterna Framework within the program and uses
 *      creates the GUI system. Actual GUI functions are placed in GUIWindow.
 */
public class LanternaHandler  {

    private Terminal terminal;
    private Screen screen;

    private GUIWindow guiInput;
    private GUIWindow guiOutboundFlight;
    private GUIWindow guiError;
    private GUIWindow guiLoad;
    private GUIWindow guiInboundFlight;
    private GUIWindow guiItenerary;

    private GUIScreen guiScreen;

    private TextBox destinationBox;
    private TextBox departureLocationBox;
    private TextBox dateOfDepartureBox;
    private TextBox passengerBox;
    private TextBox dateOfReturnBox;

    public LanternaHandler(){

    }

    public LanternaHandler(Terminal terminal, Screen screen, GUIWindow guiInput, GUIWindow guiOutboundFlight, GUIWindow guiError,
                           GUIWindow guiLoad, GUIScreen guiScreen, GUIWindow guiInboundFlight, GUIWindow guiItenerary) {
        this.terminal = terminal;
        this.screen = screen;
        this.guiInput = guiInput;
        this.guiOutboundFlight = guiOutboundFlight;
        this.guiError = guiError;
        this.guiLoad = guiLoad;
        this.guiScreen = guiScreen;
        this.guiInboundFlight = guiInboundFlight;
        this.guiItenerary = guiItenerary;
    }

    public LanternaHandler(TextBox destinationBox, TextBox departureLocationBox, TextBox dateOfDepartureBox,
                           TextBox passengerBox, TextBox dateOfReturnBox) {
        this.destinationBox = destinationBox;
        this.departureLocationBox = departureLocationBox;
        this.dateOfDepartureBox = dateOfDepartureBox;
        this.passengerBox = passengerBox;
        this.dateOfReturnBox = dateOfReturnBox;
    }

    /**
     * LanternaTerminal() -
     *
     * Creates a terminal, screen and prepares for a GUI Window within the program. Handles all essential functions with
     * Lanterna Gui and places two windows, guiInput and guiOutboundFlight and creates arguments for said Gui Windows.
     * @param flc FlightsClient
     */
    public void LanternaTerminal(FlightsClient flc) throws GoogleJsonResponseException {

        setTerminal(TerminalFacade.createTerminal());
        setScreen(new Screen(getTerminal()));
        setGuiScreen(new GUIScreen(getScreen()));

        GUIScreen screenInitializer = getGuiScreen();

        //creates lanterna terminal windows
        setGuiInput(new GUIWindow("Flights", flc, screenInitializer));
        setGuiOutboundFlight(new GUIWindow("Results", flc, screenInitializer));
        setGuiError(new GUIWindow("Error", flc, screenInitializer));
        setGuiLoad(new GUIWindow("Loading", flc, screenInitializer));
        setGuiInboundFlight(new GUIWindow("Inbound", flc, screenInitializer));
        setGuiItenerary(new GUIWindow("Itinerary", flc, screenInitializer));

        GUIWindow guiInput = getGuiInput();
        GUIWindow guiOutput = getGuiOutboundFlight();
        GUIWindow guiError = getGuiError();
        GUIWindow guiLoad = getGuiLoad();
        GUIWindow guiInboundFlight = getGuiInboundFlight();
        GUIWindow guiItenerary = getGuiItenerary();

        screenInitializer.getScreen().startScreen();

        guiInput.drawGuiInput(guiOutput, guiInboundFlight, screenInitializer, guiError, guiLoad, guiItenerary, this);

    }



    /**
     * buttons() - method
     *
     * method creating buttons for the GUI
     * @param guiInput              GUIWindow
     * @param guiOutput             GUIWindow
     * @param guiInboundFlight      GuiWindow
     * @param guiScreen             GUIScreen
     * @param destinationBox        TextBox
     * @param departureLocationBox  TextBox
     * @param dateOfDepartureBox    TextBox
     * @param passengerBox          TextBox
     * @param progressBar           ProgressBar
     * @param guiError              GUIWindow
     * @param guiLoad               GUIWindow
     * @param guiItinerary          GuiWindow
     */
    public void buttons(GUIWindow guiInput, GUIWindow guiOutput, GUIWindow guiInboundFlight, GUIScreen guiScreen,
                        TextBox destinationBox, TextBox departureLocationBox, TextBox dateOfDepartureBox,
                        TextBox passengerBox, ProgressBar progressBar, GUIWindow guiError, GUIWindow guiLoad,
                        GUIWindow guiItinerary, TextBox dateOfReturnBox) throws GoogleJsonResponseException {

        guiInput.buttons.guiInputEnterButton(guiScreen, guiOutput, guiInboundFlight, destinationBox,
                departureLocationBox, dateOfDepartureBox, passengerBox, progressBar, guiError, guiLoad, guiInput,
                guiItinerary, dateOfReturnBox);

        guiInput.buttons.quitButton(guiInput);
    }

    /**
     * rightPanel() - method
     *
     * method creating right panel objects and functions
     *
     * @param guiInput GUIWindow
     * @param dateOfDepartureBox TextBox
     */
    public void rightPanel(GUIWindow guiInput, TextBox dateOfDepartureBox, TextBox dateOfReturnBox) {
        guiInput.rightPanel.addComponent(new EmptySpace(2,2));
        guiInput.rightPanel.addComponent(new Label("Date of Departure(YYYY-MM-DD)\t\t", Terminal.Color.RED));
        guiInput.rightPanel.addComponent(dateOfDepartureBox);
        guiInput.rightPanel.addComponent(new Label("Date of Return(YYYY-MM-DD)", Terminal.Color.RED));
        guiInput.rightPanel.addComponent(dateOfReturnBox);
    }

    /**
     * middlePanel() = method
     *
     * method creating the middle panel objects and function
     *
     * @param guiInput GUIWindow
     * @param departureLocationBox TextBox
     */
    public void middlePanel(GUIWindow guiInput, TextBox departureLocationBox) {
        guiInput.middlePanel.addComponent(new EmptySpace(2,2));
        guiInput.middlePanel.addComponent(new Label("Arriving to(IATA code)\t\t", Terminal.Color.RED));
        guiInput.middlePanel.addComponent(departureLocationBox);
    }

    /**
     * leftPanel() - method
     *
     * method creating the left panel objects and function
     *
     * @param guiInput GUIWindow
     * @param passengerBox TextBox
     * @param destinationBox TextBox
     */
    public void leftPanel(GUIWindow guiInput, TextBox passengerBox, TextBox destinationBox) {
        guiInput.leftPanel.addComponent(new Label("Number of Passengers", Terminal.Color.RED));
        guiInput.leftPanel.addComponent(passengerBox);
        guiInput.leftPanel.addComponent(new Label("Leaving from(IATA code)\t\t", Terminal.Color.RED));
        guiInput.leftPanel.addComponent(destinationBox);


    }

    //--------------------------------------------------------------------------------------------------------


    public GUIWindow getGuiInput() {
        return guiInput;
    }

    public void setGuiInput(GUIWindow guiInput) {
        this.guiInput = guiInput;
    }

    public GUIWindow getGuiOutboundFlight() {
        return guiOutboundFlight;
    }

    public void setGuiOutboundFlight(GUIWindow guiOutboundFlight) {
        this.guiOutboundFlight = guiOutboundFlight;
    }

    public GUIWindow getGuiError() {
        return guiError;
    }

    public void setGuiError(GUIWindow guiError) {
        this.guiError = guiError;
    }

    public GUIWindow getGuiLoad() {
        return guiLoad;
    }

    public void setGuiLoad(GUIWindow guiLoad) {
        this.guiLoad = guiLoad;
    }

    public GUIScreen getGuiScreen() {
        return guiScreen;
    }

    public void setGuiScreen(GUIScreen guiScreen) {
        this.guiScreen = guiScreen;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public TextBox getDestinationBox() {
        return destinationBox;
    }

    public void setDestinationBox(TextBox destinationBox) {
        this.destinationBox = destinationBox;
    }

    public TextBox getDepartureLocationBox() {
        return departureLocationBox;
    }

    public void setDepartureLocationBox(TextBox departureLocationBox) {
        this.departureLocationBox = departureLocationBox;
    }

    public TextBox getDateOfDepartureBox() {
        return dateOfDepartureBox;
    }

    public void setDateOfDepartureBox(TextBox dateOfDepartureBox) {
        this.dateOfDepartureBox = dateOfDepartureBox;
    }

    public TextBox getPassengerBox() {
        return passengerBox;
    }

    public void setPassengerBox(TextBox passengerBox) {
        this.passengerBox = passengerBox;
    }

    public TextBox getDateOfReturnBox() {
        return dateOfReturnBox;
    }

    public void setDateOfReturnBox(TextBox dateOfReturnBox) {
        this.dateOfReturnBox = dateOfReturnBox;
    }

    public GUIWindow getGuiInboundFlight() {
        return guiInboundFlight;
    }

    public void setGuiInboundFlight(GUIWindow guiInboundFlight) {
        this.guiInboundFlight = guiInboundFlight;
    }

    public GUIWindow getGuiItenerary() {
        return guiItenerary;
    }

    public void setGuiItenerary(GUIWindow guiItenerary) {
        this.guiItenerary = guiItenerary;
    }
}

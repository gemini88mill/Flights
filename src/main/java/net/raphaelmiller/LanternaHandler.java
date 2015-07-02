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
    private GUIWindow guiOutput;
    private GUIWindow guiError;
    private GUIWindow guiLoad;

    private GUIScreen guiScreen;

    public LanternaHandler(){

    }

    public LanternaHandler(Terminal terminal, Screen screen, GUIWindow guiInput, GUIWindow guiOutput, GUIWindow guiError,
                           GUIWindow guiLoad, GUIScreen guiScreen) {
        this.terminal = terminal;
        this.screen = screen;
        this.guiInput = guiInput;
        this.guiOutput = guiOutput;
        this.guiError = guiError;
        this.guiLoad = guiLoad;
        this.guiScreen = guiScreen;
    }

    /**
     * LanternaTerminal() -
     *
     * Creates a terminal, screen and prepares for a GUI Window within the program. Handles all essential functions with
     * Lanterna Gui and places two windows, guiInput and guiOutput and creates arguments for said Gui Windows.
     * @param flc FlightsClient
     */
    public void LanternaTerminal(FlightsClient flc) throws GoogleJsonResponseException {



        //terminal = TerminalFacade.createTerminal();
        //screen = new Screen(terminal);
        //guiScreen = new GUIScreen(screen);

        setTerminal(TerminalFacade.createTerminal());
        setScreen(new Screen(getTerminal()));
        setGuiScreen(new GUIScreen(getScreen()));

        GUIScreen screenInitializer = getGuiScreen();

        //creates lanterna terminal windows
        setGuiInput(new GUIWindow("Flights", flc, screenInitializer));
        setGuiOutput(new GUIWindow("Results", flc, screenInitializer));
        setGuiError(new GUIWindow("Error", flc, screenInitializer));
        setGuiLoad(new GUIWindow("Loading", flc, screenInitializer));

        //guiInput = new GUIWindow("QPX", flc, guiScreen);
        //guiOutput = new GUIWindow("INFO", flc, guiScreen);
        //guiError = new GUIWindow("Error", flc, guiScreen);
        //guiLoad = new GUIWindow("Loading", flc, guiScreen);

        GUIWindow guiInput = getGuiInput();
        GUIWindow guiOutput = getGuiOutput();
        GUIWindow guiError = getGuiError();
        GUIWindow guiLoad = getGuiLoad();

        //guiScreen.getScreen().startScreen();

        screenInitializer.getScreen().startScreen();


        drawGuiInput(guiInput, guiOutput, screenInitializer, guiError, guiLoad);

    }

    /**
     * drawGuiInput() method
     *
     * method creating the positions and order for the gui
     * @param guiInput  GUIWindow
     * @param guiOutput GUIWindow
     * @param guiScreen GUIScreen
     * @param guiError GUIWindow
     * @param guiLoad
     */
    private void drawGuiInput(GUIWindow guiInput, GUIWindow guiOutput, GUIScreen guiScreen, GUIWindow guiError,
                              GUIWindow guiLoad) throws GoogleJsonResponseException {
        //objects used for input capture
        TextBox destinationBox = new TextBox(null, 125);
        TextBox departureLocationBox = new TextBox(null, 125);
        TextBox dateOfDepartureBox = new TextBox(null, 125);
        TextBox passengerBox = new TextBox(null, 100);
        ProgressBar progressBar = new ProgressBar(100);

        //methods for panel drawing
        leftPanel(guiInput, passengerBox, destinationBox);
        middlePanel(guiInput, departureLocationBox);
        rightPanel(guiInput, dateOfDepartureBox);
        buttons(guiInput, guiOutput, guiScreen, destinationBox, departureLocationBox, dateOfDepartureBox, passengerBox,
                progressBar, guiError, guiLoad);

        guiScreen.showWindow(guiInput, GUIScreen.Position.CENTER);
    }

    /**
     * buttons() - method
     *
     * method creating buttons for the GUI
     * @param guiInput              GUIWindow
     * @param guiOutput             GUIWindow
     * @param guiScreen             GUIScreen
     * @param destinationBox        TextBox
     * @param departureLocationBox  TextBox
     * @param dateOfDepartureBox    TextBox
     * @param passengerBox          TextBox
     * @param progressBar           ProgressBar
     * @param guiError              GUIWindow
     * @param guiLoad               GUIWindow
     */
    private void buttons(GUIWindow guiInput, GUIWindow guiOutput, GUIScreen guiScreen, TextBox destinationBox,
                         TextBox departureLocationBox, TextBox dateOfDepartureBox, TextBox passengerBox,
                         ProgressBar progressBar, GUIWindow guiError, GUIWindow guiLoad) throws GoogleJsonResponseException {
        guiInput.buttons.enterButton(guiScreen, guiOutput, destinationBox, departureLocationBox, dateOfDepartureBox,
                passengerBox, progressBar, guiError, guiLoad, guiInput);
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
    private void rightPanel(GUIWindow guiInput, TextBox dateOfDepartureBox) {
        guiInput.rightPanel.addComponent(new EmptySpace(2,2));
        guiInput.rightPanel.addComponent(new Label("Date of Departure(YYYY-MM-DD)\t\t", Terminal.Color.RED));
        guiInput.rightPanel.addComponent(dateOfDepartureBox);
    }

    /**
     * middlePanel() = method
     *
     * method creating the middle panel objects and function
     *
     * @param guiInput GUIWindow
     * @param departureLocationBox TextBox
     */
    private void middlePanel(GUIWindow guiInput, TextBox departureLocationBox) {
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
    private void leftPanel(GUIWindow guiInput, TextBox passengerBox, TextBox destinationBox) {
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

    public GUIWindow getGuiOutput() {
        return guiOutput;
    }

    public void setGuiOutput(GUIWindow guiOutput) {
        this.guiOutput = guiOutput;
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
}

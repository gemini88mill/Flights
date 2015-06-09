package net.raphaelmiller;

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

    /**
     * LanternaTerminal() -
     *
     * Creates a terminal, screen and prepares for a GUI Window within the program. Handles all essential functions with
     * Lanterna Gui and places two windows, guiInput and guiOutput and creates arguments for said Gui Windows.
     * @param flc FlightsClient
     */
    public void LanternaTerminal(FlightsClient flc){

        //creates lanterna terminal windows
        GUIWindow guiInput = new GUIWindow("QPX", flc);
        GUIWindow guiOutput = new GUIWindow("INFO", flc);

        Terminal terminal = TerminalFacade.createTerminal();
        Screen screen = new Screen(terminal);

        GUIScreen guiScreen = new GUIScreen(screen);
        guiScreen.getScreen().startScreen();

        drawGuiInput(guiInput, guiOutput, guiScreen);
    }

    /**
     * drawGuiInput() method
     *
     * method creating the positions and order for the gui
     *
     * @param guiInput GUIWindow
     * @param guiOutput GUIWindow
     * @param guiScreen GUIScreen
     */
    private void drawGuiInput(GUIWindow guiInput, GUIWindow guiOutput, GUIScreen guiScreen) {
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
                progressBar);

        guiScreen.showWindow(guiInput, GUIScreen.Position.CENTER);
    }

    /**
     * buttons() - method
     *
     * method creating buttons for the GUI
     *
     * @param guiInput GUIWindow
     * @param guiOutput GUIWindow
     * @param guiScreen GUIScreen
     * @param destinationBox TextBox
     * @param departureLocationBox TextBox
     * @param dateOfDepartureBox TextBox
     * @param passengerBox TextBox
     * @param progressBar ProgressBar
     */
    private void buttons(GUIWindow guiInput, GUIWindow guiOutput, GUIScreen guiScreen, TextBox destinationBox,
                         TextBox departureLocationBox, TextBox dateOfDepartureBox, TextBox passengerBox,
                         ProgressBar progressBar) {
        guiInput.enterButton(guiScreen, guiOutput, destinationBox, departureLocationBox, dateOfDepartureBox,
                passengerBox, progressBar);
        guiInput.quitButton();
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


}

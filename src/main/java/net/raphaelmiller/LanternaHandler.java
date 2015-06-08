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

        //initializes TextBoxes for first page of GUI


        //creates lanterna terminal windows
        GUIWindow guiInput = new GUIWindow("QPX", flc);
        GUIWindow guiOutput = new GUIWindow("INFO", flc);

        Terminal terminal = TerminalFacade.createTerminal();
        Screen screen = new Screen(terminal);


        GUIScreen guiScreen = new GUIScreen(screen);
        guiScreen.getScreen().startScreen();

        drawGuiInput(guiInput, guiOutput, guiScreen);




    }

    private void drawGuiInput(GUIWindow guiInput, GUIWindow guiOutput, GUIScreen guiScreen) {
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

    private void buttons(GUIWindow guiInput, GUIWindow guiOutput, GUIScreen guiScreen, TextBox destinationBox,
                         TextBox departureLocationBox, TextBox dateOfDepartureBox, TextBox passengerBox,
                         ProgressBar progressBar) {
        guiInput.enterButton(guiScreen, guiOutput, destinationBox, departureLocationBox, dateOfDepartureBox,
                passengerBox, progressBar);
        guiInput.quitButton();
    }

    private void rightPanel(GUIWindow guiInput, TextBox dateOfDepartureBox) {
        guiInput.rightPanel.addComponent(new EmptySpace(2,2));
        guiInput.rightPanel.addComponent(new Label("Date of Departure(YYYY-MM-DD)\t\t", Terminal.Color.RED));
        guiInput.rightPanel.addComponent(dateOfDepartureBox);
    }

    private void middlePanel(GUIWindow guiInput, TextBox departureLocationBox) {
        guiInput.middlePanel.addComponent(new EmptySpace(2,2));
        guiInput.middlePanel.addComponent(new Label("Arriving to(IATA code)\t\t", Terminal.Color.RED));
        guiInput.middlePanel.addComponent(departureLocationBox);
    }

    private void leftPanel(GUIWindow guiInput, TextBox passengerBox, TextBox destinationBox) {
        guiInput.leftPanel.addComponent(new Label("Number of Passengers", Terminal.Color.RED));
        guiInput.leftPanel.addComponent(passengerBox);
        guiInput.leftPanel.addComponent(new Label("Leaving from(IATA code)\t\t", Terminal.Color.RED));
        guiInput.leftPanel.addComponent(destinationBox);

    }


}

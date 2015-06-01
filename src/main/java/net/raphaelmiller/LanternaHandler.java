package net.raphaelmiller;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.TextArea;
import com.googlecode.lanterna.gui.component.TextBox;
import com.googlecode.lanterna.gui.layout.LayoutParameter;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 * Created by raphael on 5/26/15.
 */
public class LanternaHandler  {


    public void LanternaTerminal(FlightsClient flc){

        //initializes TextBoxes for frist page of GUI
        TextBox destinationBox = new TextBox(null, 125);
        TextBox departureLocationBox = new TextBox(null, 125);
        TextBox dateOfDepartureBox = new TextBox(null, 125);

        //creates lanterna terminal windows
        GUIWindow guiInput = new GUIWindow("QPX", flc);
        GUIWindow guiOutput = new GUIWindow("INFO", flc);

        GUIScreen guiScreen = TerminalFacade.createGUIScreen();
        guiScreen.getScreen().startScreen();
        //guiScreen.setTitle("QPX");

        //adds Labels for TextBoxes
        guiInput.rightPanel.addComponent(new Label("Date of Departure\t\t", Terminal.Color.RED));
        guiInput.middlePanel.addComponent(new Label("Leaving From(IATA)\t\t", Terminal.Color.RED));
        guiInput.leftPanel.addComponent(new Label("Destination (IATA)\t\t", Terminal.Color.RED));

        //places TextBoxes
        guiInput.leftPanel.addComponent(destinationBox);
        guiInput.middlePanel.addComponent(departureLocationBox);
        guiInput.rightPanel.addComponent(dateOfDepartureBox);

        guiInput.enterButton(guiScreen, guiOutput, destinationBox, departureLocationBox, dateOfDepartureBox);
        guiInput.quitButton();



        guiScreen.showWindow(guiInput, GUIScreen.Position.CENTER);




    }


}

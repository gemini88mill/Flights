package net.raphaelmiller;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.TextBox;
import com.googlecode.lanterna.gui.layout.LayoutParameter;
import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by raphael on 5/26/15.
 */
public class LanternaHandler  {


    public void LanternaTerminal(FlightsClient flc){
        //creates lanterna terminal window
        GUIWindow tw = new GUIWindow("QPX", flc);

        GUIScreen guiScreen = TerminalFacade.createGUIScreen();
        guiScreen.getScreen().startScreen();
        //guiScreen.setTitle("QPX");

        tw.rightPanel.addComponent(new Label("Date of Departure\t\t", Terminal.Color.RED));
        tw.middlePanel.addComponent(new Label("Leaving From\t\t", Terminal.Color.RED));
        tw.leftPanel.addComponent(new Label("Destination (IATA)\t\t", Terminal.Color.RED));

        tw.leftPanel.addComponent(new TextBox(null, 125));
        tw.middlePanel.addComponent(new TextBox(null, 125));
        tw.rightPanel.addComponent(new TextBox(null, 125));

        tw.enterButton(guiScreen);
        tw.quitButton();

        guiScreen.showWindow(tw, GUIScreen.Position.CENTER);




    }


}

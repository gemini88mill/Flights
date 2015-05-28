package net.raphaelmiller;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;

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

        guiScreen.showWindow(tw, GUIScreen.Position.CENTER);


    }


}

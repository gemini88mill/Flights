package net.raphaelmiller;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Border;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.layout.LayoutParameter;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.nio.charset.Charset;

/**
 * Created by raphael on 5/26/15.
 */
public class LanternaHandler  {

    public void LanternaTerminal(){
        //creates lanterna terminal window

        Terminal terminal = TerminalFacade.createTerminal(System.in, System.out, Charset.forName("UTF8"));
        Screen screen = new Screen(terminal);
        GUIScreen guiScreen = new GUIScreen(screen);

        terminal.enterPrivateMode();

        TerminalSize screenSize = terminal.getTerminalSize();
        terminal.moveCursor(screenSize.getColumns() - 1, screenSize.getRows() - 1);
        terminal.applyBackgroundColor(Terminal.Color.BLACK);

        LanternaScreen(guiScreen);

        terminal.exitPrivateMode();
    }

    private void LanternaScreen(GUIScreen guiScreen) {

        guiScreen.showWindow(new Window("QPX"));
        guiScreen.getScreen().startScreen();

    }
}

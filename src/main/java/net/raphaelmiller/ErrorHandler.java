package net.raphaelmiller;

import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.dialog.DialogButtons;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.gui.layout.LayoutParameter;
import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by raphael on 6/10/15.
 */
public class ErrorHandler  {

    private static final String ERROR_TITLE = "ERROR";
    private MessageBox mb;

    //protected ErrorHandler(String title, String message, DialogButtons buttons) {


    public void ImproperDateError(GUIWindow guiError){
        String error = "This date is before today's current date, please reenter the date and try again";
        Button ok = new Button("OK");

        guiError.horizontalPanel.addComponent(new Panel(ERROR_TITLE, Panel.Orientation.HORISONTAL));


    }

    void errorWindow(GUIWindow guiError, GUIScreen guiScreen, Buttons buttons) {
        guiError.horizontalPanel.addComponent(new Label("Error", Terminal.Color.RED));
        buttons.backButton(guiScreen, guiError);
        buttons.quitButton(guiError);
        guiError.guiScreen.showWindow(guiError, GUIScreen.Position.CENTER);
    }
}

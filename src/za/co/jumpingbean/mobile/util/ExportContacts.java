/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package za.co.jumpingbean.mobile.util;

import java.io.IOException;
import java.util.Timer;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.*;

/**
 * @author mark
 */
public class ExportContacts extends MIDlet {



    public void startApp() {
        this.ShowMainForm();
    }

    public void ShowMainForm(){
        MainForm mainForm = new MainForm(this);
        Display.getDisplay(this).setCurrent(mainForm);
    }

    
    public void pauseApp() {


    }


    public void destroyApp(boolean unconditional) {
        Display.getDisplay(this).setCurrent(null);
        destroyApp(true);
        notifyDestroyed();
    }

    void showProgress(MainForm form) {
       Alert alert = new Alert("Exporting","Exporting, Please Wait",null,AlertType.INFO);
       Display.getDisplay(this).setCurrent(alert, form);
    }

    void showDone(MainForm form) {
       Alert alert = new Alert("Finished","Finished Exporting!",null,AlertType.INFO);
       Display.getDisplay(this).setCurrent(alert, form);
    }
}

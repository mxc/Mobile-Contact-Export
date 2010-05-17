/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package za.co.jumpingbean.mobile.util;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.String;
import java.util.Enumeration;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;

/**
 *
 * @author mark
 */
public class MainForm extends Form implements CommandListener{

    private ExportContacts controller;
    private ChoiceGroup folderList;
    private ChoiceGroup formatList;

    public MainForm(ExportContacts controller){
        super("Export Contacts");
        this.controller = controller;
        Command export = new Command("Export Contacts",Command.ITEM,1);
        Command exit = new Command("Exit",Command.EXIT,1);
        ImageItem item=null;
        try {
            Image label = Image.createImage("/label.png");
            item = new ImageItem("Export Contacts",label,ImageItem.LAYOUT_CENTER,"Please use the menu to begin export");
            append(item);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        getFolders();
        getVCardFormats();
        addCommand(export);
        addCommand (exit);
        setCommandListener(this);
    }

    public void commandAction(Command c, Displayable d) {
        if (c.getLabel().equalsIgnoreCase("Export Contacts")){
            controller.showProgress(this);
            Thread thread = new Thread(new Runnable(){
                public void run() {
                    try {
                        String folder = folderList.getString(folderList.getSelectedIndex());
                        PIM pim = PIM.getInstance();
                        ContactList list = (ContactList) pim.openPIMList(PIM.CONTACT_LIST, PIM.READ_ONLY);
                        Enumeration contacts = list.items();
                        int i=0;
                        FileConnection filecon=null;
                        while(contacts.hasMoreElements()){
                          System.out.println("exporting....");
                          Contact contact = (Contact) contacts.nextElement();
                          filecon = (FileConnection)Connector.open("file:///"+folder+"export"+i+".vcf",Connector.READ_WRITE);
                          i++;
                          OutputStream os;
                          if (!filecon.exists()){
                              filecon.create();
                          }
                         os = filecon.openOutputStream();
                         String format = formatList.getString(formatList.getSelectedIndex());
                         pim.toSerialFormat(contact, os, "UTF-8",format);
                         os.flush();
                         os.close();
                         filecon.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (PIMException ex) {
                        ex.printStackTrace();
                    }
                    controller.showDone(MainForm.this);
                }
            });
            thread.start();
        }else{
            controller.notifyDestroyed();
        }
    }

    private void getVCardFormats(){
        formatList= new ChoiceGroup("Select VCard Format",List.POPUP);
        PIM pim = PIM.getInstance();
        String[] formats = pim.supportedSerialFormats(PIM.CONTACT_LIST);
        int formatsCount = formats.length;
        for(int i = 0; i < formatsCount; i++) {
            String format = formats[i];
            if(format.startsWith("VCARD") == true) {
                formatList.append(formats[i], null);
            }
        }
        append(formatList);
    }

    private void getFolders(){
        Enumeration drives = FileSystemRegistry.listRoots();
        folderList= new ChoiceGroup("Select Folder",List.POPUP);
        folderList.setLayout(ChoiceGroup.LAYOUT_CENTER);
        while(drives.hasMoreElements()) {
                String root = (String) drives.nextElement();
                folderList.append(root, null);
        }
        folderList.setSelectedIndex(0, true);
        append(folderList);
    }

}

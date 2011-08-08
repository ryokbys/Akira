package viewer.viewConfigPanel;


import java.util.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class KeyHelpPanel extends JPanel implements HyperlinkListener{
  public KeyHelpPanel(){
    create();
  }
  public void hyperlinkUpdate(HyperlinkEvent e) {
    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
      try {
        URL url = e.getURL();
        htmlPane.setPage(url);
      } catch (IOException e1) {
        //e1.printStackTrace();
      }
    }
  }

  JEditorPane htmlPane;
  void create(){
    // outout area
    htmlPane = new JEditorPane();
    htmlPane.setFocusable(false);
    htmlPane.setEditable(false);
    htmlPane.setContentType("text/html");
    htmlPane.addHyperlinkListener(this);
    //is this ok?
    String url=""+this.getClass().getResource("/viewer/keys.html");
    try {
      htmlPane.setPage(url);
    }catch (IOException e){
      System.out.println("cannot load: "+url);
    }

    JScrollPane pane =
      new JScrollPane(htmlPane,
                      ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    setLayout(new GridLayout(1, 1));
    add(pane);

  }

}

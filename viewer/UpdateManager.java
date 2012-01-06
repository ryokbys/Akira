package viewer;

import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.*;
import java.net.*;
import java.util.regex.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.net.*;
import java.text.*;

import viewer.*;

//launch browser
//http://www.centerkey.com/java/browser/
import com.centerkey.utils.BareBonesBrowserLaunch;


public class UpdateManager{
  private double thisVersion=2.1;

  private double wwwVersion;
  private String homeURL="http://code.google.com/p/project-akira/";
  private String downloadURL="http://code.google.com/p/project-akira/downloads/list";


  private Controller ctrl;
  //constructor
  public UpdateManager(Controller ctrl){
    this.ctrl=ctrl;
    //checkUpdate();
  }

  public void showAbout(){
    JFrame frame = new JFrame();
    String option[] = { "WWW page","OK"};
    String query="Akira";
    int ans = JOptionPane.showOptionDialog(frame,
                                           query,
                                           "About Akira",
                                           JOptionPane.DEFAULT_OPTION,
                                           JOptionPane.QUESTION_MESSAGE,
                                           icon,
                                           option,
                                           option[1] );
    if(ans==0){//WWW page
      BareBonesBrowserLaunch.openURL(homeURL);
    }
  }
  public void showDialog(){
    //show dialog
    String str;
    if(checkUpdate())
      str="Available new version "+wwwVersion;
    else
      str="The current version "+thisVersion+" is up to date";

    System.out.println(str);
    showDialog(str);
  }

  public boolean isStrongUpdate(){
    int newWWW=(int)wwwVersion;
    int newThis=(int)thisVersion;
    if(newWWW>newThis)
      return true;
    else
      return false;
  }
  public boolean checkUpdate(){
    try{
      //get url
      URL u = new URL(downloadURL);
      InputStream is = u.openStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      //parse and check
      MyParserCallback cb = new MyParserCallback();
      ParserDelegator pd = new ParserDelegator();
      pd.parse(br, cb, true);
    } catch (Exception e) {
      wwwVersion=thisVersion;
    }

    if(wwwVersion > thisVersion)
      return true;
    else
      return false;

  }
  private class MyParserCallback extends HTMLEditorKit.ParserCallback {
    public void handleStartTag(HTML.Tag tag, MutableAttributeSet attr, int pos){
      if(tag.equals(HTML.Tag.A)){
        String ret = (String)attr.getAttribute(HTML.Attribute.HREF);

        //if(ret.matches(".*Akira.*.zip.*"))System.out.println(ret);

        Pattern pattern = Pattern.compile("(.*)Akira-(.*).zip(.*)");
        Matcher matcher = pattern.matcher(ret);
        if(matcher.find()) wwwVersion=Double.valueOf(matcher.group(2));
      }
    }
  }

  /////////
  private ImageIcon icon=new ImageIcon(this.getClass().getResource("/img/logo/Akira600x627.png"));

  private void showDialog(String query){
    JFrame frame = new JFrame();
    String option[] = { "WWW page","OK"};
    int ans = JOptionPane.showOptionDialog(frame,
                                           query,
                                           "update notify",
                                           JOptionPane.DEFAULT_OPTION,
                                           JOptionPane.QUESTION_MESSAGE,
                                           icon,
                                           option,
                                           option[1] );

    if(ans==0){//WWW page
      BareBonesBrowserLaunch.openURL(downloadURL);
    }else if(ans==1){//ok
    }

  }



  //for enjoy mode
  private class DialogEarthquakeCenter extends Object {
    public static final int SHAKE_DISTANCE = 10;
    public static final double SHAKE_CYCLE = 50;
    public static final int SHAKE_DURATION = 1000;
    public static final int SHAKE_UPDATE = 5;

    private JDialog dialog;
    private Point naturalLocation;
    private long startTime;
    private Timer shakeTimer;
    private final double HALF_PI = Math.PI / 2.0;
    private final double TWO_PI = Math.PI * 2.0;

    public DialogEarthquakeCenter(JDialog d){
      dialog = d;
    }

    public void startShake(){
      naturalLocation = dialog.getLocation();
      startTime = System.currentTimeMillis();
      shakeTimer =
        new Timer(SHAKE_UPDATE,new ActionListener(){
            public void actionPerformed(ActionEvent e){
              // calculate elapsed time
              long elapsed = System.currentTimeMillis() -
                startTime;
              // use sin to calculate an x-offset
              double waveOffset = (elapsed % SHAKE_CYCLE) /
                SHAKE_CYCLE;
              double angle = waveOffset * TWO_PI;

              // offset the x-location by an amount
              // proportional to the sine, up to
              // shake_distance
              int shakenX = (int) ((Math.sin(angle) *
                                    SHAKE_DISTANCE) +
                                   naturalLocation.x);
              dialog.setLocation(shakenX, naturalLocation.y);
              dialog.repaint();

              // should we stop timer?
              if(elapsed >= SHAKE_DURATION)stopShake();
            }
          }
          );
      shakeTimer.start();
    }
    public void stopShake(){
      shakeTimer.stop();
      dialog.setLocation(naturalLocation);
      dialog.repaint();
    }
  }//end of private class

}

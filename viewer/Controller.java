package viewer;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;
import info.clearthought.layout.*;

import tools.*;
import data.*;
import viewer.*;
import viewer.renderer.*;
import viewer.viewConfigPanel.*;
import viewer.LF.*;

/**
 * This class controls everything.
 * SO THIS CLASS IS MAIN OF AKIRAView.
 *
 */
public class Controller implements WindowListener{

  public ViewConfig vconf=new ViewConfig();
  public ViewConfigWindow vcWin;
  public int activeRWinID=-1;//activewindow ID
  public static final int MAX_RWIN=10;
  public RenderingWindow[] RWin=new RenderingWindow[MAX_RWIN];//instance array

  private ViewConfigWindowMenuController vcWinMenu;

  //key listener
  public KeyController keyCtrl;

  //enjoy mode flag
  public double javaVer=Double.parseDouble(System.getProperty("java.specification.version"));


  public RenderingWindow getActiveRW(){
    if(activeRWinID>=0 && RWin[activeRWinID]!=null
       && RWin[activeRWinID].isConstuctorFinished){
      return RWin[activeRWinID];
    }else{
      return null;
    }
  }
  public int getRWinNum(){
    int n=0;
    for(int i=0;i<MAX_RWIN;i++) if(RWin[i]!=null)n++;
    return n;
  }

  /* accesser starts */
  public void clearPickQueue(){
    if(getActiveRW()==null)return;
    getActiveRW().sq.clearQueue();
  }

  public void setLight(){
    if(getActiveRW()==null)return;
    getActiveRW().setLight();
    getActiveRW().repaint();
  }
  public void updateAnnotationFont(){
    if(getActiveRW()==null)return;
    getActiveRW().updateAnnotationFont();
    getActiveRW().repaint();
  }

  public void setVisible2Dplot(){
    if(getActiveRW()==null)return;
    getActiveRW().setVisible2Dplot();
  }

  public void remakePrimitiveObjects(){
    if(getActiveRW()==null)return;
    getActiveRW().remakePrimitiveObjects();
  }

  public float[][] getPlanePrimitiveVector(float[] normalvec,float[] pointvec){
    if(getActiveRW()==null)return null;
    return getActiveRW().plane.getPlanePrimitiveVector(normalvec,pointvec);
  }

  public void setFPS(int n){
    if(getActiveRW()==null)return;
    getActiveRW().setFPS(n);
  }
  public void setFrame(int n){
    if(getActiveRW()==null)return;
    getActiveRW().moveFrame(n);
  }

  public String getActiveFilePath(){
    if(getActiveRW()==null)return null;
    return getActiveRW().getFilePath();
  }

  public void setFrontViewConfigWin(int i){
    //if(i>=0)vcWin.tabbedPane.setSelectedIndex(i);
    vcWin.setVisible(true);
    vcWin.toFront();
  }


  public void updateStatusString(){
    RenderingWindow rw=getActiveRW();
    if(rw!=null){
      vcWin.updateStatusString(rw);
      rw.updateStatusString();
    }

  }
  public void RWinRefresh(){
    if( getActiveRW()==null ) return;
    getActiveRW().refresh();
  }
  public void RWinRepaint(){
    //repaint only
    if(getActiveRW()==null)return;
    getActiveRW().repaint();
  }

  public void setPickedID4Trj(int id){
    if(vconf.isTrjMode)vcWin.setPickedID(id);
  }

  public TrajectoryPanel getTrj(){
    return vcWin.getTrj();
  }
  /* accesser ends */


  //constructer
  public Controller(){

    //load viewer config file
    try {
      FileInputStream inFile = new FileInputStream(vconf.configFile);
      ObjectInputStream in = new ObjectInputStream(inFile);

      vconf = (ViewConfig)in.readObject();
      in.close();
      inFile.close();
    } catch(Exception ex){
      //ex.printStackTrace();
      vconf=null;
      vconf=new ViewConfig();
      System.out.println("cannot load view config");
      System.out.println("if AKIRA dont activate, try to remove "+vconf.configFile);
    }

    //keyevent
    keyCtrl=new KeyController(this);

    //viewConfig window
    vcWin = new ViewConfigWindow(this);
    vcWinMenu=new ViewConfigWindowMenuController(this);
    vcWin.setJMenuBar(vcWinMenu.getMenu());

    vcWin.setBounds(vconf.rectViewConfigWin);
    vcWin.setVisible(vconf.isVisibleViewConfigWin);
    vcWin.addWindowListener(this);
    vcWin.addKeyListener(keyCtrl);
  }

  public void myExit(){
    //save vconf
    vconf.saveWin(this);

    File cdir = new File(vconf.configDir);
    if(!cdir.exists())cdir.mkdir();

    try {
      FileOutputStream outFile = new FileOutputStream(vconf.configFile);
      ObjectOutputStream out = new ObjectOutputStream(outFile);
      out.writeObject(vconf);
      out.close();
      outFile.close();
    } catch(Exception ex){
      ex.printStackTrace();
      System.out.println("<<< CANNOT SAVE ViewConfig >>>");
    }

    vcWinMenu.saveRecents();
    vcWinMenu.saveBookMark();
    System.exit(0);
  }


  public void createRenderingWindow(String filePath){
    //check
    if(filePath==null) return;
    File file=new File(filePath);
    filePath=file.getAbsolutePath();

    if(!file.exists()){
      System.out.println(filePath+" does not exist!!");

      //this is under debug
      Icon errorIcon = UIManager.getIcon("OptionPane.errorIcon");
      JLabel label = new JLabel(filePath+" does not exist!!",
                                errorIcon,SwingConstants.LEFT);
      SlideInNotification slider = new SlideInNotification(label,1);
      //---
      return;
    }

    //rendering window ID
    int rwID=-1;

    //search blank rwID
    for(int i=0;i<MAX_RWIN;i++){
      if(RWin[i]==null){
        rwID=i;
        activeRWinID=i;
        break;//exit loop
      }
    }
    //error trap
    if(rwID==-1){
      System.out.println("************ TOO MANY RENDERING WINDOWS ************");
      System.out.println("   YOU HAD BETTER CLOSE SOME RENDERING WINDOWS   ");
      System.out.println("****************************************************");
      return;
    }

    //create rendering window
    RWin[rwID] = new RenderingWindow(rwID,file.getAbsolutePath(),this);
    Rectangle rectRWin=new Rectangle(vconf.rectRWin);
    rectRWin.translate(rwID*30,0);//tanslate
    RWin[rwID].setBounds(rectRWin);
    RWin[rwID].setCombo(vcWin.getCombo());
    RWin[rwID].requestFocusInWindow();
    RWin[rwID].addWindowListener(this);
    RWin[rwID].addKeyListener(keyCtrl);
    RWin[rwID].setVisible(true);

    //this menu
    //activate menu item
    vcWinMenu.miRenderWin[rwID].setText(rwID+": "+filePath);
    vcWinMenu.miRenderWin[rwID].setEnabled(true);
    //add recents
    int maxRecent=vcWinMenu.MAX_RECENT;
    for(int i=maxRecent-1;i>0;i--)
      vcWinMenu.miRecent[i].setText(vcWinMenu.miRecent[i-1].getText());
    vcWinMenu.miRecent[0].setText(filePath);

    vcWinMenu.updateMenu(RWin[rwID]);
  }


  //for window listener
  public void windowActivated(WindowEvent we){
    if(we.getWindow() instanceof RenderingWindow){
      //if window is renderingwindow
      RenderingWindow rw = (RenderingWindow)we.getWindow();
      activeRWinID=rw.getID();
      updateStatusString();
      vcWin.setDataRange(rw.getDataRange());
      vcWin.setAtomsTable();
      //vcWin.setFrameNm(rw.currentFrame,rw.atoms.totalFrame,false);
      vcWinMenu.updateMenu(rw);
    }
  }
  public void windowDeactivated(WindowEvent we){
  }
  public void windowClosing(WindowEvent we){
    if(we.getWindow() ==vcWin){
      System.out.println("saving configuration");
      myExit();
    }else if(we.getWindow() instanceof RenderingWindow){
      RenderingWindow rw = (RenderingWindow)we.getWindow();
      vconf.rectRWin=rw.getBounds();
      vcWinMenu.miRenderWin[rw.getID()].setText(rw.getID()+": null");
      vcWinMenu.miRenderWin[rw.getID()].setEnabled(false);
      RWin[rw.getID()]=null;
    }
  }
  public void windowClosed(WindowEvent we){
    /*
     * if(we.getWindow() instanceof RenderingWindow){
     *   RenderingWindow rw = (RenderingWindow)we.getWindow();
     *   vconf.rectRWin=rw.getBounds();
     *   vcWinMenu.miRenderWin[rw.getID()].setText(rw.getID()+": null");
     *   vcWinMenu.miRenderWin[rw.getID()].setEnabled(false);
     *   RWin[rw.getID()]=null;
     * }
     * if(we.getWindow() ==vcWin) myExit();
     */
  }

  public void windowOpened(WindowEvent we){
  }
  public void windowIconified(WindowEvent we){
  }
  public void windowDeiconified(WindowEvent we){
  }

}//end class

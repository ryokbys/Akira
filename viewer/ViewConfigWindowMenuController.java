package viewer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;

import tools.*;
import converter.*;
import viewer.*;
import viewer.renderer.*;
import viewer.viewConfigPanel.*;

import com.centerkey.utils.BareBonesBrowserLaunch;


public class ViewConfigWindowMenuController implements ActionListener {

  private Controller ctrl;
  private String rcFile;
  private String bmFile;

  //constructor
  public ViewConfigWindowMenuController(Controller ctrl){
    this.ctrl=ctrl;
    rcFile= ctrl.vconf.configDir+File.separator+"recents";
    bmFile= ctrl.vconf.configDir+File.separator+"bookmark";
    updateManager=new UpdateManager(ctrl);
  }
  UpdateManager updateManager;
  JFrame keyHelpFrame;
  public void actionPerformed(ActionEvent ae){

    if( ae.getSource() == miExit ){
      ctrl.myExit();
    }else if(ae.getSource() == miAbout ){
      updateManager.showAbout();
    }else if(ae.getSource() == miManual ){
      BareBonesBrowserLaunch.openURL("http://code.google.com/p/project-akira/wiki/Manual");
    }else if(ae.getSource() == miKeyHelp ){
      if(keyHelpFrame==null){
        keyHelpFrame=new JFrame("Key Help");
        keyHelpFrame.add(new KeyHelpPanel());
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        keyHelpFrame.setBounds(d.width-400,20,400,d.height-100);
      }
      keyHelpFrame.setVisible(true);
      keyHelpFrame.toFront();
    }else if(ae.getSource() == miUpdate ){
      updateManager.checkUpdate();
      updateManager.showDialog();
    }else if(ae.getSource() == miConv ){
      AkiraConverter ac=new AkiraConverter(true);
    }else if(ae.getSource() == miOpen ){
      String str = (new MyOpen()).getOpenFilename();
      ctrl.createRenderingWindow(str);
    }else if(ae.getSource() == miAddBookMark ){
      String str = ctrl.getActiveFilePath();
      if(str !=null){
        for(int i=MAX_BOOKMARK-1;i>0;i--)
          miBookMark[i].setText(miBookMark[i-1].getText());
        miBookMark[0].setText(str);
      }
    }else if(ae.getSource() == miBackRW ){
      ctrl.vconf.isBackRW=!ctrl.vconf.isBackRW;
      if(ctrl.getActiveRW()!=null){
        ctrl.getActiveRW().backRW.setVisible(ctrl.vconf.isBackRW);
        ctrl.getActiveRW().refresh();
      }
    }else if(ae.getSource() == miMaximize){
      //maximize
      Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
      ctrl.vcWin.setBounds(new Rectangle(2,  22, d.width-50, 250));
      ctrl.getActiveRW().setBounds(new Rectangle(2,  277, d.width-50, d.height-280));
    }else if(ae.getSource() == miTile){
      //tiling
      Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
      ctrl.vcWin.setBounds(new Rectangle(2,  22, d.width-50, 250));
      int n=ctrl.getRWinNum();
      int inc=0;
      for(int i=0;i<ctrl.MAX_RWIN;i++){
        if(ctrl.RWin[i]!=null){
          ctrl.RWin[i].setBounds(new Rectangle(2+(d.width-50)/n*inc,277,
                                               (d.width-50)/n, d.height-280));
          inc++;
        }
      }

    }else{
      //if 'Window' menu
      for(int i=0; i<ctrl.MAX_RWIN; i++){
        if(ae.getSource() == miRenderWin[i]){
          ctrl.RWin[i].toFront();
          break;
        }
      }
      //recents
      for(int i=0; i<MAX_RECENT; i++){
        if(ae.getSource() == miRecent[i]){
          String str=miRecent[i].getText();
          ctrl.createRenderingWindow(str);
          break;
        }
      }
      //bookmark
      for(int i=0; i<MAX_RECENT; i++){
        if(ae.getSource() == miBookMark[i]){
          String str=miBookMark[i].getText();
          ctrl.createRenderingWindow(str);
          break;
        }
      }
    }

    //
    RenderingWindow rw=ctrl.getActiveRW();
    String cmd = ae.getActionCommand();
    if(cmd.startsWith("vconfWin"))rw.ctrl.setFrontViewConfigWin(-1);
    //if(cmd.startsWith("keyHelpWin"))rw.ctrl.setFrontInfoWin(2);

    updateMenu(rw);
    if(rw!=null)rw.refresh();

  }
  public void updateMenu(RenderingWindow rw){
    if(miBackRW!=null)miBackRW.setSelected(ctrl.vconf.isBackRW);
  }



  /**
   * create menu
   */
  public JMenuBar getMenu(){

    //menu bar
    JMenuBar menuBar  = new JMenuBar();
    menuBar.add(getFileMenu());
    menuBar.add(getWindowMenu());
    menuBar.add(getHelpMenu());

    return menuBar;
  }


  private JMenuItem miKeyHelp;
  private JMenuItem miManual;
  private JMenuItem miLF;

  private JMenu getHelpMenu(){
    JMenu menu = new JMenu( "Help" );

    miKeyHelp=new JMenuItem("Keys");
    miKeyHelp.addActionListener( this );

    miManual=new JMenuItem("Manual (web)");
    miManual.addActionListener( this );
    miAbout  = new JMenuItem( "About Akira" );
    miAbout.addActionListener( this );
    miUpdate  = new JMenuItem( "Check update" );
    miUpdate.addActionListener( this );



    menu.add(miAbout);
    menu.add(miKeyHelp);
    menu.add(miManual);
    menu.addSeparator();
    menu.add(miUpdate);
    return menu;
  }


  //rendering window list
  JMenuItem[] miRenderWin=new JMenuItem[ctrl.MAX_RWIN];
  private JRadioButtonMenuItem miBackRW;
  private JMenuItem miMaximize,miTile;
  private JMenu getWindowMenu(){
    //render window list
    JMenu mnWins = new JMenu( "Window" );

    for( int i=0; i<ctrl.MAX_RWIN; i++ ){
      miRenderWin[i]= new JMenuItem(String.format("%d: null",i));
      miRenderWin[i].addActionListener( this );
      miRenderWin[i].setEnabled(false);
      mnWins.add( miRenderWin[i] );
    }
    if(ctrl.isEnjoyMode){
      miBackRW=new JRadioButtonMenuItem("BackReneringWindow",ctrl.vconf.isBackRW);
      miBackRW.addActionListener( this );
      mnWins.addSeparator();
      mnWins.add(miBackRW);
    }

    //arrange
    mnWins.addSeparator();
    miMaximize= new JMenuItem("Maximize Window");
    miMaximize.addActionListener( this );
    mnWins.add(miMaximize);
    miTile= new JMenuItem("Tile Window");
    miTile.addActionListener( this );
    mnWins.add(miTile);

    return mnWins;
  }


  //file menu
  private JMenuItem miAbout;
  private JMenuItem miOpen;
  public static final int MAX_RECENT=10;
  public JMenuItem[] miRecent=new JMenuItem[MAX_RECENT];
  public static final int MAX_BOOKMARK=10;
  private JMenuItem[] miBookMark=new JMenuItem[MAX_BOOKMARK];
  private JMenuItem miAddBookMark;
  private JMenuItem miConv;
  private JMenuItem miUpdate;
  private JMenuItem miExit;

  private JMenu getFileMenu(){
    JMenu mnFile = new JMenu( "File" );


    //main menu
    miOpen  = new JMenuItem( "Open" );
    miOpen.addActionListener( this );

    JMenu mnRecentList  = new JMenu( "Recents" );
    setRecents(mnRecentList);

    JMenu mnBookMarkList  = new JMenu( "Bookmarks" );
    setBookMarks(mnBookMarkList);


    miAddBookMark  = new JMenuItem( "Bookmark The Most Front Window" );
    miAddBookMark.addActionListener( this );


    miConv  = new JMenuItem( "Convert" );
    miConv.addActionListener( this );

    miExit  = new JMenuItem( "Exit" );
    miExit.addActionListener( this );

    //add file menu
    mnFile.add( miOpen );
    mnFile.add( mnRecentList );
    mnFile.add( mnBookMarkList );
    mnFile.add( miAddBookMark );
    mnFile.addSeparator();
    mnFile.add( miConv );
    mnFile.addSeparator();
    mnFile.add( miExit );
    return mnFile;
  }


  private void setRecents(JMenu mnRecentList){
    for(int i=0;i<MAX_RECENT;i++){
      miRecent[i]= new JMenuItem("null");
      miRecent[i].addActionListener( this );
      //miRecent[i].setEnabled(false);
      //add
      mnRecentList.add( miRecent[i] );
    }

    //set
    File dotFile = new File(rcFile);
    Scanner scan;
    String line;

    if( dotFile.exists() == true ){ // dotFile exists
      try{
        FileReader fr = new FileReader( rcFile );
        BufferedReader br = new BufferedReader( fr );

        for(int i=0;i<MAX_RECENT;i++){
          line=br.readLine();
          scan = new Scanner(line);
          miRecent[i].setText(scan.next());
        }
        br.close();
        fr.close();

      }catch(IOException e){
      }
    }else{
      for(int i=0;i<MAX_RECENT;i++){
        miRecent[i].setText("null");
      }
    }
  }


  /**
   * save recents list
   * package private
   */
  void saveRecents(){
    File dotFile = new File( rcFile);
    FileWriter fw;
    BufferedWriter bw;
    PrintWriter pw;
    String str;

    // open dotFile
    try{
      // if there is not dotFile, create new one
      fw = new FileWriter( dotFile );
      bw = new BufferedWriter( fw );
      pw = new PrintWriter( bw );
      //write
      for(int i=0;i<MAX_RECENT;i++){
        str = miRecent[i].getText();
        pw.println(str);
      }
      pw.close();
      bw.close();
      fw.close();
    }catch(IOException e){
    }


  }

  private void setBookMarks(JMenu mnBookMarkList){
    for(int i=0;i<MAX_BOOKMARK;i++){
      miBookMark[i]= new JMenuItem("null");
      miBookMark[i].addActionListener( this );
      //miBookMark[i].setEnabled(false);
      //add
      mnBookMarkList.add( miBookMark[i] );
    }
    //set
    File dotFile = new File(bmFile);
    Scanner scan;
    String line;
    if( dotFile.exists() == true ){ // dotFile exists
      try{
        FileReader fr = new FileReader( bmFile );
        BufferedReader br = new BufferedReader( fr );

        for(int i=0;i<MAX_BOOKMARK;i++){
          line=br.readLine();
          scan = new Scanner(line);
          miBookMark[i].setText(scan.next());
        }
        br.close();
        fr.close();

      }catch(IOException e){
      }
    }else{
      for(int i=0;i<MAX_BOOKMARK;i++){
        miBookMark[i].setText("null");
      }
    }
  }


  /**
   * save bookmarks
   * package private
   */
  void saveBookMark(){
    File dotFile = new File( bmFile);
    FileWriter fw;
    BufferedWriter bw;
    PrintWriter pw;
    String str;

    // open dotFile
    try{
      // if there is not dotFile, create new one
      fw = new FileWriter( dotFile );
      bw = new BufferedWriter( fw );
      pw = new PrintWriter( bw );
      //write
      for(int i=0;i<MAX_BOOKMARK;i++){
        str = miBookMark[i].getText();
        pw.println(str);
      }
      pw.close();
      bw.close();
      fw.close();
    }catch(IOException e){
    }
  }



}

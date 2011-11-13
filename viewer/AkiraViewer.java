package viewer;
import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.*;
import java.util.*;

import viewer.*;
import tools.*;

public class AkiraViewer{
  /**
   * This class is root of AKIRAView.
   *   The role of this class is analyzing command line arguments
   *   and newing Controller class.
   *
   * If you want to implement splash window or progress bar,
   * you should write codes to this.
   */

  public static void main(String[] args){

    //switch LOOK&FEELS according to OSNAME
    final String OSNAME = System.getProperty("os.name");
    if(System.getProperty("os.name").startsWith("Mac")){
      System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Akira");
      System.setProperty("apple.awt.fileDialogForDirectories", "true");
    }


    //options
    boolean isSilent=false;
    boolean isEnjoyMode=false;
    String[] argFile=new String[100];
    int nFile=0;

    if((System.getProperty("user.name")).startsWith("nakamura")) isEnjoyMode=true;

    //analyze command line options
    for(int i=0;i<args.length;i++){
      if(args[i].equals("-silent") || args[i].equals("-s")){
        isSilent=true;
      }else if(args[i].equals("-h") || args[i].equals("-help")){
        printHelp();
        System.exit(0);
      }else if(args[i].equals("-enjoy")){
        isEnjoyMode=true;
      }else if(args[i].endsWith(".Akira")){
        argFile[nFile]=args[i];
        nFile++;
      }else{
        System.out.println("unsupported argment: "+args[i]);
        System.exit(1);
      }
    }


    //print java.library.path
    if(isEnjoyMode)System.out.println("java.library.path= "+System.getProperty("java.library.path"));



    //splash window
    String filePath="/img/logo/Akira600x627.png";
    SplashWindow.splash(AkiraViewer.class.getResource(filePath));

    //switch action
    if(isSilent){
      //silent activate
      nFile=0;
      new AkiraViewer(argFile,nFile,isEnjoyMode);
      //dispose splash screen
      SplashWindow.disposeSplash();
    }else if(nFile==0){
      //dispose splash screen
      SplashWindow.disposeSplash();
      MyOpen myopen= new MyOpen();
      argFile[0] = myopen.getOpenFilename();
      nFile=1;
      new AkiraViewer(argFile,nFile,isEnjoyMode);
    }else{
      //normal activate
      new AkiraViewer(argFile,nFile,isEnjoyMode);
      //dispose splash screen
      SplashWindow.disposeSplash();
    }




  }//end of main

  //constructor
  public AkiraViewer(String[] file, int n, boolean isEnjoyMode){
    this.setLookAndFeel();
    Controller Controller = new Controller(isEnjoyMode);
    for(int i=0;i<n;i++)Controller.createRenderingWindow(file[i]);
  }

  /**
  * print command-line help
  */
  static void printHelp(){
    System.out.println("");
    System.out.println("<<< USAGE >>>");
    System.out.println("AKIRAView hoge.akira fuga.akira: open hoge.akira and fuga.akira");
    System.out.println("");
    System.out.println("OPTIONS");
    System.out.println("-h/-help: show this help");
    System.out.println("-silent: silent mode");
    System.out.println("-enjoy: enjoy mode");
  }

  //////////////////////////////////////////////////////////////////////
  // LookAndFeel matters
  //////////////////////////////////////////////////////////////////////
  // Possible Look & Feels
  private static final String mac     = "com.sun.java.swing.plaf.mac.MacLookAndFeel";
  private static final String metal   = "javax.swing.plaf.metal.MetalLookAndFeel";//new linux
  private static final String motif   = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";//old linux
  private static final String windows = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
  private static final String gtk     = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
  private static final String nimbus  = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

  void setLookAndFeel(){
    /*
     * //show LF index
     * UIManager.LookAndFeelInfo[] installedLafs = UIManager.getInstalledLookAndFeels();
     * for(int i=0; i<installedLafs.length; i++){
     *   UIManager.LookAndFeelInfo info=installedLafs[i];
     *   System.out.println(info.getName());
     * }
     */
    try{
      UIManager.setLookAndFeel( nimbus );
    }catch( Exception ex ){
      //System.out.println(" Nimbus not available!!");
    }
  }
}

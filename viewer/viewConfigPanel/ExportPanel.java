package viewer.viewConfigPanel;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

import com.sun.opengl.util.gl2.*;
import javax.media.opengl.awt.*;

import data.*;
import tools.*;
import viewer.renderer.*;
import viewer.*;

import org.sourceforge.jlibeps.epsgraphics.*;

public class ExportPanel extends JPanel implements ActionListener,
                                                          ChangeListener{

  private Controller ctrl;
  private ViewConfig vconf;
  private int fileNo=0;
  public ExportPanel(Controller ctrl){
    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;
    createPanel();
    fileNo=0;
  }

  private JButton btnWritePOV;
  private JButton btnWriteEPS;
  private JButton btnWriteMDInit;
  private JButton btnWriteQmclst;
  private JButton btnWriteQmclstSiO2;
  private JProgressBar progressBar;

  private void createPanel(){

    btnWritePOV=new JButton("POV");
    btnWritePOV.setFocusable(false);
    btnWritePOV.addActionListener( this );

    btnWriteEPS=new JButton("EPS");
    btnWriteEPS.setFocusable(false);
    btnWriteEPS.addActionListener( this );

    btnWriteMDInit=new JButton("MD init");
    btnWriteMDInit.setFocusable(false);
    btnWriteMDInit.addActionListener( this );

    btnWriteQmclst=new JButton("qmclst");
    btnWriteQmclst.setFocusable(false);
    btnWriteQmclst.addActionListener( this );

    btnWriteQmclstSiO2=new JButton("qmclst-SiO2");
    btnWriteQmclstSiO2.setFocusable(false);
    btnWriteQmclstSiO2.addActionListener( this );




    progressBar=new JProgressBar();
    progressBar.setFocusable(false);
    progressBar.setPreferredSize(new Dimension(200,40));
    progressBar.setStringPainted(true);
    progressBar.setMinimum(0);


    SpringLayout layout = new SpringLayout();
    this.setLayout( layout );






    layout.putConstraint( SpringLayout.NORTH, btnWritePOV, 10,
                          SpringLayout.NORTH, this);
    layout.putConstraint( SpringLayout.WEST, btnWritePOV, 10,
                          SpringLayout.WEST, this);

    layout.putConstraint( SpringLayout.NORTH, btnWriteEPS, 0,
                          SpringLayout.NORTH, btnWritePOV);
    layout.putConstraint( SpringLayout.WEST, btnWriteEPS, 5,
                          SpringLayout.EAST, btnWritePOV);

    layout.putConstraint( SpringLayout.NORTH, btnWriteMDInit, 0,
                          SpringLayout.NORTH, btnWriteEPS);
    layout.putConstraint( SpringLayout.WEST, btnWriteMDInit, 5,
                          SpringLayout.EAST, btnWriteEPS);


    layout.putConstraint( SpringLayout.NORTH, btnWriteQmclst, 0,
                          SpringLayout.NORTH, btnWriteMDInit);
    layout.putConstraint( SpringLayout.WEST, btnWriteQmclst, 5,
                          SpringLayout.EAST, btnWriteMDInit);

    layout.putConstraint( SpringLayout.NORTH, btnWriteQmclstSiO2, 0,
                          SpringLayout.NORTH, btnWriteQmclst);
    layout.putConstraint( SpringLayout.WEST, btnWriteQmclstSiO2, 5,
                          SpringLayout.EAST, btnWriteQmclst);




    layout.putConstraint( SpringLayout.NORTH, progressBar, 10,
                          SpringLayout.SOUTH, btnWritePOV);
    layout.putConstraint( SpringLayout.EAST, progressBar, -10,
                          SpringLayout.EAST, this);
    layout.putConstraint( SpringLayout.WEST, progressBar, 10,
                          SpringLayout.WEST, this);

    this.add(btnWritePOV);
    this.add(btnWriteEPS);
    this.add(btnWriteMDInit);
    add(btnWriteQmclst);
    add(btnWriteQmclstSiO2);
    this.add(progressBar);

  }



  public void stateChanged( ChangeEvent ce ){
  }

  private MyThread myThread;
  private viewer.renderer.Atoms atoms;
  public void actionPerformed( ActionEvent e ){
    this.atoms=ctrl.getActiveRW().atoms;

    myThread = new MyThread();
    fileNo++;
    if( e.getSource() == btnWritePOV ){
      outputFormat=0;
      myThread.start();
    }else if( e.getSource() == btnWriteEPS ){
      outputFormat=1;
      myThread.start();
    }else if( e.getSource() == btnWriteMDInit ){
      outputFormat=2;
      myThread.start();
    }else if( e.getSource() == btnWriteQmclst ){
      outputFormat=3;
      myThread.start();
    }else if( e.getSource() == btnWriteQmclstSiO2 ){
      outputFormat=4;
      myThread.start();
    }
  }

  private int outputFormat=0;
  class MyThread extends Thread {
    public void run(){
      String file;
      progressBar.setValue(0);
      progressBar.setMaximum(atoms.n-1);
      String workingDir=ctrl.getActiveRW().getFileDirectory();
      switch(outputFormat){
      case 0:
        file=String.format(workingDir+"/%04d.pov",fileNo);
        writePOVFile(file);
        break;
      case 1:
        file=String.format(workingDir+"/%04d.eps",fileNo);
        writeEPSFile(file);
        break;
      case 2:
        file=String.format(workingDir+"/%04d.init.d",fileNo);
        writeMDInitFile(file);
        break;
      case 3:
        file=String.format(workingDir+"/%04d.qmclst",fileNo);
        writeQmclstFile(file);
        break;
      case 4:
        file=String.format(workingDir+"/%04d.qmclst",fileNo);
        writeQmclstSiO2File(file);
        break;
      }
      progressBar.setValue(atoms.n-1);
    }
  }


  private void writeEPSFile(String filePath){

    float[] mvm =ctrl.getActiveRW().vp.mvm;

    // open
    try{

      FileOutputStream finalImage = new FileOutputStream(filePath);
      int width=500;
      int height=500;
      float ox=width/2.f;
      float oy=height/2.f;
      int scale=20;

      EpsGraphics2D g = new EpsGraphics2D("Title", finalImage, 0, 0, width, height);

      g.setColor(new Color(vconf.bgColor[0],vconf.bgColor[1],vconf.bgColor[2],vconf.bgColor[3]));
      g.fillRect(0,0,width,height);

      //draw atom
      for(int i=0;i<atoms.n;i++){
        /*
         * progressBar.setValue(i);
         * int itag = atoms.getTag(i);
         * if( itag<=0 || itag>Const.TAG) continue; //skip negative tag
         * itag--;//itag is set [1-10]. But array index is [0-9]
         * if(!ctrl.vconf.tagOnOff[itag])continue;//skip if radius ==0
         * float[] color = atoms.getAtomColor(i,itag);
         *
         * g.setColor(new Color(color[0],color[1],color[2],color[3]));
         * float x=(mvm[0]*atoms.ra[i][0]+
         *          mvm[4]*atoms.ra[i][1]+
         *          mvm[8]*atoms.ra[i][2])*scale;
         * float y=(mvm[1]*atoms.ra[i][0]+
         *          mvm[5]*atoms.ra[i][1]+
         *          mvm[9]*atoms.ra[i][2])*scale;
         * float z=(mvm[2]*atoms.ra[i][0]+
         *          mvm[6]*atoms.ra[i][1]+
         *          mvm[10]*atoms.ra[i][2])/scale;
         * //g.fillOval(ox+x,oy-y, z,z);
         * g.draw(new Ellipse2D.Float(ox+x,oy-y, z,z));
         */
      }


      //draw box
      g.setStroke(new BasicStroke(vconf.boxLineWidth));
      g.setColor(new Color(vconf.boxColor[0],vconf.boxColor[1],vconf.boxColor[2],vconf.boxColor[3]));
      for(int i=0;i<3;i++){
        for(int j=0;j<4;j++){
          int[] on= new int[3];
          if(i==0){
            on[0]=0;
            on[1]=j/2;
            on[2]=j%2;
          }else if(i==1){
            on[0]=j/2;
            on[1]=0;
            on[2]=j%2;
          }else if(i==2){
            on[0]=j/2;
            on[1]=j%2;
            on[2]=0;
          }

          float x1=ox+(mvm[0]*(on[0]*atoms.h[0][0]+on[1]*atoms.h[1][0]+on[2]*atoms.h[2][0])+
                       mvm[4]*(on[0]*atoms.h[0][1]+on[1]*atoms.h[1][1]+on[2]*atoms.h[2][1])+
                       mvm[8]*(on[0]*atoms.h[0][2]+on[1]*atoms.h[1][2]+on[2]*atoms.h[2][2])
                       )*scale;
          float y1=oy-(mvm[1]*(on[0]*atoms.h[0][0]+on[1]*atoms.h[1][0]+on[2]*atoms.h[2][0])+
                       mvm[5]*(on[0]*atoms.h[0][1]+on[1]*atoms.h[1][1]+on[2]*atoms.h[2][1])+
                       mvm[9]*(on[0]*atoms.h[0][2]+on[1]*atoms.h[1][2]+on[2]*atoms.h[2][2])
                       )*scale;
          float x2=ox+(mvm[0]*(on[0]*atoms.h[0][0]+on[1]*atoms.h[1][0]+on[2]*atoms.h[2][0]
                               +atoms.h[i][0])+
                       mvm[4]*(on[0]*atoms.h[0][1]+on[1]*atoms.h[1][1]+on[2]*atoms.h[2][1]
                               +atoms.h[i][1])+
                       mvm[8]*(on[0]*atoms.h[0][2]+on[1]*atoms.h[1][2]+on[2]*atoms.h[2][2]
                               +atoms.h[i][2])
                       )*scale;
          float y2=oy-(mvm[1]*(on[0]*atoms.h[0][0]+on[1]*atoms.h[1][0]+on[2]*atoms.h[2][0]
                               +atoms.h[i][0])+
                       mvm[5]*(on[0]*atoms.h[0][1]+on[1]*atoms.h[1][1]+on[2]*atoms.h[2][1]
                               +atoms.h[i][1])+
                       mvm[9]*(on[0]*atoms.h[0][2]+on[1]*atoms.h[1][2]+on[2]*atoms.h[2][2]
                               +atoms.h[i][2])
                       )*scale;

          //g.drawLine(x1,y1,x2,y2);
          g.draw(new Line2D.Float(x1,y1,x2,y2));
        }
      }



      g.flush();
      g.close();
      finalImage.close();
    }catch( IOException e ){
      System.out.println("---> Failed to write POV file");
      //System.out.println(e.getMessage());
    }
  }

  //about POV file
  private void writePOVFile(String filePath){
    FileWriter fw;
    BufferedWriter bw;
    PrintWriter pw;
    String str;

    viewer.renderer.Atoms atoms=ctrl.getActiveRW().atoms;
    float[] mvm =ctrl.getActiveRW().vp.mvm;

    // open
    try{
      fw = new FileWriter( filePath );
      bw = new BufferedWriter( fw );
      pw = new PrintWriter( bw );



      float z=0.f;
      float a=atoms.h[0][0];
      float b=atoms.h[1][1];
      float c=atoms.h[2][2];


      pw.println("#include \"colors.inc\"\n");
      //back ground
      pw.println("background { color rgb <1,1,1>}\n");

      //light
      str="light_source {\n"
        + "  <%.3f,%.3f,%.3f>\n"
        + "  color rgb <1,1,1>\n"
        + "}";
      pw.println(String.format(str,10.f*a,10.f*b,10.f*c));
      pw.println(String.format(str,10.f*a,z,z));
      pw.println(String.format(str,z,10.f*b,z));
      pw.println(String.format(str,z,z,10.f*c));
      pw.println("");

      //camera
      str="camera {\n"
        + "  perspective\n"
        + "  //orthographic\n"
        + "  //fisheye\n"
        + "  //ultra_wide_angle\n"
        + "  //omnimax\n"
        + "  //panoramic\n"
        + "  location <%.3f %.3f %.3f>\n"
        + "  look_at <0,0,0>\n"
        + "  //for left hand axis. dont change below two lines.\n"
        + "  right <-1.33, 0, 0>\n"
        + "  sky <0, 0, 1>\n"
        + "}";

      //float ex=mvm[0]*a+mvm[4]*b+mvm[8]*c;
      //float ey=mvm[1]*a+mvm[5]*b+mvm[9]*c;
      //float ez=mvm[2]*a+mvm[6]*b+mvm[10]*c;
      //pw.println(String.format(str,ex,ey,ez));
      pw.println(String.format(str,a*1.2f,b*1.2f,c*1.2f));
      pw.println("");


      //box
      str="cylinder{\n"
        + "  <%.3f,%.3f,%.3f>,<%.3f,%.3f,%.3f>,0.4 open\n"
        + "  texture{\n"
        + "    pigment{color %s}\n"
        + "    finish{\n"
        + "      ambient 0.1\n"
        + "      diffuse 0.7\n"
        + "      phong 1\n"
        + "      reflection 0.1\n"
        + "      metallic\n"
        + "    }\n"
        + "  }\n"
        + "  no_shadow\n"
        + "}";
      pw.println(String.format(str,z,z,z,a,z,z,"Red"));
      pw.println(String.format(str,a,z,z,a,z,c,"Black"));
      pw.println(String.format(str,a,z,c,z,z,c,"Black"));
      pw.println(String.format(str,z,z,c,z,z,z,"Blue"));
      pw.println(String.format(str,z,z,z,z,b,z,"Green"));
      pw.println(String.format(str,a,z,z,a,b,z,"Black"));
      pw.println(String.format(str,a,z,c,a,b,c,"Black"));
      pw.println(String.format(str,z,z,c,z,b,c,"Black"));
      pw.println(String.format(str,z,b,z,a,b,z,"Black"));
      pw.println(String.format(str,a,b,z,a,b,c,"Black"));
      pw.println(String.format(str,a,b,c,z,b,c,"Black"));
      pw.println(String.format(str,z,b,c,z,b,z,"Black"));
      pw.println("");



      //shpere as atom
      for(int i=0;i<atoms.n;i++){
        progressBar.setValue(i);

        //int itag = atoms.getTag(i);
        //if( itag<=0 || itag>Const.TAG) continue; //skip negative tag
        //itag--;//itag is set [1-10]. But array index is [0-9]
        //if(!ctrl.vconf.tagOnOff[itag])continue;//skip if radius ==0
        //float[] color = atoms.getAtomColor(i,itag);

        str="sphere {\n"
          + "  <%.3f, %.3f, %.3f>, %.3f\n"
          + "  texture {\n"
          + "    finish {\n"
          + "      ambient 0.1\n"
          + "      diffuse 0.2\n"
          + "      phong 1\n"
          + "    }\n"
          + "    pigment { rgb <%.3f,%.3f,%.3f>}\n"
          + "  }\n"
          + "}";

        /*
         * pw.println(String.format(str
         *                          ,atoms.ra[i][0],atoms.ra[i][1],atoms.ra[i][2]
         *                          ,(float)ctrl.vconf.tagRadius[itag]
         *                          ,color[0],color[1],color[2]
         *                          ));
         */

      }

      pw.close();
      bw.close();
      fw.close();
    }catch( IOException e ){
      System.out.println("---> Failed to write POV file");
      //System.out.println(e.getMessage());
    }

  }

  private void writeMDInitFile(String filePath){
    FileWriter fw;
    BufferedWriter bw;
    PrintWriter pw;
    String str;

    viewer.renderer.Atoms atoms=ctrl.getActiveRW().atoms;
    // open
    try{
      fw = new FileWriter( filePath );
      bw = new BufferedWriter( fw );
      pw = new PrintWriter( bw );



      int n=0;
      for(int i=0;i<atoms.n;i++){
        if(atoms.vtag[i]<0)continue;
        n++;
      }
      System.out.println(String.format("output Natom: %d",n));

      pw.println(String.format("%d",n));
      pw.println(String.format("%e %e %e",
                               atoms.h[0][0],atoms.h[0][1],atoms.h[0][2]));
      pw.println(String.format("%e %e %e",
                               atoms.h[1][0],atoms.h[1][1],atoms.h[1][2]));
      pw.println(String.format("%e %e %e",
                               atoms.h[2][0],atoms.h[2][1],atoms.h[2][2]));

      //shpere as atom
      for(int i=0;i<atoms.n;i++){
        progressBar.setValue(i);
        //skip
        if(atoms.vtag[i]<0)continue;

        float[] out = new float[3];
        for(int k=0; k<3; k++) out[k] = atoms.hinv[k][0]*atoms.r[i][0]
                                 + atoms.hinv[k][1]*atoms.r[i][1]
                                 + atoms.hinv[k][2]*atoms.r[i][2];
        pw.println(String.format("%e %e %e %e %e %e %e %e %e %e"
                                 ,(float)atoms.tag[i],out[0],out[1],out[2]
                                 ,0e0,0e0,0e0
                                 ,out[0],out[1],out[2]
                                 ));

      }

      pw.close();
      bw.close();
      fw.close();
    }catch( IOException e ){
      System.out.println("---> Failed to write MD init file");
      //System.out.println(e.getMessage());
    }

  }

  private void writeQmclstFile(String filePath){
    FileWriter fw;
    BufferedWriter bw;
    PrintWriter pw;
    String str;

    viewer.renderer.Atoms atoms=ctrl.getActiveRW().atoms;
    // open
    try{
      fw = new FileWriter( filePath );
      bw = new BufferedWriter( fw );
      pw = new PrintWriter( bw );



      int n=0;
      for(int i=0;i<atoms.n;i++){
        if(atoms.vtag[i]<0)continue;
        n++;
      }
      System.out.println(String.format("output Natom: %d",n));

      pw.println(String.format("%d",n));

      for(int i=0;i<atoms.n;i++){
        progressBar.setValue(i);
        if(atoms.vtag[i]<0)continue;

        float[] out = new float[3];
        for(int k=0; k<3; k++) out[k] = atoms.hinv[k][0]*atoms.r[i][0]
                                 + atoms.hinv[k][1]*atoms.r[i][1]
                                 + atoms.hinv[k][2]*atoms.r[i][2];
        pw.println(String.format("%e %e %e",out[0],out[1],out[2]));
      }

      pw.close();
      bw.close();
      fw.close();
    }catch( IOException e ){
      System.out.println("---> Failed to write MD init file");
      //System.out.println(e.getMessage());
    }

  }
  private void writeQmclstSiO2File(String filePath){
    FileWriter fw;
    BufferedWriter bw;
    PrintWriter pw;
    String str;

    viewer.renderer.Atoms atoms=ctrl.getActiveRW().atoms;
    // open
    try{
      fw = new FileWriter( filePath );
      bw = new BufferedWriter( fw );
      pw = new PrintWriter( bw );


      double bond=1.6*1.3/0.529;
      double bond2=bond*bond;

      //-----cal coordinatio num
      int[] icoord=new int[atoms.n];
      for(int i=0;i<atoms.n;i++)icoord[i]=0;
      for(int i=0;i<atoms.n-1;i++){
        if(atoms.vtag[i]<0)continue;
        for(int j=i+1;j<atoms.n;j++){
          if(atoms.vtag[j]<0)continue;
          float dr2=0;
          for(int k=0; k<3; k++)
            dr2+=(atoms.r[i][k]-atoms.r[j][k])*(atoms.r[i][k]-atoms.r[j][k]);
          if(dr2< bond2){
            icoord[i]++;
            icoord[j]++;
          }
        }
      }
      //-----delete isolated Si
      for(int i=0;i<atoms.n;i++){
        if(atoms.vtag[i]<0)continue;
        if(atoms.tag[i]==1 && icoord[i]<4)atoms.vtag[i]=-1;
      }
      //-----recal coordinatio num
      for(int i=0;i<atoms.n;i++)icoord[i]=0;
      for(int i=0;i<atoms.n-1;i++){
        if(atoms.vtag[i]<0)continue;
        for(int j=i+1;j<atoms.n;j++){
          if(atoms.vtag[j]<0)continue;
          float dr2=0;
          for(int k=0; k<3; k++)
            dr2+=(atoms.r[i][k]-atoms.r[j][k])*(atoms.r[i][k]-atoms.r[j][k]);
          if(dr2< bond2){
            icoord[i]++;
            icoord[j]++;
          }
        }
      }
      //delete O
      for(int i=0;i<atoms.n;i++){
        if(atoms.vtag[i]<0)continue;
        if(atoms.tag[i]==2 && icoord[i]<=1)atoms.vtag[i]=-1;
      }


      int n=0;
      for(int i=0;i<atoms.n;i++){
        if(atoms.vtag[i]<0)continue;
        n++;
      }
      System.out.println(String.format("output Natom: %d",n));

      pw.println(String.format("%d",n));

      for(int i=0;i<atoms.n;i++){
        progressBar.setValue(i);
        if(atoms.vtag[i]<0)continue;

        float[] out = new float[3];
        for(int k=0; k<3; k++) out[k] = atoms.hinv[k][0]*atoms.r[i][0]
                                 + atoms.hinv[k][1]*atoms.r[i][1]
                                 + atoms.hinv[k][2]*atoms.r[i][2];
        pw.println(String.format("%e %e %e",out[0],out[1],out[2]));
      }

      pw.close();
      bw.close();
      fw.close();
    }catch( IOException e ){
      System.out.println("---> Failed to write MD init file");
      //System.out.println(e.getMessage());
    }

  }


}

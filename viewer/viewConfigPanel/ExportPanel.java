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
import viewer.viewConfigPanel.*;

import org.sourceforge.jlibeps.epsgraphics.*;

public class ExportPanel extends JPanel implements ActionListener{

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

    add(btnWritePOV);
    add(btnWriteEPS);
    add(btnWriteMDInit);
    add(btnWriteQmclst);
    createPluginButton();
  }
  private ArrayList<MyPluginInterface> plugins = new ArrayList<MyPluginInterface>();
  private ArrayList<String> pluginName = new ArrayList<String>();
  private void createPluginButton(){
    String dir = System.getProperty("user.dir")+"/plugin";
    try {
      File f = new File(dir);
      String[] files = f.list();
      //System.out.println(String.format("list: %d",files.length));
      for (int i = 0; i < files.length; i++) {
        //System.out.println(files[i]);
        if (files[i].endsWith(".class")){
          String classname = files[i].substring(0,files[i].length() - ".class".length());
          Class c = Class.forName("plugin."+classname);
          //System.out.println("CHECK: " + classname);
          Class[] ifs = c.getInterfaces();
          for(int j = 0; j < ifs.length; j++){
            //System.out.println("CHECK: " + ifs[j]);
            if (ifs[j].equals(MyPluginInterface.class)){
              //System.out.println("**this is MyPlugin**");
              MyPluginInterface plg = (MyPluginInterface)c.newInstance();
              plugins.add(plg);
              pluginName.add(classname);
              //System.out.println(classname+" added");
              //plugin.doPlugin();
            }
          }//j

        }
      }
    } catch (ClassNotFoundException ex) {
      //System.out.println(" --noclass");
      //ex.printStackTrace();
    }catch(Exception ex){
      //System.out.println(" --exception");
      //ex.printStackTrace();
    }
    //System.out.println("END.");

    //add
    for(int i=0;i<plugins.size();i++){
      JButton btn=new JButton(pluginName.get(i));
      btn.setActionCommand(pluginName.get(i));
      btn.addActionListener( this );
      add(btn);
    }

  }

  public void actionPerformed( ActionEvent e ){
    viewer.renderer.Atoms atoms=ctrl.getActiveRW().atoms;
    String dir=ctrl.getActiveRW().getFileDirectory();
    fileNo++;

    if( e.getSource() == btnWritePOV ){
      writePOVFile(dir,fileNo,
                   atoms.h,atoms.hinv,atoms.n,atoms.r,
                   atoms.tag,atoms.vtag);
    }else if( e.getSource() == btnWriteEPS ){
      writeEPSFile(dir,fileNo,
                   atoms.h,atoms.hinv,atoms.n,atoms.r,
                   atoms.tag,atoms.vtag);
    }else if( e.getSource() == btnWriteMDInit ){
      writeMDInitFile(dir,fileNo,
                      atoms.h,atoms.hinv,atoms.n,atoms.r,
                      atoms.tag,atoms.vtag);
    }else if( e.getSource() == btnWriteQmclst ){
      writeQmclstFile(dir,fileNo,
                      atoms.h,atoms.hinv,atoms.n,atoms.r,
                      atoms.tag,atoms.vtag);
    }

    for(int i=0;i<plugins.size();i++){
      if(e.getActionCommand().equals(pluginName.get(i))){
        (plugins.get(i)).exec(dir,fileNo,
                              atoms.h,atoms.hinv,atoms.n,atoms.r,
                              atoms.tag,atoms.vtag);
        break;
      }
    }
  }

  private void writeEPSFile(String dir,int fn,
                            float[][] h,
                            float[][] hinv,
                            int n,
                            float[][] r,
                            byte[] tag,
                            int[] vtag
                            ){

    String filePath=String.format(dir+"/%04d.eps",fn);
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
      for(int i=0;i<n;i++){
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

          float x1=ox+(mvm[0]*(on[0]*h[0][0]+on[1]*h[1][0]+on[2]*h[2][0])+
                       mvm[4]*(on[0]*h[0][1]+on[1]*h[1][1]+on[2]*h[2][1])+
                       mvm[8]*(on[0]*h[0][2]+on[1]*h[1][2]+on[2]*h[2][2])
                       )*scale;
          float y1=oy-(mvm[1]*(on[0]*h[0][0]+on[1]*h[1][0]+on[2]*h[2][0])+
                       mvm[5]*(on[0]*h[0][1]+on[1]*h[1][1]+on[2]*h[2][1])+
                       mvm[9]*(on[0]*h[0][2]+on[1]*h[1][2]+on[2]*h[2][2])
                       )*scale;
          float x2=ox+(mvm[0]*(on[0]*h[0][0]+on[1]*h[1][0]+on[2]*h[2][0]
                               +h[i][0])+
                       mvm[4]*(on[0]*h[0][1]+on[1]*h[1][1]+on[2]*h[2][1]
                               +h[i][1])+
                       mvm[8]*(on[0]*h[0][2]+on[1]*h[1][2]+on[2]*h[2][2]
                               +h[i][2])
                       )*scale;
          float y2=oy-(mvm[1]*(on[0]*h[0][0]+on[1]*h[1][0]+on[2]*h[2][0]
                               +h[i][0])+
                       mvm[5]*(on[0]*h[0][1]+on[1]*h[1][1]+on[2]*h[2][1]
                               +h[i][1])+
                       mvm[9]*(on[0]*h[0][2]+on[1]*h[1][2]+on[2]*h[2][2]
                               +h[i][2])
                       )*scale;

          //g.drawLine(x1,y1,x2,y2);
          g.draw(new Line2D.Float(x1,y1,x2,y2));
        }
      }



      g.flush();
      g.close();
      finalImage.close();
    }catch( IOException e ){
      System.out.println("---> Failed to write EPS file");
      //System.out.println(e.getMessage());
    }
  }

  //about POV file
  private void writePOVFile(String dir,int fn,
                            float[][] h,
                            float[][] hinv,
                            int n,
                            float[][] r,
                            byte[] tag,
                            int[] vtag
                            ){

    String filePath=String.format(dir+"/%04d.pov",fn);
    FileWriter fw;
    BufferedWriter bw;
    PrintWriter pw;
    String str;

    float[] mvm =ctrl.getActiveRW().vp.mvm;

    // open
    try{
      fw = new FileWriter( filePath );
      bw = new BufferedWriter( fw );
      pw = new PrintWriter( bw );



      float z=0.f;
      float a=h[0][0];
      float b=h[1][1];
      float c=h[2][2];


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
      for(int i=0;i<n;i++){
        //int itag = getTag(i);
        //if( itag<=0 || itag>Const.TAG) continue; //skip negative tag
        //itag--;//itag is set [1-10]. But array index is [0-9]
        //if(!ctrl.vconf.tagOnOff[itag])continue;//skip if radius ==0
        //float[] color = getAtomColor(i,itag);

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
         *                          ,ra[i][0],ra[i][1],ra[i][2]
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

  private void writeMDInitFile(String dir, int fn,
                               float[][] h,
                               float[][] hinv,
                               int n,
                               float[][] r,
                               byte[] tag,
                               int[] vtag
                               ){
    String filePath=String.format(dir+"/%04d.init.d",fn);

    FileWriter fw;
    BufferedWriter bw;
    PrintWriter pw;
    String str;

    // open
    try{
      fw = new FileWriter( filePath );
      bw = new BufferedWriter( fw );
      pw = new PrintWriter( bw );



      int nv=0;
      for(int i=0;i<n;i++){
        if(vtag[i]<0)continue;
        nv++;
      }
      System.out.println(String.format("output Natom: %d",nv));

      pw.println(String.format("%d",n));
      pw.println(String.format("%e %e %e",h[0][0],h[0][1],h[0][2]));
      pw.println(String.format("%e %e %e",h[1][0],h[1][1],h[1][2]));
      pw.println(String.format("%e %e %e",h[2][0],h[2][1],h[2][2]));

      //shpere as atom
      for(int i=0;i<n;i++){
        //skip
        if(vtag[i]<0)continue;

        float[] out = new float[3];
        for(int k=0; k<3; k++) out[k] =
                                 hinv[k][0]*r[i][0]+
                                 hinv[k][1]*r[i][1]+
                                 hinv[k][2]*r[i][2];
        pw.println(String.format("%e %e %e %e %e %e %e %e %e %e"
                                 ,(float)tag[i],out[0],out[1],out[2]
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

  private void writeQmclstFile(String dir, int fn,
                               float[][] h,
                               float[][] hinv,
                               int n,
                               float[][] r,
                               byte[] tag,
                               int[] vtag
                               ){
    String filePath=String.format(dir+"/%04d.qmclst",fn);
    FileWriter fw;
    BufferedWriter bw;
    PrintWriter pw;
    String str;

    // open
    try{
      fw = new FileWriter( filePath );
      bw = new BufferedWriter( fw );
      pw = new PrintWriter( bw );



      int nv=0;
      for(int i=0;i<n;i++){
        if(vtag[i]<0)continue;
        nv++;
      }
      System.out.println(String.format("output Natom: %d",nv));

      pw.println(String.format("%d",n));

      for(int i=0;i<n;i++){
        if(vtag[i]<0)continue;

        float[] out = new float[3];
        for(int k=0; k<3; k++)
          out[k] = hinv[k][0]*r[i][0]+hinv[k][1]*r[i][1]+hinv[k][2]*r[i][2];

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

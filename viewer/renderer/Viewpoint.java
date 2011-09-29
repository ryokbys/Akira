package viewer.renderer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;

import com.jogamp.opengl.util.gl2.*;
import javax.media.opengl.awt.*;

import viewer.*;
import viewer.renderer.*;

public class Viewpoint{

  private GL2 gl;
  private GLU glu;
  private GLUT glut;


  public float maxmbs;
  public float[] mbs = new float[3];

  public float[] center = { 0.0f, 0.0f, 0.0f };
  public float[] eye    = new float[3];
  public float[] up     = { 0.0f, 1.0f, 0.0f };

  public int viewportMode;
  int x;
  int y;
  int width;
  int height;


  public float fovy;
  public float zNear;
  public float zFar;

  public float[] objCenter = { 0.0f, 0.0f, 0.0f };
  public float rotX, rotY, rotZ;
  public float objScale = 1.0f;
  public float[] mvm = new float[16];
  final float[] unitMVM = { 1.0f, 0.0f, 0.0f, 0.0f,
                            0.0f, 1.0f, 0.0f, 0.0f,
                            0.0f, 0.0f, 1.0f, 0.0f,
                            0.0f, 0.0f, 0.0f, 1.0f };

  public final float[] xMVM = { 0.0f, 0.0f, 1.0f, 0.0f,
                         1.0f, 0.0f, 0.0f, 0.0f,
                         0.0f, 1.0f, 0.0f, 0.0f,
                         0.0f, 0.0f, 0.0f, 1.0f };
  public final float[] yMVM = { -1.0f, 0.0f, 0.0f, 0.0f,
                         0.0f, 0.0f, 1.0f, 0.0f,
                         0.0f, 1.0f, 0.0f, 0.0f,
                         0.0f, 0.0f, 0.0f, 1.0f };



  Axis axis;
  public void setAxis(Axis axis){
    this.axis=axis;
  }

  //private GLJPanel canvas;
  private GLCanvas canvas;
  private ViewConfig vconf;

  public Viewpoint(RenderingWindow rw){
    this.gl = rw.gl;
    this.glu = rw.glu;
    this.glut = rw.glut;
    this.canvas = rw.glCanvas;
    this.vconf=rw.vconf;

    //opposite corner
    for(int k=0; k<3; k++)
      mbs[k] = (rw.atoms.h[k][0] +rw.atoms.h[k][1] +rw.atoms.h[k][2]);

    //max
    maxmbs = 0.0f;
    for(int i=0; i<3; i++) if(mbs[i] > maxmbs )maxmbs = mbs[i];
    if(maxmbs==0.f)maxmbs=1.f;

    setHome();

    //set viewpoint file
    String str;
    for(int i=0;i<nVPFile;i++){
      str=vconf.configDir + File.separator+String.format("vp%02d",i);
      vpFile[i]=new File(str);
    }
  }//end of constructor (Viewpoint)

  public final static int nVPFile=5;
  File[] vpFile= new File[nVPFile];

  public void setHome(){
    center[0] = 0.0f;
    center[1] = 0.0f;
    center[2] = 0.0f;

    eye[0] = 0.0f;
    eye[1] = 0.0f;
    eye[2] = 1.5f*maxmbs;

    fovy = 40.0f;
    zNear = 0.1f;
    zFar = maxmbs*2.0f/3 + eye[2];

    objCenter[0] = -mbs[0]/2;
    objCenter[1] = -mbs[1]/2;
    objCenter[2] = -mbs[2]/2;

    objScale = 1.0f;
    for( int k=0; k<16; k++ ) mvm[k] = unitMVM[k];

  }

  public void resetRotation(char ixyz){
    objCenter[0] = -mbs[0]/2;
    objCenter[1] = -mbs[1]/2;
    objCenter[2] = -mbs[2]/2;

    if(ixyz=='x'){
      for( int k=0; k<16; k++ ) mvm[k] = xMVM[k];
    }else if(ixyz=='y'){
      for( int k=0; k<16; k++ ) mvm[k] = yMVM[k];
    }else if(ixyz=='z'){
      for( int k=0; k<16; k++ ) mvm[k] = unitMVM[k];
    }

  }


  public void setViewportMode(){
    vconf.viewMode = 1- vconf.viewMode;
    canvas.reshape( x, y, width, height );
  }

  public void lookAt(){
    glu.gluLookAt( eye[0],    eye[1],    eye[2],
                   center[0], center[1], center[2],
                   up[0],     up[1],     up[2] );
  }

  public void viewport( int x, int y,int width, int height ){
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    gl.glViewport( 0, 0, width, height );
    gl.glMatrixMode( GL2.GL_PROJECTION );
    gl.glLoadIdentity();
    float aspect = (float)width / (float)height;
    port(aspect);

    gl.glMatrixMode( GL2.GL_MODELVIEW );
    gl.glLoadIdentity();
    glu.gluLookAt( eye[0],    eye[1],    eye[2],
                   center[0], center[1], center[2],
                   up[0],     up[1],     up[2] );
  }

  public void port( float aspect ){
    if( vconf.viewMode == 0 ){
      glu.gluPerspective( fovy, aspect, zNear, zFar );
    }
    else {
      gl.glOrtho( -maxmbs*aspect, maxmbs*aspect,
                  -maxmbs, maxmbs,
                  zNear, zFar );
    }
  }


  public void setEyePosition( int dy ){
    eye[2] += 0.05f*dy*maxmbs*0.1f;
    //eye[2] += 0.5f*dy;
    if( eye[2] < 0.0f ){
      eye[2] = 0.001f;
    }
  }

  public void setObjectTranslate( int dx, int dy ){
    objCenter[0] = -0.05f*dx*maxmbs*0.1f;
    objCenter[1] = -0.05f*dy*maxmbs*0.1f;
  }


  public void setObjectRotate( float dx, float dy ){
    //Use degree not radian in OpneGL
    rotY = dx;
    rotX = dy;
  }
  public void setObjectRotateMouse( float dx, float dy ){
    //circular with width/height
    rotY = 360.0f * (dx/(float)width);
    rotX = 360.0f * (dy/(float)height);
  }
  public void setObjectRotateZ( float dz ){
    rotZ = dz;
  }

  public void setObjectScale( int add ){
    objScale -= 0.05f*add;
    zFar *= objScale;
    if( zFar < (maxmbs*2.0f/3+eye[2]) ){
      zFar = maxmbs*2.0f/3 + eye[2];
    }
    canvas.reshape( x, y, width, height );
  }
  public void setObjectScale( float add ){
    objScale = add;
    zFar *= objScale;
    if( zFar < (maxmbs*2.0f/3+eye[2]) ){
      zFar = maxmbs*2.0f/3 + eye[2];
    }
    canvas.reshape( x, y,width, height );
  }

  public float[] curquat = new float[4];
  public float[] lastquat = new float[4];
  public float[] m = { 1.0f, 0.0f, 0.0f, 0.0f,
                       0.0f, 1.0f, 0.0f, 0.0f,
                       0.0f, 0.0f, 1.0f, 0.0f,
                       0.0f, 0.0f, 0.0f, 1.0f };

  public void saveViewPoint(){
    for(int i=nVPFile-2;i>=0;i--){
      if(vpFile[i].exists())vpFile[i].renameTo(vpFile[i+1]);
    }

    try {
      FileWriter fw = new FileWriter( vpFile[0] );
      BufferedWriter bw = new BufferedWriter( fw );
      PrintWriter pw = new PrintWriter( bw );

      pw.println( "object matrix:" );
      String m1 = String.format("%15.8f %15.8f %15.8f %15.8f",
                                mvm[0], mvm[1], mvm[2], mvm[3] );
      String m2 = String.format("%15.8f %15.8f %15.8f %15.8f",
                                mvm[4], mvm[5], mvm[6], mvm[7] );
      String m3 = String.format("%15.8f %15.8f %15.8f %15.8f",
                                mvm[8], mvm[9], mvm[10], mvm[11] );
      String m4 = String.format("%15.8f %15.8f %15.8f %15.8f",
                                mvm[12], mvm[13], mvm[14], mvm[15] );
      pw.println( m1 );
      pw.println( m2 );
      pw.println( m3 );
      pw.println( m4 );

      pw.println( String.format("eye    : %15.8f %15.8f %15.8f",
                                eye[0], eye[1], eye[2] ) );
      pw.println( String.format("center : %15.8f %15.8f %15.8f",
                                center[0], center[1], center[2] ) );
      pw.println( String.format("up     : %15.8f %15.8f %15.8f",
                                up[0], up[1], up[2] ) );

      pw.println( "axis matrix:" );
      String am1 = String.format("%15.8f %15.8f %15.8f %15.8f",
                                 axis.mvm[0], axis.mvm[1], axis.mvm[2], axis.mvm[3] );
      String am2 = String.format("%15.8f %15.8f %15.8f %15.8f",
                                 axis.mvm[4], axis.mvm[5], axis.mvm[6], axis.mvm[7] );
      String am3 = String.format("%15.8f %15.8f %15.8f %15.8f",
                                 axis.mvm[8], axis.mvm[9], axis.mvm[10], axis.mvm[11] );
      String am4 = String.format("%15.8f %15.8f %15.8f %15.8f",
                                 axis.mvm[12], axis.mvm[13], axis.mvm[14], axis.mvm[15] );
      pw.println( am1 );
      pw.println( am2 );
      pw.println( am3 );
      pw.println( am4 );

      pw.close();
      bw.close();
      fw.close();
    }
    catch ( IOException ioe ){
    }
  }

  public void revertViewPoint(int ith){
    read(vpFile[ith]);
  }

  void read(File file){
    try {
      FileReader fr = new FileReader(file);
      BufferedReader br = new BufferedReader( fr );
      String line;
      Scanner sc;
      line = br.readLine();
      for( int k=0; k<4; k++ ){
        line = br.readLine();
        sc = new Scanner( line );
        mvm[k*4+0] = sc.nextFloat();
        mvm[k*4+1] = sc.nextFloat();
        mvm[k*4+2] = sc.nextFloat();
        mvm[k*4+3] = sc.nextFloat();
      }
      line = br.readLine();
      sc = new Scanner( line );
      sc.next();
      sc.next();
      for( int k=0; k<3; k++ ){
        eye[k] = sc.nextFloat();
      }
      line = br.readLine();
      sc = new Scanner( line );
      sc.next();
      sc.next();
      for( int k=0; k<3; k++ ){
        center[k] = sc.nextFloat();
      }
      line = br.readLine();
      sc = new Scanner( line );
      sc.next();
      sc.next();
      for( int k=0; k<3; k++ ){
        up[k] = sc.nextFloat();
      }
      line = br.readLine();
      for( int k=0; k<4; k++ ){
        line = br.readLine();
        sc = new Scanner( line );
        axis.mvm[k*4+0] = sc.nextFloat();
        axis.mvm[k*4+1] = sc.nextFloat();
        axis.mvm[k*4+2] = sc.nextFloat();
        axis.mvm[k*4+3] = sc.nextFloat();
      }
      br.close();
      fr.close();
    }
    catch ( IOException e ){
      System.out.println("no read: "+file);
    }
  }

}

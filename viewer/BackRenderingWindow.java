package viewer;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;

import com.jogamp.opengl.util.gl2.*;
import javax.media.opengl.awt.*;


import tools.*;
import data.*;
import viewer.*;
import viewer.renderer.*;
import viewer.renderer.atlantis.*;
import viewer.LF.*;

public class BackRenderingWindow extends JFrame implements GLEventListener{

  //use classes
  GLAutoDrawable drawable;
  GL2   gl;
  GLU  glu;
  GLUT glut;

  public GLCanvas glCanvas;

  Controller ctrl;
  ViewConfig vconf;
  RenderingWindow rw;



  float objScale=1.f;
  float[] objCenter={0.f,0.f,0.f};
  float rotX=0.f;
  float rotY=0.f;
  float rotZ=0.f;
  float[] mvm = new float[16];
  boolean isRendering;
  public void setViewPointVars(float s, float x,float y, float z, float[] c,float[] m,boolean isRendering){
    objScale=s;
    rotX=x;
    rotY=y;
    rotZ=z;
    objCenter[0]=c[0];
    objCenter[1]=c[1];
    objCenter[2]=c[2];
    for(int i=0;i<16;i++)mvm[i]=m[i];
    this.isRendering=isRendering;
  }

  /* constructor */
  public BackRenderingWindow(Controller ctrl, RenderingWindow rw,String filePath){
    super("Back View ::"+filePath);
    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;
    this.rw=rw;
    setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
    initialize();

    Rectangle rect=rw.getBounds();
    rect.translate(rect.width+5,0);//tanslate
    rect.setSize(300,300);
    setBounds(rect);
    setVisible(vconf.isBackRW);
  }


  /* called by this */
  void initialize(){
    glCanvas = new GLCanvas(null,null, rw.drawable.getContext(),null);
    glCanvas.addGLEventListener( this );
    add( glCanvas );

  }

  /* called by OpenGL (after initialize()) */
  public void init( GLAutoDrawable drawable ){
    this.drawable = drawable;
    gl   = drawable.getGL().getGL2();
    glu  = new GLU();
    glut = new GLUT();

    //GLContext glcontext = drawable.getContext();
    //glcontext.setSynchronized(true);

    gl.glShadeModel(GL2.GL_SMOOTH);
    gl.glEnable(GL2.GL_DEPTH_TEST);
    gl.glEnable(GL2.GL_NORMALIZE);
    gl.glEnable(GL2.GL_LINE_SMOOTH);
    gl.glEnable(GL2.GL_BLEND);//for alpha blending
    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

    gl.glClearColor(vconf.bgColor[0],vconf.bgColor[1],
                    vconf.bgColor[2],vconf.bgColor[3]);


    gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_POSITION, vconf.lightPos, 0 );
    gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_DIFFUSE,  vconf.lightDif, 0 );
    gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_AMBIENT,  vconf.lightAmb, 0 );
    gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_SPECULAR, vconf.lightSpc, 0 );
    gl.glEnable( GL2.GL_LIGHTING );
    gl.glEnable( GL2.GL_LIGHT0 );

  }

  /* called by OpenGL */
  public void reshape(GLAutoDrawable drawable,int x, int y,int width, int height){
    gl.glViewport(0, 0, width, height);

    gl.glMatrixMode( GL2.GL_PROJECTION );
    gl.glLoadIdentity();
    //gl.glOrtho(-al[0],al[0],-al[0],al[0],0.5, al[0]);
    //glu.gluPerspective(60.0, (float)width/height, 0.5, 100.0);

    float aspect = (float)width / (float)height;
    if( vconf.viewMode == 0 ){
      glu.gluPerspective( rw.vp.fovy, aspect, rw.vp.zNear, rw.vp.zFar );
    }
    else {
      gl.glOrtho( -rw.vp.maxmbs*aspect, rw.vp.maxmbs*aspect,
                  -rw.vp.maxmbs, rw.vp.maxmbs,
                  rw.vp.zNear, rw.vp.zFar );
    }
    gl.glMatrixMode( GL2.GL_MODELVIEW );
    gl.glLoadIdentity();
    glu.gluLookAt( rw.vp.eye[0],    rw.vp.eye[1],    rw.vp.eye[2],
                   rw.vp.center[0], rw.vp.center[1], rw.vp.center[2],
                   rw.vp.up[0],     rw.vp.up[1],     rw.vp.up[2] );

  }
  /* called by OpenGL */
  public void display( GLAutoDrawable drawable ){
    if( (drawable instanceof GLJPanel) &&
        !((GLJPanel) drawable).isOpaque() &&
        ((GLJPanel) drawable).shouldPreserveColorBufferIfTranslucent() ){
      gl.glClear( GL2.GL_DEPTH_BUFFER_BIT );
    } else {
      gl.glClear( GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );
    }

    //bg color
    gl.glClearColor(vconf.bgColor[0],vconf.bgColor[1],
                    vconf.bgColor[2],vconf.bgColor[3]);


    gl.glMatrixMode( GL2.GL_MODELVIEW );
    gl.glLoadIdentity();

    //view point operation starts
    glu.gluLookAt( -rw.vp.eye[0],    -rw.vp.eye[1],    -rw.vp.eye[2],
                   rw.vp.center[0], rw.vp.center[1], rw.vp.center[2],
                   rw.vp.up[0],     rw.vp.up[1],     rw.vp.up[2] );


    gl.glPushMatrix();
    //translate
    gl.glTranslatef( objCenter[0],objCenter[1],objCenter[2] );

    //rotate
    if(rw.isTrackBallMode){
      gl.glMultMatrixf( rw.vp.m, 0 );
    }else{
      //normal
      gl.glRotatef( rotX, 1.0f,  0.0f, 0.0f );
      gl.glRotatef( rotY, 0.0f, -1.0f, 0.0f );
      gl.glRotatef( rotZ, 0.0f,  0.0f, 1.0f );
    }
    //scale
    gl.glScalef( objScale, objScale, objScale );

    gl.glMultMatrixf( mvm, 0 );

    if(isRendering && rw.visibleAtoms && rw.tmpVisibleAtoms)rw.atoms.show();

    if(rw.visibleBox)rw.box.show();
    rw.volume.show();

    gl.glPopMatrix();

  }

  //?
  public void displayChanged( GLAutoDrawable drawable,
                              boolean modeChanged,
                              boolean deviceChanged ){
  }
  //for GLEventListener
  public void dispose(GLAutoDrawable drawable){
  }



}

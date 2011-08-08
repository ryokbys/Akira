package viewer.renderer;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import com.sun.opengl.util.awt.*;

import com.sun.opengl.util.gl2.*;
import javax.media.opengl.awt.*;

import viewer.*;
import data.*;

public class Axis {
  private int width;
  private int height;

  private RenderingWindow rw;
  public Axis(RenderingWindow rw){
    this.rw=rw;

    setHome();

    String fontface = "courier";
    int    fonttype = Font.PLAIN;
    int    fontsize = 10;
    Font   font     = new Font(fontface,fonttype,fontsize);
    txtRenderer = new TextRenderer(font, true, true);
  }
  private TextRenderer txtRenderer;



  private float[] xcolor = { 1.0f, 0.0f, 0.0f, 1.0f };
  private float[] ycolor = { 0.0f, 1.0f, 0.0f, 1.0f };
  private float[] zcolor = { 0.0f, 0.0f, 1.0f, 1.0f };
  private int xaxis_t=Const.DISPLAY_LIST_EMPTY;
  private int yaxis_t=Const.DISPLAY_LIST_EMPTY;
  private int zaxis_t=Const.DISPLAY_LIST_EMPTY;
  public void make(){
    GL2 gl=rw.gl;
    GLUT glut=rw.glut;
    ViewConfig vconf=rw.vconf;

    float len = 1.0f;
    float radius = 0.05f;

    if(zaxis_t!=Const.DISPLAY_LIST_EMPTY)gl.glDeleteLists( zaxis_t, 1);

    zaxis_t = gl.glGenLists(1);
    gl.glNewList( zaxis_t, GL2.GL_COMPILE );
    gl.glPushMatrix();
    glut.glutSolidCylinder( radius, len, 10, 1 );
    gl.glTranslatef( 0.0f, 0.0f, len );
    glut.glutSolidCone( radius*4.0, len, 10, 1 );
    gl.glPopMatrix();
    gl.glEndList();

    if(xaxis_t!=Const.DISPLAY_LIST_EMPTY)gl.glDeleteLists( xaxis_t, 1);

    xaxis_t = gl.glGenLists(1);
    gl.glNewList( xaxis_t, GL2.GL_COMPILE );
    gl.glPushMatrix();
    gl.glRotatef( 90.0f, 0.0f, 1.0f, 0.0f );
    glut.glutSolidCylinder( radius, len, 10, 1 );
    gl.glTranslatef( 0.0f, 0.0f, len );
    glut.glutSolidCone( radius*4.0, len, 10, 1 );
    gl.glPopMatrix();
    gl.glEndList();

    if(yaxis_t!=Const.DISPLAY_LIST_EMPTY)gl.glDeleteLists( yaxis_t, 1);

    yaxis_t = gl.glGenLists(1);
    gl.glNewList( yaxis_t, GL2.GL_COMPILE );
    gl.glPushMatrix();
    gl.glRotatef( -90.0f, 1.0f, 0.0f, 0.0f );
    glut.glutSolidCylinder( radius, len, 10, 1 );
    gl.glTranslatef( 0.0f, 0.0f, len );
    glut.glutSolidCone( radius*4.0, len, 10, 1 );
    gl.glPopMatrix();
    gl.glEndList();
  }

  public void setParentSize( int w, int h ){
    width = w;
    height = h;
  }

  float[] mvm = new float[16];
  private final float[] unitMVM = { 1.0f, 0.0f, 0.0f, 0.0f,
                                    0.0f, 1.0f, 0.0f, 0.0f,
                                    0.0f, 0.0f, 1.0f, 0.0f,
                                    0.0f, 0.0f, 0.0f, 1.0f };
  private final float[] xMVM = { 0.0f, 0.0f, 1.0f, 0.0f,
                                 1.0f, 0.0f, 0.0f, 0.0f,
                                 0.0f, 1.0f, 0.0f, 0.0f,
                                 0.0f, 0.0f, 0.0f, 1.0f };
  private final float[] yMVM = { -1.0f, 0.0f, 0.0f, 0.0f,
                                 0.0f, 0.0f, 1.0f, 0.0f,
                                 0.0f, 1.0f, 0.0f, 0.0f,
                                 0.0f, 0.0f, 0.0f, 1.0f };

  public void setHome(){
    for( int k=0; k<16; k++ )mvm[k] = unitMVM[k];
  }

  public void resetRotation(char ixyz){
    if(ixyz=='x')
      for( int k=0; k<16; k++ ) mvm[k] = xMVM[k];
    else if(ixyz=='y')
      for( int k=0; k<16; k++ ) mvm[k] = yMVM[k];
    else if(ixyz=='z')
      for( int k=0; k<16; k++ ) mvm[k] = unitMVM[k];
  }

  public void setHome( float[] mvm ){
    for( int k=0; k<16; k++ )this.mvm[k] = mvm[k];
  }

  final int windowSize = 120;
  void init(){
    GL2 gl=rw.gl;
    GLU glu=rw.glu;

    gl.glViewport( width-windowSize,
                   height-windowSize,
                   windowSize, windowSize );
    gl.glPushMatrix();
    gl.glMatrixMode( GL2.GL_PROJECTION );
    gl.glPushMatrix();
    gl.glLoadIdentity();
    glu.gluPerspective( 30.0, 1.0, 1.0, 20.0 );
    glu.gluLookAt( 0.0, 0.0, 10.0,
                   0.0, 0.0, 0.0,
                   0.0, 1.0, 0.0 );
    gl.glMatrixMode( GL2.GL_MODELVIEW );
    gl.glLoadIdentity();
  }

  void fin(){
    GL2 gl=rw.gl;
    gl.glMatrixMode( GL2.GL_PROJECTION );
    gl.glPopMatrix();
    gl.glMatrixMode( GL2.GL_MODELVIEW );
    gl.glPopMatrix();
    gl.glViewport( 0, 0, width, height );
  }

  public void show(float[] m){
    init();
    rw.gl.glPushMatrix();
    rw.gl.glMultMatrixf( m, 0 );
    rw.gl.glMultMatrixf( mvm, 0 );
    rw.gl.glGetFloatv( GL2.GL_MODELVIEW_MATRIX, mvm, 0 );
    display();
    rw.gl.glPopMatrix();
    fin();
  }

  public void show(float rotX, float rotY, float rotZ ){
    init();
    rw.gl.glPushMatrix();
    rw.gl.glRotatef( rotX, 1.0f,  0.0f, 0.0f );
    rw.gl.glRotatef( rotY, 0.0f, -1.0f, 0.0f );
    rw.gl.glRotatef( rotZ, 0.0f,  0.0f, 1.0f );
    rw.gl.glMultMatrixf( mvm, 0 );
    rw.gl.glGetFloatv( GL2.GL_MODELVIEW_MATRIX, mvm, 0 );
    this.display();
    rw.gl.glPopMatrix();
    fin();
  }

  void display(){
    GL2 gl=rw.gl;
    GLU glu=rw.glu;
    GLUT glut=rw.glut;
    ViewConfig vconf=rw.vconf;

    gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_SPECULAR, vconf.lightSpc, 0 );
    gl.glMateriali( GL2.GL_FRONT, GL2.GL_SHININESS, vconf.lightShininess );

    gl.glMaterialfv( GL2.GL_FRONT,GL2.GL_AMBIENT_AND_DIFFUSE,xcolor, 0 );
    gl.glCallList( xaxis_t );

    gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE,ycolor, 0 );
    gl.glCallList( yaxis_t );

    gl.glMaterialfv( GL2.GL_FRONT,GL2.GL_AMBIENT_AND_DIFFUSE,zcolor, 0 );
    gl.glCallList( zaxis_t );

    gl.glDisable( GL2.GL_LIGHTING );
    //gl.glColor3f( 0.5f, 0.5f, 0.5f );
    //gl.glColor3f( 0.8f, 0.8f, 0.8f );
    gl.glColor3fv( vconf.txtColor, 0  );
    gl.glRasterPos3f( 2.0f, 0.0f, 0.0f );
    glut.glutBitmapString( GLUT.BITMAP_HELVETICA_18, "x" );
    gl.glRasterPos3f( 0.0f, 2.0f, 0.0f );
    glut.glutBitmapString( GLUT.BITMAP_HELVETICA_18, "y" );
    gl.glRasterPos3f( 0.0f, 0.0f, 2.0f );
    glut.glutBitmapString( GLUT.BITMAP_HELVETICA_18, "z" );

    /*
     * float[] color={1f, 1f, 1f,1.f};
     * renderString("X",2f,0f,0f,color);
     * renderString("Y",0f,2f,0f,color);
     * renderString("Z",0f,0f,2f,color);
     */
    gl.glEnable( GL2.GL_LIGHTING );

  }
  void renderString(String str,float x,float y,float z,float[] color){
    txtRenderer.begin3DRendering();
    txtRenderer.setColor(color[0],color[1],color[2],color[3]);
    txtRenderer.draw3D(str, x,y,z,0.1f);
    txtRenderer.end3DRendering();
  }

}

package viewer.renderer;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

import com.sun.opengl.util.gl2.*;
import javax.media.opengl.awt.*;

import viewer.*;
import data.*;

public class Box implements Renderer{
  RenderingWindow rw;
  public Box(RenderingWindow rw){
    this.rw=rw;
  }

  float[] chgScale( float[][] h, float[] in ){
    float[] out = new float[3];
    for( int k=0; k<3; k++ ){
      out[k] = h[k][0]*in[0] +h[k][1]*in[1] +h[k][2]*in[2];
    }
    return out;
  }

  private int box_t=Const.DISPLAY_LIST_EMPTY;
  public void make(){
    GL2 gl=rw.gl;
    ViewConfig vconf=rw.vconf;
    float[][] size=rw.atoms.h;

    float tp[] = new float[3];
    float[] p;
    if(box_t!=Const.DISPLAY_LIST_EMPTY)gl.glDeleteLists( box_t, 1);

    box_t = gl.glGenLists(1);
    gl.glNewList( box_t, GL2.GL_COMPILE );
    gl.glLineWidth( rw.vconf.boxLineWidth );

    gl.glBegin( GL2.GL_LINE_LOOP );
    tp[0] = 0.f;
    tp[1] = 0.f;
    tp[2] = 0.f;
    p = chgScale( size, tp );
    gl.glVertex3fv( p, 0 );
    tp[0] = 1f;
    tp[1] = 0f;
    tp[2] = 0.f;
    p = chgScale( size, tp );
    gl.glVertex3fv( p, 0 );
    tp[0] = 1.f;
    tp[1] = 1.f;
    tp[2] = 0.f;
    p = chgScale( size, tp );
    gl.glVertex3fv( p, 0 );
    tp[0] = 0.f;
    tp[1] = 1.f;
    tp[2] = 0.f;
    p = chgScale( size, tp );
    gl.glVertex3fv( p, 0 );
    gl.glEnd();

    gl.glBegin( GL2.GL_LINE_LOOP );
    tp[0] = 0.f;
    tp[1] = 0.f;
    tp[2] = 1.f;
    p = chgScale( size, tp );
    gl.glVertex3fv( p, 0 );
    tp[0] = 1.f;
    tp[1] = 0.f;
    tp[2] = 1.f;
    p = chgScale( size, tp );
    gl.glVertex3fv( p, 0 );
    tp[0] = 1.f;
    tp[1] = 1.f;
    tp[2] = 1.f;
    p = chgScale( size, tp );
    gl.glVertex3fv( p, 0 );
    tp[0] = 0.f;
    tp[1] = 1.f;
    tp[2] = 1.f;
    p = chgScale( size, tp );
    gl.glVertex3fv( p, 0 );
    gl.glEnd();

    gl.glBegin( GL2.GL_LINES );
    tp[0] = 0.f;
    tp[1] = 0.f;
    tp[2] = 0.f;
    p = chgScale( size, tp );
    gl.glVertex3fv( p, 0 );
    tp[0] = 0.f;
    tp[1] = 0.f;
    tp[2] = 1.f;
    p = chgScale( size, tp );
    gl.glVertex3fv( p, 0 );
    tp[0] = 1.f;
    tp[1] = 0.f;
    tp[2] = 0.f;
    p = chgScale( size, tp );
    gl.glVertex3fv( p, 0 );
    tp[0] = 1.f;
    tp[1] = 0.f;
    tp[2] = 1.f;
    p = chgScale( size, tp );
    gl.glVertex3fv( p, 0 );
    tp[0] = 1.f;
    tp[1] = 1.f;
    tp[2] = 0.f;
    p = chgScale( size, tp );
    gl.glVertex3fv( p, 0 );
    tp[0] = 1.f;
    tp[1] = 1.f;
    tp[2] = 1.f;
    p = chgScale( size, tp );
    gl.glVertex3fv( p, 0 );
    tp[0] = 0.f;
    tp[1] = 1.f;
    tp[2] = 0.f;
    p = chgScale( size, tp );
    gl.glVertex3fv( p, 0 );
    tp[0] = 0.f;
    tp[1] = 1.f;
    tp[2] = 1.f;
    p = chgScale( size, tp );
    gl.glVertex3fv( p, 0 );
    gl.glEnd();

    gl.glEndList();
  }

  public void show(){
    rw.gl.glDisable( GL2.GL_LIGHTING );
    rw.gl.glColor4fv( rw.vconf.boxColor, 0);
    rw.gl.glCallList( box_t );
    rw.gl.glEnable( GL2.GL_LIGHTING );
  }


}

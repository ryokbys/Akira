package viewer.renderer;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

import com.sun.opengl.util.gl2.*;
import javax.media.opengl.awt.*;

import viewer.*;
import data.*;

public class BondRenderer implements Renderer{
  
  // Bond-type constants
  public static final byte BOND_TYPE_LINE     = 0;
  public static final byte BOND_TYPE_CYLINDER = 1;

  private RenderingWindow rw;
  private Atoms atoms;

  public ColorTable ctable;

  private int bonds_t=Const.DISPLAY_LIST_EMPTY;

  public float maxBondLength= 0.f;

  // Constructor
  public BondRenderer( RenderingWindow rw ){
    this.rw=rw;
    this.atoms= rw.atoms;
    ctable=new ColorTable(rw);
  }

  public void clearList(){
    if(bonds_t!=Const.DISPLAY_LIST_EMPTY)
      rw.gl.glDeleteLists( bonds_t, 1 );
  }

  public void make(){
    GL2 gl=rw.gl;
    if(bonds_t!=Const.DISPLAY_LIST_EMPTY)
      gl.glDeleteLists( bonds_t, 1 );

    bonds_t = gl.glGenLists(1);
    gl.glNewList( bonds_t, GL2.GL_COMPILE );

    //for length color
    switch(rw.renderingBondColorType){
    case 1:
      ctable.setRange(rw.vconf.bondLengthRange);
      break;
    case 2:
      ctable.setRange(rw.vconf.bondCNRange);
      break;
    }

    float[] color=new float[4];
    switch(rw.renderingBondType){
    case BOND_TYPE_LINE: // line
      gl.glDisable( GL2.GL_LIGHTING );
      gl.glLineWidth(rw.vconf.bondRadius);
      for(int i=0; i<atoms.getNumAtoms(); i++ ){
        Atom ai= atoms.getAtom(i);
        if( rw.atmRndr.vtag[i] < 0 ) continue;
        for(int j=0; j<ai.getNumBonds(); j++ ){
          Bond b= ai.getBond(j);
        
          gl.glPushMatrix();
          gl.glTranslatef( ai.pos[0],ai.pos[1],ai.pos[2]);
          gl.glRotatef( b.phi,   0.0f, 0.0f, 1.0f );
          gl.glRotatef( b.theta, 0.0f, 1.0f, 0.0f );

          gl.glBegin(GL2.GL_LINES);

          if( rw.renderingBondColorType==0 ){
            color= rw.atmRndr.getAtomColor(i);
          }else if( rw.renderingBondColorType==1 ){
            color= ctable.getColor( b.length );
          //}else if( rw.renderingBondColorType==2 ){
          // coordination number dependent coloring
          //  if( CN.containsKey(b.i) )
          //    color=ctable.getColor((float)CN.get(b.i));
          //  else
          //    color=ctable.getColor(0.f);
          }

          gl.glColor3fv(color, 0 );
          gl.glVertex3f(0.f, 0.f, 0.f);
          gl.glVertex3f(0.f, 0.f, b.length*0.5f);

          gl.glEnd();
          gl.glPopMatrix();
        }
      }
      gl.glEnable( GL2.GL_LIGHTING );
      break;

    case BOND_TYPE_CYLINDER:// solid
      gl.glMateriali( GL2.GL_FRONT, GL2.GL_SHININESS, rw.vconf.atomShineness);
      gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_SPECULAR, rw.vconf.atomSpecular, 0 );
      gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_EMISSION, rw.vconf.atomEmmission, 0 );
      gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, rw.vconf.atomAmb, 0 );

      for(int i=0; i<atoms.getNumAtoms(); i++ ){
        Atom ai= atoms.getAtom(i);
        //if( ai.tag < 0 ) continue;
        if( rw.atmRndr.vtag[i] < 0 ) continue;
        for( int j=0; j<ai.getNumBonds(); j++ ){
          Bond b= ai.getBond(j);

          gl.glPushMatrix();
          gl.glTranslatef( ai.pos[0],ai.pos[1],ai.pos[2]);
          gl.glRotatef( b.phi,   0.0f, 0.0f, 1.0f );
          gl.glRotatef( b.theta, 0.0f, 1.0f, 0.0f );

          if(rw.renderingBondColorType==0){
            color=rw.atmRndr.getAtomColor(i);
          }else if(rw.renderingBondColorType==1){
            color=ctable.getColor(b.length);
          //}else if(rw.renderingBondColorType==2){
          //  if(CN.containsKey(b.i))
          //    color=ctable.getColor((float)CN.get(b.i));
          //  else
          //    color=ctable.getColor(0.f);
          }
          gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, color,0);
          rw.glut.glutSolidCylinder( rw.vconf.bondRadius, b.length*0.5f, rw.vconf.bondSlice, 1);
          
          gl.glPopMatrix();
        }
      }
      break;
    }
    gl.glEndList();
  }

  public void show(){
    rw.gl.glCallList( bonds_t );
  }

  private float[] mulH( float[][] h,float[] in ){
    float[] out = new float[3];
    for(int k=0; k<3; k++)
      out[k] =h[k][0]*in[0] +h[k][1]*in[1] +h[k][2]*in[2];
    return out;
  }

}

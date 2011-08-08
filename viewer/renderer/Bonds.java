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

public class Bonds extends data.Bonds implements Renderer{

  private RenderingWindow rw;
  public Bonds(RenderingWindow rw){
    this.rw=rw;
    ctable=new ColorTable(rw);
  }
  public ColorTable ctable;

  private int bonds_t=Const.DISPLAY_LIST_EMPTY;
  public void clearList(){
    if(bonds_t!=Const.DISPLAY_LIST_EMPTY)rw.gl.glDeleteLists( bonds_t, 1 );
  }
  public void make(){
    GL2 gl=rw.gl;
    if(bonds_t!=Const.DISPLAY_LIST_EMPTY)gl.glDeleteLists( bonds_t, 1 );

    bonds_t = gl.glGenLists(1);
    gl.glNewList( bonds_t, GL2.GL_COMPILE );

    //for langth color
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
    case 0: // line
      gl.glDisable( GL2.GL_LIGHTING );
      gl.glLineWidth(rw.vconf.bondRadius);
      for(int i=0; i<getN(); i++ ){
        Bond b=get(i);
        if(rw.atoms.vtag[b.i]<0)continue;
        if(rw.atoms.vtag[b.j]<0)continue;
        gl.glPushMatrix();
        gl.glTranslatef( b.origin[0],b.origin[1],b.origin[2]);
        gl.glRotatef( b.phi,   0.0f, 0.0f, 1.0f );
        gl.glRotatef( b.theta, 0.0f, 1.0f, 0.0f );

        gl.glBegin(GL2.GL_LINES);
        //i
        if(rw.renderingBondColorType==0){
          color=rw.atoms.getAtomColor(b.i);
        }else if(rw.renderingBondColorType==1){
          color=ctable.getColor(b.length);
        }else if(rw.renderingBondColorType==2){
          if(CN.containsKey(b.i))
            color=ctable.getColor((float)CN.get(b.i));
          else
            color=ctable.getColor(0.f);
        }

        gl.glColor3fv(color, 0 );
        gl.glVertex3f(0.f, 0.f, 0.f);
        gl.glVertex3f(0.f, 0.f, b.length*0.5f);

        //j
        if(rw.renderingBondColorType==0){
          color=rw.atoms.getAtomColor(b.j);
        }else if(rw.renderingBondColorType==1){
          color=ctable.getColor(b.length);
        }else if(rw.renderingBondColorType==2){
          if(CN.containsKey(b.j))
            color=ctable.getColor((float)CN.get(b.j));
          else
            color=ctable.getColor(0.f);
        }
        gl.glColor3fv(color, 0 );
        gl.glVertex3f(0.f, 0.f, b.length*0.5f);
        gl.glVertex3f(0.f, 0.f, b.length);
        gl.glEnd();

        gl.glPopMatrix();
      }
      gl.glEnable( GL2.GL_LIGHTING );
      break;

    case 1:// solid
      gl.glMateriali( GL2.GL_FRONT, GL2.GL_SHININESS, rw.vconf.atomShineness);
      gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_SPECULAR, rw.vconf.atomSpecular, 0 );
      gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_EMISSION, rw.vconf.atomEmmission, 0 );
      gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, rw.vconf.atomAmb, 0 );

      for(int i=0; i<getN(); i++ ){
        Bond b=get(i);
        if(rw.atoms.vtag[b.i]<0)continue;
        if(rw.atoms.vtag[b.j]<0)continue;
        gl.glPushMatrix();
        gl.glTranslatef( b.origin[0],b.origin[1],b.origin[2]);
        gl.glRotatef( b.phi,   0.0f, 0.0f, 1.0f );
        gl.glRotatef( b.theta, 0.0f, 1.0f, 0.0f );
        //i
        if(rw.renderingBondColorType==0){
          color=rw.atoms.getAtomColor(b.i);
        }else if(rw.renderingBondColorType==1){
          color=ctable.getColor(b.length);
        }else if(rw.renderingBondColorType==2){
          if(CN.containsKey(b.i))
            color=ctable.getColor((float)CN.get(b.i));
          else
            color=ctable.getColor(0.f);
        }
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, color,0);
        rw.glut.glutSolidCylinder( rw.vconf.bondRadius, b.length*0.5f, rw.vconf.bondSlice, 1);

        //j
        gl.glTranslatef(0.f, 0.f, b.length*0.5f);
        if(rw.renderingBondColorType==0){
          color=rw.atoms.getAtomColor(b.j);
        }else if(rw.renderingBondColorType==1){
          color=ctable.getColor(b.length);
        }else if(rw.renderingBondColorType==2){
          if(CN.containsKey(b.j))
            color=ctable.getColor((float)CN.get(b.j));
          else
            color=ctable.getColor(0.f);
        }
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, color,0);
        rw.glut.glutSolidCylinder( rw.vconf.bondRadius, b.length*0.5f, rw.vconf.bondSlice, 1);
        gl.glPopMatrix();
      }
      break;
    }
    gl.glEndList();
  }

  public void show(){
    rw.gl.glCallList( bonds_t );
  }


}

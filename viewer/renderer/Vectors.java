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

import data.*;
import tools.*;
import viewer.*;
import viewer.renderer.*;

public class Vectors implements Renderer{

  private final float H_PI = 180.0f/(float)Math.PI;
  public float lengthMax=0.f;

  private RenderingWindow rw;
  public Vectors(RenderingWindow rw){
    this.rw=rw;
    ctable=new ColorTable(rw);

  }
  public ColorTable ctable;



  private float[] mulH( float[][] h,float[] in ){
    float[] out = new float[3];
    for(int k=0; k<3; k++)
      out[k] = (h[k][0]*in[0] +h[k][1]*in[1] +h[k][2]*in[2]);
    return out;
  }


  public void show(){
    rw.gl.glCallList( vec_t );
  }
  private int vec_t=Const.DISPLAY_LIST_EMPTY;
  public void make(){

    GL2 gl=rw.gl;
    ViewConfig vconf=rw.vconf;

    int ix=vconf.vectorX;
    int iy=vconf.vectorY;
    int iz=vconf.vectorZ;

    float coneRadius=vconf.vecConeRadius;
    float coneHeight=vconf.vecConeHeight;
    float lengthRatio=vconf.vecLengthRatio;
    float cylinderRadius=vconf.vecCylinderRadius;
    int slice=vconf.vecCylinderSlice;

    float[] color=new float[4];


    lengthMax=0.f;
    for(int i=0; i<rw.atoms.n; i++ ){
      float tmp=
        rw.atoms.data[i][ix]*rw.atoms.data[i][ix]+
        rw.atoms.data[i][iy]*rw.atoms.data[i][iy]+
        rw.atoms.data[i][iz]*rw.atoms.data[i][iz];
      if(tmp>lengthMax)lengthMax=tmp;
    }



    if(vec_t!=Const.DISPLAY_LIST_EMPTY)gl.glDeleteLists( vec_t, 1 );
    vec_t = gl.glGenLists(1);
    gl.glNewList( vec_t, GL2.GL_COMPILE );


    //range
    ctable.setRange(vconf.vecLengthRange);


    switch(rw.renderingVectorType){
    case 0:
      //line
      gl.glDisable( GL2.GL_LIGHTING );
      gl.glLineWidth(2.f);
      for(int i=0; i<rw.atoms.n; i++ ){
        if(rw.atoms.vtag[i]<0)continue;

        gl.glPushMatrix();
        gl.glTranslatef( rw.atoms.r[i][0],rw.atoms.r[i][1],rw.atoms.r[i][2] );


        float[] t = Coordinate.xyz2rtp( rw.atoms.data[i][ix],
                                        rw.atoms.data[i][iy],
                                        rw.atoms.data[i][iz]);

        gl.glRotatef( t[1], 0.0f, 0.0f, 1.0f );//phi
        gl.glRotatef( t[2], 0.0f, 1.0f, 0.0f );//theta

        gl.glBegin(GL2.GL_LINES);

        if(rw.renderingVectorColorType==0){
          color=rw.atoms.getAtomColor(i);
        }else if(rw.renderingVectorColorType==1){
          color=ctable.getColor(t[0]);
        }
        gl.glColor3fv(color, 0 );

        gl.glVertex3f(0.f, 0.f, 0.f);
        gl.glVertex3f(0.f, 0.f, t[0]*lengthRatio);
        gl.glEnd();

        gl.glPopMatrix();
      }
      gl.glEnable( GL2.GL_LIGHTING );
      break;
    case 1:
      //real vector
      gl.glMateriali( GL2.GL_FRONT, GL2.GL_SHININESS, vconf.atomShineness);
      gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_SPECULAR, vconf.atomSpecular, 0 );
      gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_EMISSION, vconf.atomEmmission, 0 );
      gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, vconf.atomAmb, 0 );


      for(int i=0; i<rw.atoms.n; i++ ){
        if(rw.atoms.vtag[i]<0)continue;
        gl.glPushMatrix();
        gl.glTranslatef( rw.atoms.r[i][0],rw.atoms.r[i][1],rw.atoms.r[i][2] );

        float[] t = Coordinate.xyz2rtp( rw.atoms.data[i][ix],
                                        rw.atoms.data[i][iy],
                                        rw.atoms.data[i][iz]);

        if(rw.renderingVectorColorType==0){
          color=rw.atoms.getAtomColor(i);
        }else if(rw.renderingVectorColorType==1){
          color=ctable.getColor(t[0]);
        }
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, color, 0 );

        gl.glRotatef( H_PI*t[1], 0.0f, 0.0f, 1.0f );//theta
        gl.glRotatef( H_PI*t[2], 0.0f, 1.0f, 0.0f );//phi
        rw.glut.glutSolidCylinder( cylinderRadius, t[0]*lengthRatio, slice, slice);
        gl.glTranslatef( 0.0f, 0.0f, t[0]*lengthRatio );
        rw.glut.glutSolidCone( coneRadius, coneHeight, slice, slice);
        gl.glPopMatrix();
      }
      break;
    default:
      break;
    }
    gl.glEndList();
  }

}

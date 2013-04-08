package viewer.renderer;

import java.io.*;
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

import tools.*;
import data.*;
import viewer.renderer.*;
import viewer.*;

public class AtomLabel {

  private RenderingWindow rw;

  public AtomLabel(RenderingWindow rw){
    this.rw=rw;
  }

  private int label_t=Const.DISPLAY_LIST_EMPTY;
  public void show(){
      //delete and make
    if(label_t!=Const.DISPLAY_LIST_EMPTY)rw.gl.glDeleteLists( label_t, 1);
    makeLabel( rw.gl,rw.glu,rw.glut,rw.atoms,rw.vconf, rw.vp.mvm );
    //show
    rw.gl.glDisable( GL2.GL_LIGHTING );
    rw.gl.glColor4fv( rw.vconf.txtColor, 0 );
    rw.gl.glCallList( label_t );
    rw.gl.glEnable( GL2.GL_LIGHTING );
  }


  private void makeLabel( GL2 gl, GLU glu, GLUT glut,
                          Atoms atoms, ViewConfig vconf,
                          float[] mvm ){
    HashMap<Integer,Integer> tagCount = new HashMap<Integer,Integer>();
    double[][] mat  = new double[4][4];
    double[][] mati = new double[4][4];
    for( int i=0; i<4; i++ ){
      for( int j=0; j<4; j++ ) mat[i][j] = (double)mvm[j*4+i];
    }

    Matrix.inv( mat, mati );
    float sr = getScale( mvm );

    float[] rv = new float[4];
    float[] rr = new float[4];

    label_t = gl.glGenLists(1);
    gl.glNewList( label_t, GL2.GL_COMPILE );
    int natm= atoms.getNumAtoms();
    int[] vtag= rw.atmRndr.vtag;
    for( int i=0; i<natm; i++ ){
      if( vtag[i]<0 )continue;
      Atom ai= atoms.getAtom(i);
      int itag  = ai.tag-1;
      float[] pos= ai.pos;
      if( itag >= 0 ){
        float r = (float)vconf.tagRadius[itag];
        rr[2] = r*sr;
        for( int k=0; k<4; k++ ){
          rv[k] = 0.0f;
          for( int j=0; j<4; j++ ) rv[k] += mati[k][j]*rr[j];
        }
        gl.glRasterPos3f( pos[0] + rv[0],
                          pos[1] + rv[1],
                          pos[2] + rv[2] );
        //switch string
        String str;
        switch( rw.atomLabelType ){
        case 0:
          //id
          str = String.format( "%d", i+1 );
          break;
        case 1:
          // species
          str = String.format( "%d", itag+1 );
          break;
        case 2:
          // show name
          str=vconf.tagName[itag];
          break;
        case 3:
          // show name+id
          str = vconf.tagName[itag]+String.format( "%d", i+1 );
          break;
        case 4:
          //tagname+counter
          if(tagCount.containsKey(itag)){
            int inc=tagCount.get(itag);
            inc++;
            tagCount.put(itag,inc);
          }else{
            int inc=1;
            tagCount.put(itag,inc);
          }

          // show species+count
          str = vconf.tagName[itag]+String.format( "%d", tagCount.get(itag));
          break;
        default:
          str="unknown type";
          break;
        }
        glut.glutBitmapString( GLUT.BITMAP_HELVETICA_18, str );
      }
    }
    gl.glEndList();
  }

  private float getScale( float[] mvm ){
    float[] sv = new float[3];;
    for( int i=0; i<3; i++ ){
      sv[i] = 0.0f;
      for( int j=0; j<3; j++ ){
        sv[i] += mvm[j*4+i];
      }
    }
    float s = (float)Math.sqrt( sv[0]*sv[0] + sv[1]*sv[1] + sv[2]*sv[2] );
    return s;
  }


}

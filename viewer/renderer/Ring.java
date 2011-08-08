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


import com.sun.opengl.util.texture.*;
import java.io.*;
import java.util.*;

import viewer.*;
import data.*;

public class Ring{


  private RenderingWindow rw;
  public Ring(RenderingWindow rw){
    this.rw=rw;
  }

  private int list_t=Const.DISPLAY_LIST_EMPTY;
  public void make(ArrayList<String> rings, GL2 gl){
    float[] color = { 1.0f, 0.0f, 0.0f ,0.9f};
    float linewidth=2.f;
    viewer.renderer.Atoms atoms=rw.atoms;


    if(list_t!=Const.DISPLAY_LIST_EMPTY)gl.glDeleteLists( list_t, 1 );
    list_t = gl.glGenLists(1);
    gl.glNewList( list_t, GL2.GL_COMPILE );


    gl.glDisable( GL2.GL_LIGHTING );
    gl.glLineWidth(linewidth);


    for(int i=0;i<rings.size();i++){
      String[] path=rings.get(i).split("[:-]");

      gl.glBegin(GL2.GL_LINES);
      int length=Integer.valueOf(path[0]).intValue();

      color[0]=0.f;
      color[1]=0.f;
      color[2]=0.f;
      color[3]=1.f;
      color[length%3]=length/(float)rw.vconf.ringRangeMax;

      gl.glColor3fv(color, 0 );
      for(int j=1;j<path.length-1;j++){
        int id;
        id = Integer.valueOf(path[j]).intValue();
        gl.glVertex3f(atoms.r[id][0],atoms.r[id][1],atoms.r[id][2]);
        id = Integer.valueOf(path[j+1]).intValue();
        gl.glVertex3f(atoms.r[id][0],atoms.r[id][1],atoms.r[id][2]);
      }
      gl.glEnd();
    }
    gl.glEnable( GL2.GL_LIGHTING );
    gl.glEndList();

  }
  public void show(){
    rw.gl.glCallList( list_t );
  }

}

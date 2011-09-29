package viewer.renderer;

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


import com.jogamp.opengl.util.texture.*;
import java.io.*;
import java.util.*;

import viewer.*;

public class RotationCenter{

  public void setVisible(boolean t){
    visible = t;
  }

  boolean visible;
  private float[] color = { 0.0f, 0.0f, 1.0f ,0.4f};
  private float size=1.f;

  private int rotCenter_t;
  private RenderingWindow rw;

  public RotationCenter(RenderingWindow rw){
    this.rw=rw;
    visible = false;
    for(int i=0; i<3; i++)
      for(int j=0; j<3; j++)
        if(size< rw.atoms.hMax[i][j])size= rw.atoms.hMax[i][j];
    make(rw.gl,rw.glut);
  }
  private void make(GL2 gl,GLUT glut){
    rotCenter_t = gl.glGenLists(1);
    gl.glNewList( rotCenter_t, GL2.GL_COMPILE );
    /*
     * // texture test starts
     * File filename=new File("img/tex.png");
     * try {
     *   Texture texture = TextureIO.newTexture(filename, true);
     *   texture.enable();
     *   texture.bind();
     * } catch (IOException ex){
     *   ex.printStackTrace();
     * }
     *
     * gl.glDisable(GL2.GL_DEPTH_TEST);
     * gl.glEnable(GL2.GL_TEXTURE_2D);
     * gl.glEnable(GL2.GL_TEXTURE_GEN_S);
     * gl.glEnable(GL2.GL_TEXTURE_GEN_T);
     * gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE,
     *              GL2.GL_SPHERE_MAP);
     * gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE,
     *              GL2.GL_SPHERE_MAP);
     *
     * glut.glutSolidSphere(size/2, 10,10);
     * gl.glEnable(GL2.GL_DEPTH_TEST);
     * gl.glDisable(GL2.GL_TEXTURE_2D);
     * gl.glDisable(GL2.GL_TEXTURE_GEN_S);
     * gl.glDisable(GL2.GL_TEXTURE_GEN_T);
     * // texture test ends
     */


    //rotation center
    //gl.glColor4fv(color,0);
    gl.glMaterialfv( GL2.GL_FRONT_AND_BACK,GL2.GL_AMBIENT_AND_DIFFUSE,color, 0 );
    glut.glutSolidSphere( size/4.f, 30, 30 );
    //glut.glutWireSphere( size/4.f, 10, 10 );
    //glut.glutSolidCube( size/4.f );

    gl.glEndList();

  }



  public void show(){
    if( visible )rw.gl.glCallList(rotCenter_t);
  }


}

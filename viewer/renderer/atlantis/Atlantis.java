package viewer.renderer.atlantis;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;

import com.jogamp.opengl.util.gl2.*;
import javax.media.opengl.awt.*;

import viewer.renderer.atlantis.*;

/**
 * test of whale, shark, dolphin
 */

public class Atlantis{
  GL2 gl;
  GLU glu;
  GLUT glut;
  float[][] h=new float[3][3];
  public Atlantis(GL2 gl, GLU glu, GLUT glut,float[][] h){
    this.gl = gl;
    this.glu = glu;
    this.glut = glut;
    this.h=h;

    dolphin=new Dolphin(gl,glu,glut);
    shark=new Shark(gl,glu,glut);
    whale=new Whale(gl,glu,glut);
  }


  Dolphin dolphin;
  Shark shark;
  Whale whale;

  public void show(){
    gl.glPushMatrix();
    dolphin.show();
    gl.glTranslatef( h[0][0],0.f,0.f);
    shark.show();
    gl.glTranslatef( 0.f,h[1][1],0.f);
    whale.show();
    gl.glPopMatrix();
  }

}

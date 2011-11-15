package viewer.renderer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

import com.sun.opengl.util.gl2.*;
import javax.media.opengl.awt.*;

import viewer.*;

public class Light {

  public Light(){
  }

  public void set(GL2 gl, GLU glu, GLUT glut,ViewConfig vconf){
    gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_POSITION, vconf.lightPos, 0 );
    gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_DIFFUSE,  vconf.lightDif, 0 );
    gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_AMBIENT,  vconf.lightAmb, 0 );
    gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_SPECULAR, vconf.lightSpc, 0 );
    gl.glEnable( GL2.GL_LIGHTING );
    gl.glEnable( GL2.GL_LIGHT0 );
  }

}

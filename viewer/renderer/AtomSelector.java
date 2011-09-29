package viewer.renderer;

import java.io.*;
import java.nio.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;

import com.jogamp.opengl.util.gl2.*;
import javax.media.opengl.awt.*;

import viewer.*;
import data.*;

public class AtomSelector{
  Viewpoint vp;

  static final int bufferSize = 100000;
  ByteBuffer byteBuffer;
  IntBuffer selectBuf;
  viewer.renderer.Atoms atoms;

  public AtomSelector(){
    //init
    //byteBuffer = ByteBuffer.allocateDirect( BufferUtil.SIZEOF_INT * bufferSize);
    byteBuffer = ByteBuffer.allocateDirect( 4 * bufferSize);
    //byte order depends on OS
    byteBuffer.order( ByteOrder.nativeOrder() );
    //selectBuffer
    selectBuf = byteBuffer.asIntBuffer();
  }



  int[] viewport = new int[4];
  int id;
  float z1, z2;
  public int getID(GL2 gl,GLU glu, GLUT glut,
                   viewer.renderer.Atoms atoms,Viewpoint vp,
                   int prevMouseX, int prevMouseY ){
    //picked ID
    id = -1;

    //pass selectBuf to GL
    gl.glSelectBuffer( selectBuf.capacity(), selectBuf );

    //draw objects at GL_SELECT mode
    gl.glRenderMode( GL2.GL_SELECT );
    gl.glMatrixMode( GL2.GL_PROJECTION );
    gl.glPushMatrix();
    gl.glLoadIdentity();
    gl.glGetIntegerv( GL2.GL_VIEWPORT, viewport, 0 );
    glu.gluPickMatrix( (double)prevMouseX,
                       (double)(viewport[3]-prevMouseY),
                       0.5, 0.5, viewport, 0 );
    float aspect = (float)viewport[2]/(float)viewport[3];
    vp.port( aspect );
    gl.glMatrixMode( GL2.GL_MODELVIEW );
    gl.glPushMatrix();

    //make invisible atoms for picking
    atoms.makePickingAtoms();

    gl.glPopMatrix();
    gl.glMatrixMode( GL2.GL_PROJECTION );
    gl.glPopMatrix();
    gl.glMatrixMode( GL2.GL_MODELVIEW );

    //# of hits
    int hits = gl.glRenderMode( GL2.GL_RENDER );
    int offset = 0;
    int names;
    float pz = 1.0e32f;

    for( int i=0; i<hits; i++ ){
      names = selectBuf.get( offset );
      offset++;
      //front
      z1 = (float)selectBuf.get(offset)/0x7fffffff; // 2147483647
      offset++;
      //back
      z2 = (float)selectBuf.get(offset)/0x7fffffff;
      offset++;
      if( z1 < pz ){
        id = selectBuf.get( offset );
        pz = z1;
      }
      //skip
      for( int j=0; j<names; j++ )  offset++;
    }
    return id;
  }


  public int getID(){
    return id;
  }
  public float getZ1(){
    return z1;
  }
  public float getZ2(){
    return z2;
  }
}

package viewer.renderer;

import viewer.RenderingWindow;
import viewer.ViewConfig;
import java.io.*;
import java.util.*;
import java.nio.FloatBuffer;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import javax.media.opengl.awt.*;
import com.sun.opengl.util.*;
import com.sun.opengl.util.gl2.*;
import com.sun.opengl.util.awt.*;
import com.sun.opengl.util.glsl.*;

public class Toon {

  GLAutoDrawable drawable;
  AWTGLAutoDrawable awtDrawable;
  GL2 gl;
  GLU glu;
  GLUT glut;
  GL2ES2 glsl;
  ShaderState st;
  ShaderProgram sp;

  // In order to differentiate perspective and orthogonal views
  ViewConfig vconf;
  Viewpoint vp;

  // Constructor
  public Toon(ViewConfig vc, Viewpoint vp){
    vconf= vc;
    this.vp= vp;
  }

  public void setGL( GLAutoDrawable drawable,
                     GL2 gl, GLU glu, GLUT glut ) {
    this.drawable = drawable;
    awtDrawable = (AWTGLAutoDrawable)drawable;
    this.gl = gl;
    this.glu = glu;
    this.glut = glut;
    glsl = drawable.getGL().getGL2ES2();
  }

  ClassLoader cl = this.getClass().getClassLoader();
  String[][] readShaderCode( String filename ) {
    ArrayList<String> src = new ArrayList<String>();
    try {
      InputStream is = cl.getResourceAsStream( filename );
      InputStreamReader isr = new InputStreamReader( is );
      BufferedReader br = new BufferedReader( isr );
      String line;
      while ( (line=br.readLine()) != null ) {
        int cn = line.indexOf("//");
        if ( cn > -1 ) {
          line = line.substring( 0, cn );
        }
        if ( (line.trim()).length() > 0 ) {
          src.add( line.trim() );
        }
      }
      br.close();
      isr.close();
      is.close();
    }
    catch ( IOException e ) {
    }
    String[][] sc = new String[1][src.size()];
    for ( int i=0; i<src.size(); i++ ) {
      sc[0][i] = src.get(i);
      //System.out.println( sc[0][i] );
    }
    return sc;
  }

  String vpfile = "viewer/renderer/src/toon_rk.vp";
  String fpfile = "viewer/renderer/src/toon_rk.fp";

  public void init() {
    ShaderCode rsVp = new ShaderCode( GL2ES2.GL_VERTEX_SHADER,
                                      1,
                                      readShaderCode(vpfile) );
    ShaderCode rsFp= new ShaderCode( GL2ES2.GL_FRAGMENT_SHADER,
                                     1,
                                     readShaderCode(fpfile) );
    sp = new ShaderProgram();
    sp.add( rsVp );
    sp.add( rsFp );
    if( !sp.link( glsl, System.err ) ) {
      throw new GLException( "Couldn't link program: " + sp );
    }
    st = new ShaderState();
    st.attachShaderProgram( glsl, sp );
  }

  public void set() {
    st.glUseProgram( glsl, true );
    st.glUniform( glsl, new GLUniformData("mode",vconf.viewMode) );
    if( vconf.viewMode!=0 ){ // Orthogonal
      FloatBuffer fbeye = FloatBuffer.wrap( vp.eye );
      st.glUniform( glsl, new GLUniformData("eye",3,fbeye) );
    }
  }
  public void unset() {
    st.glUseProgram( glsl, false );
  }

}

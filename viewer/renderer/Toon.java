package viewer.renderer;

import viewer.RenderingWindow;
import viewer.ViewConfig;
import java.io.*;
import java.util.*;
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
  GL2ES2 glslo;
  ShaderState st;
  ShaderProgram sp,spo;

  // In order to differentiate perspective and orthogonal views
  ViewConfig vconf;

  // Constructor
  public Toon(ViewConfig vc){
    vconf= vc;
  }

  public void setGL( GLAutoDrawable drawable,
                     GL2 gl, GLU glu, GLUT glut ) {
    this.drawable = drawable;
    awtDrawable = (AWTGLAutoDrawable)drawable;
    this.gl = gl;
    this.glu = glu;
    this.glut = glut;
    glsl = drawable.getGL().getGL2ES2();
    glslo = drawable.getGL().getGL2ES2();
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
  String fpofile= "viewer/renderer/src/toon_orth_rk.fp";

  public void init() {
    ShaderCode rsVp = new ShaderCode( GL2ES2.GL_VERTEX_SHADER,
                                      1,
                                      readShaderCode(vpfile) );
    ShaderCode rsFp= new ShaderCode( GL2ES2.GL_FRAGMENT_SHADER,
                                     1,
                                     readShaderCode(fpfile) );
    ShaderCode rsFpo = new ShaderCode( GL2ES2.GL_FRAGMENT_SHADER,
                                       1,
                                       readShaderCode(fpofile) );
    sp = new ShaderProgram();
    sp.add( rsVp );
    sp.add( rsFp );
    spo = new ShaderProgram();
    spo.add( rsVp );
    spo.add( rsFpo );
    if( !sp.link( glsl, System.err ) ) {
      throw new GLException( "Couldn't link program: " + sp );
    }
    if( !spo.link( glslo, System.err ) ){
      throw new GLException( "Couldn't link program: " + spo );
    }
    st = new ShaderState();
    if( vconf.viewMode==0 ){ // Perspective
      st.attachShaderProgram( glsl, sp );
    }else{ // Orthogonal
      st.attachShaderProgram( glslo, spo );
    }
  }

  public void changeShaderProgram(){
    if( vconf.viewMode==0 ){ // Perspective
      st.attachShaderProgram( glsl, sp );
    }else{ // Orthogonal
      st.attachShaderProgram( glslo, spo );
    }
  }

  public void set() {
    if( vconf.viewMode==0 ){ // Perspective
      st.glUseProgram( glsl, true );
    }else{ // Orthogonal
      st.glUseProgram( glslo, true );
    }
  }
  public void unset() {
    if( vconf.viewMode==0 ){ // Perspective
      st.glUseProgram( glsl, false );
    }else{ // Orthogonal
      st.glUseProgram( glslo, false );
    }
  }

}

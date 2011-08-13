package viewer.renderer;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import com.sun.opengl.util.awt.*;

import com.sun.opengl.util.gl2.*;
import javax.media.opengl.awt.*;

import viewer.*;
import viewer.renderer.*;

public class Annotation{

  int width;
  int height;

  RenderingWindow rw;
  TextRenderer txtRenderer;
  public Annotation(RenderingWindow rw){
    this.rw=rw;
    txtRenderer = new TextRenderer(rw.vconf.annotationFont, true, true);
  }

  public void updateFont(){
    txtRenderer = null;
    txtRenderer = new TextRenderer(rw.vconf.annotationFont, true, true);
  }

  public void show(){
    ViewConfig vconf=rw.vconf;

    if(rw.visibleTime)
      showTime(rw.gl,rw.glu,rw.glut,rw.vconf,
               rw.atoms.startTime, rw.atoms.timeInterval, rw.atoms.nframe);

    if(rw.visibleLegend && rw.renderingAtomDataIndex>0){
      if(vconf.isVisibleAtomColorTable)
        showDataLegend(rw.gl,rw.glu,rw.glut,rw.vconf,
                       rw.vconf.ticsType,
                       rw.atoms.ctable,
                       vconf.atomColorTablePos,
                       vconf.dataLegend[rw.renderingAtomDataIndex-1],
                       vconf.dataFormat[rw.renderingAtomDataIndex-1]
                       );
    }
    if(vconf.isVisibleBondColorTable)
      showDataLegend(rw.gl,rw.glu,rw.glut,rw.vconf,
                     rw.vconf.ticsType,
                     rw.bonds.ctable,
                     vconf.bondColorTablePos,
                     vconf.bondLegend,
                     vconf.bondColorTableFormat
                     );

    if(vconf.isVisibleVecColorTable)
      showDataLegend(rw.gl,rw.glu,rw.glut,rw.vconf,
                     rw.vconf.ticsType,
                     rw.vec.ctable,
                     vconf.vecColorTablePos,
                     vconf.vecLegend,
                     vconf.vecColorTableFormat
                     );

    if(vconf.isVisibleVolColorTable)
      showDataLegend(rw.gl,rw.glu,rw.glut,rw.vconf,
                     rw.vconf.ticsType,
                     rw.volume.ctable,
                     vconf.volColorTablePos,
                     vconf.volLegend,
                     vconf.volColorTableFormat
                     );




  }

  void showTime(GL2 gl, GLU glu, GLUT glut,
                ViewConfig vconf,
                float startTime, float timeInterval, int nframe){
    gl.glDisable( GL2.GL_LIGHTING );
    gl.glDisable( GL2.GL_DEPTH_TEST );

    gl.glPushMatrix();
    gl.glMatrixMode( GL2.GL_PROJECTION );
    gl.glPushMatrix();

    gl.glLoadIdentity();
    glu.gluOrtho2D( 0, width, 0, height );
    gl.glMatrixMode( GL2.GL_MODELVIEW );
    gl.glLoadIdentity();
    gl.glColor3fv( vconf.txtColor, 0  );
    float t = startTime + timeInterval*nframe;

    String display_time=getFormat(vconf.timePrintFormat,t);

    //render
    renderString(display_time,vconf.txtPos[0], height-vconf.txtPos[1],0.f,1.f,vconf.txtColor);

    gl.glMatrixMode( GL2.GL_PROJECTION );
    gl.glPopMatrix();
    gl.glMatrixMode( GL2.GL_MODELVIEW );
    gl.glPopMatrix();

    gl.glEnable( GL2.GL_DEPTH_TEST );
    gl.glEnable( GL2.GL_LIGHTING );
  }

  public void setParentSize( int w, int h ){
    width = w;
    height = h;
  }

  public void showDataLegend(GL2 gl, GLU glu, GLUT glut,ViewConfig vconf,
                             int ticsType,
                             ColorTable ctable,
                             float[] pos,
                             String legend,
                             String format){

    int nquads = 255;
    float nquads_i = 1.0f/255;

    float xstart=pos[0]*rw.vp.height;
    float xend=xstart+pos[2];

    float ystart=pos[1]*rw.vp.width;
    float yend=ystart+pos[3];

    float[] range=ctable.range;
    float dr = range[1]-range[0];

    float x,y,yinc;
    String str;
    int num;
    float yy;
    boolean title;

    float legendScale=1.f;

    title = true;

    gl.glDisable( GL2.GL_LIGHTING );
    gl.glDisable( GL2.GL_DEPTH_TEST );

    gl.glViewport( 0, 0, rw.vp.width,rw.vp.height);
    gl.glPushMatrix();
    gl.glMatrixMode( GL2.GL_PROJECTION );
    gl.glPushMatrix();
    gl.glLoadIdentity();
    glu.gluOrtho2D( 0, rw.vp.width, 0, rw.vp.height );
    gl.glMatrixMode( GL2.GL_MODELVIEW );
    gl.glLoadIdentity();


    int isVLong,isHLong;
    if(vconf.isTicsHLong){
      isVLong=0;
      isHLong=1;
    }else{
      isVLong=1;
      isHLong=0;
    }

    //body
    y = ystart;
    yinc = (yend-y)*nquads_i;
    gl.glBegin( GL2.GL_QUAD_STRIP );
    for( int i=0; i<=nquads; i++ ){
      x = range[0] + i*dr*nquads_i;
      gl.glColor3fv( ctable.getColor(x) , 0 );
      gl.glVertex2f( isVLong*xstart+isHLong*y,
                     isHLong*xstart+isVLong*y);
      gl.glVertex2f( isVLong*xend+isHLong*y,
                     isHLong*xend+isVLong*y);
      y += yinc;
    }
    gl.glEnd();


    float dx=vconf.ctNumPos[0];
    float dy=vconf.ctNumPos[1];
    switch ( ticsType ){
    case 0://3 text
      //3 line
      gl.glBegin( GL2.GL_LINES );
      gl.glLineWidth(1.5f);
      gl.glColor3fv( vconf.bgColor, 0 );
      y = ystart;
      gl.glVertex2f( isVLong*xstart+isHLong*y,
                     isHLong*xstart+isVLong*y);
      gl.glVertex2f( isVLong*xend+isHLong*y,
                     isHLong*xend+isVLong*y);
      y = ystart + Math.abs(range[0])/dr*(yend-ystart);
      gl.glVertex2f( isVLong*xstart+isHLong*y,
                     isHLong*xstart+isVLong*y);
      gl.glVertex2f( isVLong*xend+isHLong*y,
                     isHLong*xend+isVLong*y);
      y = yend;
      gl.glVertex2f( isVLong*xstart+isHLong*y,
                     isHLong*xstart+isVLong*y);
      gl.glVertex2f( isVLong*xend+isHLong*y,
                     isHLong*xend+isVLong*y);
      gl.glEnd();

      //3 number
      gl.glColor3fv( vconf.txtColor, 0 );
      y = ystart;
      str = getFormat(format, range[0]*vconf.dataFactor );

      //if(Math.abs(range[0])>1e-4)
      renderString(str,
                   isVLong*(xend+dx)+isHLong*(y+dy),
                   isHLong*(xend+dx)+isVLong*(y+dy),
                   0.f,legendScale,vconf.txtColor);

      y = ystart + Math.abs( range[0])/dr*(yend-ystart);
      str = getFormat(format,range[1]/2.f*vconf.dataFactor);

      renderString(str,
                   isVLong*(xend+dx)+isHLong*(y+dy),
                   isHLong*(xend+dx)+isVLong*(y+dy),
                   0.f,legendScale,vconf.txtColor);

      y = yend;
      str = getFormat(format, range[1]*vconf.dataFactor );
      //if(Math.abs(range[1])>1e-4)
      renderString(str,
                   isVLong*(xend+dx)+isHLong*(y+dy),
                   isHLong*(xend+dx)+isVLong*(y+dy),
                   0.f,legendScale,vconf.txtColor);
      break;

    case 1://2 text
      //3 line
      gl.glBegin( GL2.GL_LINES );
      gl.glLineWidth(1.5f);
      gl.glColor3fv( vconf.bgColor, 0 );
      y = ystart;
      gl.glVertex2f( isVLong*xstart+isHLong*y,
                     isHLong*xstart+isVLong*y);
      gl.glVertex2f( isVLong*xend+isHLong*y,
                     isHLong*xend+isVLong*y);

      y = yend;
      gl.glVertex2f( isVLong*xstart+isHLong*y,
                     isHLong*xstart+isVLong*y);
      gl.glVertex2f( isVLong*xend+isHLong*y,
                     isHLong*xend+isVLong*y);
      gl.glEnd();

      //2 number
      gl.glColor3fv( vconf.txtColor, 0 );
      y = ystart;
      str = getFormat(format, range[0]*vconf.dataFactor );

      //if(Math.abs(range[0])>1e-4)
      renderString(str,
                   isVLong*(xend+dx)+isHLong*(y+dy),
                   isHLong*(xend+dx)+isVLong*(y+dy),
                   0.f,legendScale,vconf.txtColor);


      y = yend;
      str = getFormat(format, range[1]*vconf.dataFactor );
      //if(Math.abs(range[1])>1e-4)
      renderString(str,
                   isVLong*(xend+dx)+isHLong*(y+dy),
                   isHLong*(xend+dx)+isVLong*(y+dy),
                   0.f,legendScale,vconf.txtColor);
      break;

    case 2://1 text
      //3 line
      gl.glBegin( GL2.GL_LINES );
      gl.glLineWidth(1.5f);
      gl.glColor3fv( vconf.bgColor, 0 );
      y = ystart;
      gl.glVertex2f( isVLong*xstart+isHLong*y,
                     isHLong*xstart+isVLong*y);
      gl.glVertex2f( isVLong*xend+isHLong*y,
                     isHLong*xend+isVLong*y);
      y = ystart + Math.abs(range[0])/dr*(yend-ystart);
      gl.glVertex2f( isVLong*xstart+isHLong*y,
                     isHLong*xstart+isVLong*y);
      gl.glVertex2f( isVLong*xend+isHLong*y,
                     isHLong*xend+isVLong*y);
      y = yend;
      gl.glVertex2f( isVLong*xstart+isHLong*y,
                     isHLong*xstart+isVLong*y);
      gl.glVertex2f( isVLong*xend+isHLong*y,
                     isHLong*xend+isVLong*y);
      gl.glEnd();

      //1text
      y = ystart + Math.abs(range[0])/dr*(yend-ystart);
      str = getFormat(format,range[1]/2.f*vconf.dataFactor);
      renderString(str,
                   isVLong*(xend+dx)+isHLong*(y+dy),
                   isHLong*(xend+dx)+isVLong*(y+dy),
                   0.f,legendScale,vconf.txtColor);
      break;

    case 3://6text
      //6text
      yinc = (yend-ystart)/6.0f;
      y = ystart;
      for( int i=0; i<=6; i++ ){
        x = range[0] + i*dr/6.0f;
        str = getFormat(format, x*vconf.dataFactor );
        renderString(str,
                     isVLong*(xend+dx)+isHLong*(y+dy),
                     isHLong*(xend+dx)+isVLong*(y+dy),
                     0.f,legendScale,vconf.txtColor);
        y += yinc;
      }

      //6 line
      yinc = (yend-ystart)/6.0f;
      gl.glBegin( GL2.GL_LINES );
      gl.glLineWidth(1.5f);
      y = ystart;
      gl.glColor3fv( vconf.bgColor, 0 );
      for( int i=0; i<=6; i++ ){
        gl.glVertex2f( isVLong*xstart+isHLong*y,
                       isHLong*xstart+isVLong*y);
        gl.glVertex2f( isVLong*xend+isHLong*y,
                       isHLong*xend+isVLong*y);
        y += yinc;
      }
      gl.glEnd();

      break;
    default:
      title = false;
      System.out.println("no legend");
      break;
    }

    if( title == true){
      float tdx=vconf.ctTitlePos[0];
      float tdy=vconf.ctTitlePos[1];

      renderString(legend,
                   isVLong*(xstart+tdx)+isHLong*(yend+tdy),
                   isHLong*(xstart+tdx)+isVLong*(yend+tdy),
                   0.f,legendScale,vconf.txtColor);

    }


    gl.glMatrixMode( GL2.GL_PROJECTION );
    gl.glPopMatrix();
    gl.glMatrixMode( GL2.GL_MODELVIEW );
    gl.glPopMatrix();

    gl.glEnable( GL2.GL_DEPTH_TEST );
    gl.glEnable( GL2.GL_LIGHTING );

    gl.glViewport( 0, 0, width, height );

  }

  void renderString(String str,int x,int y,float[] color){
    txtRenderer.beginRendering(width,height);
    txtRenderer.setColor(color[0],color[1],color[2],color[3]);
    txtRenderer.draw(str, x,height-y-(int)txtRenderer.getBounds(str).getHeight());
    txtRenderer.endRendering();
  }
  void renderString(String str,float x,float y,float z,float scale,float[] color){
    txtRenderer.begin3DRendering();
    txtRenderer.setColor(color[0],color[1],color[2],color[3]);
    txtRenderer.draw3D(str, x,y,z,scale);
    txtRenderer.end3DRendering();
  }

  /*
   * void renderBitmapString( int height, String str ){
   *   if( height >= 240 ){
   *     glut.glutBitmapString( GLUT.BITMAP_HELVETICA_18, str );
   *   }
   *   else {
   *     glut.glutBitmapString( GLUT.BITMAP_HELVETICA_12, str );
   *   }
   * }
   */



  private String getFormat(String format, float val){
    String str;
    Pattern pattern = Pattern.compile("%(.*)d");
    Matcher matcher = pattern.matcher(format);
    if(matcher.find()){
      str = String.format( format, (int)val );
    }else{
      str = String.format( format, val );
    }

    return str;
  }
}

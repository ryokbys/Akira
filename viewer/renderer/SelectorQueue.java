package viewer.renderer;
import java.util.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

import com.sun.opengl.util.gl2.*;
import javax.media.opengl.awt.*;

import viewer.*;
import data.*;

public class SelectorQueue{
  public static final int EMPTY=-91872;

  public int[] id=new int[4];
  public float[][] pos=new float[4][3];

  public boolean isBandClose=false;

  public ArrayList<Integer> winPos=new ArrayList<Integer>();
  public ArrayList<Float> cutN=new ArrayList<Float>();
  public ArrayList<Float> cutP=new ArrayList<Float>();
  public ArrayList<Float> cutRN=new ArrayList<Float>();
  public ArrayList<Float> cutRP=new ArrayList<Float>();

  public void addWinPos(int x, int y){
    winPos.add(x);
    winPos.add(y);
  }
  public void clearWinPos(){
    winPos.clear();
    cutN.clear();
    cutP.clear();
    cutRN.clear();
    cutRP.clear();
    isBandClose=false;
  }
  public int[] rect1=new int[2];
  public int[] rect2=new int[2];
  public void setRectPos(int x1, int y1, int x2, int y2){
    rect1[0]=x1;
    rect1[1]=y1;
    rect2[0]=x2;
    rect2[1]=y2;
  }
  public void drawRect(int w, int h){
    rw.gl.glDisable( GL2.GL_LIGHTING );
    rw.gl.glDisable( GL2.GL_DEPTH_TEST );

    rw.gl.glPushMatrix();
    rw.gl.glMatrixMode( GL2.GL_PROJECTION );

    rw.gl.glPushMatrix();
    rw.gl.glLoadIdentity();
    rw.glu.gluOrtho2D( 0, w, 0, h );
    rw.gl.glMatrixMode( GL2.GL_MODELVIEW );
    rw.gl.glLoadIdentity();

    rw.gl.glColor3f(1.f, 1.f, 0.f);
    rw.gl.glPointSize(3);
    rw.gl.glBegin(GL2.GL_POINTS);
    rw.gl.glVertex2i(rect1[0],h-rect1[1]);
    rw.gl.glVertex2i(rect2[0],h-rect2[1]);
    rw.gl.glEnd();

    //draw rectangle
    rw.gl.glColor3f(1.f, 0.f, 1.f);
    rw.gl.glLineWidth(2.f);
    rw.gl.glBegin(GL2.GL_LINE_LOOP);

    rw.gl.glVertex2i(rect1[0],h-rect1[1]);
    rw.gl.glVertex2i(rect2[0],h-rect1[1]);
    rw.gl.glVertex2i(rect2[0],h-rect2[1]);
    rw.gl.glVertex2i(rect1[0],h-rect2[1]);

    rw.gl.glEnd();

    rw.gl.glMatrixMode( GL2.GL_PROJECTION );
    rw.gl.glPopMatrix();
    rw.gl.glMatrixMode( GL2.GL_MODELVIEW );
    rw.gl.glPopMatrix();

    rw.gl.glEnable( GL2.GL_DEPTH_TEST );
    rw.gl.glEnable( GL2.GL_LIGHTING );
  }
  public void drawRubberBand(int w, int h){
    int n=winPos.size()/2;
    if(n==0)return;

    rw.gl.glDisable( GL2.GL_LIGHTING );
    rw.gl.glDisable( GL2.GL_DEPTH_TEST );

    rw.gl.glPushMatrix();
    rw.gl.glMatrixMode( GL2.GL_PROJECTION );

    rw.gl.glPushMatrix();
    rw.gl.glLoadIdentity();
    rw.glu.gluOrtho2D( 0, w, 0, h );
    rw.gl.glMatrixMode( GL2.GL_MODELVIEW );
    rw.gl.glLoadIdentity();

    int is=0;
    int ie=winPos.size()/2-1;
    int dx=winPos.get(2*is)-winPos.get(2*ie);
    int dy=winPos.get(2*is+1)-winPos.get(2*ie+1)-1;

    int hitArea=15;
    //start point
    rw.gl.glColor3f(1.f, 1.f, 0.f);
    rw.gl.glPointSize((float)hitArea);
    rw.gl.glBegin(GL2.GL_POINTS);
    rw.gl.glVertex2i(winPos.get(2*is),h-winPos.get(2*is+1));
    rw.gl.glEnd();

    if(n>2 &&Math.abs(dx)<=hitArea && Math.abs(dy)<=hitArea){
      isBandClose=true;

      //closed curve
      rw.gl.glColor3f(1.f, 0.f, 1.f);
      rw.gl.glLineWidth(2.f);
      rw.gl.glBegin(GL2.GL_LINE_LOOP);
      for(int i=0;i<winPos.size()/2;i++){
        rw.gl.glVertex2i(winPos.get(2*i),h-winPos.get(2*i+1));
      }
      rw.gl.glEnd();


    }else{
      //open curve
      rw.gl.glColor3f(1.f, 0.f, 0.f);
      rw.gl.glLineWidth(2.f);
      rw.gl.glBegin(GL2.GL_LINE_STRIP);
      for(int i=0;i<winPos.size()/2;i++){
        rw.gl.glVertex2i(winPos.get(2*i),h-winPos.get(2*i+1));
      }
      rw.gl.glEnd();
    }


    rw.gl.glMatrixMode( GL2.GL_PROJECTION );
    rw.gl.glPopMatrix();
    rw.gl.glMatrixMode( GL2.GL_MODELVIEW );
    rw.gl.glPopMatrix();

    rw.gl.glEnable( GL2.GL_DEPTH_TEST );
    rw.gl.glEnable( GL2.GL_LIGHTING );

  }


  public void clearQueue(){
    //init
    for(int i=0;i<4;i++){
      id[i]=EMPTY;
      for(int j=0;j<3;j++)pos[i][j]=0.f;
    }
  }


  RenderingWindow rw;
  public SelectorQueue(RenderingWindow rw){
    this.rw=rw;
    clearQueue();
  }

  private int line_t=Const.DISPLAY_LIST_EMPTY;
  private float[] color={0.5f, 1.f, 0.f, 1.f};
  public void make(){
    GL2 gl=rw.gl;
    ViewConfig vconf=rw.vconf;

    if(line_t!=Const.DISPLAY_LIST_EMPTY)gl.glDeleteLists( line_t, 1);

    line_t = gl.glGenLists(1);
    gl.glNewList( line_t, GL2.GL_COMPILE );

    gl.glLineWidth(3);
    gl.glMaterialfv( GL2.GL_FRONT_AND_BACK,GL2.GL_AMBIENT_AND_DIFFUSE,color, 0 );


    if(vconf.isSelectionLength){
      if(id[0]!=EMPTY && id[1]!=EMPTY){
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3f(pos[0][0],pos[0][1],pos[0][2]);
        gl.glVertex3f(pos[1][0],pos[1][1],pos[1][2]);
        gl.glEnd();
      }
    }
    if(vconf.isSelectionAngle && isFull3()){
      gl.glBegin(GL2.GL_LINES);
      gl.glVertex3f(pos[0][0],pos[0][1],pos[0][2]);
      gl.glVertex3f(pos[1][0],pos[1][1],pos[1][2]);
      gl.glEnd();
      gl.glBegin(GL2.GL_LINES);
      gl.glVertex3f(pos[1][0],pos[1][1],pos[1][2]);
      gl.glVertex3f(pos[2][0],pos[2][1],pos[2][2]);
      gl.glEnd();
    }
    if(vconf.isSelectionTorsion && isFull4()){
      gl.glBegin(GL2.GL_LINES);
      gl.glVertex3f(pos[0][0],pos[0][1],pos[0][2]);
      gl.glVertex3f(pos[1][0],pos[1][1],pos[1][2]);
      gl.glEnd();
      gl.glBegin(GL2.GL_LINES);
      gl.glVertex3f(pos[1][0],pos[1][1],pos[1][2]);
      gl.glVertex3f(pos[2][0],pos[2][1],pos[2][2]);
      gl.glEnd();
      gl.glBegin(GL2.GL_LINES);
      gl.glVertex3f(pos[2][0],pos[2][1],pos[2][2]);
      gl.glVertex3f(pos[3][0],pos[3][1],pos[3][2]);
      gl.glEnd();
    }
    gl.glEndList();
  }

  public void show(){
    rw.gl.glCallList( line_t );
  }

  public void add(int i, float[] p){
    for(int j=3;j>0;j--){
      pos[j]=pos[j-1];
      id[j]=id[j-1];
    }
    pos[0]=p;
    id[0]=i+1;
  }

  public boolean isFull3(){
    if(id[0]!=EMPTY && id[1]!=EMPTY && id[2]!=EMPTY)return true;
    else return false;
  }
  boolean isFull4(){
    if(id[0]!=EMPTY && id[1]!=EMPTY && id[2]!=EMPTY && id[3]!=EMPTY)return true;
    else return false;
  }

  public String showLength(){
    String str;
    if(id[0]!=EMPTY && id[1]!=EMPTY){
      float dx= pos[0][0]-pos[1][0];
      float dy= pos[0][1]-pos[1][1];
      float dz= pos[0][2]-pos[1][2];
      float r=(float)Math.sqrt(dx*dx+dy*dy+dz*dz);
      str=String.format("Length \n%d-%d = %f (%f, %f,%f)",id[1],id[0],r,dx,dy,dz);
    }else{
      str="";
    }
    return str;
  }
  public String showAngle(){
    float[] r1=new float[3];
    float[] r2=new float[3];
    double rr1,rr2,cos,angl;

    if(!isFull3())return "";

    String str="Angle [deg]";
    //0-1-2
    for(int j=0;j<3;j++){
      r1[j]=pos[0][j]-pos[1][j];
      r2[j]=pos[2][j]-pos[1][j];
    }
    rr1=Math.sqrt(r1[0]*r1[0]+r1[1]*r1[1]+r1[2]*r1[2]);
    rr2=Math.sqrt(r2[0]*r2[0]+r2[1]*r2[1]+r2[2]*r2[2]);
    cos=(r1[0]*r2[0]+r1[1]*r2[1]+r1[2]*r2[2])/(rr1*rr2);
    angl=Math.acos(cos)*180.0/Math.PI;
    str+=String.format("\n%d-%d-%d = %f",id[2],id[1],id[0],angl);

    return str;
  }
  public String showTorsion(){
    float[] r1=new float[3];
    float[] r2=new float[3];

    float[] n1=new float[3];
    float[] n2=new float[3];
    float rr1i,rr2i;

    if(!isFull4())return "";

    String str="Torsion Angle [deg]";
    //0-1-2
    for(int j=0;j<3;j++){
      r1[j]=pos[0][j]-pos[1][j];
      r2[j]=pos[2][j]-pos[1][j];
    }

    rr1i=1.f/(float)Math.sqrt(r1[0]*r1[0]+r1[1]*r1[1]+r1[2]*r1[2]);
    rr2i=1.f/(float)Math.sqrt(r2[0]*r2[0]+r2[1]*r2[1]+r2[2]*r2[2]);
    //normal vector1
    n1[0]=(r1[1]*r2[2]-r1[2]*r2[1])*rr1i*rr2i;
    n1[1]=(r1[2]*r2[0]-r1[0]*r2[1])*rr1i*rr2i;
    n1[2]=(r1[0]*r2[1]-r1[1]*r2[0])*rr1i*rr2i;

    //1-2-3
    for(int j=0;j<3;j++){
      r1[j]=pos[3][j]-pos[2][j];
      r2[j]=pos[1][j]-pos[2][j];
    }
    rr1i=1.f/(float)Math.sqrt(r1[0]*r1[0]+r1[1]*r1[1]+r1[2]*r1[2]);
    rr2i=1.f/(float)Math.sqrt(r2[0]*r2[0]+r2[1]*r2[1]+r2[2]*r2[2]);
    //normal vector2
    n2[0]=(r1[1]*r2[2]-r1[2]*r2[1])*rr1i*rr2i;
    n2[1]=(r1[2]*r2[0]-r1[0]*r2[1])*rr1i*rr2i;
    n2[2]=(r1[0]*r2[1]-r1[1]*r2[0])*rr1i*rr2i;

    float nn1=(float)Math.sqrt(n1[0]*n1[0]+n1[1]*n1[1]+n1[2]*n1[2]);
    float nn2=(float)Math.sqrt(n2[0]*n2[0]+n2[1]*n2[1]+n2[2]*n2[2]);

    float cos=(n1[0]*n2[0]+n1[1]*n2[1]+n1[2]*n2[2])/(nn1*nn2);
    float tor=(float)Math.acos(cos)*180.0f/(float)Math.PI;
    str+=String.format("\n%d-%d-%d-%d = %f",id[3],id[2],id[1],id[0],tor);

    return str;

  }



  public float[] getPlaneVariable(){
    float[] v1=new float[3];
    float[] v2=new float[3];
    //set pos[2] origin
    for(int i=0;i<3;i++){
      v1[i]=pos[0][i]-pos[2][i];
      v2[i]=pos[1][i]-pos[2][i];
    }
    //normal vector from three points
    float[] nv=new float[3];
    nv[0]=v1[1]*v2[2]-v1[2]*v2[1];
    nv[1]=v1[2]*v2[0]-v1[0]*v2[1];
    nv[2]=v1[0]*v2[1]-v1[1]*v2[0];

    float v=(float)Math.sqrt(nv[0]*nv[0]+nv[1]*nv[1]+nv[2]*nv[2]);
    //float v=1.f;
    //buffer
    float[] buf=new float[6];
    buf[0]=nv[0]/v;
    buf[1]=nv[1]/v;
    buf[2]=nv[2]/v;
    buf[3]=pos[2][0];
    buf[4]=pos[2][1];
    buf[5]=pos[2][2];
    return buf;
  }

}

package viewer.renderer.atlantis;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;

import com.jogamp.opengl.util.gl2.*;
import javax.media.opengl.awt.*;

/**
 * for special vectors
 */

public class Shark{
  GL2 gl;
  GLU glu;
  GLUT glut;
  public Shark(GL2 gl, GLU glu, GLUT glut){
    this.gl = gl;
    this.glu = glu;
    this.glut = glut;
    make();
  }


  int list_t;
  public void show(){
    float[] color={0.f, 1.f, 0.f, 1.f};
    gl.glColor4fv(color,0);
    gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,
                    color, 0 );
    gl.glCallList( list_t );
  }
  public void showNoColor(){
    gl.glCallList( list_t );
  }

  void make(){
    list_t = gl.glGenLists(1);
    gl.glNewList( list_t, GL2.GL_COMPILE );
    DrawShark();
    gl.glEndList();
  }
  void DrawShark(){
    float[] mat=new float[16];
    int n;

    gl.glPushMatrix();

    float scale=0.0005f;
    gl.glScalef(scale,scale,scale);

    gl.glRotated(180.0, 0.0, 1.0, 0.0);
    gl.glTranslated(0.0, 0.0, -6000.0);
    gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, mat,0);

    n = 0;
    if (mat[2] >= 0.0)n += 1;
    if (mat[2+4] >= 0.0) n += 2;
    if (mat[2+8] >= 0.0) n += 4;

    gl.glScaled(2.0, 1.0, 1.0);

    gl.glEnable(GL2.GL_CULL_FACE);
    switch (n) {
    case 0:
      Fish_1();
      break;
    case 1:
      Fish_2();
      break;
    case 2:
      Fish_3();
      break;
    case 3:
      Fish_4();
      break;
    case 4:
      Fish_5();
      break;
    case 5:
      Fish_6();
      break;
    case 6:
      Fish_7();
      break;
    case 7:
      Fish_8();
      break;
    }
    gl.glDisable(GL2.GL_CULL_FACE);

    gl.glPopMatrix();
  }



  void
    Fish001()
  {
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N005,0);
    gl.glVertex3fv(P005,0);
    gl.glNormal3fv(N059,0);
    gl.glVertex3fv(P059,0);
    gl.glNormal3fv(N060,0);
    gl.glVertex3fv(P060,0);
    gl.glNormal3fv(N006,0);
    gl.glVertex3fv(P006,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N015,0);
    gl.glVertex3fv(P015,0);
    gl.glNormal3fv(N005,0);
    gl.glVertex3fv(P005,0);
    gl.glNormal3fv(N006,0);
    gl.glVertex3fv(P006,0);
    gl.glNormal3fv(N016,0);
    gl.glVertex3fv(P016,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N006,0);
    gl.glVertex3fv(P006,0);
    gl.glNormal3fv(N060,0);
    gl.glVertex3fv(P060,0);
    gl.glNormal3fv(N008,0);
    gl.glVertex3fv(P008,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N016,0);
    gl.glVertex3fv(P016,0);
    gl.glNormal3fv(N006,0);
    gl.glVertex3fv(P006,0);
    gl.glNormal3fv(N008,0);
    gl.glVertex3fv(P008,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N016,0);
    gl.glVertex3fv(P016,0);
    gl.glNormal3fv(N008,0);
    gl.glVertex3fv(P008,0);
    gl.glNormal3fv(N017,0);
    gl.glVertex3fv(P017,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N017,0);
    gl.glVertex3fv(P017,0);
    gl.glNormal3fv(N008,0);
    gl.glVertex3fv(P008,0);
    gl.glNormal3fv(N018,0);
    gl.glVertex3fv(P018,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N008,0);
    gl.glVertex3fv(P008,0);
    gl.glNormal3fv(N009,0);
    gl.glVertex3fv(P009,0);
    gl.glNormal3fv(N018,0);
    gl.glVertex3fv(P018,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N008,0);
    gl.glVertex3fv(P008,0);
    gl.glNormal3fv(N060,0);
    gl.glVertex3fv(P060,0);
    gl.glNormal3fv(N009,0);
    gl.glVertex3fv(P009,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N007,0);
    gl.glVertex3fv(P007,0);
    gl.glNormal3fv(N010,0);
    gl.glVertex3fv(P010,0);
    gl.glNormal3fv(N009,0);
    gl.glVertex3fv(P009,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N009,0);
    gl.glVertex3fv(P009,0);
    gl.glNormal3fv(N019,0);
    gl.glVertex3fv(P019,0);
    gl.glNormal3fv(N018,0);
    gl.glVertex3fv(P018,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N009,0);
    gl.glVertex3fv(P009,0);
    gl.glNormal3fv(N010,0);
    gl.glVertex3fv(P010,0);
    gl.glNormal3fv(N019,0);
    gl.glVertex3fv(P019,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N010,0);
    gl.glVertex3fv(P010,0);
    gl.glNormal3fv(N020,0);
    gl.glVertex3fv(P020,0);
    gl.glNormal3fv(N019,0);
    gl.glVertex3fv(P019,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N010,0);
    gl.glVertex3fv(P010,0);
    gl.glNormal3fv(N011,0);
    gl.glVertex3fv(P011,0);
    gl.glNormal3fv(N021,0);
    gl.glVertex3fv(P021,0);
    gl.glNormal3fv(N020,0);
    gl.glVertex3fv(P020,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N004,0);
    gl.glVertex3fv(P004,0);
    gl.glNormal3fv(N011,0);
    gl.glVertex3fv(P011,0);
    gl.glNormal3fv(N010,0);
    gl.glVertex3fv(P010,0);
    gl.glNormal3fv(N007,0);
    gl.glVertex3fv(P007,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N004,0);
    gl.glVertex3fv(P004,0);
    gl.glNormal3fv(N012,0);
    gl.glVertex3fv(P012,0);
    gl.glNormal3fv(N011,0);
    gl.glVertex3fv(P011,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N012,0);
    gl.glVertex3fv(P012,0);
    gl.glNormal3fv(N022,0);
    gl.glVertex3fv(P022,0);
    gl.glNormal3fv(N011,0);
    gl.glVertex3fv(P011,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N011,0);
    gl.glVertex3fv(P011,0);
    gl.glNormal3fv(N022,0);
    gl.glVertex3fv(P022,0);
    gl.glNormal3fv(N021,0);
    gl.glVertex3fv(P021,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N059,0);
    gl.glVertex3fv(P059,0);
    gl.glNormal3fv(N005,0);
    gl.glVertex3fv(P005,0);
    gl.glNormal3fv(N015,0);
    gl.glVertex3fv(P015,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N015,0);
    gl.glVertex3fv(P015,0);
    gl.glNormal3fv(N014,0);
    gl.glVertex3fv(P014,0);
    gl.glNormal3fv(N003,0);
    gl.glVertex3fv(P003,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N015,0);
    gl.glVertex3fv(P015,0);
    gl.glNormal3fv(N003,0);
    gl.glVertex3fv(P003,0);
    gl.glNormal3fv(N059,0);
    gl.glVertex3fv(P059,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N014,0);
    gl.glVertex3fv(P014,0);
    gl.glNormal3fv(N013,0);
    gl.glVertex3fv(P013,0);
    gl.glNormal3fv(N003,0);
    gl.glVertex3fv(P003,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N003,0);
    gl.glVertex3fv(P003,0);
    gl.glNormal3fv(N012,0);
    gl.glVertex3fv(P012,0);
    gl.glNormal3fv(N059,0);
    gl.glVertex3fv(P059,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N013,0);
    gl.glVertex3fv(P013,0);
    gl.glNormal3fv(N012,0);
    gl.glVertex3fv(P012,0);
    gl.glNormal3fv(N003,0);
    gl.glVertex3fv(P003,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N013,0);
    gl.glVertex3fv(P013,0);
    gl.glNormal3fv(N022,0);
    gl.glVertex3fv(P022,0);
    gl.glNormal3fv(N012,0);
    gl.glVertex3fv(P012,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glVertex3fv(P071,0);
    gl.glVertex3fv(P072,0);
    gl.glVertex3fv(P073,0);
    gl.glVertex3fv(P074,0);
    gl.glVertex3fv(P075,0);
    gl.glVertex3fv(P076,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glVertex3fv(P077,0);
    gl.glVertex3fv(P078,0);
    gl.glVertex3fv(P079,0);
    gl.glVertex3fv(P080,0);
    gl.glVertex3fv(P081,0);
    gl.glVertex3fv(P082,0);
    gl.glEnd();
  }

  void
    Fish002()
  {
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N013,0);
    gl.glVertex3fv(P013,0);
    gl.glNormal3fv(N014,0);
    gl.glVertex3fv(P014,0);
    gl.glNormal3fv(N024,0);
    gl.glVertex3fv(P024,0);
    gl.glNormal3fv(N023,0);
    gl.glVertex3fv(P023,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N014,0);
    gl.glVertex3fv(P014,0);
    gl.glNormal3fv(N015,0);
    gl.glVertex3fv(P015,0);
    gl.glNormal3fv(N025,0);
    gl.glVertex3fv(P025,0);
    gl.glNormal3fv(N024,0);
    gl.glVertex3fv(P024,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N016,0);
    gl.glVertex3fv(P016,0);
    gl.glNormal3fv(N017,0);
    gl.glVertex3fv(P017,0);
    gl.glNormal3fv(N027,0);
    gl.glVertex3fv(P027,0);
    gl.glNormal3fv(N026,0);
    gl.glVertex3fv(P026,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N017,0);
    gl.glVertex3fv(P017,0);
    gl.glNormal3fv(N018,0);
    gl.glVertex3fv(P018,0);
    gl.glNormal3fv(N028,0);
    gl.glVertex3fv(P028,0);
    gl.glNormal3fv(N027,0);
    gl.glVertex3fv(P027,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N020,0);
    gl.glVertex3fv(P020,0);
    gl.glNormal3fv(N021,0);
    gl.glVertex3fv(P021,0);
    gl.glNormal3fv(N031,0);
    gl.glVertex3fv(P031,0);
    gl.glNormal3fv(N030,0);
    gl.glVertex3fv(P030,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N013,0);
    gl.glVertex3fv(P013,0);
    gl.glNormal3fv(N023,0);
    gl.glVertex3fv(P023,0);
    gl.glNormal3fv(N022,0);
    gl.glVertex3fv(P022,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N022,0);
    gl.glVertex3fv(P022,0);
    gl.glNormal3fv(N023,0);
    gl.glVertex3fv(P023,0);
    gl.glNormal3fv(N032,0);
    gl.glVertex3fv(P032,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N022,0);
    gl.glVertex3fv(P022,0);
    gl.glNormal3fv(N032,0);
    gl.glVertex3fv(P032,0);
    gl.glNormal3fv(N031,0);
    gl.glVertex3fv(P031,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N022,0);
    gl.glVertex3fv(P022,0);
    gl.glNormal3fv(N031,0);
    gl.glVertex3fv(P031,0);
    gl.glNormal3fv(N021,0);
    gl.glVertex3fv(P021,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N018,0);
    gl.glVertex3fv(P018,0);
    gl.glNormal3fv(N019,0);
    gl.glVertex3fv(P019,0);
    gl.glNormal3fv(N029,0);
    gl.glVertex3fv(P029,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N018,0);
    gl.glVertex3fv(P018,0);
    gl.glNormal3fv(N029,0);
    gl.glVertex3fv(P029,0);
    gl.glNormal3fv(N028,0);
    gl.glVertex3fv(P028,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N019,0);
    gl.glVertex3fv(P019,0);
    gl.glNormal3fv(N020,0);
    gl.glVertex3fv(P020,0);
    gl.glNormal3fv(N030,0);
    gl.glVertex3fv(P030,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N019,0);
    gl.glVertex3fv(P019,0);
    gl.glNormal3fv(N030,0);
    gl.glVertex3fv(P030,0);
    gl.glNormal3fv(N029,0);
    gl.glVertex3fv(P029,0);
    gl.glEnd();
  }

  void
    Fish003()
  {
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N032,0);
    gl.glVertex3fv(P032,0);
    gl.glNormal3fv(N023,0);
    gl.glVertex3fv(P023,0);
    gl.glNormal3fv(N033,0);
    gl.glVertex3fv(P033,0);
    gl.glNormal3fv(N042,0);
    gl.glVertex3fv(P042,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N031,0);
    gl.glVertex3fv(P031,0);
    gl.glNormal3fv(N032,0);
    gl.glVertex3fv(P032,0);
    gl.glNormal3fv(N042,0);
    gl.glVertex3fv(P042,0);
    gl.glNormal3fv(N041,0);
    gl.glVertex3fv(P041,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N023,0);
    gl.glVertex3fv(P023,0);
    gl.glNormal3fv(N024,0);
    gl.glVertex3fv(P024,0);
    gl.glNormal3fv(N034,0);
    gl.glVertex3fv(P034,0);
    gl.glNormal3fv(N033,0);
    gl.glVertex3fv(P033,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N024,0);
    gl.glVertex3fv(P024,0);
    gl.glNormal3fv(N025,0);
    gl.glVertex3fv(P025,0);
    gl.glNormal3fv(N035,0);
    gl.glVertex3fv(P035,0);
    gl.glNormal3fv(N034,0);
    gl.glVertex3fv(P034,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N030,0);
    gl.glVertex3fv(P030,0);
    gl.glNormal3fv(N031,0);
    gl.glVertex3fv(P031,0);
    gl.glNormal3fv(N041,0);
    gl.glVertex3fv(P041,0);
    gl.glNormal3fv(N040,0);
    gl.glVertex3fv(P040,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N025,0);
    gl.glVertex3fv(P025,0);
    gl.glNormal3fv(N026,0);
    gl.glVertex3fv(P026,0);
    gl.glNormal3fv(N036,0);
    gl.glVertex3fv(P036,0);
    gl.glNormal3fv(N035,0);
    gl.glVertex3fv(P035,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N026,0);
    gl.glVertex3fv(P026,0);
    gl.glNormal3fv(N027,0);
    gl.glVertex3fv(P027,0);
    gl.glNormal3fv(N037,0);
    gl.glVertex3fv(P037,0);
    gl.glNormal3fv(N036,0);
    gl.glVertex3fv(P036,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N027,0);
    gl.glVertex3fv(P027,0);
    gl.glNormal3fv(N028,0);
    gl.glVertex3fv(P028,0);
    gl.glNormal3fv(N038,0);
    gl.glVertex3fv(P038,0);
    gl.glNormal3fv(N037,0);
    gl.glVertex3fv(P037,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N028,0);
    gl.glVertex3fv(P028,0);
    gl.glNormal3fv(N029,0);
    gl.glVertex3fv(P029,0);
    gl.glNormal3fv(N039,0);
    gl.glVertex3fv(P039,0);
    gl.glNormal3fv(N038,0);
    gl.glVertex3fv(P038,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N029,0);
    gl.glVertex3fv(P029,0);
    gl.glNormal3fv(N030,0);
    gl.glVertex3fv(P030,0);
    gl.glNormal3fv(N040,0);
    gl.glVertex3fv(P040,0);
    gl.glNormal3fv(N039,0);
    gl.glVertex3fv(P039,0);
    gl.glEnd();
  }

  void
    Fish004()
  {
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N040,0);
    gl.glVertex3fv(P040,0);
    gl.glNormal3fv(N041,0);
    gl.glVertex3fv(P041,0);
    gl.glNormal3fv(N051,0);
    gl.glVertex3fv(P051,0);
    gl.glNormal3fv(N050,0);
    gl.glVertex3fv(P050,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N041,0);
    gl.glVertex3fv(P041,0);
    gl.glNormal3fv(N042,0);
    gl.glVertex3fv(P042,0);
    gl.glNormal3fv(N052,0);
    gl.glVertex3fv(P052,0);
    gl.glNormal3fv(N051,0);
    gl.glVertex3fv(P051,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N042,0);
    gl.glVertex3fv(P042,0);
    gl.glNormal3fv(N033,0);
    gl.glVertex3fv(P033,0);
    gl.glNormal3fv(N043,0);
    gl.glVertex3fv(P043,0);
    gl.glNormal3fv(N052,0);
    gl.glVertex3fv(P052,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N033,0);
    gl.glVertex3fv(P033,0);
    gl.glNormal3fv(N034,0);
    gl.glVertex3fv(P034,0);
    gl.glNormal3fv(N044,0);
    gl.glVertex3fv(P044,0);
    gl.glNormal3fv(N043,0);
    gl.glVertex3fv(P043,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N034,0);
    gl.glVertex3fv(P034,0);
    gl.glNormal3fv(N035,0);
    gl.glVertex3fv(P035,0);
    gl.glNormal3fv(N045,0);
    gl.glVertex3fv(P045,0);
    gl.glNormal3fv(N044,0);
    gl.glVertex3fv(P044,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N035,0);
    gl.glVertex3fv(P035,0);
    gl.glNormal3fv(N036,0);
    gl.glVertex3fv(P036,0);
    gl.glNormal3fv(N046,0);
    gl.glVertex3fv(P046,0);
    gl.glNormal3fv(N045,0);
    gl.glVertex3fv(P045,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N036,0);
    gl.glVertex3fv(P036,0);
    gl.glNormal3fv(N037,0);
    gl.glVertex3fv(P037,0);
    gl.glNormal3fv(N047,0);
    gl.glVertex3fv(P047,0);
    gl.glNormal3fv(N046,0);
    gl.glVertex3fv(P046,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N037,0);
    gl.glVertex3fv(P037,0);
    gl.glNormal3fv(N038,0);
    gl.glVertex3fv(P038,0);
    gl.glNormal3fv(N048,0);
    gl.glVertex3fv(P048,0);
    gl.glNormal3fv(N047,0);
    gl.glVertex3fv(P047,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N038,0);
    gl.glVertex3fv(P038,0);
    gl.glNormal3fv(N039,0);
    gl.glVertex3fv(P039,0);
    gl.glNormal3fv(N049,0);
    gl.glVertex3fv(P049,0);
    gl.glNormal3fv(N048,0);
    gl.glVertex3fv(P048,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N039,0);
    gl.glVertex3fv(P039,0);
    gl.glNormal3fv(N040,0);
    gl.glVertex3fv(P040,0);
    gl.glNormal3fv(N050,0);
    gl.glVertex3fv(P050,0);
    gl.glNormal3fv(N049,0);
    gl.glVertex3fv(P049,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N070,0);
    gl.glVertex3fv(P070,0);
    gl.glNormal3fv(N061,0);
    gl.glVertex3fv(P061,0);
    gl.glNormal3fv(N002,0);
    gl.glVertex3fv(P002,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N061,0);
    gl.glVertex3fv(P061,0);
    gl.glNormal3fv(N046,0);
    gl.glVertex3fv(P046,0);
    gl.glNormal3fv(N002,0);
    gl.glVertex3fv(P002,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N045,0);
    gl.glVertex3fv(P045,0);
    gl.glNormal3fv(N046,0);
    gl.glVertex3fv(P046,0);
    gl.glNormal3fv(N061,0);
    gl.glVertex3fv(P061,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N002,0);
    gl.glVertex3fv(P002,0);
    gl.glNormal3fv(N061,0);
    gl.glVertex3fv(P061,0);
    gl.glNormal3fv(N070,0);
    gl.glVertex3fv(P070,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N002,0);
    gl.glVertex3fv(P002,0);
    gl.glNormal3fv(N045,0);
    gl.glVertex3fv(P045,0);
    gl.glNormal3fv(N061,0);
    gl.glVertex3fv(P061,0);
    gl.glEnd();
  }

  void
    Fish005()
  {
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N002,0);
    gl.glVertex3fv(P002,0);
    gl.glNormal3fv(N044,0);
    gl.glVertex3fv(P044,0);
    gl.glNormal3fv(N045,0);
    gl.glVertex3fv(P045,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N002,0);
    gl.glVertex3fv(P002,0);
    gl.glNormal3fv(N043,0);
    gl.glVertex3fv(P043,0);
    gl.glNormal3fv(N044,0);
    gl.glVertex3fv(P044,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N002,0);
    gl.glVertex3fv(P002,0);
    gl.glNormal3fv(N052,0);
    gl.glVertex3fv(P052,0);
    gl.glNormal3fv(N043,0);
    gl.glVertex3fv(P043,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N002,0);
    gl.glVertex3fv(P002,0);
    gl.glNormal3fv(N051,0);
    gl.glVertex3fv(P051,0);
    gl.glNormal3fv(N052,0);
    gl.glVertex3fv(P052,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N002,0);
    gl.glVertex3fv(P002,0);
    gl.glNormal3fv(N046,0);
    gl.glVertex3fv(P046,0);
    gl.glNormal3fv(N047,0);
    gl.glVertex3fv(P047,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N002,0);
    gl.glVertex3fv(P002,0);
    gl.glNormal3fv(N047,0);
    gl.glVertex3fv(P047,0);
    gl.glNormal3fv(N048,0);
    gl.glVertex3fv(P048,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N002,0);
    gl.glVertex3fv(P002,0);
    gl.glNormal3fv(N048,0);
    gl.glVertex3fv(P048,0);
    gl.glNormal3fv(N049,0);
    gl.glVertex3fv(P049,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N002,0);
    gl.glVertex3fv(P002,0);
    gl.glNormal3fv(N049,0);
    gl.glVertex3fv(P049,0);
    gl.glNormal3fv(N050,0);
    gl.glVertex3fv(P050,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N050,0);
    gl.glVertex3fv(P050,0);
    gl.glNormal3fv(N051,0);
    gl.glVertex3fv(P051,0);
    gl.glNormal3fv(N069,0);
    gl.glVertex3fv(P069,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N051,0);
    gl.glVertex3fv(P051,0);
    gl.glNormal3fv(N002,0);
    gl.glVertex3fv(P002,0);
    gl.glNormal3fv(N069,0);
    gl.glVertex3fv(P069,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N050,0);
    gl.glVertex3fv(P050,0);
    gl.glNormal3fv(N069,0);
    gl.glVertex3fv(P069,0);
    gl.glNormal3fv(N002,0);
    gl.glVertex3fv(P002,0);
    gl.glEnd();
  }

  void
    Fish006()
  {
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N066,0);
    gl.glVertex3fv(P066,0);
    gl.glNormal3fv(N016,0);
    gl.glVertex3fv(P016,0);
    gl.glNormal3fv(N026,0);
    gl.glVertex3fv(P026,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N015,0);
    gl.glVertex3fv(P015,0);
    gl.glNormal3fv(N066,0);
    gl.glVertex3fv(P066,0);
    gl.glNormal3fv(N025,0);
    gl.glVertex3fv(P025,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N025,0);
    gl.glVertex3fv(P025,0);
    gl.glNormal3fv(N066,0);
    gl.glVertex3fv(P066,0);
    gl.glNormal3fv(N026,0);
    gl.glVertex3fv(P026,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N066,0);
    gl.glVertex3fv(P066,0);
    gl.glNormal3fv(N058,0);
    gl.glVertex3fv(P058,0);
    gl.glNormal3fv(N016,0);
    gl.glVertex3fv(P016,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N015,0);
    gl.glVertex3fv(P015,0);
    gl.glNormal3fv(N058,0);
    gl.glVertex3fv(P058,0);
    gl.glNormal3fv(N066,0);
    gl.glVertex3fv(P066,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N058,0);
    gl.glVertex3fv(P058,0);
    gl.glNormal3fv(N015,0);
    gl.glVertex3fv(P015,0);
    gl.glNormal3fv(N016,0);
    gl.glVertex3fv(P016,0);
    gl.glEnd();
  }

  void
    Fish007()
  {
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N062,0);
    gl.glVertex3fv(P062,0);
    gl.glNormal3fv(N022,0);
    gl.glVertex3fv(P022,0);
    gl.glNormal3fv(N032,0);
    gl.glVertex3fv(P032,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N062,0);
    gl.glVertex3fv(P062,0);
    gl.glNormal3fv(N032,0);
    gl.glVertex3fv(P032,0);
    gl.glNormal3fv(N064,0);
    gl.glVertex3fv(P064,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N022,0);
    gl.glVertex3fv(P022,0);
    gl.glNormal3fv(N062,0);
    gl.glVertex3fv(P062,0);
    gl.glNormal3fv(N032,0);
    gl.glVertex3fv(P032,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N062,0);
    gl.glVertex3fv(P062,0);
    gl.glNormal3fv(N064,0);
    gl.glVertex3fv(P064,0);
    gl.glNormal3fv(N032,0);
    gl.glVertex3fv(P032,0);
    gl.glEnd();
  }

  void
    Fish008()
  {
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N063,0);
    gl.glVertex3fv(P063,0);
    gl.glNormal3fv(N019,0);
    gl.glVertex3fv(P019,0);
    gl.glNormal3fv(N029,0);
    gl.glVertex3fv(P029,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N019,0);
    gl.glVertex3fv(P019,0);
    gl.glNormal3fv(N063,0);
    gl.glVertex3fv(P063,0);
    gl.glNormal3fv(N029,0);
    gl.glVertex3fv(P029,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N063,0);
    gl.glVertex3fv(P063,0);
    gl.glNormal3fv(N029,0);
    gl.glVertex3fv(P029,0);
    gl.glNormal3fv(N065,0);
    gl.glVertex3fv(P065,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glNormal3fv(N063,0);
    gl.glVertex3fv(P063,0);
    gl.glNormal3fv(N065,0);
    gl.glVertex3fv(P065,0);
    gl.glNormal3fv(N029,0);
    gl.glVertex3fv(P029,0);
    gl.glEnd();
  }

  void
    Fish009()
  {
    gl.glBegin(GL2.GL_POLYGON);
    gl.glVertex3fv(P059,0);
    gl.glVertex3fv(P012,0);
    gl.glVertex3fv(P009,0);
    gl.glVertex3fv(P060,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_POLYGON);
    gl.glVertex3fv(P012,0);
    gl.glVertex3fv(P004,0);
    gl.glVertex3fv(P007,0);
    gl.glVertex3fv(P009,0);
    gl.glEnd();
  }

  void
    Fish_1()
  {
    Fish004();
    Fish005();
    Fish003();
    Fish007();
    Fish006();
    Fish002();
    Fish008();
    Fish009();
    Fish001();
  }

  void
    Fish_2()
  {
    Fish005();
    Fish004();
    Fish003();
    Fish008();
    Fish006();
    Fish002();
    Fish007();
    Fish009();
    Fish001();
  }

  void
    Fish_3()
  {
    Fish005();
    Fish004();
    Fish007();
    Fish003();
    Fish002();
    Fish008();
    Fish009();
    Fish001();
    Fish006();
  }

  void
    Fish_4()
  {
    Fish005();
    Fish004();
    Fish008();
    Fish003();
    Fish002();
    Fish007();
    Fish009();
    Fish001();
    Fish006();
  }

  void
    Fish_5()
  {
    Fish009();
    Fish006();
    Fish007();
    Fish001();
    Fish002();
    Fish003();
    Fish008();
    Fish004();
    Fish005();
  }

  void
    Fish_6()
  {
    Fish009();
    Fish006();
    Fish008();
    Fish001();
    Fish002();
    Fish007();
    Fish003();
    Fish004();
    Fish005();
  }

  void
    Fish_7()
  {
    Fish009();
    Fish001();
    Fish007();
    Fish005();
    Fish002();
    Fish008();
    Fish003();
    Fish004();
    Fish006();
  }

  void
    Fish_8()
  {
    Fish009();
    Fish008();
    Fish001();
    Fish002();
    Fish007();
    Fish003();
    Fish005();
    Fish004();
    Fish006();
  }


  /* *INDENT-OFF* */
  static float[] N002 = {0.000077f,-0.020611f,0.999788f};
  static float[] N003 = {0.961425f,0.258729f,-0.093390f};
  static float[] N004 = {0.510811f,-0.769633f,-0.383063f};
  static float[] N005 = {0.400123f,0.855734f,-0.328055f};
  static float[] N006 = {-0.770715f,0.610204f,-0.183440f};
  static float[] N007 = {-0.915597f,-0.373345f,-0.149316f};
  static float[] N008 = {-0.972788f,0.208921f,-0.100179f};
  static float[] N009 = {-0.939713f,-0.312268f,-0.139383f};
  static float[] N010 = {-0.624138f,-0.741047f,-0.247589f};
  static float[] N011 = {0.591434f,-0.768401f,-0.244471f};
  static float[] N012 = {0.935152f,-0.328495f,-0.132598f};
  static float[] N013 = {0.997102f,0.074243f,-0.016593f};
  static float[] N014 = {0.969995f,0.241712f,-0.026186f};
  static float[] N015 = {0.844539f,0.502628f,-0.184714f};
  static float[] N016 = {-0.906608f,0.386308f,-0.169787f};
  static float[] N017 = {-0.970016f,0.241698f,-0.025516f};
  static float[] N018 = {-0.998652f,0.050493f,-0.012045f};
  static float[] N019 = {-0.942685f,-0.333051f,-0.020556f};
  static float[] N020 = {-0.660944f,-0.750276f,0.015480f};
  static float[] N021 = {0.503549f,-0.862908f,-0.042749f};
  static float[] N022 = {0.953202f,-0.302092f,-0.012089f};
  static float[] N023 = {0.998738f,0.023574f,0.044344f};
  static float[] N024 = {0.979297f,0.193272f,0.060202f};
  static float[] N025 = {0.798300f,0.464885f,0.382883f};
  static float[] N026 = {-0.756590f,0.452403f,0.472126f};
  static float[] N027 = {-0.953855f,0.293003f,0.065651f};
  static float[] N028 = {-0.998033f,0.040292f,0.048028f};
  static float[] N029 = {-0.977079f,-0.204288f,0.059858f};
  static float[] N030 = {-0.729117f,-0.675304f,0.111140f};
  static float[] N031 = {0.598361f,-0.792753f,0.116221f};
  static float[] N032 = {0.965192f,-0.252991f,0.066332f};
  static float[] N033 = {0.998201f,-0.002790f,0.059892f};
  static float[] N034 = {0.978657f,0.193135f,0.070207f};
  static float[] N035 = {0.718815f,0.680392f,0.142733f};
  static float[] N036 = {-0.383096f,0.906212f,0.178936f};
  static float[] N037 = {-0.952831f,0.292590f,0.080647f};
  static float[] N038 = {-0.997680f,0.032417f,0.059861f};
  static float[] N039 = {-0.982629f,-0.169881f,0.074700f};
  static float[] N040 = {-0.695424f,-0.703466f,0.146700f};
  static float[] N041 = {0.359323f,-0.915531f,0.180805f};
  static float[] N042 = {0.943356f,-0.319387f,0.089842f};
  static float[] N043 = {0.998272f,-0.032435f,0.048993f};
  static float[] N044 = {0.978997f,0.193205f,0.065084f};
  static float[] N045 = {0.872144f,0.470094f,-0.135565f};
  static float[] N046 = {-0.664282f,0.737945f,-0.119027f};
  static float[] N047 = {-0.954508f,0.288570f,0.075107f};
  static float[] N048 = {-0.998273f,0.032406f,0.048993f};
  static float[] N049 = {-0.979908f,-0.193579f,0.048038f};
  static float[] N050 = {-0.858736f,-0.507202f,-0.072938f};
  static float[] N051 = {0.643545f,-0.763887f,-0.048237f};
  static float[] N052 = {0.955580f,-0.288954f,0.058068f};
  static float[] N058 = {0.000050f,0.793007f,-0.609213f};
  static float[] N059 = {0.913510f,0.235418f,-0.331779f};
  static float[] N060 = {-0.807970f,0.495000f,-0.319625f};
  static float[] N061 = {0.000000f,0.784687f,-0.619892f};
  static float[] N062 = {0.000000f,-1.000000f,0.000000f};
  static float[] N063 = {0.000000f,1.000000f,0.000000f};
  static float[] N064 = {0.000000f,1.000000f,0.000000f};
  static float[] N065 = {0.000000f,1.000000f,0.000000f};
  static float[] N066 = {-0.055784f,0.257059f,0.964784f};
  static float[] N069 = {-0.000505f,-0.929775f,-0.368127f};
  static float[] N070 = {0.000000f,1.000000f,0.000000f};
  static float[] P002 = {0.00f, -36.59f, 5687.72f};
  static float[] P003 = {90.00f, 114.73f, 724.38f};
  static float[] P004 = {58.24f, -146.84f, 262.35f};
  static float[] P005 = {27.81f, 231.52f, 510.43f};
  static float[] P006 = {-27.81f, 230.43f, 509.76f};
  static float[] P007 = {-46.09f, -146.83f, 265.84f};
  static float[] P008 = {-90.00f, 103.84f, 718.53f};
  static float[] P009 = {-131.10f, -165.92f, 834.85f};
  static float[] P010 = {-27.81f, -285.31f, 500.00f};
  static float[] P011 = {27.81f, -285.32f, 500.00f};
  static float[] P012 = {147.96f, -170.89f, 845.50f};
  static float[] P013 = {180.00f, 0.00f, 2000.00f};
  static float[] P014 = {145.62f, 352.67f, 2000.00f};
  static float[] P015 = {55.62f, 570.63f, 2000.00f};
  static float[] P016 = {-55.62f, 570.64f, 2000.00f};
  static float[] P017 = {-145.62f, 352.68f, 2000.00f};
  static float[] P018 = {-180.00f, 0.01f, 2000.00f};
  static float[] P019 = {-178.20f, -352.66f, 2001.61f};
  static float[] P020 = {-55.63f, -570.63f, 2000.00f};
  static float[] P021 = {55.62f, -570.64f, 2000.00f};
  static float[] P022 = {179.91f, -352.69f, 1998.39f};
  static float[] P023 = {150.00f, 0.00f, 3000.00f};
  static float[] P024 = {121.35f, 293.89f, 3000.00f};
  static float[] P025 = {46.35f, 502.93f, 2883.09f};
  static float[] P026 = {-46.35f, 497.45f, 2877.24f};
  static float[] P027 = {-121.35f, 293.90f, 3000.00f};
  static float[] P028 = {-150.00f, 0.00f, 3000.00f};
  static float[] P029 = {-152.21f, -304.84f, 2858.68f};
  static float[] P030 = {-46.36f, -475.52f, 3000.00f};
  static float[] P031 = {46.35f, -475.53f, 3000.00f};
  static float[] P032 = {155.64f, -304.87f, 2863.50f};
  static float[] P033 = {90.00f, 0.00f, 4000.00f};
  static float[] P034 = {72.81f, 176.33f, 4000.00f};
  static float[] P035 = {27.81f, 285.32f, 4000.00f};
  static float[] P036 = {-27.81f, 285.32f, 4000.00f};
  static float[] P037 = {-72.81f, 176.34f, 4000.00f};
  static float[] P038 = {-90.00f, 0.00f, 4000.00f};
  static float[] P039 = {-72.81f, -176.33f, 4000.00f};
  static float[] P040 = {-27.81f, -285.31f, 4000.00f};
  static float[] P041 = {27.81f, -285.32f, 4000.00f};
  static float[] P042 = {72.81f, -176.34f, 4000.00f};
  static float[] P043 = {30.00f, 0.00f, 5000.00f};
  static float[] P044 = {24.27f, 58.78f, 5000.00f};
  static float[] P045 = {9.27f, 95.11f, 5000.00f};
  static float[] P046 = {-9.27f, 95.11f, 5000.00f};
  static float[] P047 = {-24.27f, 58.78f, 5000.00f};
  static float[] P048 = {-30.00f, 0.00f, 5000.00f};
  static float[] P049 = {-24.27f, -58.78f, 5000.00f};
  static float[] P050 = {-9.27f, -95.10f, 5000.00f};
  static float[] P051 = {9.27f, -95.11f, 5000.00f};
  static float[] P052 = {24.27f, -58.78f, 5000.00f};
  static float[] P058 = {0.00f, 1212.72f, 2703.08f};
  static float[] P059 = {50.36f, 0.00f, 108.14f};
  static float[] P060 = {-22.18f, 0.00f, 108.14f};
  static float[] P061 = {0.00f, 1181.61f, 6344.65f};
  static float[] P062 = {516.45f, -887.08f, 2535.45f};
  static float[] P063 = {-545.69f, -879.31f, 2555.63f};
  static float[] P064 = {618.89f, -1005.64f, 2988.32f};
  static float[] P065 = {-635.37f, -1014.79f, 2938.68f};
  static float[] P066 = {0.00f, 1374.43f, 3064.18f};
  static float[] P069 = {0.00f, -418.25f, 5765.04f};
  static float[] P070 = {0.00f, 1266.91f, 6629.60f};
  static float[] P071 = {-139.12f, -124.96f, 997.98f};
  static float[] P072 = {-139.24f, -110.18f, 1020.68f};
  static float[] P073 = {-137.33f, -94.52f, 1022.63f};
  static float[] P074 = {-137.03f, -79.91f, 996.89f};
  static float[] P075 = {-135.21f, -91.48f, 969.14f};
  static float[] P076 = {-135.39f, -110.87f, 968.76f};
  static float[] P077 = {150.23f, -78.44f, 995.53f};
  static float[] P078 = {152.79f, -92.76f, 1018.46f};
  static float[] P079 = {154.19f, -110.20f, 1020.55f};
  static float[] P080 = {151.33f, -124.15f, 993.77f};
  static float[] P081 = {150.49f, -111.19f, 969.86f};
  static float[] P082 = {150.79f, -92.41f, 969.70f};
  static float[] iP002 = {0.00f, -36.59f, 5687.72f};
  static float[] iP004 = {58.24f, -146.84f, 262.35f};
  static float[] iP007 = {-46.09f, -146.83f, 265.84f};
  static float[] iP010 = {-27.81f, -285.31f, 500.00f};
  static float[] iP011 = {27.81f, -285.32f, 500.00f};
  static float[] iP023 = {150.00f, 0.00f, 3000.00f};
  static float[] iP024 = {121.35f, 293.89f, 3000.00f};
  static float[] iP025 = {46.35f, 502.93f, 2883.09f};
  static float[] iP026 = {-46.35f, 497.45f, 2877.24f};
  static float[] iP027 = {-121.35f, 293.90f, 3000.00f};
  static float[] iP028 = {-150.00f, 0.00f, 3000.00f};
  static float[] iP029 = {-121.35f, -304.84f, 2853.86f};
  static float[] iP030 = {-46.36f, -475.52f, 3000.00f};
  static float[] iP031 = {46.35f, -475.53f, 3000.00f};
  static float[] iP032 = {121.35f, -304.87f, 2853.86f};
  static float[] iP033 = {90.00f, 0.00f, 4000.00f};
  static float[] iP034 = {72.81f, 176.33f, 4000.00f};
  static float[] iP035 = {27.81f, 285.32f, 4000.00f};
  static float[] iP036 = {-27.81f, 285.32f, 4000.00f};
  static float[] iP037 = {-72.81f, 176.34f, 4000.00f};
  static float[] iP038 = {-90.00f, 0.00f, 4000.00f};
  static float[] iP039 = {-72.81f, -176.33f, 4000.00f};
  static float[] iP040 = {-27.81f, -285.31f, 4000.00f};
  static float[] iP041 = {27.81f, -285.32f, 4000.00f};
  static float[] iP042 = {72.81f, -176.34f, 4000.00f};
  static float[] iP043 = {30.00f, 0.00f, 5000.00f};
  static float[] iP044 = {24.27f, 58.78f, 5000.00f};
  static float[] iP045 = {9.27f, 95.11f, 5000.00f};
  static float[] iP046 = {-9.27f, 95.11f, 5000.00f};
  static float[] iP047 = {-24.27f, 58.78f, 5000.00f};
  static float[] iP048 = {-30.00f, 0.00f, 5000.00f};
  static float[] iP049 = {-24.27f, -58.78f, 5000.00f};
  static float[] iP050 = {-9.27f, -95.10f, 5000.00f};
  static float[] iP051 = {9.27f, -95.11f, 5000.00f};
  static float[] iP052 = {24.27f, -58.78f, 5000.00f};
  static float[] iP061 = {0.00f, 1181.61f, 6344.65f};
  static float[] iP069 = {0.00f, -418.25f, 5765.04f};
  static float[] iP070 = {0.00f, 1266.91f, 6629.60f};
  /* *INDENT-ON* */

}

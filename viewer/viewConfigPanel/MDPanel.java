package viewer.viewConfigPanel;

import java.io.*;
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

import viewer.*;
import data.*;

/**
 * Molecular Dynamics
 */
public class MDPanel extends JPanel implements ActionListener{

  Controller ctrl;
  public MDPanel(Controller ctrl){
    this.ctrl=ctrl;
    createPanel();

  }

  public void actionPerformed( ActionEvent ae){
    if( ae.getSource() == applyButton){
      new MDFrame();
    }
  }

  private JButton applyButton;
  private void createPanel(){
    applyButton = new JButton( "Apply" );
    applyButton.addActionListener( this );
    applyButton.setFocusable(false);

    add(applyButton);
  }
}

  ////////////
class MDFrame extends JPanel implements GLEventListener,
                                        KeyListener,
                                        MouseListener,
                                        MouseMotionListener,
                                        MouseWheelListener{
  MDFrame(){
    init();
    jframe.setTitle( "Molecular Dynamics: LJ" );
    jframe.setVisible( true );
  }

  GLAutoDrawable drawable;
  GL2   gl;
  GLU  glu;
  GLUT glut;

  JFrame jframe;
  Container pane;
  GLCanvas panel;

  int prev_x,prev_y;
  float angle_x=0,angle_y=0;
  float trans_x=0,trans_y=0;
  float scale=1;
  int width,height;


  FPSAnimator fpsAnimator;
  int fps=1;

  //called by constructor
  public void init(){
    panel = new GLCanvas();
    panel.addGLEventListener( this );
    panel.addKeyListener( this );
    jframe = new JFrame("Sun Earth Moon");
    pane = jframe.getContentPane();
    pane.add( panel );
    jframe.setBounds(new Rectangle( 0, 0, 600, 600 ) );
    jframe.addKeyListener( this );
    jframe.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

    fpsAnimator = new FPSAnimator( drawable, fps );
    mdSetup();
  }



  //called by OpenGL
  public void init(GLAutoDrawable drawable){
    this.drawable = drawable;
    gl   = drawable.getGL().getGL2();
    glu  = new GLU();
    glut = new GLUT();

    if ( drawable instanceof AWTGLAutoDrawable ) {
      AWTGLAutoDrawable awtDrawable = (AWTGLAutoDrawable) drawable;
      awtDrawable.addMouseListener( this );
      awtDrawable.addMouseMotionListener( this );
      awtDrawable.addMouseWheelListener( this );
    }

    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

    gl.glShadeModel( GL2.GL_SMOOTH );
    gl.glEnable( GL2.GL_DEPTH_TEST );
    gl.glEnable( GL2.GL_NORMALIZE );
    gl.glEnable( GL2.GL_LINE_SMOOTH );
    gl.glEnable(GL2.GL_BLEND); //for alpha blending
    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
  }

  //called by OpenGL
  public void display(GLAutoDrawable drawable){
    if ( (drawable instanceof GLJPanel) &&
         !((GLJPanel) drawable).isOpaque() &&
         ((GLJPanel) drawable).shouldPreserveColorBufferIfTranslucent() ){
      gl.glClear( GL2.GL_DEPTH_BUFFER_BIT );
    } else {
      gl.glClear( GL2.GL_COLOR_BUFFER_BIT |GL2.GL_DEPTH_BUFFER_BIT );
    }


    //procedure
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity(); // Clear
    //eye
    glu.gluLookAt(eye[0],eye[1],eye[2],drc[0],
                  drc[1],drc[2],up[0],up[1],up[2]);
    //set light
    setLight();
    gl.glPushMatrix();

    //move
    gl.glTranslated( (double)trans_x,(double)trans_y,0 );
    gl.glRotatef(angle_x,1.0f,0.0f,0.0f);
    gl.glRotatef(angle_y,0.0f,1.0f,0.0f);
    gl.glScalef( scale,scale,scale );


    //make & show
    makeAxis();

    if(fpsAnimator.isAnimating()) MDstep(100);
    atomshow();

    gl.glPopMatrix();


    if(fpsAnimator.isAnimating()){
      if ( drawable instanceof AWTGLAutoDrawable ) {
        AWTGLAutoDrawable awtDrawable = (AWTGLAutoDrawable) drawable;
        awtDrawable.repaint();
      }
    }

  }
  public void displayChanged(GLAutoDrawable drawable,
                             boolean modeChanged,
                             boolean deviceChanged){
  }

  public float pos[] = { 1.0f, 0.0f, 0.0f, 0.0f };
  public float dif[] = { 0.0f, 0.0f, 1.0f, 1.0f };
  public float amb[] = { 0.2f, 0.2f, 0.2f, 1.0f };
  public float spc[] = { 0.9f, 0.9f, 0.9f, 1.0f };
  public float emi[] = { 0.9f, 0.9f, 0.9f, 1.0f };
  int shininess=50;
  void setLight(){
    gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0 );
    gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_AMBIENT,  amb, 0 );
    gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_DIFFUSE,  dif, 0 );
    gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_SPECULAR, spc, 0 );

    float[] color={1.f, 1.f, 1.f };
    gl.glEnable(GL2.GL_COLOR_MATERIAL);

    gl.glMaterialfv(GL2.GL_FRONT_AND_BACK,GL2.GL_AMBIENT_AND_DIFFUSE,color,0);
    gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_SPECULAR, spc, 0 );
    gl.glMateriali( GL2.GL_FRONT, GL2.GL_SHININESS, shininess );

    gl.glEnable( GL2.GL_LIGHTING );
    gl.glEnable( GL2.GL_LIGHT0 );

  }


  void makeAxis(){
    gl.glBegin(GL2.GL_LINES);
    gl.glLineWidth(2);

    gl.glColor3f(1.0f, 0.0f, 0.0f );

    gl.glVertex3d(0., 0., 0.);
    gl.glVertex3d(al[0], 0., 0.);

    gl.glColor3f( 0.0f, 1.0f, 0.0f );
    gl.glVertex3d(0., 0., 0.);
    gl.glVertex3d(0., al[1], 0.);

    gl.glColor3f( 0.0f, 0.0f, 1.0f );
    gl.glVertex3d(0., 0., 0.);
    gl.glVertex3d(0., 0., al[2]);
    gl.glEnd();

  }

  public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h){
    width=w;
    height=h;
    GL2 gl = drawable.getGL().getGL2();
    gl.glViewport(0, 0, w, h);
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    //glu.gluPerspective(50.0, (double)w/(double)h, .5, 100.0);
    gl.glOrtho(-al[0],al[0],-al[0],al[0],0.5, al[0]);

    gl.glMatrixMode(GL2.GL_MODELVIEW);
  }

  public void mouseEntered( MouseEvent me ) {
  }

  public void mouseExited( MouseEvent me ) {
  }

  boolean mouseLButton;
  boolean mouseMButton;
  boolean mouseRButton;
  public void mousePressed( MouseEvent me ) {
    prev_x=me.getX();
    prev_y=me.getY();
    mouseLButton = false;
    mouseMButton = false;
    mouseRButton = false;
    if ( (me.getModifiers() & me.BUTTON1_MASK) != 0 ) {
      mouseLButton = true;
    }
    else if ( (me.getModifiers() & me.BUTTON2_MASK) != 0 ) {
      mouseMButton = true;
    }
    else if ( (me.getModifiers() & me.BUTTON3_MASK) != 0 ) {
      mouseRButton = true;
    }
  }
  public void mouseReleased( MouseEvent me ) {
  }
  public void mouseClicked( MouseEvent me ) {
  }
  public void mouseDragged( MouseEvent me ) {
    int x=me.getX();
    int y=me.getY();
    int dx=x-prev_x;
    int dy=y-prev_y;

    if(mouseLButton){

      //swich according to modifires for notebook.
      if((me.getModifiers() & InputEvent.SHIFT_MASK) !=0){
        //trans
        if(Math.abs(dx) > Math.abs(dy)) trans_x += dx*0.1f;
        else trans_y += -dy*0.1f;
      }
      else if((me.getModifiers() & InputEvent.ALT_MASK) !=0){
        //zoom
        if(Math.abs(dx) > Math.abs(dy)) dy = dx;
        eye[2]+= dy/2;
        if(eye[2]<0)eye[2]=0.0001;
      }
      else if((me.getModifiers() & InputEvent.META_MASK) !=0){
        //scale
        if(dx>0)scale+=0.5;
        else scale-=0.5;
        //if(dy>0)scale+=0.5;
        //else scale-=0.5;
      }
      else{
        //rotate
        angle_y+=dx*360f/width;
        angle_x+=dy*360f/height;
        prev_x=x;
        prev_y=y;

      }
    }else if(mouseMButton){
    }else if(mouseRButton){
      //trans
      if(Math.abs(dx) > Math.abs(dy)) trans_x += dx*0.1f;
      else trans_y += -dy*0.1f;
    }
    if ( drawable instanceof AWTGLAutoDrawable ) {
      AWTGLAutoDrawable awtDrawable = (AWTGLAutoDrawable) drawable;
      awtDrawable.repaint();
    }

  }
  public void mouseMoved( MouseEvent me ) {
  }
  public void mouseWheelMoved( MouseWheelEvent mwe ) {
  }

  public void keyPressed( KeyEvent ke ) {
    switch ( ke.getKeyCode() ) {
    case KeyEvent.VK_ESCAPE:
      jframe.dispose();
      break;
    case KeyEvent.VK_H:
      setHome();
      break;
    case KeyEvent.VK_S:
      if(fpsAnimator.isAnimating()){
        fpsAnimator.stop();
      }else{
        fpsAnimator.start();
      }
      break;

    }
    if ( drawable instanceof AWTGLAutoDrawable ) {
      AWTGLAutoDrawable awtDrawable = (AWTGLAutoDrawable) drawable;
      awtDrawable.repaint();
    }
  }
  public void keyReleased( KeyEvent ke ) {
  }
  public void keyTyped( KeyEvent ke ) {
  }

  //-- MD --------------------------------
  double dt=40,dt2=40*0.5;

  int Nx=4,Ny=4,Nz=4;
  int N=4*Nx*Ny*Nz;
  double[][] x=new double[N][3];
  double[][] v=new double[N][3];
  double[][] f=new double[N][3];
  int ntable=500;
  double[][] pottable=new double[2][ntable];
  int nlspr=200;
  int[][] lspr=new int[N][nlspr];

  double eps=0.01032326/27.2116;
  double sgm=3.41/0.529177;
  double mass=1840*40;
  double massi=1/mass;
  double rc=2.5*sgm;
  double sgm6=sgm*sgm*sgm*sgm*sgm*sgm;
  double cunit=10.0399231105058;
  double[] al={Nx*cunit,Ny*cunit,Nz*cunit};
  double[][] fcc={{0.0, 0.0, 0.0 },{0.5, 0.5, 0.0 },
                  {0.5, 0.0, 0.5 },{0.0, 0.5, 0.5 }};
  double ekin,epot;
  int inc;
  double r,ri,ri6,r2;
  double vrc,dvrc;

  public void mdSetup(){
    //init v
    for(int i=0;i<N;i++) for(int j=0;j<3;j++) v[i][j]=0.;

    //init x
    inc=0;
    for(int i=0;i<Nx;i++){
      for(int j=0;j<Ny;j++){
        for(int k=0;k<Nz;k++){
          for(int l=0;l<4;l++){
            x[inc][0]=(fcc[l][0]+i)*cunit;
            x[inc][1]=(fcc[l][1]+j)*cunit;
            x[inc][2]=(fcc[l][2]+k)*cunit;
            inc++;
          }
        }
      }
    }

    v[0][0]=0.0001;
    v[0][1]=0.0001;
    v[0][2]=0.0001;

    //mk pair list
    for(int i=0;i<N;i++){
      for(int j=0;j<nlspr;j++) lspr[i][j]=-1;
      inc=0;
      for(int j=i+1;j<N;j++){
        for(int k=0;k<3;k++) dr[k]=x[j][k]-x[i][k];
        r2=dr[0]*dr[0]+dr[1]*dr[1]+dr[2]*dr[2];
        if(r2<rc*rc) lspr[i][inc++]=j;
      }
    }
    //cutoff
    r=rc; ri=1/r; ri6=ri*ri*ri*ri*ri*ri;
    r=sgm6*ri6;
    vrc=eps*(r-1)*r;
    dvrc=-24*eps*(2*r-1)*r*ri;

    //pot table
    for(int i=0;i<ntable;i++){
      r=(double)i;
      ri=1/r; ri6=ri*ri*ri*ri*ri*ri;
      r=sgm6*ri6;
      pottable[0][i]=eps*(r-1)*r;
      pottable[1][i]=-24*eps*(2*r-1)*r*ri;
    }

  }

  double[] dr=new double[3];
  public void get_force(){
    int ir;
    double r,fr,dv;

    //init
    epot=0;
    for(int i=0;i<N;i++)for(int j=0;j<3;j++)f[i][j]=0.;

    //get
    for(int i=0;i<N;i++){
      for(int jj=0;jj<nlspr;jj++){
        int j=lspr[i][jj];
        if(j<0)break; //if j<0, go next i
        if(j<i)continue;
        for(int k=0;k<3;k++) dr[k]=x[j][k]-x[i][k];
        r2=dr[0]*dr[0]+dr[1]*dr[1]+dr[2]*dr[2];
        r=Math.sqrt( r2 );
        ir=(int)r; fr=r-ir;
        if(ir>ntable)ir=ntable-2;
        epot+=fr*pottable[0][ir]+(1-fr)*pottable[0][ir+1];
        dv=fr*pottable[1][ir]+(1-fr)*pottable[1][ir+1];
        dv/=r;
        f[i][0]+=dv*dr[0]; f[i][1]+=dv*dr[1]; f[i][2]+=dv*dr[2];
        f[j][0]-=dv*dr[0]; f[j][1]-=dv*dr[1]; f[j][2]-=dv*dr[2];
      }
    }

  }

  public void MDstep(int nloop){
    int iloop;
    get_force();
    for(iloop=0;iloop<nloop;iloop++){
      //kick
      for(int i=0;i<N;i++)for(int j=0;j<3;j++) v[i][j]=v[i][j]+f[i][j]*dt2*massi;
      //up
      for(int i=0;i<N;i++)for(int j=0;j<3;j++) x[i][j]=x[i][j]+v[i][j]*dt;
      //get force
      get_force();
      //kick
      for(int i=0;i<N;i++)for(int j=0;j<3;j++) v[i][j]=v[i][j]+f[i][j]*dt2*massi;
    }
  }

  public void atomshow(){
    gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_SPECULAR, spc, 0 );
    gl.glMateriali( GL2.GL_FRONT, GL2.GL_SHININESS, shininess );
    for(int i=0;i<N;i++){
      float[] color = {0.f, 0.f, 1.f};
      gl.glMaterialfv( GL2.GL_FRONT,GL2.GL_AMBIENT_AND_DIFFUSE,color, 0 );
      gl.glPushMatrix();
      gl.glTranslated( x[i][0],x[i][1],x[i][2] );
      double r=1;
      int sl=10,st=10;
      glut.glutSolidSphere( r, sl, st );
      gl.glPopMatrix();
    }
  }

  double eye[]={al[0]/2,al[1]/2,al[2]/2};
  double drc[]={al[0]/2,al[1]/2,-al[2]};
  double up[]={0.,1.,0.};
  public void setHome(){
    eye[0]=al[0]/2; eye[1]=al[1]/2; eye[2]=al[2]/2;
    drc[0]=al[0]/2; drc[1]=al[1]/2; drc[2]=-al[2];
    up[0]=0; up[1]=1; up[2]=0;
    scale=1;
    angle_x=0; angle_y=0;
    trans_x=0; trans_y=0;
  }
  public void dispose(GLAutoDrawable drawable){
  }

}//MDFrame

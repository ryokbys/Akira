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
import com.sun.opengl.util.awt.*;

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

  MDFrame md;
  public void actionPerformed( ActionEvent ae){
    if( ae.getSource() == loadButton){
      md=null;
      md= new MDFrame(ctrl);
      md.eps=(Double)spEps.getValue();
      md.sgm=(Double)spSgm.getValue();
      md.mass=(Double)spMass.getValue();
      md.dt=(Double)spDt.getValue();
      md.dmp=(Double)spDmp.getValue();
      md.set(ctrl.getActiveRW().atoms);
    }else if( ae.getSource() == startButton){
      if(md.fpsAnimator.isAnimating()){
        md.fpsAnimator.stop();
      }else{
        md.fpsAnimator.start();
      }
    }
  }

  private JButton loadButton;
  private JButton startButton;
  private JSpinner spSgm,spEps,spDt,spMass,spDmp;

  private void createPanel(){
    loadButton = new JButton( "Load" );
    loadButton.addActionListener( this );
    loadButton.setFocusable(false);

    startButton = new JButton( "Start" );
    startButton.addActionListener( this );
    startButton.setFocusable(false);

    JLabel lSgm = new JLabel( "sgm [Å]:" );
    spSgm = new JSpinner(new SpinnerNumberModel(3.41, 0., null, 1.));
    spSgm.setFocusable(false);
    spSgm.setPreferredSize(new Dimension(80, 25));
    JLabel lEps = new JLabel( "eps [T]:" );
    spEps = new JSpinner(new SpinnerNumberModel(120., 0., null, 50.));
    spEps.setFocusable(false);
    spEps.setPreferredSize(new Dimension(80, 25));
    JLabel lMass = new JLabel( "mass [a.u.]:" );
    spMass = new JSpinner(new SpinnerNumberModel(40., 0., null, 10.));
    spMass.setFocusable(false);
    spMass.setPreferredSize(new Dimension(80, 25));
    JLabel lDt = new JLabel( "dt [a.u.]:" );
    spDt = new JSpinner(new SpinnerNumberModel(40., 0., null, 10.));
    spDt.setFocusable(false);
    spDt.setPreferredSize(new Dimension(80, 25));
    JLabel lDmp = new JLabel( "damp. fac.:" );
    spDmp = new JSpinner(new SpinnerNumberModel(1., 0., 1, 0.01));
    spDmp.setFocusable(false);
    spDmp.setPreferredSize(new Dimension(80, 25));


    add(loadButton);
    add(startButton);
    add(lSgm);
    add(spSgm);
    add(lEps);
    add(spEps);
    add(lMass);
    add(spMass);
    add(lDt);
    add(spDt);
    add(lDmp);
    add(spDmp);
  }
}//MDPanel

////////////
class MDFrame extends JFrame implements GLEventListener,
                                        KeyListener,
                                        MouseListener,
                                        MouseMotionListener,
                                        MouseWheelListener{
  Controller ctrl;
  MDFrame(Controller ctrl){
    this.ctrl=ctrl;
    panel = new GLCanvas();
    panel.addGLEventListener( this );
    panel.addKeyListener( this );

    pane = this.getContentPane();
    pane.add( panel );
    this.setBounds(new Rectangle( 20, 20, 1024, 768 ) );
    this.addKeyListener( this );
    this.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

    setTitle( "Molecular Dynamics: LJ" );
    setVisible( true );
  }

  GLAutoDrawable drawable;
  GL2   gl;
  GLU  glu;
  GLUT glut;

  Container pane;
  GLCanvas panel;

  int prev_x,prev_y;
  float angle_x=0,angle_y=0;
  float trans_x=0,trans_y=0;
  float scale=1;
  int width,height;


  public FPSAnimator fpsAnimator;
  int fps=1;

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

    fpsAnimator = new FPSAnimator( drawable, fps );

    txtRenderer = new TextRenderer(ctrl.vconf.annotationFont, true, true);
  }
  TextRenderer txtRenderer;

  //called by OpenGL
  public void display(GLAutoDrawable drawable){
    if ( (drawable instanceof GLJPanel) &&
         !((GLJPanel) drawable).isOpaque() &&
         ((GLJPanel) drawable).shouldPreserveColorBufferIfTranslucent() ){
      gl.glClear( GL2.GL_DEPTH_BUFFER_BIT );
    } else {
      gl.glClear( GL2.GL_COLOR_BUFFER_BIT |GL2.GL_DEPTH_BUFFER_BIT );
    }


    //
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity(); // Clear
    //eye
    glu.gluLookAt(eye[0],eye[1],eye[2],
                  drc[0],drc[1],drc[2],
                  up[0],up[1],up[2]);
    //set light
    setLight();

    gl.glPushMatrix();

    //move
    gl.glRotatef(angle_x,1.0f,0.0f,0.0f);
    gl.glRotatef(angle_y,0.0f,1.0f,0.0f);
    gl.glTranslated( (double)trans_x,(double)trans_y,0 );
    gl.glScalef( scale,scale,scale );


    gl.glTranslated( -h[0][0]/2, -h[1][1]/2, -h[2][2]/2);

    //make & show
    if(box_t==-1)makeBox();
    gl.glCallList( box_t );

    if(fpsAnimator.isAnimating()) MDstep(100);
    atomshow();


    gl.glPopMatrix();

    //rot center
    if(draging)gl.glCallList( rotcent_t );


    //rendering most front layer
    gl.glClear( GL.GL_DEPTH_BUFFER_BIT );
    showStatus();

    //repaint();

  }

  public void displayChanged(GLAutoDrawable drawable,
                             boolean modeChanged,
                             boolean deviceChanged){
  }
  public void dispose(GLAutoDrawable drawable){
  }

  float pos[] = { 0.0f, 0.0f, 10.0f, 0.0f };
  float dif[] = { 1.0f, 1.0f, 1.0f, 1.0f };
  float amb[] = { 0.0f, 0.0f, 0.0f, 1.0f };
  float spc[] = { 0.0f, 0.0f, 0.0f, 1.0f };
  float emi[] = { 0.0f, 0.0f, 0.0f, 1.0f };
  int shininess=50;

  void setLight(){
    gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0 );
    gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_AMBIENT,  amb, 0 );
    gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_DIFFUSE,  dif, 0 );
    gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_SPECULAR, spc, 0 );

    //gl.glEnable(GL2.GL_COLOR_MATERIAL);
    //gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_SPECULAR, spc, 0 );
    //gl.glMateriali( GL2.GL_FRONT, GL2.GL_SHININESS, shininess );


    gl.glEnable( GL2.GL_LIGHTING );
    gl.glEnable( GL2.GL_LIGHT0 );

  }

  double[] chgScale( double[] in ){
    double[] out = new double[3];
    for(int k=0;k<3;k++) out[k]=(float)(h[k][0]*in[0] +h[k][1]*in[1] +h[k][2]*in[2]);
    return out;
  }

  int box_t=-1;
  int rotcent_t=-1;
  void makeBox(){
    float color[] = { 0.0f, 1.0f, 1.0f, 0.9f };
    rotcent_t = gl.glGenLists(1);
    gl.glNewList( rotcent_t, GL2.GL_COMPILE );
    gl.glMaterialfv( GL2.GL_FRONT_AND_BACK,GL2.GL_DIFFUSE,color, 0 );
    glut.glutSolidSphere( h[0][0]/4., 30, 30 );
    gl.glEndList();

    double tp[] = new double[3];
    double[] p;

    box_t = gl.glGenLists(1);
    gl.glNewList( box_t, GL2.GL_COMPILE );

    gl.glLineWidth( ctrl.vconf.boxLineWidth );
    gl.glColor4fv( ctrl.vconf.boxColor, 0);
    gl.glDisable( GL2.GL_LIGHTING );

    gl.glBegin( GL2.GL_LINE_LOOP );
    tp[0] = 0.f;
    tp[1] = 0.f;
    tp[2] = 0.f;
    p = chgScale( tp );
    gl.glVertex3dv( p, 0 );
    tp[0] = 1f;
    tp[1] = 0f;
    tp[2] = 0.f;
    p = chgScale( tp );
    gl.glVertex3dv( p, 0 );
    tp[0] = 1.f;
    tp[1] = 1.f;
    tp[2] = 0.f;
    p = chgScale( tp );
    gl.glVertex3dv( p, 0 );
    tp[0] = 0.f;
    tp[1] = 1.f;
    tp[2] = 0.f;
    p = chgScale( tp );
    gl.glVertex3dv( p, 0 );
    gl.glEnd();

    gl.glBegin( GL2.GL_LINE_LOOP );
    tp[0] = 0.f;
    tp[1] = 0.f;
    tp[2] = 1.f;
    p = chgScale( tp );
    gl.glVertex3dv( p, 0 );
    tp[0] = 1.f;
    tp[1] = 0.f;
    tp[2] = 1.f;
    p = chgScale( tp );
    gl.glVertex3dv( p, 0 );
    tp[0] = 1.f;
    tp[1] = 1.f;
    tp[2] = 1.f;
    p = chgScale( tp );
    gl.glVertex3dv( p, 0 );
    tp[0] = 0.f;
    tp[1] = 1.f;
    tp[2] = 1.f;
    p = chgScale( tp );
    gl.glVertex3dv( p, 0 );
    gl.glEnd();

    gl.glBegin( GL2.GL_LINES );
    tp[0] = 0.f;
    tp[1] = 0.f;
    tp[2] = 0.f;
    p = chgScale( tp );
    gl.glVertex3dv( p, 0 );
    tp[0] = 0.f;
    tp[1] = 0.f;
    tp[2] = 1.f;
    p = chgScale( tp );
    gl.glVertex3dv( p, 0 );
    tp[0] = 1.f;
    tp[1] = 0.f;
    tp[2] = 0.f;
    p = chgScale( tp );
    gl.glVertex3dv( p, 0 );
    tp[0] = 1.f;
    tp[1] = 0.f;
    tp[2] = 1.f;
    p = chgScale( tp );
    gl.glVertex3dv( p, 0 );
    tp[0] = 1.f;
    tp[1] = 1.f;
    tp[2] = 0.f;
    p = chgScale( tp );
    gl.glVertex3dv( p, 0 );
    tp[0] = 1.f;
    tp[1] = 1.f;
    tp[2] = 1.f;
    p = chgScale( tp );
    gl.glVertex3dv( p, 0 );
    tp[0] = 0.f;
    tp[1] = 1.f;
    tp[2] = 0.f;
    p = chgScale( tp );
    gl.glVertex3dv( p, 0 );
    tp[0] = 0.f;
    tp[1] = 1.f;
    tp[2] = 1.f;
    p = chgScale( tp );
    gl.glVertex3dv( p, 0 );
    gl.glEnd();

    gl.glEnable( GL2.GL_LIGHTING );
    gl.glEndList();
  }


  public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h){
    width=w;
    height=h;
    GL2 gl = drawable.getGL().getGL2();
    gl.glViewport(0, 0, w, h);
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    glu.gluPerspective(50.0, (double)w/(double)h, .5, 100.0);
    //gl.glOrtho(-h[0][0],h[0][0],-h[0][0],h[0][0],0.5,h[0][0]);

    gl.glMatrixMode(GL2.GL_MODELVIEW);
  }

  public void mouseEntered( MouseEvent me ) {
  }

  public void mouseExited( MouseEvent me ) {
  }

  boolean mouseLButton;
  boolean mouseMButton;
  boolean mouseRButton;
  boolean draging=false;
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
    draging=true;
  }
  public void mouseReleased( MouseEvent me ) {
    draging=false;
    repaint();
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
        //if(Math.abs(dx) > Math.abs(dy)) trans_x += dx*0.1f;
        //else trans_y += -dy*0.1f;
        trans_x += dx*0.01f;
        trans_y += -dy*0.01f;
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
    repaint();
  }
  public void repaint(){
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
      dispose();
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
    case KeyEvent.VK_N:
      MDstep(100);
      break;
    case KeyEvent.VK_Z:
      if( ke.isShiftDown() ){
        scale-=0.5;
      }else{
        scale+=0.5;
      }
      break;

    }
    repaint();
  }
  public void keyReleased( KeyEvent ke ) {
  }
  public void keyTyped( KeyEvent ke ) {
  }

  //-- MD --------------------------------
  int natm;
  double[][] h=new double[3][3];
  double[][] hinv=new double[3][3];
  double[][] r,v,f;
  byte[] tag;

  double eps=120;
  double sgm=3.41;
  double mass=40;
  double dt=40;
  double dt2=dt*0.5;
  double dmp=1.;
  //boltzman fac
  double fkb= 1.3806503e-23*(2.41889e-17*2.41889e-17)/9.1093897e-31/(0.5291772e-10*0.5291772e-10);

  public void set(Atoms atoms){
    for(int i=0;i<3;i++)for(int j=0;j<3;j++){
        h[i][j]=atoms.h[i][j];
        hinv[i][j]=atoms.hinv[i][j];
      }
    natm=atoms.n;

    r=new double[natm][3];
    v=new double[natm][3];
    f=new double[natm][3];
    tag=new byte[natm];
    for(int i=0;i<natm;i++){
      //stop volume data
      if(atoms.tag[i]==Const.VOLUME_DATA_TAG){
        natm=i-1;
        break;
      }
      tag[i]=atoms.tag[i];
      for(int j=0;j<3;j++){
        r[i][j]=atoms.hinv[j][0]*atoms.r[i][0]+
          atoms.hinv[j][1]*atoms.r[i][1]+
          atoms.hinv[j][2]*atoms.r[i][2];
        //
        v[i][j]=0;
        f[i][j]=0;
      }
    }

    setupPot();
    setHome();
    makeList();
  }


  ArrayList<ArrayList<Integer>> lspr= new ArrayList<ArrayList<Integer>>();
  void makeList(){
    lspr.clear();

    double[] dsr=new double[3];
    double[] dr=new double[3];
    for(int i=0;i<natm;i++){
      ArrayList<Integer> iList = new ArrayList<Integer>();
      for(int j=0;j<natm;j++){
        if(i==j)continue;
        for(int k=0;k<3;k++){
          dsr[k]=r[j][k]-r[i][k];
          if(dsr[k]>0.5)dsr[k]-=1.0;
          if(dsr[k]<-0.5)dsr[k]+=1.0;
        }
        dr=chgScale( dsr );
        double r2=dr[0]*dr[0]+dr[1]*dr[1]+dr[2]*dr[2];
        double rij=Math.sqrt( r2 );
        if(rij>rc)continue;
        iList.add(j);
      }//j
      lspr.add(iList);
    }
  }

  double sgm6,rc,massi,rpot;
  int ntable=1000;
  double[][] pottable=new double[2][ntable];
  void setupPot(){
    sgm/=0.529177;//convert to a.u.
    eps*=fkb;
    mass*=1840;
    dt2=dt/2;
    massi=1/mass;
    rc=2.5*sgm;
    sgm6=sgm*sgm*sgm*sgm*sgm*sgm;

    //cutoff
    double ri=1/rc;
    double ri6=ri*ri*ri*ri*ri*ri;
    double r=sgm6*ri6;
    double vrc=4*eps*(r-1)*r;
    double dvrc=-24*eps*(2*r-1)*r*ri;

    //pot table
    rpot=rc/ntable;
    for(int i=0;i<ntable;i++){
      ri=1.0/rpot/(i+1);
      ri6=ri*ri*ri*ri*ri*ri;
      r=sgm6*ri6;
      pottable[0][i]=4*eps*(r-1)*r-vrc-dvrc*(r-rc);
      pottable[1][i]=-24*eps*(2*r-1)*r*ri-dvrc;
    }

  }

  double epot;
  double epot0=-123.;
  public void getForce(){
    //init
    epot=0;
    for(int i=0;i<natm;i++){
      f[i][0]=0.;
      f[i][1]=0.;
      f[i][2]=0.;
    }

    int ir;
    double fr,dv;
    double[] dsr=new double[3];
    double[] dr=new double[3];
    //cal force
    for(int i=0;i<natm-1;i++){
      ArrayList<Integer> iList = lspr.get(i);
      for(int jj=0;jj<iList.size();jj++){
        int j=iList.get(jj);
        if(j>i)continue;
        for(int k=0;k<3;k++){
          dsr[k]=r[j][k]-r[i][k];
          if(dsr[k]>0.5)dsr[k]-=1.0;
          if(dsr[k]<-0.5)dsr[k]+=1.0;
        }

        dr=chgScale( dsr );
        double r2=dr[0]*dr[0]+dr[1]*dr[1]+dr[2]*dr[2];
        double rij=Math.sqrt( r2 );
        if(rij>rc)continue;
        ir=(int)(rij/rpot)-1;
        fr=rij/rpot-ir;
        if(ir>ntable)ir=ntable-2;
        epot+=fr*pottable[0][ir]+(1-fr)*pottable[0][ir+1];
        dv=fr*pottable[1][ir]+(1-fr)*pottable[1][ir+1];
        dv/=rij;
        f[i][0]+=dv*dr[0];
        f[i][1]+=dv*dr[1];
        f[i][2]+=dv*dr[2];
        f[j][0]-=dv*dr[0];
        f[j][1]-=dv*dr[1];
        f[j][2]-=dv*dr[2];
      }
    }

  }

  private double calEkin(){
    double ekin=0.;
    for(int i=0;i<natm;i++)
      for(int j=0;j<3;j++){
        ekin+=v[i][j]*v[i][j];
      }
    return ekin*mass*0.5;
  }
  public void MDstep(int nloop){
    getForce();
    if(epot0==-123.)epot0=epot;
    for(int iloop=0;iloop<nloop;iloop++){
      //kick
      for(int i=0;i<natm;i++)
        for(int j=0;j<3;j++){
          v[i][j]+=f[i][j]*dt2*massi;
          v[i][j]*=dmp;
        }

      //update
      for(int i=0;i<natm;i++)for(int j=0;j<3;j++) r[i][j]+=(hinv[j][0]*v[i][0]+hinv[j][1]*v[i][1]+hinv[j][2]*v[i][2])*dt;
      //get force
      getForce();
      //kick
      for(int i=0;i<natm;i++)for(int j=0;j<3;j++) v[i][j]+=f[i][j]*dt2*massi;
    }
    double ekin=calEkin();
    System.out.println(String.format("ekin, epot, etot= %12.4e, %12.4f, %12.4f",ekin,epot-epot0,ekin+epot-epot0));
  }


  public void atomshow(){
    double ri[]=new double[3];
    double rii[]=new double[3];
    for(int i=0;i<natm;i++){

      gl.glMaterialfv( GL2.GL_FRONT,GL2.GL_DIFFUSE, ctrl.vconf.tagColor[tag[i]-1], 0 );
      gl.glPushMatrix();
      for(int j=0;j<3;j++){
        ri[j]=r[i][j];
        if(ri[j]>1.)ri[j]-=1.0;
        if(ri[j]<0.)ri[j]+=1.0;
      }

      rii=chgScale( ri );
      gl.glTranslated(rii[0],rii[1],rii[2]);
      glut.glutSolidSphere( ctrl.vconf.tagRadius[tag[i]-1],
                            ctrl.vconf.tagSlice[tag[i]-1],
                            ctrl.vconf.tagStack[tag[i]-1]);
      gl.glPopMatrix();
    }


  }
  private void showStatus(){
    gl.glDisable( GL2.GL_LIGHTING );
    gl.glDisable( GL2.GL_DEPTH_TEST );

    gl.glPushMatrix();
    gl.glMatrixMode( GL2.GL_PROJECTION );
    gl.glPushMatrix();

    gl.glLoadIdentity();
    glu.gluOrtho2D( 0, width, 0, height );
    gl.glMatrixMode( GL2.GL_MODELVIEW );
    gl.glLoadIdentity();
    gl.glColor3fv( ctrl.vconf.txtColor, 0  );

    String str;
    str=String.format("sgm= %.3f Å",sgm*0.529177);
    renderString(str,15.f, height-20,0.f,1.f,ctrl.vconf.txtColor);
    str=String.format("eps= %.1f K",eps/fkb);
    renderString(str,15.f, height-40,0.f,1.f,ctrl.vconf.txtColor);
    str=String.format("dmp= %.3f",dmp);
    renderString(str,15.f, height-60,0.f,1.f,ctrl.vconf.txtColor);

    gl.glMatrixMode( GL2.GL_PROJECTION );
    gl.glPopMatrix();
    gl.glMatrixMode( GL2.GL_MODELVIEW );
    gl.glPopMatrix();

    gl.glEnable( GL2.GL_DEPTH_TEST );
    gl.glEnable( GL2.GL_LIGHTING );
  }

  void renderString(String str,float x,float y,float z,float scale,float[] color){
    txtRenderer.begin3DRendering();
    txtRenderer.setColor(color[0],color[1],color[2],color[3]);
    txtRenderer.draw3D(str, x,y,z,scale);
    txtRenderer.end3DRendering();
  }


  double eye[]={1,1,1};
  double drc[]={1,1,-1};
  double up[]={0.,1.,0.};
  public void setHome(){
    eye[0]=eye[1]=0.;
    eye[2]=h[2][2]*2;
    drc[0]=drc[1]=0.;
    drc[2]=-h[2][2];

    up[0]=0; up[1]=1; up[2]=0;
    scale=1;
    angle_x=0; angle_y=0;
    trans_x=0; trans_y=0;
  }


  public void setFCC(){
    double cunit=10.0399231105058;
    double[][] fcc={{0.0, 0.0, 0.0 },{0.5, 0.5, 0.0 },
                    {0.5, 0.0, 0.5 },{0.0, 0.5, 0.5 }};

    int Nx=4;
    int Ny=4;
    int Nz=4;
    h[0][0]=cunit*Nx;
    h[1][0]=0;
    h[2][0]=0;
    h[0][2]=0;
    h[1][1]=cunit*Ny;
    h[2][1]=0;
    h[0][2]=0;
    h[1][2]=0;
    h[2][2]=cunit*Nz;
    //init x
    int inc=0;
    for(int i=0;i<Nx;i++){
      for(int j=0;j<Ny;j++){
        for(int k=0;k<Nz;k++){
          for(int l=0;l<4;l++){
            r[inc][0]=(fcc[l][0]+i);
            r[inc][1]=(fcc[l][1]+j);
            r[inc][2]=(fcc[l][2]+k);
            inc++;
          }
        }
      }
    }
  }

}//MDFrame

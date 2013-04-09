package viewer;

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


import tools.*;
import data.*;
import viewer.*;
import viewer.viewConfigPanel.*;
import viewer.renderer.*;
import viewer.LF.*;


public class RenderingWindow extends JFrame implements GLEventListener,
                                                       ActionListener,
                                                       MouseListener,
                                                       MouseMotionListener,
                                                       MouseWheelListener{

  //rendering window properties
  private int myID;
  private String filePath;
  private String fileDirectory;


  public boolean isConstuctorFinished=false;
  //Controller variables
  public int currentFrame;

  boolean tmpSelect=false;
  boolean isKeyZoom=true;
  boolean primitiveObjectMakeFlag=true;
  boolean remakeFlag=true;

  boolean clearTmpBond=false;//for dynamic bond creation

  // variables related not only to one frame, but also to all frames
  public String convDate;
  public int totalFrame;
  public float startTime;
  public float timeInterval;
  public byte numData;
  //public float hMinLength;
  public float minHmatLength;
  //public float[][] originalDataRange = new float[Atom.MAX_NUM_DATA][2];
  public float[][] orgDataRange = new float[Atom.MAX_NUM_DATA][2];
  //public int maxNatom=0;
  public int maxNumAtoms;
  //public float[][] hMax = new float[3][3];
  public float[][] maxHmat = new float[3][3];
  public byte maxNumTag; 
  public byte[] tags;
  public boolean[] existBonds;

  public boolean visibleAtoms=true;
  boolean tmpVisibleAtoms=true;//for acceleration
  static final public int renderingAtomTypeMAX=3;//point or sphere or sphere(toon)
  public int renderingAtomType=0;
  static final public int renderingAtomDataIndexMAX=Atom.MAX_NUM_DATA; //tag + data coloring
  public int renderingAtomDataIndex=0;//this means data index

  //private boolean isAtomSelecting=false;
  public int pickedAtomID=-1;

  public boolean visibleBonds=false;
  static final public int renderingBondTypeMAX=2;//line, cylinder
  public int renderingBondType=0;
  static final public int renderingBondColorTypeMAX=3;// depends on atom, length, coordination num.
  public int renderingBondColorType=0;

  public boolean visibleVectors=false;
  static final public int renderingVectorTypeMAX=2;//line, vector
  public int renderingVectorType=0;
  static final public int renderingVectorColorTypeMAX=2;//atom color, length
  public int renderingVectorColorType=0;

  private boolean visibleVolume=false;
  static final public int renderingVolumeTypeMAX=2;//volumedata only, volume and atom
  public int renderingVolumeType=0;
  static final public int renderingVolumeDataIndexMAX=renderingAtomDataIndexMAX;
  public int renderingVolumeDataIndex=1;//this means data index

  public boolean visibleAxis=true;
  public boolean visibleTime=true;
  public boolean visibleBox=true;
  public boolean visibleLabel=false;
  static final public int atomLabelTypeMAX=5;
  public int atomLabelType=2;
  public boolean visibleLegend=true;

  public boolean isTrackBallMode=false;
  public boolean imageSave=false;
  public boolean sequentialImageSave=false;

  //Use classes
  public GLAutoDrawable drawable;
  public GL2   gl;
  public GLU  glu;
  public GLUT glut;
  public GLCanvas glCanvas;

  public AkiraFileIO fileio;
  public Atoms atoms;
  public AtomRenderer atmRndr;
  public Viewpoint vp;
  public Axis axis;
  public Annotation annotation;
  public viewer.renderer.Box box;
  public Light light;
  public RotationCenter rotcent;
  public ViewConfig vconf;
  public Plane plane;
  public AtomLabel atomlabel;
  public AtomSelector selector;
  public BondRenderer bndRndr;
  public Vectors vec;
  public Volume volume;
  public SelectorQueue sq;
  public Snapshot snapshot;
  private TrackBall trackB;
  public Controller ctrl;

  //Animator
  public FPSAnimator animator;
  public int fps=2;
  public int fpsMin=1;
  public int fpsMax=10;

  //for mouse
  private int mouseX,mouseY;
  private int prevMouseX;
  private int prevMouseY;
  private int pressedMouseX;
  private int pressedMouseY;
  private boolean mouseLButton;
  private boolean mouseMButton;
  private boolean mouseRButton;
  private boolean mouseFPSAnimating;

  //Toon rendering
  private Toon toon;

  //statusbar
  public JLabel lStatus;
  private JSpinner spFrame;
  private JButton frameButton;

  public void actionPerformed(ActionEvent ae){
    if(ae.getSource() == frameButton){
      moveFrame( ((Integer)spFrame.getValue()).intValue()-1 );
    }

    remakeFlag=true;
    this.repaint();
  }

  /* constructor */
  public RenderingWindow(int id,String filePath,Controller ctrl){

    setTitle(filePath);

    myID=id;
    this.filePath=filePath;
    this.fileDirectory= (new File(filePath)).getParent();
    System.out.println(String.format("RWin[%d] opened: %s",id,filePath));

    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;
    this.initialize(filePath);

    //set frame propeties
    setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

    //Toon rendering
    toon= new Toon(vconf);
  }

  /* accesser  block starts *******************************************/
  public void updateAnnotationFont(){
    annotation.updateFont();
  }

  ComboPanel combo;
  public void setCombo(ComboPanel combo){
    this.combo=combo;
  }

  public void setLight(){
    light.set(gl,glu,glut,vconf);
  }

  public void setVisible2Dplot(){
    volume.setVisible2Dplot();
  }

  public void updateStatusString(){
    spFrame.setValue(currentFrame+1);

    String str=String.format("Frame: %3d/%d, "
                             ,currentFrame+1
                             ,totalFrame);
    if(visibleAtoms){
      switch (renderingAtomType){
      case 0:
        str+="atom= point, ";
        break;
      case 1:
        str+="atom= solid sphere, ";
        break;
      case 2:
        str+="atom= cartoon, ";
        break;
      }
      if(renderingAtomDataIndex==0){
        str+="tag color, ";
      }else{
        str+=String.format("data %d color, ",renderingAtomDataIndex);
      }
    }else{
      str+="invisible atoms, ";
    }

    if(visibleBonds){
      switch(renderingBondType){
      case 0:
        str+="bond= line, ";
        break;
      case 1:
        str+="bond= cylinder, ";
        break;
      }
      switch(renderingBondColorType){
      case 0:
        str+="atoms's tag color, ";
        break;
      case 1:
        str+="length color, ";
        break;
      case 2:
        str+="coordination num color, ";
        break;
      }
    }

    if(visibleVectors){
      switch(renderingVectorType){
      case 0:
        str+="vector= line, ";
        break;
      case 1:
        str+="vector= cylinder, ";
        break;
      case 2:
        str+="vector= dolphine, ";
        break;
      case 3:
        str+="vector= shark, ";
        break;
      case 4:
        str+="vector= whale, ";
        break;
      default:
        str+="vector= unknown, ";
        break;
      }
      switch(renderingVectorColorType){
      case 0:
        str+="atoms's tag color, ";
        break;
      case 1:
        str+="length color, ";
        break;
      }
    }

    if(vconf.isVolXY || vconf.isVolXZ ||vconf.isVolYZ ||
       vconf.isVolContour ||vconf.isVolSurface || vconf.isVolSurface2){
      switch(renderingVolumeType){
      case 0:
        str+="volume data only, ";
        break;
      case 1:
        str+="volume data & atom's data, ";
        break;
      }
      str+=String.format("data type: %d, ",renderingVolumeDataIndex);
    }

    if(vconf.isSelectionInfo) str+="atom selection mode, ";
    if(ctrl!=null && vconf.isTrjMode) str+="trajectory on, ";
    if(sequentialImageSave) str+="Sequential Shoting ";

    if(isTrackBallMode) str+="TrackBall Mode, ";


    lStatus.setText(str);
  }
  public void keyZoom(int add){
    isKeyZoom=true;
    vp.setObjectScale( add );
    this.repaint();
  }
  public void selectByHuman(int id){
    pickedAtomID=id;
    tmpSelect=true;
    refresh();
  }
  public void setViewMode(){
    vp.setViewportMode();
    // Reset toon shading code
    toon.changeShaderProgram();
    if(vconf.viewMode == 0){
      System.out.println("view mode: perspective");
    }else{
      System.out.println("view mode: orthogonal");
    }

    this.repaint();
  }
  public void remakePrimitiveObjects(){
    primitiveObjectMakeFlag=true;
    this.repaint();
  }

  public void writeImage(){
    imageSave=true;
    remakeFlag=true;
    this.repaint();
  }
  public void writeSequentialImage(){
    sequentialImageSave=true;
    remakeFlag=true;
    this.repaint();
  }
  public void setVisibleAtoms(){
    visibleAtoms=!visibleAtoms;
    this.repaint();
  }
  public void setVisibleAtoms(boolean v){
    visibleAtoms=v;
    this.repaint();
  }
  public void setVisibleVectors(){
    visibleVectors=!visibleVectors;
    if(visibleVectors)remakeFlag=true;

    this.repaint();
  }
  public void setVisibleBonds(){
    visibleBonds=!visibleBonds;
    if(visibleBonds)remakeFlag=true;
    this.repaint();
  }
  public void setVisibleLegend(){
    visibleLegend=!visibleLegend;
    this.repaint();
  }
  public void setVisibleTime(){
    visibleTime=!visibleTime;

    this.repaint();
  }
  public void setVisibleBox(){
    visibleBox=!visibleBox;
    this.repaint();
  }
  public void setVisibleAxis(){
    visibleAxis=!visibleAxis;

    this.repaint();
  }
  public void setPickMode(){
    vconf.isSelectionInfo=!vconf.isSelectionInfo;
    this.repaint();
  }

  public void setAtomLabelVisible(){
    visibleLabel=!visibleLabel;
    this.repaint();
  }
  public void setAtomLabelType(){
    atomLabelType++;
    if(atomLabelType>=atomLabelTypeMAX)atomLabelType=0;
    this.repaint();
  }

  public boolean isAnimating(){
    if(animator==null)return false;
    return animator.isAnimating();
  }
  public void startstopAnimating(){
    if( isAnimating() ){
      animator.stop();
    }else{
      animator.start();
    }
    repaint();
  }

  public void decrementFrame(){
    moveFrame(currentFrame-1);
  }
  public void incrementFrame(){
    moveFrame(currentFrame+1);
  }
  public void incrementFrame(int d){
    moveFrame(currentFrame+d);
  }
  public void moveFrame(int next){
    if(currentFrame==next)return;

    int nextFrame=next;

    if( nextFrame<0 )nextFrame+= totalFrame;
    if( nextFrame>= totalFrame )
      nextFrame-= totalFrame;

    //set
    fileio.set( currentFrame,nextFrame, this );

    clearTmpBond=true;

    //increment frame
    currentFrame=nextFrame;
    atmRndr.nframe=currentFrame;
    remakeFlag=true;
    if(sequentialImageSave)this.repaint();
    if(!isAnimating())this.repaint();
  }

  public void setrenderingAtomDataIndex(int i){
    renderingAtomDataIndex=i;

    remakeFlag=true;
    this.repaint();
  }

  public void setAtomsRenderingType(){
    renderingAtomType++;
    if(renderingAtomType>=renderingAtomTypeMAX)renderingAtomType=0;
    remakeFlag=true;
    this.repaint();
  }
  public void setAtomsRenderingType(int n){
    if(n>=0&&n<renderingAtomTypeMAX)renderingAtomType=n;
    remakeFlag=true;
    this.repaint();
  }

  public void setBondsRenderingType(){
    renderingBondType++;
    if(renderingBondType>=renderingBondTypeMAX)renderingBondType=0;

    remakeFlag=true;
    this.repaint();
  }
  public void setVectorRenderingType(){
    renderingVectorType++;
    if(renderingVectorType>=renderingVectorTypeMAX)renderingVectorType=0;

    remakeFlag=true;
    this.repaint();
  }
  public void saveViewPoint(){
    vp.saveViewPoint();
    System.out.println("save view point");
    this.repaint();
  }
  public void setVPHome(){
    System.out.println("set home");
    vp.setHome();
    axis.setHome();
    this.repaint();
  }
  char resetRotType='z';
  public void resetRotation(char ixyz){
    //vp.resetRotation(ixyz);
    vp.setHome();
    axis.resetRotation(ixyz);
    resetRotType=ixyz;
    this.repaint();
  }
  public void setVPSavedHome(){
    vp.revertViewPoint(0);
    System.out.println("move to saved point");
    this.repaint();
  }
  public void setVPSavedHome(int ith){
    vp.revertViewPoint(ith);
    this.repaint();
  }
  public int getID(){
    return myID;
  }

  public int getVisibleNAtoms(){
    return atmRndr.visibleNatoms;
  }
  public int getNAtoms(){
    return atoms.getNumAtoms();
  }
  public byte[] getTags(){
    return tags;
  }

  public Atoms getAtoms(){
    return atoms;
  }
  public float[][] getH(){
    return atoms.hmat;
  }
  public float[][] getDataRange(){
    return orgDataRange;
  }
  public String getFilePath(){
    return filePath;
  }
  public String getFileDirectory(){
    return fileDirectory;
  }
  public void setObjectCenter(float dx, float dy, float dz){
    vp.objCenter[0] = dx;
    vp.objCenter[1] = dy;
    vp.objCenter[2] = dz;
    this.repaint();
  }
  public void addCenter(float dx, float dy, float dz){
    vp.center[0]+=dx;
    vp.center[1]+=dy;
    vp.center[2]+=dz;
    this.repaint();
  }
  public void addEye(float dx, float dy, float dz){
    vp.eye[0]+=dx;
    vp.eye[1]+=dy;
    vp.eye[2]+=dz;
    this.repaint();
  }
  public void setObjectRotate(float dx, float dy, float dz){
    vp.setObjectRotate( dx, dy );
    vp.setObjectRotateZ( dz );
    this.repaint();
  }
  public void refresh(){
    //remake and repaint
    remakeFlag=true;
    this.repaint();
  }
  //wrapper of repaint
  public void repaint(){
    glCanvas.repaint();
    ctrl.updateStatusString();
  }
  /* accesser  block ends *********************************************/

  /* called by this */
  void initialize(String filepath){
    //menu
    //RenderingWindowMenuController menu=new RenderingWindowMenuController(this);
    //setJMenuBar(menu.getMenu());

    //long start,end;//debug
    //glCanvas = new GLJPanel();
    glCanvas = new GLCanvas();
    glCanvas.addGLEventListener( this );
    add(glCanvas);


    //monitor = new JavaSysMon();

    fileio=new AkiraFileIO(filepath);
    atoms= new Atoms();
    fileio.ropen();
    fileio.readHeader( this );
    fileio.readFooter( this );
    currentFrame=0;
    fileio.set( currentFrame,currentFrame, this );
    //ctrl.setFrameNm(currentFrame,atoms.totalFrame,false);

    atmRndr= new viewer.renderer.AtomRenderer(this);
    atmRndr.allocate4Trj();

    bndRndr= new viewer.renderer.BondRenderer(this);

    //add status bar
    JStatusBar statusBar = new JStatusBar();
    lStatus = new JLabel();
    ctrl.updateStatusString();
    spFrame = new JSpinner(new SpinnerNumberModel(currentFrame+1,
                                                  1,
                                                  totalFrame,1));
    spFrame.setFocusable(false);
    spFrame.setPreferredSize(new Dimension(30, 25));
    //spFrame.addActionListener(this);
    frameButton=new JButton("Go");
    frameButton.setFocusable(false);
    frameButton.addActionListener(this);


    statusBar.set3(lStatus,spFrame,frameButton);
    //statusBar.setMainLeftComponent(lStatus);

    add(statusBar, BorderLayout.SOUTH);

  }

  /* called by OpenGL (after initialize()) */
  public void init( GLAutoDrawable drawable ){
    this.drawable = drawable;
    gl   = drawable.getGL().getGL2();
    glu  = new GLU();
    glut = new GLUT();


    //atoms.setGL(gl,glu,glut);
    atmRndr.makePrimitiveObjects();

    //set animator
    animator = newFPSAnimator();

    GLContext glcontext = drawable.getContext();
    glcontext.setSynchronized(true);

    if( drawable instanceof AWTGLAutoDrawable ){
      AWTGLAutoDrawable awtDrawable = (AWTGLAutoDrawable) drawable;
      awtDrawable.addMouseListener( this );
      awtDrawable.addMouseMotionListener( this );
      awtDrawable.addMouseWheelListener( this );
      awtDrawable.setAutoSwapBufferMode(true);
    }

    gl.glShadeModel(GL2.GL_SMOOTH);
    //gl.glShadeModel(GL2.GL_FLAT);

    gl.glEnable(GL2.GL_DEPTH_TEST);
    gl.glEnable(GL2.GL_NORMALIZE);

    gl.glEnable(GL2.GL_BLEND);//for alpha blending
    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

    //anti alias
    //gl.glEnable(GL2.GL_POINT_SMOOTH);
    gl.glEnable(GL2.GL_LINE_SMOOTH);
    //gl.glEnable(GL2.GL_POLYGON_SMOOTH);

    //gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT,GL2.GL_DONT_CARE);
    //gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT,GL2.GL_NICEST);
    //gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT,GL2.GL_FASTEST);

    //gl.glEnable(GL2.GL_COLOR_MATERIAL);
    //gl.glEnable(GL2.GL_CULL_FACE);

    //Toon rendering
    toon.setGL( drawable, gl, glu, glut );
    toon.init();

    vec=new Vectors(this);

    light =new Light();
    light.set(gl,glu,glut,vconf);


    annotation= new Annotation(this);


    //other rendering class
    box = new viewer.renderer.Box(this);
    box.make();
    if(visibleBox)box.show();

    //
    axis = new Axis(this);
    axis.make();
    //axis.setParentSize( pane.getWidth(), pane.getHeight() );
    axis.setParentSize( this.getWidth(), this.getHeight() );

    //
    vp= new Viewpoint(this);
    vp.setAxis(axis);
    //
    rotcent=new RotationCenter(this);
    //
    plane = new Plane(ctrl,this);
    //
    atomlabel=new AtomLabel(this);
    //
    snapshot =new Snapshot(getContentPane(),fileDirectory,vconf);
    //
    selector = new AtomSelector();

    sq=new SelectorQueue(this);

    volume =new Volume(this);


    trackB= new TrackBall();
    trackB.trackball( vp.curquat, 0.0f, 0.0f, 0.0f, 0.0f );
    trackB.trackball( vp.lastquat, 0.0f, 0.0f, 0.0f, 0.0f );

    //bg color
    gl.glClearColor(vconf.bgColor[0],vconf.bgColor[1],
                    vconf.bgColor[2],vconf.bgColor[3]);


    ctrl.updateStatusString();
    isConstuctorFinished=true;
  }

  /* called by OpenGL */
  int thisWidth,thisHeight;
  public void reshape(GLAutoDrawable drawable,int x, int y,int width, int height){
    thisWidth=width;
    thisHeight=height;
    setTitle(String.format("%dx%d: ",width,height)+filePath);

    vp.viewport( x, y, width, height );
    axis.setParentSize( width, height );
    annotation.setParentSize( width,height );
  }
  /* called by OpenGL */
  public void display( GLAutoDrawable drawable ){
    if( (drawable instanceof GLJPanel) &&
        !((GLJPanel) drawable).isOpaque() &&
        ((GLJPanel) drawable).shouldPreserveColorBufferIfTranslucent() ){
      gl.glClear( GL2.GL_DEPTH_BUFFER_BIT );
    } else {
      gl.glClear( GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );
    }

    if(vconf.isVolXY || vconf.isVolXZ ||vconf.isVolYZ ||
       vconf.isVolContour ||vconf.isVolSurface || vconf.isVolSurface2){
      visibleVolume=true;
    }else{
      visibleVolume=false;
    }

    //bg color
    gl.glClearColor(vconf.bgColor[0],vconf.bgColor[1],
                    vconf.bgColor[2],vconf.bgColor[3]);

    //view point operation starts
    gl.glMatrixMode( GL2.GL_MODELVIEW );
    gl.glLoadIdentity();

    if( vconf.isLightPosAuto ) light.set(gl,glu,glut,vconf);

    vp.lookAt();

    gl.glPushMatrix();

    //reset roation
    if(resetRotType=='x'){
      gl.glMultMatrixf( vp.xMVM, 0 );
      resetRotType='z';
    }else if(resetRotType=='y'){
      gl.glMultMatrixf( vp.yMVM, 0 );
      resetRotType='z';
    }

    //translate
    gl.glTranslatef( vp.objCenter[0],vp.objCenter[1],vp.objCenter[2] );

    //rotate
    if(isTrackBallMode){
      //trackBall
      vp.curquat = trackB.add_quats( vp.lastquat, vp.curquat );
      trackB.build_rotmatrix( vp.m, vp.curquat );
      for( int k=0; k<3; k++ ){
        vp.curquat[k] = 0.0f;
        vp.lastquat[k] = 0.0f;
      }
      vp.curquat[3] = 1.0f;
      vp.lastquat[3] = 1.0f;
      gl.glMultMatrixf( vp.m, 0 );
    }else{
      //normal
      gl.glRotatef( vp.rotX, 1.0f,  0.0f, 0.0f );
      gl.glRotatef( vp.rotY, 0.0f, -1.0f, 0.0f );
      gl.glRotatef( vp.rotZ, 0.0f,  0.0f, 1.0f );
    }
    //scale
    if(isKeyZoom){
      gl.glScalef( vp.objScale, vp.objScale, vp.objScale );
    }else{
      float[] t=window2world((float)mouseX,(float)mouseY,1.0f);
      gl.glTranslatef(t[0],t[1],0.0f);
      gl.glScalef( vp.objScale, vp.objScale, vp.objScale );
      gl.glTranslatef(-t[0],-t[1],0.0f);
    }
    isKeyZoom=true;

    gl.glMultMatrixf( vp.mvm, 0 );

    if( !vconf.isLightPosAuto ) light.set(gl,glu,glut,vconf);

    gl.glGetFloatv( GL2.GL_MODELVIEW_MATRIX, vp.mvm, 0 );

    vp.mvm[12] += vp.eye[0];
    vp.mvm[13] += vp.eye[1];
    vp.mvm[14] += vp.eye[2];

    //view point operation ends

    //pick

    if(//isAtomSelecting&&
       (vconf.isSelectionInfo ||vconf.isSelectionLength  ||
        vconf.isSelectionAngle  ||vconf.isSelectionTorsion  ||
        tmpSelect)){


      if(tmpSelect){
        //System.out.println(String.format("id=%d",pickedAtomID));
      }else{
        tmpSelect=false;
        pickedAtomID = selector.getID(gl,glu,glut,
                                      atmRndr,vp,
                                      pressedMouseX,
                                      pressedMouseY );
      }

      if(pickedAtomID>=0){

        ctrl.vcWin.focusOnStatus();
        System.out.println(String.format("picked id: %d",pickedAtomID+1));
        atmRndr.makePickedAtom(pickedAtomID);

        //trajectory
        ctrl.setPickedID4Trj(pickedAtomID);

        // Write information of picked atom
        Atom ai= atoms.getAtom(pickedAtomID);
        float[] out = new float[3];
        for(int k=0; k<3; k++)
          out[k] =
            atoms.hmati[k][0] *ai.pos[0]+
            atoms.hmati[k][1] *ai.pos[1]+
            atoms.hmati[k][2] *ai.pos[2];

        System.out.println(String.format(" pos=(%12.4e,%12.4e,%12.4e) =(%12.4e,%12.4e,%12.4e)"
                                         ,ai.pos[0]
                                         ,ai.pos[1]
                                         ,ai.pos[2]
                                         ,out[0],out[1],out[2]));
        if(renderingAtomDataIndex>0){
          System.out.println(String.format(" data[%1d]=%12.4e"
                                           ,renderingAtomDataIndex
                                           ,ai.auxData[renderingAtomDataIndex-1]));
        }
        sq.add(pickedAtomID, ai.pos);
        sq.make();
        if(vconf.isSelectionLength)System.out.println(sq.showLength());
        if(vconf.isSelectionAngle)System.out.println(sq.showAngle());
        if(vconf.isSelectionTorsion)System.out.println(sq.showTorsion());

        //lines
        if(vconf.isSelectionInfo ||vconf.isSelectionLength  ||
           vconf.isSelectionAngle  ||vconf.isSelectionTorsion) sq.show();
        
      }
    }
    //isAtomSelecting=false;

    //------draw objects
    if( isAnimating() ) incrementFrame();

    //region selection
    if( sq.isBandClose ){
      calRegionNP();
    }

    //make
    box.make();
    if( primitiveObjectMakeFlag ){
      atmRndr.makePrimitiveObjects();
      primitiveObjectMakeFlag=false;
    }
    if(remakeFlag){
      atmRndr.make();
      if(visibleBonds){
        if(fileio.existBonds){
          bndRndr.make();
        }else{
          if(clearTmpBond){
            bndRndr.clearList();
            clearTmpBond=false;
          }else{
            bndRndr.make();
          }
        }
      }
      if(pickedAtomID>=0 && (vconf.isSelectionInfo ||
                             vconf.isSelectionLength  ||
                             vconf.isSelectionAngle  ||
                             vconf.isSelectionTorsion
                             ))
        atmRndr.makePickedAtom(pickedAtomID);

      if(visibleVectors)vec.make();
      plane.make(pickedAtomID);
      volume.make();
      remakeFlag=false;
    }//end of remakeFlag

    if(vconf.isTrjMode){
      if(currentFrame == 0) atmRndr.resetTrjDList();
      atmRndr.makeTrajectory(ctrl.getTrj());
    }


    //atoms
    if(visibleAtoms && tmpVisibleAtoms) {
      if( renderingAtomType==AtomRenderer.ATOM_TYPE_TOON ){ // Toon rendering
        toon.set();
        atmRndr.show();
        toon.unset();
      }else{
        atmRndr.show();
      }
    }
    //show volume render
    if(visibleVolume)volume.show();

    //show ring

    //picked atom
    if(vconf.isSelectionInfo ||vconf.isSelectionLength  || vconf.isSelectionAngle  ||
       vconf.isSelectionTorsion  ||tmpSelect
       )atmRndr.showPickedAtom();


    //trj
    if(vconf.isTrjMode)atmRndr.trajectoryShow();
    //bonds
    if(visibleBonds){
       // Toon rendering ?
      if( renderingBondType==BondRenderer.BOND_TYPE_CYLINDER
          && renderingAtomType==AtomRenderer.ATOM_TYPE_TOON ){
        toon.set();
        bndRndr.show();
        toon.unset();
      }else{
        bndRndr.show();
      }
    }
    //vector
    if(visibleVectors)vec.show();

    //label
    if(visibleLabel)atomlabel.show();
    //box
    if(visibleBox)box.show();

    //plane
    plane.show();

    gl.glPopMatrix();

    //show rotation center
    rotcent.show();


    //rendering most front layer
    gl.glClear( GL.GL_DEPTH_BUFFER_BIT );

    annotation.show();

    if(visibleAxis){
      if(isTrackBallMode)
        axis.show(vp.m);
      else
        axis.show(vp.rotX, vp.rotY, vp.rotZ );
    }




    //one shot
    if(imageSave){
      snapshot.writeImage();
      imageSave=false;
    }

    //sequential shot
    if(sequentialImageSave){
      snapshot.writeImageSequential();
      if(currentFrame == totalFrame-1){
        sequentialImageSave=false;
      }else{
        incrementFrame();
      }
    }



    vp.objScale = 1.0f;
    vp.objCenter[0] = 0.0f;
    vp.objCenter[1] = 0.0f;
    vp.objCenter[2] = 0.0f;

    vp.rotX = vp.rotY = vp.rotZ = 0.0f;


    combo.running(this);

    ctrl.updateStatusString();
    requestFocus();
  }

  //?
  public void displayChanged( GLAutoDrawable drawable,boolean modeChanged,
                              boolean deviceChanged ){
  }

  //dispose window
  public void dispose(GLAutoDrawable drawable){
    vconf.rectRWin=getBounds();
  }

  //rectangle
  void calRectangleNP(){
      ArrayList<Integer> winPos=new ArrayList<Integer>();
      winPos.add(sq.rect1[0]);
      winPos.add(sq.rect1[1]);
      winPos.add(sq.rect2[0]);
      winPos.add(sq.rect1[1]);
      winPos.add(sq.rect2[0]);
      winPos.add(sq.rect2[1]);
      winPos.add(sq.rect1[0]);
      winPos.add(sq.rect2[1]);
      winPos.add(sq.rect1[0]);//start point
      winPos.add(sq.rect1[1]);

      sq.cutRN.clear();
      sq.cutRP.clear();

      for(int i=0;i<winPos.size()/2-1;i++){

      float[] a=window2world((float)winPos.get(2*i),
                             (float)winPos.get(2*i+1),0.0f);
      float[] b=window2world((float)winPos.get(2*i+2),
                             (float)winPos.get(2*i+1+2),0.0f);
      float[] c=window2world((float)(winPos.get(2*i)+winPos.get(2*i+2))*0.5f,
                             (float)(winPos.get(2*i+1)+winPos.get(2*i+1+2))*0.5f,0.5f);

      //a-c
      float[] r1=new float[3];
      r1[0]=a[0]-c[0];
      r1[1]=a[1]-c[1];
      r1[2]=a[2]-c[2];
      //b-c
      float[] r2=new float[3];
      r2[0]=b[0]-c[0];
      r2[1]=b[1]-c[1];
      r2[2]=b[2]-c[2];
      //r1 x r2
      float[] n=new float[3];
      n[0]=r1[1]*r2[2]-r1[2]*r2[1];
      n[1]=r1[2]*r2[0]-r1[0]*r2[2];
      n[2]=r1[0]*r2[1]-r1[1]*r2[0];
      //add
      sq.cutRN.add(n[0]);
      sq.cutRN.add(n[1]);
      sq.cutRN.add(n[2]);
      sq.cutRP.add(a[0]);
      sq.cutRP.add(a[1]);
      sq.cutRP.add(a[2]);
      }
  }

  void calRegionNP(){
    if(sq.isBandClose==false)return;
    sq.cutN.clear();
    sq.cutP.clear();
    for(int i=0;i<sq.winPos.size()/2-1;i++){
      float[] a=window2world((float)sq.winPos.get(2*i),
                             (float)sq.winPos.get(2*i+1),0.0f);
      float[] b=window2world((float)sq.winPos.get(2*i+2),
                             (float)sq.winPos.get(2*i+1+2),0.0f);
      float[] c=window2world((float)(sq.winPos.get(2*i)+sq.winPos.get(2*i+2))*0.5f,
                             (float)(sq.winPos.get(2*i+1)+sq.winPos.get(2*i+1+2))*0.5f,0.5f);

      //a-c
      float[] r1=new float[3];
      r1[0]=a[0]-c[0];
      r1[1]=a[1]-c[1];
      r1[2]=a[2]-c[2];
      //b-c
      float[] r2=new float[3];
      r2[0]=b[0]-c[0];
      r2[1]=b[1]-c[1];
      r2[2]=b[2]-c[2];
      //r1 x r2
      float[] n=new float[3];
      n[0]=r1[1]*r2[2]-r1[2]*r2[1];
      n[1]=r1[2]*r2[0]-r1[0]*r2[2];
      n[2]=r1[0]*r2[1]-r1[1]*r2[0];
      //add
      sq.cutN.add(n[0]);
      sq.cutN.add(n[1]);
      sq.cutN.add(n[2]);
      sq.cutP.add(a[0]);
      sq.cutP.add(a[1]);
      sq.cutP.add(a[2]);
    }


  }//calRegionNP

  //my
  public float[] window2world(float wx, float wy, float wz){
    int viewport[] = new int[4];
    float projmatrix[] = new float[16];
    float mvmatrix[] = new float[16];
    gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
    gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
    gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);

    float[] wcoord=new float[4];
    float winx = wx;
    float winy = viewport[3] -wy -1; //note viewport[3] is height of window in pixels
    float winz = wz;

    boolean isGet=glu.gluUnProject(winx,winy,winz,
                                   mvmatrix,0,
                                   projmatrix,0,
                                   viewport,0,
                                   wcoord,0);
    if(isGet){
      return wcoord;
    }else{
      System.out.println("gluUnProj error in window2world");
      return null;
    }

  }

  public void mouseEntered( MouseEvent me ){
  }

  public void mouseExited( MouseEvent me ){
  }

  public void mousePressed( MouseEvent me ){
    pressedMouseX = me.getX();
    pressedMouseY = me.getY();
    prevMouseX=pressedMouseX;
    prevMouseY=pressedMouseY;

    mouseLButton = false;
    mouseMButton = false;
    mouseRButton = false;
    mouseFPSAnimating = false;

    if( (me.getModifiers() & me.BUTTON1_MASK) != 0 ){
      mouseLButton = true;
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    else if( (me.getModifiers() & me.BUTTON2_MASK) != 0 ){
      mouseMButton = true;
      setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }
    else if( (me.getModifiers() & me.BUTTON3_MASK) != 0 ){
      mouseRButton = true;
      setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }

    //rotcenter on
    rotcent.setVisible(true);

    //atoms off
    if( (renderingAtomType!=0 && atoms.getNumAtoms()>10000) ||
        (renderingAtomType==0 && atoms.getNumAtoms()>100000) )
      tmpVisibleAtoms=false;

  }//mousePressed

  public void mouseReleased( MouseEvent me ){
    if( (me.getModifiers() & me.BUTTON1_MASK) != 0 ){
      mouseLButton = false;
    }
    else if( (me.getModifiers() & me.BUTTON2_MASK) != 0 ){
      mouseMButton = false;
    }
    else if( (me.getModifiers() & me.BUTTON3_MASK) != 0 ){
      mouseRButton = false;
    }

    //rotcenter off
    rotcent.setVisible(false);
    //atoms on
    tmpVisibleAtoms=true;
    //default cursor
    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

    this.repaint();
  }//mouseReleased

  public void mouseClicked( MouseEvent me ){
    //isAtomSelecting=true;
  }//mouseClicked


  public void mouseDragged( MouseEvent me ){

    int x = me.getX();
    int y = me.getY();
    Dimension size = me.getComponent().getSize();


    if(isTrackBallMode){
      trackB.trackball( vp.lastquat,
                        (2.0f*prevMouseX-size.width)/size.width,
                        (size.height-2.0f*prevMouseY)/size.height,
                        (2.0f*x-size.width)/size.width,
                        (size.height-2.0f*y)/size.height );
    }

    int dx =  prevMouseX - x;
    int dy = -prevMouseY + y;
    prevMouseX = x;
    prevMouseY = y;

    if( mouseLButton ){
        if((me.getModifiers() & InputEvent.SHIFT_MASK) !=0){
          //+SHIFT: trans
          if(vconf.isTransXOnly){
            vp.objCenter[0] = -dx*0.05f*minHmatLength;
          }else if(vconf.isTransYOnly){
            vp.objCenter[1] = -dy*0.05f*minHmatLength;
          }else{
            vp.objCenter[0] = -dx*0.05f*minHmatLength;
            vp.objCenter[1] = -dy*0.05f*minHmatLength;
          }
        }else if((me.getModifiers() & InputEvent.ALT_MASK) !=0){
          //+ALT: zoom
          if(Math.abs(dx) > Math.abs(dy)) dy = dx;
          vp.setEyePosition( dy/2 );
        }else if((me.getModifiers() & InputEvent.META_MASK) !=0){
          //+command: scale
          if(Math.abs(dx) > Math.abs(dy))vp.setObjectScale(dx*0.1f);
          else vp.setObjectScale(dy*0.1f);
        }else{
          //no modifier: rotate
          if(vconf.isRotationXOnly){
            vp.setObjectRotateMouse( (float)dx, 0.f);
          }else if(vconf.isRotationYOnly){
            vp.setObjectRotateMouse( 0.f, (float)dy );
          }else{
            vp.setObjectRotateMouse( (float)dx, (float)dy );
          }
        }


    }else if( mouseMButton ){
      /* middle */
      if( Math.abs(dx) > Math.abs(dy) ){ //clip
        dy = dx;
      }
      vp.setEyePosition( dy/2 );
    }else if( mouseRButton ){
      /* Right */
      if(vconf.isTransXOnly){
        vp.objCenter[0] = -dx*0.05f*minHmatLength;
      }else if(vconf.isTransYOnly){
        vp.objCenter[1] = -dy*0.05f*minHmatLength;
      }else{
        vp.objCenter[0] = -dx*0.05f*minHmatLength;
        vp.objCenter[1] = -dy*0.05f*minHmatLength;
      }

    }
    this.repaint();
  }//mouseDragged

  public void mouseMoved( MouseEvent me ){
    mouseX = me.getX();
    mouseY = me.getY();
  }//mouseMoved

  public void mouseWheelMoved( MouseWheelEvent mwe ){
    isKeyZoom=false;
    int units = mwe.getUnitsToScroll();
    if( units != 0 ){
      int add = units/Math.abs( units );
      vp.setObjectScale( add );
      vp.objCenter[2] = add/2;// improve!?
    }
    this.repaint();
  }

  //animator
  FPSAnimator newFPSAnimator(){
    return new FPSAnimator( drawable, fps );
  }
  public void incrementFPS(){
    fps++;
    if( fps > fpsMax ) fps = fpsMax;
    resetFPS();
  }
  public void decrementFPS(){
    fps--;
    if( fps < fpsMin) fps = fpsMin;
    resetFPS();
  }
  public void setFPS(int fps){
    this.fps=fps;
    resetFPS();
  }
  public void resetFPS(){
    //if(!isConstuctorFinished)return;
    if( isAnimating() ){
      animator.stop();
      animator = null;
      animator = newFPSAnimator();
      animator.start();
    }else{
      animator = null;
      animator = newFPSAnimator();
    }
  }

}

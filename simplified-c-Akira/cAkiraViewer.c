//last modified <2011-07-31 14:40:39 by NAKAMURA Takahide>
#define maxint(a,b) ({int _a = (a), _b = (b); _a > _b ? _a : _b; })

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <limits.h>
#include <float.h>

#ifdef __APPLE__
// OSX has OpenGL at /System/Library/Frameworks/GLUT.framework/Headers
#include <GLUT/glut.h>
#else
#include <GL/glut.h>
#endif

//prototype
void setup(char* file);
void read_files_Akira(char* file);
void reset( void );
void make_sphere( void );
void makeMDbox(void);

//window size
int width = 800, height = 600;
int ratio=5;


//mouse pos.
int prevX, prevY;
//mouse button flag
int mButton;


//eye pos.
float eye[]={0,0,10};
float center[]={0,0,-10};
float up[]={0,1,0};

//
float objCent[3];
float rotX,rotY;
float zoomScale=1.f;

int znear=1;
int zfar=1;

//display list
GLint atomsid;
GLint mdboxid;

// Max number of atoms in a file
#define NMAX 1000000
int natm;
float h[3][3];
int tag[NMAX];
float ra[NMAX][3];


//flag
int xSlice=0,ySlice=0,zSlice=0;
float slice[3][3];
int radius=2;


//accelerator
int Accel=1;
int isCTRLdown=0;



void setup(char* file){
  char cmd[128];
  read_files_Akira(file);

  //initialization
  glClearColor (0.0, 0.0, 0.0, 1.0);

  reset();
  glMatrixMode(GL_PROJECTION);
  glLoadIdentity();

  glOrtho(-width/ratio,width/ratio,-height/ratio,height/ratio,znear,zfar);


  glMatrixMode(GL_MODELVIEW);

  atomsid = glGenLists(1);
  make_sphere();

  mdboxid = glGenLists(1);
  makeMDbox();

  objCent[0]=-h[0][0]/2;
  objCent[1]=-h[1][1]/2;
  objCent[2]=-h[2][2];

  //usage
  printf("\nUSAGE\n");
  printf("+shift: acceleration(twice)\n");
  printf("mouse left: rotation\n");
  printf("ctrl+mouse left: translation\n");
  printf("mouse right: translation\n");
  printf("UP/DOWN: rotate Y\n");
  printf("LEFT/RIGHT: rotate X\n");
  printf("PAGE UP/DOWN: UP/DOWN\n");
  printf("HOME/END: LEFT/RIGHT\n");
  printf("x/y/z: view from x/y/z axis\n");
  printf("a/A: zoom in/out\n");
  printf("r: reset\n");
  printf("ESC: quit\n");
}

void reset( void ){
  //reset parameters
  rotY = 0.0;
  rotX = 0.0;
  zoomScale=1.;

  objCent[0]=-h[0][0]/2;
  objCent[1]=-h[1][1]/2;
  objCent[2]=-h[2][2]*2;

  eye[0]=0.;
  eye[1]=0.;
  eye[2]=h[2][2]*2;
  center[0]=eye[0];
  center[1]=eye[1];
  center[2]=-eye[2];


  znear=1;
  zfar=maxint(h[2][2]+eye[2],1000);

  glutPostRedisplay();
}

void display(void){
  //main display function, which is called OpenGL

  int i;

  glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  glMatrixMode(GL_MODELVIEW);

  glLoadIdentity();

  gluLookAt(eye[0],eye[1],eye[2],
            center[0],center[1],center[2],
            up[0],up[1],up[2]);


  glScalef(zoomScale,zoomScale,zoomScale);
  glTranslatef(objCent[0],objCent[1],objCent[2]);
  glRotatef( rotY, 0.0, 1.0, 0.0);
  glRotatef( rotX, 1.0, 0.0, 0.0);


  //draw
  glCallList (atomsid);
  glCallList (mdboxid);

  glutSwapBuffers();//repaint
}

void reshape (int w, int h){
  //this function is called from OpenGL, when window size changed

  width = w;   /* pass the window size to the globals */
  height = h;

  glViewport (0, 0, w, h);

  glMatrixMode(GL_PROJECTION);
  glLoadIdentity();
  glOrtho(-width/ratio,width/ratio,-height/ratio,height/ratio,znear,zfar);

  glMatrixMode(GL_MODELVIEW);
}

void setAcceleration(void){
  int modifier = glutGetModifiers();
  switch(modifier) {
  case GLUT_ACTIVE_SHIFT :
    Accel=2;
    break;
  case GLUT_ACTIVE_CTRL :
    isCTRLdown=1;
    break;
  default:
    isCTRLdown=0;
    Accel=1;
    break;
  }
}

void specialKeyPress( int key, int x, int y ){
  setAcceleration();

  float step=1.0;
  //key controll
  switch( key ) {
  case GLUT_KEY_LEFT:
    rotY -= step*Accel;
    break;
  case GLUT_KEY_RIGHT:
    rotY += step*Accel;
    break;
  case GLUT_KEY_UP:
    rotX -= step*Accel;
    break;
  case GLUT_KEY_DOWN:
    rotX += step*Accel;
    break;

  case GLUT_KEY_PAGE_UP:
    objCent[1] += height/20*Accel;
    break;
  case GLUT_KEY_PAGE_DOWN:
    objCent[1] -= height/20*Accel;
    break;
  case GLUT_KEY_HOME:
    objCent[0]-= width/20*Accel;
    break;
  case GLUT_KEY_END:
    objCent[0]+= width/20*Accel;
    break;
  }
  glutPostRedisplay();
}

void keyPress( unsigned char key, int x, int y ){
  setAcceleration();
  //key controll
  switch( key ) {
  case 'x':
    rotY=-90.;
    rotX=-90;
    objCent[0]=-h[0][0]/2;
    objCent[1]=-h[1][1]/2;
    objCent[2]=-h[2][2]*2;

    break;
  case 'y':
    rotY=-180.;
    rotX=-90;
    objCent[0]=h[0][0]/2;
    objCent[1]=-h[1][1]/2;
    objCent[2]=-h[2][2];

    break;
  case 'z':
    rotY = 0.0;
    rotX = 0.0;
    objCent[0]=-h[0][0]/2;
    objCent[1]=-h[1][1]/2;
    objCent[2]=-h[2][2]*2;
    break;
  case 'r':
    reset();
    break;
  case 'a': //zoom in
    zoomScale+=0.05;
    break;
  case 'A': //zoom out
    zoomScale-=0.05;
    break;

  case 27://ESC
    exit( 0 );
  }

  glutPostRedisplay();
  x = y = 0;
}

void mousePress( int button, int state, int x, int y ){
  setAcceleration();

  //mouse pushed
  if (state == GLUT_DOWN) {
    switch(button) {
    case GLUT_LEFT_BUTTON:
      mButton = button;
      break;
    case GLUT_MIDDLE_BUTTON:
      mButton = button;
      break;
    case GLUT_RIGHT_BUTTON:
      mButton = button;
      break;
    }
    prevX = x;
    prevY = y;
  }
}


void mouseDrag( int x, int y ){
  //mouse drag
  int dx, dy;

  dx = x - prevX;
  dy = y - prevY;

  switch (mButton) {
  case GLUT_LEFT_BUTTON:
    if(isCTRLdown==1){
      objCent[0] += (float) dx/2.0*Accel;
      objCent[1] -= (float) dy/2.0*Accel;
    }else{
      rotY += (float) dx/4.0*Accel;
      rotX += (float) dy/4.0*Accel;
    }
    break;
  case GLUT_MIDDLE_BUTTON:
    zoomScale+=(float) dy/10.0*Accel;
  case GLUT_RIGHT_BUTTON:
    objCent[0] += (float) dx/2.0*Accel;
    objCent[1] -= (float) dy/2.0*Accel;
    break;
  }
  prevX = x;
  prevY = y;
  glutPostRedisplay();
}



void make_sphere(){
  //make display list
  int i,j;

  setbuf(stdout,NULL);

  glNewList (atomsid, GL_COMPILE);

  for(i=0;i<natm;i++){
    glPushMatrix();
    glTranslated (ra[i][0], ra[i][1], ra[i][2]);
    //change color
    if(tag[i]==0) glColor3f (1.f,0.f, 0.f);
    else if(tag[i]==1) glColor3f (0.f,1.f, 0.f);
    else if(tag[i]==2) glColor3f (0.f,0.f, 1.f);
    else if(tag[i]==3) glColor3f (1.f,1.f, 0.f);
    else glColor3f (0.5f,0.5f, 0.5f);

    glutSolidSphere((float)radius, 20,20);

    glPopMatrix();
  }

  glEndList();
}

/**********************************************************************
  Make MD box display-list
***********************************************************************/
void makeMDbox(void){
  // md box vertex
  float mdboxv[8][3];
  // md box edge
  int mdboxe[12][2]={
    { 0, 1 },{ 1, 2 },{ 2, 3 },{ 3, 0 },
    { 4, 5 },{ 5, 6 },{ 6, 7 },{ 7, 4 },
    { 0, 4 },{ 1, 5 },{ 2, 6 },{ 3, 7 }
  };

  int i;
  /* line width */
  float lw;

  /* MD box vertex */
  /* index is blow
   *   upper plane
   *   7--6
   *   |  |
   *   4--5
   *
   *   lower plane
   *   3--2
   *   |  |
   *   0--1
   */

  mdboxv[0][0]= 0;
  mdboxv[0][1]= 0;
  mdboxv[0][2]= 0;
  mdboxv[1][0]= h[0][0];
  mdboxv[1][1]= h[1][0];
  mdboxv[1][2]= h[2][0];
  mdboxv[2][0]= h[0][0]+ h[0][1];
  mdboxv[2][1]= h[1][0]+ h[1][1];
  mdboxv[2][2]= h[2][0]+ h[2][1];
  mdboxv[3][0]= h[0][1];
  mdboxv[3][1]= h[1][1];
  mdboxv[3][2]= h[2][1];

  mdboxv[4][0]= h[0][2]+0;
  mdboxv[4][1]= h[1][2]+0;
  mdboxv[4][2]= h[2][2]+0;
  mdboxv[5][0]= h[0][2]+h[0][0];
  mdboxv[5][1]= h[1][2]+h[1][0];
  mdboxv[5][2]= h[2][2]+h[2][0];
  mdboxv[6][0]= h[0][2]+h[0][0]+ h[0][1];
  mdboxv[6][1]= h[1][2]+h[1][0]+ h[1][1];
  mdboxv[6][2]= h[2][2]+h[2][0]+ h[2][1];
  mdboxv[7][0]= h[0][2]+h[0][1];
  mdboxv[7][1]= h[1][2]+h[1][1];
  mdboxv[7][2]= h[2][2]+h[2][1];

  glNewList (mdboxid, GL_COMPILE);

  //white MD box
  lw = 1.0;
  glLineWidth(lw);
  glBegin(GL_LINES);
  glColor3f(1.f, 1.f, 1.f);//white
  for(i=0; i<12; i++){
    glVertex3fv(mdboxv[mdboxe[i][0]]);
    glVertex3fv(mdboxv[mdboxe[i][1]]);
  }
  glEnd();

  //Axis
  glLineWidth(2.f);
  glBegin(GL_LINES);
  //z
  glColor3f( 0.0, 0.0, 1.0 );//blue
  glVertex3i(0,0,0);
  glVertex3f(0.f,0.f,h[2][2]*1.5f);
  //y
  glColor3f( 0.0, 1.0, 0.0 );//green
  glVertex3i(0,0,0);
  glVertex3f(0.f,h[1][1]*1.5f,0.f);
  //x
  glColor3f( 1.0, 0.0, 0.0 );//red
  glVertex3i(0,0,0);
  glVertex3f(h[0][0]*1.5f,0.f,0.f);

  glEnd();

  glEndList();
}

void read_files_Akira(char* file){
  //READ KVS FORMAT
  FILE *ifp;
  char tmpin[2000];
  int tmp;
  int iatm,inc,n,j;
  int sp;
  float xp,yp,zp,vx,vy,vz,q0,q1,q2,q3,q4,q5,q6,q7,q8;


  setbuf(stdout,NULL);

  printf("reading... %s\n",file);

  //file open
  if((ifp=fopen(file,"r"))==NULL) {
    printf("\nERROR: Failed opening input file: %s\n",file);
    fflush(stdout);
    exit(0);
  }

  // Read total number of particles
  fscanf(ifp,"%d %d %d %d ",&n, &tmp, &tmp, &tmp);
  //printf("to read N:%d \n",n);
  //read h-matrix
  fscanf(ifp,"%f %f %f",&h[0][0],&h[0][1],&h[0][2]);
  fscanf(ifp,"%f %f %f",&h[1][0],&h[1][1],&h[1][2]);
  fscanf(ifp,"%f %f %f",&h[2][0],&h[2][1],&h[2][2]);

  printf("h matrix\n");
  printf("%e %e %e\n",h[0][0],h[0][1],h[0][2]);
  printf("%e %e %e\n",h[1][0],h[1][1],h[1][2]);
  printf("%e %e %e\n",h[2][0],h[2][1],h[2][2]);

  inc=0;
  printf("reading %d atoms\n",n);
  for(iatm = 0; iatm < n; iatm++) {
    fgets( tmpin, 2000, ifp );
    sscanf(tmpin,"%d %f %f %f",&sp,&xp,&yp,&zp);

    //selection
    if(xSlice==1)if(xp<slice[0][0] || slice[0][1]<xp)continue;
    if(ySlice==1)if(yp<slice[1][0] || slice[1][1]<yp)continue;
    if(zSlice==1)if(zp<slice[2][0] || slice[2][1]<zp)continue;

    tag[inc]=(int)sp;
    ra[inc][0]=h[0][0]*xp+h[0][1]*yp+h[0][2]*zp;
    ra[inc][1]=h[1][0]*xp+h[1][1]*yp+h[1][2]*zp;
    ra[inc][2]=h[2][0]*xp+h[2][1]*yp+h[2][2]*zp;
    //printf("%d %f %f %f\n",tag[inc],ra[inc][0],ra[inc][1],ra[inc][2]);
    inc++;

    //progress bar
    /*
     * if(iatm%maxint(1,n/NDIGIT) ==0 ){
     *   printf("\r");
     *   printf("[");
     *   for(j=0;j<iatm/maxint(1,n/NDIGIT);j++)printf("=");
     *   printf(">");
     *   for(j=0;j<NDIGIT-iatm/maxint(1,n/NDIGIT);j++)printf(" ");
     *   printf("]");
     *   printf(" %3.2f %%",iatm/(float)n*100);
     * }
     */
  }
  //finish progress bar
  /*
   * printf("\r");
   * printf("[");
   * for(j=0;j<NDIGIT;j++)printf("=");
   * printf(">]");
   * printf(" 100.00 %%\n");
   * printf("Done!\n");
   */

  natm=inc;
  printf("visible atoms/all atoms = %d/%d \n",natm,n);
  fclose(ifp);
}



void printCommanlineOption(void){
    printf("\n");
    printf("******* command line options ********\n");
    printf("-x a b: draw a<x<b\n");
    printf("-y a b: draw a<y<b\n");
    printf("-z a b: draw a<z<b\n");
    printf("-r n: atom sphere=n. Default=point\n");
    printf("*************************************\n");
    printf("\n");
}

int main(int argc, char** argv){

  int i=1;
  char title[128],inFile[128];

  if(argc==1){
    printCommanlineOption();
    //interactive mode
    printf("--->interactive mode\n");
    printf("enter filename\n");
    scanf("%s",inFile);
    sprintf(title,"AkiraViewer: %s",inFile);

  }else{
    //
    while (argv[i][0] == '-'){ //parse argument
      switch (argv[i][1]) {
      case 'x':  //x sllice
        xSlice=1;
        slice[0][0] = atof(argv[++i]);
        slice[0][1] = atof(argv[++i]);
        break;
      case 'y':  //y slice
        ySlice=1;
        slice[1][0] = atof(argv[++i]);
        slice[1][1] = atof(argv[++i]);
        break;
      case 'z':  //z slice
        zSlice=1;
        slice[2][0] = atof(argv[++i]);
        slice[2][1] = atof(argv[++i]);
        break;
      case 'r':
        radius = atoi(argv[++i]);
        break;
      default:
        fprintf(stderr,"Bad command line arguments, %s\n\n",argv[i]);
        exit(0);
        break;
      } /* end switch argv[i][1] */
      ++i;
    } /* end while i */

    //last argv is input file
    sprintf(inFile,"%s",argv[i]);
    sprintf(title,"cAkiraViewer: %s",inFile);
  }

  printf("\n<<< AkiraViewer >>>\n");
  glutInit(&argc, argv);

  glutInitWindowPosition(200, 10);
  glutInitWindowSize( width, height);
  glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGBA);

  glutCreateWindow(title);

  //last argv is input file
  setup(inFile);

  glutDisplayFunc(display);
  glutReshapeFunc (reshape);
  glutKeyboardFunc(keyPress);
  glutSpecialFunc(specialKeyPress);
  glutMouseFunc(mousePress);
  glutMotionFunc(mouseDrag);
  glutIdleFunc(NULL);
  glutMainLoop();

  return( 0 );
}
//linux:
// % gcc -O3 -lm -lGLU -lGL -lXmu -lXt -lSM -lICE -lXext -lX11 -lglut -L/usr/X11R6/lib -I/usr/X11R6/include -o cAkiraViewer cAkiraViewer.c

//osx:
// % gcc -lm -framework Foundation -framework OpenGL -framework GLUT -o cAkiraViewer cAkiraViewer.c

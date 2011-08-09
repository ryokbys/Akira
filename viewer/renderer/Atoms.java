package viewer.renderer;

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

public class Atoms extends data.Atoms implements Renderer{

  public int nframe;//?
  public int visibleNatoms=0;
  public int[] vtag;
  public HashMap<Integer,Integer> specialTag = new HashMap<Integer,Integer>();

  public boolean isSetVtag=false;

  //override
  public void allocate(int n){
    if(maxNatom==0 || maxNatom<n){
      this.maxNatom=n;
      tag  = null;
      tag  = new byte[n];
      r    = null;
      r    = new float[n][3];
      data = null;
      data = new float[n][Const.DATA];
      vtag = null;
      vtag = new int[n];
      resetVisualTag();
    }
  }


  /* constructor */
  private RenderingWindow rw;
  public ColorTable ctable;
  public Atoms(RenderingWindow rw){
    super();
    this.rw=rw;
    ctable=new ColorTable(rw);
  }

  public void show(){
    rw.gl.glCallList(atoms_t);
  }


  private void addSTag(int i,int val){
    if(i<0 || i>n)System.out.println("wowowow!");
    //val=1: delete
    //val=2: special color
    specialTag.put(i,val);
  }

  private float[] mulH( float[][] h,float[] in ){
    float[] out = new float[3];
    for(int k=0; k<3; k++) out[k] =h[k][0]*in[0] +h[k][1]*in[1] +h[k][2]*in[2];
    return out;
  }

  private int atoms_t=Const.DISPLAY_LIST_EMPTY;
  public void make(){
    ViewConfig vconf=rw.vconf;
    if(rw.renderingAtomDataIndex>0)ctable.setRange(vconf.dataRange[rw.renderingAtomDataIndex-1]);

    //clear & new display list
    if(atoms_t!=Const.DISPLAY_LIST_EMPTY)rw.gl.glDeleteLists( atoms_t, 1);

    atoms_t = rw.gl.glGenLists(1);
    rw.gl.glNewList(atoms_t, GL2.GL_COMPILE);

    //for extend rendering
    float[][] ext=vconf.extendRenderingFactor;

    int extendNx1=(int)Math.floor(ext[0][0]);//切り下げ
    int extendNy1=(int)Math.ceil(ext[0][1]-1.f);//切り上げ
    int extendNz1=(int)Math.floor(ext[0][2]);//切り下げ
    int extendNx2=(int)Math.ceil(ext[1][0]-1.f);//切り上げ
    int extendNy2=(int)Math.floor(ext[1][1]);//切り下げ
    int extendNz2=(int)Math.ceil(ext[1][2]-1.f);//切り上げ

    visibleNatoms=0;
    for( int i=0; i<n; i++ ){
      int itag=tag[i]-1;
      if(!vconf.tagOnOff[itag])continue;


      //
      if(isSetVtag && vtag[i]<0)continue;


      float[] color=getAtomColor(i);
      rw.gl.glMateriali( GL2.GL_FRONT, GL2.GL_SHININESS, vconf.atomShineness);
      rw.gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_SPECULAR, vconf.atomSpecular, 0 );
      rw.gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_EMISSION, vconf.atomEmmission, 0 );
      rw.gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, vconf.atomAmb, 0 );
      rw.gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, color, 0 );
      rw.gl.glColor3fv(color, 0 );


      // tmp[0:2]: atom position scaled by h-matrix, value range=[0:1]
      float[] tmp=mulH(hinv,r[i]);

      for(int ix=extendNx1;ix<=extendNx2;ix++){
        if(!(ext[0][0]<=ix+tmp[0] && ix+tmp[0]<ext[1][0]))continue;

        rw.gl.glPushMatrix();
        rw.gl.glTranslatef( ix*h[0][0], ix*h[1][0], ix*h[2][0]);
        for(int iy=extendNy1;iy<=extendNy2;iy++){
          if(!(ext[0][1]<=iy+tmp[1] && iy+tmp[1]<ext[1][1]))continue;

          rw.gl.glPushMatrix();
          rw.gl.glTranslatef(iy*h[0][1],iy*h[1][1],iy*h[2][1]);
          for(int iz=extendNz1;iz<=extendNz2;iz++){
            if(!(ext[0][2]<=iz+tmp[2] && iz+tmp[2]<ext[1][2]))continue;

            rw.gl.glPushMatrix();
            rw.gl.glTranslatef(iz*h[0][2],iz*h[1][2],iz*h[2][2]);
            rw.gl.glTranslatef( r[i][0],r[i][1],r[i][2] );

            visibleNatoms++;
            //rendering

            switch(rw.renderingAtomType){
            case 0:
              //point
              rw.gl.glDisable( GL2.GL_LIGHTING );
              rw.gl.glCallList(pointObject.get(itag));
              rw.gl.glEnable( GL2.GL_LIGHTING );
              break;
            case 1:
              //solid sphere
              rw.gl.glCallList(sphereObject.get(itag));
              break;
            case 2:
              //toon sphere
              rw.gl.glCallList(sphereObject.get(itag));
              break;
            case 3:
              //wire sphere
              rw.gl.glCallList(meshSphereObject.get(itag));
              break;
            }

            rw.gl.glPopMatrix();
          }//iz
          rw.gl.glPopMatrix();
        }//iy
        rw.gl.glPopMatrix();
      }//ix
    }//iatom

    rw.gl.glEndList();
  }

  //display list of point object
  private ArrayList<Integer> pointObject = new ArrayList<Integer>();
  private ArrayList<Integer> sphereObject = new ArrayList<Integer>();
  private ArrayList<Integer> meshSphereObject = new ArrayList<Integer>();

  public void makePrimitiveObjects(){
    ViewConfig vconf=rw.vconf;

    //delete
    if(sphereObject.size()>0){
      for(int i=0;i<Const.TAG;i++)rw.gl.glDeleteLists(sphereObject.get(i), 1);
      sphereObject.clear();
    }
    if(meshSphereObject.size()>0){
      for(int i=0;i<Const.TAG;i++)rw.gl.glDeleteLists(meshSphereObject.get(i), 1);
      meshSphereObject.clear();
    }
    if(pointObject.size()>0){
      for(int i=0;i<Const.TAG;i++)rw.gl.glDeleteLists(pointObject.get(i), 1);
      pointObject.clear();
    }

    //create
    for(int i=0;i<Const.TAG;i++){
      //point
      int po = rw.gl.glGenLists(1);
      rw.gl.glNewList(po, GL2.GL_COMPILE);
      rw.gl.glPointSize((float)vconf.tagRadius[i]);

      rw.gl.glBegin(GL2.GL_POINTS);
      rw.gl.glVertex3f( 0.f, 0.f, 0.f);
      rw.gl.glEnd();
      rw.gl.glEndList();
      pointObject.add(po);
      //solid
      int solid;
      solid = rw.gl.glGenLists(1);
      rw.gl.glNewList(solid, GL2.GL_COMPILE);
      rw.glut.glutSolidSphere( vconf.tagRadius[i],
                               vconf.tagSlice[i],
                               vconf.tagStack[i]);
      rw.gl.glEndList();
      sphereObject.add(solid);

      //wire
      int wire = rw.gl.glGenLists(1);
      rw.gl.glNewList(wire, GL2.GL_COMPILE);
      rw.glut.glutWireSphere( vconf.tagRadius[i],
                              vconf.tagSlice[i],
                              vconf.tagStack[i]);
      rw.gl.glEndList();
      meshSphereObject.add(wire);
    }
  }

  public void resetVisualTag(){
    for( int i=0; i<n; i++ )vtag[i]=0;
  }

  public void setVisualTag(){
    isSetVtag=true;
    ViewConfig vconf=rw.vconf;

    MAINLOOP:for( int i=0; i<n; i++ ){
      float[] ri=r[i];

      if(vtag[i]<0)continue MAINLOOP;

      //region cut
      if(rw.sq.isBandClose){
        ArrayList<Float> cutN=rw.sq.cutN;
        ArrayList<Float> cutP=rw.sq.cutP;

        for(int k=0;k<cutN.size()/3;k++){
          double inner=0;
          for(int j=0;j<3;j++) inner+=cutN.get(3*k+j)*(ri[j]-cutP.get(3*k+j));
          if(inner<0.0){
            vtag[i]=-1;
            continue MAINLOOP;
          }
        }
      }

      //rectangle cut
      if(vconf.isRectangleSelectMode){
        ArrayList<Float> cutN=rw.sq.cutRN;
        ArrayList<Float> cutP=rw.sq.cutRP;

        for(int k=0;k<cutN.size()/3;k++){
          double inner=0;
          for(int j=0;j<3;j++) inner+=cutN.get(3*k+j)*(ri[j]-cutP.get(3*k+j));
          if(inner>0.0){
            vtag[i]=-1;
            continue MAINLOOP;
          }
        }
      }



      //special tag cut
      Set set = specialTag.keySet();
      Iterator iterator = set.iterator();
      Integer object;
      while(iterator.hasNext()){
        object = (Integer)iterator.next();
        if(i==object && specialTag.get(object)==1){
          vtag[i]=-1;
          continue MAINLOOP;
        }
      }

      //sphere cut
      if(vconf.isSphereCut){
        double[] org=new double[3];
        for(int j=0;j<3;j++){
          org[j]=0.;
          for(int k=0;k<3;k++)org[j]+=h[j][k]*vconf.spherecutPos[k];
        }
        double r2=(ri[0]-org[0])*(ri[0]-org[0])
          +(ri[1]-org[1])*(ri[1]-org[1])
          +(ri[2]-org[2])*(ri[2]-org[2]);
        double rc2=vconf.spherecutRadius*vconf.spherecutRadius;

        if(r2>rc2){
          vtag[i]=-1;
          continue MAINLOOP;
        }
      }

      //plane cut
      for(int ii=0;ii<Const.PLANE;ii++){
        if(vconf.isOnSlicer[ii]){
          double[] org=new double[3];
          double[] n=new double[3];
          for(int j=0;j<3;j++){
            org[j]=0.;
            n[j]=0.;
            for(int k=0;k<3;k++){
              org[j]+=h[j][k]*vconf.posVecSlicer[ii][k];
              n[j]+=h[j][k]*vconf.normalVecSlicer[ii][k];
            }
          }

          double inner=0;
          for(int j=0;j<3;j++) inner+=n[j]*(ri[j]-org[j]);
          if(inner>0.0){
            vtag[i]=-1;
            continue MAINLOOP;
          }
        }
      }

    }//i

  }

  public float[] getAtomColor(int i){
    if(rw.renderingAtomDataIndex>0){
      //data color
      return ctable.getColor(data[i][rw.renderingAtomDataIndex-1]);
    }else{
      //species color
      return rw.vconf.tagColor[tag[i]-1];
    }

  }

  //for picking
  private int pickedAtoms_t=Const.DISPLAY_LIST_EMPTY;
  public void makePickingAtoms(){
    ViewConfig vconf=rw.vconf;
    if(pickedAtoms_t!=Const.DISPLAY_LIST_EMPTY)rw.gl.glDeleteLists( pickedAtoms_t, 1);
    pickedAtoms_t = rw.gl.glGenLists(1);
    rw.gl.glNewList( pickedAtoms_t, GL2.GL_COMPILE );
    rw.gl.glInitNames();
    rw.gl.glPushName( -1 );
    for( int i=0; i<n; i++ ){
      rw.gl.glPushMatrix();
      rw.gl.glLoadName(i);
      //render invisible atom
      if( vtag[i] <0 ) continue; //skip negative tag

      rw.gl.glTranslatef( r[i][0],r[i][1],r[i][2] );
      rw.glut.glutSolidSphere( vconf.tagRadius[vtag[i]],6,6);

      rw.gl.glPopMatrix();
    }
    rw.gl.glEndList();
    rw.gl.glCallList( pickedAtoms_t);
  }

  //picked atom
  private int pickedAtom_t=Const.DISPLAY_LIST_EMPTY;
  public void makePickedAtom(int id){
    ViewConfig vconf=rw.vconf;
    if(id<0)return;
    rw.gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_SPECULAR, vconf.lightSpc, 0 );
    rw.gl.glMateriali( GL2.GL_FRONT, GL2.GL_SHININESS, vconf.lightShininess );
    if(pickedAtom_t!=Const.DISPLAY_LIST_EMPTY)rw.gl.glDeleteLists( pickedAtom_t, 1);
    pickedAtom_t = rw.gl.glGenLists(1);
    rw.gl.glNewList( pickedAtom_t, GL2.GL_COMPILE );

    float[] pcolor = {0.0f, 1.0f, 1.0f, 1.f };

    rw.gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE,
                        pcolor, 0 );
    rw.gl.glPushMatrix();
    rw.gl.glTranslatef( r[id][0],r[id][1],r[id][2] );
    rw.glut.glutWireSphere( vconf.tagRadius[tag[id]-1]*1.1, 10, 10 );
    rw.gl.glPopMatrix();
    rw.gl.glEndList();
  }
  public void showPickedAtom(){
    rw.gl.glCallList( pickedAtom_t );
  }

  public void deletePickedAtom(int id){
    System.out.println(String.format("deleted id: %d",id));
    addSTag(id,1);
    make();
  }
  public void changePickedAtomColor(int id){
    addSTag(id,2);
    make();
  }

  //trajectory
  private ArrayList<Integer> trjList = new ArrayList<Integer>();
  public void makeTrajectory(TrajectoryPanel trj){
    ViewConfig vconf=rw.vconf;
    int trj_t = rw.gl.glGenLists(1);
    rw.gl.glNewList( trj_t, GL2.GL_COMPILE );
    for( int irow=0; irow<trj.jtable.getRowCount(); irow++ ){
      int i = ((Integer)trj.jtable.getValueAt( irow, 0 )).intValue();
      String type = (String)trj.jtable.getValueAt( irow, 1 );
      float width = ((Float)trj.jtable.getValueAt( irow, 2 )).floatValue();
      float[] color = ((Color)trj.jtable.getValueAt( irow, 3 )).getRGBComponents(null);

      rw.gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_SPECULAR, vconf.lightSpc, 0 );
      rw.gl.glMateriali( GL2.GL_FRONT, GL2.GL_SHININESS, vconf.lightShininess );
      if(prev[i][0]!=EMPTY){

        //draw
        if( type.equals("LinesPoint") ){
          //point
          rw.gl.glDisable( GL2.GL_LIGHTING );
          rw.gl.glColor3fv(color, 0 );

          rw.gl.glPointSize(width);
          rw.gl.glBegin(GL2.GL_POINTS);
          rw.gl.glVertex3f( r[i][0], r[i][1], r[i][2] );
          rw.gl.glEnd();
          //line
          rw.gl.glColor3fv(color, 0 );
          rw.gl.glLineWidth(width);
          rw.gl.glBegin(GL2.GL_LINES);
          rw.gl.glVertex3f( prev[i][0], prev[i][1], prev[i][2] );
          rw.gl.glVertex3f( r[i][0], r[i][1], r[i][2] );
          rw.gl.glEnd();
          rw.gl.glEnable( GL2.GL_LIGHTING );

        }else if( type.equals("Lines") ){
          rw.gl.glDisable( GL2.GL_LIGHTING );
          rw.gl.glColor3fv(color, 0 );
          rw.gl.glLineWidth( width );
          rw.gl.glBegin( GL2.GL_LINES );
          rw.gl.glVertex3f( prev[i][0],    prev[i][1],    prev[i][2] );
          rw.gl.glVertex3f( r[i][0], r[i][1], r[i][2] );
          rw.gl.glEnd();
          rw.gl.glEnable( GL2.GL_LIGHTING );
        }else if( type.equals("SolidSphere") ){
          rw.gl.glMaterialfv( GL2.GL_FRONT_AND_BACK,GL2.GL_AMBIENT_AND_DIFFUSE,
                              color, 0 );
          rw.gl.glPushMatrix();
          rw.gl.glTranslatef( r[i][0], r[i][1], r[i][2] );
          rw.glut.glutSolidSphere( width, 8, 8 );
          rw.gl.glPopMatrix();
        }else {
          rw.gl.glMaterialfv( GL2.GL_FRONT_AND_BACK,GL2.GL_AMBIENT_AND_DIFFUSE,
                              color, 0 );
          rw.gl.glPushMatrix();
          rw.gl.glTranslatef( r[i][0], r[i][1], r[i][2] );
          rw.glut.glutWireSphere( width, 8, 8 );
          rw.gl.glPopMatrix();
        }
      }//end of empty
    }//end of i: jtable

    //end list
    rw.gl.glEndList();
    trjList.add(trj_t);
  }


  final static float EMPTY=-127.889f;
  public void allocate4Trj(){
    prev=new float[maxNatom][3];
    for(int i=0;i<maxNatom;i++)prev[i][0]=EMPTY;
  }
  public void resetTrjDList(){
    for(int i=0;i<maxNatom;i++)prev[i][0]=EMPTY;
    for(int i=0;i<trjList.size();i++)rw.gl.glDeleteLists(trjList.get(i) , 1);
    trjList.clear();
  }
  public void trajectoryShow(){
    for(int i=0;i<trjList.size();i++)rw.gl.glCallList(trjList.get(i));
  }


}

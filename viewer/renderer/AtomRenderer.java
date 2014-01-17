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

public class AtomRenderer implements Renderer{

  // Atom-type constants
  public static final byte ATOM_TYPE_POINT    = 0;
  public static final byte ATOM_TYPE_SOLID    = 1;
  public static final byte ATOM_TYPE_TOON     = 2;
  public static final byte ATOM_TYPE_WIRE     = 3;

  public Atoms atoms= new Atoms();

  public int nframe;//?
  public int visibleNatoms=0;
  public HashMap<Integer,Integer> specialTag = new HashMap<Integer,Integer>();

  public boolean isSetVtag=false;

  //for trajectory
  public float[][] prev;

  private RenderingWindow rw;
  public ColorTable ctable;

  //display list of point object
  private ArrayList<Integer> pointObject = new ArrayList<Integer>();
  private ArrayList<Integer> sphereObject = new ArrayList<Integer>();
  private ArrayList<Integer> meshSphereObject = new ArrayList<Integer>();

  private int atoms_t      =Const.DISPLAY_LIST_EMPTY;
  private int pickedAtoms_t=Const.DISPLAY_LIST_EMPTY;
  private int pickedAtom_t =Const.DISPLAY_LIST_EMPTY;

  /**
   * Constructs new AtomRenderer with doing nothing.
   */
  public AtomRenderer(){
  }

  /**
   * Constructs new AtomRenderer with a given RenderingWindow.
   */
  public AtomRenderer( RenderingWindow rw ){
    this.rw=rw;
    this.atoms= rw.atoms;
    ctable=new ColorTable(rw);

  }

  public void show(){
    rw.gl.glCallList(atoms_t);
  }

  private void addSTag(int i,int val){
    if( i<0 || i>atoms.getNumAtoms() )
      System.out.println("wowowow!");
    //val=1: delete
    //val=2: special color
    specialTag.put(i,val);
  }

  public void make(){
    ViewConfig vconf=rw.vconf;
    if( rw.renderingAtomDataIndex>0 )
      ctable.setRange(vconf.dataRange[rw.renderingAtomDataIndex-1]);

    //clear & new display list
    if( atoms_t!=Const.DISPLAY_LIST_EMPTY )
      rw.gl.glDeleteLists( atoms_t, 1);

    atoms_t = rw.gl.glGenLists(1);
    rw.gl.glNewList(atoms_t, GL2.GL_COMPILE);

    //for extend rendering
    float[][] ext=vconf.extendRenderingFactor;

    int extendNx1=(int)Math.floor(ext[0][0]);   //
    int extendNy1=(int)Math.ceil(ext[0][1]-1.f);//
    int extendNz1=(int)Math.floor(ext[0][2]);   //
    int extendNx2=(int)Math.ceil(ext[1][0]-1.f);//
    int extendNy2=(int)Math.floor(ext[1][1]);   //
    int extendNz2=(int)Math.ceil(ext[1][2]-1.f);//

    float[][] h = atoms.hmat;
    float[][] hi= atoms.hmati;
    float[] sft= {vconf.pshiftx,vconf.pshifty,vconf.pshiftz};
    int natm= atoms.getNumAtoms();
    visibleNatoms=0;
    for( int i=0; i<natm; i++ ){
      Atom ai= atoms.getAtom(i);
      int itag= ai.tag -1;
      if( ! vconf.tagOnOff[itag] ) continue;
      //
      if( isSetVtag && ! ai.isVisible ) continue;

      float[] color=getAtomColor(i);
      rw.gl.glMateriali( GL2.GL_FRONT, GL2.GL_SHININESS, vconf.atomShineness);
      rw.gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_SPECULAR, vconf.atomSpecular, 0 );
      rw.gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_EMISSION, vconf.atomEmmission, 0 );
      rw.gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, vconf.atomAmb, 0 );
      rw.gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, color, 0 );
      rw.gl.glColor3fv(color, 0 );

      // tmp[0:2]: atom position scaled by h-matrix, value range=[0:1]
      float[] tmp=MDMath.mulH( hi, ai.pos );
      float[] posi= new float[3];
      tmp[0]= tmp[0]+sft[0];
      tmp[1]= tmp[1]+sft[1];
      tmp[2]= tmp[2]+sft[2];
      tmp[0]= MDMath.pbc(tmp[0]);
      tmp[1]= MDMath.pbc(tmp[1]);
      tmp[2]= MDMath.pbc(tmp[2]);
      posi= MDMath.mulH(h,tmp);

      for(int ix=extendNx1;ix<=extendNx2;ix++){
        if(!(ext[0][0]<=ix+tmp[0] && ix+tmp[0]<ext[1][0])) continue;

        rw.gl.glPushMatrix();
        rw.gl.glTranslatef( ix*h[0][0], ix*h[1][0], ix*h[2][0]);
        for(int iy=extendNy1;iy<=extendNy2;iy++){
          if(!(ext[0][1]<=iy+tmp[1] && iy+tmp[1]<ext[1][1])) continue;

          rw.gl.glPushMatrix();
          rw.gl.glTranslatef(iy*h[0][1],iy*h[1][1],iy*h[2][1]);
          for(int iz=extendNz1;iz<=extendNz2;iz++){
            if(!(ext[0][2]<=iz+tmp[2] && iz+tmp[2]<ext[1][2])) continue;

            rw.gl.glPushMatrix();
            rw.gl.glTranslatef(iz*h[0][2],iz*h[1][2],iz*h[2][2]);
            rw.gl.glTranslatef( posi[0],posi[1],posi[2] );

            visibleNatoms++;
            //rendering

            switch( rw.renderingAtomType ){
            case ATOM_TYPE_POINT:
              //point
              rw.gl.glDisable( GL2.GL_LIGHTING );
              rw.gl.glCallList(pointObject.get(itag));
              rw.gl.glEnable( GL2.GL_LIGHTING );
              break;
            case ATOM_TYPE_SOLID:
              //solid sphere
              rw.gl.glCallList(sphereObject.get(itag));
              break;
            case ATOM_TYPE_TOON:
              //toon sphere
              rw.gl.glCallList(sphereObject.get(itag));
              break;
            case ATOM_TYPE_WIRE:
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
    isSetVtag=true;

  }

  public void makePrimitiveObjects(){
    ViewConfig vconf=rw.vconf;

    //delete
    if(sphereObject.size()>0){
      for(int i=0;i<Const.TAG;i++)
        rw.gl.glDeleteLists(sphereObject.get(i), 1);
      sphereObject.clear();
    }
    if(meshSphereObject.size()>0){
      for(int i=0;i<Const.TAG;i++)
        rw.gl.glDeleteLists(meshSphereObject.get(i), 1);
      meshSphereObject.clear();
    }
    if(pointObject.size()>0){
      for(int i=0;i<Const.TAG;i++)
        rw.gl.glDeleteLists(pointObject.get(i), 1);
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
    int natm= atoms.getNumAtoms();
    for( int i=0; i<natm; i++ ) {
      atoms.getAtom(i).isVisible= true;
      //vtag[i]=1;
    }
    specialTag.clear();
  }

  public void setVisualTag(){
    isSetVtag=true;
    ViewConfig vconf=rw.vconf;

    float[][] h= atoms.hmat;
    int natm= atoms.getNumAtoms();
    float[] sft= {vconf.pshiftx,vconf.pshifty,vconf.pshiftz};

  MAINLOOP:
    for( int i=0; i<natm; i++ ){
      Atom ai= atoms.getAtom(i);

      if( ! ai.isVisible ) continue MAINLOOP;

      float[] ri= MDMath.mulH(atoms.hmati,ai.pos);
      ri[0]= ri[0]+sft[0];
      ri[1]= ri[1]+sft[1];
      ri[2]= ri[2]+sft[2];
      ri[0]= MDMath.pbc(ri[0]);
      ri[1]= MDMath.pbc(ri[1]);
      ri[2]= MDMath.pbc(ri[2]);
      ri= MDMath.mulH(atoms.hmati,ri);

      //region cut
      if(rw.sq.isBandClose){
        ArrayList<Float> cutN=rw.sq.cutN;
        ArrayList<Float> cutP=rw.sq.cutP;

        for(int k=0;k<cutN.size()/3;k++){
          double inner=0;
          for(int j=0;j<3;j++)
            inner+=cutN.get(3*k+j)*(ri[j]-cutP.get(3*k+j));
          if(inner<0.0){
            //vtag[i]=-1;
            ai.isVisible= false;
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
          //vtag[i]=-1;
          ai.isVisible= false;
          continue MAINLOOP;
        }
      }

      //plane cut
      for(int ii=0;ii<Const.PLANE;ii++){
        if(vconf.isOnSlicer[ii]){
          double[] org =new double[3];
          double[] n   =new double[3];
          for(int j=0;j<3;j++){
            org[j]=0.;
            n[j]=0.;
            for(int k=0;k<3;k++){
              org[j]+=h[j][k]*vconf.posVecSlicer[ii][k];
              n[j]  +=h[j][k]*vconf.normalVecSlicer[ii][k];
            }
          }

          double inner=0;
          for(int j=0;j<3;j++) inner+=n[j]*(ri[j]-org[j]);
          if(inner>0.0){
            //vtag[i]=-1;
            ai.isVisible= false;
            continue MAINLOOP;
          }
        }
      }

    }//i

  }

  public float[] getAtomColor( int i ){
    Atom ai= atoms.getAtom(i);
    if( rw.renderingAtomDataIndex > 0 ){
      //data color
      //return ctable.getColor(data[i][rw.renderingAtomDataIndex-1]);
      return ctable.getColor(ai.auxData[rw.renderingAtomDataIndex-1]);
    }else{
      //species color
      //return rw.vconf.tagColor[tag[i]-1];
      return rw.vconf.tagColor[ai.tag-1];
    }
  }

  public void makePickingAtoms(){
    ViewConfig vconf=rw.vconf;
    if( pickedAtoms_t!=Const.DISPLAY_LIST_EMPTY )
      rw.gl.glDeleteLists( pickedAtoms_t, 1);
    pickedAtoms_t = rw.gl.glGenLists(1);
    rw.gl.glNewList( pickedAtoms_t, GL2.GL_COMPILE );
    rw.gl.glInitNames();
    rw.gl.glPushName( -1 );
    float[][] h= atoms.hmat;
    float[] sft= {vconf.pshiftx,vconf.pshifty,vconf.pshiftz};
    for( int i=0; i<atoms.getNumAtoms(); i++ ){
      Atom ai= atoms.getAtom(i);
      rw.gl.glPushMatrix();
      rw.gl.glLoadName(i);
      //render invisible atom
      if( ! ai.isVisible ) continue; //skip negative tag
      float[] posi= MDMath.mulH(atoms.hmati,ai.pos);
      posi[0]= posi[0] +sft[0];
      posi[1]= posi[1] +sft[1];
      posi[2]= posi[2] +sft[2];
      posi[0]= MDMath.pbc(posi[0]);
      posi[1]= MDMath.pbc(posi[1]);
      posi[2]= MDMath.pbc(posi[2]);
      posi= MDMath.mulH(atoms.hmat,posi);

      rw.gl.glTranslatef( posi[0],posi[1],posi[2] );
      rw.glut.glutSolidSphere( vconf.tagRadius[ai.tag],6,6);

      rw.gl.glPopMatrix();
    }
    rw.gl.glEndList();
    rw.gl.glCallList( pickedAtoms_t );
  }

  public void makePickedAtom( int id ){
    Atom aid= atoms.getAtom(id);
    ViewConfig vconf=rw.vconf;
    if( id<0 ) return;
    float[] sft= {vconf.pshiftx,vconf.pshifty,vconf.pshiftz};
    float[] posi= MDMath.mulH(atoms.hmati,aid.pos);
    posi[0]= posi[0]+sft[0];
    posi[1]= posi[1]+sft[1];
    posi[2]= posi[2]+sft[2];
    posi[0]= MDMath.pbc(posi[0]);
    posi[1]= MDMath.pbc(posi[1]);
    posi[2]= MDMath.pbc(posi[2]);
    posi= MDMath.mulH(atoms.hmat,posi);
    
    rw.gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_SPECULAR, vconf.lightSpc, 0 );
    rw.gl.glMateriali( GL2.GL_FRONT, GL2.GL_SHININESS, vconf.lightShininess );
    if(pickedAtom_t!=Const.DISPLAY_LIST_EMPTY)rw.gl.glDeleteLists( pickedAtom_t, 1);
    pickedAtom_t = rw.gl.glGenLists(1);
    rw.gl.glNewList( pickedAtom_t, GL2.GL_COMPILE );

    float[] pcolor = {0.0f, 1.0f, 1.0f, 1.f };

    rw.gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE,
                        pcolor, 0 );
    rw.gl.glPushMatrix();
    rw.gl.glTranslatef( posi[0],posi[1],posi[2] );
    rw.glut.glutWireSphere( vconf.tagRadius[aid.tag-1]*1.1, 10, 10 );
    rw.gl.glPopMatrix();
    rw.gl.glEndList();
  }
  public void showPickedAtom(){
    rw.gl.glCallList( pickedAtom_t );
  }

  public void deletePickedAtom(int id){
    System.out.println(String.format("deleted id: %d",id));
    addSTag(id,1);
    //vtag[id]=-1;
    atoms.getAtom(id).isVisible= false;
    make();
  }
  public void changePickedAtomColor(int id){
    addSTag(id,2);
    //vtag[id]=-1;
    atoms.getAtom(id).isVisible= false;
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
      Atom ai= atoms.getAtom(i);
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
          rw.gl.glVertex3f( ai.pos[0], ai.pos[1], ai.pos[2] );
          rw.gl.glEnd();
          //line
          rw.gl.glColor3fv(color, 0 );
          rw.gl.glLineWidth(width);
          rw.gl.glBegin(GL2.GL_LINES);
          rw.gl.glVertex3f( prev[i][0], prev[i][1], prev[i][2] );
          rw.gl.glVertex3f( ai.pos[0], ai.pos[1], ai.pos[2] );
          rw.gl.glEnd();
          rw.gl.glEnable( GL2.GL_LIGHTING );

        }else if( type.equals("Lines") ){
          rw.gl.glDisable( GL2.GL_LIGHTING );
          rw.gl.glColor3fv(color, 0 );
          rw.gl.glLineWidth( width );
          rw.gl.glBegin( GL2.GL_LINES );
          rw.gl.glVertex3f( prev[i][0],    prev[i][1],    prev[i][2] );
          rw.gl.glVertex3f( ai.pos[0], ai.pos[1], ai.pos[2] );
          rw.gl.glEnd();
          rw.gl.glEnable( GL2.GL_LIGHTING );
        }else if( type.equals("SolidSphere") ){
          rw.gl.glMaterialfv( GL2.GL_FRONT_AND_BACK,GL2.GL_AMBIENT_AND_DIFFUSE,
                              color, 0 );
          rw.gl.glPushMatrix();
          rw.gl.glTranslatef( ai.pos[0], ai.pos[1], ai.pos[2] );
          rw.glut.glutSolidSphere( width, 8, 8 );
          rw.gl.glPopMatrix();
        }else {
          rw.gl.glMaterialfv( GL2.GL_FRONT_AND_BACK,GL2.GL_AMBIENT_AND_DIFFUSE,
                              color, 0 );
          rw.gl.glPushMatrix();
          rw.gl.glTranslatef( ai.pos[0], ai.pos[1], ai.pos[2] );
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
    int maxNA= rw.maxNumAtoms;
    prev=new float[maxNA][3];
    for(int i=0;i<maxNA;i++)
      prev[i][0]=EMPTY;
  }
  public void resetTrjDList(){
    int maxNA= rw.maxNumAtoms;
    for(int i=0;i<maxNA;i++)
      prev[i][0]=EMPTY;
    for(int i=0;i<trjList.size();i++)
      rw.gl.glDeleteLists(trjList.get(i) , 1);
    trjList.clear();
  }
  public void trajectoryShow(){
    for(int i=0;i<trjList.size();i++)
      rw.gl.glCallList(trjList.get(i));
  }
}

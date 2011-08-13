package viewer.renderer;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

import com.sun.opengl.util.gl2.*;
import javax.media.opengl.awt.*;


import tools.*;
import tools.DelaunayTriangle.*;

//import quickhull3d.*;

import viewer.*;
import data.*;
import viewer.renderer.*;

/**
 * 平面を表示
 */
public class Plane {

  private final double SMALL=1e-2;



  //globals
  private GL2 gl;
  private GLU glu;
  private GLUT glut;
  private Controller ctrl;
  private ViewConfig vconf;
  private viewer.renderer.Atoms atoms;
  private RenderingWindow rw;
  //constructor
  public Plane(Controller ctrl,RenderingWindow rw){
    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;
    this.rw=rw;

    this.gl = rw.gl;
    this.glu = rw.glu;
    this.glut = rw.glut;
    this.atoms=rw.atoms;


  }

  public void calAllVoronoi(){
    ArrayList<ArrayList<Integer>> lspr= PairList.makePairList(atoms,vconf.planeRcut,true,true);
    HashMap<Integer,Integer> count = new HashMap<Integer,Integer>();

    int face=0;
    for(int id=0;id<atoms.n;id++){
      ArrayList<ArrayList<Float>> jVoronoi= makeVoronoi(id,lspr.get(id));

      for(int i=0;i<jVoronoi.size();i++){
        ArrayList<Float> v= jVoronoi.get(i);
        int na=v.size()/3;
        if(na<3)continue;
        //count
        face++;
        if(count.containsKey(na)){
          int inc=count.get(na);
          inc++;
          count.put(na,inc);
        }else{
          int inc=1;
          count.put(na,inc);
        }
      }//end i
    }//id
      //display count
      Set set = count.keySet();
      Iterator iterator = set.iterator();
      Integer object;
      System.out.println("voronoi information");
      System.out.println(String.format("  |- %d faces are included",face));
      while(iterator.hasNext()){
        object = (Integer)iterator.next();
        System.out.println(String.format("  |-- %d-gon face: %d",
                                         object.intValue(),
                                         count.get(object)));
        face+=count.get(object);
      }//while

  }//allVoronoi
  /**
   * 平面のdisplay listを作成
   */
  public void make(int id){

    if(plane_t!=Const.DISPLAY_LIST_EMPTY)gl.glDeleteLists( plane_t, 1);
    plane_t = gl.glGenLists(1);
    gl.glNewList( plane_t, GL2.GL_COMPILE );

    ArrayList<ArrayList<Integer>> lspr=null;

    //voronoi
    if(vconf.isVoronoiMode && id>=0 && id<atoms.n){
      if(lspr==null)lspr= PairList.makePairList(atoms,vconf.planeRcut,true,false);

      HashMap<Integer,Integer> count = new HashMap<Integer,Integer>();
      ArrayList<ArrayList<Float>> jVoronoi= makeVoronoi(id,lspr.get(id));

      int face=0;
      for(int i=0;i<jVoronoi.size();i++){
        ArrayList<Float> v= jVoronoi.get(i);
        //draw
        gl.glColor4fv(vconf.singlePlaneColor,0);
        if(i%2==0 ){
          gl.glColor4f(0.f,1.f,(float)i/jVoronoi.size(),0.8f);
        }else{
          gl.glColor4f(1.f,(float)i/jVoronoi.size(),0.f,0.8f);
        }


        int na=v.size()/3;
        if(na<3)continue;
        gl.glBegin( GL2.GL_POLYGON );
        //gl.glBegin( GL2.GL_POINTS );
        for(int j=0;j<na;j++){
          gl.glVertex3f(v.get(3*j),v.get(3*j+1),v.get(3*j+2));
        }
        gl.glEnd();
        //count
        face++;
        if(count.containsKey(na)){
          int inc=count.get(na);
          inc++;
          count.put(na,inc);
        }else{
          int inc=1;
          count.put(na,inc);
        }
      }//end i
      //display count
      Set set = count.keySet();
      Iterator iterator = set.iterator();
      Integer object;
      System.out.println("voronoi information");
      System.out.println(String.format("  |- %d-centered VP has %d faces",id,face));
      while(iterator.hasNext()){
        object = (Integer)iterator.next();
        System.out.println(String.format("  |-- %d-gon face: %d",
                                         object.intValue(),
                                         count.get(object)));
        face+=count.get(object);
      }

    }//end voronoi
    if(vconf.isDelaunayMode)makeDelaunay();

    //tetrahedron
    if(vconf.isTetrahedronMode){
      if(lspr==null)lspr= PairList.makePairList(atoms,vconf.planeRcut,true,false);
      ArrayList<ArrayList<Float>> tetra= makeTetrahedron(vconf.tetrahedronCenter,
                                                         vconf.planeRcut,lspr);
      for(int i=0;i<tetra.size();i++){
        ArrayList<Float> v= tetra.get(i);
        gl.glColor4fv(vconf.singlePlaneColor,0);

        for(int j=0;j<v.size()/3-2;j++){
          for(int k=j+1;k<v.size()/3-1;k++){
            for(int l=k+1;l<v.size()/3;l++){
              gl.glBegin( GL2.GL_TRIANGLES);
              gl.glVertex3f(v.get(3*j),v.get(3*j+1),v.get(3*j+2));
              gl.glVertex3f(v.get(3*k),v.get(3*k+1),v.get(3*k+2));
              gl.glVertex3f(v.get(3*l),v.get(3*l+1),v.get(3*l+2));
              gl.glEnd();
            }
          }
        }

      }

    }

    //picked Plane
    if(vconf.isSelectionPlaneMode && rw.sq.isFull3() ){
      float[][]pos= rw.sq.pos;

      //normal vector
      float[] dr1=new float[3];
      float[] dr2=new float[3];
      for(int i=0;i<3;i++){
        dr1[i]=pos[1][i]-pos[0][i];
        dr2[i]=pos[2][i]-pos[0][i];
      }
      float[] nr=new float[3];
      nr[0]=dr1[1]*dr2[2]-dr1[2]*dr2[1];
      nr[1]=dr1[2]*dr2[0]-dr1[0]*dr2[2];
      nr[2]=dr1[0]*dr2[1]-dr1[1]*dr2[0];
      float r=(float)Math.sqrt(nr[0]*nr[0]+nr[1]*nr[1]+nr[2]*nr[2]);
      nr[0]/=r;
      nr[1]/=r;
      nr[2]/=r;

      ArrayList<Float> vertex= makePlane(nr,pos[0]);
      gl.glBegin( GL2.GL_POLYGON );
      gl.glColor4fv(vconf.singlePlaneColor,0);
      for(int j=0;j<vertex.size()/3;j++){
        gl.glVertex3f(vertex.get(3*j),
                      vertex.get(3*j+1),
                      vertex.get(3*j+2));
      }
      gl.glEnd();

    }


    //plane
    for(int i=0;i<Const.PLANE;i++){
      if(vconf.isPlaneVisible[i]){
        gl.glBegin( GL2.GL_POLYGON );
        gl.glColor4fv(vconf.planeColor[i],0);

        float n[]=new float[3];
        float p[]=new float[3];
        for(int j=0;j<3;j++){
          n[j]=0.f;
          p[j]=0.f;
          for(int k=0;k<3;k++){
            n[j]+=atoms.h[j][k]*vconf.planeNormal[i][k];
            p[j]+=atoms.h[j][k]*vconf.planePoint[i][k];
          }
        }
        ArrayList<Float> vertex= makePlane(n,p);

        for(int j=0;j<vertex.size()/3;j++){
          gl.glVertex3f(vertex.get(3*j),
                        vertex.get(3*j+1),
                        vertex.get(3*j+2));
        }
        gl.glEnd();
      }
    }//end i
    gl.glEndList();
  }

  /**
   * 法線ベクトルのi2成分はゼロではない．
   */
  private int i0,i1,i2;
  /**
   * display list
   */
  private int plane_t=Const.DISPLAY_LIST_EMPTY;

  /**
   * 平面とboxの交点を返す．
   */
  private ArrayList<Float> makePlane(float[] normalvec,float[] pointvec){
    //determine normal vector
    if(normalvec[2]!=0.f){
      i0=0;
      i1=1;
      i2=2;
    }else if(normalvec[1]!=0.f && normalvec[2]==0.f){
      i0=2;
      i1=0;
      i2=1;
    }else if(normalvec[0]!=0.f && normalvec[1]==0.f && normalvec[2]==0.f){
      i0=1;
      i1=2;
      i2=0;
    }else{
      System.out.println("error in Miller plane");
    }

    ArrayList<Float> tmpV= new ArrayList<Float>();
    float sp[] = new float[3];
    float tp[] = new float[3];
    sp[i0] = 0f;
    sp[i1] = 0.f;
    sp[i2] = 0.f;
    tp = chgScale( atoms.h,sp );
    getPoint(normalvec, tp, pointvec,tmpV);
    sp[i0] = 1.f;
    sp[i1] = 0.f;
    sp[i2] = 0.f;
    tp = chgScale( atoms.h,sp );
    getPoint(normalvec, tp, pointvec,tmpV);
    sp[i0] = 1.f;
    sp[i1] = 1.f;
    sp[i2] = 0.f;
    tp = chgScale( atoms.h,sp );
    getPoint(normalvec, tp, pointvec,tmpV);
    sp[i0] = 0.f;
    sp[i1] = 1.f;
    sp[i2] = 0.f;
    tp = chgScale( atoms.h,sp );
    getPoint(normalvec, tp, pointvec,tmpV);


    return deleteDuplicationAndSortPoints(tmpV);
  }


  /**
   * calculate primitive vector for VolumeSlicer
   */
  public float[][] getPlanePrimitiveVector(float[] normalvec,float[] pointvec){
    ArrayList<Float> vertex= makePlane(normalvec,pointvec);

    float[][] pVec=new float[2][3];
    float r0=0.f,r1=0.f;
    for(int j=0;j<3;j++){
      pVec[0][j]=vertex.get(3*0+j)-vertex.get(3*1+j);
      pVec[1][j]=vertex.get(3*2+j)-vertex.get(3*1+j);

      r0+=pVec[0][j]*pVec[0][j];
      r1+=pVec[1][j]*pVec[1][j];
    }
    for(int j=0;j<3;j++){
      pVec[0][j]/=Math.sqrt(r0);
      pVec[1][j]/=Math.sqrt(r1);
    }
    return pVec;
  }

  /**
   * 反時計回りかどうか．
   */
  private float isCounterClockWise(float[] nr,
                                   float pix,float piy,float piz,
                                   float pjx,float pjy,float pjz,
                                   float pkx,float pky,float pkz){
    float[] a=new float[3];
    float[] b=new float[3];
    float[] c=new float[3];

    a[0]=pjx-pix;
    a[1]=pjy-piy;
    a[2]=pjz-piz;
    b[0]=pkx-pix;
    b[1]=pky-piy;
    b[2]=pkz-piz;

    c[0]=a[1]*b[2]-a[2]*b[1];
    c[1]=a[2]*b[0]-a[0]*b[2];
    c[2]=a[0]*b[1]-a[1]*b[0];
    return nr[0]*c[0]+nr[1]*c[1]+nr[2]*c[2];
  }

  /**
   * 重複を消して，反時計回りにソート
   */
  private ArrayList<Float> deleteDuplicationAndSortPoints(ArrayList<Float> v){

    ArrayList<Float> newV= new ArrayList<Float>();

    for(int i=0;i<v.size()/3;i++){
      float x=v.get(3*i);
      float y=v.get(3*i+1);
      float z=v.get(3*i+2);
      boolean isDuplicated=false;
      for(int j=0;j<newV.size()/3;j++){
        if(Math.abs(x-newV.get(3*j  ))<SMALL &&
           Math.abs(y-newV.get(3*j+1))<SMALL &&
           Math.abs(z-newV.get(3*j+2))<SMALL){
          isDuplicated=true;
          break;
        }
      }//j
      if(!isDuplicated){
        newV.add(x);
        newV.add(y);
        newV.add(z);
      }
    }//i


    return sortPoints(newV);

  }


  /**
   * sort points in counter clockwise
   */
  private ArrayList<Float> sortPoints(ArrayList<Float> v){
    int nv=v.size()/3;
    if(nv<=3)return v;

    //normal vector
    float[] dr1=new float[3];
    float[] dr2=new float[3];
    for(int i=0;i<3;i++){
      dr1[i]=v.get(3*0+i)-v.get(3*1+i);
      dr2[i]=v.get(3*2+i)-v.get(3*1+i);
    }
    float[] nr=new float[3];
    nr[0]=dr1[1]*dr2[2]-dr1[2]*dr2[1];
    nr[1]=dr1[2]*dr2[0]-dr1[0]*dr2[2];
    nr[2]=dr1[0]*dr2[1]-dr1[1]*dr2[0];
    float r=(float)Math.sqrt(nr[0]*nr[0]+nr[1]*nr[1]+nr[2]*nr[2]);
    nr[0]/=r;
    nr[1]/=r;
    nr[2]/=r;

    //queue
    ArrayList<Integer> q= new ArrayList<Integer>();
    //start point is 0
    q.add(0);
    for(int ii=0;ii<nv;ii++){
      int i=q.get(ii);
      float xi=v.get(3*i  );
      float yi=v.get(3*i+1);
      float zi=v.get(3*i+2);
      for(int j=0;j<nv;j++){
        if(i==j)continue;
        float xj=v.get(3*j  );
        float yj=v.get(3*j+1);
        float zj=v.get(3*j+2);

        boolean jflag=true;
        for(int k=0;k<nv;k++){
          if(k==i || k==j)continue;
          //if i-j-k is clockwise order, exit k-loop
          if(isCounterClockWise(nr,xi,yi,zi,xj,yj,zj,
                                v.get(3*k),v.get(3*k+1),v.get(3*k+2))<=0){
            jflag=false;
            break;
          }
        }//k-loop
        //if jflag is on, add queue and exit j-loop
        if(jflag){
          q.add(j);
          break;
        }
      }//j-loop
    }//ii

    ArrayList<Float> sortedV= new ArrayList<Float>();
    for(int i=0;i<q.size();i++){
      int j=q.get(i);
      sortedV.add(v.get(3*j));
      sortedV.add(v.get(3*j+1));
      sortedV.add(v.get(3*j+2));
    }
    return sortedV;
  }


  /*
   * public void getPoint0( float[] normal, float[] tp, float[] point,ArrayList<Float> tmpV){
   *   tp[i2] = (ip(normal,point) -normal[i0]*tp[i0]-normal[i1]*tp[i1])/normal[i2];
   *   tmpV.add(tp[0]);
   *   tmpV.add(tp[1]);
   *   tmpV.add(tp[2]);
   * }
   * public void getPoint1( float[] normal, float[] tp, float[] point,ArrayList<Float> tmpV){
   *   tp[i2] = (normal[0]*point[0]+normal[1]*point[1]+normal[2]*point[2]
   *             -normal[i0]*tp[i0]-normal[i1]*tp[i1])/normal[i2];
   *
   *   if(tp[i2]>atoms.h[i2][i2]){
   *     tp[i2] = atoms.h[i2][i2]+atoms.h[i2][i1];
   *     tp[i1] = atoms.h[i1][i2]+atoms.h[i1][i1];
   *     tp[i0] = (normal[0]*point[0]+normal[1]*point[1]+normal[2]*point[2]
   *               -normal[i2]*tp[i2]-normal[i1]*tp[i1])/normal[i0];
   *     if(tp[i0]<atoms.h[i0][i0] && tp[i0]>0.f){
   *       tmpV.add(tp[0]);tmpV.add(tp[1]);tmpV.add(tp[2]);
   *     }
   *
   *     tp[i2] = atoms.h[i2][i2];
   *     tp[i1] = atoms.h[i1][i2];
   *     tp[i0] = (normal[0]*point[0]+normal[1]*point[1]+normal[2]*point[2]
   *               -normal[i2]*tp[i2]-normal[i1]*tp[i1])/normal[i0];
   *     if(tp[i0]<atoms.h[i0][i0] && tp[i0]>0.f){
   *       tmpV.add(tp[0]);tmpV.add(tp[1]);tmpV.add(tp[2]);
   *     }
   *
   *     tp[i2] = atoms.h[i2][i2]+atoms.h[i2][i0];
   *     tp[i0] = atoms.h[i0][i2]+atoms.h[i0][i0];
   *     tp[i1] = (normal[0]*point[0]+normal[2]*point[2]+normal[1]*point[1]
   *               -normal[i0]*tp[i0]-normal[i2]*tp[i2])/normal[i1];
   *     if(tp[i1]<atoms.h[i1][i1] && tp[i1]>0.f){
   *       tmpV.add(tp[0]);tmpV.add(tp[1]);tmpV.add(tp[2]);
   *     }
   *     tp[i2] = atoms.h[i2][i2];
   *     tp[i0] = atoms.h[i0][i2];
   *     tp[i1] = (normal[0]*point[0]+normal[2]*point[2]+normal[1]*point[1]
   *               -normal[i0]*tp[i0]-normal[i2]*tp[i2])/normal[i1];
   *     if(tp[i1]<atoms.h[i1][i1] && tp[i1]>0.f){
   *       tmpV.add(tp[0]);tmpV.add(tp[1]);tmpV.add(tp[2]);
   *     }
   *   }else if(tp[i2]<0.f){
   *     tp[i2] = 0.f;
   *     tp[i1] = atoms.h[i1][i1];
   *     tp[i0] = (normal[0]*point[0]+normal[1]*point[1]+normal[2]*point[2]
   *               -normal[i2]*tp[i2]-normal[i1]*tp[i1])/normal[i0];
   *     if(tp[i0]<atoms.h[i0][i0] && tp[i0]>0.f){
   *       tmpV.add(tp[0]); tmpV.add(tp[1]); tmpV.add(tp[2]);
   *     }
   *     tp[i2] = 0.f;
   *     tp[i1] = 0.f;
   *     tp[i0] = (normal[0]*point[0]+normal[1]*point[1]+normal[2]*point[2]
   *               -normal[i2]*tp[i2]-normal[i1]*tp[i1])/normal[i0];
   *     if(tp[i0]<atoms.h[i0][i0] && tp[i0]>0.f){
   *       tmpV.add(tp[0]); tmpV.add(tp[1]); tmpV.add(tp[2]);
   *     }
   *     tp[i2] = 0.f;
   *     tp[i0] = atoms.h[i0][i0];
   *     tp[i1] = (normal[0]*point[0]+normal[2]*point[2]+normal[1]*point[1]
   *               -normal[i0]*tp[i0]-normal[i2]*tp[i2])/normal[i1];
   *     if(tp[i1]<atoms.h[i1][i1] && tp[i1]>0.f){
   *       tmpV.add(tp[0]); tmpV.add(tp[1]); tmpV.add(tp[2]);
   *     }
   *     tp[i2] = 0.f;
   *     tp[i0] = 0.f;
   *     tp[i1] = (normal[0]*point[0]+normal[2]*point[2]+normal[1]*point[1]
   *               -normal[i0]*tp[i0]-normal[i2]*tp[i2])/normal[i1];
   *     if(tp[i1]<atoms.h[i1][i1] && tp[i1]>0.f){
   *       tmpV.add(tp[0]); tmpV.add(tp[1]); tmpV.add(tp[2]);
   *     }
   *   }else{
   *     tmpV.add(tp[0]); tmpV.add(tp[1]); tmpV.add(tp[2]);
   *   }
   * }
   */

  /**
   * inner product
   */
  float ip(float[] n, float[] p){
    return n[0]*p[0]+n[1]*p[1]+n[2]*p[2];
  }

  /**
   * boxと交差する点を返す
   */
  public void getPoint( float[] normal, float[] tp, float[] point,ArrayList<Float> tmpV){


    tp[i2] = (ip(normal,point) -normal[i0]*tp[i0]-normal[i1]*tp[i1])/normal[i2];

    float[] v1=new float[3];
    float[] v2=new float[3];
    float t,tmp;
    if(tp[i2]>atoms.h[i2][i2]){
      ///// up 1
      v1[0]=atoms.h[0][i2];
      v1[1]=atoms.h[1][i2];
      v1[2]=atoms.h[2][i2];
      v2[0]=atoms.h[0][i1]+atoms.h[0][i2];
      v2[1]=atoms.h[1][i1]+atoms.h[1][i2];
      v2[2]=atoms.h[2][i1]+atoms.h[2][i2];
      tmp=ip(normal,v1)-ip(normal,v2);
      if(tmp!=0.f){
        t=(ip(normal,point)-ip(normal,v2))/tmp;
        if(0.f<=t && t<=1.f){
          tp[i0] = t*v1[i0]+(1-t)*v2[i0];
          tp[i1] = t*v1[i1]+(1-t)*v2[i1];
          tp[i2] = t*v1[i2]+(1-t)*v2[i2];
          tmpV.add(tp[0]);
          tmpV.add(tp[1]);
          tmpV.add(tp[2]);
        }
      }

      ///// up 2
      v1[0]=atoms.h[0][i1]+atoms.h[0][i2];
      v1[1]=atoms.h[1][i1]+atoms.h[1][i2];
      v1[2]=atoms.h[2][i1]+atoms.h[2][i2];
      v2[0]=atoms.h[0][i0]+atoms.h[0][i1]+atoms.h[0][i2];
      v2[1]=atoms.h[1][i0]+atoms.h[1][i1]+atoms.h[1][i2];
      v2[2]=atoms.h[2][i0]+atoms.h[2][i1]+atoms.h[2][i2];
      tmp=ip(normal,v1)-ip(normal,v2);
      if(tmp!=0.f){
        t=(ip(normal,point)-ip(normal,v2))/tmp;
        if(0.f<=t && t<=1.f){
          tp[i0] = t*v1[i0]+(1-t)*v2[i0];
          tp[i1] = t*v1[i1]+(1-t)*v2[i1];
          tp[i2] = t*v1[i2]+(1-t)*v2[i2];
          tmpV.add(tp[0]);
          tmpV.add(tp[1]);
          tmpV.add(tp[2]);
        }
      }

      //up 3
      v1[0]=atoms.h[0][i0]+atoms.h[0][i1]+atoms.h[0][i2];
      v1[1]=atoms.h[1][i0]+atoms.h[1][i1]+atoms.h[1][i2];
      v1[2]=atoms.h[2][i0]+atoms.h[2][i1]+atoms.h[2][i2];
      v2[0]=atoms.h[0][i0]+atoms.h[0][i2];
      v2[1]=atoms.h[1][i0]+atoms.h[1][i2];
      v2[2]=atoms.h[2][i0]+atoms.h[2][i2];
      tmp=ip(normal,v1)-ip(normal,v2);
      if(tmp!=0.f){
        t=(ip(normal,point)-ip(normal,v2))/tmp;
        if(0.f<=t && t<=1.f){
          tp[i0] = t*v1[i0]+(1-t)*v2[i0];
          tp[i1] = t*v1[i1]+(1-t)*v2[i1];
          tp[i2] = t*v1[i2]+(1-t)*v2[i2];
          tmpV.add(tp[0]);
          tmpV.add(tp[1]);
          tmpV.add(tp[2]);
        }
      }

      // up 4
      v1[0]=atoms.h[0][i0]+atoms.h[0][i2];
      v1[1]=atoms.h[1][i0]+atoms.h[1][i2];
      v1[2]=atoms.h[2][i0]+atoms.h[2][i2];
      v2[0]=atoms.h[0][i2];
      v2[1]=atoms.h[1][i2];
      v2[2]=atoms.h[2][i2];
      tmp=ip(normal,v1)-ip(normal,v2);
      if(tmp!=0.f){
        t=(ip(normal,point)-ip(normal,v2))/tmp;
        if(0.f<=t && t<=1.f){
          tp[i0] = t*v1[i0]+(1-t)*v2[i0];
          tp[i1] = t*v1[i1]+(1-t)*v2[i1];
          tp[i2] = t*v1[i2]+(1-t)*v2[i2];
          tmpV.add(tp[0]);
          tmpV.add(tp[1]);
          tmpV.add(tp[2]);
        }
      }

    }else if(tp[i2]<0.f){
      ///// down 1
      v1[0]=0.f;
      v1[1]=0.f;
      v1[2]=0.f;
      v2[0]=atoms.h[0][i1];
      v2[1]=atoms.h[1][i1];
      v2[2]=atoms.h[2][i1];
      tmp=ip(normal,v1)-ip(normal,v2);
      if(tmp!=0.f){
        t=(ip(normal,point)-ip(normal,v2))/tmp;
        if(0.f<=t && t<=1.f){
          tp[i0] = t*v1[i0]+(1-t)*v2[i0];
          tp[i1] = t*v1[i1]+(1-t)*v2[i1];
          tp[i2] = t*v1[i2]+(1-t)*v2[i2];
          tmpV.add(tp[0]);
          tmpV.add(tp[1]);
          tmpV.add(tp[2]);
        }
      }
      ///// down 2
      v1[0]=atoms.h[0][i1];
      v1[1]=atoms.h[1][i1];
      v1[2]=atoms.h[2][i1];
      v2[0]=atoms.h[0][i0]+atoms.h[0][i1];
      v2[1]=atoms.h[1][i0]+atoms.h[1][i1];
      v2[2]=atoms.h[2][i0]+atoms.h[2][i1];
      tmp=ip(normal,v1)-ip(normal,v2);
      if(tmp!=0.f){
        t=(ip(normal,point)-ip(normal,v2))/tmp;
        if(0.f<=t && t<=1.f){
          tp[i0] = t*v1[i0]+(1-t)*v2[i0];
          tp[i1] = t*v1[i1]+(1-t)*v2[i1];
          tp[i2] = t*v1[i2]+(1-t)*v2[i2];
          tmpV.add(tp[0]);
          tmpV.add(tp[1]);
          tmpV.add(tp[2]);
        }
      }

      //down 3
      v1[0]=atoms.h[0][i0]+atoms.h[0][i1];
      v1[1]=atoms.h[1][i0]+atoms.h[1][i1];
      v1[2]=atoms.h[2][i0]+atoms.h[2][i1];
      v2[0]=atoms.h[0][i0];
      v2[1]=atoms.h[1][i0];
      v2[2]=atoms.h[2][i0];
      tmp=ip(normal,v1)-ip(normal,v2);
      if(tmp!=0.f){
        t=(ip(normal,point)-ip(normal,v2))/tmp;
        if(0.f<=t && t<=1.f){
          tp[i0] = t*v1[i0]+(1-t)*v2[i0];
          tp[i1] = t*v1[i1]+(1-t)*v2[i1];
          tp[i2] = t*v1[i2]+(1-t)*v2[i2];
          tmpV.add(tp[0]);
          tmpV.add(tp[1]);
          tmpV.add(tp[2]);
        }
      }

      // down 4
      v1[0]=atoms.h[0][i0];
      v1[1]=atoms.h[1][i0];
      v1[2]=atoms.h[2][i0];
      v2[0]=0.f;
      v2[1]=0.f;
      v2[2]=0.f;
      tmp=ip(normal,v1)-ip(normal,v2);
      if(tmp!=0.f){
        t=(ip(normal,point)-ip(normal,v2))/tmp;
        if(0.f<=t && t<=1.f){
          tp[i0] = t*v1[i0]+(1-t)*v2[i0];
          tp[i1] = t*v1[i1]+(1-t)*v2[i1];
          tp[i2] = t*v1[i2]+(1-t)*v2[i2];
          tmpV.add(tp[0]);
          tmpV.add(tp[1]);
          tmpV.add(tp[2]);
        }
      }

    }else{
      System.out.println("ordinary");
      tmpV.add(tp[0]);
      tmpV.add(tp[1]);
      tmpV.add(tp[2]);
    }
  }

  public void show(){
    gl.glDisable( GL2.GL_LIGHTING );
    gl.glCallList( plane_t );
    gl.glEnable( GL2.GL_LIGHTING );
  }

  /**
   * 四面体を作成
   */
  private ArrayList<ArrayList<Float>> makeTetrahedron(int isp,float rcut,
                                                      ArrayList<ArrayList<Integer>> lspr
                                                      ){
    ArrayList<ArrayList<Float>> tetra= new ArrayList<ArrayList<Float>>();
    float rcut2=rcut*rcut;

    for(int i=0;i<atoms.n;i++){
      if(atoms.tag[i]!=isp)continue;
      if(atoms.vtag[i]<0)continue;

      float[] ri=atoms.r[i];
      ArrayList<Integer> iList = lspr.get(i);
      ArrayList<Float> itetra= new ArrayList<Float>();
      for(int jj=0;jj<iList.size();jj++){
        int j= iList.get(jj);// obtain neighbor from lspr
        if(atoms.vtag[j]<0)continue;
        if(i==j)continue;
        float rij=0.f;
        for(int l=0;l<3;l++)rij+=(atoms.r[j][l]-ri[l])*(atoms.r[j][l]-ri[l]);
        if(rij<rcut2){
          itetra.add(atoms.r[j][0]);
          itetra.add(atoms.r[j][1]);
          itetra.add(atoms.r[j][2]);
        }
      }//end of jj
      tetra.add(itetra);
    }//end of i
    return tetra;
  }


  /**
   * ボロノイ多面体を作成
   */
  private ArrayList<ArrayList<Float>> makeVoronoi(int j, ArrayList<Integer> jList){
    HashMap<Integer,ArrayList<Float>> jPolyhedronHash
      = new HashMap<Integer,ArrayList<Float>>();

    ArrayList<ArrayList<Float>> jPolyhedronList= new ArrayList<ArrayList<Float>>();
    if(j<0 || atoms.n<j)return jPolyhedronList;

    double[] xj={atoms.r[j][0],
                 atoms.r[j][1],
                 atoms.r[j][2]};

    //start voronoi: (k,l,m) = j-centered region
    for(int kk=0;kk<jList.size();kk++){
      int k=jList.get(kk);
      double[] rjk ={atoms.r[k][0]-xj[0],
                     atoms.r[k][1]-xj[1],
                     atoms.r[k][2]-xj[2]};
      double[] rjk1={atoms.r[k][0]+xj[0],
                     atoms.r[k][1]+xj[1],
                     atoms.r[k][2]+xj[2]};
      for(int ll=0;ll<jList.size();ll++){
        int l=jList.get(ll);
        if(l==k)continue;
        double[] rjl ={atoms.r[l][0]-xj[0],
                       atoms.r[l][1]-xj[1],
                       atoms.r[l][2]-xj[2]};
        double[] rjl1={atoms.r[l][0]+xj[0],
                       atoms.r[l][1]+xj[1],
                       atoms.r[l][2]+xj[2]};
        for(int mm=0;mm<jList.size();mm++){
          int m=jList.get(mm);
          if(m==l || m==k)continue;
          double[] rjm ={atoms.r[m][0]-xj[0],
                         atoms.r[m][1]-xj[1],
                         atoms.r[m][2]-xj[2]};
          double[] rjm1={atoms.r[m][0]+xj[0],
                         atoms.r[m][1]+xj[1],
                         atoms.r[m][2]+xj[2]};
          //solve A*c=b
          double[][] a=new double[3][3];
          a[0][0]=rjk[0]; a[0][1]=rjk[1]; a[0][2]=rjk[2];
          a[1][0]=rjl[0]; a[1][1]=rjl[1]; a[1][2]=rjl[2];
          a[2][0]=rjm[0]; a[2][1]=rjm[1]; a[2][2]=rjm[2];


          double[][] ainv=new double[3][3];
          if(!Matrix.inv(a,ainv))continue;//if det==0, skip

          double[] b=new double[3];
          b[0]=0.5*innerProduct(rjk[0],rjk[1],rjk[2],
                                rjk1[0],rjk1[1],rjk1[2]);
          b[1]=0.5*innerProduct(rjl[0],rjl[1],rjl[2],
                                rjl1[0],rjl1[1],rjl1[2]);
          b[2]=0.5*innerProduct(rjm[0],rjm[1],rjm[2],
                                rjm1[0],rjm1[1],rjm1[2]);
          //c=A^(-1)*b
          double[] c=new double[3];
          c[0]=ainv[0][0]*b[0]+ainv[0][1]*b[1]+ainv[0][2]*b[2];
          c[1]=ainv[1][0]*b[0]+ainv[1][1]*b[1]+ainv[1][2]*b[2];
          c[2]=ainv[2][0]*b[0]+ainv[2][1]*b[1]+ainv[2][2]*b[2];

          //check
          double[] rcj={c[0]-xj[0],
                        c[1]-xj[1],
                        c[2]-xj[2]};
          double rcj2=innerProduct(rcj[0],rcj[1],rcj[2],
                                   rcj[0],rcj[1],rcj[2]);

          boolean isVoronoi=true;
          for(int ii=0;ii<jList.size();ii++){
            int i=jList.get(ii);
            //for(int i=0;i<atoms.n;i++){
            if(i==j || i==k || i==l || i==m)continue;
            double[] rci={atoms.r[i][0]-c[0],
                          atoms.r[i][1]-c[1],
                          atoms.r[i][2]-c[2]};
            double rci2=innerProduct(rci[0],rci[1],rci[2],
                                     rci[0],rci[1],rci[2]);
            if(rcj2>rci2+SMALL){
              isVoronoi=false;
              break;
            }
          }

          if(isVoronoi){
            pushVoronoiVertex2Hash(k,c,jPolyhedronHash);
            pushVoronoiVertex2Hash(l,c,jPolyhedronHash);
            pushVoronoiVertex2Hash(m,c,jPolyhedronHash);
          }

        }//m
      }//l
    }//k

    for(int ii=0;ii<jList.size();ii++){
      int i=jList.get(ii);
      if( jPolyhedronHash.containsKey(i) )
        jPolyhedronList.add( deleteDuplicationAndSortPoints(jPolyhedronHash.get(i)) );
    }
    return jPolyhedronList;
  }//end makeVoronoi

  /**
   * ボロノイ頂点をhashへ追加
   */
  private void pushVoronoiVertex2Hash(int i,double[] c,HashMap<Integer,ArrayList<Float>> hash){
    if(hash.containsKey(i)){
      ArrayList<Float> tmp= hash.get(i);
      tmp.add((float)c[0]);
      tmp.add((float)c[1]);
      tmp.add((float)c[2]);
      hash.put(i,tmp);
    }else{
      ArrayList<Float> tmp= new ArrayList<Float>();
      tmp.add((float)c[0]);
      tmp.add((float)c[1]);
      tmp.add((float)c[2]);
      hash.put(i,tmp);
    }
  }

  /**
   * 内積
   */
  private double innerProduct(double x1,double y1,double z1,
                              double x2,double y2,double z2){
    return x1*x2+y1*y2+z1*z2;
  }


  /**
   *
   */
  private void makeDelaunay(){
    //import tools.DelaunayTriangle.*;
    Delaunay3D del=new Delaunay3D(0.0, atoms.h[0][0],
                                  0.0, atoms.h[1][1],
                                  0.0, atoms.h[2][2]);
    for(int i=0;i<atoms.n;i++){
      del.insertNode(new double[]{atoms.r[i][0],atoms.r[i][1],atoms.r[i][2]});
    }

    MeshData3D mesh3d=del.getMeshData();
    double node[][]=mesh3d.getNode();
    int elem[][]=mesh3d.getElem();
    float color[]={1.f,0.f,0.f,1.f};

    for(int i=0;i<elem.length;i++){
      for(int j=0;j<elem[i].length;j++){
        int[] f=mesh3d.getFace(i,j);
        //gl.glBegin( GL2.GL_POLYGON );
        gl.glBegin( GL2.GL_LINE_LOOP );
        color[0]=j/(float)elem[i].length;
        gl.glColor4fv( color,0);
        for(int k=0;k<f.length;k++){
          int id=f[k]+8;
          gl.glVertex3dv(node[id],0);
        }//k
        gl.glEnd();
      }//j
    }//i
  }

  /**
   * 変換
   */
  private float[] chgScale(float[][] h, float[] in ){
    float[] out = new float[3];
    for( int k=0; k<3; k++ ){
      out[k] = h[k][0]*in[0] + h[k][1]*in[1] + h[k][2]*in[2];
    }
    return out;
  }

}

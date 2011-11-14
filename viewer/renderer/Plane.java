package viewer.renderer;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;

import com.jogamp.opengl.util.gl2.*;
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

  /**
   * 平面のdisplay listを作成
   */
  public void make(int id){

    if(plane_t!=Const.DISPLAY_LIST_EMPTY)gl.glDeleteLists( plane_t, 1);
    plane_t = gl.glGenLists(1);
    gl.glNewList( plane_t, GL2.GL_COMPILE );

    ArrayList<ArrayList<Integer>> lspr=null;

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
      if(atoms.vtag[i]<0)continue;//skip invisible atom

      float[] ri=atoms.r[i];
      ArrayList<Integer> iList = lspr.get(i);
      ArrayList<Float> itetra= new ArrayList<Float>();
      for(int jj=0;jj<iList.size();jj++){
        int j= iList.get(jj);// obtain neighbor from lspr
        if(atoms.vtag[j]<0)continue;//skip invisible atom
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
   * 内積
   */
  private double innerProduct(double x1,double y1,double z1,
                              double x2,double y2,double z2){
    return x1*x2+y1*y2+z1*z2;
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

package tools.DelaunayTriangle.del3d;

import java.io.Serializable;

/**
 * Tetra
 * <pre>
 * 四面体クラス
 * </pre>
 * @author t.matsuoka
 * @version 0.1
 */
public class Tetra implements Serializable {

  private static final long serialVersionUID = 1L;
  private Vertex3d[] node;
  private Face[] face;
  private Tetra[] tetra;
  private double[] vor;
  private double radius2;
  private double volume;

  /**
   * コンストラクタ
   *
   * @param n1　頂点1
   * @param n2　頂点2
   * @param n3　頂点3
   * @param n4　頂点4
   */
  public Tetra(Vertex3d n1,Vertex3d n2,Vertex3d n3,Vertex3d n4){
    node=new Vertex3d[]{n1,n2,n3,n4};
    face=new Face[4];
    face[0]=new Face(n2,n4,n3);
    face[1]=new Face(n3,n4,n1);
    face[2]=new Face(n4,n2,n1);
    face[3]=new Face(n1,n2,n3);
    tetra=new Tetra[4];
    computeVolume();
    computeSphere();
    if(volume<0)System.err.println(volume);
  }

  /**
   * 頂点のID番号の配列を取得
   *
   * @return ID番号配列（int[]）
   */
  public int[] getNodeID(){
    int[] ret=new int[node.length];
    for(int i=0;i<ret.length;i++){
      ret[i]=node[i].getID();
    }
    return ret;
  }

  /**
   * 指定した頂点から見た面を取得
   *
   * @param arg　頂点
   * @return　面（Face）
   */
  public Face getFaceToViewNode(Vertex3d arg){
    int id=arg.getID();
    for(int i=0;i<node.length;i++){
      if(node[i].getID()==id){
        return face[i];
      }
    }
    return null;
  }

  public double getMinH(){
    double ret=Double.MAX_VALUE;
    for(int i=0;i<face.length;i++){
      double[] v=face[i].getTriEq();
      double h=Math.abs(v[0]*node[i].x+v[1]*node[i].y+v[2]*node[i].z+v[3])/Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
      if(h<ret)ret=h;
    }
    return ret;
  }

  /**
   * 当該IDを持つ頂点を含むか否か判別
   *
   * @param id　頂点のID
   * @return
   */
  public boolean isContain(int id){
    for(int i=0;i<node.length;i++){
      if(id==node[i].getID())return true;
    }
    return false;
  }

  /**
   * 四面体頂点に当該頂点が含まれるか否かを判別
   *
   * @param arg　頂点
   * @return
   */
  public boolean isContain(Vertex3d arg){
    for(int i=0;i<node.length;i++){
      if(arg==node[i])return true;
    }
    return false;
  }

  /**
   * 面のID番号を取得
   *
   * @param f　面
   * @return
   */
  public int getFaceID(Face f){
    for(int i=0;i<face.length;i++){
      if(face[i].equals(f))return i;
    }
    return -1;
  }

  /**
   * 頂点が四面体の外接球に含まれるか判別
   *
   * @param node
   * @return
   */
  public boolean isContainSphere(Vertex3d node){
    double rr=Math.pow(vor[0]-node.x,2)+Math.pow(vor[1]-node.y,2)+Math.pow(vor[2]-node.z,2);
    return (radius2>=rr);
  }

  /**
   * 四面体に頂点を挿入に分割
   *
   * @param newNode
   * @param marginOfError　許容誤差
   * @return　四面体の配列
   */
  public Tetra[] createTetra(Vertex3d newNode,double marginOfError){
    Tetra[] ret=new Tetra[4];
    ret[0]=face[0].createTetra(newNode,marginOfError);
    ret[1]=face[1].createTetra(newNode,marginOfError);
    ret[2]=face[2].createTetra(newNode,marginOfError);
    ret[3]=face[3].createTetra(newNode,marginOfError);
    for(int i=0;i<ret.length;i++){
      for(int j=0;j<tetra.length;j++){
        int id=ret[i].getSharedFace(tetra[j]);
        if(id!=-1)ret[i].tetra[id]=tetra[j];
      }
      for(int j=0;j<ret.length;j++){
        if(i==j)continue;
        int id=ret[i].getSharedFace(ret[j]);
        if(id!=-1)ret[i].tetra[id]=ret[j];
      }
    }
    return ret;
  }

  /**
   * 四面体の体積を取得
   *
   * @return
   */
  public double getVolume(){
    return volume;
  }

  /**
   *  隣接四面体を設定
   *
   * @param id
   * @param t
   */
  public void setTetra3D(int id,Tetra t){
    tetra[id]=t;
    if(t!=null){
      int i=t.getFaceID(face[id]);
      t.tetra[i]=this;
    }
  }

  /**
   * 面を共有する四面体を設定
   *
   * @param t　四面体
   * @return　面のID
   */
  public int getSharedFace(Tetra t){
    if(t==null)return -1;
    for(int i=0;i<face.length;i++){
      for(int j=0;j<t.face.length;j++){
        if(face[i].equals(t.face[j]))return i;
      }
    }
    return -1;
  }

  /**
   * 隣接する四面体を取得
   *
   * @param i　ID
   * @return　隣接四面体
   */
  public Tetra getTetra(int i){
    return tetra[i];
  }

  /**
   * 隣接四面体の配列を取得
   *
   * @return 四面体の配列
   */
  public Tetra[] getTetraArray(){
    return tetra;
  }

  /**
   * ボロノイ中点を取得
   *
   * @return
   */
  public double[] getVoronoiCenter(){
    return vor;
  }

  /**
   * 外接円半径2乗を取得
   *
   * @return
   */
  public double getRadius2(){
    return this.radius2;
  }

  /**
   * 面の配列を取得
   *
   * @return　面の配列
   */
  public Face[] getFace(){
    return face;
  }

  /**
   * 隣接四面体を設定
   *
   * @param p　四面体の配列
   */
  public void setShaeradFaceTetra3D(Tetra[] p){
    for(int i=0;i<p.length;i++){
      if(this==p[i])continue;
      int id=getSharedFace(p[i]);
      if(id!=-1){
        tetra[id]=p[i];
        int ix=p[i].getFaceID(face[id]);
        p[i].tetra[ix]=this;
      }
    }
  }

  private void computeVolume(){
    double va=node[1].x*node[2].y*node[3].z+node[0].x*node[0].y*node[3].z
              +node[1].x*node[0].y*node[0].z+node[0].x*node[2].y*node[0].z
              -(node[1].x*node[2].y*node[0].z+node[0].x*node[0].y*node[0].z
                +node[1].x*node[0].y*node[3].z+node[0].x*node[2].y*node[3].z);
    double vb=node[1].y*node[2].z*node[3].x+node[0].y*node[0].z*node[3].x
              +node[1].y*node[0].z*node[0].x+node[0].y*node[2].z*node[0].x
              -(node[1].y*node[2].z*node[0].x+node[0].y*node[0].z*node[0].x
                +node[1].y*node[0].z*node[3].x+node[0].y*node[2].z*node[3].x);
    double vc=node[1].z*node[2].x*node[3].y+node[0].z*node[0].x*node[3].y
              +node[1].z*node[0].x*node[0].y+node[0].z*node[2].x*node[0].y
              -(node[1].z*node[2].x*node[0].y+node[0].z*node[0].x*node[0].y
               +node[1].z*node[0].x*node[3].y+node[0].z*node[2].x*node[3].y);
    double wa=node[1].x*node[2].z*node[0].y+node[0].x*node[0].z*node[0].y
              +node[1].x*node[0].z*node[3].y+node[0].x*node[2].z*node[3].y
              -(node[1].x*node[2].z*node[3].y+node[0].x*node[0].z*node[3].y
                +node[1].x*node[0].z*node[0].y+node[0].x*node[2].z*node[0].y);
    double wb=node[1].y*node[2].x*node[0].z+node[0].y*node[0].x*node[0].z
              +node[1].y*node[0].x*node[3].z+node[0].y*node[2].x*node[3].z
              -(node[1].y*node[2].x*node[3].z+node[0].y*node[0].x*node[3].z
                +node[1].y*node[0].x*node[0].z+node[0].y*node[2].x*node[0].z);
    double wc=node[1].z*node[2].y*node[0].x+node[0].z*node[0].y*node[0].x
              +node[1].z*node[0].y*node[3].x+node[0].z*node[2].y*node[3].x
              -(node[1].z*node[2].y*node[3].x+node[0].z*node[0].y*node[3].x
                +node[1].z*node[0].y*node[0].x+node[0].z*node[2].y*node[0].x);
    volume=va+vb+vc+wa+wb+wc;
  }

  private void computeSphere(){
    double p11=node[2].y*node[3].z+node[0].y*node[0].z
                +node[3].y*node[0].z+node[0].y*node[2].z
                -(node[2].y*node[0].z+node[0].y*node[3].z
                  +node[3].y*node[2].z+node[0].y*node[0].z);
    double p12=node[3].x*node[2].z+node[0].x*node[0].z
                +node[2].x*node[0].z+node[0].x*node[3].z
                -(node[3].x*node[0].z+node[0].x*node[2].z
                  +node[2].x*node[3].z+node[0].x*node[0].z);
    double p13=node[2].x*node[3].y+node[0].x*node[0].y
                +node[3].x*node[0].y+node[0].x*node[2].y
                -(node[2].x*node[0].y+node[0].x*node[3].y
                  +node[3].x*node[2].y+node[0].x*node[0].y);
    double p21=node[3].y*node[1].z+node[0].y*node[0].z
                +node[1].y*node[0].z+node[0].y*node[3].z
                -(node[3].y*node[0].z+node[0].y*node[1].z
                   +node[1].y*node[3].z+node[0].y*node[0].z);
    double p22=node[1].x*node[3].z+node[0].x*node[0].z
                +node[3].x*node[0].z+node[0].x*node[1].z
                -(node[1].x*node[0].z+node[0].x*node[3].z
                  +node[3].x*node[1].z+node[0].x*node[0].z);
    double p23=node[3].x*node[1].y+node[0].x*node[0].y
                +node[1].x*node[0].y+node[0].x*node[3].y
                -(node[3].x*node[0].y+node[0].x*node[1].y
                  +node[1].x*node[3].y+node[0].x*node[0].y);
    double p31=node[1].y*node[2].z+node[0].y*node[0].z
                +node[2].y*node[0].z+node[0].y*node[1].z
                -(node[1].y*node[0].z+node[0].y*node[2].z
                  +node[2].y*node[1].z+node[0].y*node[0].z);
    double p32=node[2].x*node[1].z+node[0].x*node[0].z
                +node[1].x*node[0].z+node[0].x*node[2].z
                -(node[2].x*node[0].z+node[0].x*node[1].z
                  +node[1].x*node[2].z+node[0].x*node[0].z);
    double p33=node[1].x*node[2].y+node[0].x*node[0].y
                +node[2].x*node[0].y+node[0].x*node[1].y
                -(node[1].x*node[0].y+node[0].x*node[2].y
                  +node[2].x*node[1].y+node[0].x*node[0].y);
    double xyza=node[0].x*node[0].x+node[0].y*node[0].y+node[0].z*node[0].z;
    double aa=0.5*(node[1].x*node[1].x+node[1].y*node[1].y+node[1].z*node[1].z-xyza);
    double bb=0.5*(node[2].x*node[2].x+node[2].y*node[2].y+node[2].z*node[2].z-xyza);
    double cc=0.5*(node[3].x*node[3].x+node[3].y*node[3].y+node[3].z*node[3].z-xyza);
    double xx=p11*aa+p21*bb+p31*cc;
    double yy=p12*aa+p22*bb+p32*cc;
    double zz=p13*aa+p23*bb+p33*cc;
    double xv=xx/(volume);
    double yv=yy/(volume);
    double zv=zz/(volume);
    vor=new double[]{xv,yv,zv};
    radius2=node[0].x*node[0].x+xv*xv+node[0].y*node[0].y+yv*yv
        +node[0].z*node[0].z+zv*zv-2*(node[0].x*xx+node[0].y*yy
        +node[0].z*zz)/(volume);
  }

}

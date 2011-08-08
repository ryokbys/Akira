package tools.DelaunayTriangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tools.DelaunayTriangle.del3d.Face;
import tools.DelaunayTriangle.del3d.Vertex3d;
import tools.DelaunayTriangle.del3d.Tetra;
import tools.DelaunayTriangle.misc.DelaunayException;

/**
 * Delaunay3D
 * <pre>
 * 三次元Delaunay分割処理クラス
 * </pre>
 * @author t.matsuoka
 * @version 0.1
 */
public class Delaunay3D implements Delaunay {

  private static final long serialVersionUID = 1L;
  private double xmin,ymin,zmin,xmax,ymax,zmax;
  private List<Tetra> list;
  private double EPS=1e-14;
  private Vertex3d[] superNode;
  private List<double[]> nodes;
  private List<Vertex3d> vert;

  /**
   * コンストラクタ
   *
   * @param minX　処理対象領域のx座標最小値
   * @param maxX　処理対象領域のx座標最大値
   * @param minY　処理対象領域のy座標最小値
   * @param maxY　処理対象領域のy座標最大値
   * @param minZ　処理対象領域のz座標最小値
   * @param maxZ　処理対象領域のz座標最大値
   */
  public Delaunay3D(double minX,double maxX,double minY,double maxY,double minZ,double maxZ){
    xmin=minX-1;
    xmax=maxX+1;
    ymin=minY-1;
    ymax=maxY+1;
    zmin=minZ-1;
    zmax=maxZ+1;
    list=new ArrayList<Tetra>();
    nodes=new ArrayList<double[]>();
    vert=new ArrayList<Vertex3d>();
    createSuperCube();
  }

  /**
   * コンストラクタ
   *
   * @param area 処理対象領域（x最小座標、x最大座標、y最小座標、y最大座標、z最小座標、z最大座標）
   */
  public Delaunay3D(double[] mn){
    this(mn[0],mn[1],mn[2],mn[3],mn[4],mn[5]);
  }

  /*
   * スーパーノード除去後のTetraクラスのリストを取得
   *
   * @return List<Tetra>
   */
  private final List<Tetra> getTetra3DListRemoveSuperNode(){
    List<Tetra> ret=new ArrayList<Tetra>();
    for(int i=0;i<list.size();i++){
      Tetra t=list.get(i);
      boolean flg=true;
      for(int j=0;j<superNode.length;j++){
        if(t.isContain(superNode[j])){
          flg=false;
          break;
        }
      }
      if(flg)ret.add(t);
    }
    return ret;
  }

  /**
   * メッシュデータを取得
   *
   * @return
   */
  public MeshData3D getMeshData() {
    List<Tetra> tt=getTetra3DListRemoveSuperNode();
    int[][] dd=new int[tt.size()][];
    for(int i=0;i<dd.length;i++){
      dd[i]=tt.get(i).getNodeID();
      for(int j=0;j<dd[i].length;j++){
        dd[i][j] -=8;
      }

    }
    MeshData3D ret=new MeshData3D(
      nodes.toArray(new double[nodes.size()][]),
      dd);
    return ret;
  }

  public boolean insertNode(double[] node){
    Vertex3d val=transXYZ(node);
    Tetra pos=list.get(list.size()-1);
    pos=getLocate(val);
    if(pos!=null){
      vert.add(val);
    }
    Set<Tetra> tmp=pickup(new HashSet<Tetra>(),pos,val);
    Tetra[] t=poly(tmp, val);
    for(int i=0;i<t.length;i++)list.add(t[i]);
    nodes.add(node);
    return true;
  }

  public void addBoundary(double[][] arg, boolean isClose) {
    throw new DelaunayException("Not supported.");
  }

  /*
   * スーパーキューブを生成
   */
  private void createSuperCube(){
    superNode=new Vertex3d[8];
    superNode[0]=new Vertex3d(0.0,0.0,0.0);
    superNode[1]=new Vertex3d(1.0,0.0,0.0);
    superNode[2]=new Vertex3d(1.0,1.0,0.0);
    superNode[3]=new Vertex3d(0.0,1.0,0.0);
    superNode[4]=new Vertex3d(0.0,0.0,1.0);
    superNode[5]=new Vertex3d(1.0,0.0,1.0);
    superNode[6]=new Vertex3d(1.0,1.0,1.0);
    superNode[7]=new Vertex3d(0.0,1.0,1.0);
    for(int i=0;i<superNode.length;i++){
      superNode[i].setID(i);
      vert.add(superNode[i]);
    }
    Tetra[] st=new Tetra[6];
    st[0]=new Tetra(superNode[1],superNode[6],superNode[4],superNode[5]);
    st[1]=new Tetra(superNode[0],superNode[1],superNode[2],superNode[4]);
    st[2]=new Tetra(superNode[1],superNode[2],superNode[4],superNode[6]);
    st[3]=new Tetra(superNode[4],superNode[3],superNode[7],superNode[6]);
    st[4]=new Tetra(superNode[0],superNode[2],superNode[3],superNode[4]);
    st[5]=new Tetra(superNode[3],superNode[2],superNode[6],superNode[4]);
    st[0].setShaeradFaceTetra3D(st);
    st[1].setShaeradFaceTetra3D(st);
    st[2].setShaeradFaceTetra3D(st);
    st[3].setShaeradFaceTetra3D(st);
    st[4].setShaeradFaceTetra3D(st);
    st[5].setShaeradFaceTetra3D(st);
    for(int i=0;i<st.length;i++){
      list.add(st[i]);
    }
  }

  /*
   * 節点(double[])を正規化した頂点座標を取得
   *
   * @param xyz
   * @return Vertex3d 正規化頂点座標
   */
  private Vertex3d transXYZ(double[] xyz){
    double x=(xyz[0]-xmin)/(xmax-xmin);
    double y=(xyz[1]-ymin)/(ymax-ymin);
    double z=(xyz[2]-zmin)/(zmax-zmin);
    Vertex3d ret=new Vertex3d(x,y,z);
    ret.setID(vert.size());
    return ret;
  }

  /*
   * 挿入節点を領域内に含むTetraオブジェクトを取得
   */
  private Tetra getLocate(Vertex3d newNode){
    int dd=0;
    Tetra tetra=list.get(list.size()-1);
    int id=0;
    int count=0;
    Face[] face=tetra.getFace();
    while(count<4){
      if(dd++>10000){
        System.out.println("exit.>10000");
        System.exit(0);
      }
      if(face[id%4].isClockwise(newNode, -EPS)){
        tetra=tetra.getTetra(id%4);
        id=tetra.getFaceID(face[id%4]);
        face=tetra.getFace();
        count=0;
      }else{
        count++;
      }
      id++;
    }
    return tetra;
  }

  /*
   *
   */
  private Set<Tetra> pickup(Set<Tetra> col,Tetra obj,Vertex3d newNode){
    if(!col.add(obj))return col;
    Tetra[] t=obj.getTetraArray();
    for(int i=0;i<t.length;i++){
      if(t[i]==null)continue;
      if(t[i].isContainSphere(newNode)){
        col=pickup(col,t[i],newNode);
      }
    }
    return col;
  }

  /*
   *
   */
  private Tetra[] poly(Set<Tetra> te,Vertex3d newNode){
    List<Tetra> tetra=new ArrayList<Tetra>();
    tetra.addAll(te);
    Set<Face> face=new HashSet<Face>();
    Map<Face,Tetra> map=new HashMap<Face,Tetra>();
    int s=tetra.size();
    for(int k=0;k<s;k++){
      Tetra tmp=tetra.get(k);
      Face[] fs=tmp.getFace();
      Tetra[] tr=tmp.getTetraArray();
      for(int i=0;i<tr.length;i++){
        if(tr[i]==null){
          map.put(fs[i], tr[i]);
          face.add(fs[i]);
        }else{
          if(tetra.contains(tr[i]))continue;
          if(fs[i].isClockwise(newNode, EPS)){
            if(!tetra.contains(tr[i])){
              tetra.add(tr[i]);
              Face[] fx=tr[i].getFace();
              for(int j=0;j<fx.length;j++){
                face.remove(fx[j]);
              }
              s++;
            }
          }else{
            face.add(fs[i]);
            map.put(fs[i],tr[i]);
          }
        }
      }
    }
    list.removeAll(tetra);
    List<Tetra> ret=new ArrayList<Tetra>();
    Iterator<Face> it=face.iterator();
    while(it.hasNext()){
      Face f=it.next();
      Tetra t=f.createTetra(newNode,-EPS);
      t.setTetra3D(3,map.get(f));
      ret.add(t);
    }
    Tetra[] pp=ret.toArray(new Tetra[ret.size()]);
    for(int i=0;i<pp.length;i++){
      pp[i].setShaeradFaceTetra3D(pp);
    }
    return pp;
  }

}

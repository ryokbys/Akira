package tools.DelaunayTriangle.del3d;

import java.io.Serializable;

/**
 * Face
 * <pre>
 * 四面体の面クラス
 * </pre>
 * @author t.matsuoka
 * @version 0.1
 */
public class Face implements Serializable {
  private Vertex3d[] node;
  private String name;
  private int[] ids;

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * コンストラクタ
   *
   * @param a　頂点1
   * @param b　頂点2
   * @param c　頂点3
   */
  public Face(Vertex3d a,Vertex3d b,Vertex3d c){
    node=new Vertex3d[]{a,b,c};
    ids=sort(new int[]{a.getID(),b.getID(),c.getID()});
    name=getHashCodeString(ids);
  }

  /**
   * 頂点のID番号の配列を取得
   *
   * @return ID番号配列（int[]）
   */
  public int[] getNodeID(){
    return new int[]{node[0].getID(),node[1].getID(),node[2].getID()};
  }

  /**
   * argが面を構成する頂点に含まれるか判別
   *
   * @param arg 頂点Vertec3d
   * @return
   */
  public boolean isContain(Vertex3d arg){
    for(int i=0;i<node.length;i++){
      if(node[i]==arg)return true;
    }
    return false;
  }

  public double[] getTriEq(){
    double a=node[0].y*node[1].z+node[1].y*node[2].z+node[2].y*node[0].z-node[0].y*node[2].z-node[1].y*node[0].z-node[2].y*node[1].z;
    double b=node[0].z*node[1].x+node[1].z*node[2].x+node[2].z*node[0].x-node[0].z*node[2].x-node[1].z*node[0].x-node[2].z*node[1].x;
    double c=node[0].x*node[1].y+node[1].x*node[2].y+node[2].x*node[0].y-node[0].x*node[2].y-node[1].x*node[0].y-node[2].x*node[1].y;
    double d=-a*node[0].x-b*node[0].y-c*node[0].z;
    return new double[]{a,b,c,d};
  }

  /**
   * 頂点Vertex3dからみて時計回りか否かを判別
   *
   * @param newNode　頂点
   * @param err　許容誤差
   * @return
   */
  public boolean isClockwise(Vertex3d newNode,double err){
    double a=node[0].y*node[1].z+node[1].y*node[2].z+node[2].y*node[0].z-(node[0].y*node[2].z+node[1].y*node[0].z+node[2].y*node[1].z);
    double b=node[0].z*node[1].x+node[1].z*node[2].x+node[2].z*node[0].x-(node[0].z*node[2].x+node[1].z*node[0].x+node[2].z*node[1].x);
    double c=node[0].x*node[1].y+node[1].x*node[2].y+node[2].x*node[0].y-(node[0].x*node[2].y+node[1].x*node[0].y+node[2].x*node[1].y);
    double d=-(a*node[0].x+b*node[0].y+c*node[0].z);
    double val=(a*newNode.x+b*newNode.y+c*newNode.z+d);
    return (val<err);
  }

  /**
   * 面と頂点から四面体を生成
   *
   * @param newNode 頂点
   * @param marginOfError 許容誤差
   * @return
   */
  public Tetra createTetra(Vertex3d newNode,double marginOfError){
    if(isClockwise(newNode,marginOfError)){
      return new Tetra(node[0],node[2],node[1],newNode);
    }else{
      return new Tetra(node[0],node[1],node[2],newNode);
    }
  }

  private static int[] sort(int[] a){
    if(a[0]>a[1]){
      int[] t={a[1],a[0],a[2]};
      return sort(t);
    }
    if(a[1]>a[2]){
      int[] t={a[0],a[2],a[1]};
      return sort(t);
    }
    return a;
  }

  public String getName(){
    return name;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  private String getHashCodeString(int[] ids) {
    return Integer.toString(ids[0])+"_"+Integer.toString(ids[1])+"_"+Integer.toString(ids[2]);
  }

  @Override
  public boolean equals(Object arg0) {
    if(arg0 instanceof Face){
      int[] idd=((Face)arg0).ids;
      return (ids[0]==idd[0]&&ids[1]==idd[1]&&ids[2]==idd[2]);
    }else{
      return false;
    }
  }

}

package tools.DelaunayTriangle;

import java.awt.geom.Rectangle2D;
/**
 * MeshData3D
 * <pre>
 * 三次元メッシュ（四面体格子）クラス
 * </pre>
 * @author t.matsuoka
 * @version 0.1
 */
public class MeshData3D{

  private static final long serialVersionUID = 1L;
  private double[][] node;
  private int[][] tetra;
  private double[] minmax;

  /**
   * コンストラクタ
   *
   * @param d　節点配列
   * @param t　四面体情報配列
   */
  MeshData3D(double[][] d,int[][] t){
    node=d;
    tetra=t;
  }

  /**
   * 領域の最小・最大値を取得
   *
   * @return double[]{xmin,ymax,ymin,ymax,zmin,zmax}
   */
  public double[] getMinMax() {
    return minmax;
  }

  void setMinMax(double[] minmax) {
    this.minmax = minmax;
  }

  /**
   * 節点配列を取得
   *
   * @return 節点配列(double[][])
   */
  public double[][] getNode() {
    return node;
  }

  /**
   * 四面体情報を取得
   *
   * @return 四面体情報配列(int[][])
   */
  public int[][] getElem(){
    return tetra;
  }

  /**
   * i番目の四面体のid番目の面（三角形）の節点情報を取得
   *
   * @param tid
   * @param id
   * @return
   */
  public  int[] getFace(int tid,int id){
    id=id%4;
    switch(id){
      case 0:
        return new int[]{tetra[tid][1]-8,tetra[tid][3]-8,tetra[tid][2]-8};
      case 1:
        return new int[]{tetra[tid][2]-8,tetra[tid][3]-8,tetra[tid][0]-8};
      case 2:
        return new int[]{tetra[tid][3]-8,tetra[tid][1]-8,tetra[tid][0]-8};
      default:
        return new int[]{tetra[tid][0]-8,tetra[tid][1]-8,tetra[tid][2]-8};
    }
  }

  /**
   * 節点配列のcol列のデータを配列で取得
   *
   * @param col 節点配列の列インデックス
   * @return データの配列
   */
  public double[] getMinMax(int col){
    double max=-Double.MAX_VALUE;
    double min=Double.MAX_VALUE;
    for(int i=0;i<node.length;i++){
      if(max<node[i][col])max=node[i][col];
      if(min>node[i][col])min=node[i][col];
    }
    return new double[]{min,max};
  }
/*
  public int[][] getBoundary() {
    return new int[0][0];
  }
*/
  /**
   * メッシュの処理対象xy平面領域を取得
   *
   * @return Rectangle2D
   */
  public Rectangle2D getBounds() {
    double[] mx=getMinMax(0);
    double[] my=getMinMax(1);
    return new Rectangle2D.Double(mx[0],my[0],mx[1]-mx[0],my[1]-my[0]);
  }
}

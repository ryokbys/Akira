package tools.DelaunayTriangle;

import java.awt.geom.Rectangle2D;

/**
 * MeshData2D
 * <pre>
 * 二次元メッシュ（非構造格子）クラス
 * </pre>
 * @author t.matsuoka
 * @version 0.1
 */
public class MeshData2D{

  private static final long serialVersionUID = 1L;
  private double[][] node;
  private int[][] elem;
  private int[][] neigh;
  private int[][] boundary;
  private static final double EPS=1e-12;
  private double[] minMax;

  /**
   * コンストラクタ
   *
   * @param d 節点データ double[][]
   * @param e 要素データ int[][]
   * @param m 隣接要素データ int[][]
   * @param b 境界区分データ int[][]
   */
  MeshData2D(double[][] d,int[][] e,int[][] m,int[][] b){
    node=d;
    elem=e;
    neigh=m;
    boundary=b;
  }

  /**
   * メッシュ領域の最小値,最大値を取得
   *
   * @return
   */
  public double[] getMinMax() {
    return minMax;
  }

  void setMinMax(double[] minMax) {
    this.minMax = minMax;
  }

  /**
   * 節点データを取得
   *
   * @return double[][]
   */
  public double[][] getNode() {
    return node;
  }

  void setNode(double[][] data) {
    this.node = data;
  }

  /**
   * 要素データを取得
   *
   * @return int[][]
   */
  public int[][] getElem() {
    return elem;
  }

  void setElem(int[][] elem) {
    this.elem = elem;
  }

  /**
   * 隣接要素データを取得
   *
   * @return int[][]
   */
  public int[][] getMap() {
    return neigh;
  }

  void setMap(int[][] map) {
    this.neigh = map;
  }

  /**
   * 境界区分データを取得
   *
   * @return int[][]
   */
  public int[][] getBoundary() {
    return boundary;
  }

  void setBoundary(int[][] boundary) {
    this.boundary = boundary;
  }

  /**
   * i番目の要素の面積を取得
   *
   * @param id 要素のID番号
   * @return 面積
   */
  public double getTriArea(int id){
    double a=node[elem[id][0]][0]*node[elem[id][1]][1];
    double b=node[elem[id][1]][0]*node[elem[id][2]][1];
    double c=node[elem[id][2]][0]*node[elem[id][0]][1];
    double d=node[elem[id][0]][0]*node[elem[id][2]][1];
    double e=node[elem[id][1]][0]*node[elem[id][0]][1];
    double f=node[elem[id][2]][0]*node[elem[id][1]][1];
    return 0.5*(a+b+c-d-e-f);
  }

  /**
   * 要素面積の配列を取得する。
   *
   * @return double[]
   */
  public double[] getTriAreas(){
    double[] ret=new double[elem.length];
    for(int i=0;i<ret.length;i++){
      ret[i]=getTriArea(i);
    }
    return ret;
  }


  /**
   * 要素の頂角を取得
   *
   * @param elemId 要素番号
   * @param vertId 頂点番号
   * @return 頂角
   */
  public double getTheta(int elemId,int vertId){
    double x0=node[elem[elemId][vertId]][0];
    double y0=node[elem[elemId][vertId]][1];
    double x1=node[elem[elemId][(vertId+2)%3]][0];
    double y1=node[elem[elemId][(vertId+2)%3]][1];
    double x2=node[elem[elemId][(vertId+1)%3]][0];
    double y2=node[elem[elemId][(vertId+1)%3]][0];
    double xa=x1-x0;
    double ya=y1-y0;
    double xb=x2-x0;
    double yb=y2-y0;
    double prdin=xa*xb+ya*ya;
    double prdex=xa*yb-xb*ya;
    double theta=0;
    if(Math.abs(prdin)<EPS){
      theta=Math.PI/2.0;
    }else{
      theta=Math.tan(prdex/prdin);
      if(theta<0){
        theta +=Math.PI;
      }else if(theta>Math.PI){
        theta -=Math.PI;
      }
    }
    return theta;
  }

  /**
   * 要素の頂角の配列を取得
   *
   * @param elemId 要素番号
   * @return double[]
   */
  public double[] getTheta(int elemId){
    double[] ret=new double[3];
    for(int i=0;i<ret.length;i++){
      ret[i]=getTheta(elemId,i);
    }
    return ret;
  }

  /**
   * 全要素の頂角の配列を取得
   *
   * @return double[][]
   */
  public double[][] getThetas(){
    double[][] ret=new double[elem.length][];
    for(int i=0;i<ret.length;i++){
      ret[i]=getTheta(i);
    }
    return ret;
  }

  /**
   * 辺の長さを取得
   *
   * @param elemId 要素番号
   * @param vertId 頂点番号
   * @return
   */
  public double getEdgeLength(int elemId,int vertId){
    double x0=node[elem[elemId][vertId]][0];
    double y0=node[elem[elemId][vertId]][1];
    double x1=node[elem[elemId][(vertId+1)%3]][0];
    double y1=node[elem[elemId][(vertId+1)%3]][0];
    double xx=(x1-x0)*(x1-x0);
    double yy=(y1-y0)*(y1-y0);
    return Math.sqrt(xx+yy);
  }

  /**
   * 辺の長さの配列を取得
   *
   * @param elemId 要素番号
   * @return double[][]
   */
  public double[] getEdgeLength(int elemId){
    double[] ret=new double[3];
    for(int i=0;i<ret.length;i++){
      ret[i]=getEdgeLength(elemId,i);
    }
    return ret;
  }

  /**
   * 辺の長さの配列を取得
   *
   * @return double[][]
   */
  public double[][] getEdgeLengths(){
    double[][] ret=new double[elem.length][];
    for(int i=0;i<ret.length;i++){
      ret[i]=getEdgeLength(i);
    }
    return ret;
  }

  /**
   * elemID番の要素の中点を取得
   *
   * @param elemId 要素番号
   * @return double[]
   */
  public double[] getTriCenter(int elemId){
    double x0=node[elem[elemId][0]][0];
    double y0=node[elem[elemId][0]][1];
    double x1=node[elem[elemId][1]][0];
    double y1=node[elem[elemId][1]][1];
    double x2=node[elem[elemId][2]][0];
    double y2=node[elem[elemId][2]][0];
    double xx=(x0+x1+x2)/3;
    double yy=(y0+y1+y2)/3;
    return new double[]{xx,yy};
  }

  /**
   * メッシュの処理対象領域を取得
   *
   * @return Rectangle2D
   */
  public Rectangle2D getBounds() {
    return new Rectangle2D.Double(minMax[0],minMax[2],minMax[1]-minMax[0],minMax[3]-minMax[2]);
  }
}

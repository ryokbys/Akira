package tools.DelaunayTriangle.del3d;

/**
 * Vertex3d
 * <pre>
 * 四面体の頂点クラス
 * </pre>
 * @author t.matsuoka
 * @version 0.1
 */
public class Vertex3d{
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private int id;
  /**
   * 座標
   */
  public double x,y,z;

  /**
   * コンストラクタ
   */
  public Vertex3d() {
    x=0.0;
    y=0.0;
    z=0.0;
  }

  /**
   * コンストラクタ
   *
   * @param arg0　x座標
   * @param arg1　y座標
   * @param arg2　z座標
   */
  public Vertex3d(double arg0, double arg1, double arg2) {
    x=arg0;
    y=arg1;
    z=arg2;
  }

  /**
   * コンストラクタ
   *
   * @param arg0　座標の配列
   */
  public Vertex3d(double[] arg0) {
    x=arg0[0];
    y=arg0[1];
    z=arg0[2];
  }

  /**
   * コンストラクタ
   *
   * @param arg0　頂点
   */
  public Vertex3d(Vertex3d arg0) {
    this.x=arg0.x;
    this.y=arg0.y;
    this.z=arg0.z;
  }

  /**
   * 頂点のID番号を設定
   *
   * @param i
   */
  public void setID(int i){
    id=i;
  }

  /**
   * 頂点のID番号を取得
   *
   * @return
   */
  public int getID(){
    return id;
  }

}

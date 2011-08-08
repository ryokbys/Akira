package tools.DelaunayTriangle;

/**
 * Delaunay
 * <pre>
 * Delaunay分割処理クラスのインターフェイス
 * </pre>
 * @author t.matsuoka
 * @version 0.1
 */
public interface Delaunay {

  /**
   * 節点の挿入
   *
   * @param node 節点（double[]）
   * @return 節点挿入の成否
   */
  public boolean insertNode(double[] node);

  /**
   * 境界の挿入
   *
   * @param arg 境界節点の配列(double[][])
   * @param isClose 最初と最後の節点を境界でつなぐか否か
   */
  public void addBoundary(double[][] arg, boolean isClose);
}

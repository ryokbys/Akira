package tools.DelaunayTriangle.voxel;

/**
 * Voxel
 * <pre>
 * Voxelのインターフェイス
 * </pre>
 * @author t.matsuoka
 * @version 0.1
 */
public interface Voxel {
  /**
   * ボクセル格子データを取得
   *
   * @return
   */
  public double[][][] getVoxel();

  /**
   * ボクセル展開方向のベクトルを取得
   *
   * @return
   */
  public double[] getVoxelVector();

  /**
   * ボクセル格子の基準点を取得
   *
   * @return
   */
  public double[] getVoxelOrigin();
}

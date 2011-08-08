package tools.DelaunayTriangle.event;

/**
 * DelaunayEvent
 * <pre>
 * Delaunay分割のイベントリスナー
 * </pre>
 * @author t.matsuoka
 * @version 0.1
 */
public interface DelaunayListener {
  /**
   * イベント処理
   *
   * @param evt イベントオブジェクト
   */
  public void delaunayEvenet(DelaunayEvent evt);
}

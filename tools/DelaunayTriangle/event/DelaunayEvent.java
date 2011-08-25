package tools.DelaunayTriangle.event;

import java.awt.event.ActionEvent;

/**
 * DelaunayEvent
 * <pre>
 * Delaunay分割のイベントクラス
 * </pre>
 * @author t.matsuoka
 * @version 0.1
 */
public class DelaunayEvent extends ActionEvent {

  private static final long serialVersionUID = 1L;

  public enum Status{GENE_TRIANGLE,DIVIDE_TRIANGLE,DIVIDE_BOUNDARY,PROC_LAPLAS,
      COMPLETE,GENE_QUAD};

  private float progress;
  private Status status;

  /**
   * コンストラクタ
   *
   * @param arg0
   * @param st
   */
  public DelaunayEvent(Object arg0, Status st) {
    super(arg0, 0, getCommand(st));
    status=st;
    progress=0;
  }

  /**
   *
   *
   * @param st　ステータス
   * @return
   */
  private static String getCommand(Status st){
    switch(st){
    case GENE_TRIANGLE:
      return "Delaunay trianguration.";
    case DIVIDE_BOUNDARY:
      return "Boundary subdivision.";
    case DIVIDE_TRIANGLE:
      return "Triangle subdivision.";
    case PROC_LAPLAS:
      return "Adjustment by Laplas Method.";
    case GENE_QUAD:
      return "Quad generation.";
    default:
      return "Complete process.";
    }
  }

  /**
   * コンストラクタ
   *
   * @param arg0
   * @param arg1
   * @param arg2
   */
  public DelaunayEvent(Object arg0, int arg1, String arg2) {
    super(arg0, arg1, arg2);
  }

  /**
   * 進捗値を取得
   *
   * @return
   */
  public float getProgress(){
    return progress;
  }

  /**
   * 進捗値を設定
   *
   * @param arg
   */
  public void setProgress(float arg){
    progress=arg;
  }

  /**
   * ステータスを取得
   *
   * @return　ステータス
   */
  public Status getStatus() {
    return status;
  }

  /**
   * ステータスを設定
   *
   * @param status　ステータス
   */
  public void setStatus(Status status) {
    this.status = status;
  }

}

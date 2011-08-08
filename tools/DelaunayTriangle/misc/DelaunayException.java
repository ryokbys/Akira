package tools.DelaunayTriangle.misc;

/**
 * DelaunayException
 * <pre>
 * Delaunay分割処理の例外クラス
 * </pre>
 * @author t.matsuoka
 * @version 0.1
 */
public class DelaunayException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DelaunayException() {
    super();
  }

  public DelaunayException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  public DelaunayException(String arg0) {
    super(arg0);
  }

  public DelaunayException(Throwable arg0) {
    super(arg0);
  }

}

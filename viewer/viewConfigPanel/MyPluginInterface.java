package viewer.viewConfigPanel;

public interface MyPluginInterface {
  public void exec(String dir, int fn,
                   float[][] h,
                   float[][] hinv,
                   int n,
                   float[][] r,
                   byte[] tag,
                   int[] vtag
                   );

}

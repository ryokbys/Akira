package viewer.viewConfigPanel.plugin;

public interface ExportPluginInterface {
  public void exec(String dir, int fn,
                   float[][] h,
                   float[][] hinv,
                   int n,
                   float[][] r,
                   byte[] tag,
                   int[] vtag
                   );

}

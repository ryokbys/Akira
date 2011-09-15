package viewer.viewConfigPanel.plugin;

public interface ExportPluginInterface {
  public String getSaveFileName();
  public String getPluginName();
  public void exec(String saveFile,
                   float[][] h,
                   float[][] hinv,
                   int n,
                   float[][] r,
                   byte[] tag,
                   int[] vtag
                   );

}

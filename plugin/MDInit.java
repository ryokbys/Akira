package plugin;
import viewer.viewConfigPanel.MyPluginInterface;

public class MDInit implements MyPluginInterface {
  public void exec(String dir, int fn,
                   float[][] h,
                   float[][] hinv,
                   int n,
                   float[][] r,
                   byte[] tag,
                   int[] vtag
                   ){
    System.out.println("md init");
  }
}

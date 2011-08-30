package viewer.viewConfigPanel.plugin;

public interface MDPluginInterface {
  public String getName();
  public double[][] getForce(double[][]h, int natm,double[][] r);
}

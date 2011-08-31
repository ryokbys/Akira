package viewer.viewConfigPanel.plugin;

public interface MDPluginInterface {
  public String getName();
  public int getSpeciesN();
  public double getMass(int i);
  public double[][] getForce(double[][]h, int natm,double[][] r);
}

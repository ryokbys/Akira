package viewer.viewConfigPanel.plugin;

public interface ModelingPluginInterface {
  public String getSaveFileName();
  public String getPluginName();
  public void make(String dir, int Nx, int Ny, int Nz);
}

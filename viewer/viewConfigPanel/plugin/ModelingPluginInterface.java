package viewer.viewConfigPanel.plugin;

public interface ModelingPluginInterface {
  public String getName();
  public void make(String dir, int fnum,
                   int Nx, int Ny, int Nz);
}

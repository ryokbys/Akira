package viewer;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import viewer.*;
import viewer.renderer.*;
import viewer.viewConfigPanel.*;

public class ViewConfigWindow extends JFrame{

  /* accesser starts */
  public void updateStatusString(RenderingWindow rw){
    maniPanel.changebtnStSpIcon(rw);
    statusPanel.updateStatusString(rw);
    atom.updateStatus();
    bond.updateStatus();
    vec.updateStatus();
    volumerender.updateStatus();
    if(plotterPanel!=null)plotterPanel.update();
  }

  public void focusOnStatus(){
    if(statusPanel.statusFrame.isVisible()) statusPanel.statusFrame.toFront();
    //else tabbedPane.setSelectedIndex(0);
  }
  public void setAtomsTable(){
    atom.addTableRow();
  }
  public void setDataRange(float[][] range){
    data.setDataRange(range);
  }
  public void setTrjMode(){
    trj.setTrjMode();
  }
  public void setPickedID(int id){
    trj.setPickedID(id);
  }

  public ComboPanel getCombo(){
    return combo;
  }
  public TrajectoryPanel getTrj(){
    return trj;
  }


  /* end accesser*/

  private Controller ctrl;
  //constructor
  public ViewConfigWindow(Controller ctrl){
    this.ctrl=ctrl;
    setTitle("View Configuration");
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    //tabbed Pane
    createTabbedPane();
  }

  JTabbedPane tabbedPane;
  StatusPanel statusPanel;
  private ManipulationPanel maniPanel;
  private AtomPanel atom;
  private DataPanel data;
  private BondPanel bond;
  private VectorPanel vec;
  private BoundaryPanel boundary;
  private VolumeRenderPanel volumerender;
  private PlotterPanel plotterPanel;
  private ComboPanel combo;
  private TrajectoryPanel trj;
  private RadialDistributionPanel rdst;



  private void createTabbedPane(){

    //create tabbed pane
    tabbedPane = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.WRAP_TAB_LAYOUT);
    tabbedPane.setFocusable(false);



    //statusPanel panel
    statusPanel = new StatusPanel(ctrl);
    statusPanel.setFocusable(false);
    tabbedPane.addTab(null,
                      new ImageIcon(this.getClass().getResource("/img/tab/status.png")),
                      statusPanel,
                      "Status Panel");
    //manipulation
    maniPanel=new ManipulationPanel(ctrl);
    tabbedPane.addTab(null,
                      new ImageIcon(this.getClass().getResource("/img/tab/manipulation.png")),
                      maniPanel,
                      "Manipulation Panel");
    //atom panel
    atom = new AtomPanel(ctrl);
    atom.setFocusable(false);
    tabbedPane.addTab(null,
                      new ImageIcon(this.getClass().getResource("/img/tab/atom.png")),
                      atom,
                      "Atom Property");
    //data panel
    data = new DataPanel(ctrl);
    data.setFocusable(false);
    tabbedPane.addTab(null,
                      new ImageIcon(this.getClass().getResource("/img/tab/data.png")),
                      data,
                      "Data range");
    //boundary panel
    boundary=new BoundaryPanel(ctrl);
    boundary.setFocusable(false);
    tabbedPane.addTab(null,
                      new ImageIcon(this.getClass().getResource("/img/tab/boundary.png")),
                      boundary,
                      "Boundary");
    //bond
    bond = new BondPanel(ctrl);
    bond.setFocusable(false);
    tabbedPane.addTab(null,
                      new ImageIcon(this.getClass().getResource("/img/tab/bond.png")),
                      bond,
                      "Bond setting");
    //color table
    ColorTablePanel ctable =new ColorTablePanel(ctrl);
    ctable.setFocusable(false);
    tabbedPane.addTab(null,
                      new ImageIcon(this.getClass().getResource("/img/tab/color.png")),
                      ctable,
                      "Color table");
    //annotaion
    AnnotationPanel annotaionPanel=new AnnotationPanel(ctrl);
    annotaionPanel.setFocusable(false);
    tabbedPane.addTab(null,
                      new ImageIcon(this.getClass().getResource("/img/tab/annotation.png")),
                      annotaionPanel,
                      "Annotation");
    //volume rendering
    volumerender=new VolumeRenderPanel(ctrl);
    volumerender.setFocusable(false);
    tabbedPane.addTab(null,
                      new ImageIcon(this.getClass().getResource("/img/tab/volume.png")),
                      volumerender,
                      "Volume Rendering");
    //vector
    vec = new VectorPanel(ctrl);
    vec.setFocusable(false);
    tabbedPane.addTab(null,
                      new ImageIcon(this.getClass().getResource("/img/tab/vector.png")),
                      vec,
                      "Vectors");
    //combo
    combo = new ComboPanel(ctrl);
    combo.setFocusable(false);
    tabbedPane.addTab(null,
                      new ImageIcon(this.getClass().getResource("/img/tab/combo.png")),
                      combo,
                      "Combo");
    //trj
    trj=new TrajectoryPanel(ctrl);
    trj.setFocusable(false);
    tabbedPane.addTab(null,
                      new ImageIcon(this.getClass().getResource("/img/tab/trj.png")),
                      trj,
                      "Trajectory");
    //export
    ExportPanel export=new ExportPanel(ctrl);
    export.setFocusable(false);
    tabbedPane.addTab(null,
                      new ImageIcon(this.getClass().getResource("/img/tab/export.png")),
                      export,
                      "Export");

    if(ctrl.isEnjoyMode){
      //plane panel
      PlanePanel plane=new PlanePanel(ctrl);
      plane.setFocusable(false);
      tabbedPane.addTab(null,
                        new ImageIcon(this.getClass().getResource("/img/tab/plane.png")),
                        plane,
                        "Plane");
      //plotter
      plotterPanel=new PlotterPanel(ctrl);
      tabbedPane.addTab(null,
                        new ImageIcon(this.getClass().getResource("/img/tab/plotter.png")),
                        plotterPanel,
                        "Plotter");
      //radial
      rdst=new RadialDistributionPanel(ctrl);
      rdst.setFocusable(false);
      tabbedPane.addTab(null,
                        new ImageIcon(this.getClass().getResource("/img/tab/rd.png")),
                        rdst,
                        "Radial Distribution");
      //neighbor analysis
      NeighborAnalysisPanel na=new NeighborAnalysisPanel(ctrl);
      na.setFocusable(false);
      tabbedPane.addTab(null,
                        new ImageIcon(this.getClass().getResource("/img/tab/neighbor.png")),
                        na,
                        "Neighbor Analysis");
      //light
      LightPanel light= new LightPanel(ctrl);
      light.setFocusable(false);
      tabbedPane.addTab(null,
                        new ImageIcon(this.getClass().getResource("/img/tab/light.png")),
                        light,
                        "Light Setting");
      //viewpoint
      ViewPointPanel vpPanel=new ViewPointPanel(ctrl);
      vpPanel.setFocusable(false);
      tabbedPane.addTab(null,
                        new ImageIcon(this.getClass().getResource("/img/tab/viewpoint.png")),
                        vpPanel,"ViewPoint Setting");
      //Modeling
      ModelingPanel model=new ModelingPanel(ctrl);
      model.setFocusable(false);
      tabbedPane.addTab(null,
                        new ImageIcon(this.getClass().getResource("/img/tab/model.png")),
                        model,
                        "Modeling");
      //MD
      MDPanel md=new MDPanel(ctrl);
      md.setFocusable(false);
      tabbedPane.addTab(null,
                        new ImageIcon(this.getClass().getResource("/img/tab/md.png")),
                        md,
                        "MD");
    }


    if(ctrl.vconf.viewConfigWinTabIndex < tabbedPane.getTabCount())
      tabbedPane.setSelectedIndex(ctrl.vconf.viewConfigWinTabIndex);
    else
      tabbedPane.setSelectedIndex(0);

    //add tabbedPane to frame
    add(tabbedPane);
    requestFocusInWindow();
  }

}

package viewer;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import viewer.*;
import viewer.renderer.*;
import viewer.viewConfigPanel.*;

public class ViewConfigWindow extends JFrame implements ItemListener{

  /* accesser starts */
  public void updateStatusString(RenderingWindow rw){
    maniPanel.changebtnStSpIcon(rw);
    statusPanel.updateStatusString(rw);
    atom.updateStatus();
    bond.updateStatus();
    vec.updateStatus();
    volumerender.updateStatus();
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
    //createTabbedPane();

    basePanel= new JPanel();
    getContentPane().add(basePanel);

    createComboBox();
    createPanels();
    createCardPanel();

    SpringLayout layout= new SpringLayout();
    basePanel.setLayout(layout);
    layout.putConstraint( SpringLayout.NORTH, jcPanelNameList, 10, SpringLayout.NORTH, basePanel );
    layout.putConstraint( SpringLayout.WEST, jcPanelNameList, 10, SpringLayout.WEST, basePanel );
    basePanel.add(jcPanelNameList);

    layout.putConstraint( SpringLayout.NORTH, cardPanel, 10, SpringLayout.SOUTH, jcPanelNameList );
    layout.putConstraint( SpringLayout.SOUTH, cardPanel, 0, SpringLayout.SOUTH, basePanel );
    layout.putConstraint( SpringLayout.WEST, cardPanel, 0, SpringLayout.WEST, basePanel );
    layout.putConstraint( SpringLayout.EAST, cardPanel, 0, SpringLayout.EAST, basePanel );
    basePanel.add(cardPanel);
    
    requestFocusInWindow();
  }

  private JPanel basePanel;
  private JPanel cardPanel;
  public StatusPanel statusPanel;
  private ManipulationPanel maniPanel;
  private AtomPanel atom;
  private DataPanel data;
  private BondPanel bond;
  private ColorTablePanel ctable;
  private AnnotationPanel annotationPanel;
  private VectorPanel vec;
  private BoundaryPanel boundary;
  private VolumeRenderPanel volumerender;
  private ComboPanel combo;
  private TrajectoryPanel trj;
  private PlanePanel plane;

  private String[] strPanelNameList={"Status",
                                      "Manipulation",
                                      "Atom",
                                      "Data",
                                      "Bond",
                                      "Color Table",
                                      "Annotation",
                                      "Vector",
                                      "Boundary",
                                      "Volume rendering",
                                      "Combo",
                                      "Trajectory",
                                      "Plane"};
  public JComboBox jcPanelNameList;

  void createComboBox(){
    jcPanelNameList= new JComboBox(strPanelNameList);
    jcPanelNameList.setSelectedIndex(0);
    jcPanelNameList.setFocusable(false);
    jcPanelNameList.addItemListener(this);
  }
  
  void createPanels(){
    //status
    statusPanel = new StatusPanel(ctrl);
    statusPanel.setFocusable(false);
    //manipulation
    maniPanel=new ManipulationPanel(ctrl);
    maniPanel.setFocusable(false);
    //atom panel
    atom = new AtomPanel(ctrl);
    atom.setFocusable(false);
    //data panel
    data = new DataPanel(ctrl);
    data.setFocusable(false);
    //boundary panel
    boundary=new BoundaryPanel(ctrl);
    boundary.setFocusable(false);
    //bond
    bond = new BondPanel(ctrl);
    bond.setFocusable(false);
    //color table
    ctable =new ColorTablePanel(ctrl);
    ctable.setFocusable(false);
    //annotaion
    annotationPanel=new AnnotationPanel(ctrl);
    annotationPanel.setFocusable(false);
    //volume rendering
    volumerender=new VolumeRenderPanel(ctrl);
    volumerender.setFocusable(false);
    //vector
    vec = new VectorPanel(ctrl);
    vec.setFocusable(false);
    //combo
    combo = new ComboPanel(ctrl);
    combo.setFocusable(false);
    //trj
    trj=new TrajectoryPanel(ctrl);
    trj.setFocusable(false);
    //plane panel
    plane=new PlanePanel(ctrl);
    plane.setFocusable(false);
  }

  //create cardPanel
  void createCardPanel(){
    cardPanel= new JPanel();
    cardPanel.setLayout(new CardLayout());
    cardPanel.add(statusPanel,strPanelNameList[0]);
    cardPanel.add(maniPanel,strPanelNameList[1]);
    cardPanel.add(atom,strPanelNameList[2]);
    cardPanel.add(data,strPanelNameList[3]);
    cardPanel.add(bond,strPanelNameList[4]);
    cardPanel.add(ctable,strPanelNameList[5]);
    cardPanel.add(annotationPanel,strPanelNameList[6]);
    cardPanel.add(vec,strPanelNameList[7]);
    cardPanel.add(boundary,strPanelNameList[8]);
    cardPanel.add(volumerender,strPanelNameList[9]);
    cardPanel.add(combo,strPanelNameList[10]);
    cardPanel.add(trj,strPanelNameList[11]);
    cardPanel.add(plane,strPanelNameList[12]);
  }

  public void itemStateChanged(ItemEvent e){
    CardLayout cl= (CardLayout)(cardPanel.getLayout());
    cl.show(cardPanel,(String)e.getItem());
  }
}

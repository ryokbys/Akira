package viewer.viewConfigPanel;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.LineBorder;

import viewer.*;
import data.*;
import tools.*;
import tools.colorpicker.*;

public class PlanePanel extends JPanel implements ActionListener{


  // color picker
  ColorPicker colorPicker;
  // JFrame is necessary to call the ColorPicker.showDialog
  JFrame dummyFrame;

  Controller ctrl;
  ViewConfig vconf;
  public PlanePanel(Controller ctrl){
    this.ctrl=ctrl;
    vconf=ctrl.vconf;
    //Set up the picker that the button brings up.
    colorPicker = new ColorPicker(true,true);

    setVars();
    createPanel();


  }
  void setVars(){
    for(int i=0;i<Const.PLANE;i++){
      vconf.isPlaneVisible[i]=false;
    }
  }

  JButton colorButton;
  JButton resetButton;
  JButton applyButton;

  JCheckBox cbPickPlane;
  JCheckBox cbVoronoi;
  JCheckBox cbDelaunay;
  JCheckBox cbTetrahedron;
  JButton calAllVoronoiButton;

  final String[] colNames = { "On/Off", "Nx", "Ny", "Nz",
                              "Px","Py","Pz",
                              "Color" };
  MyTableModel tableModel;
  JTable table;
  JScrollPane sp;
  private JSpinner spTetraSpecies;
  private JSpinner spRcut;
  private JButton singlePlaneColorButton;


  public void createPanel(){
    tableModel = new MyTableModel( colNames, 0 );
    //tableModel.setFocusable(false);
    table = new JTable( tableModel );
    //table.setFocusable(false);

    table.setRowHeight( 20 );
    table.setIntercellSpacing( new Dimension(2,2) );
    table.setColumnSelectionAllowed( true );

    table.setDefaultRenderer( Color.class, new MyColorRenderer(true));
    table.setDefaultEditor( Color.class, new MyColorEditor() );
    table.setFocusable(false);

    sp = new JScrollPane( table );
    sp.setFocusable(false);
    sp.setWheelScrollingEnabled( true );
    sp.setPreferredSize( new Dimension(400,100) );
    addTable();

    DefaultTableColumnModel columnModel
      = (DefaultTableColumnModel)table.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(2);
    columnModel.getColumn(1).setPreferredWidth(4);
    columnModel.getColumn(2).setPreferredWidth(4);
    columnModel.getColumn(3).setPreferredWidth(4);
    columnModel.getColumn(4).setPreferredWidth(4);
    columnModel.getColumn(5).setPreferredWidth(4);
    columnModel.getColumn(6).setPreferredWidth(4);

    //reset button
    resetButton=new JButton("Reset");
    resetButton.setFocusable(false);
    resetButton.addActionListener( this );


    applyButton=new JButton("Apply");
    applyButton.setFocusable(false);
    applyButton.addActionListener( this );

    calAllVoronoiButton=new JButton("Cal. All Voronoi");
    calAllVoronoiButton.setFocusable(false);
    calAllVoronoiButton.addActionListener( this );



    //check box
    cbPickPlane =new JCheckBox("Pick Plane Mode",vconf.isSelectionPlaneMode);
    cbPickPlane.setFocusable(false);

    cbVoronoi =new JCheckBox("Pick Voronoi",vconf.isVoronoiMode);
    cbVoronoi.setFocusable(false);

    cbDelaunay =new JCheckBox("Delaunay Mode",vconf.isDelaunayMode);
    cbDelaunay.setFocusable(false);

    cbTetrahedron =new JCheckBox("Tetrahedron Mode",vconf.isTetrahedronMode);
    cbTetrahedron.setFocusable(false);


    spTetraSpecies =new JSpinner( new SpinnerNumberModel( vconf.tetrahedronCenter,1,null,1 ) );
    spTetraSpecies.setPreferredSize( new Dimension(50,25) );

    spRcut =new JSpinner( new SpinnerNumberModel( (double)vconf.planeRcut,0.0,null,0.2 ) );
    spRcut.setPreferredSize( new Dimension(50,25) );

    singlePlaneColorButton= new JButton();
    singlePlaneColorButton.setFocusable(false);
    singlePlaneColorButton.setBorder(new LineBorder(Color.BLACK, 1, true));
    singlePlaneColorButton.addActionListener(this);
    singlePlaneColorButton.setOpaque(true);
    singlePlaneColorButton.setName("BG");
    singlePlaneColorButton.setPreferredSize(new Dimension(50,25));
    singlePlaneColorButton.setBackground(new Color(vconf.singlePlaneColor[0],
                                          vconf.singlePlaneColor[1],
                                          vconf.singlePlaneColor[2],
                                          vconf.singlePlaneColor[3]));


    SpringLayout layout = new SpringLayout();
    setLayout( layout );



    //table
    layout.putConstraint( SpringLayout.SOUTH, sp, -10,
                          SpringLayout.SOUTH, this );
    layout.putConstraint( SpringLayout.NORTH, sp, 10,
                          SpringLayout.NORTH, this );
    layout.putConstraint( SpringLayout.WEST, sp, 10,
                          SpringLayout.WEST, this );

    //color
    layout.putConstraint( SpringLayout.NORTH, singlePlaneColorButton, 10,
                          SpringLayout.NORTH, this);
    layout.putConstraint( SpringLayout.WEST, singlePlaneColorButton, 10,
                          SpringLayout.EAST, sp);
    JLabel rcutLabel=new JLabel("Rcut");
    layout.putConstraint( SpringLayout.NORTH, rcutLabel, 10,
                          SpringLayout.SOUTH,  singlePlaneColorButton);
    layout.putConstraint( SpringLayout.WEST, rcutLabel, 0,
                          SpringLayout.WEST, singlePlaneColorButton);
    layout.putConstraint( SpringLayout.NORTH, spRcut, 0,
                          SpringLayout.NORTH, rcutLabel);
    layout.putConstraint( SpringLayout.WEST, spRcut, 0,
                          SpringLayout.EAST, rcutLabel);



    //cb
    //voronoi
    layout.putConstraint( SpringLayout.NORTH, cbVoronoi, 10,
                          SpringLayout.NORTH, this);
    layout.putConstraint( SpringLayout.WEST, cbVoronoi, 10,
                          SpringLayout.EAST, resetButton);

    layout.putConstraint( SpringLayout.NORTH, calAllVoronoiButton, 0,
                          SpringLayout.NORTH, cbVoronoi);
    layout.putConstraint( SpringLayout.WEST, calAllVoronoiButton, 10,
                          SpringLayout.EAST, cbVoronoi);


    //tetrahedron mode
    layout.putConstraint( SpringLayout.NORTH, cbTetrahedron, 10,
                          SpringLayout.SOUTH,  cbVoronoi);
    layout.putConstraint( SpringLayout.WEST, cbTetrahedron, 0,
                          SpringLayout.WEST, cbVoronoi);
    JLabel tetraSpeciesLabel=new JLabel("Tag");
    layout.putConstraint( SpringLayout.NORTH, tetraSpeciesLabel, 0,
                          SpringLayout.SOUTH,  cbTetrahedron);
    layout.putConstraint( SpringLayout.WEST, tetraSpeciesLabel, 25,
                          SpringLayout.WEST, cbTetrahedron);
    layout.putConstraint( SpringLayout.NORTH, spTetraSpecies, 0,
                          SpringLayout.NORTH, tetraSpeciesLabel);
    layout.putConstraint( SpringLayout.WEST, spTetraSpecies, 0,
                          SpringLayout.EAST, tetraSpeciesLabel);
    //delaynay
    layout.putConstraint( SpringLayout.NORTH, cbDelaunay, 20,
                          SpringLayout.SOUTH, cbTetrahedron );
    layout.putConstraint( SpringLayout.WEST, cbDelaunay, 0,
                          SpringLayout.WEST, cbTetrahedron);
    //pick plane
    layout.putConstraint( SpringLayout.NORTH, cbPickPlane, 10,
                          SpringLayout.SOUTH, cbDelaunay);
    layout.putConstraint( SpringLayout.WEST, cbPickPlane, 0,
                          SpringLayout.WEST,  cbDelaunay);





    //reset
    layout.putConstraint( SpringLayout.SOUTH, applyButton, -10,
                          SpringLayout.SOUTH, this);
    layout.putConstraint( SpringLayout.WEST, applyButton, 10,
                          SpringLayout.EAST,  sp);

    layout.putConstraint( SpringLayout.SOUTH, resetButton, 0,
                          SpringLayout.SOUTH,  applyButton);
    layout.putConstraint( SpringLayout.WEST, resetButton, 5,
                          SpringLayout.EAST, applyButton );


    //addition
    add(cbPickPlane);
    add(cbVoronoi);
    add(cbDelaunay);
    add(cbTetrahedron);
    add(resetButton);
    add(applyButton);
    add(sp);
    add(spTetraSpecies);
    add(spRcut);
    add(singlePlaneColorButton);
    add(tetraSpeciesLabel);
    add(rcutLabel);
    add(calAllVoronoiButton);
  }

  boolean visiblePlane=false;
  public void actionPerformed( ActionEvent e ){
    if( e.getSource() == resetButton ){
      //resetparameters();
      setVars();
      setTable();
    }else if( e.getSource() == calAllVoronoiButton){
      if(ctrl.getActiveRW()!=null){
        ctrl.getActiveRW().plane.calAllVoronoi();
      }
    }else if( e.getSource() == applyButton ){
      updateList();
      ctrl.RWinRefresh();
    }else if( e.getSource() == singlePlaneColorButton ){
      Color prevColor= new Color(vconf.singlePlaneColor[0],vconf.singlePlaneColor[1],
                                 vconf.singlePlaneColor[2],vconf.singlePlaneColor[3]);
      Color newColor= new Color(vconf.singlePlaneColor[0],vconf.singlePlaneColor[1],
                                vconf.singlePlaneColor[2],vconf.singlePlaneColor[3]);
      singlePlaneColorButton.setForeground(prevColor);

      colorPicker.setColor(prevColor);

      newColor= colorPicker.showDialog(dummyFrame,newColor,true);

      if( newColor==null ){
        // usr previous color if the user cancels the dialog
        newColor= prevColor;
      }else{ // newColor exists
        vconf.singlePlaneColor=newColor.getRGBComponents(null);
      }
      singlePlaneColorButton.setBackground( newColor );
    }


  }




  void setTable(){
    for( int i=0; i<Const.PLANE; i++ ){
      tableModel.setValueAt( vconf.isPlaneVisible[i],i,0 );
      tableModel.setValueAt( (double)vconf.planeNormal[i][0],i,1 );
      tableModel.setValueAt( (double)vconf.planeNormal[i][1],i,2 );
      tableModel.setValueAt( (double)vconf.planeNormal[i][2],i,3 );
      tableModel.setValueAt( (double)vconf.planePoint[i][0],i,4 );
      tableModel.setValueAt( (double)vconf.planePoint[i][1],i,5 );
      tableModel.setValueAt( (double)vconf.planePoint[i][2],i,6 );
      tableModel.setValueAt( new Color(vconf.planeColor[i][0]
                                       ,vconf.planeColor[i][1]
                                       ,vconf.planeColor[i][2]
                                       ,vconf.planeColor[i][3]) ,i,7 );
    }
  }

  void addTable(){
    Object[] s = new Object[colNames.length];
    for( int i=0; i<Const.PLANE; i++ ){
      s[0] = vconf.isPlaneVisible[i];
      s[1] = (double)vconf.planeNormal[i][0];
      s[2] = (double)vconf.planeNormal[i][1];
      s[3] = (double)vconf.planeNormal[i][2];
      s[4] = (double)vconf.planePoint[i][0];
      s[5] = (double)vconf.planePoint[i][1];
      s[6] = (double)vconf.planePoint[i][2];
      s[7] = new Color(vconf.planeColor[i][0]
                       ,vconf.planeColor[i][1]
                       ,vconf.planeColor[i][2]
                       ,vconf.planeColor[i][3]);
      tableModel.addRow( s );
    }
  }

  void updateList(){
    vconf.isSelectionPlaneMode=cbPickPlane.isSelected();
    vconf.isVoronoiMode=cbVoronoi.isSelected();
    vconf.isDelaunayMode=cbDelaunay.isSelected();
    vconf.isTetrahedronMode=cbTetrahedron.isSelected();
    vconf.tetrahedronCenter=((Integer)spTetraSpecies.getValue()).intValue();
    vconf.planeRcut=((Double)spRcut.getValue()).floatValue();

    //int s = table.getRowCount();
    Double data;
    for( int i=0; i<Const.PLANE; i++ ){
      vconf.isPlaneVisible[i]=(Boolean)table.getValueAt(i,0);
      data=(Double)table.getValueAt(i,1);
      vconf.planeNormal[i][0]=data.floatValue();
      data=(Double)table.getValueAt(i,2);
      vconf.planeNormal[i][1]=data.floatValue();
      data=(Double)table.getValueAt(i,3);
      vconf.planeNormal[i][2]=data.floatValue();
      data=(Double)table.getValueAt(i,4);
      vconf.planePoint[i][0]=data.floatValue();
      data=(Double)table.getValueAt(i,5);
      vconf.planePoint[i][1]=data.floatValue();
      data=(Double)table.getValueAt(i,6);
      vconf.planePoint[i][2]=data.floatValue();
      Color newColor = (Color)table.getValueAt(i,7);
      float[] tmp=newColor.getRGBComponents(null);
      float alpha=(float)(newColor.getAlpha()/255.f);
      vconf.planeColor[i][0]=tmp[0];
      vconf.planeColor[i][1]=tmp[1];
      vconf.planeColor[i][2]=tmp[2];
      vconf.planeColor[i][3]=alpha;
    }
  }
  class MyTableModel extends DefaultTableModel {
    MyTableModel( String[] columnNames, int rowNum ){
      super( columnNames, rowNum );
    }
    public Class getColumnClass(int c){
      return getValueAt(0, c).getClass();
    }
  }

}

package viewer.viewConfigPanel;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import data.*;
import viewer.*;
import tools.*;
import viewer.renderer.*;
import viewer.LF.*;

public class BoundaryPanel extends JPanel implements ChangeListener,
                                                   ActionListener{

  /*
   * Slice view Controller
   */


  //
  Controller ctrl;
  ViewConfig vconf;

  //pointers
  boolean[] isOn;
  float[][] normalVec;
  float[][] posVec;

  String filePath;
  //constructor
  public BoundaryPanel(Controller ctrl){
    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;
    filePath= vconf.configDir+File.separator+"sliceinfo";

    isOn=vconf.isOnSlicer;
    normalVec=vconf.normalVecSlicer;
    posVec=vconf.posVecSlicer;

    createPanel();
  }

  public void actionPerformed(ActionEvent ae){
    if(ae.getSource() == resetButton ){
      vconf.resetBoundary();
      setValue();
      if(ctrl.getActiveRW()!=null){
        ctrl.getActiveRW().sq.clearWinPos();
        ctrl.getActiveRW().atoms.resetVisualTag();
      }
      ctrl.RWinRefresh();
    }else if(ae.getSource() == applyButton){
      updateList();
      if(ctrl.getActiveRW()!=null){
        ctrl.getActiveRW().atoms.setVisualTag();
        ctrl.RWinRefresh();
      }
    }else if( ae.getSource() == btnRegionClear ){
      if(ctrl.getActiveRW()!=null){
        ctrl.getActiveRW().sq.clearWinPos();
        ctrl.RWinRefresh();
      }
    }
  }

  public void stateChanged( ChangeEvent ce ){
    updateList();
  }



  JButton resetButton, applyButton;

  final String[] colNames = { "On/Off", "Nx", "Ny", "Nz",
                              "Px","Py","Pz"};
  MyTableModel tableModel;
  JTable table;
  JScrollPane sp;

  private JSpinner spExtendX1,spExtendY1,spExtendZ1;
  private JSpinner spExtendX2,spExtendY2,spExtendZ2;

  private JCheckBox cbSphereCut;
  private JSpinner spherePx,spherePy,spherePz,sphereRadius;

  JCheckBox cbDelete;
  JCheckBox cbRegionSelect;
  JButton btnRegionClear;
  JCheckBox cbRectangleSelect;


  public void createPanel(){
    this.addKeyListener(ctrl.keyCtrl);

    JLabel lExtendX=new JLabel("<= a <");
    lExtendX.setFocusable(false);
    spExtendX1 = new JSpinner(new SpinnerNumberModel((double)vconf.extendRenderingFactor[0][0], null, null, 0.1));
    spExtendX1.setFocusable(false);
    spExtendX1.setPreferredSize(new Dimension(60, 25));
    spExtendX1.addChangeListener(this);
    spExtendX2 = new JSpinner(new SpinnerNumberModel((double)vconf.extendRenderingFactor[1][0], null, null, 0.1));
    spExtendX2.setFocusable(false);
    spExtendX2.setPreferredSize(new Dimension(60, 25));
    spExtendX2.addChangeListener(this);

    JLabel lExtendY=new JLabel("<= b <");
    lExtendY.setFocusable(false);

    spExtendY1 = new JSpinner(new SpinnerNumberModel((double)vconf.extendRenderingFactor[0][1], null, null, 0.1));
    spExtendY1.setFocusable(false);
    spExtendY1.setPreferredSize(new Dimension(60, 25));
    spExtendY1.addChangeListener(this);
    spExtendY2 = new JSpinner(new SpinnerNumberModel((double)vconf.extendRenderingFactor[1][1], null, null, 0.1));
    spExtendY2.setFocusable(false);
    spExtendY2.setPreferredSize(new Dimension(60, 25));
    spExtendY2.addChangeListener(this);

    JLabel lExtendZ=new JLabel("<= c <");
    lExtendZ.setFocusable(false);
    spExtendZ1 = new JSpinner(new SpinnerNumberModel((double)vconf.extendRenderingFactor[0][2], null, null, 0.1));
    spExtendZ1.setFocusable(false);
    spExtendZ1.setPreferredSize(new Dimension(60, 25));
    spExtendZ1.addChangeListener(this);
    spExtendZ2 = new JSpinner(new SpinnerNumberModel((double)vconf.extendRenderingFactor[1][2], null, null, 0.1));
    spExtendZ2.setFocusable(false);
    spExtendZ2.setPreferredSize(new Dimension(60, 25));
    spExtendZ2.addChangeListener(this);


    //sphere cut
    vconf.isSphereCut=false;
    cbSphereCut =new JCheckBox("Sphere Cut",vconf.isSphereCut);
    cbSphereCut.setFocusable(false);

    spherePx = new JSpinner(new SpinnerNumberModel((double)vconf.spherecutPos[0], 0., 1., 0.1));
    spherePx.setFocusable(false);
    spherePx.setPreferredSize(new Dimension(60, 25));
    spherePx.addChangeListener(this);

    spherePy = new JSpinner(new SpinnerNumberModel((double)vconf.spherecutPos[1], 0., 1., 0.1));
    spherePy.setFocusable(false);
    spherePy.setPreferredSize(new Dimension(60, 25));
    spherePy.addChangeListener(this);

    spherePz = new JSpinner(new SpinnerNumberModel((double)vconf.spherecutPos[2], 0., 1., 0.1));
    spherePz.setFocusable(false);
    spherePz.setPreferredSize(new Dimension(60, 25));
    spherePz.addChangeListener(this);

    sphereRadius = new JSpinner(new SpinnerNumberModel((double)vconf.spherecutRadius, null, null, 1.));
    sphereRadius.setFocusable(false);
    sphereRadius.setPreferredSize(new Dimension(60, 25));
    sphereRadius.addChangeListener(this);

    vconf.isDeletionMode=false;
    cbDelete =new JCheckBox("Delete",vconf.isDeletionMode);
    cbDelete.setFocusable(false);
    cbDelete.addChangeListener(this);

    vconf.isRegionSelectMode=false;
    cbRegionSelect =new JCheckBox("Region Select",vconf.isRegionSelectMode);
    cbRegionSelect.setFocusable(false);
    cbRegionSelect.addChangeListener(this);

    vconf.isRectangleSelectMode=false;
    cbRectangleSelect =new JCheckBox("Rectangle Select",vconf.isRectangleSelectMode);
    cbRectangleSelect.setFocusable(false);
    cbRectangleSelect.addChangeListener(this);


    btnRegionClear  = new JButton( "Clear Region" );
    btnRegionClear.setFocusable(false);
    btnRegionClear.addActionListener( this );


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

    JLabel tableLabel = new JLabel("Plane Slicer");

    addTable();

    DefaultTableColumnModel columnModel
      = (DefaultTableColumnModel)table.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(2);
    columnModel.getColumn(1).setPreferredWidth(3);
    columnModel.getColumn(2).setPreferredWidth(4);
    columnModel.getColumn(3).setPreferredWidth(4);
    columnModel.getColumn(4).setPreferredWidth(4);
    columnModel.getColumn(5).setPreferredWidth(4);
    columnModel.getColumn(6).setPreferredWidth(4);



    //Reset Button
    resetButton = new JButton("Reset");
    resetButton.addActionListener( this );
    resetButton.setFocusable(false);


    //Apply Button
    applyButton = new JButton("Apply");
    applyButton.setToolTipText("Apply Slice Position");
    applyButton.addActionListener( this );
    applyButton.setFocusable(false);


    SpringLayout layout = new SpringLayout();


    JLabel lExtend=new JLabel("Boundary");
    add(lExtend);
    layout.putConstraint( SpringLayout.NORTH, lExtend, 10,
                          SpringLayout.NORTH, this);
    layout.putConstraint( SpringLayout.WEST, lExtend, 10,
                          SpringLayout.WEST, this);

    layout.putConstraint( SpringLayout.NORTH, spExtendX1, 10,
                          SpringLayout.SOUTH, lExtend);
    layout.putConstraint( SpringLayout.WEST, spExtendX1, 10,
                          SpringLayout.WEST, lExtend);
    layout.putConstraint( SpringLayout.NORTH, lExtendX, 0,
                          SpringLayout.NORTH, spExtendX1);
    layout.putConstraint( SpringLayout.WEST, lExtendX, 0,
                          SpringLayout.EAST, spExtendX1);
    layout.putConstraint( SpringLayout.NORTH, spExtendX2, 0,
                          SpringLayout.NORTH, lExtendX);
    layout.putConstraint( SpringLayout.WEST, spExtendX2, 0,
                          SpringLayout.EAST, lExtendX);

    layout.putConstraint( SpringLayout.NORTH, spExtendY1, 0,
                          SpringLayout.SOUTH, spExtendX1);
    layout.putConstraint( SpringLayout.WEST, spExtendY1, 0,
                          SpringLayout.WEST, spExtendX1);
    layout.putConstraint( SpringLayout.NORTH, lExtendY, 0,
                          SpringLayout.NORTH, spExtendY1);
    layout.putConstraint( SpringLayout.WEST, lExtendY, 0,
                          SpringLayout.WEST, lExtendX);
    layout.putConstraint( SpringLayout.NORTH, spExtendY2, 0,
                          SpringLayout.NORTH, spExtendY1);
    layout.putConstraint( SpringLayout.WEST, spExtendY2, 0,
                          SpringLayout.WEST, spExtendX2);

    layout.putConstraint( SpringLayout.NORTH, spExtendZ1, 0,
                          SpringLayout.SOUTH, spExtendY1);
    layout.putConstraint( SpringLayout.WEST, spExtendZ1, 0,
                          SpringLayout.WEST, spExtendY1);
    layout.putConstraint( SpringLayout.NORTH, lExtendZ, 0,
                          SpringLayout.NORTH, spExtendZ1);
    layout.putConstraint( SpringLayout.WEST, lExtendZ, 0,
                          SpringLayout.WEST, lExtendY);
    layout.putConstraint( SpringLayout.NORTH, spExtendZ2, 0,
                          SpringLayout.NORTH, spExtendZ1);
    layout.putConstraint( SpringLayout.WEST, spExtendZ2, 0,
                          SpringLayout.WEST, spExtendY2);



    //table
    layout.putConstraint( SpringLayout.NORTH, tableLabel, 10, SpringLayout.NORTH, this );
    layout.putConstraint( SpringLayout.WEST, sp, 20 ,SpringLayout.EAST,  spExtendX2);
    layout.putConstraint( SpringLayout.SOUTH, sp, -10, SpringLayout.SOUTH, this);
    layout.putConstraint( SpringLayout.NORTH, sp, 0, SpringLayout.SOUTH, tableLabel );

    layout.putConstraint( SpringLayout.WEST, tableLabel, 0,SpringLayout.WEST, sp );


    layout.putConstraint( SpringLayout.NORTH, cbSphereCut, 10,SpringLayout.NORTH, this );
    layout.putConstraint( SpringLayout.WEST,  cbSphereCut, 10,SpringLayout.EAST,sp);

    layout.putConstraint( SpringLayout.NORTH, spherePx, 5,SpringLayout.SOUTH,cbSphereCut);
    layout.putConstraint( SpringLayout.WEST,  spherePx, 10,SpringLayout.WEST,cbSphereCut);

    layout.putConstraint( SpringLayout.NORTH, spherePy, 0,SpringLayout.NORTH,spherePx);
    layout.putConstraint( SpringLayout.WEST,  spherePy, 0,SpringLayout.EAST,spherePx);

    layout.putConstraint( SpringLayout.NORTH, spherePz, 0,SpringLayout.NORTH,spherePy);
    layout.putConstraint( SpringLayout.WEST,  spherePz, 0,SpringLayout.EAST,spherePy);

    layout.putConstraint( SpringLayout.NORTH, sphereRadius, 5,SpringLayout.SOUTH,spherePx);
    layout.putConstraint( SpringLayout.WEST,  sphereRadius, 0,SpringLayout.WEST,spherePx);




    //Button: Reset
    layout.putConstraint( SpringLayout.SOUTH, applyButton, -10,SpringLayout.SOUTH, this );
    layout.putConstraint( SpringLayout.WEST,  applyButton, 0,SpringLayout.EAST,  spherePz);
    //Button: Apply
    layout.putConstraint( SpringLayout.NORTH, resetButton, 0,SpringLayout.NORTH, applyButton);
    layout.putConstraint( SpringLayout.WEST,  resetButton, 10,SpringLayout.EAST,  applyButton );

    //
    layout.putConstraint( SpringLayout.NORTH, cbDelete, 10,
                          SpringLayout.NORTH, this);
    layout.putConstraint( SpringLayout.WEST, cbDelete, 0,
                          SpringLayout.EAST, spherePz);

    layout.putConstraint( SpringLayout.NORTH, cbRegionSelect, 0,
                          SpringLayout.SOUTH, cbDelete);
    layout.putConstraint( SpringLayout.WEST, cbRegionSelect, 0,
                          SpringLayout.WEST, cbDelete);

    layout.putConstraint( SpringLayout.NORTH, btnRegionClear, 0,
                          SpringLayout.NORTH, cbRegionSelect);
    layout.putConstraint( SpringLayout.WEST, btnRegionClear, 5,
                          SpringLayout.EAST, cbRegionSelect);

    layout.putConstraint( SpringLayout.NORTH, cbRectangleSelect, 0,
                          SpringLayout.SOUTH, cbRegionSelect);
    layout.putConstraint( SpringLayout.WEST, cbRectangleSelect, 0,
                          SpringLayout.WEST, cbRegionSelect);



    setLayout( layout );

    add(lExtendX);
    add(lExtendY);
    add(lExtendZ);
    add(spExtendX1);
    add(spExtendY1);
    add(spExtendZ1);
    add(spExtendX2);
    add(spExtendY2);
    add(spExtendZ2);

    add( resetButton );
    add( applyButton);
    add(tableLabel);
    add(sp);
    add(cbSphereCut);
    add(spherePx);
    add(spherePy);
    add(spherePz);
    add(sphereRadius);
    add(cbDelete);
    add(cbRegionSelect);
    add(btnRegionClear);
    add(cbRectangleSelect);
  }



  void setValue(){
    for( int i=0; i<Const.PLANE; i++ ){
      tableModel.setValueAt( isOn[i],i,0 );
      tableModel.setValueAt( (double)normalVec[i][0],i,1 );
      tableModel.setValueAt( (double)normalVec[i][1],i,2 );
      tableModel.setValueAt( (double)normalVec[i][2],i,3 );
      tableModel.setValueAt( (double)posVec[i][0],i,4 );
      tableModel.setValueAt( (double)posVec[i][1],i,5 );
      tableModel.setValueAt( (double)posVec[i][2],i,6 );
    }

    spExtendX1.setValue((double)vconf.extendRenderingFactor[0][0]);
    spExtendY1.setValue((double)vconf.extendRenderingFactor[0][1]);
    spExtendZ1.setValue((double)vconf.extendRenderingFactor[0][2]);

    spExtendX2.setValue((double)vconf.extendRenderingFactor[1][0]);
    spExtendY2.setValue((double)vconf.extendRenderingFactor[1][1]);
    spExtendZ2.setValue((double)vconf.extendRenderingFactor[1][2]);


    spherePx.setValue((double)vconf.spherecutPos[0]);
    spherePy.setValue((double)vconf.spherecutPos[1]);
    spherePz.setValue((double)vconf.spherecutPos[2]);
    sphereRadius.setValue((double)vconf.spherecutRadius);
    cbSphereCut.setSelected(vconf.isSphereCut);

  }

  void addTable(){
    Object[] s = new Object[colNames.length];
    for( int i=0; i<Const.PLANE; i++ ){
      s[0] = isOn[i];
      s[1] = (double)normalVec[i][0];
      s[2] = (double)normalVec[i][1];
      s[3] = (double)normalVec[i][2];
      s[4] = (double)posVec[i][0];
      s[5] = (double)posVec[i][1];
      s[6] = (double)posVec[i][2];
      tableModel.addRow( s );
    }
  }

  void updateList(){
    vconf.spherecutPos[0]=(float)((Double)spherePx.getValue()).floatValue();
    vconf.spherecutPos[1]=(float)((Double)spherePy.getValue()).floatValue();
    vconf.spherecutPos[2]=(float)((Double)spherePz.getValue()).floatValue();
    vconf.spherecutRadius=(float)((Double)sphereRadius.getValue()).floatValue();
    vconf.isSphereCut=cbSphereCut.isSelected();

    vconf.extendRenderingFactor[0][0]=(float)((Double)spExtendX1.getValue()).floatValue();
    vconf.extendRenderingFactor[0][1]=(float)((Double)spExtendY1.getValue()).floatValue();
    vconf.extendRenderingFactor[0][2]=(float)((Double)spExtendZ1.getValue()).floatValue();
    vconf.extendRenderingFactor[1][0]=(float)((Double)spExtendX2.getValue()).floatValue();
    vconf.extendRenderingFactor[1][1]=(float)((Double)spExtendY2.getValue()).floatValue();
    vconf.extendRenderingFactor[1][2]=(float)((Double)spExtendZ2.getValue()).floatValue();

    vconf.isDeletionMode=cbDelete.isSelected();
    vconf.isRegionSelectMode=cbRegionSelect.isSelected();
    vconf.isRectangleSelectMode=cbRectangleSelect.isSelected();


    Double data;
    for( int i=0; i<Const.PLANE; i++ ){
      isOn[i]=(Boolean)table.getValueAt(i,0);
      data=(Double)table.getValueAt(i,1);
      normalVec[i][0]=data.floatValue();
      data=(Double)table.getValueAt(i,2);
      normalVec[i][1]=data.floatValue();
      data=(Double)table.getValueAt(i,3);
      normalVec[i][2]=data.floatValue();
      data=(Double)table.getValueAt(i,4);
      posVec[i][0]=data.floatValue();
      data=(Double)table.getValueAt(i,5);
      posVec[i][1]=data.floatValue();
      data=(Double)table.getValueAt(i,6);
      posVec[i][2]=data.floatValue();
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

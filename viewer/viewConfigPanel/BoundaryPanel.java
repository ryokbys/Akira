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
        ctrl.getActiveRW().atmRndr.resetVisualTag();
      }
      ctrl.RWinRefresh();
    }else if(ae.getSource() == applyButton){
      updateList();
      if(ctrl.getActiveRW()!=null){
        ctrl.getActiveRW().atmRndr.setVisualTag();
        ctrl.RWinRefresh();
      }
    }
  }

  public void stateChanged( ChangeEvent ce ){
    updateList();
  }



  JButton resetButton, applyButton;

  final String[] colNames = { "On", "Nx", "Ny", "Nz", "Px","Py","Pz"};
  MyTableModel tableModel;
  JTable table;
  JScrollPane sp;

  private JSpinner spExtendX1,spExtendY1,spExtendZ1;
  private JSpinner spExtendX2,spExtendY2,spExtendZ2;


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




    tableModel = new MyTableModel( colNames, 0 );
    //tableModel.setFocusable(false);
    table = new JTable( tableModel );
    //table.setFocusable(false);

    table.setRowHeight( 20 );
    table.setIntercellSpacing( new Dimension(2,2) );
    table.setColumnSelectionAllowed( true );
    table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
    table.setDefaultRenderer( Color.class, new MyColorRenderer(true));
    table.setDefaultEditor( Color.class, new MyColorEditor() );
    table.setFocusable(false);

    sp = new JScrollPane( table );
    sp.setFocusable(false);
    sp.setWheelScrollingEnabled( true );
    sp.setPreferredSize( new Dimension(300,300) );

    JLabel tableLabel = new JLabel("Plane Slicer");

    addTable();

    DefaultTableColumnModel columnModel
      = (DefaultTableColumnModel)table.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(30);
    columnModel.getColumn(1).setPreferredWidth(30);
    columnModel.getColumn(2).setPreferredWidth(30);
    columnModel.getColumn(3).setPreferredWidth(30);
    columnModel.getColumn(4).setPreferredWidth(50);
    columnModel.getColumn(5).setPreferredWidth(50);
    columnModel.getColumn(6).setPreferredWidth(50);



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

    //Table
    layout.putConstraint( SpringLayout.NORTH, tableLabel, 20, SpringLayout.SOUTH, spExtendZ1 );
    layout.putConstraint( SpringLayout.WEST, tableLabel, 0, SpringLayout.WEST, spExtendZ1 );
    layout.putConstraint( SpringLayout.NORTH, sp, 0, SpringLayout.SOUTH, tableLabel );
    layout.putConstraint( SpringLayout.WEST, sp, 0 ,SpringLayout.WEST, tableLabel );
    layout.putConstraint( SpringLayout.EAST, sp, -20 ,SpringLayout.EAST, this );
    layout.putConstraint( SpringLayout.SOUTH, sp, -60 ,SpringLayout.SOUTH, this );

    //Button: Reset
    layout.putConstraint( SpringLayout.NORTH, resetButton, 0, SpringLayout.SOUTH, sp );
    layout.putConstraint( SpringLayout.EAST,  resetButton, 0, SpringLayout.EAST,  sp);
    layout.putConstraint( SpringLayout.SOUTH,  resetButton, -20, SpringLayout.SOUTH, this );
    //Button: Apply
    layout.putConstraint( SpringLayout.NORTH, applyButton, 0, SpringLayout.SOUTH, sp);
    layout.putConstraint( SpringLayout.WEST,  applyButton, 0, SpringLayout.WEST,  sp );
    layout.putConstraint( SpringLayout.SOUTH,  applyButton, -20, SpringLayout.SOUTH, this );




    setLayout( layout );
    add(lExtend);
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

    vconf.extendRenderingFactor[0][0]=(float)((Double)spExtendX1.getValue()).floatValue();
    vconf.extendRenderingFactor[0][1]=(float)((Double)spExtendY1.getValue()).floatValue();
    vconf.extendRenderingFactor[0][2]=(float)((Double)spExtendZ1.getValue()).floatValue();
    vconf.extendRenderingFactor[1][0]=(float)((Double)spExtendX2.getValue()).floatValue();
    vconf.extendRenderingFactor[1][1]=(float)((Double)spExtendY2.getValue()).floatValue();
    vconf.extendRenderingFactor[1][2]=(float)((Double)spExtendZ2.getValue()).floatValue();

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

  // Private class that provides table
  class MyTableModel extends DefaultTableModel {
    MyTableModel( String[] columnNames, int rowNum ){
      super( columnNames, rowNum );
    }
    public Class getColumnClass(int c){
      return getValueAt(0, c).getClass();
    }
  }

}

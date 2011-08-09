package viewer.viewConfigPanel;

import java.io.*;
import java.util.*;
import javax.swing.table.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import javax.swing.event.*;


import viewer.*;
import data.*;
import tools.*;
import viewer.renderer.*;

public class AtomPanel extends JPanel implements ActionListener,ChangeListener{
  /* accesser */
  public void updateStatus(){
    RenderingWindow rw=ctrl.getActiveRW();
    if(rw==null)return;
    if(rw.visibleAtoms){
      atomType.setSelectedIndex(rw.renderingAtomType+1);
    }else{
      atomType.setSelectedIndex(0);
    }

    atomColor.setSelectedIndex(rw.renderingAtomDataIndex);
    if(rw.visibleLabel){
      labelType.setSelectedIndex(rw.atomLabelType+1);
    }else{
      labelType.setSelectedIndex(0);
    }

  }
  /* accesser */


  public void stateChanged(ChangeEvent ce){
    vconf.isSelectionInfo=cbSelectionMode.isSelected();
    vconf.isSelectionLength=cbLength.isSelected();
    vconf.isSelectionAngle=cbAngle.isSelected();
    vconf.isSelectionTorsion=cbTorsion.isSelected();
  }

  // called when the event happens
  public void actionPerformed( ActionEvent ae){

    if( ae.getSource() == applyButton){
      RenderingWindow rw=ctrl.getActiveRW();
      if(rw==null)return;
      switch(atomType.getSelectedIndex()){
      case 0:
        rw.visibleAtoms=false;
        break;
      case 1:
        rw.renderingAtomType=0;
        rw.visibleAtoms=true;
        break;
      case 2:
        rw.renderingAtomType=1;
        rw.visibleAtoms=true;
        break;
      case 3:
        rw.renderingAtomType=2;
        rw.visibleAtoms=true;
        break;
      }
      rw.renderingAtomDataIndex=atomColor.getSelectedIndex();
      switch(labelType.getSelectedIndex()){
      case 0:
        rw.visibleLabel=false;
        break;
      case 1:
        rw.atomLabelType=0;//ID
        rw.visibleLabel=true;
        break;
      case 2:
        rw.atomLabelType=1;//species
        rw.visibleLabel=true;
        break;
      case 3:
        rw.atomLabelType=2;//tag name
        rw.visibleLabel=true;
        break;
      case 4:
        rw.atomLabelType=3;//tagname+id
        rw.visibleLabel=true;
        break;
      case 5:
        rw.atomLabelType=4;//tagname+counter
        rw.visibleLabel=true;
        break;
      }



      updateList();
      ctrl.remakePrimitiveObjects();
      ctrl.RWinRefresh();
    }else if( ae.getSource() == resetButton ){
      vconf.resetAtom();
      applyModification2Table();
      ctrl.RWinRefresh();
    }

    requestFocus();
  }



  Controller ctrl;
  ViewConfig vconf;
  //constructor
  public AtomPanel(Controller ctrl){
    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;
    createPanel();
  }




  JButton applyButton;
  private JButton resetButton;
  final String[] colNames = { "Tag", "Name", "On/Off", "Radius",
                              "Slices", "Stacks","Color" };
  MyTableModel tableModel;
  JTable table;

  private String[] atStr = {"Invisible","Point","Sphere","Cartoon"};
  JComboBox atomType;
  private String[] acStr = {"Tag","Data1","Data2","Data3","Data4","Data5","Data6","Data7","Data8","Data9"};
  JComboBox atomColor;

  JCheckBox cbSelectionMode;
  JCheckBox cbLength;
  JCheckBox cbAngle;
  JCheckBox cbTorsion;

  JComboBox labelType;

  public void createPanel(){


    String[] lStr={"Invisible","ID","Species","Tag Name","Tag Name+ID","Tag Name+Num."};
    labelType= new JComboBox(lStr);
    labelType.setSelectedIndex(0);
    labelType.addActionListener(this);

    atomType= new JComboBox(atStr);
    atomType.setSelectedIndex(1);
    atomColor= new JComboBox(acStr);
    atomColor.setSelectedIndex(0);

    //General panel
    setFocusable( false );

    applyButton = new JButton( "Apply" );
    applyButton.addActionListener( this );
    applyButton.setFocusable(false);

    resetButton  = new JButton( "Reset" );
    resetButton.setFocusable(false);
    resetButton.addActionListener( this );

    ///
    cbSelectionMode =new JCheckBox("Select Info",vconf.isSelectionInfo);
    cbSelectionMode.setFocusable(false);
    cbSelectionMode.addChangeListener(this);

    cbLength =new JCheckBox("Select Length",vconf.isSelectionLength);
    cbLength.setFocusable(false);
    cbLength.addChangeListener(this);

    cbAngle =new JCheckBox("Select Angle",vconf.isSelectionAngle);
    cbAngle.setFocusable(false);
    cbAngle.addChangeListener(this);

    cbTorsion =new JCheckBox("Select Torsion",vconf.isSelectionTorsion);
    cbTorsion.setFocusable(false);
    cbTorsion.addChangeListener(this);


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

    JScrollPane sp = new JScrollPane( table );
    sp.setFocusable(false);
    sp.setWheelScrollingEnabled( true );
    sp.setPreferredSize( new Dimension(500,100) );


    addTableRow();

    DefaultTableColumnModel columnModel
      = (DefaultTableColumnModel)table.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(2);
    columnModel.getColumn(1).setPreferredWidth(10);
    columnModel.getColumn(2).setPreferredWidth(4);
    columnModel.getColumn(3).setPreferredWidth(4);
    columnModel.getColumn(4).setPreferredWidth(4);
    columnModel.getColumn(5).setPreferredWidth(4);







    SpringLayout layout = new SpringLayout();
    setLayout( layout );

    JLabel atomTypeLabel=new JLabel("Rendering Type");
    layout.putConstraint( SpringLayout.NORTH, atomTypeLabel, 10, SpringLayout.NORTH, this );
    layout.putConstraint( SpringLayout.WEST,  atomTypeLabel, 10, SpringLayout.WEST, this );
    layout.putConstraint( SpringLayout.NORTH, atomType, 0, SpringLayout.SOUTH, atomTypeLabel);
    layout.putConstraint( SpringLayout.WEST, atomType, 5, SpringLayout.WEST, atomTypeLabel);

    JLabel atomColorLabel=new JLabel("Atom Color");
    layout.putConstraint( SpringLayout.NORTH, atomColorLabel, 5, SpringLayout.SOUTH, atomType);
    layout.putConstraint( SpringLayout.WEST,  atomColorLabel, 10, SpringLayout.WEST, this );
    layout.putConstraint( SpringLayout.NORTH, atomColor, 0, SpringLayout.SOUTH, atomColorLabel);
    layout.putConstraint( SpringLayout.WEST,  atomColor, 5, SpringLayout.WEST, atomColorLabel);

    layout.putConstraint( SpringLayout.SOUTH, sp, -10, SpringLayout.SOUTH, this );
    layout.putConstraint( SpringLayout.NORTH, sp, 10, SpringLayout.NORTH, this );
    layout.putConstraint( SpringLayout.WEST, sp, 5, SpringLayout.EAST, atomTypeLabel);


    layout.putConstraint( SpringLayout.SOUTH, applyButton, 0,
                          SpringLayout.SOUTH, sp );
    layout.putConstraint( SpringLayout.WEST, applyButton, 5,
                          SpringLayout.EAST, sp  );
    layout.putConstraint( SpringLayout.NORTH, resetButton, 0,
                          SpringLayout.NORTH, applyButton );
    layout.putConstraint( SpringLayout.WEST, resetButton, 5,
                          SpringLayout.EAST, applyButton  );

    //label
    JLabel lLabel=new JLabel("Label");
    add(lLabel);
    layout.putConstraint( SpringLayout.NORTH, lLabel, 10, SpringLayout.NORTH,this);
    layout.putConstraint( SpringLayout.WEST,  lLabel, 10, SpringLayout.EAST, sp);
    layout.putConstraint( SpringLayout.NORTH, labelType, 0, SpringLayout.SOUTH, lLabel);
    layout.putConstraint( SpringLayout.WEST,  labelType, 5, SpringLayout.WEST, lLabel);


    layout.putConstraint( SpringLayout.NORTH, cbSelectionMode, 10,
                          SpringLayout.NORTH, this);
    layout.putConstraint( SpringLayout.WEST, cbSelectionMode, 10,
                          SpringLayout.EAST, labelType);
    layout.putConstraint( SpringLayout.NORTH, cbLength, 0,
                          SpringLayout.SOUTH, cbSelectionMode);
    layout.putConstraint( SpringLayout.WEST, cbLength, 0,
                          SpringLayout.WEST, cbSelectionMode);
    layout.putConstraint( SpringLayout.NORTH, cbAngle, 0,
                          SpringLayout.SOUTH, cbLength);
    layout.putConstraint( SpringLayout.WEST, cbAngle, 0,
                          SpringLayout.WEST, cbLength);
    layout.putConstraint( SpringLayout.NORTH, cbTorsion, 0,
                          SpringLayout.SOUTH, cbAngle);
    layout.putConstraint( SpringLayout.WEST, cbTorsion, 0,
                          SpringLayout.WEST, cbAngle);


    add(labelType);

    add(atomTypeLabel);
    add(atomColorLabel);
    add( atomType );
    add( atomColor );
    add( sp );

    add(cbSelectionMode);
    add(cbLength);
    add(cbAngle);
    add(cbTorsion);
    add( applyButton );
    add( resetButton );

  }

  void applyModification2Table(){
    int[] tags=ctrl.RWin[ctrl.activeRWinID].getTags();
    for( int j=0; j<tags.length; j++ ){
      int i=tags[j]-1;
      if(i==100)continue;//skip volume data
      tableModel.setValueAt( String.valueOf( i+1 ),j,0 );
      tableModel.setValueAt( vconf.tagName[i],j,1 );
      tableModel.setValueAt( vconf.tagOnOff[i] ,j,2 );
      tableModel.setValueAt( String.format("%.3f",vconf.tagRadius[i] ),j,3 );
      tableModel.setValueAt( String.format("%d",vconf.tagSlice[i] ),j,4 );
      tableModel.setValueAt( String.format("%d",vconf.tagStack[i] ),j,5 );

      tableModel.setValueAt(new Color(vconf.tagColor[i][0],vconf.tagColor[i][1],
                                      vconf.tagColor[i][2],vconf.tagColor[i][3]) ,j,6 );
    }
  }

  public void addTableRow(){
    //clear table
    int n=tableModel.getRowCount();
    for( int j=0; j<n; j++ ){
      tableModel.removeRow(0);
    }
    //add table
    Object[] s = new Object[colNames.length];
    if(ctrl.activeRWinID>=0 && ctrl.RWin[ctrl.activeRWinID]!=null){
      int[] tags=ctrl.RWin[ctrl.activeRWinID].getTags();
      for( int j=0; j<tags.length; j++ ){
        int i=tags[j]-1;
        if(i==100)continue;//skip volume data
        s[0] = String.valueOf( i+1 );
        s[1] = vconf.tagName[i];
        s[2] = vconf.tagOnOff[i];
        s[3] = String.format("%.3f",vconf.tagRadius[i] );
        s[4] = String.format("%d",vconf.tagSlice[i] );
        s[5] = String.format("%d",vconf.tagStack[i] );
        s[6] = new Color(vconf.tagColor[i][0],vconf.tagColor[i][1],
                         vconf.tagColor[i][2],vconf.tagColor[i][3]);
        tableModel.addRow( s );
      }
    }
  }
  private void updateList(){
    RenderingWindow rw=ctrl.getActiveRW();

    int[] tags=rw.getTags();
    for( int j=0; j<tags.length; j++ ){
      int i=tags[j]-1;
      if(i==100)continue;//skip volume data
      vconf.tagName[i]=(String)table.getValueAt(j,1);
      vconf.tagOnOff[i]=(Boolean)table.getValueAt(j,2);
      vconf.tagRadius[i]=Double.valueOf((String)table.getValueAt(j,3));
      vconf.tagSlice[i]=Integer.valueOf((String)table.getValueAt(j,4));
      vconf.tagStack[i]=Integer.valueOf((String)table.getValueAt(j,5));
      Color newColor = (Color)table.getValueAt(j,6);
      vconf.tagColor[i]=newColor.getRGBComponents(null);
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

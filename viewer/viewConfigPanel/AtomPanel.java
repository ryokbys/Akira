package viewer.viewConfigPanel;

import java.io.*;
import java.util.*;
import javax.swing.table.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import javax.swing.event.*;
import info.clearthought.layout.*;

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

    cbSelectionMode.setSelected(vconf.isSelectionInfo);
    cbLength.setSelected(vconf.isSelectionLength);
    cbAngle.setSelected(vconf.isSelectionAngle);
    cbTorsion.setSelected(vconf.isSelectionTorsion);
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
    RenderingWindow rw=ctrl.getActiveRW();
    if(rw==null)return;
    if( ae.getSource() == applyButton){
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
    }else if( ae.getSource() ==  selectButton){
      int id=((Integer)spSelect.getValue()).intValue();
      rw.selectByHuman(id-1);//note: input id starts with 1, but java id starts with 0.
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
  final String[] colNames = { "Tag", "Name", "On", "Radius",
                              "Slices", "Stacks","Color" };
  MyTableModel tableModel;
  JTable table;

  private String[] atStr = {"Invisible","Point","Sphere","Cartoon"};
  JComboBox atomType;
  private String[] acStr = {"Tag","Data1","Data2","Data3","Data4","Data5","Data6","Data7","Data8","Data9"};
  JComboBox atomColor;

  JCheckBox cbSelectionMode;
  private JSpinner spSelect;
  JButton selectButton;
  JCheckBox cbLength;
  JCheckBox cbAngle;
  JCheckBox cbTorsion;
  JComboBox labelType;

  public void createPanel(){
    this.addKeyListener(ctrl.keyCtrl);
    //General panel
    setFocusable( false );

    String[] lStr={"Invisible","ID","Species","Tag Name","Tag Name+ID","Tag Name+Num."};
    labelType= new JComboBox(lStr);
    labelType.setSelectedIndex(0);
    labelType.addActionListener(this);
    labelType.setFocusable(false);

    atomType= new JComboBox(atStr);
    atomType.setSelectedIndex(1);
    atomType.setFocusable(false);
    atomType.addKeyListener(ctrl.keyCtrl);

    atomColor= new JComboBox(acStr);
    atomColor.setSelectedIndex(0);
    atomColor.setFocusable(false);
    atomColor.addKeyListener(ctrl.keyCtrl);

    applyButton = new JButton( "Apply" );
    applyButton.addActionListener( this );
    applyButton.setFocusable(false);
    applyButton.addKeyListener(ctrl.keyCtrl);

    resetButton  = new JButton( "Reset" );
    resetButton.setFocusable(false);
    resetButton.addActionListener( this );
    resetButton.addKeyListener(ctrl.keyCtrl);

    selectButton  = new JButton( "ID Select" );
    selectButton.setFocusable(false);
    selectButton.addActionListener( this );
    selectButton.addKeyListener(ctrl.keyCtrl);

    ///
    vconf.isSelectionInfo=false;
    cbSelectionMode =new JCheckBox("atom info",vconf.isSelectionInfo);
    cbSelectionMode.setFocusable(false);
    cbSelectionMode.addChangeListener(this);
    cbSelectionMode.addKeyListener(ctrl.keyCtrl);

    vconf.isSelectionLength=false;
    cbLength =new JCheckBox("bond length",vconf.isSelectionLength);
    cbLength.setFocusable(false);
    cbLength.addChangeListener(this);

    vconf.isSelectionAngle=false;
    cbAngle =new JCheckBox("bond angle",vconf.isSelectionAngle);
    cbAngle.setFocusable(false);
    cbAngle.addChangeListener(this);

    vconf.isSelectionTorsion=false;
    cbTorsion =new JCheckBox("torsion angle",vconf.isSelectionTorsion);
    cbTorsion.setFocusable(false);
    cbTorsion.addChangeListener(this);



    tableModel = new MyTableModel( colNames, 0 );
    //tableModel.setFocusable(false);
    table = new JTable( tableModel );
    table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    table.setRowHeight( 20 );
    table.setIntercellSpacing( new Dimension(2,2) );
    table.setColumnSelectionAllowed( true );

    table.setDefaultRenderer( Color.class, new MyColorRenderer(true));
    table.setDefaultEditor( Color.class, new MyColorEditor() );
    table.setFocusable(false);

    JScrollPane sp = new JScrollPane( table );
    sp.setFocusable(false);
    sp.setWheelScrollingEnabled( true );
    //sp.setPreferredSize( new Dimension(500,100) );
    sp.addKeyListener(ctrl.keyCtrl);

    addTableRow();

    DefaultTableColumnModel columnModel
      = (DefaultTableColumnModel)table.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(10);
    columnModel.getColumn(1).setPreferredWidth(30);
    columnModel.getColumn(2).setPreferredWidth(10);
    columnModel.getColumn(3).setPreferredWidth(20);
    columnModel.getColumn(4).setPreferredWidth(20);
    columnModel.getColumn(5).setPreferredWidth(20);
    columnModel.getColumn(6).setPreferredWidth(4);

    JLabel atomTypeLabel=new JLabel("Rendering Type:");
    JLabel atomColorLabel=new JLabel("Atom Color:");
    JLabel lLabel=new JLabel("Label:");
    JLabel selLabel= new JLabel("Select:");

    //Panel for selection info,length,angle,torsion
    JPanel selPanel= new JPanel();
    SpringLayout layout= new SpringLayout();
    String north= SpringLayout.NORTH;
    String south= SpringLayout.SOUTH;
    String east = SpringLayout.EAST;
    String west = SpringLayout.WEST;
    selPanel.setLayout( layout );
    selLabel.setFocusable( false );
    layout.putConstraint( north, selLabel, 10, north, selPanel );
    layout.putConstraint( west,  selLabel, 10, west, selPanel );
    layout.putConstraint( north, cbSelectionMode, 5, south, selLabel );
    layout.putConstraint( west, cbSelectionMode, 0, west, selLabel );
    layout.putConstraint( north, cbLength, 0, south, cbSelectionMode );
    layout.putConstraint( west,  cbLength, 0, west, cbSelectionMode );
    layout.putConstraint( north, cbAngle, 0, south, cbLength );
    layout.putConstraint( west,  cbAngle, 0, west, cbLength );
    layout.putConstraint( north, cbTorsion, 0, south, cbAngle );
    layout.putConstraint( west,  cbTorsion, 0, west, cbAngle );
    selPanel.add( selLabel );
    selPanel.add( cbSelectionMode );
    selPanel.add( cbLength );
    selPanel.add( cbAngle );
    selPanel.add( cbTorsion );

    //.....table layout from here
    //-----constants for TableLayout
    // f : FILL
    // p : PREFERRED
    // vb: vertical border
    // vg: vertical gap between elements
    // hb: horizontal border
    // hs: horizontal space between labels and fields
    // hg: horizontal gap between elements
    double f= TableLayout.FILL;
    double p= TableLayout.PREFERRED;
    double vg= 10;
    double hg= 5;
    
    //horizontal grid
    double colSizeTL[]={vg,150,vg,f,vg};
                        
    //vertical grid
    double rowSizeTL[]={hg,
                        20,// 1:rendering type
                        30,
                        20,// 3:atom color
                        30,
                        20,// 5:label
                        30,
                        hg,
                        f, // 8:table
                        hg,
                        30,//10:apply button 
                        hg
    };

    setLayout(new TableLayout(colSizeTL,rowSizeTL));
    
    add( atomTypeLabel, "1, 1, f, f" );// atom type
    add( atomType, "1, 2, f, f" );
    add( atomColorLabel, "1, 3, f, f" );// atom color
    add( atomColor, "1, 4, f, f" );
    add( lLabel, "1,5, f, f" );// label type
    add( labelType, "1, 6, f, f" );
    add( selPanel, "3, 1, 3, 6" ); // selection panel
    add( sp, "1, 8, 3, 8" );
    add( applyButton, "1, 10, f, f" );
    add( resetButton, "3, 10, f, f" );
  }

  void applyModification2Table(){
    byte[] tags=ctrl.RWin[ctrl.activeRWinID].getTags();
    for( int j=0; j<tags.length; j++ ){
      int i=(int)tags[j]-1;
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
      byte[] tags=ctrl.RWin[ctrl.activeRWinID].getTags();
      for( int j=0; j<tags.length; j++ ){
        int i=(int)tags[j]-1;
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

    byte[] tags=rw.getTags();
    for( int j=0; j<tags.length; j++ ){
      int i=(int)tags[j]-1;
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

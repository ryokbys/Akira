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
import viewer.LF.*;
import converter.*;

public class BondPanel extends JPanel implements ActionListener{
  /* accesser */
  public void updateStatus(){
    RenderingWindow rw=ctrl.getActiveRW();
    if(rw==null)return;
    if(rw.visibleBonds){
      bondType.setSelectedIndex(rw.renderingBondType+1);
    }else{
      bondType.setSelectedIndex(0);
    }

    bondColor.setSelectedIndex(rw.renderingBondColorType);

  }
  /* accesser */


  private Controller ctrl;
  private ViewConfig vconf;
  //constructor
  public BondPanel(Controller ctrl){
    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;

    createPanel();
    bondCreator= new BondCreator();
  }
  private BondCreator bondCreator;

  public void actionPerformed(ActionEvent ae){
    getValue();
    if(ae.getSource() == applyButton){

      RenderingWindow rw=ctrl.getActiveRW();
      if(rw==null)return;
      switch(bondType.getSelectedIndex()){
      case 0:
        rw.visibleBonds=false;
        break;
      case 1:
        rw.renderingBondType=0;
        rw.visibleBonds=true;
        break;
      case 2:
        rw.renderingBondType=1;
        rw.visibleBonds=true;
        break;
      }
      rw.renderingBondColorType=bondColor.getSelectedIndex();
      vconf.bondRadius= ((Double)radiusSpinner.getValue()).floatValue();

      ctrl.RWinRefresh();
    }else if(ae.getSource() == resetButton){
      vconf.resetBond();
      setValue();
    }else if(ae.getSource() == setRangeButton){
      vconf.bondLengthRange[1]=(ctrl.getActiveRW()).atoms.getMaxBondLength();
      vconf.bondCNRange[1]=(ctrl.getActiveRW()).atoms.getMaxCoordinationNumber();
      lengthMaxSpinner.setValue((double)vconf.bondLengthRange[1]);
      CNMaxSpinner.setValue((double)vconf.bondCNRange[1]);
    }else if(ae.getSource() == createButton ){
      setListFromTable();
      if(ctrl.activeRWinID>=0 && ctrl.RWin[ctrl.activeRWinID] != null){
        ctrl.RWin[ctrl.activeRWinID].atoms.clearBonds();
        bondCreator.createWithBondLength(ctrl.RWin[ctrl.activeRWinID].atoms);

        ctrl.RWinRefresh();
      }
    }
  }


  private JButton resetButton,applyButton;

  private JSpinner radiusSpinner;
  private JSpinner lengthMinSpinner,lengthMaxSpinner;
  private JSpinner CNMinSpinner,CNMaxSpinner;
  private JButton createButton;
  private JButton setRangeButton;
  private final String[] colNames = { "On", "Tag1", "Tag2", "Length"};
  private MyTableModel tableModel;
  private JTable table;
  private JScrollPane sp;
  private JTextField taLegend,taFormat;

  private String[] btStr={"Invisible","Line","Cylinder"};
  private JComboBox bondType;
  private String[] bcStr={"Atom","Length","Coordination"};
  private JComboBox bondColor;

  private void createPanel(){
    this.addKeyListener(ctrl.keyCtrl);
    //General panel
    setFocusable( false );

    bondType= new JComboBox(btStr);
    bondType.setSelectedIndex(0);
    bondType.addActionListener(this);
    bondType.setFocusable(false);

    bondColor= new JComboBox(bcStr);
    bondColor.setSelectedIndex(0);
    bondColor.addActionListener(this);
    bondColor.setFocusable(false);

    JLabel radiusLabel = new JLabel( "Radius" );
    radiusSpinner = new JSpinner(new SpinnerNumberModel((double)vconf.bondRadius, 0.0, null, 0.1));
    radiusSpinner.setFocusable(false);
    radiusSpinner.setPreferredSize(new Dimension(60, 25));


    JLabel lengthRangeLabel= new JLabel("Length Range:");
    JLabel lengthMinLabel=new JLabel("Min");
    lengthMinSpinner = new JSpinner(new SpinnerNumberModel((double)vconf.bondLengthRange[0], 0., null, 1.));
    lengthMinSpinner.setFocusable(false);
    lengthMinSpinner.setPreferredSize(new Dimension(90, 25));
    lengthMinSpinner.setEditor(new JSpinner.NumberEditor(lengthMinSpinner, "0.####E0"));

    JLabel lengthMaxLabel=new JLabel("Max");
    lengthMaxSpinner = new JSpinner(new SpinnerNumberModel((double)vconf.bondLengthRange[1], 0., null, 1.));
    lengthMaxSpinner.setFocusable(false);
    lengthMaxSpinner.setPreferredSize(new Dimension(90, 25));
    lengthMaxSpinner.setEditor(new JSpinner.NumberEditor(lengthMaxSpinner, "0.####E0"));

    JLabel CNRangeLabel= new JLabel("CN Range:");
    JLabel CNMinLabel=new JLabel("Min");
    CNMinSpinner = new JSpinner(new SpinnerNumberModel((double)vconf.bondCNRange[0], 0., null, 1.));
    CNMinSpinner.setFocusable(false);
    CNMinSpinner.setPreferredSize(new Dimension(90, 25));
    CNMinSpinner.setEditor(new JSpinner.NumberEditor(CNMinSpinner, "0.####E0"));

    JLabel CNMaxLabel=new JLabel("Max");
    CNMaxSpinner = new JSpinner(new SpinnerNumberModel((double)vconf.bondCNRange[1], 0., null, 1.));
    CNMaxSpinner.setFocusable(false);
    CNMaxSpinner.setPreferredSize(new Dimension(90, 25));
    CNMaxSpinner.setEditor(new JSpinner.NumberEditor(CNMaxSpinner, "0.####E0"));

    //temporary create
    setRangeButton = new JButton("Load Original Range");
    setRangeButton.setToolTipText("Load original range");
    setRangeButton.addActionListener( this );
    setRangeButton.setFocusable(false);

    //temporary create
    createButton = new JButton("Create Bonds");
    createButton.setToolTipText("Create");
    createButton.addActionListener( this );
    createButton.setFocusable(false);



    JLabel lLegend=new JLabel("Lagend");
    taLegend = new JTextField(vconf.bondLegend);
    taLegend.setEditable(true);
    taLegend.setFocusable(false);
    JLabel lFormat=new JLabel("Format");
    taFormat = new JTextField(vconf.bondColorTableFormat);
    taFormat.setFocusable(false);
    taFormat.setEditable(true);
    taLegend.setPreferredSize(new Dimension(65, 25));
    taFormat.setPreferredSize(new Dimension(65, 25));


    tableModel = new MyTableModel( colNames, 0 );
    table = new JTable( tableModel );
    table.setRowHeight( 20 );
    table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
    table.setIntercellSpacing( new Dimension(2,2) );
    table.setColumnSelectionAllowed( true );

    //table.setDefaultRenderer( Color.class, new MyColorRenderer(true));
    table.setDefaultEditor( Color.class, new MyColorEditor() );
    table.setFocusable(false);

    sp = new JScrollPane( table );
    sp.setFocusable(false);
    sp.setWheelScrollingEnabled( true );
    //sp.setPreferredSize( new Dimension(200,100) );

    addTable();

    DefaultTableColumnModel columnModel
      = (DefaultTableColumnModel)table.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(30);
    columnModel.getColumn(1).setPreferredWidth(50);
    columnModel.getColumn(2).setPreferredWidth(50);
    columnModel.getColumn(3).setPreferredWidth(50);


    applyButton  = new JButton( "Apply" );
    applyButton.setFocusable(false);
    applyButton.addActionListener( this );
    resetButton  = new JButton( "Reset" );
    resetButton.setFocusable(false);
    resetButton.addActionListener( this );

    SpringLayout layout = new SpringLayout();
    setLayout( layout );

    JLabel bondTypeLabel =new JLabel("Type");
    layout.putConstraint( SpringLayout.NORTH, bondTypeLabel, 10, SpringLayout.NORTH, this );
    layout.putConstraint( SpringLayout.WEST,  bondTypeLabel, 20, SpringLayout.WEST, this );
    layout.putConstraint( SpringLayout.NORTH, bondType, 0, SpringLayout.NORTH, bondTypeLabel);
    layout.putConstraint( SpringLayout.WEST,  bondType, 5, SpringLayout.EAST, bondTypeLabel);
    JLabel bondColorLabel =new JLabel("Color");
    layout.putConstraint( SpringLayout.NORTH, bondColorLabel, 5, SpringLayout.SOUTH, bondType);
    layout.putConstraint( SpringLayout.WEST,  bondColorLabel, 0, SpringLayout.WEST, bondTypeLabel );
    layout.putConstraint( SpringLayout.NORTH, bondColor, 0, SpringLayout.NORTH, bondColorLabel);
    layout.putConstraint( SpringLayout.WEST,  bondColor, 5, SpringLayout.EAST, bondColorLabel);

    layout.putConstraint( SpringLayout.NORTH, radiusLabel, 5,SpringLayout.SOUTH, bondColor);
    layout.putConstraint( SpringLayout.WEST, radiusLabel, 0,SpringLayout.WEST, bondTypeLabel );
    layout.putConstraint( SpringLayout.NORTH, radiusSpinner, 0,SpringLayout.NORTH, radiusLabel );
    layout.putConstraint( SpringLayout.WEST, radiusSpinner, 5,SpringLayout.EAST, radiusLabel );

    layout.putConstraint( SpringLayout.NORTH, lLegend, 10,SpringLayout.SOUTH, radiusSpinner);
    layout.putConstraint( SpringLayout.WEST, lLegend, 0,SpringLayout.WEST, bondTypeLabel );
    layout.putConstraint( SpringLayout.NORTH, taLegend, 0,SpringLayout.NORTH, lLegend);
    layout.putConstraint( SpringLayout.WEST, taLegend, 0,SpringLayout.EAST, lLegend);
    layout.putConstraint( SpringLayout.NORTH, lFormat, 10,SpringLayout.SOUTH, taLegend);
    layout.putConstraint( SpringLayout.WEST, lFormat, 0,SpringLayout.WEST, bondTypeLabel );
    layout.putConstraint( SpringLayout.NORTH, taFormat, 0,SpringLayout.NORTH, lFormat);
    layout.putConstraint( SpringLayout.WEST, taFormat, 0,SpringLayout.EAST, lFormat);

    //Length Range:
    layout.putConstraint( SpringLayout.NORTH, lengthRangeLabel, 10, SpringLayout.SOUTH, taFormat );
    layout.putConstraint( SpringLayout.WEST, lengthRangeLabel, 0, SpringLayout.WEST, bondTypeLabel );
    //Length Min label
    layout.putConstraint( SpringLayout.NORTH, lengthMinLabel, 5, SpringLayout.SOUTH, lengthRangeLabel );
    layout.putConstraint( SpringLayout.WEST, lengthMinLabel, 20, SpringLayout.WEST, lengthRangeLabel );
    //Length Min spinner
    layout.putConstraint( SpringLayout.NORTH, lengthMinSpinner, 0, SpringLayout.NORTH, lengthMinLabel);
    layout.putConstraint( SpringLayout.WEST, lengthMinSpinner, 5,SpringLayout.EAST, lengthMinLabel);
    //Length Max label
    layout.putConstraint( SpringLayout.NORTH, lengthMaxLabel, 0,SpringLayout.NORTH, lengthMinSpinner);
    layout.putConstraint( SpringLayout.WEST, lengthMaxLabel, 10,SpringLayout.EAST, lengthMinSpinner);
    //Length Max spinner
    layout.putConstraint( SpringLayout.NORTH, lengthMaxSpinner, 0,SpringLayout.NORTH, lengthMaxLabel);
    layout.putConstraint( SpringLayout.WEST, lengthMaxSpinner, 5,SpringLayout.EAST, lengthMaxLabel);

    //CN Range:
    layout.putConstraint( SpringLayout.NORTH, CNRangeLabel, 10, SpringLayout.SOUTH, lengthMaxSpinner );
    layout.putConstraint( SpringLayout.WEST, CNRangeLabel, 0, SpringLayout.WEST, bondTypeLabel );
    //CN Min Label
    layout.putConstraint( SpringLayout.NORTH, CNMinLabel, 5, SpringLayout.SOUTH, CNRangeLabel);
    layout.putConstraint( SpringLayout.WEST, CNMinLabel, 20, SpringLayout.WEST, CNRangeLabel );
    //CN Min spinner
    layout.putConstraint( SpringLayout.NORTH, CNMinSpinner, 0,SpringLayout.NORTH, CNMinLabel);
    layout.putConstraint( SpringLayout.WEST, CNMinSpinner, 5,SpringLayout.WEST, lengthMinSpinner);
    //CN Max label
    layout.putConstraint( SpringLayout.NORTH, CNMaxLabel, 0,SpringLayout.NORTH, CNMinSpinner);
    layout.putConstraint( SpringLayout.WEST, CNMaxLabel, 10,SpringLayout.EAST, CNMinSpinner);
    //CN Max spinner
    layout.putConstraint( SpringLayout.NORTH, CNMaxSpinner, 0,SpringLayout.NORTH, CNMaxLabel);
    layout.putConstraint( SpringLayout.WEST, CNMaxSpinner, 5,SpringLayout.EAST, CNMaxLabel);

    //"Apply" button
    layout.putConstraint( SpringLayout.NORTH, applyButton, 10,SpringLayout.SOUTH, CNMinSpinner );
    layout.putConstraint( SpringLayout.WEST, applyButton, 0, SpringLayout.WEST, bondTypeLabel );
    //"Load original range" button
    layout.putConstraint( SpringLayout.NORTH, setRangeButton, 0, SpringLayout.NORTH, applyButton);
    layout.putConstraint( SpringLayout.WEST, setRangeButton, 20, SpringLayout.EAST, applyButton );
    //"Reset" button
    layout.putConstraint( SpringLayout.NORTH, resetButton, 0, SpringLayout.NORTH, setRangeButton);
    layout.putConstraint( SpringLayout.WEST, resetButton, 20, SpringLayout.EAST, setRangeButton);

    //"Create" button
    layout.putConstraint( SpringLayout.SOUTH, createButton, -10, SpringLayout.SOUTH, this);
    layout.putConstraint( SpringLayout.EAST, createButton, 0, SpringLayout.EAST, sp);
    //Slider panel
    layout.putConstraint( SpringLayout.SOUTH, sp, 0, SpringLayout.NORTH, createButton);
    layout.putConstraint( SpringLayout.NORTH, sp, 20, SpringLayout.SOUTH, applyButton );
    layout.putConstraint( SpringLayout.EAST, sp, -10, SpringLayout.EAST, this);
    layout.putConstraint( SpringLayout.WEST, sp, 0, SpringLayout.WEST, bondTypeLabel );



    add(bondTypeLabel);
    add(bondType);
    add(radiusLabel);
    add(radiusSpinner);
    add(sp);
    add(createButton);
    add(applyButton);
    add(resetButton);

    add(bondColorLabel);
    add(bondColor);
    add(setRangeButton);
    add(lengthRangeLabel);
    add(lengthMinLabel);
    add(lengthMinSpinner);
    add(lengthMaxLabel);
    add(lengthMaxSpinner);
    add(CNRangeLabel);
    add(CNMinLabel);
    add(CNMinSpinner);
    add(CNMaxLabel);
    add(CNMaxSpinner);


    add(lFormat);
    add(lLegend);
    add(taFormat);
    add(taLegend);


  }

  final int maxNBond=9;
  void addTable(){
    Object[] s = new Object[colNames.length];
    for( int i=1; i<maxNBond; i++ ){
      for( int j=i+1; j<=maxNBond; j++ ){
        s[0] = new Boolean(false);
        s[1] = String.valueOf(i);
        s[2] = String.valueOf(j);
        s[3] = String.valueOf(4);
        tableModel.addRow( s );
      }
    }
  }
  void setListFromTable(){
    int s = table.getRowCount();
    bondCreator.atom1List.clear();
    bondCreator.atom2List.clear();
    bondCreator.lengthList.clear();
    for( int i=0; i<s; i++ ){
      if(Boolean.valueOf((Boolean)table.getValueAt(i,0))){
        bondCreator.atom1List.add(Integer.valueOf((String)table.getValueAt(i,1)));
        bondCreator.atom2List.add(Integer.valueOf((String)table.getValueAt(i,2)));
        bondCreator.lengthList.add(Float.valueOf((String)table.getValueAt(i,3)));
      }
    }
  }


  private void getValue(){
    vconf.bondLengthRange[0]=((Double)lengthMinSpinner.getValue()).floatValue();
    vconf.bondLengthRange[1]=((Double)lengthMaxSpinner.getValue()).floatValue();
    vconf.bondCNRange[0]=((Double)CNMinSpinner.getValue()).floatValue();
    vconf.bondCNRange[1]=((Double)CNMaxSpinner.getValue()).floatValue();
    vconf.bondLegend=taLegend.getText();
    vconf.bondColorTableFormat=taFormat.getText();
  }
  private void setValue(){
    radiusSpinner.setValue((double)vconf.bondRadius);
    lengthMinSpinner.setValue((double)vconf.bondLengthRange[0]);
    lengthMaxSpinner.setValue((double)vconf.bondLengthRange[1]);
    CNMinSpinner.setValue((double)vconf.bondCNRange[0]);
    CNMaxSpinner.setValue((double)vconf.bondCNRange[1]);
    taLegend.setText(vconf.bondLegend);
    taFormat.setText(vconf.bondColorTableFormat);
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

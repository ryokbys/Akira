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

      ctrl.RWinRefresh();
    }else if(ae.getSource() == resetButton){
      vconf.resetBond();
      setValue();
    }else if(ae.getSource() == setRangeButton){
      vconf.bondLengthRange[1]=(ctrl.getActiveRW()).bonds.maxBondLength;
      vconf.bondCNRange[1]=(ctrl.getActiveRW()).bonds.maxCN;
      lengthMaxSpinner.setValue((double)vconf.bondLengthRange[1]);
      CNMaxSpinner.setValue((double)vconf.bondCNRange[1]);
    }else if(ae.getSource() == createButton ){
      setListFromTable();
      if(ctrl.activeRWinID>=0 && ctrl.RWin[ctrl.activeRWinID] != null){
        ctrl.RWin[ctrl.activeRWinID].bonds.clear();
        bondCreator.create(ctrl.RWin[ctrl.activeRWinID].atoms,
                           ctrl.RWin[ctrl.activeRWinID].bonds);

        ctrl.RWinRefresh();
      }
    }
  }


  private JButton resetButton,applyButton;

  private JSpinner radiusSpinner;
  private JSpinner sliceSpinner;
  private JSpinner lengthMinSpinner,lengthMaxSpinner;
  private JSpinner CNMinSpinner,CNMaxSpinner;
  private JButton createButton;
  private JButton setRangeButton;
  private final String[] colNames = { "On/Off", "Tag1", "Tag2", "Length"};
  private MyTableModel tableModel;
  private JTable table;
  private JScrollPane sp;
  private JTextField taLegend,taFormat;

  private String[] btStr={"Invisible","Line","Cylinder"};
  private JComboBox bondType;
  private String[] bcStr={"Atom","Length","Coordination"};
  private JComboBox bondColor;

  private void createPanel(){
    //General panel
    setFocusable( false );

    bondType= new JComboBox(btStr);
    bondType.setSelectedIndex(0);
    bondType.addActionListener(this);
    bondColor= new JComboBox(bcStr);
    bondColor.setSelectedIndex(0);
    bondColor.addActionListener(this);

    JLabel radiusLabel = new JLabel( "Radius" );
    radiusSpinner = new JSpinner(new SpinnerNumberModel((double)vconf.bondRadius, 0.0, null, 0.1));
    radiusSpinner.setFocusable(false);
    radiusSpinner.setPreferredSize(new Dimension(60, 25));

    JLabel sliceLabel = new JLabel( "Slice" );
    sliceSpinner = new JSpinner(new SpinnerNumberModel(vconf.bondSlice, 1, null, 1));
    sliceSpinner.setFocusable(false);
    sliceSpinner.setPreferredSize(new Dimension(60, 25));

    JLabel lengthMinLabel=new JLabel("Length Range Min");
    lengthMinSpinner = new JSpinner(new SpinnerNumberModel((double)vconf.bondLengthRange[0], 0., null, 1.));
    lengthMinSpinner.setFocusable(false);
    lengthMinSpinner.setPreferredSize(new Dimension(90, 25));
    lengthMinSpinner.setEditor(new JSpinner.NumberEditor(lengthMinSpinner, "0.####E0"));

    JLabel lengthMaxLabel=new JLabel("Max");
    lengthMaxSpinner = new JSpinner(new SpinnerNumberModel((double)vconf.bondLengthRange[1], 0., null, 1.));
    lengthMaxSpinner.setFocusable(false);
    lengthMaxSpinner.setPreferredSize(new Dimension(90, 25));
    lengthMaxSpinner.setEditor(new JSpinner.NumberEditor(lengthMaxSpinner, "0.####E0"));

    JLabel CNMinLabel=new JLabel("CN Range Min");
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
    setRangeButton.setToolTipText("Create");
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
    JLabel lFormat=new JLabel("Format");
    taFormat = new JTextField(vconf.bondColorTableFormat);
    taFormat.setEditable(true);
    taLegend.setPreferredSize(new Dimension(65, 25));
    taFormat.setPreferredSize(new Dimension(65, 25));


    tableModel = new MyTableModel( colNames, 0 );
    //tableModel.setFocusable(false);
    table = new JTable( tableModel );
    //table.setFocusable(false);

    table.setRowHeight( 20 );
    table.setIntercellSpacing( new Dimension(2,2) );
    table.setColumnSelectionAllowed( true );

    //table.setDefaultRenderer( Color.class, new MyColorRenderer(true));
    table.setDefaultEditor( Color.class, new MyColorEditor() );
    table.setFocusable(false);

    sp = new JScrollPane( table );
    sp.setFocusable(false);
    sp.setWheelScrollingEnabled( true );
    sp.setPreferredSize( new Dimension(200,100) );

    addTable();

    DefaultTableColumnModel columnModel
      = (DefaultTableColumnModel)table.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(8);
    columnModel.getColumn(1).setPreferredWidth(4);
    columnModel.getColumn(2).setPreferredWidth(4);
    columnModel.getColumn(3).setPreferredWidth(4);


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
    layout.putConstraint( SpringLayout.WEST,  bondTypeLabel, 10, SpringLayout.WEST, this );
    layout.putConstraint( SpringLayout.NORTH, bondType, 0, SpringLayout.NORTH, bondTypeLabel);
    layout.putConstraint( SpringLayout.WEST,  bondType, 5, SpringLayout.EAST, bondTypeLabel);
    JLabel bondColorLabel =new JLabel("Color");
    layout.putConstraint( SpringLayout.NORTH, bondColorLabel, 5, SpringLayout.SOUTH, bondType);
    layout.putConstraint( SpringLayout.WEST,  bondColorLabel, 10, SpringLayout.WEST, this );
    layout.putConstraint( SpringLayout.NORTH, bondColor, 0, SpringLayout.NORTH, bondColorLabel);
    layout.putConstraint( SpringLayout.WEST,  bondColor, 5, SpringLayout.EAST, bondColorLabel);


    layout.putConstraint( SpringLayout.NORTH, radiusLabel, 5,SpringLayout.SOUTH, bondColor);
    layout.putConstraint( SpringLayout.WEST, radiusLabel, 10,SpringLayout.WEST, this);
    layout.putConstraint( SpringLayout.NORTH, radiusSpinner, 0,SpringLayout.NORTH, radiusLabel );
    layout.putConstraint( SpringLayout.WEST, radiusSpinner, 5,SpringLayout.EAST, radiusLabel );

    layout.putConstraint( SpringLayout.NORTH, lLegend, 10,SpringLayout.SOUTH, radiusSpinner);
    layout.putConstraint( SpringLayout.WEST, lLegend, 0,SpringLayout.WEST, radiusLabel);
    layout.putConstraint( SpringLayout.NORTH, taLegend, 0,SpringLayout.NORTH, lLegend);
    layout.putConstraint( SpringLayout.WEST, taLegend, 0,SpringLayout.EAST, lLegend);
    layout.putConstraint( SpringLayout.NORTH, lFormat, 0,SpringLayout.NORTH, taLegend);
    layout.putConstraint( SpringLayout.WEST, lFormat, 10,SpringLayout.EAST, taLegend);
    layout.putConstraint( SpringLayout.NORTH, taFormat, 0,SpringLayout.NORTH, lFormat);
    layout.putConstraint( SpringLayout.WEST, taFormat, 0,SpringLayout.EAST, lFormat);


    layout.putConstraint( SpringLayout.NORTH, lengthMinLabel, 10,SpringLayout.NORTH,this);
    layout.putConstraint( SpringLayout.WEST, lengthMinLabel, 10,SpringLayout.EAST,bondColor);
    layout.putConstraint( SpringLayout.NORTH, lengthMinSpinner, 0,SpringLayout.NORTH, lengthMinLabel);
    layout.putConstraint( SpringLayout.WEST, lengthMinSpinner, 5,SpringLayout.EAST, lengthMinLabel);
    layout.putConstraint( SpringLayout.NORTH, lengthMaxLabel, 0,SpringLayout.NORTH, lengthMinSpinner);
    layout.putConstraint( SpringLayout.WEST, lengthMaxLabel, 10,SpringLayout.EAST, lengthMinSpinner);
    layout.putConstraint( SpringLayout.NORTH, lengthMaxSpinner, 0,SpringLayout.NORTH, lengthMaxLabel);
    layout.putConstraint( SpringLayout.WEST, lengthMaxSpinner, 5,SpringLayout.EAST, lengthMaxLabel);

    layout.putConstraint( SpringLayout.NORTH, CNMinLabel, 15,SpringLayout.SOUTH, lengthMinLabel);
    layout.putConstraint( SpringLayout.WEST, CNMinLabel, 0,SpringLayout.WEST, lengthMinLabel);
    layout.putConstraint( SpringLayout.NORTH, CNMinSpinner, 0,SpringLayout.NORTH, CNMinLabel);
    layout.putConstraint( SpringLayout.WEST, CNMinSpinner, 0,SpringLayout.WEST, lengthMinSpinner);
    layout.putConstraint( SpringLayout.NORTH, CNMaxLabel, 0,SpringLayout.NORTH, CNMinSpinner);
    layout.putConstraint( SpringLayout.WEST, CNMaxLabel, 10,SpringLayout.EAST, CNMinSpinner);
    layout.putConstraint( SpringLayout.NORTH, CNMaxSpinner, 0,SpringLayout.NORTH, CNMaxLabel);
    layout.putConstraint( SpringLayout.WEST, CNMaxSpinner, 5,SpringLayout.EAST, CNMaxLabel);

    layout.putConstraint( SpringLayout.NORTH, setRangeButton, 5,SpringLayout.SOUTH, CNMinSpinner);
    layout.putConstraint( SpringLayout.EAST, setRangeButton, 0,SpringLayout.EAST, CNMaxSpinner);

    layout.putConstraint( SpringLayout.NORTH, resetButton, 10,SpringLayout.SOUTH, setRangeButton);
    layout.putConstraint( SpringLayout.EAST, resetButton, 0,SpringLayout.EAST,setRangeButton);
    layout.putConstraint( SpringLayout.SOUTH, applyButton, 0,SpringLayout.SOUTH, resetButton);
    layout.putConstraint( SpringLayout.EAST, applyButton, 0,SpringLayout.WEST, resetButton);

    layout.putConstraint( SpringLayout.SOUTH, createButton, -10,SpringLayout.SOUTH, this);
    layout.putConstraint( SpringLayout.EAST, createButton, 0,SpringLayout.EAST, sp);
    layout.putConstraint( SpringLayout.SOUTH, sp, 0,SpringLayout.NORTH, createButton);
    layout.putConstraint( SpringLayout.NORTH, sp, 10,SpringLayout.NORTH, this);
    layout.putConstraint( SpringLayout.EAST, sp, -10,SpringLayout.EAST, this);
    layout.putConstraint( SpringLayout.WEST, sp, 30, SpringLayout.EAST, lengthMaxSpinner);

    //enjoy
    layout.putConstraint( SpringLayout.NORTH, sliceLabel, 0,SpringLayout.NORTH, radiusSpinner);
    layout.putConstraint( SpringLayout.WEST, sliceLabel, 10,SpringLayout.EAST,radiusSpinner);
    layout.putConstraint( SpringLayout.NORTH, sliceSpinner, 0,SpringLayout.NORTH, sliceLabel );
    layout.putConstraint( SpringLayout.WEST, sliceSpinner, 5,SpringLayout.EAST, sliceLabel );




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
    add(lengthMinLabel);
    add(lengthMinSpinner);
    add(lengthMaxLabel);
    add(lengthMaxSpinner);
    add(CNMinLabel);
    add(CNMinSpinner);
    add(CNMaxLabel);
    add(CNMaxSpinner);


    add(lFormat);
    add(lLegend);
    add(taFormat);
    add(taLegend);

    if(ctrl.isEnjoyMode){
      add(sliceLabel);
      add(sliceSpinner);
    }

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
    vconf.bondRadius=((Double)radiusSpinner.getValue()).floatValue();
    vconf.bondSlice=((Integer)sliceSpinner.getValue()).intValue();
    vconf.bondLengthRange[0]=((Double)lengthMinSpinner.getValue()).floatValue();
    vconf.bondLengthRange[1]=((Double)lengthMaxSpinner.getValue()).floatValue();
    vconf.bondCNRange[0]=((Double)CNMinSpinner.getValue()).floatValue();
    vconf.bondCNRange[1]=((Double)CNMaxSpinner.getValue()).floatValue();
    vconf.bondLegend=taLegend.getText();
    vconf.bondColorTableFormat=taFormat.getText();
  }
  private void setValue(){
    radiusSpinner.setValue((double)vconf.bondRadius);
    sliceSpinner.setValue(vconf.bondSlice);
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

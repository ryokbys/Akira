package viewer.viewConfigPanel;

import java.io.*;
import java.util.*;
import javax.swing.table.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import javax.swing.event.*;

import tools.*;
import viewer.*;
import data.*;
import viewer.renderer.*;

public class DataPanel extends JPanel implements ActionListener{


  // called when the event happens
  public void actionPerformed( ActionEvent ae){
    if(ae.getSource() == loadRangeButton){
      for( int i=0; i<Atom.MAX_NUM_DATA; i++ ){
        vconf.dataRange[i][0]=this.refDataRange[i][0];
        vconf.dataRange[i][1]=this.refDataRange[i][1];
      }
    }else if(ae.getSource() == applyButton){
      updateList();
    }else if(ae.getSource() == resetButton){
      vconf.resetData();
    }
    setTable();

    ctrl.RWinRefresh();
  }



  private Controller ctrl;
  private ViewConfig vconf;
  //constructor
  public DataPanel(Controller ctrl){
    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;
    createPanel();
    //Set up the picker that the button brings up.
  }




  private JButton applyButton;
  private JButton resetButton;
  private JButton loadRangeButton;


  private static final String[] colNames = { "ID", "Legend", "Format",
                                             "min", "max","min(ref.)","max(ref.)" };
  private MyTableModel tableModel;
  private JTable table;
  JScrollPane sp;

  public void createPanel(){
    this.addKeyListener(ctrl.keyCtrl);
    //General panel
    setFocusable( false );

    applyButton = new JButton( "Apply" );
    applyButton.setFocusable(false);
    applyButton.addActionListener( this );
    applyButton.addKeyListener(ctrl.keyCtrl);

    resetButton  = new JButton( "Reset" );
    resetButton.setFocusable(false);
    resetButton.addActionListener( this );
    resetButton.addKeyListener(ctrl.keyCtrl);

    loadRangeButton= new JButton( "Load Original Range" );
    loadRangeButton.setFocusable(false);
    loadRangeButton.addActionListener( this );
    loadRangeButton.addKeyListener(ctrl.keyCtrl);

    tableModel = new MyTableModel( colNames, 0 );
    table = new JTable( tableModel );
    table.setFocusable(false);
    table.setRowHeight( 20 );
    table.setIntercellSpacing( new Dimension(2,2) );

    table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    table.setCellSelectionEnabled( true);
    table.setColumnSelectionAllowed( true );
    table.addKeyListener(ctrl.keyCtrl);
    //table.setCellSelectionEnabled( false);
    //table.setColumnSelectionAllowed( false );


    table.setDefaultRenderer( Color.class, new MyColorRenderer(true) );
    table.setDefaultEditor( Color.class, new MyColorEditor() );

    sp = new JScrollPane( table );
    sp.setFocusable(false);
    sp.setWheelScrollingEnabled( true );
    sp.setPreferredSize( new Dimension(700,120) );
    sp.addKeyListener(ctrl.keyCtrl);
    addTable();

    DefaultTableColumnModel columnModel = (DefaultTableColumnModel)table.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(5);
    columnModel.getColumn(1).setPreferredWidth(16);
    columnModel.getColumn(2).setPreferredWidth(10);
    columnModel.getColumn(3).setPreferredWidth(10);
    columnModel.getColumn(4).setPreferredWidth(10);
    columnModel.getColumn(5).setPreferredWidth(10);
    columnModel.getColumn(6).setPreferredWidth(10);






    SpringLayout layout = new SpringLayout();
    setLayout( layout );

    layout.putConstraint( SpringLayout.SOUTH, sp, -10,SpringLayout.SOUTH, this );
    layout.putConstraint( SpringLayout.NORTH, sp, 10,SpringLayout.NORTH, this );
    layout.putConstraint( SpringLayout.WEST, sp, 10,SpringLayout.WEST, this );

    layout.putConstraint( SpringLayout.SOUTH, applyButton, 0, SpringLayout.SOUTH, sp );
    layout.putConstraint( SpringLayout.WEST, applyButton, 5, SpringLayout.EAST, sp);
    layout.putConstraint( SpringLayout.NORTH, resetButton, 0, SpringLayout.NORTH, applyButton );
    layout.putConstraint( SpringLayout.WEST, resetButton, 5, SpringLayout.EAST, applyButton );
    layout.putConstraint( SpringLayout.SOUTH, loadRangeButton, -5, SpringLayout.NORTH, applyButton);
    layout.putConstraint( SpringLayout.WEST, loadRangeButton, 5, SpringLayout.EAST, sp);

    //enjoy




    add( sp );
    add( applyButton );
    add( resetButton );
    add( loadRangeButton );


      /*
       * add(cutRangeMinLabel);
       * add(cutRangeMaxLabel);
       * add(cutRangeMinSpinner);
       * add(cutRangeMaxSpinner);
       * add(calButton);
       * add(factorLabel);
       * add(factorSpinner);
       */

  }

  float[][] refDataRange = new float[Atom.MAX_NUM_DATA][2];
  public void setDataRange(float[][] range){
    this.refDataRange=range;
    setTable();
  }
  private void setTable(){
    for( int i=0; i<Atom.MAX_NUM_DATA; i++ ){
      tableModel.setValueAt( String.valueOf( i+1 ),i,0 );
      tableModel.setValueAt( vconf.dataLegend[i],i,1 );
      tableModel.setValueAt( vconf.dataFormat[i],i,2 );
      tableModel.setValueAt( String.format("%.3e",vconf.dataRange[i][0] ),i,3 );
      tableModel.setValueAt( String.format("%.3e",vconf.dataRange[i][1] ),i,4 );
      tableModel.setValueAt( String.format("%.3e",this.refDataRange[i][0] ),i,5 );
      tableModel.setValueAt( String.format("%.3e",this.refDataRange[i][1] ),i,6 );
    }
  }
  private void addTable(){
    Object[] s = new Object[colNames.length];
    for( int i=0; i<Atom.MAX_NUM_DATA; i++ ){
      s[0] = String.valueOf( i+1 );
      s[1] = vconf.dataLegend[i];
      s[2] = vconf.dataFormat[i];
      s[3] = String.format("%.3e",vconf.dataRange[i][0] );
      s[4] = String.format("%.3e",vconf.dataRange[i][1] );
      s[5] = String.format("%.3e",this.refDataRange[i][0] );
      s[6] = String.format("%.3e",this.refDataRange[i][1] );

      tableModel.addRow( s );
    }
  }

  private void updateList(){
    int s = table.getRowCount();
    for( int i=0; i<s; i++ ){
      vconf.dataLegend[i]=(String)table.getValueAt(i,1);
      vconf.dataFormat[i]=(String)table.getValueAt(i,2);
      vconf.dataRange[i][0]=Float.valueOf((String)table.getValueAt(i,3));
      vconf.dataRange[i][1]=Float.valueOf((String)table.getValueAt(i,4));
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


  private String newline = System.getProperty("line.separator");
  private void calculate(){

    ViewConfig vconf=ctrl.vconf;
    RenderingWindow rw=ctrl.getActiveRW();

    float sum=0.f;
    int inc=0;
    int natm= rw.atoms.getNumAtoms();
    int renderingAtomDataIndex= rw.renderingAtomDataIndex;
    for( int i=0; i<natm; i++ ){
      Atom ai= rw.atoms.getAtom(i);
      if( !ai.isVisible ) continue;
      if( renderingAtomDataIndex>0 ){
        sum+= ai.auxData[renderingAtomDataIndex-1];
        inc++;
      }else{
        inc++;
      }
    }

    String head="";
    String body="";
    head=String.format("including atoms: %d",inc);

    if( renderingAtomDataIndex>0 ){
      body="averaged "+vconf.dataLegend[renderingAtomDataIndex-1]+": ";
      body+=String.format("%f",sum/(float)inc);
    }
    System.out.println(head+newline+body+newline);
  }

}

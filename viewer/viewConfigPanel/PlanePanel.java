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

  JCheckBox cbTetrahedron;

  final String[] colNames = { "On/Off", "Nx", "Ny", "Nz",
                              "Px","Py","Pz",
                              "Color" };
  MyTableModel tableModel;
  JTable table;
  JScrollPane sp;
  private JSpinner spTetraSpecies;
  private JSpinner spRcut;
  private JButton singlePlaneColorButton;
  private JSpinner sx1,sx2,sx3,sy1,sy2,sy3,sz1,sz2,sz3;

  public void createPanel(){
    this.addKeyListener(ctrl.keyCtrl);
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
    sp.addKeyListener(ctrl.keyCtrl);
    //sp.setPreferredSize( new Dimension(430,100) );
    addTable();

    DefaultTableColumnModel columnModel
      = (DefaultTableColumnModel)table.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(8);
    columnModel.getColumn(1).setPreferredWidth(5);
    columnModel.getColumn(2).setPreferredWidth(5);
    columnModel.getColumn(3).setPreferredWidth(5);
    columnModel.getColumn(4).setPreferredWidth(4);
    columnModel.getColumn(5).setPreferredWidth(4);
    columnModel.getColumn(6).setPreferredWidth(4);
    columnModel.getColumn(7).setPreferredWidth(4);

    //reset button
    resetButton=new JButton("Reset");
    resetButton.setFocusable(false);
    resetButton.addActionListener( this );
    resetButton.addKeyListener(ctrl.keyCtrl);

    applyButton=new JButton("Apply");
    applyButton.setFocusable(false);
    applyButton.addActionListener( this );
    applyButton.addKeyListener(ctrl.keyCtrl);


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

    //.....definition of each axis
    String defAxisTitle= "Definition of each axis:";
    JPanel defAxisPanel= new JPanel();
    JLabel daX= new JLabel("x: ",JLabel.RIGHT);
    JLabel daY= new JLabel("y: ",JLabel.RIGHT);
    JLabel daZ= new JLabel("z: ",JLabel.RIGHT);
    JLabel daA= new JLabel("a",JLabel.CENTER);
    JLabel daB= new JLabel("b",JLabel.CENTER);
    JLabel daC= new JLabel("c",JLabel.CENTER);
    JLabel daSpace= new JLabel(" ");
    sx1 =new JSpinner( new SpinnerNumberModel( 1.0,null,null,1.0 ) );
    sx2 =new JSpinner( new SpinnerNumberModel( 0.0,null,null,1.0 ) );
    sx3 =new JSpinner( new SpinnerNumberModel( 0.0,null,null,1.0 ) );
    sy1 =new JSpinner( new SpinnerNumberModel( 0.0,null,null,1.0 ) );
    sy2 =new JSpinner( new SpinnerNumberModel( 1.0,null,null,1.0 ) );
    sy3 =new JSpinner( new SpinnerNumberModel( 0.0,null,null,1.0 ) );
    sz1 =new JSpinner( new SpinnerNumberModel( 0.0,null,null,1.0 ) );
    sz2 =new JSpinner( new SpinnerNumberModel( 0.0,null,null,1.0 ) );
    sz3 =new JSpinner( new SpinnerNumberModel( 1.0,null,null,1.0 ) );
    defAxisPanel.setLayout(new GridLayout(4,4));
    defAxisPanel.add(daSpace);
    defAxisPanel.add(daA);
    defAxisPanel.add(daB);
    defAxisPanel.add(daC);
    defAxisPanel.add(daX);
    defAxisPanel.add(sx1);
    defAxisPanel.add(sx2);
    defAxisPanel.add(sx3);
    defAxisPanel.add(daY);
    defAxisPanel.add(sy1);
    defAxisPanel.add(sy2);
    defAxisPanel.add(sy3);
    defAxisPanel.add(daZ);
    defAxisPanel.add(sz1);
    defAxisPanel.add(sz2);
    defAxisPanel.add(sz3);
    defAxisPanel.setBorder(BorderFactory.createTitledBorder(defAxisTitle));
    defAxisPanel.setPreferredSize(new Dimension(100,150));

    SpringLayout layout = new SpringLayout();
    setLayout( layout );

    //tetrahedron mode
    layout.putConstraint( SpringLayout.NORTH, cbTetrahedron, 10, SpringLayout.NORTH, this  );
    layout.putConstraint( SpringLayout.WEST, cbTetrahedron, 10, SpringLayout.WEST, this );
    JLabel tetraSpeciesLabel=new JLabel("Tag:");
    layout.putConstraint( SpringLayout.NORTH, tetraSpeciesLabel, 5, SpringLayout.SOUTH,  cbTetrahedron);
    layout.putConstraint( SpringLayout.WEST, tetraSpeciesLabel, 20, SpringLayout.WEST, cbTetrahedron);
    layout.putConstraint( SpringLayout.NORTH, spTetraSpecies, 0, SpringLayout.NORTH, tetraSpeciesLabel);
    layout.putConstraint( SpringLayout.WEST, spTetraSpecies, 5, SpringLayout.EAST, tetraSpeciesLabel);
    //color
    JLabel lColor= new JLabel("Color:");
    layout.putConstraint( SpringLayout.NORTH, lColor, 5, SpringLayout.SOUTH, spTetraSpecies );
    layout.putConstraint( SpringLayout.WEST, lColor, 0, SpringLayout.WEST, tetraSpeciesLabel );
    layout.putConstraint( SpringLayout.NORTH, singlePlaneColorButton, 5, SpringLayout.NORTH, lColor );
    layout.putConstraint( SpringLayout.WEST, singlePlaneColorButton, 5, SpringLayout.EAST, lColor);
    JLabel rcutLabel=new JLabel("Rcut");
    layout.putConstraint( SpringLayout.NORTH, rcutLabel, 5, SpringLayout.SOUTH, singlePlaneColorButton );
    layout.putConstraint( SpringLayout.WEST, rcutLabel, 0, SpringLayout.WEST, lColor );
    layout.putConstraint( SpringLayout.NORTH, spRcut, 0, SpringLayout.NORTH, rcutLabel);
    layout.putConstraint( SpringLayout.WEST, spRcut, 5, SpringLayout.EAST, rcutLabel);

    //table
    layout.putConstraint( SpringLayout.NORTH, sp, 10, SpringLayout.SOUTH, spRcut );
    layout.putConstraint( SpringLayout.WEST, sp, 10, SpringLayout.WEST, this );
    layout.putConstraint( SpringLayout.EAST, sp, -10, SpringLayout.EAST, this );
    layout.putConstraint( SpringLayout.SOUTH, sp, -10, SpringLayout.NORTH, defAxisPanel );

    //defAxisPanel
    layout.putConstraint( SpringLayout.WEST, defAxisPanel, 0, SpringLayout.WEST, sp );
    layout.putConstraint( SpringLayout.EAST, defAxisPanel, -10, SpringLayout.EAST, this );
    layout.putConstraint( SpringLayout.SOUTH, defAxisPanel, -10, SpringLayout.NORTH, applyButton );

    //reset
    layout.putConstraint( SpringLayout.SOUTH, applyButton, -10, SpringLayout.SOUTH, this);
    layout.putConstraint( SpringLayout.WEST, applyButton, 10, SpringLayout.WEST, this );

    layout.putConstraint( SpringLayout.SOUTH, resetButton, 0, SpringLayout.SOUTH, applyButton );
    layout.putConstraint( SpringLayout.EAST, resetButton, -10, SpringLayout.EAST, this );


    //addition
    add(cbTetrahedron);
    add(resetButton);
    add(applyButton);
    add(sp);
    add(spTetraSpecies);
    add(spRcut);
    add(lColor);
    add(singlePlaneColorButton);
    add(tetraSpeciesLabel);
    add(rcutLabel);
    add(defAxisPanel);
  }

  boolean visiblePlane=false;
  public void actionPerformed( ActionEvent e ){
    if( e.getSource() == resetButton ){
      //resetparameters();
      setVars();
      setTable();
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

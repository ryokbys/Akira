package viewer.viewConfigPanel;

import java.io.*;
import java.util.*;
import javax.swing.table.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import javax.swing.event.*;


import viewer.*;
import tools.*;
import data.*;
import viewer.renderer.*;

public class VectorPanel extends JPanel implements ActionListener{
  /* accesser */
  public void updateStatus(){
    RenderingWindow rw=ctrl.getActiveRW();
    if(rw==null)return;
    if(rw.visibleVectors){
      vecType.setSelectedIndex(rw.renderingVectorType+1);
    }else{
      vecType.setSelectedIndex(0);
    }

    vecColor.setSelectedIndex(rw.renderingVectorColorType);

  }
  /* accesser */

  private Controller ctrl;
  private ViewConfig vconf;

  //constructor
  public VectorPanel(Controller ctrl){
    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;
    createPanel();
  }

  public void actionPerformed( ActionEvent ae){
    getValue();
    if(ae.getSource() == setRangeButton){
      vconf.vecLengthRange[1]=(ctrl.getActiveRW()).vec.lengthMax;
      lengthMaxSpinner.setValue((double)vconf.vecLengthRange[1]);
    }else if(ae.getSource() == resetButton){
      vconf.resetVector();
      setValue();
    }else if(ae.getSource() == applyButton){
      RenderingWindow rw=ctrl.getActiveRW();
      if(rw==null)return;
      switch(vecType.getSelectedIndex()){
      case 0:
        rw.visibleVectors=false;
        break;
      case 1:
        rw.renderingVectorType=0;
        rw.visibleVectors=true;
        break;
      case 2:
        rw.renderingVectorType=1;
        rw.visibleVectors=true;
        break;
      case 3:
        rw.renderingVectorType=2;
        rw.visibleVectors=true;
        break;
      }
      rw.renderingVectorColorType=vecColor.getSelectedIndex();

      ctrl.RWinRefresh();
    }
  }


  private JSpinner coneRadiusSpinner,coneHeightSpinner;
  private JSpinner spLengthRatio;
  private JSpinner cylinderRadiusSpinner;
  private JSpinner sliceSpinner;
  private JSpinner lengthMinSpinner,lengthMaxSpinner;
  private JButton setRangeButton;
  private JButton resetButton,applyButton;
  private JComboBox vecX,vecY,vecZ;
  private JTextField taLegend,taFormat;

  private JComboBox vecType;
  private JComboBox vecColor;

  private void createPanel(){
    this.addKeyListener(ctrl.keyCtrl);
    //General panel
    setFocusable( false );

    if(ctrl.isEnjoyMode){
      String[] vtStr={"Invisible","Line","Vector","Dolphine","Shark","Whale"};
      vecType= new JComboBox(vtStr);
    }else{
      String[] vtStr={"Invisible","Line","Vector"};
      vecType= new JComboBox(vtStr);
    }
    vecType.setSelectedIndex(0);
    vecType.addActionListener(this);
    vecType.setFocusable(false);

    String[] vcStr={"Atom","Length"};
    vecColor= new JComboBox(vcStr);
    vecColor.setSelectedIndex(0);
    vecColor.addActionListener(this);
    vecColor.setFocusable(false);


    JLabel radiusLabel = new JLabel("Cone Radius" );

    coneRadiusSpinner = new JSpinner(new SpinnerNumberModel((double)vconf.vecConeRadius, 0.0, null, 0.1));
    coneRadiusSpinner.setFocusable(false);
    coneRadiusSpinner.setPreferredSize(new Dimension(60, 23));
    //coneRadiusSpinner.addActionListener(this);

    JLabel heightLabel = new JLabel();
    heightLabel.setText( "Cone Height" );

    coneHeightSpinner = new JSpinner(new SpinnerNumberModel((double)vconf.vecConeHeight, 0.0, null, 0.1));
    coneHeightSpinner.setFocusable(false);
    coneHeightSpinner.setPreferredSize(new Dimension(60, 23));
    //coneHeightSpinner.addActionListener(this);


    JLabel lengthRatioLabel = new JLabel();
    lengthRatioLabel.setText( "Length Factor" );

    spLengthRatio = new JSpinner(new SpinnerNumberModel((double)vconf.vecLengthRatio, 0.1, null, 10.));
    spLengthRatio.setFocusable(false);
    spLengthRatio.setPreferredSize(new Dimension(60, 23));
    //spLengthRatio.addActionListener(this);


    JLabel cylinderRadiusLabel = new JLabel("Cylinder Radius" );

    cylinderRadiusSpinner = new JSpinner(new SpinnerNumberModel((double)vconf.vecCylinderRadius, null, null, 0.1));
    cylinderRadiusSpinner.setFocusable(false);
    cylinderRadiusSpinner.setPreferredSize(new Dimension(60, 23));
    //cylinderRadiusSpinner.addActionListener(this);

    JLabel sliceLabel = new JLabel("Slice" );
    sliceSpinner = new JSpinner(new SpinnerNumberModel(vconf.vecCylinderSlice, 1, null, 1));
    sliceSpinner.setFocusable(false);
    sliceSpinner.setPreferredSize(new Dimension(60, 23));
    //sliceSpinner.addActionListener(this);

    JLabel lengthMinLabel=new JLabel("Range Min");
    lengthMinSpinner = new JSpinner(new SpinnerNumberModel((double)vconf.vecLengthRange[0], 0., null, 1.));
    lengthMinSpinner.setFocusable(false);
    lengthMinSpinner.setPreferredSize(new Dimension(90, 23));
    lengthMinSpinner.setEditor(new JSpinner.NumberEditor(lengthMinSpinner, "0.####E0"));

    JLabel lengthMaxLabel=new JLabel("Max");
    lengthMaxSpinner = new JSpinner(new SpinnerNumberModel((double)vconf.vecLengthRange[1], 0., null, 1.));
    lengthMaxSpinner.setFocusable(false);
    lengthMaxSpinner.setPreferredSize(new Dimension(90, 23));
    lengthMaxSpinner.setEditor(new JSpinner.NumberEditor(lengthMaxSpinner, "0.####E0"));


    setRangeButton = new JButton("Load Original Range");
    setRangeButton.setToolTipText("Create");
    setRangeButton.addActionListener( this );
    setRangeButton.setFocusable(false);

    //components
    String[] comboData = {"Data Index 1",
                          "Data Index 2",
                          "Data Index 3",
                          "Data Index 4",
                          "Data Index 5",
                          "Data Index 6",
                          "Data Index 7",
                          "Data Index 8",
                          "Data Index 9"};

    JLabel vecXLabel = new JLabel("X-component");
    vecX = new JComboBox(comboData);
    vecX.setSelectedIndex(vconf.vectorX);
    vecX.setFocusable(false);
    vecX.addActionListener(this);


    JLabel vecYLabel = new JLabel("Y-component");
    vecY = new JComboBox(comboData);
    vecY.setFocusable(false);
    vecY.setSelectedIndex(vconf.vectorY);
    vecY.addActionListener(this);

    JLabel vecZLabel = new JLabel("Z-component");
    vecZ = new JComboBox(comboData);
    vecZ.setFocusable(false);
    vecZ.setSelectedIndex(vconf.vectorZ);
    vecZ.addActionListener(this);


    JLabel lLegend=new JLabel("Legend");
    taLegend = new JTextField(vconf.vecLegend);
    taLegend.setEditable(true);
    JLabel lFormat=new JLabel("Format");
    taFormat = new JTextField(vconf.vecColorTableFormat);
    taFormat.setFocusable(false);
    taFormat.setEditable(true);
    taLegend.setPreferredSize(new Dimension(55, 23));
    taFormat.setPreferredSize(new Dimension(55, 23));

    applyButton  = new JButton( "Apply" );
    applyButton.setFocusable(false);
    applyButton.addActionListener( this );
    resetButton  = new JButton( "Reset" );
    resetButton.setFocusable(false);
    resetButton.addActionListener( this );


    SpringLayout layout = new SpringLayout();
    setLayout( layout );

    JLabel vecTypeLabel =new JLabel("Type");
    layout.putConstraint( SpringLayout.NORTH, vecTypeLabel, 10, SpringLayout.NORTH, this );
    layout.putConstraint( SpringLayout.WEST,  vecTypeLabel, 10, SpringLayout.WEST, this );
    layout.putConstraint( SpringLayout.NORTH, vecType, 0, SpringLayout.SOUTH, vecTypeLabel);
    layout.putConstraint( SpringLayout.WEST,  vecType, 5, SpringLayout.WEST, vecTypeLabel);

    JLabel vecColorLabel =new JLabel("Color");
    layout.putConstraint( SpringLayout.NORTH, vecColorLabel, 5, SpringLayout.SOUTH, vecType);
    layout.putConstraint( SpringLayout.WEST,  vecColorLabel, 0, SpringLayout.WEST, vecType);
    layout.putConstraint( SpringLayout.NORTH, vecColor, 0, SpringLayout.SOUTH, vecColorLabel);
    layout.putConstraint( SpringLayout.WEST,  vecColor, 5, SpringLayout.WEST, vecColorLabel);

    layout.putConstraint( SpringLayout.NORTH, vecXLabel, 0, SpringLayout.NORTH, vecTypeLabel);
    layout.putConstraint( SpringLayout.WEST,  vecXLabel, 10, SpringLayout.EAST,  vecType);
    layout.putConstraint( SpringLayout.NORTH, vecX, 0, SpringLayout.NORTH, vecXLabel);
    layout.putConstraint( SpringLayout.WEST,  vecX, 10, SpringLayout.EAST,  vecXLabel);

    layout.putConstraint( SpringLayout.NORTH, vecYLabel, 10, SpringLayout.SOUTH, vecXLabel);
    layout.putConstraint( SpringLayout.WEST,  vecYLabel, 0, SpringLayout.WEST,  vecXLabel);
    layout.putConstraint( SpringLayout.NORTH, vecY, 0, SpringLayout.NORTH, vecYLabel);
    layout.putConstraint( SpringLayout.WEST,  vecY, 10, SpringLayout.EAST,  vecYLabel);

    layout.putConstraint( SpringLayout.NORTH, vecZLabel, 10, SpringLayout.SOUTH, vecYLabel);
    layout.putConstraint( SpringLayout.WEST,  vecZLabel, 0, SpringLayout.WEST,  vecYLabel);
    layout.putConstraint( SpringLayout.NORTH, vecZ, 0, SpringLayout.NORTH, vecZLabel);
    layout.putConstraint( SpringLayout.WEST,  vecZ, 10, SpringLayout.EAST,  vecZLabel);



    //range
    layout.putConstraint( SpringLayout.NORTH, lengthMinLabel, 10, SpringLayout.NORTH, this);
    layout.putConstraint( SpringLayout.WEST, lengthMinLabel, 10, SpringLayout.EAST, vecZ);
    layout.putConstraint( SpringLayout.NORTH, lengthMinSpinner, 0, SpringLayout.NORTH, lengthMinLabel);
    layout.putConstraint( SpringLayout.WEST, lengthMinSpinner, 0, SpringLayout.EAST,  lengthMinLabel);
    layout.putConstraint( SpringLayout.NORTH, lengthMaxLabel, 0, SpringLayout.NORTH, lengthMinSpinner);
    layout.putConstraint( SpringLayout.WEST, lengthMaxLabel, 10, SpringLayout.EAST, lengthMinSpinner);
    layout.putConstraint( SpringLayout.NORTH, lengthMaxSpinner, 0, SpringLayout.NORTH, lengthMaxLabel);
    layout.putConstraint( SpringLayout.WEST, lengthMaxSpinner, 0, SpringLayout.EAST, lengthMaxLabel);
    layout.putConstraint( SpringLayout.NORTH, setRangeButton, 0,SpringLayout.SOUTH, lengthMaxSpinner);
    layout.putConstraint( SpringLayout.EAST, setRangeButton, 0,SpringLayout.EAST, lengthMaxSpinner);

    //legend
    layout.putConstraint( SpringLayout.NORTH, lLegend, 10,SpringLayout.SOUTH, setRangeButton);
    layout.putConstraint( SpringLayout.WEST, lLegend, 0,SpringLayout.WEST, lengthMinLabel);
    layout.putConstraint( SpringLayout.NORTH, taLegend, 0,SpringLayout.NORTH, lLegend);
    layout.putConstraint( SpringLayout.WEST, taLegend, 0,SpringLayout.EAST, lLegend);
    layout.putConstraint( SpringLayout.NORTH, lFormat, 0,SpringLayout.NORTH, taLegend);
    layout.putConstraint( SpringLayout.WEST, lFormat, 10,SpringLayout.EAST, taLegend);
    layout.putConstraint( SpringLayout.NORTH, taFormat, 0,SpringLayout.NORTH, lFormat);
    layout.putConstraint( SpringLayout.WEST, taFormat, 0,SpringLayout.EAST, lFormat);

    //button
    layout.putConstraint( SpringLayout.NORTH, applyButton, 20,SpringLayout.SOUTH, lLegend);
    layout.putConstraint( SpringLayout.WEST, applyButton, 0,SpringLayout.WEST, lLegend);
    layout.putConstraint( SpringLayout.NORTH, resetButton, 0,SpringLayout.NORTH, applyButton);
    layout.putConstraint( SpringLayout.WEST, resetButton, 10,SpringLayout.EAST, applyButton);

    //property
    layout.putConstraint( SpringLayout.NORTH, cylinderRadiusLabel, 10,SpringLayout.NORTH, this);
    layout.putConstraint( SpringLayout.WEST,  cylinderRadiusLabel, 20,SpringLayout.EAST,  lengthMaxSpinner);
    layout.putConstraint( SpringLayout.NORTH, cylinderRadiusSpinner, 0, SpringLayout.NORTH, cylinderRadiusLabel );
    layout.putConstraint( SpringLayout.WEST,  cylinderRadiusSpinner, 0, SpringLayout.EAST,  cylinderRadiusLabel );

    layout.putConstraint( SpringLayout.NORTH, radiusLabel, 5, SpringLayout.SOUTH,cylinderRadiusSpinner);
    layout.putConstraint( SpringLayout.WEST,  radiusLabel, 0, SpringLayout.WEST,cylinderRadiusLabel);
    layout.putConstraint( SpringLayout.NORTH, coneRadiusSpinner, 0, SpringLayout.NORTH, radiusLabel);
    layout.putConstraint( SpringLayout.WEST,  coneRadiusSpinner, 0, SpringLayout.WEST,cylinderRadiusSpinner);

    layout.putConstraint( SpringLayout.NORTH, heightLabel, 5, SpringLayout.SOUTH, coneRadiusSpinner );
    layout.putConstraint( SpringLayout.WEST,  heightLabel, 0, SpringLayout.WEST,  radiusLabel);
    layout.putConstraint( SpringLayout.NORTH, coneHeightSpinner, 0, SpringLayout.NORTH, heightLabel);
    layout.putConstraint( SpringLayout.WEST,  coneHeightSpinner,0, SpringLayout.WEST,cylinderRadiusSpinner);

    layout.putConstraint( SpringLayout.NORTH, lengthRatioLabel, 5, SpringLayout.SOUTH, coneHeightSpinner);
    layout.putConstraint( SpringLayout.WEST,  lengthRatioLabel, 0, SpringLayout.WEST,  heightLabel);
    layout.putConstraint( SpringLayout.NORTH, spLengthRatio, 0, SpringLayout.NORTH, lengthRatioLabel );
    layout.putConstraint( SpringLayout.WEST,  spLengthRatio,0, SpringLayout.WEST,cylinderRadiusSpinner);

    layout.putConstraint( SpringLayout.NORTH, sliceLabel, 5,SpringLayout.SOUTH,spLengthRatio );
    layout.putConstraint( SpringLayout.WEST,  sliceLabel, 0,SpringLayout.WEST,  lengthRatioLabel);
    layout.putConstraint( SpringLayout.NORTH, sliceSpinner, 0, SpringLayout.NORTH, sliceLabel );
    layout.putConstraint( SpringLayout.WEST,  sliceSpinner, 0, SpringLayout.WEST,cylinderRadiusSpinner);

    add(vecType);
    add(vecTypeLabel);

    add(applyButton);
    add(resetButton);
    add(vecXLabel);
    add(vecX);
    add(vecYLabel);
    add(vecY);
    add(vecZLabel);
    add(vecZ);


    add(vecColor);
    add(vecColorLabel);
    add(lengthMinLabel);
    add(lengthMinSpinner);
    add(lengthMaxLabel);
    add(lengthMaxSpinner);
    add(setRangeButton);
    add(radiusLabel);
    add(coneRadiusSpinner);
    add(heightLabel);
    add(coneHeightSpinner);
    add(lengthRatioLabel);
    add(spLengthRatio);
    add(cylinderRadiusLabel);
    add(cylinderRadiusSpinner);

    add(lFormat);
    add(lLegend);
    add(taFormat);
    add(taLegend);
    if(ctrl.isEnjoyMode){
      add(sliceLabel);
      add(sliceSpinner);
    }

    setValue();
  }

  private void getValue(){
    vconf.vectorX=vecX.getSelectedIndex();
    vconf.vectorY=vecY.getSelectedIndex();
    vconf.vectorZ=vecZ.getSelectedIndex();
    vconf.vecLegend=taLegend.getText();
    vconf.vecColorTableFormat=taFormat.getText();
    vconf.vecConeRadius=((Double)coneRadiusSpinner.getValue()).floatValue();
    vconf.vecConeHeight=((Double)coneHeightSpinner.getValue()).floatValue();
    vconf.vecLengthRatio=((Double)spLengthRatio.getValue()).floatValue();
    vconf.vecCylinderRadius=((Double)cylinderRadiusSpinner.getValue()).floatValue();
    vconf.vecCylinderSlice=((Integer)sliceSpinner.getValue()).intValue();
    vconf.vecLengthRange[0]=((Double)lengthMinSpinner.getValue()).floatValue();
    vconf.vecLengthRange[1]=((Double)lengthMaxSpinner.getValue()).floatValue();
  }
  private void setValue(){
    vecX.setSelectedIndex(vconf.vectorX);
    vecY.setSelectedIndex(vconf.vectorY);
    vecZ.setSelectedIndex(vconf.vectorZ);
    taLegend.setText(vconf.vecLegend);
    taFormat.setText(vconf.vecColorTableFormat);
    coneRadiusSpinner.setValue((double)vconf.vecConeRadius);
    coneHeightSpinner.setValue((double)vconf.vecConeHeight);
    spLengthRatio.setValue((double)vconf.vecLengthRatio);
    cylinderRadiusSpinner.setValue((double)vconf.vecCylinderRadius);
    sliceSpinner.setValue((int)vconf.vecCylinderSlice);
    lengthMinSpinner.setValue((double)vconf.vecLengthRange[0]);
    lengthMaxSpinner.setValue((double)vconf.vecLengthRange[1]);
  }

}

package viewer.viewConfigPanel;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;

import tools.colorpicker.*;
import info.clearthought.layout.*;

import viewer.*;
import viewer.renderer.*;
import tools.*;

public class ColorTablePanel extends JPanel implements ActionListener,ChangeListener{

  private Controller ctrl;
  private ViewConfig vconf;
  //constructor
  public ColorTablePanel(Controller ctrl){
    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;
    makePanel();
  }

  private JComboBox ctableType,ticsType,alphaType;
  private JCheckBox cbIsTicsHLong;
  private JCheckBox cbA,cbB,cbV,cbVol;
  private JSpinner spAX,spAY,spAW,spAH;
  private JSpinner spBX,spBY,spBW,spBH;
  private JSpinner spVX,spVY,spVW,spVH;
  private JSpinner spVolX,spVolY,spVolW,spVolH;
  private JSpinner spTitleDx,spTitleDy;
  private JSpinner spNumDx,spNumDy;
  private JButton resetButton,applyButton;


  private void makePanel(){
    JLabel colorLabel=new JLabel("Color Type");
    String[] cTableStr = {"Max-Min-Enhaced3",
                          "Max-Min-Enhaced2",
                          "Max-Min-Enhanced1",
                          "Thermography",
                          "Rainbow",
                          "Cycle Rainbow",
                          "White-Black",
                          "Black-White",
                          "Discrete BGYR",
                          "Discrete BGR"};

    ctableType = new JComboBox(viewer.renderer.ColorTable.colorName);
    ctableType.setPreferredSize(new Dimension(150, 25));
    ctableType.addActionListener(this);
    ctableType.setFocusable(false);

    JLabel ticsLabel=new JLabel("Tics Type");
    String[] ticsStr = {"3","2","1","6"};
    ticsType = new JComboBox(ticsStr);
    ticsType.setPreferredSize(new Dimension(150, 25));
    ticsType.addActionListener(this);
    ticsType.setFocusable(false);

    JLabel alphaLabel=new JLabel("Alpha Type");
    String[] alphaStr = {"Normal", "High Opancity","Low Opancity","Cut"};
    alphaType = new JComboBox(alphaStr);
    alphaType.setPreferredSize(new Dimension(150, 25));
    alphaType.setFocusable(false);
    alphaType.addActionListener(this);


    cbIsTicsHLong =new JCheckBox("Horizonal Long",vconf.isTicsHLong);
    cbIsTicsHLong.setFocusable(false);
    cbIsTicsHLong.addChangeListener(this);
    //atom
    cbA =new JCheckBox("Atom Color Table",vconf.isVisibleAtomColorTable);
    cbA.setFocusable(false);
    cbA.addChangeListener(this);
    spAX =new JSpinner( new SpinnerNumberModel((double)vconf.atomColorTablePos[0],0.0, 1.0, 0.1 ) );
    spAX.setPreferredSize( new Dimension(60,25) );
    spAX.addChangeListener( this );
    spAY =new JSpinner( new SpinnerNumberModel( (double)vconf.atomColorTablePos[1],0.0, 1.0, 0.1 ) );
    spAY.setPreferredSize( new Dimension(60,25) );
    spAY.addChangeListener( this );
    spAW =new JSpinner( new SpinnerNumberModel( (double)vconf.atomColorTablePos[2],0.0, null, 1 ) );
    spAW.setPreferredSize( new Dimension(60,25) );
    spAW.addChangeListener( this );
    spAH =new JSpinner( new SpinnerNumberModel( (double)vconf.atomColorTablePos[3],0.0, null, 1 ) );
    spAH.setPreferredSize( new Dimension(60,25) );
    spAH.addChangeListener( this );
    //bond
    cbB =new JCheckBox("Bond Color Table",vconf.isVisibleBondColorTable);
    cbB.setFocusable(false);
    cbB.addChangeListener(this);
    spBX =new JSpinner( new SpinnerNumberModel((double)vconf.bondColorTablePos[0],0.0, 1.0, 0.1 ) );
    spBX.setPreferredSize( new Dimension(60,25) );
    spBX.addChangeListener( this );
    spBY =new JSpinner( new SpinnerNumberModel( (double)vconf.bondColorTablePos[1],0.0, 1.0, 0.1 ) );
    spBY.setPreferredSize( new Dimension(60,25) );
    spBY.addChangeListener( this );
    spBW =new JSpinner( new SpinnerNumberModel( (double)vconf.bondColorTablePos[2],0.0, null, 1 ) );
    spBW.setPreferredSize( new Dimension(60,25) );
    spBW.addChangeListener( this );
    spBH =new JSpinner( new SpinnerNumberModel( (double)vconf.bondColorTablePos[3],0.0, null, 1 ) );
    spBH.setPreferredSize( new Dimension(60,25) );
    spBH.addChangeListener( this );
    //vec
    cbV =new JCheckBox("Vec. Color Table",vconf.isVisibleVecColorTable);
    cbV.setFocusable(false);
    cbV.addChangeListener(this);
    spVX =new JSpinner( new SpinnerNumberModel((double)vconf.vecColorTablePos[0],0.0, 1.0, 0.1 ) );
    spVX.setPreferredSize( new Dimension(60,25) );
    spVX.addChangeListener( this );
    spVY =new JSpinner( new SpinnerNumberModel( (double)vconf.vecColorTablePos[1],0.0, 1.0, 0.1 ) );
    spVY.setPreferredSize( new Dimension(60,25) );
    spVY.addChangeListener( this );
    spVW =new JSpinner( new SpinnerNumberModel( (double)vconf.vecColorTablePos[2],0.0, null, 1 ) );
    spVW.setPreferredSize( new Dimension(60,25) );
    spVW.addChangeListener( this );
    spVH =new JSpinner( new SpinnerNumberModel( (double)vconf.vecColorTablePos[3],0.0, null, 1 ) );
    spVH.setPreferredSize( new Dimension(60,25) );
    spVH.addChangeListener( this );
    //vol
    cbVol =new JCheckBox("Vol. Color Table",vconf.isVisibleVolColorTable);
    cbVol.setFocusable(false);
    cbVol.addChangeListener(this);
    spVolX =new JSpinner( new SpinnerNumberModel((double)vconf.volColorTablePos[0],0.0, 1.0, 0.1 ) );
    spVolX.setPreferredSize( new Dimension(60,25) );
    spVolX.addChangeListener( this );
    spVolY =new JSpinner( new SpinnerNumberModel( (double)vconf.volColorTablePos[1],0.0, 1.0, 0.1 ) );
    spVolY.setPreferredSize( new Dimension(60,25) );
    spVolY.addChangeListener( this );
    spVolW =new JSpinner( new SpinnerNumberModel( (double)vconf.volColorTablePos[2],0.0, null, 1 ) );
    spVolW.setPreferredSize( new Dimension(60,25) );
    spVolW.addChangeListener( this );
    spVolH =new JSpinner( new SpinnerNumberModel( (double)vconf.volColorTablePos[3],0.0, null, 1 ) );
    spVolH.setPreferredSize( new Dimension(60,25) );
    spVolH.addChangeListener( this );

    spTitleDx =new JSpinner( new SpinnerNumberModel( (double)vconf.ctTitlePos[0],null, null, 1 ) );
    spTitleDx.setPreferredSize( new Dimension(60,25) );
    spTitleDx.addChangeListener( this );
    spTitleDy =new JSpinner( new SpinnerNumberModel( (double)vconf.ctTitlePos[1],null, null, 1 ) );
    spTitleDy.setPreferredSize( new Dimension(60,25) );
    spTitleDy.addChangeListener( this );
    spNumDx =new JSpinner( new SpinnerNumberModel( (double)vconf.ctNumPos[0],null, null, 1 ) );
    spNumDx.setPreferredSize( new Dimension(60,25) );
    spNumDx.addChangeListener( this );
    spNumDy =new JSpinner( new SpinnerNumberModel( (double)vconf.ctNumPos[1],null, null, 1 ) );
    spNumDy.setPreferredSize( new Dimension(60,25) );
    spNumDy.addChangeListener( this );

    applyButton  = new JButton( "Apply" );
    applyButton.setFocusable(false);
    applyButton.addActionListener( this );
    resetButton  = new JButton( "Reset" );
    resetButton.setFocusable(false);
    resetButton.addActionListener( this );



    SpringLayout layout = new SpringLayout();
    setLayout( layout );

    //
    layout.putConstraint( SpringLayout.NORTH, colorLabel, 10, SpringLayout.NORTH, this );
    layout.putConstraint( SpringLayout.WEST, colorLabel, 10, SpringLayout.WEST, this );
    layout.putConstraint( SpringLayout.NORTH, ctableType, 0, SpringLayout.NORTH, colorLabel);
    layout.putConstraint( SpringLayout.WEST, ctableType, 5, SpringLayout.EAST, colorLabel);

    layout.putConstraint( SpringLayout.NORTH, ticsLabel, 10, SpringLayout.SOUTH, colorLabel);
    layout.putConstraint( SpringLayout.WEST, ticsLabel, 0, SpringLayout.WEST,colorLabel);
    layout.putConstraint( SpringLayout.NORTH, ticsType, 0, SpringLayout.NORTH, ticsLabel);
    layout.putConstraint( SpringLayout.WEST, ticsType, 0, SpringLayout.WEST, ctableType);

    layout.putConstraint( SpringLayout.NORTH, alphaLabel, 10, SpringLayout.SOUTH, ticsLabel);
    layout.putConstraint( SpringLayout.WEST, alphaLabel, 0, SpringLayout.WEST, ticsLabel);
    layout.putConstraint( SpringLayout.NORTH, alphaType, 0, SpringLayout.NORTH, alphaLabel);
    layout.putConstraint( SpringLayout.WEST, alphaType, 0, SpringLayout.WEST, ticsType);
    layout.putConstraint( SpringLayout.NORTH, cbIsTicsHLong, 10, SpringLayout.SOUTH, alphaLabel);
    layout.putConstraint( SpringLayout.WEST, cbIsTicsHLong, 0, SpringLayout.WEST, alphaLabel);

    //
    layout.putConstraint( SpringLayout.NORTH, cbA, 20, SpringLayout.NORTH, this);
    layout.putConstraint( SpringLayout.WEST, cbA, 8, SpringLayout.EAST, ctableType);
    layout.putConstraint( SpringLayout.NORTH, cbB, 10, SpringLayout.SOUTH, cbA);
    layout.putConstraint( SpringLayout.WEST, cbB, 0, SpringLayout.WEST, cbA);
    layout.putConstraint( SpringLayout.NORTH, cbV, 10, SpringLayout.SOUTH, cbB);
    layout.putConstraint( SpringLayout.WEST, cbV, 0, SpringLayout.WEST, cbB);
    layout.putConstraint( SpringLayout.NORTH, cbVol, 10, SpringLayout.SOUTH, cbV);
    layout.putConstraint( SpringLayout.WEST, cbVol, 0, SpringLayout.WEST, cbV);


    layout.putConstraint( SpringLayout.NORTH, spAX, 0, SpringLayout.NORTH, cbA);
    layout.putConstraint( SpringLayout.WEST, spAX, 5, SpringLayout.EAST, cbA);
    layout.putConstraint( SpringLayout.NORTH, spAY, 0, SpringLayout.NORTH, spAX);
    layout.putConstraint( SpringLayout.WEST, spAY, 5, SpringLayout.EAST, spAX);
    layout.putConstraint( SpringLayout.NORTH, spAW, 0, SpringLayout.NORTH, spAY);
    layout.putConstraint( SpringLayout.WEST, spAW, 5, SpringLayout.EAST, spAY);
    layout.putConstraint( SpringLayout.NORTH, spAH, 0, SpringLayout.NORTH, spAW);
    layout.putConstraint( SpringLayout.WEST, spAH, 5, SpringLayout.EAST, spAW);

    layout.putConstraint( SpringLayout.NORTH, spBX, 0, SpringLayout.NORTH, cbB);
    layout.putConstraint( SpringLayout.WEST, spBX, 0, SpringLayout.WEST, spAX);
    layout.putConstraint( SpringLayout.NORTH, spBY, 0, SpringLayout.NORTH, spBX);
    layout.putConstraint( SpringLayout.WEST, spBY, 5, SpringLayout.EAST, spBX);
    layout.putConstraint( SpringLayout.NORTH, spBW, 0, SpringLayout.NORTH, spBY);
    layout.putConstraint( SpringLayout.WEST, spBW, 5, SpringLayout.EAST, spBY);
    layout.putConstraint( SpringLayout.NORTH, spBH, 0, SpringLayout.NORTH, spBW);
    layout.putConstraint( SpringLayout.WEST, spBH, 5, SpringLayout.EAST, spBW);

    layout.putConstraint( SpringLayout.NORTH, spVX, 0, SpringLayout.NORTH, cbV);
    layout.putConstraint( SpringLayout.WEST, spVX, 0, SpringLayout.WEST, spBX);
    layout.putConstraint( SpringLayout.NORTH, spVY, 0, SpringLayout.NORTH, spVX);
    layout.putConstraint( SpringLayout.WEST, spVY, 5, SpringLayout.EAST, spVX);
    layout.putConstraint( SpringLayout.NORTH, spVW, 0, SpringLayout.NORTH, spVY);
    layout.putConstraint( SpringLayout.WEST, spVW, 5, SpringLayout.EAST, spVY);
    layout.putConstraint( SpringLayout.NORTH, spVH, 0, SpringLayout.NORTH, spVW);
    layout.putConstraint( SpringLayout.WEST, spVH, 5, SpringLayout.EAST, spVW);

    layout.putConstraint( SpringLayout.NORTH, spVolX, 0, SpringLayout.NORTH, cbVol);
    layout.putConstraint( SpringLayout.WEST, spVolX, 0, SpringLayout.WEST, spVX);
    layout.putConstraint( SpringLayout.NORTH, spVolY, 0, SpringLayout.NORTH, spVolX);
    layout.putConstraint( SpringLayout.WEST, spVolY, 5, SpringLayout.EAST, spVolX);
    layout.putConstraint( SpringLayout.NORTH, spVolW, 0, SpringLayout.NORTH, spVolY);
    layout.putConstraint( SpringLayout.WEST, spVolW, 5, SpringLayout.EAST, spVolY);
    layout.putConstraint( SpringLayout.NORTH, spVolH, 0, SpringLayout.NORTH, spVolW);
    layout.putConstraint( SpringLayout.WEST, spVolH, 5, SpringLayout.EAST, spVolW);

    //label
    JLabel lPosx=new JLabel("x");
    JLabel lPosy=new JLabel("y");
    JLabel lWidth=new JLabel("Width");
    JLabel lHeight=new JLabel("Height");
    layout.putConstraint( SpringLayout.SOUTH, lPosx, 0, SpringLayout.NORTH,spAX );
    layout.putConstraint( SpringLayout.WEST, lPosx, 0, SpringLayout.WEST, spAX);
    layout.putConstraint( SpringLayout.SOUTH, lPosy, 0, SpringLayout.NORTH, spAY);
    layout.putConstraint( SpringLayout.WEST, lPosy, 0, SpringLayout.WEST, spAY);
    layout.putConstraint( SpringLayout.SOUTH, lWidth, 0, SpringLayout.NORTH,spAW );
    layout.putConstraint( SpringLayout.WEST, lWidth, 0, SpringLayout.WEST, spAW);
    layout.putConstraint( SpringLayout.SOUTH, lHeight, 0, SpringLayout.NORTH, spAH);
    layout.putConstraint( SpringLayout.WEST, lHeight, 0, SpringLayout.WEST, spAH);


    JLabel lTitle=new JLabel("Title Pos.");
    layout.putConstraint( SpringLayout.NORTH, lTitle, 0, SpringLayout.NORTH, lPosx);
    layout.putConstraint( SpringLayout.WEST, lTitle, 20, SpringLayout.EAST, spAH);
    layout.putConstraint( SpringLayout.NORTH, spTitleDx, 0, SpringLayout.SOUTH, lTitle);
    layout.putConstraint( SpringLayout.WEST, spTitleDx, 0, SpringLayout.WEST, lTitle);
    layout.putConstraint( SpringLayout.NORTH, spTitleDy, 0, SpringLayout.NORTH, spTitleDx);
    layout.putConstraint( SpringLayout.WEST, spTitleDy, 0, SpringLayout.EAST, spTitleDx);
    JLabel lNumPos=new JLabel("Num. Pos.");
    layout.putConstraint( SpringLayout.NORTH, lNumPos, 10, SpringLayout.SOUTH, spTitleDx);
    layout.putConstraint( SpringLayout.WEST, lNumPos, 0, SpringLayout.WEST, lTitle);
    layout.putConstraint( SpringLayout.NORTH, spNumDx, 0, SpringLayout.SOUTH, lNumPos);
    layout.putConstraint( SpringLayout.WEST, spNumDx, 0, SpringLayout.WEST, lNumPos);
    layout.putConstraint( SpringLayout.NORTH, spNumDy, 0, SpringLayout.NORTH, spNumDx);
    layout.putConstraint( SpringLayout.WEST, spNumDy, 0, SpringLayout.EAST, spNumDx);

    layout.putConstraint( SpringLayout.SOUTH, applyButton, -10,SpringLayout.SOUTH, this);
    layout.putConstraint( SpringLayout.WEST, applyButton, 0,SpringLayout.EAST, spNumDy);
    layout.putConstraint( SpringLayout.SOUTH, resetButton, 0,SpringLayout.SOUTH, applyButton);
    layout.putConstraint( SpringLayout.WEST, resetButton, 0,SpringLayout.EAST, applyButton);


    //add
    add(applyButton);
    add(resetButton);
    add(lPosx);
    add(lPosy);
    add(lWidth);
    add(lHeight);
    this.add( colorLabel);
    this.add( ctableType );
    this.add( ticsLabel);
    this.add( ticsType );


    this.add(cbA);
    this.add(spAX);
    this.add(spAY);
    this.add(spAH);
    this.add(spAW);
    add(lTitle);
    add(spTitleDx);
    add(spTitleDy);
    add(lNumPos);
    add(spNumDx);
    add(spNumDy);

    this.add(cbB);
    this.add(spBX);
    this.add(spBY);
    this.add(spBH);
    this.add(spBW);

    this.add(cbV);
    this.add(spVX);
    this.add(spVY);
    this.add(spVH);
    this.add(spVW);

    this.add(cbVol);
    this.add(spVolX);
    this.add(spVolY);
    this.add(spVolH);
    this.add(spVolW);

    if(ctrl.isEnjoyMode){
      this.add( alphaLabel);
      this.add( alphaType );
      add(cbIsTicsHLong);
    }


    update();
  }

  private void update(){
    ctableType.setSelectedIndex(vconf.colorTableType);
    ticsType.setSelectedIndex(vconf.ticsType);
    alphaType.setSelectedIndex(vconf.colorAlphaType);
    cbIsTicsHLong.setSelected(vconf.isTicsHLong);
    cbA.setSelected(vconf.isVisibleAtomColorTable);
    spAX.setValue((double)vconf.atomColorTablePos[0]);
    spAY.setValue((double)vconf.atomColorTablePos[1]);
    spAW.setValue((double)vconf.atomColorTablePos[2]);
    spAH.setValue((double)vconf.atomColorTablePos[3]);
    cbB.setSelected(vconf.isVisibleBondColorTable);
    spBX.setValue((double)vconf.bondColorTablePos[0]);
    spBY.setValue((double)vconf.bondColorTablePos[1]);
    spBW.setValue((double)vconf.bondColorTablePos[2]);
    spBH.setValue((double)vconf.bondColorTablePos[3]);
    cbV.setSelected(vconf.isVisibleVecColorTable);
    spVX.setValue((double)vconf.vecColorTablePos[0]);
    spVY.setValue((double)vconf.vecColorTablePos[1]);
    spVW.setValue((double)vconf.vecColorTablePos[2]);
    spVH.setValue((double)vconf.vecColorTablePos[3]);
    cbVol.setSelected(vconf.isVisibleVolColorTable);
    spVolX.setValue((double)vconf.volColorTablePos[0]);
    spVolY.setValue((double)vconf.volColorTablePos[1]);
    spVolW.setValue((double)vconf.volColorTablePos[2]);
    spVolH.setValue((double)vconf.volColorTablePos[3]);
    spTitleDx.setValue( (double)vconf.ctTitlePos[0]);
    spTitleDy.setValue( (double)vconf.ctTitlePos[1]);
    spNumDx.setValue( (double)vconf.ctNumPos[0]);
    spNumDy.setValue( (double)vconf.ctNumPos[1]);
  }

  public void stateChanged( ChangeEvent ce ){
    vconf.isTicsHLong=cbIsTicsHLong.isSelected();
    vconf.isVisibleAtomColorTable=cbA.isSelected();
    vconf.atomColorTablePos[0]=((Double)spAX.getValue()).floatValue();
    vconf.atomColorTablePos[1]=((Double)spAY.getValue()).floatValue();
    vconf.atomColorTablePos[2]=((Double)spAW.getValue()).floatValue();
    vconf.atomColorTablePos[3]=((Double)spAH.getValue()).floatValue();
    vconf.isVisibleBondColorTable=cbB.isSelected();
    vconf.bondColorTablePos[0]=((Double)spBX.getValue()).floatValue();
    vconf.bondColorTablePos[1]=((Double)spBY.getValue()).floatValue();
    vconf.bondColorTablePos[2]=((Double)spBW.getValue()).floatValue();
    vconf.bondColorTablePos[3]=((Double)spBH.getValue()).floatValue();
    vconf.isVisibleVecColorTable=cbV.isSelected();
    vconf.vecColorTablePos[0]=((Double)spVX.getValue()).floatValue();
    vconf.vecColorTablePos[1]=((Double)spVY.getValue()).floatValue();
    vconf.vecColorTablePos[2]=((Double)spVW.getValue()).floatValue();
    vconf.vecColorTablePos[3]=((Double)spVH.getValue()).floatValue();
    vconf.isVisibleVolColorTable=cbVol.isSelected();
    vconf.volColorTablePos[0]=((Double)spVolX.getValue()).floatValue();
    vconf.volColorTablePos[1]=((Double)spVolY.getValue()).floatValue();
    vconf.volColorTablePos[2]=((Double)spVolW.getValue()).floatValue();
    vconf.volColorTablePos[3]=((Double)spVolH.getValue()).floatValue();
    vconf.ctTitlePos[0]=((Double)spTitleDx.getValue()).floatValue();
    vconf.ctTitlePos[1]=((Double)spTitleDy.getValue()).floatValue();
    vconf.ctNumPos[0]=((Double)spNumDx.getValue()).floatValue();
    vconf.ctNumPos[1]=((Double)spNumDy.getValue()).floatValue();
  }
  public void actionPerformed( ActionEvent ae ){
    vconf.colorTableType=ctableType.getSelectedIndex();
    vconf.ticsType=ticsType.getSelectedIndex();
    vconf.colorAlphaType=alphaType.getSelectedIndex();
    if(ae.getSource() == applyButton){
      ctrl.RWinRepaint();
    }else if(ae.getSource() == resetButton){
      vconf.resetColorTable();
      update();
    }

  }


}

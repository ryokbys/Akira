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

public class LightPanel extends JPanel implements ActionListener{

  Controller ctrl;
  ViewConfig vconf;
  //constructor
  public LightPanel(Controller ctrl){
    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;
    makePanel();
    //Set up the picker that the button brings up.
    colorPicker = new ColorPicker(true,true);
    colorPicker.setFocusable(false);
  }
  // color picker
  ColorPicker colorPicker;
  // JFrame is necessary to call the ColorPicker.showDialog
  JFrame dummyFrame;

  JButton difColorButton,ambColorButton,spcColorButton,emiColorButton;
  JButton resetButton;

  JDialog jframe;
  JSpinner posXSpinner;
  JSpinner posYSpinner;
  JSpinner posZSpinner;
  JSpinner posDirectionSpinner;
  JLabel difLabel,ambLabel,spcLabel,emiLabel;
  JSpinner shininessSpinner;
  JButton updateButton;
  JLabel posLabel,directionLabel;
  JLabel directionInfoLabel;
  JLabel difInfoLabel,ambInfoLabel,spcInfoLabel,emiInfoLabel;
  JLabel shineLabel;

  // Auto/Manual selection
  JRadioButton jrbAuto,jrbManu;
  ButtonGroup bgAM;

  public void makePanel(){

    //Label: AUTO / MANUAL
    jrbAuto= new JRadioButton( "Auto");
    jrbManu= new JRadioButton( "Manual");
    bgAM= new ButtonGroup();
    bgAM.add(jrbAuto);
    bgAM.add(jrbManu);
    //Initial setting: AUTO
    jrbAuto.setSelected(true);
    jrbAuto.addActionListener(this);
    jrbManu.addActionListener(this);
    jrbAuto.setFocusable(false);
    jrbManu.setFocusable(false);

    //label
    posLabel = new JLabel("Position:" );

    directionLabel = new JLabel("Direction:" );

    shineLabel = new JLabel("Shineness:" );

    //info
    directionInfoLabel= new JLabel("0: Infinitedirection Light" );

    difInfoLabel= new JLabel("From a Particular Point" );
    ambInfoLabel= new JLabel("From All Directions" );
    spcInfoLabel= new JLabel("from a point souce" );
    emiInfoLabel= new JLabel("actually emitted by the polygon" );

    //pos spinner
    Dimension pd = new Dimension(50,25);
    changeListener cl = new changeListener();
    posXSpinner =
      new JSpinner( new SpinnerNumberModel( (double)vconf.lightPos[0],
                                            -1000.0, 1000.0, 5.0 ) );
    posXSpinner.setPreferredSize( pd );
    posXSpinner.addChangeListener( cl );
    posXSpinner.setFocusable(false);

    posYSpinner =
      new JSpinner( new SpinnerNumberModel( (double)vconf.lightPos[1],
                                            -1000.0, 1000.0, 5.0 ) );
    posYSpinner.setPreferredSize( pd );
    posYSpinner.addChangeListener( cl );
    posYSpinner.setFocusable(false);

    posZSpinner =
      new JSpinner( new SpinnerNumberModel( (double)vconf.lightPos[2],
                                            -1000.0, 1000.0, 5.0) );
    posZSpinner.setPreferredSize( pd );
    posZSpinner.addChangeListener( cl );
    posZSpinner.setFocusable(false);

    posDirectionSpinner =
      new JSpinner( new SpinnerNumberModel( (double)vconf.lightPos[3],
                                            -1000.0, 1000.0, 5.0) );
    posDirectionSpinner.setPreferredSize( pd );
    posDirectionSpinner.addChangeListener( cl );
    posDirectionSpinner.setFocusable(false);

    difLabel = new JLabel( "Diffuse" );
    ambLabel = new JLabel( "Ambient" );
    spcLabel = new JLabel( "Specular" );
    emiLabel = new JLabel( "Emission" );


    shininessSpinner =
      new JSpinner( new SpinnerNumberModel( vconf.lightShininess, 0, 128, 1 ) );
    shininessSpinner.setPreferredSize( pd );
    shininessSpinner.addChangeListener( cl );
    shininessSpinner.setFocusable(false);

    updateButton = new JButton("Repaint");
    updateButton.addActionListener( this);
    updateButton.setFocusable(false);

    resetButton = new JButton("Reset");
    resetButton.addActionListener( this);
    resetButton.setFocusable(false);

    Border thickBorder = new LineBorder(Color.WHITE, 1);
    //color chooser
    difColorButton= new JButton();
    difColorButton.addActionListener( this );
    difColorButton.setName("Diff. Color");
    difColorButton.setPreferredSize(new Dimension(80,20));
    difColorButton.setBackground(new Color(vconf.lightDif[0],vconf.lightDif[1],vconf.lightDif[2],vconf.lightDif[3]));
    difColorButton.setOpaque(true);
    difColorButton.setBorderPainted(true);
    difColorButton.setBorder(BorderFactory.createLineBorder(Color.gray));
    difColorButton.setFocusable(false);

    ambColorButton= new JButton();
    ambColorButton.addActionListener( this );
    ambColorButton.setName("Amb. Color");
    ambColorButton.setPreferredSize(new Dimension(80,20));
    ambColorButton.setBorderPainted(true);
    ambColorButton.setOpaque(true);
    ambColorButton.setBackground(new Color(vconf.lightAmb[0],vconf.lightAmb[1],vconf.lightAmb[2],vconf.lightAmb[3]));
    ambColorButton.setBorder(BorderFactory.createLineBorder(Color.gray));
    ambColorButton.setFocusable(false);

    spcColorButton= new JButton();
    spcColorButton.addActionListener( this );
    spcColorButton.setName("Spc. Color");
    spcColorButton.setPreferredSize(new Dimension(80,20));
    spcColorButton.setBorderPainted(true);
    spcColorButton.setOpaque(true);
    spcColorButton.setBackground(new Color(vconf.lightSpc[0],vconf.lightSpc[1],vconf.lightSpc[2],vconf.lightSpc[3]));
    spcColorButton.setBorder(BorderFactory.createLineBorder(Color.gray));
    spcColorButton.setFocusable(false);

    emiColorButton= new JButton();
    emiColorButton.addActionListener( this );
    emiColorButton.setName("Emi. Color");
    emiColorButton.setPreferredSize(new Dimension(80,20));
    emiColorButton.setBorderPainted(true);
    emiColorButton.setOpaque(true);
    emiColorButton.setBackground(new Color(vconf.lightEmi[0],vconf.lightEmi[1],vconf.lightEmi[2],vconf.lightEmi[3]));
    emiColorButton.setBorder(BorderFactory.createLineBorder(Color.gray));
    emiColorButton.setFocusable(false);

    SpringLayout layout = new SpringLayout();
    //pos label
    layout.putConstraint( SpringLayout.NORTH, posLabel, 10,
                          SpringLayout.NORTH, this );
    layout.putConstraint( SpringLayout.WEST,  posLabel, 10,
                          SpringLayout.WEST,  this );
    //Position auto/manual buttons
    layout.putConstraint( SpringLayout.NORTH, jrbAuto,0,
                          SpringLayout.NORTH, posLabel );
    layout.putConstraint( SpringLayout.WEST, jrbAuto,0,
                          SpringLayout.EAST, posLabel );
    layout.putConstraint( SpringLayout.NORTH, jrbManu,0,
                          SpringLayout.NORTH, jrbAuto );
    layout.putConstraint( SpringLayout.WEST, jrbManu,0,
                          SpringLayout.EAST, jrbAuto );
    //pos spinner
    layout.putConstraint( SpringLayout.NORTH, posXSpinner, 0,
                          SpringLayout.NORTH, posLabel );
    layout.putConstraint( SpringLayout.WEST,  posXSpinner, 10,
                          SpringLayout.EAST,  jrbManu );
    layout.putConstraint( SpringLayout.NORTH, posYSpinner, 0,
                          SpringLayout.NORTH, posXSpinner );
    layout.putConstraint( SpringLayout.WEST,  posYSpinner, 0,
                          SpringLayout.EAST,  posXSpinner );
    layout.putConstraint( SpringLayout.NORTH, posZSpinner, 0,
                          SpringLayout.NORTH, posYSpinner );
    layout.putConstraint( SpringLayout.WEST,  posZSpinner, 0,
                          SpringLayout.EAST,  posYSpinner );

    //light direction parameter
    layout.putConstraint( SpringLayout.NORTH, directionLabel, 0,
                          SpringLayout.SOUTH, posXSpinner );
    layout.putConstraint( SpringLayout.WEST,  directionLabel, 0,
                          SpringLayout.WEST,  posLabel );
    //
    layout.putConstraint( SpringLayout.NORTH, posDirectionSpinner, 0,
                          SpringLayout.SOUTH, directionLabel );
    layout.putConstraint( SpringLayout.WEST,  posDirectionSpinner, 0,
                          SpringLayout.WEST,  directionLabel );
    //
    layout.putConstraint( SpringLayout.NORTH, directionInfoLabel, 2,
                          SpringLayout.NORTH, posDirectionSpinner );
    layout.putConstraint( SpringLayout.WEST,  directionInfoLabel, 5,
                          SpringLayout.EAST,  posDirectionSpinner );

    //shineness
    layout.putConstraint( SpringLayout.NORTH, shineLabel, 0,
                          SpringLayout.SOUTH, posDirectionSpinner );
    layout.putConstraint( SpringLayout.WEST,  shineLabel, 0,
                          SpringLayout.WEST,  posDirectionSpinner );
    layout.putConstraint( SpringLayout.NORTH, shininessSpinner, 0,
                          SpringLayout.SOUTH, shineLabel );
    layout.putConstraint( SpringLayout.WEST,  shininessSpinner, 0,
                          SpringLayout.WEST,  shineLabel );

    //repaint buttons
    layout.putConstraint( SpringLayout.SOUTH, updateButton, -10,
                          SpringLayout.SOUTH, this );
    layout.putConstraint( SpringLayout.WEST,  updateButton, 10,
                          SpringLayout.WEST,  this );

    layout.putConstraint( SpringLayout.NORTH, resetButton, 0,
                          SpringLayout.NORTH, updateButton );
    layout.putConstraint( SpringLayout.WEST,  resetButton, 0,
                          SpringLayout.EAST,  updateButton);

    //lights color
    layout.putConstraint( SpringLayout.NORTH, difLabel, 10,
                          SpringLayout.NORTH, this );
    layout.putConstraint( SpringLayout.WEST,  difLabel, 500,
                          SpringLayout.WEST,  this );
    layout.putConstraint( SpringLayout.NORTH, ambLabel, 20,
                          SpringLayout.SOUTH, difLabel );
    layout.putConstraint( SpringLayout.WEST,  ambLabel, 0,
                          SpringLayout.WEST,  difLabel );
    layout.putConstraint( SpringLayout.NORTH, spcLabel, 20,
                          SpringLayout.SOUTH, ambLabel );
    layout.putConstraint( SpringLayout.WEST,  spcLabel, 0,
                          SpringLayout.WEST,  ambLabel );
    layout.putConstraint( SpringLayout.NORTH, emiLabel, 20,
                          SpringLayout.SOUTH, spcLabel );
    layout.putConstraint( SpringLayout.WEST,  emiLabel, 0,
                          SpringLayout.WEST,  spcLabel );

    //color chooser
    layout.putConstraint( SpringLayout.NORTH, difColorButton, 0,
                          SpringLayout.NORTH, difLabel );
    layout.putConstraint( SpringLayout.WEST,  difColorButton, 30,
                          SpringLayout.EAST,  difLabel );

    layout.putConstraint( SpringLayout.NORTH, ambColorButton, 0,
                          SpringLayout.NORTH, ambLabel );
    layout.putConstraint( SpringLayout.WEST,  ambColorButton, 0,
                          SpringLayout.WEST,  difColorButton );

    layout.putConstraint( SpringLayout.NORTH, spcColorButton, 0,
                          SpringLayout.NORTH, spcLabel );
    layout.putConstraint( SpringLayout.WEST,  spcColorButton, 0,
                          SpringLayout.WEST,  difColorButton );

    layout.putConstraint( SpringLayout.NORTH, emiColorButton, 0,
                          SpringLayout.NORTH, emiLabel );
    layout.putConstraint( SpringLayout.WEST,  emiColorButton, 0,
                          SpringLayout.WEST,  difColorButton );
    //info label
    layout.putConstraint( SpringLayout.NORTH, difInfoLabel, 2,
                          SpringLayout.NORTH, difColorButton );
    layout.putConstraint( SpringLayout.WEST,  difInfoLabel, 5,
                          SpringLayout.EAST,  difColorButton );

    layout.putConstraint( SpringLayout.NORTH, ambInfoLabel, 2,
                          SpringLayout.NORTH, ambColorButton );
    layout.putConstraint( SpringLayout.WEST,  ambInfoLabel, 5,
                          SpringLayout.EAST,  ambColorButton );

    layout.putConstraint( SpringLayout.NORTH, spcInfoLabel, 2,
                          SpringLayout.NORTH, spcColorButton );
    layout.putConstraint( SpringLayout.WEST,  spcInfoLabel, 5,
                          SpringLayout.EAST,  spcColorButton );

    layout.putConstraint( SpringLayout.NORTH, emiInfoLabel, 2,
                          SpringLayout.NORTH, emiColorButton );
    layout.putConstraint( SpringLayout.WEST,  emiInfoLabel, 5,
                          SpringLayout.EAST,  emiColorButton );


    this.setLayout( layout );
    this.add( posLabel );
    this.add( jrbAuto );
    this.add( jrbManu );
    this.add( posXSpinner );
    this.add( posYSpinner );
    this.add( posZSpinner );
    this.add( directionLabel );
    this.add( posDirectionSpinner );
    this.add( directionInfoLabel );


    this.add( shineLabel );

    this.add( difLabel );
    this.add( ambLabel );
    this.add( spcLabel );
    this.add( emiLabel );

    this.add( shininessSpinner );

    this.add( updateButton );
    this.add( resetButton );

    this.add( difColorButton );
    this.add( ambColorButton );
    this.add( spcColorButton );
    this.add( emiColorButton );

    this.add( difInfoLabel );
    this.add( ambInfoLabel );
    this.add( spcInfoLabel );
    this.add( emiInfoLabel );

    // Auto/Manual setting
    this.manualSettingEnabled( jrbManu.isSelected() );
  }


  class changeListener implements ChangeListener {
    public void stateChanged( ChangeEvent ce ){
      if( ce.getSource() == shininessSpinner ){
        vconf.lightShininess = ((Integer)shininessSpinner.getValue()).intValue();
        return;
      }
      else if( ce.getSource() == posXSpinner ){
        vconf.lightPos[0] = ((Double)posXSpinner.getValue()).floatValue();
      }
      else if( ce.getSource() == posYSpinner ){
        vconf.lightPos[1] = ((Double)posYSpinner.getValue()).floatValue();
      }
      else if( ce.getSource() == posZSpinner ){
        vconf.lightPos[2] = ((Double)posZSpinner.getValue()).floatValue();
      }
      else if( ce.getSource() == posDirectionSpinner ){
        vconf.lightPos[3] = ((Double)posDirectionSpinner.getValue()).floatValue();
      }

    }
  }


  public void actionPerformed( ActionEvent ae ){
    Color newColor;
    if( ae.getSource()==jrbAuto ){
      vconf.isLightPosAuto= true;
      this.manualSettingEnabled( jrbManu.isSelected() );
    }else if( ae.getSource()==jrbManu ){
      vconf.isLightPosAuto= false;
      this.manualSettingEnabled( jrbManu.isSelected() );
    }else if( ae.getSource() == updateButton ){
      ctrl.setLight();
      ctrl.RWinRepaint();

    }else if( ae.getSource() == resetButton ){
      resetParameters();
    }else if( ae.getSource() == difColorButton ){
      colorPicker.setColor(new Color(vconf.lightDif[0],vconf.lightDif[1],vconf.lightDif[2],vconf.lightDif[3]));
      newColor=new Color(vconf.lightDif[0],vconf.lightDif[1],vconf.lightDif[2],vconf.lightDif[3]);
      newColor= colorPicker.showDialog(dummyFrame,newColor,true);
      if( newColor!=null ){
        this.setColor( newColor ,vconf.lightDif);
        difColorButton.setBackground(new Color(vconf.lightDif[0],vconf.lightDif[1],vconf.lightDif[2],vconf.lightDif[3]));
      }
    }else if( ae.getSource() == ambColorButton ){
      colorPicker.setColor(new Color(vconf.lightAmb[0],vconf.lightAmb[1],vconf.lightAmb[2],vconf.lightAmb[3]));
      newColor=new Color(vconf.lightAmb[0],vconf.lightAmb[1],vconf.lightAmb[2],vconf.lightAmb[3]);
      newColor= colorPicker.showDialog(dummyFrame,newColor,true);
      if( newColor!=null ){
        this.setColor( newColor, vconf.lightAmb);
        ambColorButton.setBackground(new Color(vconf.lightAmb[0],vconf.lightAmb[1],vconf.lightAmb[2],vconf.lightAmb[3]));
      }
    }else if( ae.getSource() == spcColorButton ){
      colorPicker.setColor(new Color(vconf.lightSpc[0],vconf.lightSpc[1],vconf.lightSpc[2],vconf.lightSpc[3]));
      newColor=new Color(vconf.lightSpc[0],vconf.lightSpc[1],vconf.lightSpc[2],vconf.lightSpc[3]);
      newColor= colorPicker.showDialog(dummyFrame,newColor,true);
      if( newColor!=null ){
        this.setColor( newColor, vconf.lightSpc);
        spcColorButton.setBackground(new Color(vconf.lightSpc[0],vconf.lightSpc[1],vconf.lightSpc[2],vconf.lightSpc[3]));
      }
    }else if( ae.getSource() == emiColorButton ){
      colorPicker.setColor(new Color(vconf.lightEmi[0],vconf.lightEmi[1],vconf.lightEmi[2],vconf.lightEmi[3]));
      newColor=new Color(vconf.lightEmi[0],vconf.lightEmi[1],vconf.lightEmi[2],vconf.lightEmi[3]);
      newColor= colorPicker.showDialog(dummyFrame,newColor,true);
      if( newColor!=null ){
        this.setColor( newColor, vconf.lightEmi );
        emiColorButton.setBackground(new Color(vconf.lightEmi[0],vconf.lightEmi[1],vconf.lightEmi[2],vconf.lightEmi[3]));
      }
    }
  }

  public void setColor( Color color ,float[] target){
    float[] tmp = color.getRGBColorComponents( null );
    float alp=(float)(color.getAlpha()/255.f);
    target[0]=tmp[0];
    target[1]=tmp[1];
    target[2]=tmp[2];
    target[3]=alp;
  }

  void resetParameters(){
    //set values
    posXSpinner.setValue((double)vconf.lightPos[0]);
    posYSpinner.setValue((double)vconf.lightPos[1]);
    posZSpinner.setValue((double)vconf.lightPos[2]);
    posDirectionSpinner.setValue((double)vconf.lightPos[3]);

    difColorButton.setBackground(new Color(vconf.lightDif[0],vconf.lightDif[1],vconf.lightDif[2],vconf.lightDif[3]));
    ambColorButton.setBackground(new Color(vconf.lightAmb[0],vconf.lightAmb[1],vconf.lightAmb[2],vconf.lightAmb[3]));
    spcColorButton.setBackground(new Color(vconf.lightSpc[0],vconf.lightSpc[1],vconf.lightSpc[2],vconf.lightSpc[3]));
    emiColorButton.setBackground(new Color(vconf.lightEmi[0],vconf.lightEmi[1],vconf.lightEmi[2],vconf.lightEmi[3]));
    shininessSpinner.setValue(vconf.lightShininess);
  }

  // Enable/Disable for manual position setting
  private void manualSettingEnabled( boolean b ){
    posXSpinner.setEnabled(b);
    posYSpinner.setEnabled(b);
    posZSpinner.setEnabled(b);
    posDirectionSpinner.setEnabled(b);
    // shininessSpinner.setEnabled(b);
    // difColorButton.setEnabled(b);
    // ambColorButton.setEnabled(b);
    // spcColorButton.setEnabled(b);
    // emiColorButton.setEnabled(b);
  }

}

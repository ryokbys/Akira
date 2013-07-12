package viewer.viewConfigPanel;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.LineBorder;
import info.clearthought.layout.*;

import tools.colorpicker.*;

import viewer.*;
import tools.*;
import viewer.renderer.*;

public class AnnotationPanel extends JPanel implements ActionListener,ChangeListener{
  /* accesser */
  public void updateStatus(){
    RenderingWindow rw=ctrl.getActiveRW();
    cbAxis.setSelected(rw.visibleAxis);
    cbBox.setSelected(rw.visibleBox);
    cbTime.setSelected(rw.visibleTime);


  }
  /* accesser */



  public void stateChanged( ChangeEvent ce ){
    vconf.txtPos[0]=((Double)spTX.getValue()).floatValue();
    vconf.txtPos[1]=((Double)spTY.getValue()).floatValue();
    vconf.boxLineWidth=((Double)spBoxW.getValue()).floatValue();
    updateAnnotationFont();
  }

  public void actionPerformed( ActionEvent ae){
    RenderingWindow rw=ctrl.getActiveRW();
    if(rw==null)return;

    rw.visibleAxis=cbAxis.isSelected();
    rw.visibleBox=cbBox.isSelected();
    rw.visibleTime=cbTime.isSelected();

    if( ae.getSource() == applyTimeFormatButton ){
      vconf.timePrintFormat=timeFormatTextArea.getText();
    }else if( ae.getSource() == fontsComboBox){
      updateAnnotationFont();
    }else if( ae.getSource() == fontStyleComboBox){
      updateAnnotationFont();
    }

    if( ae.getSource() == bgColorButton ){
      Color prevColor= new Color(vconf.bgColor[0],vconf.bgColor[1],
                                 vconf.bgColor[2],vconf.bgColor[3]);
      Color newColor= new Color(vconf.bgColor[0],vconf.bgColor[1],
                                 vconf.bgColor[2],vconf.bgColor[3]);
      bgColorButton.setForeground(prevColor);

      colorPicker.setColor(prevColor);

      newColor= colorPicker.showDialog(dummyFrame,newColor,true);

      if( newColor==null ){
        // usr previous color if the user cancels the dialog
        newColor= prevColor;
      }else{ // newColor exists
        vconf.bgColor=newColor.getRGBComponents(null);
      }
      bgColorButton.setBackground( newColor );
    }else if( ae.getSource() == txtColorButton ){
      Color prevColor= new Color(vconf.txtColor[0],vconf.txtColor[1],
                                 vconf.txtColor[2],vconf.txtColor[3]);
      Color newColor= new Color(vconf.txtColor[0],vconf.txtColor[1],
                                 vconf.txtColor[2],vconf.txtColor[3]);
      txtColorButton.setForeground(prevColor);

      colorPicker.setColor(prevColor);

      newColor= colorPicker.showDialog(dummyFrame,newColor,true);

      if( newColor==null ){
        // usr previous color if the user cancels the dialog
        newColor= prevColor;
      }else{ // newColor exists
        vconf.txtColor=newColor.getRGBComponents(null);
      }

      txtColorButton.setBackground( newColor );
    }else if( ae.getSource() == boxColorButton ){
      Color prevColor= new Color(vconf.boxColor[0],vconf.boxColor[1],
                                 vconf.boxColor[2],vconf.boxColor[3]);
      Color newColor= new Color(vconf.boxColor[0],vconf.boxColor[1],
                                 vconf.boxColor[2],vconf.boxColor[3]);
      boxColorButton.setForeground(prevColor);

      colorPicker.setColor(prevColor);

      newColor= colorPicker.showDialog(dummyFrame,newColor,true);

      if( newColor==null ){
        // usr previous color if the user cancels the dialog
        newColor= prevColor;
      }else{ // newColor exists
        vconf.boxColor=newColor.getRGBComponents(null);
      }
      boxColorButton.setBackground( newColor );
    }else if(ae.getSource() == resetButton){
      vconf.resetMisc();

      bgColorButton.setBackground(new Color(vconf.bgColor[0],vconf.bgColor[1],
                                       vconf.bgColor[2],vconf.bgColor[3]));
      txtColorButton.setBackground(new Color(vconf.txtColor[0],vconf.txtColor[1],
                                        vconf.txtColor[2],vconf.txtColor[3]));
      boxColorButton.setBackground(new Color(vconf.boxColor[0],vconf.boxColor[1],
                                        vconf.boxColor[2],vconf.boxColor[3]));
      timeFormatTextArea.setText(vconf.timePrintFormat);
    }
    ctrl.RWinRepaint();
  }



  private Controller ctrl;
  private ViewConfig vconf;
  //constructor
  public AnnotationPanel(Controller ctrl){
    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;
    create();
    //Set up the picker that the button brings up.
    colorPicker = new ColorPicker(true,true);
    colorPicker.setFocusable(false);

  }



  //left
  private JTextField timeFormatTextArea;
  private JButton applyTimeFormatButton;
  private JSpinner spTX,spTY;


  JCheckBox cbAxis,cbTime,cbBox;

  //right
  JLabel bgColorLabel;
  JLabel txtColorLabel;
  JLabel boxColorLabel;
  JButton bgColorButton;
  JButton txtColorButton;
  JButton boxColorButton;
  private JSpinner spBoxW;

  // panels that provide borders to the buttons
  JPanel bgColorPanel;
  JPanel txtColorPanel;
  JPanel boxColorPanel;

  JButton resetButton;

  JComboBox fontsComboBox;
  String[] fontStyle = {"PLAIN", "BOLD", "ITALIC"};
  JComboBox fontStyleComboBox;
  JSpinner fontSizeSP;

  // color picker
  private ColorPicker colorPicker;
  // JFrame is necessary to call the ColorPicker.showDialog
  private JFrame dummyFrame;



  public void create(){
    this.addKeyListener(ctrl.keyCtrl);
    //General panel
    setFocusable( false );

    cbAxis= new JCheckBox( "Show axis",true);
    cbAxis.addActionListener(this);
    cbAxis.setFocusable(false);

    cbTime= new JCheckBox( "Show time",true);
    cbTime.addActionListener(this);
    cbTime.setFocusable(false);

    cbBox= new JCheckBox( "Draw simulation box",true);
    cbBox.addActionListener(this);
    cbBox.setFocusable(false);

    //----left
    timeFormatTextArea = new JTextField(vconf.timePrintFormat);
    timeFormatTextArea.setEditable(true);
    timeFormatTextArea.setPreferredSize( new Dimension(100,25) );
    timeFormatTextArea.setFocusable(false);

    //timeFormatTextArea.setFocusable(false);
    applyTimeFormatButton=new JButton("Apply");
    applyTimeFormatButton.addActionListener(this);
    applyTimeFormatButton.setFocusable(false);

    spTX =new JSpinner( new SpinnerNumberModel( (double)vconf.txtPos[0],0.0, null, 1 ) );
    spTX.setPreferredSize( new Dimension(50,25) );
    spTX.addChangeListener( this );
    spTX.setFocusable(false);

    spTY =new JSpinner( new SpinnerNumberModel( (double)vconf.txtPos[1],0.0, null, 1 ) );
    spTY.setPreferredSize( new Dimension(50,25) );
    spTY.addChangeListener( this );
    spTY.setFocusable(false);




    //-----right: colors
    // buttons without borders
    bgColorButton= new JButton();
    bgColorButton.setFocusable(false);
    bgColorButton.setBorder(new LineBorder(Color.BLACK, 1, true));
    bgColorButton.addActionListener(this);
    bgColorButton.setOpaque(true);
    bgColorButton.setName("BG");
    bgColorButton.setPreferredSize(new Dimension(50,25));
    bgColorButton.setBackground(new Color(vconf.bgColor[0],vconf.bgColor[1],
                                     vconf.bgColor[2],vconf.bgColor[3]));

    txtColorButton= new JButton();
    txtColorButton.setFocusable(false);
    txtColorButton.setBorder(new LineBorder(Color.BLACK, 1, true));
    txtColorButton.addActionListener(this);
    txtColorButton.setOpaque(true);
    txtColorButton.setName("Text");
    txtColorButton.setPreferredSize(new Dimension(50,25));
    txtColorButton.setBackground(new Color(vconf.txtColor[0],vconf.txtColor[1],
                                     vconf.txtColor[2],vconf.txtColor[3]));

    boxColorButton= new JButton();
    boxColorButton.setFocusable(false);
    boxColorButton.setBorder(new LineBorder(Color.BLACK, 1, true));
    boxColorButton.addActionListener(this);
    boxColorButton.setOpaque(true);
    boxColorButton.setName("Box");
    boxColorButton.setPreferredSize(new Dimension(50,25));
    boxColorButton.setBackground(new Color(vconf.boxColor[0],vconf.boxColor[1],
                                      vconf.boxColor[2],vconf.boxColor[3]));

    spBoxW =new JSpinner( new SpinnerNumberModel( (double)vconf.boxLineWidth,0.0, null, 1 ) );
    spBoxW.setPreferredSize( new Dimension(40,25) );
    spBoxW.addChangeListener( this );

    //
    bgColorLabel= new JLabel("Background Color: ");
    bgColorLabel.setFocusable(false);
    txtColorLabel= new JLabel("Color: ");
    txtColorLabel.setFocusable(false);
    boxColorLabel= new JLabel("Color: ");
    boxColorLabel.setFocusable(false);
    bgColorPanel= new JPanel(new BorderLayout());
    bgColorPanel.setFocusable(false);
    bgColorPanel.setPreferredSize(new Dimension(50,25));
    txtColorPanel= new JPanel(new BorderLayout());
    txtColorPanel.setFocusable(false);
    txtColorPanel.setPreferredSize(new Dimension(50,25));
    boxColorPanel= new JPanel(new BorderLayout());
    boxColorPanel.setFocusable(false);
    boxColorPanel.setPreferredSize(new Dimension(50,25));
    bgColorPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
    txtColorPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
    boxColorPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
    bgColorPanel.add(bgColorButton,BorderLayout.CENTER);
    txtColorPanel.add(txtColorButton,BorderLayout.CENTER);
    boxColorPanel.add(boxColorButton,BorderLayout.CENTER);



    resetButton= new JButton("Revert to Default");
    resetButton.setFocusable(false);
    resetButton.addActionListener(this);



    //annotation font
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    Font[] fonts = ge.getAllFonts();
    String[] fontsList=new String[fonts.length];
    for(int i=0;i<fonts.length;i++){
      fontsList[i]=fonts[i].getName();
      //System.out.println(fonts[i].getName());
    }
    fonts=null;

    fontsComboBox = new JComboBox(fontsList);
    fontsComboBox.setSelectedItem(vconf.annotationFont.getName());
    fontsComboBox.addActionListener(this);
    fontsComboBox.setFocusable(false);

    fontStyleComboBox = new JComboBox(fontStyle);
    fontStyleComboBox.setSelectedIndex(vconf.annotationFont.getStyle());
    fontStyleComboBox.addActionListener(this);
    fontStyleComboBox.setFocusable(false);

    //System.out.println(vconf.annotationFont.getName());
    //System.out.println(vconf.annotationFont.getStyle());


    fontSizeSP = new JSpinner(new SpinnerNumberModel(vconf.annotationFont.getSize(), 1, null, 2));
    fontSizeSP.setFocusable(false);
    fontSizeSP.setPreferredSize(new Dimension(50, 25));
    fontSizeSP.addChangeListener(this);


    SpringLayout layout = new SpringLayout();
    setLayout( layout );

    //axis
    layout.putConstraint( SpringLayout.NORTH, cbAxis, 10, SpringLayout.NORTH,this );
    layout.putConstraint( SpringLayout.WEST,  cbAxis, 20, SpringLayout.WEST, this );
    //time
    layout.putConstraint( SpringLayout.NORTH, cbTime, 30, SpringLayout.SOUTH, cbAxis );
    layout.putConstraint( SpringLayout.WEST, cbTime, 0, SpringLayout.WEST, cbAxis );
    JLabel lTime=new JLabel("Format:");
    layout.putConstraint( SpringLayout.NORTH, lTime, 10, SpringLayout.SOUTH, cbTime);
    layout.putConstraint( SpringLayout.WEST, lTime, 20, SpringLayout.WEST, cbTime);
    layout.putConstraint( SpringLayout.NORTH, timeFormatTextArea, 0, SpringLayout.NORTH, lTime);
    layout.putConstraint( SpringLayout.WEST, timeFormatTextArea, 5, SpringLayout.EAST, lTime);
    JLabel lTimePos=new JLabel("Legend Position:");
    layout.putConstraint( SpringLayout.NORTH, lTimePos, 10, SpringLayout.SOUTH, timeFormatTextArea);
    layout.putConstraint( SpringLayout.WEST, lTimePos, 0, SpringLayout.WEST, lTime);
    layout.putConstraint( SpringLayout.NORTH, spTX, 0, SpringLayout.NORTH,lTimePos);
    layout.putConstraint( SpringLayout.WEST, spTX, 10, SpringLayout.EAST, lTimePos);
    layout.putConstraint( SpringLayout.NORTH,spTY, 0, SpringLayout.NORTH, spTX);
    layout.putConstraint( SpringLayout.WEST, spTY, 5, SpringLayout.EAST, spTX);

    //box
    layout.putConstraint( SpringLayout.NORTH, cbBox, 30, SpringLayout.SOUTH, spTX );
    layout.putConstraint( SpringLayout.WEST,  cbBox, 0, SpringLayout.WEST, cbAxis );
    layout.putConstraint( SpringLayout.NORTH, boxColorLabel, 5, SpringLayout.SOUTH, cbBox);
    layout.putConstraint( SpringLayout.WEST, boxColorLabel, 20, SpringLayout.WEST, cbBox);
    layout.putConstraint( SpringLayout.NORTH, boxColorPanel, 0, SpringLayout.NORTH,boxColorLabel);
    layout.putConstraint( SpringLayout.WEST, boxColorPanel, 10, SpringLayout.EAST, boxColorLabel);
    JLabel lBoxW=new JLabel("Box Line Width");
    layout.putConstraint( SpringLayout.NORTH, lBoxW, 10 ,SpringLayout.SOUTH,boxColorPanel );
    layout.putConstraint( SpringLayout.WEST, lBoxW, 0, SpringLayout.WEST, boxColorLabel );
    layout.putConstraint( SpringLayout.NORTH, spBoxW, 0, SpringLayout.NORTH,lBoxW);
    layout.putConstraint( SpringLayout.WEST, spBoxW, 10, SpringLayout.EAST, lBoxW);


    //background
    layout.putConstraint( SpringLayout.NORTH, bgColorLabel, 30, SpringLayout.SOUTH, spBoxW );
    layout.putConstraint( SpringLayout.WEST, bgColorLabel, 0, SpringLayout.WEST, cbAxis );
    layout.putConstraint( SpringLayout.NORTH, bgColorPanel, 0, SpringLayout.NORTH, bgColorLabel);
    layout.putConstraint( SpringLayout.WEST, bgColorPanel, 10, SpringLayout.EAST, bgColorLabel);

    //font
    JLabel lFont=new JLabel("Font:");
    add(lFont);
    layout.putConstraint( SpringLayout.NORTH, lFont, 30, SpringLayout.SOUTH, bgColorPanel );
    layout.putConstraint( SpringLayout.WEST, lFont, 0, SpringLayout.WEST, cbAxis );
    layout.putConstraint( SpringLayout.NORTH, fontsComboBox, 0, SpringLayout.NORTH, lFont);
    layout.putConstraint( SpringLayout.WEST, fontsComboBox, 5, SpringLayout.EAST, lFont);
    JLabel lFontStyle=new JLabel("Style:");
    add(lFontStyle);
    layout.putConstraint( SpringLayout.NORTH, lFontStyle, 5, SpringLayout.SOUTH, fontsComboBox);
    layout.putConstraint( SpringLayout.WEST,  lFontStyle, 20, SpringLayout.WEST, lFont);
    layout.putConstraint( SpringLayout.NORTH, fontStyleComboBox, 0, SpringLayout.NORTH, lFontStyle);
    layout.putConstraint( SpringLayout.WEST, fontStyleComboBox, 5, SpringLayout.EAST, lFontStyle);
    JLabel lFontSize=new JLabel("Size:");
    layout.putConstraint( SpringLayout.NORTH, lFontSize, 5, SpringLayout.SOUTH, fontStyleComboBox);
    layout.putConstraint( SpringLayout.WEST, lFontSize, 0, SpringLayout.WEST, lFontStyle);
    layout.putConstraint( SpringLayout.NORTH, fontSizeSP, 0, SpringLayout.NORTH, lFontSize);
    layout.putConstraint( SpringLayout.WEST, fontSizeSP, 5, SpringLayout.EAST, lFontSize);
    layout.putConstraint( SpringLayout.NORTH, txtColorLabel, 0, SpringLayout.SOUTH, fontSizeSP);
    layout.putConstraint( SpringLayout.WEST, txtColorLabel, 0, SpringLayout.WEST, lFontSize);
    layout.putConstraint( SpringLayout.NORTH, txtColorPanel, 0, SpringLayout.NORTH, txtColorLabel);
    layout.putConstraint( SpringLayout.WEST, txtColorPanel, 5, SpringLayout.EAST, txtColorLabel);

    layout.putConstraint( SpringLayout.NORTH, applyTimeFormatButton, 20, SpringLayout.SOUTH, txtColorPanel );
    layout.putConstraint( SpringLayout.EAST, applyTimeFormatButton, -10, SpringLayout.WEST, resetButton );

    layout.putConstraint( SpringLayout.NORTH, resetButton,0,SpringLayout.NORTH, applyTimeFormatButton );
    layout.putConstraint( SpringLayout.EAST, resetButton, -10, SpringLayout.EAST, this );

    add(cbAxis);
    add(cbTime);
    add(cbBox);

    add(lBoxW);
    add(lFontSize);
    add(timeFormatTextArea);
    add(spTX);
    add(spTY);
    add(lTime);
    add(lTimePos);

    add(fontsComboBox);
    add(fontStyleComboBox);
    add(fontSizeSP);
    add(txtColorLabel);
    add(txtColorPanel);

    add(bgColorLabel);
    add(bgColorPanel);
    add(boxColorLabel);
    add(boxColorPanel);
    add(spBoxW);

    add(applyTimeFormatButton);
    add(resetButton);
  }

  void updateAnnotationFont(){
    vconf.annotationFont=null;
    if(fontStyleComboBox.getSelectedIndex()==0){
      vconf.annotationFont=new Font((String)fontsComboBox.getSelectedItem(),
                                    Font.PLAIN,
                                    ((Integer)fontSizeSP.getValue()).intValue());
    }else if(fontStyleComboBox.getSelectedIndex()==1){
      vconf.annotationFont=new Font((String)fontsComboBox.getSelectedItem(),
                                    Font.BOLD,
                                    ((Integer)fontSizeSP.getValue()).intValue());
    }else if(fontStyleComboBox.getSelectedIndex()==2){
      vconf.annotationFont=new Font((String)fontsComboBox.getSelectedItem(),
                                    Font.ITALIC,
                                    ((Integer)fontSizeSP.getValue()).intValue());
    }
    ctrl.updateAnnotationFont();
  }
}

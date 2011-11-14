package viewer.viewConfigPanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;
import info.clearthought.layout.*;


import viewer.*;
import viewer.renderer.*;
import viewer.LF.*;

public class ManipulationPanel extends JPanel implements ActionListener,MouseListener,ChangeListener{

  private Controller ctrl;
  private ViewConfig vconf;

  public ManipulationPanel(Controller ctrl){
    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;

    createPanel();
  }



  public void actionPerformed(ActionEvent ae){
    RenderingWindow RW=ctrl.getActiveRW();
    if(RW==null)return;

    float val=vconf.ControllerValue;
    if(ae.getSource() == revertHomeButton){
      RW.setVPHome();
    }else if(ae.getSource() == revertXButton){
      RW.resetRotation('x');
    }else if(ae.getSource() == revertYButton){
      RW.resetRotation('y');
    }else if(ae.getSource() == revertSavedHomeButton){
      RW.setVPSavedHome();
    }else if(ae.getSource() == saveViewPointButton){
      RW.saveViewPoint();
    }else if(ae.getSource() == rotUpButton){
      RW.setObjectRotate(0.f, val, 0.f);
    }else if(ae.getSource() == rotDwnButton){
      RW.setObjectRotate(0.f, -val, 0.f);
    }else if(ae.getSource() == rotLeftButton){
      RW.setObjectRotate(val, 0.f, 0.f);
    }else if(ae.getSource() == rotRightButton){
      RW.setObjectRotate(-val, 0.f, 0.f);
    }else if(ae.getSource() == rotClockWiseButton){
      RW.setObjectRotate(0.f, 0.f, -val);
    }else if(ae.getSource() == rotCounterClockWiseButton){
      RW.setObjectRotate(0.f, 0.f, val);
    }else if(ae.getSource() == trnsUpButton){
      RW.setObjectCenter(0.f, val,0.f);
    }else if(ae.getSource() == trnsDownButton){
      RW.setObjectCenter(0.f, -val,0.f);
    }else if(ae.getSource() == trnsRightButton){
      RW.setObjectCenter(val, 0.f,0.f);
    }else if(ae.getSource() == trnsLeftButton){
      RW.setObjectCenter(-val, 0.f,0.f);
    }else if(ae.getSource() == trnsZupButton){
      RW.setObjectCenter(0.f, 0.f,-val);
    }else if(ae.getSource() == trnsZdownButton){
      RW.setObjectCenter(0.f, 0.f,val);
    }else if(ae.getSource() == zoomInButton){
      RW.keyZoom(-(int)val);
    }else if(ae.getSource() == zoomOutButton){
      RW.keyZoom((int)val);
    }else if(ae.getSource() == startStopButton){
      RW.startstopAnimating();
    }else if(ae.getSource() == snapShotButton){
      RW.writeImage();
    }else if(ae.getSource() == recordButton){
      RW.writeSequentialImage();
    }else if( ae.getSource() == cmbImgFormat){
      vconf.imageFormat=(String)cmbImgFormat.getSelectedItem();
    }else{
      //revert VP
      for(int i=0;i<MAX_SAVEDHOME;i++){
        if(ae.getSource() == menuSavedHome[i]) RW.setVPSavedHome(i);
      }

    }
    RW.updateStatusString();

    requestFocusInWindow();
  }

  public void changebtnStSpIcon(RenderingWindow rw){
    //if(rw!=null && rw.isAnimating()) startStopButton.setIcon(icnStop);
    //else startStopButton.setIcon(icnPlay);
  }

  //mouse listener
  public void mouseClicked(MouseEvent e){
  }
  public void mouseEntered(MouseEvent e){
  }
  public void mouseExited(MouseEvent e){
  }
  public void mousePressed(MouseEvent e){
    showPopup(e);
  }
  public void mouseReleased(MouseEvent e){
    showPopup(e);
    requestFocusInWindow();
  }
  void showPopup(MouseEvent e){
    //popup
    if(e.isPopupTrigger() && e.getComponent()==revertSavedHomeButton){
      revertSavedHomePopup.show(e.getComponent(), e.getX(), e.getY());
    }
  }


  public void stateChanged( ChangeEvent ce ){
    vconf.ControllerValue=(Float)valInputSpinner.getValue();
    vconf.isRotationXOnly=cbRotationXOnly.isSelected();
    vconf.isRotationYOnly=cbRotationYOnly.isSelected();
    vconf.isTransXOnly=cbTransXOnly.isSelected();
    vconf.isTransYOnly=cbTransYOnly.isSelected();
    requestFocusInWindow();
  }

  /* create Controller frame */
  private JComboBox cmbImgFormat;

  JButton revertHomeButton;
  JButton revertXButton;
  JButton revertYButton;
  JButton revertSavedHomeButton;
  JButton saveViewPointButton;
  JPopupMenu revertSavedHomePopup;
  static final int MAX_SAVEDHOME=Viewpoint.nVPFile;
  JMenuItem[] menuSavedHome
    =new JMenuItem[MAX_SAVEDHOME];//instance array

  JButton rotUpButton;
  JButton rotDwnButton;
  JButton rotLeftButton;
  JButton rotRightButton;
  JButton rotClockWiseButton;
  JButton rotCounterClockWiseButton;
  JButton trnsUpButton;
  JButton trnsDownButton;
  JButton trnsRightButton;
  JButton trnsLeftButton;

  JButton trnsZupButton;
  JButton trnsZdownButton;

  JButton zoomInButton;
  JButton zoomOutButton;
  JButton startStopButton;
  JButton snapShotButton;
  JButton recordButton;


  Icon icnZmIn=
    new ImageIcon(this.getClass().getResource("/img/button/zoomin.png"));
  Icon icnZmOut=
    new ImageIcon(this.getClass().getResource("/img/button/zoomout.png"));
  Icon icnArrowUp=
    new ImageIcon(this.getClass().getResource("/img/button/arrow_up.png"));
  Icon icnArrowDown=
    new ImageIcon(this.getClass().getResource("/img/button/arrow_down.png"));
  Icon icnArrowRight=
    new ImageIcon(this.getClass().getResource("/img/button/arrow_right.png"));
  Icon icnArrowLeft=
    new ImageIcon(this.getClass().getResource("/img/button/arrow_left.png"));
  Icon icnArrowZup=
    new ImageIcon(this.getClass().getResource("/img/button/arrow_zup.png"));
  Icon icnArrowZdown=
    new ImageIcon(this.getClass().getResource("/img/button/arrow_zdown.png"));

  Icon icnArrowCW=
    new ImageIcon(this.getClass().getResource("/img/button/arrow_clockwise.png"));
  Icon icnArrowACW=
    new ImageIcon(this.getClass().getResource("/img/button/arrow_anticlockwise.png"));
  Icon icnHome=
    new ImageIcon(this.getClass().getResource("/img/button/home.png"));
  Icon icnHomeX=
    new ImageIcon(this.getClass().getResource("/img/button/homex.png"));
  Icon icnHomeY=
    new ImageIcon(this.getClass().getResource("/img/button/homey.png"));
  Icon icnDoc=
    new ImageIcon(this.getClass().getResource("/img/button/doc.png"));
  Icon icnDocSave=
    new ImageIcon(this.getClass().getResource("/img/button/doc_save.png"));
  Icon icnPlay=
    new ImageIcon(this.getClass().getResource("/img/button/play.png"));
  Icon icnStop=
    new ImageIcon(this.getClass().getResource("/img/button/stop.png"));
  Icon icnSnap=
    new ImageIcon(this.getClass().getResource("/img/button/snapshot.png"));
  Icon icnRecord=
    new ImageIcon(this.getClass().getResource("/img/button/record.png"));



  private JCheckBox cbRotationXOnly,cbRotationYOnly,cbTransXOnly,cbTransYOnly;


  JSpinner valInputSpinner;
  private void createPanel(){
    this.addKeyListener(ctrl.keyCtrl);
    //input field
    JLabel valLabel;

    valLabel = new JLabel("Control Value:");
    valLabel.setFocusable(false);


    valInputSpinner = new JSpinner(new SpinnerNumberModel(vconf.ControllerValue, 0.f, null, 10.f));
    valInputSpinner.setFocusable(false);

    valInputSpinner.setPreferredSize(new Dimension(70, 25));
    valInputSpinner.setFocusable(false);
    valInputSpinner.addChangeListener(this);


    revertHomeButton= new JButton(icnHome);
    revertHomeButton.setToolTipText("Revert to home position: h");
    revertHomeButton.setBorderPainted(true);
    revertHomeButton.addMouseListener(this);
    revertHomeButton.setFocusable(false);
    revertHomeButton.addActionListener( this );

    revertXButton= new JButton(icnHomeX);
    revertXButton.setToolTipText("Revert to home position: h");
    revertXButton.setBorderPainted(true);
    revertXButton.setFocusable(false);
    revertXButton.addActionListener( this );

    revertYButton= new JButton(icnHomeY);
    revertYButton.setToolTipText("Revert to home position: h");
    revertYButton.setBorderPainted(true);
    revertYButton.setFocusable(false);
    revertYButton.addActionListener( this );


    revertSavedHomeButton= new JButton(icnDoc);
    revertSavedHomeButton.setToolTipText("Revert to saved position: shift+h");
    revertSavedHomeButton.setBorderPainted(true);
    //revertSavedHomeButton.setBorder(null);
    revertSavedHomeButton.addMouseListener(this);
    revertSavedHomeButton.addActionListener( this );
    revertSavedHomeButton.setFocusable(false);

    saveViewPointButton= new JButton(icnDocSave);
    saveViewPointButton.setToolTipText("Save current position: alt+h");
    saveViewPointButton.setBorderPainted(true);
    //saveViewPointButton.setBorder(null);
    saveViewPointButton.addActionListener( this );
    saveViewPointButton.addMouseListener(this);
    saveViewPointButton.setFocusable(false);

    revertSavedHomePopup=new JPopupMenu();
    for(int i=0;i<MAX_SAVEDHOME;i++){
      menuSavedHome[i]=new JMenuItem(String.format("%d",i));
      menuSavedHome[i].addActionListener( this );
      revertSavedHomePopup.add(menuSavedHome[i]);
    }

    //animation-start/stop
    startStopButton= new JButton(icnPlay);
    startStopButton.setToolTipText("Start/Stop: s");
    startStopButton.setBorderPainted(true);
    //startStopButton.setBorder(null);
    startStopButton.addActionListener( this );
    startStopButton.setFocusable(false);

    //animation-record
    recordButton= new JButton(icnRecord);
    recordButton.setToolTipText("Record: shift+w");
    recordButton.setBorderPainted(true);
    //recordButton.setBorder(null);
    recordButton.addActionListener( this );
    recordButton.setFocusable(false);

    //snapshot
    snapShotButton= new JButton(icnSnap);
    snapShotButton.setToolTipText("Snapshot: w");
    snapShotButton.setBorderPainted(true);
    //snapShotButton.setBorder(null);
    snapShotButton.addActionListener( this );
    snapShotButton.setFocusable(false);

    //rotate-upward
    rotUpButton= new JButton(icnArrowUp);
    rotUpButton.setToolTipText("Rotate upward");
    rotUpButton.setBorderPainted(true);
    //rotUpButton.setBorder(null);
    rotUpButton.addActionListener( this );
    rotUpButton.setFocusable(false);

    //rotate-downward
    rotDwnButton= new JButton(icnArrowDown);
    rotDwnButton.setToolTipText("Rotate downward");
    rotDwnButton.setBorderPainted(true);
    //rotDwnButton.setBorder(null);
    rotDwnButton.addActionListener( this );
    rotDwnButton.setFocusable(false);

    //rotate-anti-clockwise
    rotCounterClockWiseButton= new JButton(icnArrowACW);
    rotCounterClockWiseButton.setToolTipText("Rotate anti-clockwise");
    rotCounterClockWiseButton.setBorderPainted(true);
    //rotCounterClockWiseButton.setBorder(null);
    rotCounterClockWiseButton.addActionListener( this );
    rotCounterClockWiseButton.setFocusable(false);

    //rotate-clockwise
    rotClockWiseButton= new JButton(icnArrowCW);
    rotClockWiseButton.setToolTipText("Rotate clockwise");
    rotClockWiseButton.setBorderPainted(true);
    //rotClockWiseButton.setBorder(null);
    rotClockWiseButton.addActionListener( this );
    rotClockWiseButton.setFocusable(false);

    //rotate-left
    rotLeftButton= new JButton(icnArrowLeft);
    rotLeftButton.setToolTipText("Rotate left");
    rotLeftButton.setBorderPainted(true);
    //rotLeftButton.setBorder(null);
    rotLeftButton.addActionListener( this );
    rotLeftButton.setFocusable(false);

    //rotate-right
    rotRightButton= new JButton(icnArrowRight);
    rotRightButton.setToolTipText("Rotate right");
    rotRightButton.setBorderPainted(true);
    //rotRightButton.setBorder(null);
    rotRightButton.addActionListener( this );
    rotRightButton.setFocusable(false);

    //translate-up
    trnsUpButton= new JButton(icnArrowUp);
    trnsUpButton.setToolTipText("Translation Upward");
    trnsUpButton.setBorderPainted(true);
    //trnsUpButton.setBorder(null);
    trnsUpButton.addActionListener( this );
    trnsUpButton.setFocusable(false);

    //translate-down
    trnsDownButton= new JButton(icnArrowDown);
    trnsDownButton.setToolTipText("Translation Downward");
    trnsDownButton.setBorderPainted(true);
    //trnsDownButton.setBorder(null);
    trnsDownButton.addActionListener( this );
    trnsDownButton.setFocusable(false);

    //translate-right
    trnsRightButton= new JButton(icnArrowRight);
    trnsRightButton.setToolTipText("Translation Right");
    trnsRightButton.setBorderPainted(true);
    //trnsRightButton.setBorder(null);
    trnsRightButton.addActionListener( this );
    trnsRightButton.setFocusable(false);

    //translate-left
    trnsLeftButton= new JButton(icnArrowLeft);
    trnsLeftButton.setToolTipText("Translation Left");
    trnsLeftButton.setBorderPainted(true);
    //trnsLeftButton.setBorder(null);
    trnsLeftButton.addActionListener( this );
    trnsLeftButton.setFocusable(false);

    // //translate-Zup
    trnsZupButton= new JButton(icnArrowZup);
    trnsZupButton.setToolTipText("Translation Left");
    trnsZupButton.setBorderPainted(true);
    //trnsZupButton.setBorder(null);
    trnsZupButton.addActionListener( this );
    trnsZupButton.setFocusable(false);
    //translate-Zdown
    trnsZdownButton= new JButton(icnArrowZdown);
    trnsZdownButton.setToolTipText("Translation Left");
    trnsZdownButton.setBorderPainted(true);
    //trnsZdownButton.setBorder(null);
    trnsZdownButton.addActionListener( this );
    trnsZdownButton.setFocusable(false);

    //zoom-in
    zoomInButton= new JButton(icnZmIn);
    zoomInButton.setToolTipText("Zoom In: z");
    zoomInButton.setBorderPainted(true);
    //zoomInButton.setBorder(null);
    zoomInButton.addActionListener( this );
    zoomInButton.setFocusable(false);

    //zoom-out
    zoomOutButton= new JButton(icnZmOut);
    zoomOutButton.setToolTipText("Zoom Out: shift+z");
    zoomOutButton.setBorderPainted(true);
    //zoomOutButton.setBorder(null);
    zoomOutButton.addActionListener( this );
    zoomOutButton.setFocusable(false);


  cbRotationXOnly =new JCheckBox("Rot. X Only",vconf.isRotationXOnly);
  cbRotationXOnly.setFocusable(false);
  cbRotationXOnly.addChangeListener(this);
  cbRotationYOnly =new JCheckBox("Rot. Y Only",vconf.isRotationYOnly);
  cbRotationYOnly.setFocusable(false);
  cbRotationYOnly.addChangeListener(this);

  cbTransXOnly =new JCheckBox("Trans. X Only",vconf.isTransXOnly);
  cbTransXOnly.setFocusable(false);
  cbTransXOnly.addChangeListener(this);
  cbTransYOnly =new JCheckBox("Trans. Y Only",vconf.isTransYOnly);
  cbTransYOnly.setFocusable(false);
  cbTransYOnly.addChangeListener(this);

    JLabel imgFormatLabel=new JLabel("Save image format");
    String[] strImgFormat = {"png", "jpg", "bmp"};
    cmbImgFormat = new JComboBox(strImgFormat);
    cmbImgFormat.setSelectedItem(vconf.imageFormat);
    cmbImgFormat.addActionListener(this);
    cmbImgFormat.setFocusable(false);


    JPanel valPanel= new JPanel();
    valPanel.setFocusable(false);

    SpringLayout layout = new SpringLayout();
    valPanel.setLayout(layout);
    layout.putConstraint(SpringLayout.NORTH, valInputSpinner, 0, SpringLayout.NORTH, valPanel);
    layout.putConstraint(SpringLayout.WEST,  valInputSpinner, 0, SpringLayout.EAST, valLabel);

    layout.putConstraint(SpringLayout.SOUTH, valLabel, -6, SpringLayout.SOUTH, valInputSpinner);
    layout.putConstraint(SpringLayout.WEST, valLabel, 6, SpringLayout.WEST, valPanel);

    layout.putConstraint(SpringLayout.NORTH, cbRotationXOnly, 5, SpringLayout.SOUTH, valInputSpinner);
    layout.putConstraint(SpringLayout.WEST,  cbRotationXOnly, 8, SpringLayout.WEST, valPanel);
    layout.putConstraint(SpringLayout.NORTH, cbRotationYOnly, 0, SpringLayout.SOUTH, cbRotationXOnly);
    layout.putConstraint(SpringLayout.WEST,  cbRotationYOnly, 0, SpringLayout.WEST, cbRotationXOnly);
    layout.putConstraint(SpringLayout.NORTH, cbTransXOnly, 0, SpringLayout.SOUTH, cbRotationYOnly);
    layout.putConstraint(SpringLayout.WEST,  cbTransXOnly, 0, SpringLayout.WEST, cbRotationYOnly);
    layout.putConstraint(SpringLayout.NORTH, cbTransYOnly , 0, SpringLayout.SOUTH, cbTransXOnly);
    layout.putConstraint(SpringLayout.WEST,  cbTransYOnly, 0, SpringLayout.WEST, cbTransXOnly);

    valPanel.add(valLabel);
    valPanel.add(valInputSpinner);
    valPanel.add(cbRotationXOnly);
    valPanel.add(cbRotationYOnly);
    valPanel.add(cbTransXOnly);
    valPanel.add(cbTransYOnly);


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
    double vg= 14;
    double hg= 5;
    //x direction
    double colSizeTL[]={vg,
                        170,
                        vg,
                        30,//rot 3
                        30,
                        30,
                        vg,
                        30,//trans 7
                        30,
                        30,
                        vg,
                        30,//zoom 11
                        30,
                        vg,
                        30,//home 14
                        30,
                        30,
                        vg,
                        30,//anime 18
                        30,
                        30
    };

    //y direction
    double rowSizeTL[]= {hg,15,30,30,30,f,hg};//each side has gap.

    setFocusable(false);
    setLayout(new TableLayout(colSizeTL,rowSizeTL));

    //1st col
    add(valPanel,       "1, 1, 1, 5");

    JLabel lRot= new JLabel("Rotation");
    lRot.setFocusable(false);
    add(lRot,                      "3, 2, 5, 2");
    add(rotCounterClockWiseButton, "3, 3, f, f");
    add(rotUpButton,               "4, 3, f, f");
    add(rotClockWiseButton,        "5, 3, f, f");
    add(rotLeftButton,             "3, 4, f, f");
    add(rotDwnButton,              "4, 4, f, f");
    add(rotRightButton,            "5, 4, f, f");

    JLabel lTrs= new JLabel("Translation");
    lTrs.setFocusable(false);
    add(lTrs,           "7, 2, 9, 2");
    add(trnsUpButton,   "8, 3, f, f");
    add(trnsDownButton, "8, 4, f, f");
    add(trnsLeftButton, "7, 4, f, f");
    add(trnsRightButton,"9, 4, f, f");

    JLabel lZoom= new JLabel("Zoom");
    add(lZoom,        "11, 2, 12, 2");
    add(zoomInButton, "11, 3, f, f");
    add(zoomOutButton,"12, 3, f, f");

    JLabel lHome= new JLabel("View Point");
    add(lHome,                 "14,2, 16, 2");
    add(revertHomeButton,      "14,3, f, f");
    add(revertSavedHomeButton, "15,3, f, f");
    add(saveViewPointButton,   "16,3, f, f");
    add(revertXButton,      "14,4, f, f");
    add(revertYButton,      "15,4, f, f");

    JLabel lAni= new JLabel("Anime");
    add(lAni,            "18,2, 20, 2");
    add(startStopButton, "18,3, f, f");
    add(snapShotButton,  "19,3, f, f");
    add(recordButton,    "20,3, f, f");
    add(cmbImgFormat,    "19,4, 20, 4");


  }

}

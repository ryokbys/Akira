package viewer.viewConfigPanel;

import java.io.*;
import java.util.*;
import javax.swing.table.*;
import javax.swing.border.LineBorder;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;

import tools.colorpicker.*;

import tools.*;
import viewer.*;
import data.*;
import viewer.renderer.*;

public class VolumeRenderPanel extends JPanel implements ActionListener{

  /* accesser */
  public void updateStatus(){
    RenderingWindow rw=ctrl.getActiveRW();
    if(rw==null)return;

    cmbVolExclude.setSelectedIndex(rw.renderingVolumeType);
    cmbVolData.setSelectedIndex(rw.renderingVolumeDataIndex);
  }
  /* accesser */

  private Controller ctrl;
  private ViewConfig vconf;
  //Maxructor
  public VolumeRenderPanel(Controller ctrl){
    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;
    createPanel();
    colorPicker = new ColorPicker(true,true);
  }



  // color picker
  private ColorPicker colorPicker;
  // JFrame is necessary to call the ColorPicker.showDialog
  private JFrame dummyFrame;


  private JCheckBox cbxy,cbxz,cbyz;
  private JCheckBox cbCutTiny;
  private JSpinner spDataX,spDataY,spDataZ;
  private JSpinner spDrawX,spDrawY,spDrawZ;
  private JButton setRangeButton;
  private JTextField taLegend,taFormat;

  private JButton btnApply;
  private JButton btn2Dplot;
  private JSpinner spDensFac;
  private JSpinner spContourPointSize;
  private JCheckBox cbContour;
  private JSpinner spContourPlaneNormalX,spContourPlaneNormalY,spContourPlaneNormalZ;
  private JSpinner spContourPlanePointX,spContourPlanePointY,spContourPlanePointZ;
  private JCheckBox cbContour2;
  private JSpinner spContour2PlaneNormalX,spContour2PlaneNormalY,spContour2PlaneNormalZ;
  private JSpinner spContour2PlanePointX,spContour2PlanePointY,spContour2PlanePointZ;

  //cut
  private JCheckBox cbLevel;
  private JSpinner spLevelMax,spLevelMin;
  private String[] strSurfaceType = {"Dot ", "MarchingCube", "MarchingCube2"};
  private JComboBox cmbSurfaceType;
  private JCheckBox cbSrf,cbSrf2;
  private JSpinner spSrfLevel,spSrfLevel2;
  private JSpinner spSrfNeighbors;
  private JSpinner spRangeMin,spRangeMax;
  private JButton resetButton;


  private String[] vtStr={"Volume Data Only","Including Atoms"};
  private JComboBox cmbVolExclude;
  private String[] vcStr={"Density","Data 1","Data 2","Data 3","Data 4","Data 5","Data 6","Data 7","Data 8","Data 9"};
  private JComboBox cmbVolData;


  public void createPanel(){
    this.addKeyListener(ctrl.keyCtrl);
    //General panel
    setFocusable( false );


    cmbVolExclude= new JComboBox(vtStr);
    cmbVolExclude.setSelectedIndex(0);
    cmbVolExclude.addActionListener(this);
    cmbVolData= new JComboBox(vcStr);
    cmbVolData.setSelectedIndex(0);
    cmbVolData.addActionListener(this);


    vconf.isVolXY=false;
    cbxy =new JCheckBox("XY",vconf.isVolXY);
    cbxy.setFocusable(false);
    vconf.isVolXZ=false;
    cbxz =new JCheckBox("XZ",vconf.isVolXZ);
    cbxz.setFocusable(false);
    vconf.isVolYZ=false;
    cbyz =new JCheckBox("YZ",vconf.isVolYZ);
    cbyz.setFocusable(false);


    JLabel lData = new JLabel( "Data Mesh" );
    spDataX = new JSpinner(new SpinnerNumberModel(vconf.volDataMesh[0], 1, null, 10));
    spDataX.setFocusable(false);
    spDataX.setPreferredSize(new Dimension(55, 25));
    spDataY = new JSpinner(new SpinnerNumberModel(vconf.volDataMesh[1], 1, null, 10));
    spDataY.setFocusable(false);
    spDataY.setPreferredSize(new Dimension(55, 25));
    spDataZ = new JSpinner(new SpinnerNumberModel(vconf.volDataMesh[2], 1, null, 10));
    spDataZ.setFocusable(false);
    spDataZ.setPreferredSize(new Dimension(55, 25));

    JLabel lDraw = new JLabel( "Draw Mesh" );
    spDrawX = new JSpinner(new SpinnerNumberModel(vconf.volDrawMesh[0], 1, null, 10));
    spDrawX.setFocusable(false);
    spDrawX.setPreferredSize(new Dimension(55, 25));
    spDrawY = new JSpinner(new SpinnerNumberModel(vconf.volDrawMesh[1], 1, null, 10));
    spDrawY.setFocusable(false);
    spDrawY.setPreferredSize(new Dimension(55, 25));
    spDrawZ = new JSpinner(new SpinnerNumberModel(vconf.volDrawMesh[2], 1, null, 10));
    spDrawZ.setFocusable(false);
    spDrawZ.setPreferredSize(new Dimension(55, 25));

    cmbSurfaceType = new JComboBox(strSurfaceType);
    cmbSurfaceType.addActionListener(this);
    cmbSurfaceType.setSelectedIndex(2);

    JLabel lSrfNeighbors = new JLabel( "Dot Resolution" );
    spSrfNeighbors = new JSpinner(new SpinnerNumberModel(vconf.volSurfaceNeighbors, 1, null, 1));
    spSrfNeighbors.setFocusable(false);
    spSrfNeighbors.setPreferredSize(new Dimension(55, 25));

    btnApply = new JButton( "Apply" );
    btnApply.setFocusable(false);
    btnApply.addActionListener( this );
    btn2Dplot = new JButton( "2D Plot" );
    btn2Dplot.setFocusable(false);
    btn2Dplot.addActionListener( this );
    resetButton  = new JButton( "Reset" );
    resetButton.setFocusable(false);
    resetButton.addActionListener( this );

    setRangeButton = new JButton("Load Original Range");
    setRangeButton.setToolTipText("Create");
    setRangeButton.addActionListener( this );
    setRangeButton.setFocusable(false);


    JLabel lLegend=new JLabel("Legend");
    taLegend = new JTextField(vconf.volLegend);
    taLegend.setEditable(true);
    JLabel lFormat=new JLabel("Format");
    taFormat = new JTextField(vconf.volColorTableFormat);
    taFormat.setEditable(true);
    taLegend.setPreferredSize(new Dimension(65, 25));
    taFormat.setPreferredSize(new Dimension(65, 25));



    JLabel lDensFac = new JLabel();
    lDensFac.setText( "Density Factor" );
    spDensFac = new JSpinner(new SpinnerNumberModel((double)vconf.volDensityFactor, 0.0, null, 0.1));
    spDensFac.setFocusable(false);
    spDensFac.setPreferredSize(new Dimension(70, 25));

    JLabel lContourPointSize = new JLabel( "Contour Point Size" );
    spContourPointSize = new JSpinner(new SpinnerNumberModel(vconf.volContourPointSize, 1, null, 5));
    spContourPointSize.setFocusable(false);
    spContourPointSize.setPreferredSize(new Dimension(55, 25));

    vconf.isVolContour=false;
    cbContour=new JCheckBox("Contour ",vconf.isVolContour);
    cbContour.setFocusable(false);
    //normal
    JLabel lContourPlaneNormal=new JLabel("Normal");
    spContourPlaneNormalX = new JSpinner(new SpinnerNumberModel((double)vconf.volContourPlaneNormal[0], null, null, 1));
    spContourPlaneNormalX.setFocusable(false);
    spContourPlaneNormalX.setPreferredSize(new Dimension(55, 25));
    spContourPlaneNormalY = new JSpinner(new SpinnerNumberModel((double)vconf.volContourPlaneNormal[1], null, null, 1));
    spContourPlaneNormalY.setFocusable(false);
    spContourPlaneNormalY.setPreferredSize(new Dimension(55, 25));
    spContourPlaneNormalZ = new JSpinner(new SpinnerNumberModel((double)vconf.volContourPlaneNormal[2], null, null, 1));
    spContourPlaneNormalZ.setFocusable(false);
    spContourPlaneNormalZ.setPreferredSize(new Dimension(55, 25));
    //point
    JLabel lContourPlanePoint=new JLabel("Point");
    spContourPlanePointX = new JSpinner(new SpinnerNumberModel((double)vconf.volContourPlanePoint[0], 0.0, 1.0, 0.1));
    spContourPlanePointX.setFocusable(false);
    spContourPlanePointX.setPreferredSize(new Dimension(55, 25));
    spContourPlanePointY = new JSpinner(new SpinnerNumberModel((double)vconf.volContourPlanePoint[1], 0.0, 1.0, 0.1));
    spContourPlanePointY.setFocusable(false);
    spContourPlanePointY.setPreferredSize(new Dimension(55, 25));
    spContourPlanePointZ = new JSpinner(new SpinnerNumberModel((double)vconf.volContourPlanePoint[2], 0.0, 1.0, 0.1));
    spContourPlanePointZ.setFocusable(false);
    spContourPlanePointZ.setPreferredSize(new Dimension(55, 25));


    //second
    vconf.isVolContour2=false;
    cbContour2=new JCheckBox("Contour2",vconf.isVolContour2);
    cbContour2.setFocusable(false);
    //normal
    JLabel lContour2PlaneNormal=new JLabel("Normal");
    spContour2PlaneNormalX = new JSpinner(new SpinnerNumberModel((double)vconf.volContour2PlaneNormal[0], null, null, 1));
    spContour2PlaneNormalX.setFocusable(false);
    spContour2PlaneNormalX.setPreferredSize(new Dimension(55, 25));
    spContour2PlaneNormalY = new JSpinner(new SpinnerNumberModel((double)vconf.volContour2PlaneNormal[1], null, null, 1));
    spContour2PlaneNormalY.setFocusable(false);
    spContour2PlaneNormalY.setPreferredSize(new Dimension(55, 25));
    spContour2PlaneNormalZ = new JSpinner(new SpinnerNumberModel((double)vconf.volContour2PlaneNormal[2], null, null, 1));
    spContour2PlaneNormalZ.setFocusable(false);
    spContour2PlaneNormalZ.setPreferredSize(new Dimension(55, 25));
    //point
    JLabel lContour2PlanePoint=new JLabel("Point");
    spContour2PlanePointX = new JSpinner(new SpinnerNumberModel((double)vconf.volContour2PlanePoint[0], 0.0, 1.0, 0.1));
    spContour2PlanePointX.setFocusable(false);
    spContour2PlanePointX.setPreferredSize(new Dimension(55, 25));
    spContour2PlanePointY = new JSpinner(new SpinnerNumberModel((double)vconf.volContour2PlanePoint[1], 0.0, 1.0, 0.1));
    spContour2PlanePointY.setFocusable(false);
    spContour2PlanePointY.setPreferredSize(new Dimension(55, 25));
    spContour2PlanePointZ = new JSpinner(new SpinnerNumberModel((double)vconf.volContour2PlanePoint[2], 0.0, 1.0, 0.1));
    spContour2PlanePointZ.setFocusable(false);
    spContour2PlanePointZ.setPreferredSize(new Dimension(55, 25));


    //cut
    vconf.isVolCut=false;
    cbLevel =new JCheckBox("Cut",vconf.isVolCut);
    cbLevel.setFocusable(false);
    JLabel lLevelMin = new JLabel();
    lLevelMin.setText( "Min" );
    JLabel lLevelMax = new JLabel();
    lLevelMax.setText( "Max" );

    cbCutTiny =new JCheckBox("Cut Tiny",vconf.isVolCutTiny);
    cbCutTiny.setFocusable(false);

    spLevelMin = new JSpinner(new SpinnerNumberModel((double)vconf.volCutLevel[0], 0.0, 1.0, 0.05));
    spLevelMin.setFocusable(false);
    spLevelMin.setPreferredSize(new Dimension(55, 30));

    spLevelMax = new JSpinner(new SpinnerNumberModel((double)vconf.volCutLevel[1], 0.0, 1.0, 0.05));
    spLevelMax.setFocusable(false);
    spLevelMax.setPreferredSize(new Dimension(55, 30));

    vconf.isVolSurface=false;
    cbSrf=new JCheckBox("Isosurface ",vconf.isVolSurface);
    cbSrf.setFocusable(false);
    vconf.isVolSurface2=false;
    cbSrf2=new JCheckBox("Isosurface2",vconf.isVolSurface2);
    cbSrf2.setFocusable(false);
    JLabel lSrf = new JLabel( "Level" );
    JLabel lSrf2 = new JLabel( "Level" );
    spSrfLevel = new JSpinner(new SpinnerNumberModel((double)vconf.volSurfaceLevel, 0.0, 1.0, 0.05));
    spSrfLevel.setFocusable(false);
    spSrfLevel.setPreferredSize(new Dimension(65, 25));
    spSrfLevel2 = new JSpinner(new SpinnerNumberModel((double)vconf.volSurfaceLevel2, 0.0, 1.0, 0.05));
    spSrfLevel2.setFocusable(false);
    spSrfLevel2.setPreferredSize(new Dimension(65, 25));

    JLabel lRange=new JLabel("Range");
    spRangeMin = new JSpinner(new SpinnerNumberModel((double)vconf.volRange[0], null,null, 0.05));
    spRangeMin.setFocusable(false);
    spRangeMin.setPreferredSize(new Dimension(110, 25));
    spRangeMin.setEditor(new JSpinner.NumberEditor(spRangeMin, "0.####E0"));
    spRangeMax = new JSpinner(new SpinnerNumberModel((double)vconf.volRange[1], null, null, 0.05));
    spRangeMax.setFocusable(false);
    spRangeMax.setPreferredSize(new Dimension(110, 25));
    spRangeMax.setEditor(new JSpinner.NumberEditor(spRangeMax, "0.####E0"));


    SpringLayout layout = new SpringLayout();
    setLayout(layout);

    //surface
    layout.putConstraint(SpringLayout.NORTH, cbSrf, 10, SpringLayout.NORTH,this);
    layout.putConstraint(SpringLayout.WEST, cbSrf, 10, SpringLayout.WEST, this);
    layout.putConstraint(SpringLayout.NORTH, lSrf, 0, SpringLayout.NORTH, cbSrf);
    layout.putConstraint(SpringLayout.WEST, lSrf, 5, SpringLayout.EAST, cbSrf);
    layout.putConstraint(SpringLayout.NORTH, spSrfLevel, 0, SpringLayout.NORTH, lSrf);
    layout.putConstraint(SpringLayout.WEST, spSrfLevel, 3, SpringLayout.EAST, lSrf);
    //contour1
    layout.putConstraint(SpringLayout.NORTH, cbContour, 15, SpringLayout.SOUTH, cbSrf);
    layout.putConstraint(SpringLayout.WEST, cbContour, 0, SpringLayout.WEST, cbSrf);
    layout.putConstraint(SpringLayout.NORTH, lContourPlaneNormal, -5, SpringLayout.NORTH, cbContour);
    layout.putConstraint(SpringLayout.WEST, lContourPlaneNormal, 5,SpringLayout.EAST, cbContour);
    layout.putConstraint(SpringLayout.NORTH, spContourPlaneNormalX, 0,SpringLayout.NORTH, lContourPlaneNormal);
    layout.putConstraint(SpringLayout.WEST, spContourPlaneNormalX, 5,SpringLayout.EAST, lContourPlaneNormal);
    layout.putConstraint(SpringLayout.NORTH, spContourPlaneNormalY, 0,SpringLayout.NORTH, spContourPlaneNormalX);
    layout.putConstraint(SpringLayout.WEST, spContourPlaneNormalY, 5,SpringLayout.EAST, spContourPlaneNormalX);
    layout.putConstraint(SpringLayout.NORTH, spContourPlaneNormalZ, 0,SpringLayout.NORTH, spContourPlaneNormalY);
    layout.putConstraint(SpringLayout.WEST, spContourPlaneNormalZ, 5,SpringLayout.EAST, spContourPlaneNormalY);
    layout.putConstraint(SpringLayout.NORTH, lContourPlanePoint, 10,SpringLayout.SOUTH, lContourPlaneNormal);
    layout.putConstraint(SpringLayout.WEST, lContourPlanePoint, 0,SpringLayout.WEST, lContourPlaneNormal);
    layout.putConstraint(SpringLayout.NORTH, spContourPlanePointX, 0,SpringLayout.NORTH, lContourPlanePoint);
    layout.putConstraint(SpringLayout.WEST, spContourPlanePointX, 0,SpringLayout.WEST, spContourPlaneNormalX);
    layout.putConstraint(SpringLayout.NORTH, spContourPlanePointY, 0,SpringLayout.NORTH, spContourPlanePointX);
    layout.putConstraint(SpringLayout.WEST, spContourPlanePointY, 5,SpringLayout.EAST, spContourPlanePointX);
    layout.putConstraint(SpringLayout.NORTH, spContourPlanePointZ, 0,SpringLayout.NORTH, spContourPlanePointY);
    layout.putConstraint(SpringLayout.WEST, spContourPlanePointZ, 5,SpringLayout.EAST, spContourPlanePointY);

    //
    JLabel volDataLabel=new JLabel("Data");
    layout.putConstraint( SpringLayout.NORTH, volDataLabel, 10, SpringLayout.NORTH, this);
    layout.putConstraint( SpringLayout.WEST,  volDataLabel, 10, SpringLayout.EAST, spContourPlanePointZ);
    layout.putConstraint( SpringLayout.NORTH, cmbVolData, 0, SpringLayout.NORTH, volDataLabel);
    layout.putConstraint( SpringLayout.WEST,  cmbVolData, 5, SpringLayout.EAST, volDataLabel);

    //range
    layout.putConstraint(SpringLayout.NORTH, lRange, 0, SpringLayout.NORTH,volDataLabel);
    layout.putConstraint(SpringLayout.WEST, lRange, 10, SpringLayout.EAST, cmbVolData);
    layout.putConstraint(SpringLayout.NORTH, spRangeMin, 0, SpringLayout.NORTH, lRange);
    layout.putConstraint(SpringLayout.WEST, spRangeMin, 0, SpringLayout.EAST, lRange);
    layout.putConstraint(SpringLayout.NORTH, spRangeMax, 0, SpringLayout.NORTH, spRangeMin);
    layout.putConstraint(SpringLayout.WEST, spRangeMax, 0, SpringLayout.EAST, spRangeMin);
    layout.putConstraint(SpringLayout.NORTH, setRangeButton, 0, SpringLayout.SOUTH, spRangeMax);
    layout.putConstraint(SpringLayout.EAST, setRangeButton, 0, SpringLayout.EAST, spRangeMax);
    //legend
    layout.putConstraint( SpringLayout.NORTH, lLegend, 5,SpringLayout.SOUTH, setRangeButton);
    layout.putConstraint( SpringLayout.WEST, lLegend, 0,SpringLayout.WEST, volDataLabel);
    layout.putConstraint( SpringLayout.NORTH, taLegend, 0,SpringLayout.NORTH, lLegend);
    layout.putConstraint( SpringLayout.WEST, taLegend, 0,SpringLayout.EAST, lLegend);
    layout.putConstraint( SpringLayout.NORTH, lFormat, 0,SpringLayout.NORTH, taLegend);
    layout.putConstraint( SpringLayout.WEST, lFormat, 0,SpringLayout.EAST, taLegend);
    layout.putConstraint( SpringLayout.NORTH, taFormat, 0,SpringLayout.NORTH, lFormat);
    layout.putConstraint( SpringLayout.WEST, taFormat, 0,SpringLayout.EAST, lFormat);

    //apply button
    layout.putConstraint(SpringLayout.NORTH, btnApply, 10, SpringLayout.SOUTH, taFormat);
    layout.putConstraint(SpringLayout.WEST, btnApply, 0, SpringLayout.WEST, volDataLabel);
    layout.putConstraint( SpringLayout.SOUTH, resetButton, 0,SpringLayout.SOUTH, btnApply);
    layout.putConstraint( SpringLayout.WEST, resetButton, 0,SpringLayout.EAST, btnApply);
    layout.putConstraint(SpringLayout.SOUTH, btn2Dplot, 0, SpringLayout.SOUTH, resetButton);
    layout.putConstraint(SpringLayout.WEST, btn2Dplot, 0, SpringLayout.EAST, resetButton);


    //data mesh
    layout.putConstraint(SpringLayout.NORTH, lData, 10, SpringLayout.NORTH, this);
    layout.putConstraint(SpringLayout.WEST, lData, 20, SpringLayout.EAST, spRangeMax);
    layout.putConstraint(SpringLayout.NORTH, spDataX, 0, SpringLayout.NORTH, lData);
    layout.putConstraint(SpringLayout.WEST, spDataX, 5, SpringLayout.EAST, lData);
    layout.putConstraint(SpringLayout.NORTH, spDataY, 0, SpringLayout.NORTH, spDataX);
    layout.putConstraint(SpringLayout.WEST, spDataY, 2, SpringLayout.EAST, spDataX);
    layout.putConstraint(SpringLayout.NORTH, spDataZ, 0, SpringLayout.NORTH, spDataY);
    layout.putConstraint(SpringLayout.WEST, spDataZ, 2, SpringLayout.EAST, spDataY);
    //draw mesh
    layout.putConstraint(SpringLayout.NORTH, lDraw, 25, SpringLayout.NORTH, lData);
    layout.putConstraint(SpringLayout.WEST, lDraw, 0, SpringLayout.WEST, lData);
    layout.putConstraint(SpringLayout.NORTH, spDrawX, 0, SpringLayout.NORTH, lDraw);
    layout.putConstraint(SpringLayout.WEST, spDrawX, 2, SpringLayout.EAST, lDraw);
    layout.putConstraint(SpringLayout.NORTH, spDrawY, 0, SpringLayout.NORTH, spDrawX);
    layout.putConstraint(SpringLayout.WEST, spDrawY, 2, SpringLayout.EAST, spDrawX);
    layout.putConstraint(SpringLayout.NORTH, spDrawZ, 0, SpringLayout.NORTH, spDrawY);
    layout.putConstraint(SpringLayout.WEST, spDrawZ, 2, SpringLayout.EAST, spDrawY);

    //contour
    layout.putConstraint(SpringLayout.NORTH, lContourPointSize, 5, SpringLayout.SOUTH, spDrawZ);
    layout.putConstraint(SpringLayout.WEST, lContourPointSize, 0, SpringLayout.WEST, lDraw);
    layout.putConstraint(SpringLayout.NORTH, spContourPointSize, 0, SpringLayout.NORTH, lContourPointSize);
    layout.putConstraint(SpringLayout.WEST, spContourPointSize, 5, SpringLayout.EAST, lContourPointSize);

    //level cut
    layout.putConstraint(SpringLayout.NORTH, cbLevel, 5, SpringLayout.SOUTH, spContourPointSize);
    layout.putConstraint(SpringLayout.WEST, cbLevel, 0, SpringLayout.WEST, lContourPointSize);
    //cut tiny
    layout.putConstraint(SpringLayout.NORTH, cbCutTiny, 0, SpringLayout.NORTH, cbLevel);
    layout.putConstraint(SpringLayout.WEST, cbCutTiny, 0, SpringLayout.EAST, cbLevel);

    //level
    layout.putConstraint(SpringLayout.NORTH, lLevelMin, 0, SpringLayout.SOUTH, cbLevel);
    layout.putConstraint(SpringLayout.WEST, lLevelMin, 10, SpringLayout.WEST, cbLevel);
    layout.putConstraint(SpringLayout.NORTH, spLevelMin, 2, SpringLayout.NORTH, lLevelMin);
    layout.putConstraint(SpringLayout.WEST, spLevelMin, 0, SpringLayout.EAST, lLevelMin);
    layout.putConstraint(SpringLayout.NORTH, lLevelMax, 0, SpringLayout.NORTH, spLevelMin);
    layout.putConstraint(SpringLayout.WEST, lLevelMax, 10, SpringLayout.EAST, spLevelMin);
    layout.putConstraint(SpringLayout.NORTH, spLevelMax, 0, SpringLayout.NORTH, lLevelMax);
    layout.putConstraint(SpringLayout.WEST, spLevelMax, 0, SpringLayout.EAST, lLevelMax);


    //////enjoy mode
    //xy, yz, zx
    layout.putConstraint(SpringLayout.NORTH, cbxy, 0, SpringLayout.NORTH,spSrfLevel);
    layout.putConstraint(SpringLayout.WEST, cbxy, 0, SpringLayout.EAST, spSrfLevel);
    layout.putConstraint(SpringLayout.NORTH, cbxz, 0, SpringLayout.NORTH, cbxy);
    layout.putConstraint(SpringLayout.WEST, cbxz, 0, SpringLayout.EAST, cbxy);
    layout.putConstraint(SpringLayout.NORTH, cbyz, 0, SpringLayout.NORTH, cbxz);
    layout.putConstraint(SpringLayout.WEST, cbyz, 0, SpringLayout.EAST, cbxz);
    //dot
    layout.putConstraint(SpringLayout.NORTH, lSrfNeighbors, 10, SpringLayout.NORTH, this);
    layout.putConstraint(SpringLayout.WEST, lSrfNeighbors, 10, SpringLayout.EAST, spDataZ);
    layout.putConstraint(SpringLayout.NORTH, spSrfNeighbors, 0, SpringLayout.NORTH, lSrfNeighbors);
    layout.putConstraint(SpringLayout.WEST, spSrfNeighbors, 0, SpringLayout.EAST, lSrfNeighbors);
    //dens
    layout.putConstraint(SpringLayout.NORTH, lDensFac, 10, SpringLayout.SOUTH, lSrfNeighbors);
    layout.putConstraint(SpringLayout.WEST, lDensFac, 0, SpringLayout.WEST, lSrfNeighbors);
    layout.putConstraint(SpringLayout.NORTH, spDensFac, 0, SpringLayout.NORTH, lDensFac);
    layout.putConstraint(SpringLayout.WEST, spDensFac, 0, SpringLayout.EAST, lDensFac);
    //surface type
    layout.putConstraint(SpringLayout.NORTH, cmbSurfaceType, 10, SpringLayout.SOUTH, spDensFac);
    layout.putConstraint(SpringLayout.WEST, cmbSurfaceType, 0, SpringLayout.WEST, lDensFac);

    JLabel cmbVolExcludeLabel=new JLabel("Data Type");
    layout.putConstraint( SpringLayout.NORTH, cmbVolExcludeLabel, 10, SpringLayout.SOUTH, cmbSurfaceType);
    layout.putConstraint( SpringLayout.WEST,  cmbVolExcludeLabel, 0, SpringLayout.WEST, cmbSurfaceType);
    layout.putConstraint( SpringLayout.NORTH, cmbVolExclude, 0, SpringLayout.SOUTH, cmbVolExcludeLabel);
    layout.putConstraint( SpringLayout.WEST,  cmbVolExclude, 10, SpringLayout.WEST, cmbVolExcludeLabel);





    //surface2
    layout.putConstraint(SpringLayout.NORTH, cbSrf2, 5, SpringLayout.SOUTH, spContourPlanePointZ);
    layout.putConstraint(SpringLayout.WEST, cbSrf2, 0, SpringLayout.WEST, cbContour);
    layout.putConstraint(SpringLayout.NORTH, lSrf2, 0, SpringLayout.NORTH, cbSrf2);
    layout.putConstraint(SpringLayout.WEST, lSrf2, 5, SpringLayout.EAST, cbSrf2);
    layout.putConstraint(SpringLayout.NORTH, spSrfLevel2, 0, SpringLayout.NORTH, cbSrf2);
    layout.putConstraint(SpringLayout.WEST, spSrfLevel2, 3, SpringLayout.EAST, lSrf);
    //contour2
    layout.putConstraint(SpringLayout.NORTH, cbContour2, 5, SpringLayout.SOUTH, spSrfLevel2);
    layout.putConstraint(SpringLayout.WEST, cbContour2, 0, SpringLayout.WEST, cbSrf2);
    layout.putConstraint(SpringLayout.NORTH, lContour2PlaneNormal, -5, SpringLayout.NORTH, cbContour2);
    layout.putConstraint(SpringLayout.WEST, lContour2PlaneNormal, 5,SpringLayout.EAST, cbContour2);
    layout.putConstraint(SpringLayout.NORTH, spContour2PlaneNormalX, 0,SpringLayout.NORTH, lContour2PlaneNormal);
    layout.putConstraint(SpringLayout.WEST, spContour2PlaneNormalX, 5,SpringLayout.EAST, lContour2PlaneNormal);
    layout.putConstraint(SpringLayout.NORTH, spContour2PlaneNormalY, 0,SpringLayout.NORTH, spContour2PlaneNormalX);
    layout.putConstraint(SpringLayout.WEST, spContour2PlaneNormalY, 5,SpringLayout.EAST, spContour2PlaneNormalX);
    layout.putConstraint(SpringLayout.NORTH, spContour2PlaneNormalZ, 0,SpringLayout.NORTH, spContour2PlaneNormalY);
    layout.putConstraint(SpringLayout.WEST, spContour2PlaneNormalZ, 5,SpringLayout.EAST, spContour2PlaneNormalY);
    layout.putConstraint(SpringLayout.NORTH, lContour2PlanePoint, 10,SpringLayout.SOUTH, lContour2PlaneNormal);
    layout.putConstraint(SpringLayout.WEST, lContour2PlanePoint, 0,SpringLayout.WEST, lContour2PlaneNormal);
    layout.putConstraint(SpringLayout.NORTH, spContour2PlanePointX, 0,SpringLayout.NORTH, lContour2PlanePoint);
    layout.putConstraint(SpringLayout.WEST, spContour2PlanePointX, 0,SpringLayout.WEST, spContour2PlaneNormalX);
    layout.putConstraint(SpringLayout.NORTH, spContour2PlanePointY, 0,SpringLayout.NORTH, spContour2PlanePointX);
    layout.putConstraint(SpringLayout.WEST, spContour2PlanePointY, 5,SpringLayout.EAST, spContour2PlanePointX);
    layout.putConstraint(SpringLayout.NORTH, spContour2PlanePointZ, 0,SpringLayout.NORTH, spContour2PlanePointY);
    layout.putConstraint(SpringLayout.WEST, spContour2PlanePointZ, 5,SpringLayout.EAST, spContour2PlanePointY);


    //add
    add(cmbVolData);
    add(volDataLabel);

    add(lRange);
    add(spRangeMin);
    add(spRangeMax);
    add(setRangeButton);


    add(btnApply);
    add(resetButton);
    add(btn2Dplot);


    add(cbContour);
    add(lContourPlaneNormal);
    add(spContourPlaneNormalX);
    add(spContourPlaneNormalY);
    add(spContourPlaneNormalZ);
    add(lContourPlanePoint);
    add(spContourPlanePointX);
    add(spContourPlanePointY);
    add(spContourPlanePointZ);


    add(lContourPointSize);
    add(spContourPointSize);

    add(cbSrf);
    add(lSrf);
    add(spSrfLevel);



    add(lData);
    add(spDataX);
    add(spDataY);
    add(spDataZ);
    add(lDraw);
    add(spDrawX);
    add(spDrawY);
    add(spDrawZ);

    add(lFormat);
    add(lLegend);
    add(taFormat);
    add(taLegend);
    if(ctrl.isEnjoyMode){
      add(cbCutTiny);
      add(cbxy);
      add(cbxz);
      add(cbyz);

      add(cmbVolExcludeLabel);
      add(cmbVolExclude);

      add(cbLevel);
      add(lLevelMin);
      add(spLevelMin);
      add(lLevelMax);
      add(spLevelMax);

      add(cbContour2);
      add(lContour2PlaneNormal);
      add(spContour2PlaneNormalX);
      add(spContour2PlaneNormalY);
      add(spContour2PlaneNormalZ);
      add(lContour2PlanePoint);
      add(spContour2PlanePointX);
      add(spContour2PlanePointY);
      add(spContour2PlanePointZ);

      add(cmbSurfaceType);
      add(cbSrf2);
      add(lSrf2);
      add(spSrfLevel2);
      add(lSrfNeighbors);
      add(spSrfNeighbors);
      add(lDensFac);
      add(spDensFac);
    }
  }




  private void getValue(){
    vconf.isVolXY=cbxy.isSelected();
    vconf.isVolXZ=cbxz.isSelected();
    vconf.isVolYZ=cbyz.isSelected();
    vconf.isVolContour=cbContour.isSelected();
    vconf.isVolCut=cbLevel.isSelected();
    vconf.isVolSurface=cbSrf.isSelected();
    vconf.isVolSurface2=cbSrf2.isSelected();
    vconf.volSurfaceRenderType=cmbSurfaceType.getSelectedIndex();
    vconf.isVolCutTiny=cbCutTiny.isSelected();
    vconf.volDataMesh[0]=((Integer)spDataX.getValue()).intValue();
    vconf.volDataMesh[1]=((Integer)spDataY.getValue()).intValue();
    vconf.volDataMesh[2]=((Integer)spDataZ.getValue()).intValue();
    vconf.volDrawMesh[0]=((Integer)spDrawX.getValue()).intValue();
    vconf.volDrawMesh[1]=((Integer)spDrawY.getValue()).intValue();
    vconf.volDrawMesh[2]=((Integer)spDrawZ.getValue()).intValue();
    vconf.volDensityFactor=((Double)spDensFac.getValue()).floatValue();
    vconf.volContourPlaneNormal[0]=((Double)spContourPlaneNormalX.getValue()).floatValue();
    vconf.volContourPlaneNormal[1]=((Double)spContourPlaneNormalY.getValue()).floatValue();
    vconf.volContourPlaneNormal[2]=((Double)spContourPlaneNormalZ.getValue()).floatValue();
    vconf.volContourPlanePoint[0]=((Double)spContourPlanePointX.getValue()).floatValue();
    vconf.volContourPlanePoint[1]=((Double)spContourPlanePointY.getValue()).floatValue();
    vconf.volContourPlanePoint[2]=((Double)spContourPlanePointZ.getValue()).floatValue();
    vconf.volContour2PlaneNormal[0]=((Double)spContour2PlaneNormalX.getValue()).floatValue();
    vconf.volContour2PlaneNormal[1]=((Double)spContour2PlaneNormalY.getValue()).floatValue();
    vconf.volContour2PlaneNormal[2]=((Double)spContour2PlaneNormalZ.getValue()).floatValue();
    vconf.volContour2PlanePoint[0]=((Double)spContour2PlanePointX.getValue()).floatValue();
    vconf.volContour2PlanePoint[1]=((Double)spContour2PlanePointY.getValue()).floatValue();
    vconf.volContour2PlanePoint[2]=((Double)spContour2PlanePointZ.getValue()).floatValue();

    vconf.volContourPointSize=((Integer)spContourPointSize.getValue()).intValue();
    vconf.volCutLevel[0]=((Double)spLevelMin.getValue()).floatValue();
    vconf.volCutLevel[1]=((Double)spLevelMax.getValue()).floatValue();
    vconf.volSurfaceLevel=((Double)spSrfLevel.getValue()).floatValue();
    vconf.volSurfaceLevel2=((Double)spSrfLevel2.getValue()).floatValue();
    vconf.volSurfaceNeighbors=((Integer)spSrfNeighbors.getValue()).intValue();
    vconf.volRange[0]=((Double)spRangeMin.getValue()).floatValue();
    vconf.volRange[1]=((Double)spRangeMax.getValue()).floatValue();


    vconf.volLegend=taLegend.getText();
    vconf.volColorTableFormat=taFormat.getText();
  }

  private void setValue(){
    cbxy.setSelected(vconf.isVolXY);
    cbxz.setSelected(vconf.isVolXZ);
    cbyz.setSelected(vconf.isVolYZ);
    cbContour.setSelected(vconf.isVolContour);
    cbLevel.setSelected(vconf.isVolCut);
    cbSrf.setSelected(vconf.isVolSurface);
    cbSrf2.setSelected(vconf.isVolSurface2);
    cmbSurfaceType.setSelectedIndex(vconf.volSurfaceRenderType);
    cbCutTiny.setSelected(vconf.isVolCutTiny);

    spDataX.setValue(vconf.volDataMesh[0]);
    spDataY.setValue(vconf.volDataMesh[1]);
    spDataZ.setValue(vconf.volDataMesh[2]);
    spDrawX.setValue(vconf.volDrawMesh[0]);
    spDrawY.setValue(vconf.volDrawMesh[1]);
    spDrawZ.setValue(vconf.volDrawMesh[2]);
    spDensFac.setValue((double)vconf.volDensityFactor);

    spContourPlaneNormalX.setValue((double)vconf.volContourPlaneNormal[0]);
    spContourPlaneNormalY.setValue((double)vconf.volContourPlaneNormal[1]);
    spContourPlaneNormalZ.setValue((double)vconf.volContourPlaneNormal[2]);
    spContourPlanePointX.setValue((double)vconf.volContourPlanePoint[0]);
    spContourPlanePointY.setValue((double)vconf.volContourPlanePoint[1]);
    spContourPlanePointZ.setValue((double)vconf.volContourPlanePoint[2]);
    spContour2PlaneNormalX.setValue((double)vconf.volContour2PlaneNormal[0]);
    spContour2PlaneNormalY.setValue((double)vconf.volContour2PlaneNormal[1]);
    spContour2PlaneNormalZ.setValue((double)vconf.volContour2PlaneNormal[2]);
    spContour2PlanePointX.setValue((double)vconf.volContour2PlanePoint[0]);
    spContour2PlanePointY.setValue((double)vconf.volContour2PlanePoint[1]);
    spContour2PlanePointZ.setValue((double)vconf.volContour2PlanePoint[2]);

    spContourPointSize.setValue(vconf.volContourPointSize);


    spLevelMin.setValue((double)vconf.volCutLevel[0]);
    spLevelMax.setValue((double)vconf.volCutLevel[1]);
    spSrfLevel.setValue((double)vconf.volSurfaceLevel);
    spSrfLevel2.setValue((double)vconf.volSurfaceLevel2);

    spSrfNeighbors.setValue(vconf.volSurfaceNeighbors);


    spRangeMin.setValue((double)vconf.volRange[0]);
    spRangeMax.setValue((double)vconf.volRange[1]);

    taLegend.setText(vconf.volLegend);
    taFormat.setText(vconf.volColorTableFormat);
  }


  public void actionPerformed( ActionEvent ae){

    if( ae.getSource() == btnApply){
      getValue();
      RenderingWindow rw=ctrl.getActiveRW();
      if(rw==null)return;
      rw.renderingVolumeType=cmbVolExclude.getSelectedIndex();
      rw.renderingVolumeDataIndex=cmbVolData.getSelectedIndex();
      ctrl.RWinRefresh();
    }else if(ae.getSource() == resetButton){
      vconf.resetVolume();
      setValue();
    }else if( ae.getSource() == setRangeButton){
      int i=(ctrl.getActiveRW()).renderingVolumeDataIndex-1;
      vconf.volRange[0]=(ctrl.getActiveRW()).atoms.originalDataRange[i][0];
      vconf.volRange[1]=(ctrl.getActiveRW()).atoms.originalDataRange[i][1];
      spRangeMin.setValue((double)vconf.volRange[0]);
      spRangeMax.setValue((double)vconf.volRange[1]);
    }else if( ae.getSource() == btn2Dplot){
      ctrl.setVisible2Dplot();
    }

  }

}

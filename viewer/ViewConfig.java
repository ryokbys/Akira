package viewer;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.text.*;

import data.*;
import viewer.renderer.*;

public class ViewConfig implements Serializable{
  /*
   * AKIRAView configuration
   *  this class has;
   *    main config. and its read/write methods
   *    window pos. file and its read/write methods
   */

  public String configDir;
  public String configFile;

  public String pluginDir;

  //status
  public boolean isPopStatus=false;
  //manipulation
  public float   ControllerValue;
  public int     viewMode;
  public boolean isRotationXOnly;
  public boolean isRotationYOnly;
  public boolean isTransXOnly;
  public boolean isTransYOnly;
  public boolean isTrjMode;

  //misc
  public Font   annotationFont  = new Font("courier",Font.BOLD,18);
  public float  bgColor[]       = new float[4];
  public float  txtColor[]      = new float[4];
  public float  txtPos[]        = new float[2];
  public float  boxColor[]      = new float[4];
  public float  boxLineWidth;
  public float  startTime;
  public float  timeInterval;
  public String timePrintFormat;
  public String imageFormat;

  //atom
  public String  tagName[]               = new String[Const.TAG];
  public boolean tagOnOff[]              = new boolean[Const.TAG];
  public double  tagRadius[]             = new double[Const.TAG];
  public int     tagSlice[]              = new int[Const.TAG];
  public int     tagStack[]              = new int[Const.TAG];
  public float   tagColor[][]            = new float[Const.TAG][4];
  public float   atomSpecular[]          = new float[4];
  public float   atomAmb[]               = new float[4];
  public float   atomEmmission[]         = new float[4];
  public int     atomShineness;
  public boolean isSelectionInfo;
  public boolean isSelectionLength;
  public boolean isSelectionAngle;
  public boolean isSelectionTorsion;
  public boolean isDeletionMode;
  public boolean isDragMoveMode;
  public int moveDirection=0;
  public float moveVal=0.1f;

  //data
  public float  dataRange[][]  = new float[Const.DATA][2];
  public float  dataCutRange[] = new float[2];
  public float  dataFactor=1.f;
  public String dataLegend[]   = new String[Const.DATA];
  public String dataFormat[]   = new String[Const.DATA];

  //bond
  public float   bondRadius;
  public int     bondSlice;
  public float[] bondLengthRange = new float[2];
  public float[] bondCNRange     = new float[2];
  public String  bondLegend;
  public String  bondColorTableFormat;

  //vector
  public float  vecConeRadius;
  public float  vecConeHeight;
  public float  vecLengthRatio;
  public float  vecCylinderRadius;
  public int    vecCylinderSlice;
  public int    vectorX;
  public int    vectorY;
  public int    vectorZ;
  public float[] vecLengthRange = new float[2];
  public String vecLegend;
  public String vecColorTableFormat;

  //boundary
  public float   extendRenderingFactor[][] = new float[2][3];
  public boolean isOnSlicer[]              = new boolean[Const.PLANE];
  public float   normalVecSlicer[][]       = new float[Const.PLANE][3];
  public float   posVecSlicer[][]          = new float[Const.PLANE][3];
  public float   spherecutPos[]            = new float[3];
  public float   spherecutRadius;
  public boolean isSphereCut;
  public boolean isRegionSelectMode;
  public boolean isRectangleSelectMode;

  //volume rendering
  public int     volDataMesh[]          = new int[3];
  public int     volDrawMesh[]          = new int[3];
  public int     volContourPointSize;
  public boolean isVolCut;
  public float   volCutLevel[]          = new float[2];
  public float   volDensityFactor;
  public int     volSurfaceRenderType;
  public float   volSurfaceLevel;
  public float   volSurfaceLevel2;
  public int     volSurfaceNeighbors;
  public boolean isVolXY;
  public boolean isVolXZ;
  public boolean isVolYZ;
  public boolean isVolContour;
  public float[] volContourPlaneNormal  = new float[3];
  public float[] volContourPlanePoint   = new float[3];
  public boolean isVolContour2;
  public float[] volContour2PlaneNormal = new float[3];
  public float[] volContour2PlanePoint  = new float[3];
  public boolean isVolSurface;
  public boolean isVolSurface2;
  public boolean isVolCutTiny;
  public float[] volRange               = new float[2];
  public String  volLegend;
  public String  volColorTableFormat;


  //plane
  public boolean isSelectionPlaneMode=false;
  public boolean isVoronoiMode;
  public boolean isDelaunayMode;
  public boolean isPlaneVisible[] = new boolean[Const.PLANE];
  public float   planeNormal[][]  = new float[Const.PLANE][3];
  public float   planePoint[][]   = new float[Const.PLANE][3];
  public float   planeColor[][]   = new float[Const.PLANE][4];
  public boolean isTetrahedronMode=false;
  public int tetrahedronCenter=1;
  public float planeRcut=4.f;
  public float   singlePlaneColor[]   = new float[4];

  //light
  public boolean isLightPosAuto;
  public float lightPos[]   = new float[4];
  public float lightDif[]   = new float[4];
  public float lightAmb[]   = new float[4];
  public float lightSpc[]   = new float[4];
  public float lightEmi[]   = new float[4];
  public int lightShininess;


  //radial distribution
  public float rdSlice=0.2f;
  public float rdCut=10.f;

  public boolean plotterExport=false;
  public int plotterDrawType=0;

  //neighbor
  public float neighborAnalysisRcut;
  public boolean isShowRing;
  public boolean isPBC;
  public int ringCalType;
  public int ringRangeMax;

  //colortable
  public int colorTableTypeMAX=ColorTable.maxColorType;//color table
  public int colorTableType;
  public int colorAlphaTypeMAX=4;//alpha type
  public int colorAlphaType;
  public boolean isTicsHLong;
  public int ticsType;
  public int ticsTypeMAX=3;
  public boolean isVisibleAtomColorTable;
  public float[] atomColorTablePos=new float[4];//x,y,width,height
  public boolean isVisibleBondColorTable;
  public float[] bondColorTablePos=new float[4];//x,y,width,height
  public boolean isVisibleVecColorTable;
  public float[] vecColorTablePos=new float[4];//x,y,width,height
  public boolean isVisibleVolColorTable;
  public float[] volColorTablePos=new float[4];//x,y,width,height
  public float[] ctTitlePos=new float[2];
  public float[] ctNumPos=new float[2];


  public boolean isBackRW=false;



  //constructor
  public ViewConfig(){
    if(System.getProperty("os.name").startsWith("Win")){
      configDir= "\\Akira";
      configFile="\\Akira\\viewer.config";
      pluginDir="\\Akira\\plugin";

    }else{
      configDir= System.getProperty("user.home")+"/Akira";
      configFile= configDir+"/viewer.config";
      pluginDir= configDir+"/plugin";
    }
    resetAll();
  }


  //reset default
  public void resetAll(){
    resetManipulation();
    resetNeighbor();
    resetMisc();
    resetAtom();
    resetData();
    resetBond();
    resetVector();
    resetBoundary();
    resetVolume();
    resetPlane();
    resetLight();
    resetColorTable();
  }

  public void resetManipulation(){
    viewMode        = 1;
    ControllerValue = 1.f;
    isRotationXOnly=false;
    isRotationYOnly=false;
    isTransXOnly=false;
    isTransYOnly=false;
    isTrjMode=false;
  }
  public void resetNeighbor(){
    neighborAnalysisRcut=4f;
    isShowRing=true;
    isPBC=false;
    ringCalType=1;
    ringRangeMax=6;
  }

  public void resetMisc(){
    annotationFont  = new Font("courier",Font.BOLD,18);
    bgColor[0]      = 0.f;
    bgColor[1]      = 0.f;
    bgColor[2]      = 0.f;
    bgColor[3]      = 1.f;
    txtColor[0]     = 1.f;
    txtColor[1]     = 1.f;
    txtColor[2]     = 1.f;
    txtColor[3]     = 1.f;
    txtPos[0]       = 5.f;
    txtPos[1]       = 20.f;
    boxColor[0]     = 1.f;
    boxColor[1]     = 1.f;
    boxColor[2]     = 1.f;
    boxColor[3]     = 1.f;
    boxLineWidth    = 2.f;
    startTime       = 0.f;
    timeInterval    = 1.f;
    timePrintFormat = "%.2e Frame";
    imageFormat     = "png";
  }

  //reset atom
  public void resetAtom(){
    // periodic table
    /*
     * ! Element data exported from CrystalMaker 8.0.3
     * ! (Any notes must be preceded by the 'NOTE' card;
     * ! element data must be preceded by the 'ELMT' card.)
     *
     * NOTE
     * Atomic Radii (Calculated) with CPK colours.
     * This dataset uses colours derived from those of the plastic spacefilling models
     * developed by Corey, Pauling and (later improved on by) Kultun ("CPK"). This
     * is the scheme conventionally used by chemists.
     * The atomic radii values are calculated values, from:
     * E Clementi, D L Raimondi, W P Reinhardt (1963) J Chem Phys. 38:2686
     * Data given here are taken from WebElements, copyright Mark Winter, University
     * of Sheffield, UK.
     * Note that where no radius value can be found for a particular element, its
     * radius has been set to a default value of 1.0 Angstroms.
     */

    tagName[0]       = "H";
    tagOnOff[0]      = true;
    tagRadius[0]     = 0.530000;
    tagSlice[0]      = 20;
    tagStack[0]      = 20;
    tagColor[0][0]   = 1.000000f;
    tagColor[0][1]   = 1.000000f;
    tagColor[0][2]   = 1.000000f;
    tagColor[0][3]   = 1.f;
    tagName[1]       = "He";
    tagOnOff[1]      = true;
    tagRadius[1]     = 0.310000;
    tagSlice[1]      = 20;
    tagStack[1]      = 20;
    tagColor[1][0]   = 1.000000f;
    tagColor[1][1]   = 0.784300f;
    tagColor[1][2]   = 0.784300f;
    tagColor[1][3]   = 1.f;
    tagName[2]       = "Li";
    tagOnOff[2]      = true;
    tagRadius[2]     = 1.670000;
    tagSlice[2]      = 20;
    tagStack[2]      = 20;
    tagColor[2][0]   = 0.647090f;
    tagColor[2][1]   = 0.164690f;
    tagColor[2][2]   = 0.164690f;
    tagColor[2][3]   = 1.f;
    tagName[3]       = "Be";
    tagOnOff[3]      = true;
    tagRadius[3]     = 1.120000;
    tagSlice[3]      = 20;
    tagStack[3]      = 20;
    tagColor[3][0]   = 1.000000f;
    tagColor[3][1]   = 0.078390f;
    tagColor[3][2]   = 0.576490f;
    tagColor[3][3]   = 1.f;
    tagName[4]       = "B";
    tagOnOff[4]      = true;
    tagRadius[4]     = 0.870000;
    tagSlice[4]      = 20;
    tagStack[4]      = 20;
    tagColor[4][0]   = 0.000000f;
    tagColor[4][1]   = 1.000000f;
    tagColor[4][2]   = 0.000000f;
    tagColor[4][3]   = 1.f;
    tagName[5]       = "C";
    tagOnOff[5]      = true;
    tagRadius[5]     = 0.670000;
    tagSlice[5]      = 20;
    tagStack[5]      = 20;
    tagColor[5][0]   = 0.784300f;
    tagColor[5][1]   = 0.784300f;
    tagColor[5][2]   = 0.784300f;
    tagColor[5][3]   = 1.f;
    tagName[6]       = "N";
    tagOnOff[6]      = true;
    tagRadius[6]     = 0.560000;
    tagSlice[6]      = 20;
    tagStack[6]      = 20;
    tagColor[6][0]   = 0.560800f;
    tagColor[6][1]   = 0.560800f;
    tagColor[6][2]   = 1.000000f;
    tagColor[6][3]   = 1.f;
    tagName[7]       = "O";
    tagOnOff[7]      = true;
    tagRadius[7]     = 0.480000;
    tagSlice[7]      = 20;
    tagStack[7]      = 20;
    tagColor[7][0]   = 0.941190f;
    tagColor[7][1]   = 0.000000f;
    tagColor[7][2]   = 0.000000f;
    tagColor[7][3]   = 1.f;
    tagName[8]       = "F";
    tagOnOff[8]      = true;
    tagRadius[8]     = 0.420000;
    tagSlice[8]      = 20;
    tagStack[8]      = 20;
    tagColor[8][0]   = 0.784300f;
    tagColor[8][1]   = 0.647090f;
    tagColor[8][2]   = 0.094090f;
    tagColor[8][3]   = 1.f;
    tagName[9]       = "Ne";
    tagOnOff[9]      = true;
    tagRadius[9]     = 0.380000;
    tagSlice[9]      = 20;
    tagStack[9]      = 20;
    tagColor[9][0]   = 1.000000f;
    tagColor[9][1]   = 0.078390f;
    tagColor[9][2]   = 0.576490f;
    tagColor[9][3]   = 1.f;
    tagName[10]      = "Na";
    tagOnOff[10]     = true;
    tagRadius[10]    = 1.900000;
    tagSlice[10]     = 20;
    tagStack[10]     = 20;
    tagColor[10][0]  = 0.000000f;
    tagColor[10][1]  = 0.000000f;
    tagColor[10][2]  = 1.000000f;
    tagColor[10][3]  = 1.f;
    tagName[11]      = "Mg";
    tagOnOff[11]     = true;
    tagRadius[11]    = 1.450000;
    tagSlice[11]     = 20;
    tagStack[11]     = 20;
    tagColor[11][0]  = 0.164690f;
    tagColor[11][1]  = 0.501990f;
    tagColor[11][2]  = 0.164690f;
    tagColor[11][3]  = 1.f;
    tagName[12]      = "Al";
    tagOnOff[12]     = true;
    tagRadius[12]    = 1.180000;
    tagSlice[12]     = 20;
    tagStack[12]     = 20;
    tagColor[12][0]  = 0.501990f;
    tagColor[12][1]  = 0.501990f;
    tagColor[12][2]  = 0.564690f;
    tagColor[12][3]  = 1.f;
    tagName[13]      = "Si";
    tagOnOff[13]     = true;
    tagRadius[13]    = 1.110000;
    tagSlice[13]     = 20;
    tagStack[13]     = 20;
    tagColor[13][0]  = 0.784300f;
    tagColor[13][1]  = 0.647090f;
    tagColor[13][2]  = 0.094090f;
    tagColor[13][3]  = 1.f;
    tagName[14]      = "P";
    tagOnOff[14]     = true;
    tagRadius[14]    = 0.980000;
    tagSlice[14]     = 20;
    tagStack[14]     = 20;
    tagColor[14][0]  = 1.000000f;
    tagColor[14][1]  = 0.647090f;
    tagColor[14][2]  = 0.000000f;
    tagColor[14][3]  = 1.f;
    tagName[15]      = "S";
    tagOnOff[15]     = true;
    tagRadius[15]    = 0.880000;
    tagSlice[15]     = 20;
    tagStack[15]     = 20;
    tagColor[15][0]  = 1.000000f;
    tagColor[15][1]  = 0.784300f;
    tagColor[15][2]  = 0.196090f;
    tagColor[15][3]  = 1.f;
    tagName[16]      = "Cl";
    tagOnOff[16]     = true;
    tagRadius[16]    = 0.790000;
    tagSlice[16]     = 20;
    tagStack[16]     = 20;
    tagColor[16][0]  = 0.000000f;
    tagColor[16][1]  = 1.000000f;
    tagColor[16][2]  = 0.000000f;
    tagColor[16][3]  = 1.f;
    tagName[17]      = "Ar";
    tagOnOff[17]     = true;
    tagRadius[17]    = 0.710000;
    tagSlice[17]     = 20;
    tagStack[17]     = 20;
    tagColor[17][0]  = 1.000000f;
    tagColor[17][1]  = 0.078390f;
    tagColor[17][2]  = 0.576490f;
    tagColor[17][3]  = 1.f;
    tagName[18]      = "K";
    tagOnOff[18]     = true;
    tagRadius[18]    = 2.430000;
    tagSlice[18]     = 20;
    tagStack[18]     = 20;
    tagColor[18][0]  = 1.000000f;
    tagColor[18][1]  = 0.078390f;
    tagColor[18][2]  = 0.576490f;
    tagColor[18][3]  = 1.f;
    tagName[19]      = "Ca";
    tagOnOff[19]     = true;
    tagRadius[19]    = 1.940000;
    tagSlice[19]     = 20;
    tagStack[19]     = 20;
    tagColor[19][0]  = 0.501990f;
    tagColor[19][1]  = 0.501990f;
    tagColor[19][2]  = 0.564690f;
    tagColor[19][3]  = 1.f;
    tagName[20]      = "Sc";
    tagOnOff[20]     = true;
    tagRadius[20]    = 1.840000;
    tagSlice[20]     = 20;
    tagStack[20]     = 20;
    tagColor[20][0]  = 1.000000f;
    tagColor[20][1]  = 0.078390f;
    tagColor[20][2]  = 0.576490f;
    tagColor[20][3]  = 1.f;
    tagName[21]      = "Ti";
    tagOnOff[21]     = true;
    tagRadius[21]    = 1.760000;
    tagSlice[21]     = 20;
    tagStack[21]     = 20;
    tagColor[21][0]  = 0.501990f;
    tagColor[21][1]  = 0.501990f;
    tagColor[21][2]  = 0.564690f;
    tagColor[21][3]  = 1.f;
    tagName[22]      = "V";
    tagOnOff[22]     = true;
    tagRadius[22]    = 1.710000;
    tagSlice[22]     = 20;
    tagStack[22]     = 20;
    tagColor[22][0]  = 1.000000f;
    tagColor[22][1]  = 0.078390f;
    tagColor[22][2]  = 0.576490f;
    tagColor[22][3]  = 1.f;
    tagName[23]      = "Cr";
    tagOnOff[23]     = true;
    tagRadius[23]    = 1.660000;
    tagSlice[23]     = 20;
    tagStack[23]     = 20;
    tagColor[23][0]  = 0.501990f;
    tagColor[23][1]  = 0.501990f;
    tagColor[23][2]  = 0.564690f;
    tagColor[23][3]  = 1.f;
    tagName[24]      = "Mn";
    tagOnOff[24]     = true;
    tagRadius[24]    = 1.610000;
    tagSlice[24]     = 20;
    tagStack[24]     = 20;
    tagColor[24][0]  = 0.501990f;
    tagColor[24][1]  = 0.501990f;
    tagColor[24][2]  = 0.564690f;
    tagColor[24][3]  = 1.f;
    tagName[25]      = "Fe";
    tagOnOff[25]     = true;
    tagRadius[25]    = 1.560000;
    tagSlice[25]     = 20;
    tagStack[25]     = 20;
    tagColor[25][0]  = 1.000000f;
    tagColor[25][1]  = 0.647090f;
    tagColor[25][2]  = 0.000000f;
    tagColor[25][3]  = 1.f;
    tagName[26]      = "Co";
    tagOnOff[26]     = true;
    tagRadius[26]    = 1.520000;
    tagSlice[26]     = 20;
    tagStack[26]     = 20;
    tagColor[26][0]  = 1.000000f;
    tagColor[26][1]  = 0.078390f;
    tagColor[26][2]  = 0.576490f;
    tagColor[26][3]  = 1.f;
    tagName[27]      = "Ni";
    tagOnOff[27]     = true;
    tagRadius[27]    = 1.490000;
    tagSlice[27]     = 20;
    tagStack[27]     = 20;
    tagColor[27][0]  = 0.647090f;
    tagColor[27][1]  = 0.164690f;
    tagColor[27][2]  = 0.164690f;
    tagColor[27][3]  = 1.f;
    tagName[28]      = "Cu";
    tagOnOff[28]     = true;
    tagRadius[28]    = 1.450000;
    tagSlice[28]     = 20;
    tagStack[28]     = 20;
    tagColor[28][0]  = 0.647090f;
    tagColor[28][1]  = 0.164690f;
    tagColor[28][2]  = 0.164690f;
    tagColor[28][3]  = 1.f;
    tagName[29]      = "Zn";
    tagOnOff[29]     = true;
    tagRadius[29]    = 1.420000;
    tagSlice[29]     = 20;
    tagStack[29]     = 20;
    tagColor[29][0]  = 0.647090f;
    tagColor[29][1]  = 0.164690f;
    tagColor[29][2]  = 0.164690f;
    tagColor[29][3]  = 1.f;
    tagName[30]      = "Ga";
    tagOnOff[30]     = true;
    tagRadius[30]    = 1.360000;
    tagSlice[30]     = 20;
    tagStack[30]     = 20;
    tagColor[30][0]  = 1.000000f;
    tagColor[30][1]  = 0.078390f;
    tagColor[30][2]  = 0.576490f;
    tagColor[30][3]  = 1.f;
    tagName[31]      = "Ge";
    tagOnOff[31]     = true;
    tagRadius[31]    = 1.250000;
    tagSlice[31]     = 20;
    tagStack[31]     = 20;
    tagColor[31][0]  = 1.000000f;
    tagColor[31][1]  = 0.078390f;
    tagColor[31][2]  = 0.576490f;
    tagColor[31][3]  = 1.f;
    tagName[32]      = "As";
    tagOnOff[32]     = true;
    tagRadius[32]    = 1.140000;
    tagSlice[32]     = 20;
    tagStack[32]     = 20;
    tagColor[32][0]  = 1.000000f;
    tagColor[32][1]  = 0.078390f;
    tagColor[32][2]  = 0.576490f;
    tagColor[32][3]  = 1.f;
    tagName[33]      = "Se";
    tagOnOff[33]     = true;
    tagRadius[33]    = 1.030000;
    tagSlice[33]     = 20;
    tagStack[33]     = 20;
    tagColor[33][0]  = 1.000000f;
    tagColor[33][1]  = 0.078390f;
    tagColor[33][2]  = 0.576490f;
    tagColor[33][3]  = 1.f;
    tagName[34]      = "Br";
    tagOnOff[34]     = true;
    tagRadius[34]    = 0.940000;
    tagSlice[34]     = 20;
    tagStack[34]     = 20;
    tagColor[34][0]  = 0.647090f;
    tagColor[34][1]  = 0.164690f;
    tagColor[34][2]  = 0.164690f;
    tagColor[34][3]  = 1.f;
    tagName[35]      = "Kr";
    tagOnOff[35]     = true;
    tagRadius[35]    = 0.880000;
    tagSlice[35]     = 20;
    tagStack[35]     = 20;
    tagColor[35][0]  = 1.000000f;
    tagColor[35][1]  = 0.078390f;
    tagColor[35][2]  = 0.576490f;
    tagColor[35][3]  = 1.f;
    tagName[36]      = "Rb";
    tagOnOff[36]     = true;
    tagRadius[36]    = 2.650000;
    tagSlice[36]     = 20;
    tagStack[36]     = 20;
    tagColor[36][0]  = 1.000000f;
    tagColor[36][1]  = 0.078390f;
    tagColor[36][2]  = 0.576490f;
    tagColor[36][3]  = 1.f;
    tagName[37]      = "Sr";
    tagOnOff[37]     = true;
    tagRadius[37]    = 2.190000;
    tagSlice[37]     = 20;
    tagStack[37]     = 20;
    tagColor[37][0]  = 1.000000f;
    tagColor[37][1]  = 0.078390f;
    tagColor[37][2]  = 0.576490f;
    tagColor[37][3]  = 1.f;
    tagName[38]      = "Y";
    tagOnOff[38]     = true;
    tagRadius[38]    = 2.120000;
    tagSlice[38]     = 20;
    tagStack[38]     = 20;
    tagColor[38][0]  = 1.000000f;
    tagColor[38][1]  = 0.078390f;
    tagColor[38][2]  = 0.576490f;
    tagColor[38][3]  = 1.f;
    tagName[39]      = "Zr";
    tagOnOff[39]     = true;
    tagRadius[39]    = 2.060000;
    tagSlice[39]     = 20;
    tagStack[39]     = 20;
    tagColor[39][0]  = 1.000000f;
    tagColor[39][1]  = 0.078390f;
    tagColor[39][2]  = 0.576490f;
    tagColor[39][3]  = 1.f;
    tagName[40]      = "Nb";
    tagOnOff[40]     = true;
    tagRadius[40]    = 1.980000;
    tagSlice[40]     = 20;
    tagStack[40]     = 20;
    tagColor[40][0]  = 1.000000f;
    tagColor[40][1]  = 0.078390f;
    tagColor[40][2]  = 0.576490f;
    tagColor[40][3]  = 1.f;
    tagName[41]      = "Mo";
    tagOnOff[41]     = true;
    tagRadius[41]    = 1.900000;
    tagSlice[41]     = 20;
    tagStack[41]     = 20;
    tagColor[41][0]  = 1.000000f;
    tagColor[41][1]  = 0.078390f;
    tagColor[41][2]  = 0.576490f;
    tagColor[41][3]  = 1.f;
    tagName[42]      = "Tc";
    tagOnOff[42]     = true;
    tagRadius[42]    = 1.830000;
    tagSlice[42]     = 20;
    tagStack[42]     = 20;
    tagColor[42][0]  = 1.000000f;
    tagColor[42][1]  = 0.078390f;
    tagColor[42][2]  = 0.576490f;
    tagColor[42][3]  = 1.f;
    tagName[43]      = "Ru";
    tagOnOff[43]     = true;
    tagRadius[43]    = 1.780000;
    tagSlice[43]     = 20;
    tagStack[43]     = 20;
    tagColor[43][0]  = 1.000000f;
    tagColor[43][1]  = 0.078390f;
    tagColor[43][2]  = 0.576490f;
    tagColor[43][3]  = 1.f;
    tagName[44]      = "Rh";
    tagOnOff[44]     = true;
    tagRadius[44]    = 1.730000;
    tagSlice[44]     = 20;
    tagStack[44]     = 20;
    tagColor[44][0]  = 1.000000f;
    tagColor[44][1]  = 0.078390f;
    tagColor[44][2]  = 0.576490f;
    tagColor[44][3]  = 1.f;
    tagName[45]      = "Pd";
    tagOnOff[45]     = true;
    tagRadius[45]    = 1.690000;
    tagSlice[45]     = 20;
    tagStack[45]     = 20;
    tagColor[45][0]  = 1.000000f;
    tagColor[45][1]  = 0.078390f;
    tagColor[45][2]  = 0.576490f;
    tagColor[45][3]  = 1.f;
    tagName[46]      = "Ag";
    tagOnOff[46]     = true;
    tagRadius[46]    = 1.650000;
    tagSlice[46]     = 20;
    tagStack[46]     = 20;
    tagColor[46][0]  = 0.501990f;
    tagColor[46][1]  = 0.501990f;
    tagColor[46][2]  = 0.564690f;
    tagColor[46][3]  = 1.f;
    tagName[47]      = "Cd";
    tagOnOff[47]     = true;
    tagRadius[47]    = 1.610000;
    tagSlice[47]     = 20;
    tagStack[47]     = 20;
    tagColor[47][0]  = 1.000000f;
    tagColor[47][1]  = 0.078390f;
    tagColor[47][2]  = 0.576490f;
    tagColor[47][3]  = 1.f;
    tagName[48]      = "In";
    tagOnOff[48]     = true;
    tagRadius[48]    = 1.560000;
    tagSlice[48]     = 20;
    tagStack[48]     = 20;
    tagColor[48][0]  = 1.000000f;
    tagColor[48][1]  = 0.078390f;
    tagColor[48][2]  = 0.576490f;
    tagColor[48][3]  = 1.f;
    tagName[49]      = "Sn";
    tagOnOff[49]     = true;
    tagRadius[49]    = 1.450000;
    tagSlice[49]     = 20;
    tagStack[49]     = 20;
    tagColor[49][0]  = 1.000000f;
    tagColor[49][1]  = 0.078390f;
    tagColor[49][2]  = 0.576490f;
    tagColor[49][3]  = 1.f;
    tagName[50]      = "Sb";
    tagOnOff[50]     = true;
    tagRadius[50]    = 1.330000;
    tagSlice[50]     = 20;
    tagStack[50]     = 20;
    tagColor[50][0]  = 1.000000f;
    tagColor[50][1]  = 0.078390f;
    tagColor[50][2]  = 0.576490f;
    tagColor[50][3]  = 1.f;
    tagName[51]      = "Te";
    tagOnOff[51]     = true;
    tagRadius[51]    = 1.230000;
    tagSlice[51]     = 20;
    tagStack[51]     = 20;
    tagColor[51][0]  = 1.000000f;
    tagColor[51][1]  = 0.078390f;
    tagColor[51][2]  = 0.576490f;
    tagColor[51][3]  = 1.f;
    tagName[52]      = "I";
    tagOnOff[52]     = true;
    tagRadius[52]    = 1.150000;
    tagSlice[52]     = 20;
    tagStack[52]     = 20;
    tagColor[52][0]  = 0.560800f;
    tagColor[52][1]  = 0.560800f;
    tagColor[52][2]  = 1.000000f;
    tagColor[52][3]  = 1.f;
    tagName[53]      = "Xe";
    tagOnOff[53]     = true;
    tagRadius[53]    = 1.080000;
    tagSlice[53]     = 20;
    tagStack[53]     = 20;
    tagColor[53][0]  = 1.000000f;
    tagColor[53][1]  = 0.078390f;
    tagColor[53][2]  = 0.576490f;
    tagColor[53][3]  = 1.f;
    tagName[54]      = "Cs";
    tagOnOff[54]     = true;
    tagRadius[54]    = 2.980000;
    tagSlice[54]     = 20;
    tagStack[54]     = 20;
    tagColor[54][0]  = 1.000000f;
    tagColor[54][1]  = 0.078390f;
    tagColor[54][2]  = 0.576490f;
    tagColor[54][3]  = 1.f;
    tagName[55]      = "Ba";
    tagOnOff[55]     = true;
    tagRadius[55]    = 2.530000;
    tagSlice[55]     = 20;
    tagStack[55]     = 20;
    tagColor[55][0]  = 1.000000f;
    tagColor[55][1]  = 0.647090f;
    tagColor[55][2]  = 0.000000f;
    tagColor[55][3]  = 1.f;
    tagName[56]      = "La";
    tagOnOff[56]     = true;
    tagRadius[56]    = 1.950000;
    tagSlice[56]     = 20;
    tagStack[56]     = 20;
    tagColor[56][0]  = 1.000000f;
    tagColor[56][1]  = 0.078390f;
    tagColor[56][2]  = 0.576490f;
    tagColor[56][3]  = 1.f;
    tagName[57]      = "Ce";
    tagOnOff[57]     = true;
    tagRadius[57]    = 1.850000;
    tagSlice[57]     = 20;
    tagStack[57]     = 20;
    tagColor[57][0]  = 1.000000f;
    tagColor[57][1]  = 0.078390f;
    tagColor[57][2]  = 0.576490f;
    tagColor[57][3]  = 1.f;
    tagName[58]      = "Pr";
    tagOnOff[58]     = true;
    tagRadius[58]    = 2.470000;
    tagSlice[58]     = 20;
    tagStack[58]     = 20;
    tagColor[58][0]  = 1.000000f;
    tagColor[58][1]  = 0.078390f;
    tagColor[58][2]  = 0.576490f;
    tagColor[58][3]  = 1.f;
    tagName[59]      = "Nd";
    tagOnOff[59]     = true;
    tagRadius[59]    = 2.060000;
    tagSlice[59]     = 20;
    tagStack[59]     = 20;
    tagColor[59][0]  = 1.000000f;
    tagColor[59][1]  = 0.078390f;
    tagColor[59][2]  = 0.576490f;
    tagColor[59][3]  = 1.f;
    tagName[60]      = "Pm";
    tagOnOff[60]     = true;
    tagRadius[60]    = 2.050000;
    tagSlice[60]     = 20;
    tagStack[60]     = 20;
    tagColor[60][0]  = 1.000000f;
    tagColor[60][1]  = 0.078390f;
    tagColor[60][2]  = 0.576490f;
    tagColor[60][3]  = 1.f;
    tagName[61]      = "Sm";
    tagOnOff[61]     = true;
    tagRadius[61]    = 2.380000;
    tagSlice[61]     = 20;
    tagStack[61]     = 20;
    tagColor[61][0]  = 1.000000f;
    tagColor[61][1]  = 0.078390f;
    tagColor[61][2]  = 0.576490f;
    tagColor[61][3]  = 1.f;
    tagName[62]      = "Eu";
    tagOnOff[62]     = true;
    tagRadius[62]    = 2.310000;
    tagSlice[62]     = 20;
    tagStack[62]     = 20;
    tagColor[62][0]  = 1.000000f;
    tagColor[62][1]  = 0.078390f;
    tagColor[62][2]  = 0.576490f;
    tagColor[62][3]  = 1.f;
    tagName[63]      = "Gd";
    tagOnOff[63]     = true;
    tagRadius[63]    = 2.330000;
    tagSlice[63]     = 20;
    tagStack[63]     = 20;
    tagColor[63][0]  = 1.000000f;
    tagColor[63][1]  = 0.078390f;
    tagColor[63][2]  = 0.576490f;
    tagColor[63][3]  = 1.f;
    tagName[64]      = "Tb";
    tagOnOff[64]     = true;
    tagRadius[64]    = 2.250000;
    tagSlice[64]     = 20;
    tagStack[64]     = 20;
    tagColor[64][0]  = 1.000000f;
    tagColor[64][1]  = 0.078390f;
    tagColor[64][2]  = 0.576490f;
    tagColor[64][3]  = 1.f;
    tagName[65]      = "Dy";
    tagOnOff[65]     = true;
    tagRadius[65]    = 2.280000;
    tagSlice[65]     = 20;
    tagStack[65]     = 20;
    tagColor[65][0]  = 1.000000f;
    tagColor[65][1]  = 0.078390f;
    tagColor[65][2]  = 0.576490f;
    tagColor[65][3]  = 1.f;
    tagName[66]      = "Ho";
    tagOnOff[66]     = true;
    tagRadius[66]    = 2.260000;
    tagSlice[66]     = 20;
    tagStack[66]     = 20;
    tagColor[66][0]  = 1.000000f;
    tagColor[66][1]  = 0.078390f;
    tagColor[66][2]  = 0.576490f;
    tagColor[66][3]  = 1.f;
    tagName[67]      = "Er";
    tagOnOff[67]     = true;
    tagRadius[67]    = 2.260000;
    tagSlice[67]     = 20;
    tagStack[67]     = 20;
    tagColor[67][0]  = 1.000000f;
    tagColor[67][1]  = 0.078390f;
    tagColor[67][2]  = 0.576490f;
    tagColor[67][3]  = 1.f;
    tagName[68]      = "Tm";
    tagOnOff[68]     = true;
    tagRadius[68]    = 2.220000;
    tagSlice[68]     = 20;
    tagStack[68]     = 20;
    tagColor[68][0]  = 1.000000f;
    tagColor[68][1]  = 0.078390f;
    tagColor[68][2]  = 0.576490f;
    tagColor[68][3]  = 1.f;
    tagName[69]      = "Yb";
    tagOnOff[69]     = true;
    tagRadius[69]    = 2.220000;
    tagSlice[69]     = 20;
    tagStack[69]     = 20;
    tagColor[69][0]  = 1.000000f;
    tagColor[69][1]  = 0.078390f;
    tagColor[69][2]  = 0.576490f;
    tagColor[69][3]  = 1.f;
    tagName[70]      = "Lu";
    tagOnOff[70]     = true;
    tagRadius[70]    = 2.170000;
    tagSlice[70]     = 20;
    tagStack[70]     = 20;
    tagColor[70][0]  = 1.000000f;
    tagColor[70][1]  = 0.078390f;
    tagColor[70][2]  = 0.576490f;
    tagColor[70][3]  = 1.f;
    tagName[71]      = "Hf";
    tagOnOff[71]     = true;
    tagRadius[71]    = 2.080000;
    tagSlice[71]     = 20;
    tagStack[71]     = 20;
    tagColor[71][0]  = 1.000000f;
    tagColor[71][1]  = 0.078390f;
    tagColor[71][2]  = 0.576490f;
    tagColor[71][3]  = 1.f;
    tagName[72]      = "Ta";
    tagOnOff[72]     = true;
    tagRadius[72]    = 2.000000;
    tagSlice[72]     = 20;
    tagStack[72]     = 20;
    tagColor[72][0]  = 1.000000f;
    tagColor[72][1]  = 0.078390f;
    tagColor[72][2]  = 0.576490f;
    tagColor[72][3]  = 1.f;
    tagName[73]      = "W";
    tagOnOff[73]     = true;
    tagRadius[73]    = 1.930000;
    tagSlice[73]     = 20;
    tagStack[73]     = 20;
    tagColor[73][0]  = 1.000000f;
    tagColor[73][1]  = 0.078390f;
    tagColor[73][2]  = 0.576490f;
    tagColor[73][3]  = 1.f;
    tagName[74]      = "Re";
    tagOnOff[74]     = true;
    tagRadius[74]    = 1.880000;
    tagSlice[74]     = 20;
    tagStack[74]     = 20;
    tagColor[74][0]  = 1.000000f;
    tagColor[74][1]  = 0.078390f;
    tagColor[74][2]  = 0.576490f;
    tagColor[74][3]  = 1.f;
    tagName[75]      = "Os";
    tagOnOff[75]     = true;
    tagRadius[75]    = 1.850000;
    tagSlice[75]     = 20;
    tagStack[75]     = 20;
    tagColor[75][0]  = 1.000000f;
    tagColor[75][1]  = 0.078390f;
    tagColor[75][2]  = 0.576490f;
    tagColor[75][3]  = 1.f;
    tagName[76]      = "Ir";
    tagOnOff[76]     = true;
    tagRadius[76]    = 1.800000;
    tagSlice[76]     = 20;
    tagStack[76]     = 20;
    tagColor[76][0]  = 1.000000f;
    tagColor[76][1]  = 0.078390f;
    tagColor[76][2]  = 0.576490f;
    tagColor[76][3]  = 1.f;
    tagName[77]      = "Pt";
    tagOnOff[77]     = true;
    tagRadius[77]    = 1.770000;
    tagSlice[77]     = 20;
    tagStack[77]     = 20;
    tagColor[77][0]  = 1.000000f;
    tagColor[77][1]  = 0.078390f;
    tagColor[77][2]  = 0.576490f;
    tagColor[77][3]  = 1.f;
    tagName[78]      = "Au";
    tagOnOff[78]     = true;
    tagRadius[78]    = 1.740000;
    tagSlice[78]     = 20;
    tagStack[78]     = 20;
    tagColor[78][0]  = 0.784300f;
    tagColor[78][1]  = 0.647090f;
    tagColor[78][2]  = 0.094090f;
    tagColor[78][3]  = 1.f;
    tagName[79]      = "Hg";
    tagOnOff[79]     = true;
    tagRadius[79]    = 1.710000;
    tagSlice[79]     = 20;
    tagStack[79]     = 20;
    tagColor[79][0]  = 1.000000f;
    tagColor[79][1]  = 0.078390f;
    tagColor[79][2]  = 0.576490f;
    tagColor[79][3]  = 1.f;
    tagName[80]      = "Tl";
    tagOnOff[80]     = true;
    tagRadius[80]    = 1.560000;
    tagSlice[80]     = 20;
    tagStack[80]     = 20;
    tagColor[80][0]  = 1.000000f;
    tagColor[80][1]  = 0.078390f;
    tagColor[80][2]  = 0.576490f;
    tagColor[80][3]  = 1.f;
    tagName[81]      = "Pb";
    tagOnOff[81]     = true;
    tagRadius[81]    = 1.540000;
    tagSlice[81]     = 20;
    tagStack[81]     = 20;
    tagColor[81][0]  = 1.000000f;
    tagColor[81][1]  = 0.078390f;
    tagColor[81][2]  = 0.576490f;
    tagColor[81][3]  = 1.f;
    tagName[82]      = "Bi";
    tagOnOff[82]     = true;
    tagRadius[82]    = 1.430000;
    tagSlice[82]     = 20;
    tagStack[82]     = 20;
    tagColor[82][0]  = 1.000000f;
    tagColor[82][1]  = 0.078390f;
    tagColor[82][2]  = 0.576490f;
    tagColor[82][3]  = 1.f;
    tagName[83]      = "Po";
    tagOnOff[83]     = true;
    tagRadius[83]    = 1.350000;
    tagSlice[83]     = 20;
    tagStack[83]     = 20;
    tagColor[83][0]  = 1.000000f;
    tagColor[83][1]  = 0.078390f;
    tagColor[83][2]  = 0.576490f;
    tagColor[83][3]  = 1.f;
    tagName[84]      = "At";
    tagOnOff[84]     = true;
    tagRadius[84]    = 1.270000;
    tagSlice[84]     = 20;
    tagStack[84]     = 20;
    tagColor[84][0]  = 1.000000f;
    tagColor[84][1]  = 0.078390f;
    tagColor[84][2]  = 0.576490f;
    tagColor[84][3]  = 1.f;
    tagName[85]      = "Rn";
    tagOnOff[85]     = true;
    tagRadius[85]    = 1.200000;
    tagSlice[85]     = 20;
    tagStack[85]     = 20;
    tagColor[85][0]  = 1.000000f;
    tagColor[85][1]  = 0.078390f;
    tagColor[85][2]  = 0.576490f;
    tagColor[85][3]  = 1.f;
    tagName[86]      = "Fr";
    tagOnOff[86]     = true;
    tagRadius[86]    = 1.000000;
    tagSlice[86]     = 20;
    tagStack[86]     = 20;
    tagColor[86][0]  = 1.000000f;
    tagColor[86][1]  = 0.078390f;
    tagColor[86][2]  = 0.576490f;
    tagColor[86][3]  = 1.f;
    tagName[87]      = "Ra";
    tagOnOff[87]     = true;
    tagRadius[87]    = 1.000000;
    tagSlice[87]     = 20;
    tagStack[87]     = 20;
    tagColor[87][0]  = 1.000000f;
    tagColor[87][1]  = 0.078390f;
    tagColor[87][2]  = 0.576490f;
    tagColor[87][3]  = 1.f;
    tagName[88]      = "Ac";
    tagOnOff[88]     = true;
    tagRadius[88]    = 1.950000;
    tagSlice[88]     = 20;
    tagStack[88]     = 20;
    tagColor[88][0]  = 1.000000f;
    tagColor[88][1]  = 0.078390f;
    tagColor[88][2]  = 0.576490f;
    tagColor[88][3]  = 1.f;
    tagName[89]      = "Th";
    tagOnOff[89]     = true;
    tagRadius[89]    = 1.800000;
    tagSlice[89]     = 20;
    tagStack[89]     = 20;
    tagColor[89][0]  = 1.000000f;
    tagColor[89][1]  = 0.078390f;
    tagColor[89][2]  = 0.576490f;
    tagColor[89][3]  = 1.f;
    tagName[90]      = "Pa";
    tagOnOff[90]     = true;
    tagRadius[90]    = 1.800000;
    tagSlice[90]     = 20;
    tagStack[90]     = 20;
    tagColor[90][0]  = 1.000000f;
    tagColor[90][1]  = 0.078390f;
    tagColor[90][2]  = 0.576490f;
    tagColor[90][3]  = 1.f;
    tagName[91]      = "U";
    tagOnOff[91]     = true;
    tagRadius[91]    = 1.750000;
    tagSlice[91]     = 20;
    tagStack[91]     = 20;
    tagColor[91][0]  = 1.000000f;
    tagColor[91][1]  = 0.078390f;
    tagColor[91][2]  = 0.576490f;
    tagColor[91][3]  = 1.f;
    tagName[92]      = "Np";
    tagOnOff[92]     = true;
    tagRadius[92]    = 1.750000;
    tagSlice[92]     = 20;
    tagStack[92]     = 20;
    tagColor[92][0]  = 1.000000f;
    tagColor[92][1]  = 0.078390f;
    tagColor[92][2]  = 0.576490f;
    tagColor[92][3]  = 1.f;
    tagName[93]      = "Pu";
    tagOnOff[93]     = true;
    tagRadius[93]    = 1.750000;
    tagSlice[93]     = 20;
    tagStack[93]     = 20;
    tagColor[93][0]  = 1.000000f;
    tagColor[93][1]  = 0.078390f;
    tagColor[93][2]  = 0.576490f;
    tagColor[93][3]  = 1.f;
    tagName[94]      = "Am";
    tagOnOff[94]     = true;
    tagRadius[94]    = 1.750000;
    tagSlice[94]     = 20;
    tagStack[94]     = 20;
    tagColor[94][0]  = 1.000000f;
    tagColor[94][1]  = 0.078390f;
    tagColor[94][2]  = 0.576490f;
    tagColor[94][3]  = 1.f;
    tagName[95]      = "Cm";
    tagOnOff[95]     = true;
    tagRadius[95]    = 1.000000;
    tagSlice[95]     = 20;
    tagStack[95]     = 20;
    tagColor[95][0]  = 1.000000f;
    tagColor[95][1]  = 0.078390f;
    tagColor[95][2]  = 0.576490f;
    tagColor[95][3]  = 1.f;
    tagName[96]      = "Bk";
    tagOnOff[96]     = true;
    tagRadius[96]    = 1.000000;
    tagSlice[96]     = 20;
    tagStack[96]     = 20;
    tagColor[96][0]  = 1.000000f;
    tagColor[96][1]  = 0.078390f;
    tagColor[96][2]  = 0.576490f;
    tagColor[96][3]  = 1.f;
    tagName[97]      = "Cf";
    tagOnOff[97]     = true;
    tagRadius[97]    = 1.000000;
    tagSlice[97]     = 20;
    tagStack[97]     = 20;
    tagColor[97][0]  = 1.000000f;
    tagColor[97][1]  = 0.078390f;
    tagColor[97][2]  = 0.576490f;
    tagColor[97][3]  = 1.f;
    tagName[98]      = "Es";
    tagOnOff[98]     = true;
    tagRadius[98]    = 1.000000;
    tagSlice[98]     = 20;
    tagStack[98]     = 20;
    tagColor[98][0]  = 1.000000f;
    tagColor[98][1]  = 0.078390f;
    tagColor[98][2]  = 0.576490f;
    tagColor[98][3]  = 1.f;
    tagName[99]      = "Fm";
    tagOnOff[99]     = true;
    tagRadius[99]    = 1.000000;
    tagSlice[99]     = 20;
    tagStack[99]     = 20;
    tagColor[99][0]  = 1.000000f;
    tagColor[99][1]  = 0.078390f;
    tagColor[99][2]  = 0.576490f;
    tagColor[99][3]  = 1.f;
    tagName[100]     = "Volume data";
    tagOnOff[100]    = false;
    tagRadius[100]   = 1.000000;
    tagSlice[100]    = 20;
    tagStack[100]    = 20;
    tagColor[100][0] = 1.000000f;
    tagColor[100][1] = 1.000000f;
    tagColor[100][2] = 1.000000f;
    tagColor[100][3] = 1.f;

    atomSpecular[0]=atomSpecular[1]=atomSpecular[2]= 0.3f;
    atomSpecular[3]=1.f;
    atomAmb[0]=atomAmb[1]=atomAmb[2]= 0.f;
    atomAmb[3]=1.f;
    atomEmmission[0]=atomEmmission[1]=atomEmmission[2]=0.f;
    atomEmmission[3]=1.f;
    atomShineness=100;
    isSelectionInfo    = false;
    isSelectionLength  = false;
    isSelectionAngle   = false;
    isSelectionTorsion = false;
  }

  //reset data
  public void resetData(){
    for(int i=0;i<9;i++){
      dataRange[i][0] = 0.f;
      dataRange[i][1] = 1.f;
    }
    dataCutRange[0] = 0.f;
    dataCutRange[1] = 1.f;
    dataLegend[0]   = "data 1";
    dataLegend[1]   = "data 2";
    dataLegend[2]   = "data 3";
    dataLegend[3]   = "data 4";
    dataLegend[4]   = "data 5";
    dataLegend[5]   = "data 6";
    dataLegend[6]   = "data 7";
    dataLegend[7]   = "data 8";
    dataLegend[8]   = "data 9";
    dataFormat[0]   = "%.1e";
    dataFormat[1]   = "%.1e";
    dataFormat[2]   = "%.1e";
    dataFormat[3]   = "%.1e";
    dataFormat[4]   = "%.1e";
    dataFormat[5]   = "%.1e";
    dataFormat[6]   = "%.1e";
    dataFormat[7]   = "%.1e";
    dataFormat[8]   = "%.1e";
  }

  //reset bond
  public void resetBond(){
    bondRadius           = 0.1f;
    bondSlice            = 15;
    bondLengthRange[0]   = 0.f;
    bondLengthRange[1]   = 1.f;
    bondCNRange[0]       = 0.f;
    bondCNRange[1]       = 1.f;
    bondLegend           = "bond";
    bondColorTableFormat = "%.1e";
  }

  //reset vector
  public void resetVector(){
    vecConeRadius       = 0.3f;
    vecConeHeight       = 0.8f;
    vecLengthRatio      = 10.f;
    vecCylinderRadius   = 0.1f;
    vecCylinderSlice    = 15;
    vectorX             = 0;
    vectorY             = 1;
    vectorZ             = 2;
    vecLengthRange[0]   = 0.f;
    vecLengthRange[1]   = 1.f;
    vecLegend           = "vector";
    vecColorTableFormat = "%.2e";
  }

  //slicer
  public void resetBoundary(){
    extendRenderingFactor[0][0] = 0.f;
    extendRenderingFactor[0][1] = 0.f;
    extendRenderingFactor[0][2] = 0.f;
    extendRenderingFactor[1][0] = 1.f;
    extendRenderingFactor[1][1] = 1.f;
    extendRenderingFactor[1][2] = 1.f;
    for(int i=0;i<Const.PLANE;i++){
      isOnSlicer[i] = false;
      for(int j=0;j<3;j++)normalVecSlicer[i][j]=1.f;
      for(int j=0;j<3;j++)posVecSlicer[i][j]=0.5f;
    }
    isSphereCut=false;
    spherecutPos[0]=0.5f;
    spherecutPos[1]=0.5f;
    spherecutPos[2]=0.5f;
    spherecutRadius=10.f;
    isDeletionMode=false;
    isDragMoveMode=false;
    moveDirection=0;
    moveVal=0.1f;
    isRegionSelectMode=false;
    isRectangleSelectMode=false;
  }

  //volume rendering
  public void resetVolume(){
    for(int i=0;i<3;i++){
      volDataMesh[i]            = 30;
      volDrawMesh[i]            = 30;
      volContourPlaneNormal[i]  = 1.f;
      volContourPlanePoint[i]   = 0.5f;
      volContour2PlaneNormal[i] = 1.f;
      volContour2PlanePoint[i]  = 0.5f;
    }
    volContourPointSize  = 10;
    volCutLevel[0]       = 0.3f;
    volCutLevel[1]       = 0.7f;
    volDensityFactor     = 1.33f;
    volSurfaceNeighbors  = 1;
    volSurfaceLevel      = 0.5f;
    volSurfaceLevel2     = 0.5f;
    volSurfaceRenderType = 2;
    isVolXY              = false;
    isVolXZ              = false;
    isVolYZ              = false;
    isVolContour         = false;
    isVolContour2        = false;
    isVolCut             = false;
    isVolSurface         = false;
    isVolSurface2        = false;
    isVolCutTiny         = false;
    volRange[0]          = 0.f;
    volRange[1]          = 1.f;
    volLegend            = "volume";
    volColorTableFormat  = "%.2e";
  }

  //plane
  public void resetPlane(){
    isSelectionPlaneMode = false;
    isVoronoiMode = false;
    isDelaunayMode = false;
    for(int i=0;i<Const.PLANE;i++){
      isPlaneVisible[i]=false;
      for(int j=0;j<3;j++)planeNormal[i][j]=1.f;
      for(int j=0;j<3;j++)planePoint[i][j]=0.5f;
      for(int j=0;j<4;j++)planeColor[i][j]=j/4.f;
    }
    isTetrahedronMode=false;
    tetrahedronCenter=1;
    planeRcut=4.f;
    singlePlaneColor[0]=1.0f;
    singlePlaneColor[1]=0.0f;
    singlePlaneColor[2]=0.0f;
    singlePlaneColor[3]=0.7f;
  }

  //light
  public void resetLight(){
    lightPos[0] = 10.0f;
    lightPos[1] = 10.0f;
    lightPos[2] = 100.0f;
    lightPos[3] = 0.0f;

    lightDif[0]=lightDif[1]=lightDif[2]= 1f;
    lightDif[3] = 1.0f;

    lightAmb[0]=lightAmb[1]=lightAmb[2]= 0f;
    lightAmb[3] = 1.0f;

    lightSpc[0]=lightSpc[1]=lightSpc[2]= 0.f;
    lightSpc[3] = 1.0f;

    lightEmi[0]=lightEmi[1]=lightEmi[2]= 0.0f;
    lightEmi[3] = 1.0f;

    lightShininess = 45;

    isLightPosAuto = true;
  }

  public void resetColorTable(){
    colorTableType = 0;
    colorAlphaType = 0;
    isTicsHLong=false;
    ticsType       = 0;

    isVisibleAtomColorTable = true;
    atomColorTablePos[0]    = 0.01f;
    atomColorTablePos[1]    = 0.01f;
    atomColorTablePos[2]    = 20f;
    atomColorTablePos[3]    = 200f;

    isVisibleBondColorTable = false;
    bondColorTablePos[0]    = 0.2f;
    bondColorTablePos[1]    = 0.01f;
    bondColorTablePos[2]    = 20f;
    bondColorTablePos[3]    = 200f;

    isVisibleVecColorTable = false;
    vecColorTablePos[0]    = 0.4f;
    vecColorTablePos[1]    = 0.01f;
    vecColorTablePos[2]    = 20f;
    vecColorTablePos[3]    = 200f;

    isVisibleVolColorTable = false;
    volColorTablePos[0]    = 0.6f;
    volColorTablePos[1]    = 0.01f;
    volColorTablePos[2]    = 20f;
    volColorTablePos[3]    = 200f;

    ctTitlePos[0] = 0.f;
    ctTitlePos[1] = 15.f;
    ctNumPos[0]   = 0.f;
    ctNumPos[1]   = 0.f;
  }



  /* window pos. and size */
  private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

  public Rectangle rectViewConfigWin      = new Rectangle(2,22, d.width-100, 250 );
  public boolean   isVisibleViewConfigWin = true;
  public int       viewConfigWinTabIndex  = 0;

  public Rectangle rectRWin = new Rectangle(2,  277, d.width-350, d.height-290 );

  public Rectangle rect2DPlot = new Rectangle(40,  100, 730, 550 );
  public Rectangle rectStatusWin = new Rectangle(350, 80, 450, 700 );

  // exiting process; write pos and size of all windows
  void saveWin(Controller ctrl){
    //view config win
    rectViewConfigWin      = ctrl.vcWin.getBounds();
    isVisibleViewConfigWin = true;
    viewConfigWinTabIndex  = ctrl.vcWin.tabbedPane.getSelectedIndex();

    //if(ctrl.vcWin.statusPanel.statusFrame!=null)rectStatusWin = ctrl.vcWin.statusPanel.statusFrame.getBounds();
    //rw
    if(ctrl.getActiveRW()!=null)rectRWin  = ctrl.getActiveRW().getBounds();

  }



}

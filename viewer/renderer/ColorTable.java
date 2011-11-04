package viewer.renderer;

import java.util.*;

import viewer.*;

interface ColorType {
  void setRange();
  float[] getColor( float val);
}


public class ColorTable {
  private float[] rgba = new float[4];

  ArrayList<ColorType> tables = new ArrayList<ColorType>();
  static public final int maxColorType=10;
  public static final String colorName[]={
    "Blue-Gray-Red 0.8",
    "Blue-Gray-Red 0.3",
    "Blue-Gray-Red 0.1",
    "ThermoGraphy",
    "Cycle ThermoGraphy",
    "Rainbow",
    "White-Black",
    "Black-White",
    "BGYR",
    "BGR"
  };
  private BlueGrayRed                bgr3    = new BlueGrayRed(0.8f);
  private BlueGrayRed                bgr2    = new BlueGrayRed(0.3f);
  private BlueGrayRed                bgr1    = new BlueGrayRed(0.1f);
  private Rainbow                    rainbow = new Rainbow();
  private CycleRainbow               cyclerainbow = new CycleRainbow();
  private BlueGreenRedYellow         bgry    = new BlueGreenRedYellow();
  private GrayScaleWB                grayWB  = new GrayScaleWB();
  private GrayScaleBW                grayBW  = new GrayScaleBW();
  private DiscreteBlueGreenYellowRed dbgry   = new DiscreteBlueGreenYellowRed();
  private DiscreteBlueGreenRed       dbgr    = new DiscreteBlueGreenRed();

  private RenderingWindow rw;
  public ColorTable(RenderingWindow rw){
    this.rw=rw;
    tables.add( bgr3 );
    tables.add( bgr2 );
    tables.add( bgr1 );
    tables.add( rainbow );
    tables.add( cyclerainbow );
    tables.add( bgry );
    tables.add( grayWB );
    tables.add( grayBW );
    tables.add(dbgry);
    tables.add(dbgr);
  }

  public float[] range=new float[2];
  private float drRange,drRangei;
  public void setRange(float[] range){
    this.range[0]=range[0];
    this.range[1]=range[1];
    drRange=range[1]-range[0];
    drRangei=1.f/drRange;
    for(int i=0;i<tables.size();i++)(tables.get(i)).setRange();
  }

  public float[] getColor( float val){
    try {
      return(tables.get(rw.vconf.colorTableType)).getColor(val);
    }
    catch( IndexOutOfBoundsException e ){
    }
    return(tables.get(0)).getColor(val);
  }

  private float getAlpha(float val){
    float alpha;

    switch(rw.vconf.colorAlphaType){
    case 1:
      //high data is opacity color
      alpha=(val-range[0])*drRangei;
      break;
    case 2:
      //low data is opacity color
      alpha=1.f-(val-range[0])*drRangei;
      break;
    case 3:
      //cut range
      float tmp=(val-range[0])*drRangei;
      if(rw.vconf.dataCutRange[0]<=tmp && tmp<=rw.vconf.dataCutRange[1]){
        alpha=1.f;
      }else{
        alpha=0.f;
      }
      alpha=1.f;
      break;
    default:
      alpha=1.f;
      break;
    }

    return alpha;
  }

  private float[] convertHSVtoRGB( float[] hsv ){
    int i;
    float h, f, m, n, k;
    HSVperiodicboundary( hsv );
    h = hsv[0]/60.0f;
    i =(int) Math.floor( h );
    f = h - i;
    m = hsv[2] *( 1.0f - hsv[1] );
    n = hsv[2] *( 1.0f - hsv[1] * f );
    k = hsv[2] *( 1.0f - hsv[1] *( 1 - f ) );

    switch(i){
    case 0:
      rgba[0] = hsv[2];
      rgba[1] = k;
      rgba[2] = m;
      break;
    case 1:
      rgba[0] = n;
      rgba[1] = hsv[2];
      rgba[2] = m;
      break;
    case 2:
      rgba[0] = m;
      rgba[1] = hsv[2];
      rgba[2] = k;
      break;
    case 3:
      rgba[0] = m;
      rgba[1] = n;
      rgba[2] = hsv[2];
      break;
    case 4:
      rgba[0] = k;
      rgba[1] = m;
      rgba[2] = hsv[2];
      break;
    case 5:
      rgba[0] = hsv[2];
      rgba[1] = m;
      rgba[2] = n;
      break;
    default:
      System.out.println("Error: hsv[0]=" + hsv[0]+ " at " + i );
      rgba[0] = 0.0f;
      rgba[1] = 0.0f;
      rgba[2] = 0.0f;
    }
    rgba[3] = 1.f;//set temporary
    return rgba;
  }

  private void HSVperiodicboundary( float[] hsv ){
    /*
      hue        = 0.0 ~ 359.0
      saturation = 0.0 ~ 1.0
      value      = 0.0 ~ 1.0
    */
    if( hsv[0] < 0.0f ){
      hsv[0] += 360.0f;
    }
    if( hsv[0] >= 360.0f ){
      hsv[0] -= 360.0f;
    }

    for( int k=1; k<3; k++ ){
      if( hsv[k] < 0.0f ){
        hsv[k] = 0.0f;
      }
      else if( hsv[k] > 1.0f ){
        hsv[k] = 1.0f;
      }
    }
  }

  private float calcColor( int m,float[] knotx, float[] knoty,float r){
    while( m > 0 && r <= knotx[m] ){
      --m;
    }
    float slope;

    if(knoty.length>4){
      if(m+1<=4)
        slope =(knoty[m+1]-knoty[m]) /(knotx[m+1]-knotx[m]);
      else
        slope =(knoty[m]-knoty[m-1]) /(knotx[m]-knotx[m-1]);
    }else{
      if(m+1<=3)
        slope =(knoty[m+1]-knoty[m]) /(knotx[m+1]-knotx[m]);
      else
        slope =(knoty[m]-knoty[m-1]) /(knotx[m]-knotx[m-1]);
    }

    float c = slope *( r - knotx[m] ) + knoty[m];
    return c;
  }


  //////////////////////////////////////////////////////////////////////////////
  class GrayScaleBW implements ColorType {

    public GrayScaleBW(){
    }
    public void setRange(){}
    public float[] getColor(float val ){
      float[] hsv  = new float[3];
      hsv[0] = 1.0f;
      hsv[1] = 0.0f;
      if( val >= range[1] ){
        hsv[2] = 1.0f;
      }else if( val <= range[0] ){
        hsv[2] = 0.0f;
      }else {
        hsv[2] =  1.0f *(val-range[0])*drRangei;
      }
      rgba = convertHSVtoRGB( hsv );
      rgba[3]=getAlpha(val);
      return rgba;
    }
  }

  class GrayScaleWB implements ColorType {

    public GrayScaleWB(){
    }
    public void setRange(){}
    public float[] getColor(float val ){
      float[] hsv  = new float[3];
      hsv[0] = 1.0f;
      hsv[1] = 0.0f;
      if( val >= range[1] ){
        hsv[2] = 0.0f;
      }else if( val <= range[0] ){
        hsv[2] = 1.0f;
      }else {
        hsv[2] =  1.0f-1.0f*(val-range[0])*drRangei;
      }
      rgba = convertHSVtoRGB( hsv );
      rgba[3]=getAlpha(val);
      return rgba;
    }
  }

  class Rainbow implements ColorType {
    public Rainbow(){
    }
    public void setRange(){}
    public float[] getColor(float val ){
      float[] hsv  = new float[3];
      hsv[1] = 1.0f;
      hsv[2] = 1.0f;
      if( val >= range[1] ){
        hsv[0] = 0.0f;
      }else if( val <= range[0] ){
        hsv[0] = 240.0f;
      }else {
        hsv[0] = 240.0f *( 1.0f -(val-range[0])*drRangei);
      }
      rgba = convertHSVtoRGB( hsv );
      rgba[3]=getAlpha(val);
      return rgba;
    }
  }
  class CycleRainbow implements ColorType {
    public CycleRainbow(){
    }
    public void setRange(){}
    public float[] getColor(float val ){
      float[] hsv  = new float[3];
      hsv[1] = 1.0f;
      hsv[2] = 1.0f;

      int ndiv=5;
      float v=(val-range[0])*drRangei;
      if(v>1f)v=1f;
      if(v<0f)v=0f;
      int i=(int)(v*ndiv);
      if(i<0)i=0;
      if(i>ndiv)i=ndiv-1;
      hsv[0]=240.0f *( 1.f -(v-i/(float)ndiv)*ndiv);

      rgba = convertHSVtoRGB( hsv );
      rgba[3]=getAlpha(val);
      return rgba;
    }
  }

  class BlueGrayRed implements ColorType {
    private float[] rknotx = new float[5];
    private float[] gknotx = new float[5];
    private float[] bknotx = new float[5];
    private float[] rknoty = { 0.0f, 0.0f, 0.6f, 1.0f, 1.0f };
    private float[] gknoty = { 0.0f, 1.0f, 0.6f, 1.0f, 0.0f };
    private float[] bknoty = { 1.0f, 1.0f, 0.6f, 0.0f, 0.0f };
    float scale;
    public BlueGrayRed(float scale){
      this.scale=scale;
    }
    public void setRange(){
      rknotx[0] = range[0];
      rknotx[1] = scale*range[0];
      rknotx[2] = 0.0f;
      rknotx[3] = scale*range[1];
      rknotx[4] = range[1];

      gknotx[0] = range[0];
      gknotx[1] = scale*range[0];
      gknotx[2] = 0.0f;
      gknotx[3] = scale*range[1];
      gknotx[4] = range[1];

      bknotx[0] = range[0];
      bknotx[1] = scale*range[0];
      bknotx[2] = 0.0f;
      bknotx[3] = scale*range[1];
      bknotx[4] = range[1];
    }
    public float[] getColor(float val){
      if(val<= range[0] ){
        rgba[0] = rknoty[0];
        rgba[1] = gknoty[0];
        rgba[2] = bknoty[0];
      }else if(val>= range[1] ){
        rgba[0] = rknoty[4];
        rgba[1] = gknoty[4];
        rgba[2] = bknoty[4];
      }else {
        rgba[0] = calcColor( 4, rknotx, rknoty,val);
        rgba[1] = calcColor( 4, gknotx, gknoty,val);
        rgba[2] = calcColor( 4, bknotx, bknoty,val);
      }
      rgba[3] = getAlpha(val);
      return rgba;
    }
  }

  class BlueGreenRedYellow implements ColorType {
    private float[] rknotx = new float[4];
    private float[] gknotx = new float[4];
    private float[] bknotx = new float[4];
    private float[] rknoty = { 0.0f, 0.0f, 1.0f, 1.0f };
    private float[] gknoty = { 0.0f, 0.0f, 1.0f, 1.0f };
    private float[] bknoty = { 1.0f, 1.0f, 0.0f, 0.0f };

    public BlueGreenRedYellow(){
    }
    public void setRange(){
      rknotx[0] = range[0];
      rknotx[1] = range[0] + 0.01f*drRange;
      rknotx[2] = range[0] + 0.50f*drRange;
      rknotx[3] = range[1];

      gknotx[0] = range[0];
      gknotx[1] = range[0] + 0.66f*drRange;
      gknotx[2] = range[0] + 0.99f*drRange;
      gknotx[3] = range[1];

      bknotx[0] = range[0];
      bknotx[1] = range[0] + 0.33f*drRange;
      bknotx[2] = range[0] + 0.66f*drRange;
      bknotx[3] = range[1];
    }
    public float[] getColor(float val){
      if(val< range[0] ){
        rgba[0] = rknoty[0];
        rgba[1] = gknoty[0];
        rgba[2] = bknoty[0];
      }
      else if(val> range[1] ){
        rgba[0] = rknoty[3];
        rgba[1] = gknoty[3];
        rgba[2] = bknoty[3];
      }
      else {
        rgba[0] = calcColor( 3, rknotx, rknoty, val );
        rgba[1] = calcColor( 3, gknotx, gknoty, val );
        rgba[2] = calcColor( 3, bknotx, bknoty, val );
      }
      rgba[3] = getAlpha(val);
      return rgba;
    }
  }


  class DiscreteBlueGreenYellowRed implements ColorType {
    private float[] rknoty = { 0.0f, 0.0f, 1.0f, 1.0f };
    private float[] gknoty = { 0.0f, 1.0f, 1.0f, 0.0f };
    private float[] bknoty = { 1.0f, 1.0f, 0.0f, 0.0f };

    public DiscreteBlueGreenYellowRed(){
    }
    public void setRange(){
    }
    public float[] getColor(float val){
      float vs=(val-range[0])*drRangei;
      int i=(int)Math.floor(vs*4.f);
      if(i>=3){
        rgba[0] = rknoty[3];
        rgba[1] = gknoty[3];
        rgba[2] = bknoty[3];
      }else if(i<=0){
        rgba[0] = rknoty[0];
        rgba[1] = gknoty[0];
        rgba[2] = bknoty[0];
      }else{
        rgba[0] = rknoty[i];
        rgba[1] = gknoty[i];
        rgba[2] = bknoty[i];
      }
      rgba[3] = getAlpha(val);
      return rgba;
    }
  }
  class DiscreteBlueGreenRed implements ColorType {
    private float[] rknoty = { 0.0f,  0.0f, 1.0f };
    private float[] gknoty = { 0.0f,  1.0f, 0.0f };
    private float[] bknoty = { 1.0f,  0.0f, 0.0f };

    public DiscreteBlueGreenRed(){
    }
    public void setRange(){
    }
    public float[] getColor(float val){
      float vs=(val-range[0])*drRangei;
      int i=(int)Math.floor(vs*3.f);
      if(i>=2){
        rgba[0] = rknoty[2];
        rgba[1] = gknoty[2];
        rgba[2] = bknoty[2];
      }else if(i<=0){
        rgba[0] = rknoty[0];
        rgba[1] = gknoty[0];
        rgba[2] = bknoty[0];
      }else{
        rgba[0] = rknoty[i];
        rgba[1] = gknoty[i];
        rgba[2] = bknoty[i];
      }
      rgba[3] = getAlpha(val);
      return rgba;
    }
  }


}

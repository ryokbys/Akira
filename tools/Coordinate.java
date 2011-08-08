package tools;

/**
 * translate Cartesian to polar coordinate
 */
public class Coordinate {

  public Coordinate(){
  }


  static float H_PI = 180.0f/(float)Math.PI;
  static double safety = 1.0e-10f;

  /**
   * 直交系(xyz)から極座標系(r,theta,phi)への変換
   */
  public static float[] xyz2rtp( float x, float y, float z ){
    double rxy2, rxy, r2;
    float[] r = new float[3];

    rxy2 = x*x + y*y;
    r2 = rxy2 + z*z;
    if( r2 <= safety ){
      r[0] = 0.0f;
      r[1] = 0.0f;
      r[2] = 0.0f;
    }else if( rxy2 <= safety ){
      r[0] = (float)Math.sqrt(r2);
      if( z == 0.0f )r[1] = 0.0f;
      else if( z > 0.0f ) r[1] = 0.0f;
      else  r[1] = (float)Math.PI;
      r[2] = 0.0f;
    }else {
      r[0] = (float)Math.sqrt(r2);
      rxy = (float)Math.sqrt(rxy2);
      r[1] = (float)Math.acos(z/r[0])*H_PI;
      r[2] = (float)Math.acos(x/rxy)*H_PI;
      if( y < 0.0f ) r[2] = 360.0f - r[2];
    }
    return r;
  }
}

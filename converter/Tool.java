package converter;

import tools.*;

public class Tool{

  /**
   * multiple h-matrix
   */

  /**
   * check region: slice
   * カットされる原子ならば，負のtagが返される
   */
  public static int cutRange(int tag,float[] ra, float[][] h, char d, float min, float max ){
    //if atom is out of rendring region, set tag to minus
    int k=0;
    if( d == 'x' ) k = 0;
    else if( d == 'y' ) k = 1;
    else if( d == 'z' ) k = 2;

    if( min < max ){
      if(ra[k] < min*h[k][k] || ra[k] > max*h[k][k]) return -Math.abs(tag);
      else return tag;
    }else{
      if(ra[k] < min*h[k][k] && ra[k] > max*h[k][k])return -Math.abs(tag);
      else return tag;
    }
  }

  /**
   * check region: sphere
   * @return tag
   * カットされる原子ならば，負のtagが返される
   */
  public static int cutShepre(int tag,float[] ra, float[][]h, float[] cent, float r ){
    //if atom is out of rendring region, set tag to minus
    float d2;
    float[] dr = new float[3];
    float r2 = r*r;

    //inputed center is not scaled.
    float[] center=MDMath.mulH(h, cent);

    if( r > 0.0 ){
      //delete outside
      for( int k=0; k<3; k++ ) dr[k] = ra[k] - center[k];
      d2 = dr[0]*dr[0] +dr[1]*dr[1] +dr[2]*dr[2];
      if( d2 > r2 ) return -Math.abs(tag);
      else return tag;
    }else{
      //delete inside (Is this need?)
      for( int k=0; k<3; k++ ) dr[k] = ra[k] - center[k];
      d2 = dr[0]*dr[0] +dr[1]*dr[1] +dr[2]*dr[2];
      if( d2 < r2 ) return - Math.abs(tag);
      else return tag;
    }
  }

}

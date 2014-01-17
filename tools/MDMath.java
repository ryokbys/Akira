package tools;

public class MDMath{
  
  public static float[] mulH( float[][] h, float[] v ){
    float[] vt= new float[3];
    for(int k=0; k<3; k++)
      vt[k] = (h[k][0]*v[0] +h[k][1]*v[1] +h[k][2]*v[2]);
    return vt;
  }

  private float[] mulH( float[][] h,float x, float y, float z){
    float[] out = new float[3];
    for(int k=0; k<3; k++)
      out[k] = (h[k][0]*x +h[k][1]*y +h[k][2]*z);

    return out;
  }

  public static float pbc( float x ){
    if( x >= 1.f ) return x -1.f;
    if( x <  0.f ) return x +1.f;
    return x;
  }
}

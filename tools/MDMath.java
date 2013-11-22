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
      out[k] = (h[0][k]*x +h[1][k]*y +h[2][k]*z);

    return out;
  }

  public static float pbc( float x ){
    if( x >= 1.f ) x= x -1.f;
    if( x <  0.f ) x= x +1.f;
    return x;
  }
}

package tools;

public class MDMath{
  
  public static float[] mulH( float[][] h, float[] v ){
    float[] vt= new float[3];
    for(int k=0; k<3; k++)
      vt[k] = (h[k][0]*v[0] +h[k][1]*v[1] +h[k][2]*v[2]);
    return vt;
  }
}

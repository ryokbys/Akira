package data;

public class Bond{
  public static float maxBondLength;

  public float length;
  public float theta;
  public float phi;

  public Bond( float length, float theta, float phi ){
    this.length= length;
    this.theta = theta;
    this.phi   = phi;
  }

  public int getSizeByByte(){
    return 4 // length
      +4 // theta
      +4; // phi
  }
}

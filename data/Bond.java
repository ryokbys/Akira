package data;

public class Bond{
  public int i;
  public int j;
  public float[] origin=new float[3];
  public float length;
  public float theta;
  public float phi;

  Bond(int i, int j, float[] org, float l, float t, float p){
    this.i=i;
    this.j=j;
    for(int k=0;k<3;k++)this.origin[k]=org[k];
    this.length=l;
    this.theta=t;
    this.phi=p;
  }
}

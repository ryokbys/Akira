package tools.DelaunayTriangle.voxel;


//メタボールのクラス
//中心点(Point3)と、幅を与えます
public class MetaBall {
  //coodination point and Q
  double[] myPoint;
  double myQ;
  double myExpA,myExpB;
  static final double sqrt2PI = Math.sqrt(2*Math.PI);

  //constructor
  MetaBall() {
  }
  MetaBall(double[] aPoint, double aQ) {
    setPoint(aPoint);
    setQ(aQ);
  }
  //Parameter set
  void setPoint(double[] aPoint) {
    myPoint = aPoint;
  }
  void setQ(double aQ) {
    myQ = aQ;
    myExpA = (1 / myQ*myQ*sqrt2PI);
    myExpB = 2*myQ*myQ;
  }

  //value at Point
  double value(double[] aPoint) {
    double dSquare=distanceSquared(myPoint,aPoint);
    return myExpA * Math.exp(-dSquare/myExpB);
  }

  private static double distanceSquared(double[] a,double[] b){
    double x=b[0]-a[0];
    double y=b[1]-a[1];
    double z=b[2]-a[2];
    return (x*x+y*y+z*z);
  }
}

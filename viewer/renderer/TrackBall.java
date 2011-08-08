package viewer.renderer;

import java.util.*;

public class TrackBall {

  public boolean mode;

  static float TRACKBALLSIZE = 0.8f;

  public TrackBall(){
    mode = false;
  }

  public void vzero( float[] v ){
    for( int k=0; k<3; k++ ) v[k] = 0.0f;
  }

  public void vset( float[] v,float x, float y, float z ){
    v[0] = x;
    v[1] = y;
    v[2] = z;
  }

  public void vsub( float[] src1, float[] src2, float[] dst ){
    for( int k=0; k<3; k++ ) dst[k] = src1[k] - src2[k];

  }

  public void vcopy( float[] src, float[] dst ){
    for( int k=0; k<3; k++ ) dst[k] = src[k];
  }

  public void vcross( float[] v1, float[] v2, float[] cross ){
    float[] temp = new float[3];
    temp[0] = (v1[1]*v2[2]) - (v1[2]*v2[1]);
    temp[1] = (v1[2]*v2[0]) - (v1[0]*v2[2]);
    temp[2] = (v1[0]*v2[1]) - (v1[1]*v2[0]);
    vcopy( temp, cross );
  }

  public float vlength( float[] v ){
    double v2 = v[0]*v[0] + v[1]*v[1] + v[2]*v[2];
    return (float)Math.sqrt( v2 );
  }
  public void vscale( float[] v, float div ){
    for( int k=0; k<3; k++ ) v[k] *= div;
  }

  public void vnormal( float[] v ){
    vscale( v, 1.0f/vlength(v) );
  }

  public float vdot( float[] v1, float[] v2 ){
    return (v1[0]*v2[0] + v1[1]*v2[1] + v1[2]*v2[2]);
  }

  public void vadd( float[] src1, float[] src2, float[] dst ){
    for( int k=0; k<3; k++ ) dst[k] = src1[k] + src2[k];

  }

  public void trackball( float[] q,
                         float p1x,float p1y,
                         float p2x,float p2y ){
    float[] a = new float[3];
    float phi;
    float[] p1, p2, d;
    p1 = new float[3];
    p2 = new float[3];
    d  = new float[3];


    if( p1x == p2x && p1y == p2y ){
      vzero( q );
      q[3] = 1.0f;
      return;
    }

    vset( p1, p1x, p1y,
          tb_project_to_sphere(TRACKBALLSIZE,p1x,p1y) );
    vset( p2, p2x, p2y,
          tb_project_to_sphere(TRACKBALLSIZE,p2x,p2y) );
    vcross( p2, p1, a );
    vsub( p1, p2, d );
    float t = vlength(d) / (2.0f*TRACKBALLSIZE);

    if( t > 1.0f )t = 1.0f;
    if( t < -1.0f )t = -1.0f;

    phi = 2.0f * (float)Math.asin(t);
    axis_to_quat( a, phi, q );
  }

  public void axis_to_quat( float[] a, float phi, float[] q ){
    vnormal( a );
    vcopy( a, q );
    vscale( q, (float)Math.sin(phi/2.0f) );
    q[3] =(float)Math.cos(phi/2.0f);
  }

  public float tb_project_to_sphere( float r, float x, float y ){
    float z;
    float d = (float)Math.sqrt(x*x + y*y);
    if( d < r * 0.70710678118654752440f ){
      z = (float)Math.sqrt(r*r - d*d);
    }else {
      float t = r/1.41421356237309504880f;
      z = t*t / d;
    }
    return z;
  }

  final int RENORMCOUNT = 97;
  int count = 0;
  public float[] add_quats( float[] q1, float[] q2 ){

    float[] dest = new float[4];
    float[] t1, t2, t3;
    float[] tf;

    t1 = new float[4];
    t2 = new float[4];
    t3 = new float[4];
    tf = new float[4];

    vcopy( q1, t1 );
    vscale( t1, q2[3] );
    vcopy( q2, t2 );
    vscale( t2, q1[3] );

    vcross( q2, q1, t3 );
    vadd( t1, t2, tf );
    vadd( t3, tf, tf );
    tf[3] = q1[3]*q2[3] - vdot(q1,q2);

    for( int k=0; k<4; k++ ) dest[k] = tf[k];

    if( ++count > RENORMCOUNT ){
      count = 0;
      dest = normalize_quat( dest );
    }

    return dest;
  }

  public float[] normalize_quat( float[] q ){
    float mag = (q[0]*q[0] + q[1]*q[1] + q[2]*q[2] + q[3]*q[3]);
    for( int i=0; i<4; i++ ) q[i] /= mag;
    return q;
  }

  public void build_rotmatrix( float[] m,float[] q ){
    m[0] = 1.0f - 2.0f*(q[1]*q[1] + q[2]*q[2]);
    m[1] = 2.0f*(q[0]*q[1] - q[2]*q[3]);
    m[2] = 2.0f*(q[2]*q[0] + q[1]*q[3]);
    m[3] = 0.0f;

    m[4] = 2.0f*(q[0]*q[1] + q[2]*q[3]);
    m[5] = 1.0f - 2.0f*(q[2]*q[2] + q[0]*q[0]);
    m[6] = 2.0f*(q[1]*q[2] - q[0]*q[3]);
    m[7] = 0.0f;

    m[8] = 2.0f*(q[2]*q[0] - q[1]*q[3]);
    m[9] = 2.0f*(q[1]*q[2] + q[0]*q[3]);
    m[10] = 1.0f - 2.0f*(q[1]*q[1] + q[0]*q[0]);
    m[11] = 0.0f;

    m[12] = 0.0f;
    m[13] = 0.0f;
    m[14] = 0.0f;
    m[15] = 1.0f;
  }
}

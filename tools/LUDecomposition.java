package tools;

import tools.*;
import java.io.*;

/**
 * LU decomposition
 */
public class LUDecomposition {
  private LUDecomposition(){
  }

  public static double lu( double[][] a, int[] ip ){
    int n = a.length;
    double[] weight = new double[n];

    for( int k=0; k<n; k++ ){
      ip[k] = k;
      double u = 0;

      for( int j=0; j<n; j++ ){
        double t = Math.abs( a[k][j] );
        if( t > u ) u = t;
      }

      if( u == 0.0 ) return 0;
      weight[k] = 1 / u;
    }

    double det = 1.0;
    for( int k=0; k<n; k++ ){
      double u = 0.0;
      int m = 0;
      for( int i=k; i<n; i++ ){
        int ii = ip[i];
        double t = Math.abs( a[ii][k] ) * weight[ii];
        if( t > u ){
          u = t;
          m = i;
        }
      }
      int ik = ip[m];
      if( m != k ){
        ip[m] = ip[k];
        ip[k] = ik;
        det = -det;
      }
      u = a[ik][k];
      det *= u;
      if( u == 0 ) return 0;
      for( int i=k+1; i<n; i++ ){
        int ii = ip[i];
        double t = ( a[ii][k] /= u );
        for( int j=k+1; j<n; j++ ){
          a[ii][j] -= t * a[ik][j];
        }
      }
    }
    return det;
  }

  public static double lu( float[][] a, int[] ip ){
    int n = a.length;
    double[] weight = new double[n];

    for( int k=0; k<n; k++ ){
      ip[k] = k;
      double u = 0;

      for( int j=0; j<n; j++ ){
        double t = Math.abs( a[k][j] );
        if( t > u ) u = t;
      }

      if( u == 0.0 ) return 0;
      weight[k] = 1 / u;
    }

    double det = 1.0;
    for( int k=0; k<n; k++ ){
      double u = 0.0;
      int m = 0;
      for( int i=k; i<n; i++ ){
        int ii = ip[i];
        double t = Math.abs( a[ii][k] ) * weight[ii];
        if( t > u ){
          u = t;
          m = i;
        }
      }
      int ik = ip[m];
      if( m != k ){
        ip[m] = ip[k];
        ip[k] = ik;
        det = -det;
      }
      u = a[ik][k];
      det *= u;
      if( u == 0 ) return 0;
      for( int i=k+1; i<n; i++ ){
        int ii = ip[i];
        double t = ( a[ii][k] /= u );
        for( int j=k+1; j<n; j++ ){
          a[ii][j] -= t * a[ik][j];
        }
      }
    }
    return det;
  }
}

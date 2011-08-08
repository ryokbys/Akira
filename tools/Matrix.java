package tools;

import tools.*;

/**
 * calculate inverse matrix
 */
public class Matrix {

  static final float eps=(float)1e-4;

  public static boolean inv(float[][] mat, float[][] inv){
    int n = mat.length;
    if(n==3){
      return mat3inv(mat,inv);
    }else{
      return false;
    }
  }
  public static boolean inv(double[][] mat, double[][] inv){
    int n = mat.length;
    if(n==3){
      return mat3inv(mat,inv);
    }else if(n==4){
      return mat4inv(mat,inv);
    }else{
      return false;
    }
  }

  private static float mat3Determinant(float[][] mat){
    return mat[0][0]*mat[1][1]*mat[2][2]
      +mat[0][2]*mat[1][0]*mat[2][1]
      +mat[0][1]*mat[1][2]*mat[2][0]
      -mat[0][0]*mat[2][1]*mat[1][2]
      -mat[0][2]*mat[1][1]*mat[2][0]
      -mat[0][1]*mat[1][0]*mat[2][2];
  }
  private static boolean mat3inv(float[][] mat, float[][] inv){

    float det=mat3Determinant(mat);

    if(Math.abs(det)<eps){
      //System.out.println("mat inv is not exist");
      return false;
    }


    inv[0][0]=(mat[1][1]*mat[2][2]-mat[1][2]*mat[2][1])/det;
    inv[0][1]=(mat[0][2]*mat[2][1]-mat[0][1]*mat[2][2])/det;
    inv[0][2]=(mat[0][1]*mat[1][2]-mat[0][2]*mat[1][1])/det;

    inv[1][0]=(mat[1][2]*mat[2][0]-mat[1][0]*mat[2][2])/det;
    inv[1][1]=(mat[0][0]*mat[2][2]-mat[0][2]*mat[2][0])/det;
    inv[1][2]=(mat[0][2]*mat[1][0]-mat[0][0]*mat[1][2])/det;

    inv[2][0]=(mat[1][0]*mat[2][1]-mat[1][1]*mat[2][0])/det;
    inv[2][1]=(mat[0][1]*mat[2][0]-mat[0][0]*mat[2][1])/det;
    inv[2][2]=(mat[0][0]*mat[1][1]-mat[0][1]*mat[1][0])/det;


    //check
    /*
     * float aa;
     * for(int i=0;i<3;i++){
     *     for(int j=0;j<3;j++){
     *         aa=0.f;
     *         for(int k=0;k<3;k++)
     *             aa+=mat[i][k]*inv[k][j];
     *         System.out.println(String.format( "%d %d %f",i,j,aa));
     *     }
     * }
     * System.out.println("");
     * for(int i=0;i<3;i++){
     *   for(int j=0;j<3;j++)System.out.print(String.format("%f",inv[i][j]));
     *    System.out.println();
     * }
     */
    return true;
  }
  private static double mat3Determinant(double[][] mat){
    return mat[0][0]*mat[1][1]*mat[2][2]
      +mat[0][2]*mat[1][0]*mat[2][1]
      +mat[0][1]*mat[1][2]*mat[2][0]
      -mat[0][0]*mat[2][1]*mat[1][2]
      -mat[0][2]*mat[1][1]*mat[2][0]
      -mat[0][1]*mat[1][0]*mat[2][2];
  }
  private static boolean mat3inv(double[][] mat, double[][] inv){

    double det=mat3Determinant(mat);

    if(Math.abs(det)<eps){
      //System.out.println("mat inv is not exist");
      return false;
    }


    inv[0][0]=(mat[1][1]*mat[2][2]-mat[1][2]*mat[2][1])/det;
    inv[0][1]=(mat[0][2]*mat[2][1]-mat[0][1]*mat[2][2])/det;
    inv[0][2]=(mat[0][1]*mat[1][2]-mat[0][2]*mat[1][1])/det;

    inv[1][0]=(mat[1][2]*mat[2][0]-mat[1][0]*mat[2][2])/det;
    inv[1][1]=(mat[0][0]*mat[2][2]-mat[0][2]*mat[2][0])/det;
    inv[1][2]=(mat[0][2]*mat[1][0]-mat[0][0]*mat[1][2])/det;

    inv[2][0]=(mat[1][0]*mat[2][1]-mat[1][1]*mat[2][0])/det;
    inv[2][1]=(mat[0][1]*mat[2][0]-mat[0][0]*mat[2][1])/det;
    inv[2][2]=(mat[0][0]*mat[1][1]-mat[0][1]*mat[1][0])/det;

    //check
    /*
     * float aa;
     * for(int i=0;i<3;i++){
     *     for(int j=0;j<3;j++){
     *         aa=0.f;
     *         for(int k=0;k<3;k++)
     *             aa+=mat[i][k]*inv[k][j];
     *         System.out.println(String.format( "%d %d %f",i,j,aa));
     *     }
     * }
     * System.out.println("");
     * for(int i=0;i<3;i++){
     *   for(int j=0;j<3;j++)System.out.print(String.format("%f",inv[i][j]));
     *    System.out.println();
     * }
     */
    return true;
  }

  private static double mat4Determinant(double[][] mat){
    return mat[0][0]*mat[1][1]*mat[2][2]*mat[3][3]
      + mat[0][0]*mat[1][2]*mat[2][3]*mat[3][1]
      + mat[0][0]*mat[1][3]*mat[2][1]*mat[3][2]
      + mat[0][1]*mat[1][0]*mat[2][3]*mat[3][2]
      + mat[0][1]*mat[1][2]*mat[2][0]*mat[3][3]
      + mat[0][1]*mat[1][3]*mat[2][2]*mat[3][0]
      + mat[0][2]*mat[1][0]*mat[2][1]*mat[3][3]
      + mat[0][2]*mat[1][1]*mat[2][3]*mat[3][0]
      + mat[0][2]*mat[1][3]*mat[2][0]*mat[3][1]
      + mat[0][3]*mat[1][0]*mat[2][2]*mat[3][1]
      + mat[0][3]*mat[1][1]*mat[2][0]*mat[3][2]
      + mat[0][3]*mat[1][2]*mat[2][1]*mat[3][0]
      - mat[0][0]*mat[1][1]*mat[2][3]*mat[3][2]
      - mat[0][0]*mat[1][2]*mat[2][1]*mat[3][3]
      - mat[0][0]*mat[1][3]*mat[2][2]*mat[3][1]
      - mat[0][1]*mat[1][0]*mat[2][2]*mat[3][3]
      - mat[0][1]*mat[1][2]*mat[2][3]*mat[3][0]
      - mat[0][1]*mat[1][3]*mat[2][0]*mat[3][2]
      - mat[0][2]*mat[1][0]*mat[2][3]*mat[3][1]
      - mat[0][2]*mat[1][1]*mat[2][0]*mat[3][3]
      - mat[0][2]*mat[1][3]*mat[2][1]*mat[3][0]
      - mat[0][3]*mat[1][0]*mat[2][1]*mat[3][2]
      - mat[0][3]*mat[1][1]*mat[2][2]*mat[3][0]
      - mat[0][3]*mat[1][2]*mat[2][0]*mat[3][1];
  }
  private static boolean mat4inv(double[][] mat, double[][] inv){
    double det=mat4Determinant(mat);
    if(Math.abs(det)<eps){
      //System.out.println(String.format("det= %f",det));
      //System.out.println("mat inv is not exist");
      return false;
    }
    inv[0][0] = (mat[1][1]*mat[2][2]*mat[3][3]
                 + mat[1][2]*mat[2][3]*mat[3][1]
                 + mat[1][3]*mat[2][1]*mat[3][2]
                 - mat[1][1]*mat[2][3]*mat[3][2]
                 - mat[1][2]*mat[2][1]*mat[3][3]
                 - mat[1][3]*mat[2][2]*mat[3][1])/det;
    inv[0][1] = (mat[0][1]*mat[2][3]*mat[3][2]
                 + mat[0][2]*mat[2][1]*mat[3][3]
                 + mat[0][3]*mat[2][2]*mat[3][1]
                 - mat[0][1]*mat[2][2]*mat[3][3]
                 - mat[0][2]*mat[2][3]*mat[3][1]
                 - mat[0][3]*mat[2][1]*mat[3][2])/det;
    inv[0][2] = (mat[0][1]*mat[1][2]*mat[3][3]
                 + mat[0][2]*mat[1][3]*mat[3][1]
                 + mat[0][3]*mat[1][1]*mat[3][2]
                 - mat[0][1]*mat[1][3]*mat[3][2]
                 - mat[0][2]*mat[1][1]*mat[3][3]
                 - mat[0][3]*mat[1][2]*mat[3][1])/det;
    inv[0][3] = (mat[0][1]*mat[1][3]*mat[2][2]
                 + mat[0][2]*mat[1][1]*mat[2][3]
                 + mat[0][3]*mat[1][2]*mat[2][1]
                 - mat[0][1]*mat[1][2]*mat[2][3]
                 - mat[0][2]*mat[1][3]*mat[2][1]
                 - mat[0][3]*mat[1][1]*mat[2][2])/det;
    inv[1][0] = (mat[1][0]*mat[2][3]*mat[3][2]
                 + mat[1][2]*mat[2][0]*mat[3][3]
                 + mat[1][3]*mat[2][2]*mat[3][0]
                 - mat[1][0]*mat[2][2]*mat[3][3]
                 - mat[1][2]*mat[2][3]*mat[3][0]
                 - mat[1][3]*mat[2][0]*mat[3][2])/det;
    inv[1][1] = (mat[0][0]*mat[2][2]*mat[3][3]
                 + mat[0][2]*mat[2][3]*mat[3][0]
                 + mat[0][3]*mat[2][0]*mat[3][2]
                 - mat[0][0]*mat[2][3]*mat[3][2]
                 - mat[0][2]*mat[2][0]*mat[3][3]
                 - mat[0][3]*mat[2][2]*mat[3][0])/det;
    inv[1][2] = (mat[0][0]*mat[1][3]*mat[3][2]
                 + mat[0][2]*mat[1][0]*mat[3][3]
                 + mat[0][3]*mat[1][2]*mat[3][0]
                 - mat[0][0]*mat[1][2]*mat[3][3]
                 - mat[0][2]*mat[1][3]*mat[3][0]
                 - mat[0][3]*mat[1][0]*mat[3][2])/det;
    inv[1][3] = (mat[0][0]*mat[1][2]*mat[2][3]
                 + mat[0][2]*mat[1][3]*mat[2][0]
                 + mat[0][3]*mat[1][0]*mat[2][2]
                 - mat[0][0]*mat[1][3]*mat[2][2]
                 - mat[0][2]*mat[1][0]*mat[2][3]
                 - mat[0][3]*mat[1][2]*mat[2][0])/det;
    inv[2][0] = (mat[1][0]*mat[2][1]*mat[3][3]
                 + mat[1][1]*mat[2][3]*mat[3][0]
                 + mat[1][3]*mat[2][0]*mat[3][1]
                 - mat[1][0]*mat[2][3]*mat[3][1]
                 - mat[1][1]*mat[2][0]*mat[3][3]
                 - mat[1][3]*mat[2][1]*mat[3][0])/det;
    inv[2][1] = (mat[0][0]*mat[2][3]*mat[3][1]
                 + mat[0][1]*mat[2][0]*mat[3][3]
                 + mat[0][3]*mat[2][1]*mat[3][0]
                 - mat[0][0]*mat[2][1]*mat[3][3]
                 - mat[0][1]*mat[2][3]*mat[3][0]
                 - mat[0][3]*mat[2][0]*mat[3][1])/det;
    inv[2][2] = (mat[0][0]*mat[1][1]*mat[3][3]
                 + mat[0][1]*mat[1][3]*mat[3][0]
                 + mat[0][3]*mat[1][0]*mat[3][1]
                 - mat[0][0]*mat[1][3]*mat[3][1]
                 - mat[0][1]*mat[1][0]*mat[3][3]
                 - mat[0][3]*mat[1][1]*mat[3][0])/det;
    inv[2][3] = (mat[0][0]*mat[1][3]*mat[2][1]
                 + mat[0][1]*mat[1][0]*mat[2][3]
                 + mat[0][3]*mat[1][1]*mat[2][0]
                 - mat[0][0]*mat[1][1]*mat[2][3]
                 - mat[0][1]*mat[1][3]*mat[2][0]
                 - mat[0][3]*mat[1][0]*mat[2][1])/det;
    inv[3][0] = (mat[1][0]*mat[2][2]*mat[3][1]
                 + mat[1][1]*mat[2][0]*mat[3][2]
                 + mat[1][2]*mat[2][1]*mat[3][0]
                 - mat[1][0]*mat[2][1]*mat[3][2]
                 - mat[1][1]*mat[2][2]*mat[3][0]
                 - mat[1][2]*mat[2][0]*mat[3][1])/det;
    inv[3][1] = (mat[0][0]*mat[2][1]*mat[3][2]
                 + mat[0][1]*mat[2][2]*mat[3][0]
                 + mat[0][2]*mat[2][0]*mat[3][1]
                 - mat[0][0]*mat[2][2]*mat[3][1]
                 - mat[0][1]*mat[2][0]*mat[3][2]
                 - mat[0][2]*mat[2][1]*mat[3][0])/det;
    inv[3][2] = (mat[0][0]*mat[1][2]*mat[3][1]
                 + mat[0][1]*mat[1][0]*mat[3][2]
                 + mat[0][2]*mat[1][1]*mat[3][0]
                 - mat[0][0]*mat[1][1]*mat[3][2]
                 - mat[0][1]*mat[1][2]*mat[3][0]
                 - mat[0][2]*mat[1][0]*mat[3][1])/det;
    inv[3][3] = (mat[0][0]*mat[1][1]*mat[2][2]
                 + mat[0][1]*mat[1][2]*mat[2][0]
                 + mat[0][2]*mat[1][0]*mat[2][1]
                 - mat[0][0]*mat[1][2]*mat[2][1]
                 - mat[0][1]*mat[1][0]*mat[2][2]
                 - mat[0][2]*mat[1][1]*mat[2][0])/det;


    /*
     * //check
     * float aa;
     * for(int i=0;i<4;i++){
     *     for(int j=0;j<4;j++){
     *         aa=0.f;
     *         for(int k=0;k<4;k++)
     *             aa+=mat[i][k]*inv[k][j];
     *         System.out.println(String.format( "%d %d %.4f",i,j,aa));
     *     }
     * }
     * System.out.println("original");
     * for(int i=0;i<4;i++){
     *   for(int j=0;j<4;j++)System.out.print(String.format("%.4f ",mat[i][j]));
     *    System.out.println();
     * }
     * System.out.println("inv");
     * for(int i=0;i<4;i++){
     *   for(int j=0;j<4;j++)System.out.print(String.format("%.4f ",inv[i][j]));
     *    System.out.println();
     * }
     */
    return true;
  }


  public static double inverse( double[][] mat, double[][] imat ){
    int n = mat.length;
    int[] ivec = new int[n];

    double det = LUDecomposition.lu( mat, ivec );
    if( det == 0 ){
      System.out.println("Error: det=0");
      return 0;
    }
    double t;
    for( int k=0; k<n; k++ ){
      for( int i=0; i<n; i++ ){
        int ii = ivec[i];
        if( ii == k ){
          t = 1.0;
        }
        else {
          t = 0.0;
        }
        for( int j=0; j<i; j++ ){
          t -= mat[ii][j] * imat[j][k];
        }
        imat[i][k] = t;
      }
      for( int i=n-1; i>=0; i-- ){
        int ii = ivec[i];
        t = imat[i][k];
        for( int j=i+1; j<n; j++ ){
          t -= mat[ii][j] * imat[j][k];
        }
        imat[i][k] = (t/mat[ii][i]);
      }
    }
    return det;
  }




}

package tools.DelaunayTriangle.voxel;

public class MetaBallVoxel implements Voxel {
  //ボールの位置と数
  int metaBallCount = 3;
  double valueMemory[][][];
  int myFieldSize;
  MetaBall metaBalls[] = {
    new MetaBall(new double[]{0.4,-0.3, 0.4}, 0.4) ,
    new MetaBall(new double[]{0.3, 0.5, 0.0}, 0.5) ,
    new MetaBall(new double[]{-0.6,-0.2, 0.2}, 0.3) ,
  };
  double myValueMax = 0.0;

  MetaBallVoxel(int aSize) {
    super();
    myFieldSize = aSize;
    valueMemory = new double[myFieldSize+1][myFieldSize+1][myFieldSize+1];
    for (int x=0; x<=myFieldSize; x++) {
      for (int y=0; y<=myFieldSize; y++) {
        for (int z=0; z<=myFieldSize; z++) {
          valueMemory[x][y][z] = meshRawValue(x,y,z);
          myValueMax =
            Math.max(myValueMax, valueMemory[x][y][z]);
        }
      }
    }
    for (int x=0; x<=myFieldSize; x++)
      for (int y=0; y<=myFieldSize; y++)
        for (int z=0; z<=myFieldSize; z++)
          valueMemory[x][y][z] = valueMemory[x][y][z]/myValueMax;
  }

  //メッシュフィールドの値
  //x,y,zは0からfieldSize()までの整数値
  //返り値は0から1までの値
  double meshField(int x,int y, int z) {
    return valueMemory[x][y][z];
  }
  double meshRawValue(int x, int y, int z) {
    double retVal = 0.0;
    double[] aP = new double[]{
             -1.0 + 2.0*(double)x/(double)(myFieldSize-1),
             -1.0 + 2.0*(double)y/(double)(myFieldSize-1),
             -1.0 + 2.0*(double)z/(double)(myFieldSize-1)};
    for (int i=0; i<metaBallCount; i++)
      retVal += metaBalls[i].value(aP);
    return retVal;
  }

  int fieldSize() {
    return myFieldSize;
  }

  //@Override
  public double[][][] getVoxel() {
    return valueMemory;
  }

  //@Override
  public double[] getVoxelOrigin() {
    return new double[]{0,0,0};
  }

  //@Override
  public double[] getVoxelVector() {
    return new double[]{1,1,1};
  }

}

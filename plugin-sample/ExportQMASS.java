package plugin;
import java.io.*;
import viewer.viewConfigPanel.plugin.ExportPluginInterface;

public class ExportQMASS implements ExportPluginInterface {
  public String getSaveFileName(){
    return "qmass.inp";
  }
  public String getPluginName(){
    return "QMASS coord.";
  }
  public void exec(String saveFile,
                   float[][] h,
                   float[][] hinv,
                   int n,
                   float[][] r,
                   byte[] tag,
                   int[] vtag
                   ){

    FileWriter fw;
    BufferedWriter bw;
    PrintWriter pw;
    String str;


    String[] type={"H","He","Li","Be","B","C","N","O","F","Ne","Na","Mg","Al","Si","P","S","Kr","K","Ca"};

    // open
    try{
      fw = new FileWriter( saveFile);
      bw = new BufferedWriter( fw );
      pw = new PrintWriter( bw );



      int nv=0;
      for(int i=0;i<n;i++){
        if(vtag[i]<0)continue;
        nv++;
      }
      System.out.println(String.format("output Natom: %d",nv));

      pw.println(String.format("numver_atom = %d",nv));
      //pw.println(String.format("numver_element: %d",atoms.nTagMax));
      pw.println("numver_element: ??");
      pw.println("lattice_type = 2  !{1: lattice_parameter&angle, 2: vector}");
      pw.println("lattice_list ! A B C");
      pw.println(String.format("(%e %e %e)",h[0][0],h[0][1],h[0][2]));
      pw.println(String.format("(%e %e %e)",h[1][0],h[1][1],h[1][2]));
      pw.println(String.format("(%e %e %e)",h[2][0],h[2][1],h[2][2]));

      pw.println("atom_list (element, x,y,z, katm, iposfix)");
      //shpere as atom
      for(int i=0;i<n;i++){
        //skip
        if(vtag[i]<0)continue;

        float[] out = new float[3];
        for(int k=0; k<3; k++) out[k] =
                                 hinv[k][0]*r[i][0]+
                                 hinv[k][1]*r[i][1]+
                                 hinv[k][2]*r[i][2];
        pw.println(String.format("%s %e, %e, %e, %d, %d",type[(int)tag[i]],out[0],out[1],out[2],(int)tag[i],0));
      }

      pw.close();
      bw.close();
      fw.close();
    }catch( IOException e ){
      System.out.println("---> Failed to write mts file");
      //System.out.println(e.getMessage());
    }
  }
}

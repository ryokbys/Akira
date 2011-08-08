package tools;

import java.util.*;

/**
 * reading tools
 */

public class Exponent {
  private String str;
  public Exponent(){
  }

  public void setString( String str ){
    this.str = str;
  }

  public double getNumber(){
    int e, E;
    Tokens tokens = new Tokens();
    tokens.setString(str);
    e = this.str.indexOf("e");
    E = this.str.indexOf("E");
    if( e > -1 ){
      tokens.setDelim("e");
      String[] strnum = tokens.getTokens();
      if( strnum.length > 2 ){
        System.out.println(strnum.length );
      }
      else {
        return Double.parseDouble(strnum[0]) *
          Math.pow( 10, Double.parseDouble(strnum[1]) );
      }
    }
    else if( E > -1 ){
      tokens.setDelim("E");
      String[] strnum = tokens.getTokens();
      if( strnum.length > 2 ){
        System.out.println(strnum.length );
      }
      else {
        return Double.parseDouble(strnum[0]) *
          Math.pow( 10, Double.parseDouble(strnum[1]) );
      }
    }
    else {
      return Double.parseDouble( this.str );
    }
    return 0.0;
  }
}

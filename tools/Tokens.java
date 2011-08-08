package tools;

import java.util.*;
/**
 * separator tokens
 */

public class Tokens {
  private String string;
  private String delim;

  public Tokens(){
  }

  public String[] getTokens(){
    StringTokenizer st;
    st = new StringTokenizer( string, delim );
    String[] tokens = new String[st.countTokens()];
    int i=0;
    while ( st.hasMoreTokens() ){
      tokens[i] = st.nextToken();
      i++;
    }
    return tokens;
  }

  public void setString( String string ){
    this.string = string;
  }
  public void setDelim( String delim ){
    this.delim = delim;
  }
}

package com.sun.jmx.snmp.IPAcl;

class Token
{
  public int kind;
  public int beginLine;
  public int beginColumn;
  public int endLine;
  public int endColumn;
  public String image;
  public Token next;
  public Token specialToken;
  
  Token() {}
  
  public final String toString()
  {
    return image;
  }
  
  public static final Token newToken(int paramInt)
  {
    switch (paramInt)
    {
    }
    return new Token();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\Token.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
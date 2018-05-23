package com.sun.jmx.snmp.IPAcl;

class ParseException
  extends Exception
{
  private static final long serialVersionUID = -3695190720704845876L;
  protected boolean specialConstructor;
  public Token currentToken;
  public int[][] expectedTokenSequences;
  public String[] tokenImage;
  protected String eol = System.getProperty("line.separator", "\n");
  
  public ParseException(Token paramToken, int[][] paramArrayOfInt, String[] paramArrayOfString)
  {
    super("");
    specialConstructor = true;
    currentToken = paramToken;
    expectedTokenSequences = paramArrayOfInt;
    tokenImage = paramArrayOfString;
  }
  
  public ParseException()
  {
    specialConstructor = false;
  }
  
  public ParseException(String paramString)
  {
    super(paramString);
    specialConstructor = false;
  }
  
  public String getMessage()
  {
    if (!specialConstructor) {
      return super.getMessage();
    }
    String str1 = "";
    int i = 0;
    for (int j = 0; j < expectedTokenSequences.length; j++)
    {
      if (i < expectedTokenSequences[j].length) {
        i = expectedTokenSequences[j].length;
      }
      for (int k = 0; k < expectedTokenSequences[j].length; k++) {
        str1 = str1 + tokenImage[expectedTokenSequences[j][k]] + " ";
      }
      if (expectedTokenSequences[j][(expectedTokenSequences[j].length - 1)] != 0) {
        str1 = str1 + "...";
      }
      str1 = str1 + eol + "    ";
    }
    String str2 = "Encountered \"";
    Token localToken = currentToken.next;
    for (int m = 0; m < i; m++)
    {
      if (m != 0) {
        str2 = str2 + " ";
      }
      if (kind == 0)
      {
        str2 = str2 + tokenImage[0];
        break;
      }
      str2 = str2 + add_escapes(image);
      localToken = next;
    }
    str2 = str2 + "\" at line " + currentToken.next.beginLine + ", column " + currentToken.next.beginColumn + "." + eol;
    if (expectedTokenSequences.length == 1) {
      str2 = str2 + "Was expecting:" + eol + "    ";
    } else {
      str2 = str2 + "Was expecting one of:" + eol + "    ";
    }
    str2 = str2 + str1;
    return str2;
  }
  
  protected String add_escapes(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramString.length(); i++) {
      switch (paramString.charAt(i))
      {
      case '\000': 
        break;
      case '\b': 
        localStringBuffer.append("\\b");
        break;
      case '\t': 
        localStringBuffer.append("\\t");
        break;
      case '\n': 
        localStringBuffer.append("\\n");
        break;
      case '\f': 
        localStringBuffer.append("\\f");
        break;
      case '\r': 
        localStringBuffer.append("\\r");
        break;
      case '"': 
        localStringBuffer.append("\\\"");
        break;
      case '\'': 
        localStringBuffer.append("\\'");
        break;
      case '\\': 
        localStringBuffer.append("\\\\");
        break;
      default: 
        char c;
        if (((c = paramString.charAt(i)) < ' ') || (c > '~'))
        {
          String str = "0000" + Integer.toString(c, 16);
          localStringBuffer.append("\\u" + str.substring(str.length() - 4, str.length()));
        }
        else
        {
          localStringBuffer.append(c);
        }
        break;
      }
    }
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\ParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
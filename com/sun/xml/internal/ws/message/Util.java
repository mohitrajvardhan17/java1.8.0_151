package com.sun.xml.internal.ws.message;

public abstract class Util
{
  public Util() {}
  
  public static boolean parseBool(String paramString)
  {
    if (paramString.length() == 0) {
      return false;
    }
    int i = paramString.charAt(0);
    return (i == 116) || (i == 49);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
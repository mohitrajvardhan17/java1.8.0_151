package com.sun.org.apache.regexp.internal;

public class REUtil
{
  private static final String complexPrefix = "complex:";
  
  public REUtil() {}
  
  public static RE createRE(String paramString, int paramInt)
    throws RESyntaxException
  {
    if (paramString.startsWith("complex:")) {
      return new RE(paramString.substring("complex:".length()), paramInt);
    }
    return new RE(RE.simplePatternToFullRegularExpression(paramString), paramInt);
  }
  
  public static RE createRE(String paramString)
    throws RESyntaxException
  {
    return createRE(paramString, 0);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\regexp\internal\REUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
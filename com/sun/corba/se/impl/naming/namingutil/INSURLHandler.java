package com.sun.corba.se.impl.naming.namingutil;

public class INSURLHandler
{
  private static INSURLHandler insURLHandler = null;
  private static final int CORBALOC_PREFIX_LENGTH = 9;
  private static final int CORBANAME_PREFIX_LENGTH = 10;
  
  private INSURLHandler() {}
  
  public static synchronized INSURLHandler getINSURLHandler()
  {
    if (insURLHandler == null) {
      insURLHandler = new INSURLHandler();
    }
    return insURLHandler;
  }
  
  public INSURL parseURL(String paramString)
  {
    String str = paramString;
    if (str.startsWith("corbaloc:") == true) {
      return new CorbalocURL(str.substring(9));
    }
    if (str.startsWith("corbaname:") == true) {
      return new CorbanameURL(str.substring(10));
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\namingutil\INSURLHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
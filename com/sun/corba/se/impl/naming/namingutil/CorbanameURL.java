package com.sun.corba.se.impl.naming.namingutil;

import com.sun.corba.se.impl.logging.NamingSystemException;
import java.util.ArrayList;
import org.omg.CORBA.BAD_PARAM;

public class CorbanameURL
  extends INSURLBase
{
  private static NamingSystemException wrapper = NamingSystemException.get("naming");
  
  public CorbanameURL(String paramString)
  {
    String str1 = paramString;
    try
    {
      str1 = Utility.cleanEscapes(str1);
    }
    catch (Exception localException1)
    {
      badAddress(localException1);
    }
    int i = str1.indexOf('#');
    String str2 = null;
    if (i != -1)
    {
      str2 = "corbaloc:" + str1.substring(0, i) + "/";
    }
    else
    {
      str2 = "corbaloc:" + str1.substring(0, str1.length());
      if (str2.endsWith("/") != true) {
        str2 = str2 + "/";
      }
    }
    try
    {
      INSURL localINSURL = INSURLHandler.getINSURLHandler().parseURL(str2);
      copyINSURL(localINSURL);
      if ((i > -1) && (i < paramString.length() - 1))
      {
        int j = i + 1;
        String str3 = str1.substring(j);
        theStringifiedName = str3;
      }
    }
    catch (Exception localException2)
    {
      badAddress(localException2);
    }
  }
  
  private void badAddress(Throwable paramThrowable)
    throws BAD_PARAM
  {
    throw wrapper.insBadAddress(paramThrowable);
  }
  
  private void copyINSURL(INSURL paramINSURL)
  {
    rirFlag = paramINSURL.getRIRFlag();
    theEndpointInfo = ((ArrayList)paramINSURL.getEndpointInfo());
    theKeyString = paramINSURL.getKeyString();
    theStringifiedName = paramINSURL.getStringifiedName();
  }
  
  public boolean isCorbanameURL()
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\namingutil\CorbanameURL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
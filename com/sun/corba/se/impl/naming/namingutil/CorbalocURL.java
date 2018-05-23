package com.sun.corba.se.impl.naming.namingutil;

import com.sun.corba.se.impl.logging.NamingSystemException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class CorbalocURL
  extends INSURLBase
{
  static NamingSystemException wrapper = NamingSystemException.get("naming.read");
  
  public CorbalocURL(String paramString)
  {
    String str1 = paramString;
    if (str1 != null)
    {
      try
      {
        str1 = Utility.cleanEscapes(str1);
      }
      catch (Exception localException)
      {
        badAddress(localException);
      }
      int i = str1.indexOf('/');
      if (i == -1) {
        i = str1.length();
      }
      if (i == 0) {
        badAddress(null);
      }
      StringTokenizer localStringTokenizer = new StringTokenizer(str1.substring(0, i), ",");
      while (localStringTokenizer.hasMoreTokens())
      {
        String str2 = localStringTokenizer.nextToken();
        IIOPEndpointInfo localIIOPEndpointInfo = null;
        if (str2.startsWith("iiop:"))
        {
          localIIOPEndpointInfo = handleIIOPColon(str2);
        }
        else if (str2.startsWith("rir:"))
        {
          handleRIRColon(str2);
          rirFlag = true;
        }
        else if (str2.startsWith(":"))
        {
          localIIOPEndpointInfo = handleColon(str2);
        }
        else
        {
          badAddress(null);
        }
        if (!rirFlag)
        {
          if (theEndpointInfo == null) {
            theEndpointInfo = new ArrayList();
          }
          theEndpointInfo.add(localIIOPEndpointInfo);
        }
      }
      if (str1.length() > i + 1) {
        theKeyString = str1.substring(i + 1);
      }
    }
  }
  
  private void badAddress(Throwable paramThrowable)
  {
    throw wrapper.insBadAddress(paramThrowable);
  }
  
  private IIOPEndpointInfo handleIIOPColon(String paramString)
  {
    paramString = paramString.substring(4);
    return handleColon(paramString);
  }
  
  private IIOPEndpointInfo handleColon(String paramString)
  {
    paramString = paramString.substring(1);
    String str1 = paramString;
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "@");
    IIOPEndpointInfo localIIOPEndpointInfo = new IIOPEndpointInfo();
    int i = localStringTokenizer.countTokens();
    if ((i == 0) || (i > 2)) {
      badAddress(null);
    }
    if (i == 2)
    {
      String str2 = localStringTokenizer.nextToken();
      int k = str2.indexOf('.');
      if (k == -1) {
        badAddress(null);
      }
      try
      {
        localIIOPEndpointInfo.setVersion(Integer.parseInt(str2.substring(0, k)), Integer.parseInt(str2.substring(k + 1)));
        str1 = localStringTokenizer.nextToken();
      }
      catch (Throwable localThrowable2)
      {
        badAddress(localThrowable2);
      }
    }
    try
    {
      int j = str1.indexOf('[');
      if (j != -1)
      {
        String str3 = getIPV6Port(str1);
        if (str3 != null) {
          localIIOPEndpointInfo.setPort(Integer.parseInt(str3));
        }
        localIIOPEndpointInfo.setHost(getIPV6Host(str1));
        return localIIOPEndpointInfo;
      }
      localStringTokenizer = new StringTokenizer(str1, ":");
      if (localStringTokenizer.countTokens() == 2)
      {
        localIIOPEndpointInfo.setHost(localStringTokenizer.nextToken());
        localIIOPEndpointInfo.setPort(Integer.parseInt(localStringTokenizer.nextToken()));
      }
      else if ((str1 != null) && (str1.length() != 0))
      {
        localIIOPEndpointInfo.setHost(str1);
      }
    }
    catch (Throwable localThrowable1)
    {
      badAddress(localThrowable1);
    }
    Utility.validateGIOPVersion(localIIOPEndpointInfo);
    return localIIOPEndpointInfo;
  }
  
  private void handleRIRColon(String paramString)
  {
    if (paramString.length() != 4) {
      badAddress(null);
    }
  }
  
  private String getIPV6Port(String paramString)
  {
    int i = paramString.indexOf(']');
    if (i + 1 != paramString.length())
    {
      if (paramString.charAt(i + 1) != ':') {
        throw new RuntimeException("Host and Port is not separated by ':'");
      }
      return paramString.substring(i + 2);
    }
    return null;
  }
  
  private String getIPV6Host(String paramString)
  {
    int i = paramString.indexOf(']');
    String str = paramString.substring(1, i);
    return str;
  }
  
  public boolean isCorbanameURL()
  {
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\namingutil\CorbalocURL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.xml.internal.ws.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Version
{
  public final String BUILD_ID = fixNull(paramString1);
  public final String BUILD_VERSION = fixNull(paramString2);
  public final String MAJOR_VERSION = fixNull(paramString3);
  public final String SVN_REVISION = fixNull(paramString4);
  public static final Version RUNTIME_VERSION = create(Version.class.getResourceAsStream("version.properties"));
  
  private Version(String paramString1, String paramString2, String paramString3, String paramString4) {}
  
  public static Version create(InputStream paramInputStream)
  {
    Properties localProperties = new Properties();
    try
    {
      localProperties.load(paramInputStream);
    }
    catch (IOException localIOException) {}catch (Exception localException) {}
    return new Version(localProperties.getProperty("build-id"), localProperties.getProperty("build-version"), localProperties.getProperty("major-version"), localProperties.getProperty("svn-revision"));
  }
  
  private String fixNull(String paramString)
  {
    if (paramString == null) {
      return "unknown";
    }
    return paramString;
  }
  
  public String toString()
  {
    return BUILD_VERSION + " svn-revision#" + SVN_REVISION;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\Version.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
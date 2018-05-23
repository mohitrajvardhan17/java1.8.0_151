package com.sun.org.apache.xml.internal.serializer;

import java.io.PrintStream;

public final class Version
{
  public Version() {}
  
  public static String getVersion()
  {
    return getProduct() + " " + getImplementationLanguage() + " " + getMajorVersionNum() + "." + getReleaseVersionNum() + "." + (getDevelopmentVersionNum() > 0 ? "D" + getDevelopmentVersionNum() : new StringBuilder().append("").append(getMaintenanceVersionNum()).toString());
  }
  
  public static void _main(String[] paramArrayOfString)
  {
    System.out.println(getVersion());
  }
  
  public static String getProduct()
  {
    return "Serializer";
  }
  
  public static String getImplementationLanguage()
  {
    return "Java";
  }
  
  public static int getMajorVersionNum()
  {
    return 2;
  }
  
  public static int getReleaseVersionNum()
  {
    return 7;
  }
  
  public static int getMaintenanceVersionNum()
  {
    return 0;
  }
  
  public static int getDevelopmentVersionNum()
  {
    try
    {
      if (new String("").length() == 0) {
        return 0;
      }
      return Integer.parseInt("");
    }
    catch (NumberFormatException localNumberFormatException) {}
    return 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\Version.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.org.apache.xerces.internal.impl;

import java.io.PrintStream;

public class Version
{
  /**
   * @deprecated
   */
  public static final String fVersion = ;
  private static final String fImmutableVersion = "Xerces-J 2.7.1";
  
  public Version() {}
  
  public static String getVersion()
  {
    return "Xerces-J 2.7.1";
  }
  
  public static void main(String[] paramArrayOfString)
  {
    System.out.println(fVersion);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\Version.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
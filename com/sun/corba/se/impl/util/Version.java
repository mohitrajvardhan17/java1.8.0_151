package com.sun.corba.se.impl.util;

import java.io.PrintStream;

public class Version
{
  public static final String PROJECT_NAME = "RMI-IIOP";
  public static final String VERSION = "1.0";
  public static final String BUILD = "0.0";
  public static final String BUILD_TIME = "unknown";
  public static final String FULL = "RMI-IIOP 1.0 (unknown)";
  
  public Version() {}
  
  public static String asString()
  {
    return "RMI-IIOP 1.0 (unknown)";
  }
  
  public static void main(String[] paramArrayOfString)
  {
    System.out.println("RMI-IIOP 1.0 (unknown)");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\util\Version.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
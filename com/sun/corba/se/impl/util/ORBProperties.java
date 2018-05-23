package com.sun.corba.se.impl.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class ORBProperties
{
  public static final String ORB_CLASS = "org.omg.CORBA.ORBClass=com.sun.corba.se.impl.orb.ORBImpl";
  public static final String ORB_SINGLETON_CLASS = "org.omg.CORBA.ORBSingletonClass=com.sun.corba.se.impl.orb.ORBSingleton";
  
  public ORBProperties() {}
  
  public static void main(String[] paramArrayOfString)
  {
    try
    {
      String str = System.getProperty("java.home");
      File localFile = new File(str + File.separator + "lib" + File.separator + "orb.properties");
      if (localFile.exists()) {
        return;
      }
      FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
      PrintWriter localPrintWriter = new PrintWriter(localFileOutputStream);
      try
      {
        localPrintWriter.println("org.omg.CORBA.ORBClass=com.sun.corba.se.impl.orb.ORBImpl");
        localPrintWriter.println("org.omg.CORBA.ORBSingletonClass=com.sun.corba.se.impl.orb.ORBSingleton");
      }
      finally
      {
        localPrintWriter.close();
        localFileOutputStream.close();
      }
    }
    catch (Exception localException) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\util\ORBProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.org.apache.xalan.internal.xsltc;

import java.io.PrintStream;

public class ProcessorVersion
{
  private static int MAJOR = 1;
  private static int MINOR = 0;
  private static int DELTA = 0;
  
  public ProcessorVersion() {}
  
  public static void main(String[] paramArrayOfString)
  {
    System.out.println("XSLTC version " + MAJOR + "." + MINOR + (DELTA > 0 ? "." + DELTA : ""));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\ProcessorVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
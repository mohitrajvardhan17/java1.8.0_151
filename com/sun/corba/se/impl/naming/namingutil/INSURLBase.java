package com.sun.corba.se.impl.naming.namingutil;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public abstract class INSURLBase
  implements INSURL
{
  protected boolean rirFlag = false;
  protected ArrayList theEndpointInfo = null;
  protected String theKeyString = "NameService";
  protected String theStringifiedName = null;
  
  public INSURLBase() {}
  
  public boolean getRIRFlag()
  {
    return rirFlag;
  }
  
  public List getEndpointInfo()
  {
    return theEndpointInfo;
  }
  
  public String getKeyString()
  {
    return theKeyString;
  }
  
  public String getStringifiedName()
  {
    return theStringifiedName;
  }
  
  public abstract boolean isCorbanameURL();
  
  public void dPrint()
  {
    System.out.println("URL Dump...");
    System.out.println("Key String = " + getKeyString());
    System.out.println("RIR Flag = " + getRIRFlag());
    System.out.println("isCorbanameURL = " + isCorbanameURL());
    for (int i = 0; i < theEndpointInfo.size(); i++) {
      ((IIOPEndpointInfo)theEndpointInfo.get(i)).dump();
    }
    if (isCorbanameURL()) {
      System.out.println("Stringified Name = " + getStringifiedName());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\namingutil\INSURLBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
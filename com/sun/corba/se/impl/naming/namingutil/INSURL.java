package com.sun.corba.se.impl.naming.namingutil;

import java.util.List;

public abstract interface INSURL
{
  public abstract boolean getRIRFlag();
  
  public abstract List getEndpointInfo();
  
  public abstract String getKeyString();
  
  public abstract String getStringifiedName();
  
  public abstract boolean isCorbanameURL();
  
  public abstract void dPrint();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\namingutil\INSURL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
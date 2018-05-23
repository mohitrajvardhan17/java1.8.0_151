package com.sun.corba.se.spi.activation.RepositoryPackage;

import org.omg.CORBA.portable.IDLEntity;

public final class ServerDef
  implements IDLEntity
{
  public String applicationName = null;
  public String serverName = null;
  public String serverClassPath = null;
  public String serverArgs = null;
  public String serverVmArgs = null;
  
  public ServerDef() {}
  
  public ServerDef(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    applicationName = paramString1;
    serverName = paramString2;
    serverClassPath = paramString3;
    serverArgs = paramString4;
    serverVmArgs = paramString5;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\RepositoryPackage\ServerDef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
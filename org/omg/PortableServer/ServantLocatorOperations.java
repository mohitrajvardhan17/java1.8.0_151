package org.omg.PortableServer;

import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public abstract interface ServantLocatorOperations
  extends ServantManagerOperations
{
  public abstract Servant preinvoke(byte[] paramArrayOfByte, POA paramPOA, String paramString, CookieHolder paramCookieHolder)
    throws ForwardRequest;
  
  public abstract void postinvoke(byte[] paramArrayOfByte, POA paramPOA, String paramString, Object paramObject, Servant paramServant);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\ServantLocatorOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
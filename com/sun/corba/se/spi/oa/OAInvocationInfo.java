package com.sun.corba.se.spi.oa;

import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA.portable.ServantObject;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public class OAInvocationInfo
  extends ServantObject
{
  private Object servantContainer;
  private ObjectAdapter oa;
  private byte[] oid;
  private CookieHolder cookieHolder;
  private String operation;
  private ObjectCopierFactory factory;
  
  public OAInvocationInfo(ObjectAdapter paramObjectAdapter, byte[] paramArrayOfByte)
  {
    oa = paramObjectAdapter;
    oid = paramArrayOfByte;
  }
  
  public OAInvocationInfo(OAInvocationInfo paramOAInvocationInfo, String paramString)
  {
    servant = servant;
    servantContainer = servantContainer;
    cookieHolder = cookieHolder;
    oa = oa;
    oid = oid;
    factory = factory;
    operation = paramString;
  }
  
  public ObjectAdapter oa()
  {
    return oa;
  }
  
  public byte[] id()
  {
    return oid;
  }
  
  public Object getServantContainer()
  {
    return servantContainer;
  }
  
  public CookieHolder getCookieHolder()
  {
    if (cookieHolder == null) {
      cookieHolder = new CookieHolder();
    }
    return cookieHolder;
  }
  
  public String getOperation()
  {
    return operation;
  }
  
  public ObjectCopierFactory getCopierFactory()
  {
    return factory;
  }
  
  public void setOperation(String paramString)
  {
    operation = paramString;
  }
  
  public void setCopierFactory(ObjectCopierFactory paramObjectCopierFactory)
  {
    factory = paramObjectCopierFactory;
  }
  
  public void setServant(Object paramObject)
  {
    servantContainer = paramObject;
    if ((paramObject instanceof Tie)) {
      servant = ((Tie)paramObject).getTarget();
    } else {
      servant = paramObject;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\oa\OAInvocationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
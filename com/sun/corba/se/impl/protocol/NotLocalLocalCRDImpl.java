package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import org.omg.CORBA.portable.ServantObject;

public class NotLocalLocalCRDImpl
  implements LocalClientRequestDispatcher
{
  public NotLocalLocalCRDImpl() {}
  
  public boolean useLocalInvocation(org.omg.CORBA.Object paramObject)
  {
    return false;
  }
  
  public boolean is_local(org.omg.CORBA.Object paramObject)
  {
    return false;
  }
  
  public ServantObject servant_preinvoke(org.omg.CORBA.Object paramObject, String paramString, Class paramClass)
  {
    return null;
  }
  
  public void servant_postinvoke(org.omg.CORBA.Object paramObject, ServantObject paramServantObject) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\NotLocalLocalCRDImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
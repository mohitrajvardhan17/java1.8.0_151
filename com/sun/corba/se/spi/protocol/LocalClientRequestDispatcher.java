package com.sun.corba.se.spi.protocol;

import org.omg.CORBA.portable.ServantObject;

public abstract interface LocalClientRequestDispatcher
{
  public abstract boolean useLocalInvocation(org.omg.CORBA.Object paramObject);
  
  public abstract boolean is_local(org.omg.CORBA.Object paramObject);
  
  public abstract ServantObject servant_preinvoke(org.omg.CORBA.Object paramObject, String paramString, Class paramClass);
  
  public abstract void servant_postinvoke(org.omg.CORBA.Object paramObject, ServantObject paramServantObject);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\protocol\LocalClientRequestDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
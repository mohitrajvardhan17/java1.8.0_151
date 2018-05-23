package com.sun.corba.se.spi.oa;

import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Policy;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

public abstract interface ObjectAdapter
{
  public abstract ORB getORB();
  
  public abstract Policy getEffectivePolicy(int paramInt);
  
  public abstract IORTemplate getIORTemplate();
  
  public abstract int getManagerId();
  
  public abstract short getState();
  
  public abstract ObjectReferenceTemplate getAdapterTemplate();
  
  public abstract ObjectReferenceFactory getCurrentFactory();
  
  public abstract void setCurrentFactory(ObjectReferenceFactory paramObjectReferenceFactory);
  
  public abstract org.omg.CORBA.Object getLocalServant(byte[] paramArrayOfByte);
  
  public abstract void getInvocationServant(OAInvocationInfo paramOAInvocationInfo);
  
  public abstract void enter()
    throws OADestroyed;
  
  public abstract void exit();
  
  public abstract void returnServant();
  
  public abstract OAInvocationInfo makeInvocationInfo(byte[] paramArrayOfByte);
  
  public abstract String[] getInterfaces(Object paramObject, byte[] paramArrayOfByte);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\oa\ObjectAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
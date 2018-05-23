package com.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.impl.ior.ObjectKeyTemplateBase;
import com.sun.corba.se.impl.javax.rmi.CORBA.Util;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import java.util.HashMap;
import java.util.Map;

public class TOAFactory
  implements ObjectAdapterFactory
{
  private ORB orb;
  private ORBUtilSystemException wrapper;
  private TOAImpl toa;
  private Map codebaseToTOA;
  private TransientObjectManager tom;
  
  public TOAFactory() {}
  
  public ObjectAdapter find(ObjectAdapterId paramObjectAdapterId)
  {
    if (paramObjectAdapterId.equals(ObjectKeyTemplateBase.JIDL_OAID)) {
      return getTOA();
    }
    throw wrapper.badToaOaid();
  }
  
  public void init(ORB paramORB)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "oa.lifecycle");
    tom = new TransientObjectManager(paramORB);
    codebaseToTOA = new HashMap();
  }
  
  public void shutdown(boolean paramBoolean)
  {
    if (Util.isInstanceDefined()) {
      Util.getInstance().unregisterTargetsForORB(orb);
    }
  }
  
  public synchronized TOA getTOA(String paramString)
  {
    Object localObject = (TOA)codebaseToTOA.get(paramString);
    if (localObject == null)
    {
      localObject = new TOAImpl(orb, tom, paramString);
      codebaseToTOA.put(paramString, localObject);
    }
    return (TOA)localObject;
  }
  
  public synchronized TOA getTOA()
  {
    if (toa == null) {
      toa = new TOAImpl(orb, tom, null);
    }
    return toa;
  }
  
  public ORB getORB()
  {
    return orb;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\toa\TOAFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
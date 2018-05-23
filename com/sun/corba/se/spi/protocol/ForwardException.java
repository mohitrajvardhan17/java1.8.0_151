package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Object;

public class ForwardException
  extends RuntimeException
{
  private ORB orb;
  private Object obj;
  private IOR ior;
  
  public ForwardException(ORB paramORB, IOR paramIOR)
  {
    orb = paramORB;
    obj = null;
    ior = paramIOR;
  }
  
  public ForwardException(ORB paramORB, Object paramObject)
  {
    if ((paramObject instanceof LocalObject)) {
      throw new BAD_PARAM();
    }
    orb = paramORB;
    obj = paramObject;
    ior = null;
  }
  
  public synchronized Object getObject()
  {
    if (obj == null) {
      obj = ORBUtility.makeObjectReference(ior);
    }
    return obj;
  }
  
  public synchronized IOR getIOR()
  {
    if (ior == null) {
      ior = ORBUtility.getIOR(obj);
    }
    return ior;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\protocol\ForwardException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
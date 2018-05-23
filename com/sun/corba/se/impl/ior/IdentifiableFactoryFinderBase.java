package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.ior.Identifiable;
import com.sun.corba.se.spi.ior.IdentifiableFactory;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.orb.ORB;
import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA_2_3.portable.InputStream;

public abstract class IdentifiableFactoryFinderBase
  implements IdentifiableFactoryFinder
{
  private ORB orb;
  private Map map = new HashMap();
  protected IORSystemException wrapper;
  
  protected IdentifiableFactoryFinderBase(ORB paramORB)
  {
    orb = paramORB;
    wrapper = IORSystemException.get(paramORB, "oa.ior");
  }
  
  protected IdentifiableFactory getFactory(int paramInt)
  {
    Integer localInteger = new Integer(paramInt);
    IdentifiableFactory localIdentifiableFactory = (IdentifiableFactory)map.get(localInteger);
    return localIdentifiableFactory;
  }
  
  public abstract Identifiable handleMissingFactory(int paramInt, InputStream paramInputStream);
  
  public Identifiable create(int paramInt, InputStream paramInputStream)
  {
    IdentifiableFactory localIdentifiableFactory = getFactory(paramInt);
    if (localIdentifiableFactory != null) {
      return localIdentifiableFactory.create(paramInputStream);
    }
    return handleMissingFactory(paramInt, paramInputStream);
  }
  
  public void registerFactory(IdentifiableFactory paramIdentifiableFactory)
  {
    Integer localInteger = new Integer(paramIdentifiableFactory.getId());
    map.put(localInteger, paramIdentifiableFactory);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\IdentifiableFactoryFinderBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
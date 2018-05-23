package com.sun.corba.se.impl.copyobject;

import com.sun.corba.se.impl.orbutil.DenseIntMapImpl;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.orb.ORB;

public class CopierManagerImpl
  implements CopierManager
{
  private int defaultId = 0;
  private DenseIntMapImpl map = new DenseIntMapImpl();
  private ORB orb;
  
  public CopierManagerImpl(ORB paramORB)
  {
    orb = paramORB;
  }
  
  public void setDefaultId(int paramInt)
  {
    defaultId = paramInt;
  }
  
  public int getDefaultId()
  {
    return defaultId;
  }
  
  public ObjectCopierFactory getObjectCopierFactory(int paramInt)
  {
    return (ObjectCopierFactory)map.get(paramInt);
  }
  
  public ObjectCopierFactory getDefaultObjectCopierFactory()
  {
    return (ObjectCopierFactory)map.get(defaultId);
  }
  
  public void registerObjectCopierFactory(ObjectCopierFactory paramObjectCopierFactory, int paramInt)
  {
    map.set(paramInt, paramObjectCopierFactory);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\copyobject\CopierManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
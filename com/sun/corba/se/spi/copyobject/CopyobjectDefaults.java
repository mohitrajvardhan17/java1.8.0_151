package com.sun.corba.se.spi.copyobject;

import com.sun.corba.se.impl.copyobject.FallbackObjectCopierImpl;
import com.sun.corba.se.impl.copyobject.JavaStreamObjectCopierImpl;
import com.sun.corba.se.impl.copyobject.ORBStreamObjectCopierImpl;
import com.sun.corba.se.impl.copyobject.ReferenceObjectCopierImpl;
import com.sun.corba.se.spi.orb.ORB;

public abstract class CopyobjectDefaults
{
  private static final ObjectCopier referenceObjectCopier = new ReferenceObjectCopierImpl();
  private static ObjectCopierFactory referenceObjectCopierFactory = new ObjectCopierFactory()
  {
    public ObjectCopier make()
    {
      return CopyobjectDefaults.referenceObjectCopier;
    }
  };
  
  private CopyobjectDefaults() {}
  
  public static ObjectCopierFactory makeORBStreamObjectCopierFactory(ORB paramORB)
  {
    new ObjectCopierFactory()
    {
      public ObjectCopier make()
      {
        return new ORBStreamObjectCopierImpl(val$orb);
      }
    };
  }
  
  public static ObjectCopierFactory makeJavaStreamObjectCopierFactory(ORB paramORB)
  {
    new ObjectCopierFactory()
    {
      public ObjectCopier make()
      {
        return new JavaStreamObjectCopierImpl(val$orb);
      }
    };
  }
  
  public static ObjectCopierFactory getReferenceObjectCopierFactory()
  {
    return referenceObjectCopierFactory;
  }
  
  public static ObjectCopierFactory makeFallbackObjectCopierFactory(ObjectCopierFactory paramObjectCopierFactory1, final ObjectCopierFactory paramObjectCopierFactory2)
  {
    new ObjectCopierFactory()
    {
      public ObjectCopier make()
      {
        ObjectCopier localObjectCopier1 = val$f1.make();
        ObjectCopier localObjectCopier2 = paramObjectCopierFactory2.make();
        return new FallbackObjectCopierImpl(localObjectCopier1, localObjectCopier2);
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\copyobject\CopyobjectDefaults.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
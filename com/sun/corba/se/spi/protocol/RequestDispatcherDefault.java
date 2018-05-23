package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.impl.protocol.BootstrapServerRequestDispatcher;
import com.sun.corba.se.impl.protocol.CorbaClientRequestDispatcherImpl;
import com.sun.corba.se.impl.protocol.CorbaServerRequestDispatcherImpl;
import com.sun.corba.se.impl.protocol.FullServantCacheLocalCRDImpl;
import com.sun.corba.se.impl.protocol.INSServerRequestDispatcher;
import com.sun.corba.se.impl.protocol.InfoOnlyServantCacheLocalCRDImpl;
import com.sun.corba.se.impl.protocol.JIDLLocalCRDImpl;
import com.sun.corba.se.impl.protocol.MinimalServantCacheLocalCRDImpl;
import com.sun.corba.se.impl.protocol.POALocalCRDImpl;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;

public final class RequestDispatcherDefault
{
  private RequestDispatcherDefault() {}
  
  public static ClientRequestDispatcher makeClientRequestDispatcher()
  {
    return new CorbaClientRequestDispatcherImpl();
  }
  
  public static CorbaServerRequestDispatcher makeServerRequestDispatcher(ORB paramORB)
  {
    return new CorbaServerRequestDispatcherImpl(paramORB);
  }
  
  public static CorbaServerRequestDispatcher makeBootstrapServerRequestDispatcher(ORB paramORB)
  {
    return new BootstrapServerRequestDispatcher(paramORB);
  }
  
  public static CorbaServerRequestDispatcher makeINSServerRequestDispatcher(ORB paramORB)
  {
    return new INSServerRequestDispatcher(paramORB);
  }
  
  public static LocalClientRequestDispatcherFactory makeMinimalServantCacheLocalClientRequestDispatcherFactory(ORB paramORB)
  {
    new LocalClientRequestDispatcherFactory()
    {
      public LocalClientRequestDispatcher create(int paramAnonymousInt, IOR paramAnonymousIOR)
      {
        return new MinimalServantCacheLocalCRDImpl(val$orb, paramAnonymousInt, paramAnonymousIOR);
      }
    };
  }
  
  public static LocalClientRequestDispatcherFactory makeInfoOnlyServantCacheLocalClientRequestDispatcherFactory(ORB paramORB)
  {
    new LocalClientRequestDispatcherFactory()
    {
      public LocalClientRequestDispatcher create(int paramAnonymousInt, IOR paramAnonymousIOR)
      {
        return new InfoOnlyServantCacheLocalCRDImpl(val$orb, paramAnonymousInt, paramAnonymousIOR);
      }
    };
  }
  
  public static LocalClientRequestDispatcherFactory makeFullServantCacheLocalClientRequestDispatcherFactory(ORB paramORB)
  {
    new LocalClientRequestDispatcherFactory()
    {
      public LocalClientRequestDispatcher create(int paramAnonymousInt, IOR paramAnonymousIOR)
      {
        return new FullServantCacheLocalCRDImpl(val$orb, paramAnonymousInt, paramAnonymousIOR);
      }
    };
  }
  
  public static LocalClientRequestDispatcherFactory makeJIDLLocalClientRequestDispatcherFactory(ORB paramORB)
  {
    new LocalClientRequestDispatcherFactory()
    {
      public LocalClientRequestDispatcher create(int paramAnonymousInt, IOR paramAnonymousIOR)
      {
        return new JIDLLocalCRDImpl(val$orb, paramAnonymousInt, paramAnonymousIOR);
      }
    };
  }
  
  public static LocalClientRequestDispatcherFactory makePOALocalClientRequestDispatcherFactory(ORB paramORB)
  {
    new LocalClientRequestDispatcherFactory()
    {
      public LocalClientRequestDispatcher create(int paramAnonymousInt, IOR paramAnonymousIOR)
      {
        return new POALocalCRDImpl(val$orb, paramAnonymousInt, paramAnonymousIOR);
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\protocol\RequestDispatcherDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.corba.se.spi.transport;

import com.sun.corba.se.impl.protocol.CorbaClientDelegateImpl;
import com.sun.corba.se.impl.transport.CorbaContactInfoListImpl;
import com.sun.corba.se.impl.transport.ReadTCPTimeoutsImpl;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.ClientDelegateFactory;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;

public abstract class TransportDefault
{
  private TransportDefault() {}
  
  public static CorbaContactInfoListFactory makeCorbaContactInfoListFactory(ORB paramORB)
  {
    new CorbaContactInfoListFactory()
    {
      public void setORB(ORB paramAnonymousORB) {}
      
      public CorbaContactInfoList create(IOR paramAnonymousIOR)
      {
        return new CorbaContactInfoListImpl(val$broker, paramAnonymousIOR);
      }
    };
  }
  
  public static ClientDelegateFactory makeClientDelegateFactory(ORB paramORB)
  {
    new ClientDelegateFactory()
    {
      public CorbaClientDelegate create(CorbaContactInfoList paramAnonymousCorbaContactInfoList)
      {
        return new CorbaClientDelegateImpl(val$broker, paramAnonymousCorbaContactInfoList);
      }
    };
  }
  
  public static IORTransformer makeIORTransformer(ORB paramORB)
  {
    return null;
  }
  
  public static ReadTimeoutsFactory makeReadTimeoutsFactory()
  {
    new ReadTimeoutsFactory()
    {
      public ReadTimeouts create(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        return new ReadTCPTimeoutsImpl(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\transport\TransportDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
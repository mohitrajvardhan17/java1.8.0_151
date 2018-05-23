package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.transport.CorbaContactInfoListImpl;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;

public class SocketFactoryContactInfoListImpl
  extends CorbaContactInfoListImpl
{
  public SocketFactoryContactInfoListImpl(ORB paramORB)
  {
    super(paramORB);
  }
  
  public SocketFactoryContactInfoListImpl(ORB paramORB, IOR paramIOR)
  {
    super(paramORB, paramIOR);
  }
  
  public Iterator iterator()
  {
    return new SocketFactoryContactInfoListIteratorImpl(orb, this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\legacy\connection\SocketFactoryContactInfoListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
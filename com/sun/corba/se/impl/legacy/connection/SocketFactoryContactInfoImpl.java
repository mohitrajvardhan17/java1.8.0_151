package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.transport.SocketOrChannelContactInfoImpl;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.legacy.connection.ORBSocketFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.SocketInfo;

public class SocketFactoryContactInfoImpl
  extends SocketOrChannelContactInfoImpl
{
  protected ORBUtilSystemException wrapper;
  protected SocketInfo socketInfo;
  
  public SocketFactoryContactInfoImpl() {}
  
  public SocketFactoryContactInfoImpl(ORB paramORB, CorbaContactInfoList paramCorbaContactInfoList, IOR paramIOR, short paramShort, SocketInfo paramSocketInfo)
  {
    super(paramORB, paramCorbaContactInfoList);
    effectiveTargetIOR = paramIOR;
    addressingDisposition = paramShort;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.transport");
    socketInfo = paramORB.getORBData().getLegacySocketFactory().getEndPointInfo(paramORB, paramIOR, paramSocketInfo);
    socketType = socketInfo.getType();
    hostname = socketInfo.getHost();
    port = socketInfo.getPort();
  }
  
  public Connection createConnection()
  {
    SocketFactoryConnectionImpl localSocketFactoryConnectionImpl = new SocketFactoryConnectionImpl(orb, this, orb.getORBData().connectionSocketUseSelectThreadToWait(), orb.getORBData().connectionSocketUseWorkerThreadForEvent());
    return localSocketFactoryConnectionImpl;
  }
  
  public String toString()
  {
    return "SocketFactoryContactInfoImpl[" + socketType + " " + hostname + " " + port + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\legacy\connection\SocketFactoryContactInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
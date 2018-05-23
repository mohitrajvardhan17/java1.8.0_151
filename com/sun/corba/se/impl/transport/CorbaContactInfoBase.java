package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.CorbaMessageMediatorImpl;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import java.nio.ByteBuffer;
import sun.corba.OutputStreamFactory;

public abstract class CorbaContactInfoBase
  implements CorbaContactInfo
{
  protected ORB orb;
  protected CorbaContactInfoList contactInfoList;
  protected IOR effectiveTargetIOR;
  protected short addressingDisposition;
  protected OutboundConnectionCache connectionCache;
  
  public CorbaContactInfoBase() {}
  
  public Broker getBroker()
  {
    return orb;
  }
  
  public ContactInfoList getContactInfoList()
  {
    return contactInfoList;
  }
  
  public ClientRequestDispatcher getClientRequestDispatcher()
  {
    int i = getEffectiveProfile().getObjectKeyTemplate().getSubcontractId();
    RequestDispatcherRegistry localRequestDispatcherRegistry = orb.getRequestDispatcherRegistry();
    return localRequestDispatcherRegistry.getClientRequestDispatcher(i);
  }
  
  public void setConnectionCache(OutboundConnectionCache paramOutboundConnectionCache)
  {
    connectionCache = paramOutboundConnectionCache;
  }
  
  public OutboundConnectionCache getConnectionCache()
  {
    return connectionCache;
  }
  
  public MessageMediator createMessageMediator(Broker paramBroker, ContactInfo paramContactInfo, Connection paramConnection, String paramString, boolean paramBoolean)
  {
    CorbaMessageMediatorImpl localCorbaMessageMediatorImpl = new CorbaMessageMediatorImpl((ORB)paramBroker, paramContactInfo, paramConnection, GIOPVersion.chooseRequestVersion((ORB)paramBroker, effectiveTargetIOR), effectiveTargetIOR, ((CorbaConnection)paramConnection).getNextRequestId(), getAddressingDisposition(), paramString, paramBoolean);
    return localCorbaMessageMediatorImpl;
  }
  
  public MessageMediator createMessageMediator(Broker paramBroker, Connection paramConnection)
  {
    ORB localORB = (ORB)paramBroker;
    CorbaConnection localCorbaConnection = (CorbaConnection)paramConnection;
    if (transportDebugFlag) {
      if (localCorbaConnection.shouldReadGiopHeaderOnly()) {
        dprint(".createMessageMediator: waiting for message header on connection: " + localCorbaConnection);
      } else {
        dprint(".createMessageMediator: waiting for message on connection: " + localCorbaConnection);
      }
    }
    MessageBase localMessageBase = null;
    if (localCorbaConnection.shouldReadGiopHeaderOnly()) {
      localMessageBase = MessageBase.readGIOPHeader(localORB, localCorbaConnection);
    } else {
      localMessageBase = MessageBase.readGIOPMessage(localORB, localCorbaConnection);
    }
    ByteBuffer localByteBuffer = localMessageBase.getByteBuffer();
    localMessageBase.setByteBuffer(null);
    CorbaMessageMediatorImpl localCorbaMessageMediatorImpl = new CorbaMessageMediatorImpl(localORB, localCorbaConnection, localMessageBase, localByteBuffer);
    return localCorbaMessageMediatorImpl;
  }
  
  public MessageMediator finishCreatingMessageMediator(Broker paramBroker, Connection paramConnection, MessageMediator paramMessageMediator)
  {
    ORB localORB = (ORB)paramBroker;
    CorbaConnection localCorbaConnection = (CorbaConnection)paramConnection;
    CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    if (transportDebugFlag) {
      dprint(".finishCreatingMessageMediator: waiting for message body on connection: " + localCorbaConnection);
    }
    Message localMessage = localCorbaMessageMediator.getDispatchHeader();
    localMessage.setByteBuffer(localCorbaMessageMediator.getDispatchBuffer());
    localMessage = MessageBase.readGIOPBody(localORB, localCorbaConnection, localMessage);
    ByteBuffer localByteBuffer = localMessage.getByteBuffer();
    localMessage.setByteBuffer(null);
    localCorbaMessageMediator.setDispatchHeader(localMessage);
    localCorbaMessageMediator.setDispatchBuffer(localByteBuffer);
    return localCorbaMessageMediator;
  }
  
  public OutputObject createOutputObject(MessageMediator paramMessageMediator)
  {
    CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    CDROutputObject localCDROutputObject = OutputStreamFactory.newCDROutputObject(orb, paramMessageMediator, localCorbaMessageMediator.getRequestHeader(), localCorbaMessageMediator.getStreamFormatVersion());
    paramMessageMediator.setOutputObject(localCDROutputObject);
    return localCDROutputObject;
  }
  
  public InputObject createInputObject(Broker paramBroker, MessageMediator paramMessageMediator)
  {
    CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    return new CDRInputObject((ORB)paramBroker, (CorbaConnection)paramMessageMediator.getConnection(), localCorbaMessageMediator.getDispatchBuffer(), localCorbaMessageMediator.getDispatchHeader());
  }
  
  public short getAddressingDisposition()
  {
    return addressingDisposition;
  }
  
  public void setAddressingDisposition(short paramShort)
  {
    addressingDisposition = paramShort;
  }
  
  public IOR getTargetIOR()
  {
    return contactInfoList.getTargetIOR();
  }
  
  public IOR getEffectiveTargetIOR()
  {
    return effectiveTargetIOR;
  }
  
  public IIOPProfile getEffectiveProfile()
  {
    return effectiveTargetIOR.getProfile();
  }
  
  public String toString()
  {
    return "CorbaContactInfoBase[]";
  }
  
  protected void dprint(String paramString)
  {
    ORBUtility.dprint("CorbaContactInfoBase", paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\transport\CorbaContactInfoBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
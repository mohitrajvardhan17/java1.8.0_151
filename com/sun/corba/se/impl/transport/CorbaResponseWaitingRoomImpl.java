package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.encoding.BufferManagerReadStream;
import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyOrReplyMessage;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaResponseWaitingRoom;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;

public class CorbaResponseWaitingRoomImpl
  implements CorbaResponseWaitingRoom
{
  private ORB orb;
  private ORBUtilSystemException wrapper;
  private CorbaConnection connection;
  private final Map<Integer, OutCallDesc> out_calls;
  
  public CorbaResponseWaitingRoomImpl(ORB paramORB, CorbaConnection paramCorbaConnection)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.transport");
    connection = paramCorbaConnection;
    out_calls = Collections.synchronizedMap(new HashMap());
  }
  
  public void registerWaiter(MessageMediator paramMessageMediator)
  {
    CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    if (orb.transportDebugFlag) {
      dprint(".registerWaiter: " + opAndId(localCorbaMessageMediator));
    }
    Integer localInteger = localCorbaMessageMediator.getRequestIdInteger();
    OutCallDesc localOutCallDesc = new OutCallDesc();
    thread = Thread.currentThread();
    messageMediator = localCorbaMessageMediator;
    out_calls.put(localInteger, localOutCallDesc);
  }
  
  public void unregisterWaiter(MessageMediator paramMessageMediator)
  {
    CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    if (orb.transportDebugFlag) {
      dprint(".unregisterWaiter: " + opAndId(localCorbaMessageMediator));
    }
    Integer localInteger = localCorbaMessageMediator.getRequestIdInteger();
    out_calls.remove(localInteger);
  }
  
  public InputObject waitForResponse(MessageMediator paramMessageMediator)
  {
    CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    try
    {
      InputObject localInputObject = null;
      if (orb.transportDebugFlag) {
        dprint(".waitForResponse->: " + opAndId(localCorbaMessageMediator));
      }
      Integer localInteger = localCorbaMessageMediator.getRequestIdInteger();
      if (localCorbaMessageMediator.isOneWay())
      {
        if (orb.transportDebugFlag) {
          dprint(".waitForResponse: one way - not waiting: " + opAndId(localCorbaMessageMediator));
        }
        localObject1 = null;
        return (InputObject)localObject1;
      }
      Object localObject1 = (OutCallDesc)out_calls.get(localInteger);
      if (localObject1 == null) {
        throw wrapper.nullOutCall(CompletionStatus.COMPLETED_MAYBE);
      }
      synchronized (done)
      {
        while ((inputObject == null) && (exception == null)) {
          try
          {
            if (orb.transportDebugFlag) {
              dprint(".waitForResponse: waiting: " + opAndId(localCorbaMessageMediator));
            }
            done.wait();
          }
          catch (InterruptedException localInterruptedException) {}
        }
        if (exception != null) {
          throw exception;
        }
        localInputObject = inputObject;
      }
      if (localInputObject != null) {
        ((CDRInputObject)localInputObject).unmarshalHeader();
      }
      ??? = localInputObject;
      return (InputObject)???;
    }
    finally
    {
      if (orb.transportDebugFlag) {
        dprint(".waitForResponse<-: " + opAndId(localCorbaMessageMediator));
      }
    }
  }
  
  public void responseReceived(InputObject paramInputObject)
  {
    CDRInputObject localCDRInputObject = (CDRInputObject)paramInputObject;
    LocateReplyOrReplyMessage localLocateReplyOrReplyMessage = (LocateReplyOrReplyMessage)localCDRInputObject.getMessageHeader();
    Integer localInteger = new Integer(localLocateReplyOrReplyMessage.getRequestId());
    OutCallDesc localOutCallDesc = (OutCallDesc)out_calls.get(localInteger);
    if (orb.transportDebugFlag) {
      dprint(".responseReceived: id/" + localInteger + ": " + localLocateReplyOrReplyMessage);
    }
    if (localOutCallDesc == null)
    {
      if (orb.transportDebugFlag) {
        dprint(".responseReceived: id/" + localInteger + ": no waiter: " + localLocateReplyOrReplyMessage);
      }
      return;
    }
    synchronized (done)
    {
      CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)messageMediator;
      if (orb.transportDebugFlag) {
        dprint(".responseReceived: " + opAndId(localCorbaMessageMediator) + ": notifying waiters");
      }
      localCorbaMessageMediator.setReplyHeader(localLocateReplyOrReplyMessage);
      localCorbaMessageMediator.setInputObject(paramInputObject);
      localCDRInputObject.setMessageMediator(localCorbaMessageMediator);
      inputObject = paramInputObject;
      done.notify();
    }
  }
  
  public int numberRegistered()
  {
    return out_calls.size();
  }
  
  public void signalExceptionToAllWaiters(SystemException paramSystemException)
  {
    if (orb.transportDebugFlag) {
      dprint(".signalExceptionToAllWaiters: " + paramSystemException);
    }
    synchronized (out_calls)
    {
      if (orb.transportDebugFlag) {
        dprint(".signalExceptionToAllWaiters: out_calls size :" + out_calls.size());
      }
      Iterator localIterator = out_calls.values().iterator();
      while (localIterator.hasNext())
      {
        OutCallDesc localOutCallDesc = (OutCallDesc)localIterator.next();
        if (orb.transportDebugFlag) {
          dprint(".signalExceptionToAllWaiters: signaling " + localOutCallDesc);
        }
        synchronized (done)
        {
          try
          {
            CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)messageMediator;
            CDRInputObject localCDRInputObject = (CDRInputObject)localCorbaMessageMediator.getInputObject();
            if (localCDRInputObject != null)
            {
              BufferManagerReadStream localBufferManagerReadStream = (BufferManagerReadStream)localCDRInputObject.getBufferManager();
              int i = localCorbaMessageMediator.getRequestId();
              localBufferManagerReadStream.cancelProcessing(i);
            }
          }
          catch (Exception localException) {}finally
          {
            inputObject = null;
            exception = paramSystemException;
            done.notifyAll();
          }
        }
      }
    }
  }
  
  public MessageMediator getMessageMediator(int paramInt)
  {
    Integer localInteger = new Integer(paramInt);
    OutCallDesc localOutCallDesc = (OutCallDesc)out_calls.get(localInteger);
    if (localOutCallDesc == null) {
      return null;
    }
    return messageMediator;
  }
  
  protected void dprint(String paramString)
  {
    ORBUtility.dprint("CorbaResponseWaitingRoomImpl", paramString);
  }
  
  protected String opAndId(CorbaMessageMediator paramCorbaMessageMediator)
  {
    return ORBUtility.operationNameAndRequestId(paramCorbaMessageMediator);
  }
  
  static final class OutCallDesc
  {
    Object done = new Object();
    Thread thread;
    MessageMediator messageMediator;
    SystemException exception;
    InputObject inputObject;
    
    OutCallDesc() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\transport\CorbaResponseWaitingRoomImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
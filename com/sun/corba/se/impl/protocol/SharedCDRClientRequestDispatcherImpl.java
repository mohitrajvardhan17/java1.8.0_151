package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.encoding.ByteBufferWithInfo;
import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;

public class SharedCDRClientRequestDispatcherImpl
  extends CorbaClientRequestDispatcherImpl
{
  public SharedCDRClientRequestDispatcherImpl() {}
  
  public InputObject marshalingComplete(Object paramObject, OutputObject paramOutputObject)
    throws ApplicationException, RemarshalException
  {
    ORB localORB1 = null;
    CorbaMessageMediator localCorbaMessageMediator = null;
    try
    {
      localCorbaMessageMediator = (CorbaMessageMediator)paramOutputObject.getMessageMediator();
      localORB1 = (ORB)localCorbaMessageMediator.getBroker();
      if (subcontractDebugFlag) {
        dprint(".marshalingComplete->: " + opAndId(localCorbaMessageMediator));
      }
      CDROutputObject localCDROutputObject = (CDROutputObject)paramOutputObject;
      ByteBufferWithInfo localByteBufferWithInfo = localCDROutputObject.getByteBufferWithInfo();
      localCDROutputObject.getMessageHeader().setSize(byteBuffer, localByteBufferWithInfo.getSize());
      final ORB localORB2 = localORB1;
      final ByteBuffer localByteBuffer1 = byteBuffer;
      final Message localMessage1 = localCDROutputObject.getMessageHeader();
      CDRInputObject localCDRInputObject1 = (CDRInputObject)AccessController.doPrivileged(new PrivilegedAction()
      {
        public CDRInputObject run()
        {
          return new CDRInputObject(localORB2, null, localByteBuffer1, localMessage1);
        }
      });
      localCorbaMessageMediator.setInputObject(localCDRInputObject1);
      localCDRInputObject1.setMessageMediator(localCorbaMessageMediator);
      ((CorbaMessageMediatorImpl)localCorbaMessageMediator).handleRequestRequest(localCorbaMessageMediator);
      try
      {
        localCDRInputObject1.close();
      }
      catch (IOException localIOException)
      {
        if (transportDebugFlag) {
          dprint(".marshalingComplete: ignoring IOException - " + localIOException.toString());
        }
      }
      localCDROutputObject = (CDROutputObject)localCorbaMessageMediator.getOutputObject();
      localByteBufferWithInfo = localCDROutputObject.getByteBufferWithInfo();
      localCDROutputObject.getMessageHeader().setSize(byteBuffer, localByteBufferWithInfo.getSize());
      final ORB localORB3 = localORB1;
      final ByteBuffer localByteBuffer2 = byteBuffer;
      final Message localMessage2 = localCDROutputObject.getMessageHeader();
      localCDRInputObject1 = (CDRInputObject)AccessController.doPrivileged(new PrivilegedAction()
      {
        public CDRInputObject run()
        {
          return new CDRInputObject(localORB3, null, localByteBuffer2, localMessage2);
        }
      });
      localCorbaMessageMediator.setInputObject(localCDRInputObject1);
      localCDRInputObject1.setMessageMediator(localCorbaMessageMediator);
      localCDRInputObject1.unmarshalHeader();
      CDRInputObject localCDRInputObject2 = localCDRInputObject1;
      InputObject localInputObject = processResponse(localORB1, localCorbaMessageMediator, localCDRInputObject2);
      return localInputObject;
    }
    finally
    {
      if (subcontractDebugFlag) {
        dprint(".marshalingComplete<-: " + opAndId(localCorbaMessageMediator));
      }
    }
  }
  
  protected void dprint(String paramString)
  {
    ORBUtility.dprint("SharedCDRClientRequestDispatcherImpl", paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\SharedCDRClientRequestDispatcherImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
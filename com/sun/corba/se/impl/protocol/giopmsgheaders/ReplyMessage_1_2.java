package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import java.io.IOException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;

public final class ReplyMessage_1_2
  extends Message_1_2
  implements ReplyMessage
{
  private ORB orb = null;
  private ORBUtilSystemException wrapper = null;
  private int reply_status = 0;
  private ServiceContexts service_contexts = null;
  private IOR ior = null;
  private String exClassName = null;
  private int minorCode = 0;
  private CompletionStatus completionStatus = null;
  private short addrDisposition = 0;
  
  ReplyMessage_1_2(ORB paramORB)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
  }
  
  ReplyMessage_1_2(ORB paramORB, int paramInt1, int paramInt2, ServiceContexts paramServiceContexts, IOR paramIOR)
  {
    super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)1, 0);
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    request_id = paramInt1;
    reply_status = paramInt2;
    service_contexts = paramServiceContexts;
    ior = paramIOR;
  }
  
  public int getRequestId()
  {
    return request_id;
  }
  
  public int getReplyStatus()
  {
    return reply_status;
  }
  
  public short getAddrDisposition()
  {
    return addrDisposition;
  }
  
  public ServiceContexts getServiceContexts()
  {
    return service_contexts;
  }
  
  public void setServiceContexts(ServiceContexts paramServiceContexts)
  {
    service_contexts = paramServiceContexts;
  }
  
  public SystemException getSystemException(String paramString)
  {
    return MessageBase.getSystemException(exClassName, minorCode, completionStatus, paramString, wrapper);
  }
  
  public IOR getIOR()
  {
    return ior;
  }
  
  public void setIOR(IOR paramIOR)
  {
    ior = paramIOR;
  }
  
  public void read(org.omg.CORBA.portable.InputStream paramInputStream)
  {
    super.read(paramInputStream);
    request_id = paramInputStream.read_ulong();
    reply_status = paramInputStream.read_long();
    isValidReplyStatus(reply_status);
    service_contexts = new ServiceContexts((org.omg.CORBA_2_3.portable.InputStream)paramInputStream);
    ((CDRInputStream)paramInputStream).setHeaderPadding(true);
    Object localObject;
    if (reply_status == 2)
    {
      localObject = paramInputStream.read_string();
      exClassName = ORBUtility.classNameOf((String)localObject);
      minorCode = paramInputStream.read_long();
      int i = paramInputStream.read_long();
      switch (i)
      {
      case 0: 
        completionStatus = CompletionStatus.COMPLETED_YES;
        break;
      case 1: 
        completionStatus = CompletionStatus.COMPLETED_NO;
        break;
      case 2: 
        completionStatus = CompletionStatus.COMPLETED_MAYBE;
        break;
      default: 
        throw wrapper.badCompletionStatusInReply(CompletionStatus.COMPLETED_MAYBE, new Integer(i));
      }
    }
    else if (reply_status != 1)
    {
      if ((reply_status == 3) || (reply_status == 4))
      {
        localObject = (CDRInputStream)paramInputStream;
        ior = IORFactories.makeIOR((org.omg.CORBA_2_3.portable.InputStream)localObject);
      }
      else if (reply_status == 5)
      {
        addrDisposition = AddressingDispositionHelper.read(paramInputStream);
      }
    }
  }
  
  public void write(org.omg.CORBA.portable.OutputStream paramOutputStream)
  {
    super.write(paramOutputStream);
    paramOutputStream.write_ulong(request_id);
    paramOutputStream.write_long(reply_status);
    if (service_contexts != null) {
      service_contexts.write((org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream, GIOPVersion.V1_2);
    } else {
      ServiceContexts.writeNullServiceContext((org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream);
    }
    ((CDROutputStream)paramOutputStream).setHeaderPadding(true);
  }
  
  public static void isValidReplyStatus(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
      break;
    default: 
      ORBUtilSystemException localORBUtilSystemException = ORBUtilSystemException.get("rpc.protocol");
      throw localORBUtilSystemException.illegalReplyStatus(CompletionStatus.COMPLETED_MAYBE);
    }
  }
  
  public void callback(MessageHandler paramMessageHandler)
    throws IOException
  {
    paramMessageHandler.handleInput(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\ReplyMessage_1_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
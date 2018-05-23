package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.OutputStream;

public final class LocateReplyMessage_1_2
  extends Message_1_2
  implements LocateReplyMessage
{
  private ORB orb = null;
  private ORBUtilSystemException wrapper = null;
  private int reply_status = 0;
  private IOR ior = null;
  private String exClassName = null;
  private int minorCode = 0;
  private CompletionStatus completionStatus = null;
  private short addrDisposition = 0;
  
  LocateReplyMessage_1_2(ORB paramORB)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
  }
  
  LocateReplyMessage_1_2(ORB paramORB, int paramInt1, int paramInt2, IOR paramIOR)
  {
    super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)4, 0);
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    request_id = paramInt1;
    reply_status = paramInt2;
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
  
  public SystemException getSystemException(String paramString)
  {
    return MessageBase.getSystemException(exClassName, minorCode, completionStatus, paramString, wrapper);
  }
  
  public IOR getIOR()
  {
    return ior;
  }
  
  public void read(org.omg.CORBA.portable.InputStream paramInputStream)
  {
    super.read(paramInputStream);
    request_id = paramInputStream.read_ulong();
    reply_status = paramInputStream.read_long();
    isValidReplyStatus(reply_status);
    Object localObject;
    if (reply_status == 4)
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
        throw wrapper.badCompletionStatusInLocateReply(CompletionStatus.COMPLETED_MAYBE, new Integer(i));
      }
    }
    else if ((reply_status == 2) || (reply_status == 3))
    {
      localObject = (CDRInputStream)paramInputStream;
      ior = IORFactories.makeIOR((org.omg.CORBA_2_3.portable.InputStream)localObject);
    }
    else if (reply_status == 5)
    {
      addrDisposition = AddressingDispositionHelper.read(paramInputStream);
    }
  }
  
  public void write(OutputStream paramOutputStream)
  {
    super.write(paramOutputStream);
    paramOutputStream.write_ulong(request_id);
    paramOutputStream.write_long(reply_status);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\LocateReplyMessage_1_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class LocateReplyMessage_1_0
  extends Message_1_0
  implements LocateReplyMessage
{
  private ORB orb = null;
  private int request_id = 0;
  private int locate_status = 0;
  private IOR ior = null;
  
  LocateReplyMessage_1_0(ORB paramORB)
  {
    orb = paramORB;
  }
  
  LocateReplyMessage_1_0(ORB paramORB, int paramInt1, int paramInt2, IOR paramIOR)
  {
    super(1195986768, false, (byte)4, 0);
    orb = paramORB;
    request_id = paramInt1;
    locate_status = paramInt2;
    ior = paramIOR;
  }
  
  public int getRequestId()
  {
    return request_id;
  }
  
  public int getReplyStatus()
  {
    return locate_status;
  }
  
  public short getAddrDisposition()
  {
    return 0;
  }
  
  public SystemException getSystemException(String paramString)
  {
    return null;
  }
  
  public IOR getIOR()
  {
    return ior;
  }
  
  public void read(InputStream paramInputStream)
  {
    super.read(paramInputStream);
    request_id = paramInputStream.read_ulong();
    locate_status = paramInputStream.read_long();
    isValidReplyStatus(locate_status);
    if (locate_status == 2)
    {
      CDRInputStream localCDRInputStream = (CDRInputStream)paramInputStream;
      ior = IORFactories.makeIOR(localCDRInputStream);
    }
  }
  
  public void write(OutputStream paramOutputStream)
  {
    super.write(paramOutputStream);
    paramOutputStream.write_ulong(request_id);
    paramOutputStream.write_long(locate_status);
  }
  
  public static void isValidReplyStatus(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
    case 1: 
    case 2: 
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\LocateReplyMessage_1_0.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
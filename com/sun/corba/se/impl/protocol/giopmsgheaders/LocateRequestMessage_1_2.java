package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class LocateRequestMessage_1_2
  extends Message_1_2
  implements LocateRequestMessage
{
  private ORB orb = null;
  private ObjectKey objectKey = null;
  private TargetAddress target = null;
  
  LocateRequestMessage_1_2(ORB paramORB)
  {
    orb = paramORB;
  }
  
  LocateRequestMessage_1_2(ORB paramORB, int paramInt, TargetAddress paramTargetAddress)
  {
    super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)3, 0);
    orb = paramORB;
    request_id = paramInt;
    target = paramTargetAddress;
  }
  
  public int getRequestId()
  {
    return request_id;
  }
  
  public ObjectKey getObjectKey()
  {
    if (objectKey == null) {
      objectKey = MessageBase.extractObjectKey(target, orb);
    }
    return objectKey;
  }
  
  public void read(InputStream paramInputStream)
  {
    super.read(paramInputStream);
    request_id = paramInputStream.read_ulong();
    target = TargetAddressHelper.read(paramInputStream);
    getObjectKey();
  }
  
  public void write(OutputStream paramOutputStream)
  {
    super.write(paramOutputStream);
    paramOutputStream.write_ulong(request_id);
    nullCheck(target);
    TargetAddressHelper.write(paramOutputStream, target);
  }
  
  public void callback(MessageHandler paramMessageHandler)
    throws IOException
  {
    paramMessageHandler.handleInput(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\LocateRequestMessage_1_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
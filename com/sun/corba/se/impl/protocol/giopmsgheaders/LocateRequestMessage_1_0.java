package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class LocateRequestMessage_1_0
  extends Message_1_0
  implements LocateRequestMessage
{
  private ORB orb = null;
  private int request_id = 0;
  private byte[] object_key = null;
  private ObjectKey objectKey = null;
  
  LocateRequestMessage_1_0(ORB paramORB)
  {
    orb = paramORB;
  }
  
  LocateRequestMessage_1_0(ORB paramORB, int paramInt, byte[] paramArrayOfByte)
  {
    super(1195986768, false, (byte)3, 0);
    orb = paramORB;
    request_id = paramInt;
    object_key = paramArrayOfByte;
  }
  
  public int getRequestId()
  {
    return request_id;
  }
  
  public ObjectKey getObjectKey()
  {
    if (objectKey == null) {
      objectKey = MessageBase.extractObjectKey(object_key, orb);
    }
    return objectKey;
  }
  
  public void read(InputStream paramInputStream)
  {
    super.read(paramInputStream);
    request_id = paramInputStream.read_ulong();
    int i = paramInputStream.read_long();
    object_key = new byte[i];
    paramInputStream.read_octet_array(object_key, 0, i);
  }
  
  public void write(OutputStream paramOutputStream)
  {
    super.write(paramOutputStream);
    paramOutputStream.write_ulong(request_id);
    nullCheck(object_key);
    paramOutputStream.write_long(object_key.length);
    paramOutputStream.write_octet_array(object_key, 0, object_key.length);
  }
  
  public void callback(MessageHandler paramMessageHandler)
    throws IOException
  {
    paramMessageHandler.handleInput(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\LocateRequestMessage_1_0.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
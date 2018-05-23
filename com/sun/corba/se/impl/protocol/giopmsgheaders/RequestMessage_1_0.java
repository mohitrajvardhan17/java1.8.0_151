package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import java.io.IOException;
import org.omg.CORBA.Principal;

public final class RequestMessage_1_0
  extends Message_1_0
  implements RequestMessage
{
  private ORB orb = null;
  private ServiceContexts service_contexts = null;
  private int request_id = 0;
  private boolean response_expected = false;
  private byte[] object_key = null;
  private String operation = null;
  private Principal requesting_principal = null;
  private ObjectKey objectKey = null;
  
  RequestMessage_1_0(ORB paramORB)
  {
    orb = paramORB;
  }
  
  RequestMessage_1_0(ORB paramORB, ServiceContexts paramServiceContexts, int paramInt, boolean paramBoolean, byte[] paramArrayOfByte, String paramString, Principal paramPrincipal)
  {
    super(1195986768, false, (byte)0, 0);
    orb = paramORB;
    service_contexts = paramServiceContexts;
    request_id = paramInt;
    response_expected = paramBoolean;
    object_key = paramArrayOfByte;
    operation = paramString;
    requesting_principal = paramPrincipal;
  }
  
  public ServiceContexts getServiceContexts()
  {
    return service_contexts;
  }
  
  public int getRequestId()
  {
    return request_id;
  }
  
  public boolean isResponseExpected()
  {
    return response_expected;
  }
  
  public byte[] getReserved()
  {
    return null;
  }
  
  public ObjectKey getObjectKey()
  {
    if (objectKey == null) {
      objectKey = MessageBase.extractObjectKey(object_key, orb);
    }
    return objectKey;
  }
  
  public String getOperation()
  {
    return operation;
  }
  
  public Principal getPrincipal()
  {
    return requesting_principal;
  }
  
  public void setThreadPoolToUse(int paramInt) {}
  
  public void read(org.omg.CORBA.portable.InputStream paramInputStream)
  {
    super.read(paramInputStream);
    service_contexts = new ServiceContexts((org.omg.CORBA_2_3.portable.InputStream)paramInputStream);
    request_id = paramInputStream.read_ulong();
    response_expected = paramInputStream.read_boolean();
    int i = paramInputStream.read_long();
    object_key = new byte[i];
    paramInputStream.read_octet_array(object_key, 0, i);
    operation = paramInputStream.read_string();
    requesting_principal = paramInputStream.read_Principal();
  }
  
  public void write(org.omg.CORBA.portable.OutputStream paramOutputStream)
  {
    super.write(paramOutputStream);
    if (service_contexts != null) {
      service_contexts.write((org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream, GIOPVersion.V1_0);
    } else {
      ServiceContexts.writeNullServiceContext((org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream);
    }
    paramOutputStream.write_ulong(request_id);
    paramOutputStream.write_boolean(response_expected);
    nullCheck(object_key);
    paramOutputStream.write_long(object_key.length);
    paramOutputStream.write_octet_array(object_key, 0, object_key.length);
    paramOutputStream.write_string(operation);
    if (requesting_principal != null) {
      paramOutputStream.write_Principal(requesting_principal);
    } else {
      paramOutputStream.write_long(0);
    }
  }
  
  public void callback(MessageHandler paramMessageHandler)
    throws IOException
  {
    paramMessageHandler.handleInput(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\RequestMessage_1_0.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
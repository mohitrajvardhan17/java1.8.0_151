package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import java.io.IOException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Principal;

public final class RequestMessage_1_2
  extends Message_1_2
  implements RequestMessage
{
  private ORB orb = null;
  private ORBUtilSystemException wrapper = null;
  private byte response_flags = 0;
  private byte[] reserved = null;
  private TargetAddress target = null;
  private String operation = null;
  private ServiceContexts service_contexts = null;
  private ObjectKey objectKey = null;
  
  RequestMessage_1_2(ORB paramORB)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
  }
  
  RequestMessage_1_2(ORB paramORB, int paramInt, byte paramByte, byte[] paramArrayOfByte, TargetAddress paramTargetAddress, String paramString, ServiceContexts paramServiceContexts)
  {
    super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)0, 0);
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    request_id = paramInt;
    response_flags = paramByte;
    reserved = paramArrayOfByte;
    target = paramTargetAddress;
    operation = paramString;
    service_contexts = paramServiceContexts;
  }
  
  public int getRequestId()
  {
    return request_id;
  }
  
  public boolean isResponseExpected()
  {
    return (response_flags & 0x1) == 1;
  }
  
  public byte[] getReserved()
  {
    return reserved;
  }
  
  public ObjectKey getObjectKey()
  {
    if (objectKey == null) {
      objectKey = MessageBase.extractObjectKey(target, orb);
    }
    return objectKey;
  }
  
  public String getOperation()
  {
    return operation;
  }
  
  public Principal getPrincipal()
  {
    return null;
  }
  
  public ServiceContexts getServiceContexts()
  {
    return service_contexts;
  }
  
  public void read(org.omg.CORBA.portable.InputStream paramInputStream)
  {
    super.read(paramInputStream);
    request_id = paramInputStream.read_ulong();
    response_flags = paramInputStream.read_octet();
    reserved = new byte[3];
    for (int i = 0; i < 3; i++) {
      reserved[i] = paramInputStream.read_octet();
    }
    target = TargetAddressHelper.read(paramInputStream);
    getObjectKey();
    operation = paramInputStream.read_string();
    service_contexts = new ServiceContexts((org.omg.CORBA_2_3.portable.InputStream)paramInputStream);
    ((CDRInputStream)paramInputStream).setHeaderPadding(true);
  }
  
  public void write(org.omg.CORBA.portable.OutputStream paramOutputStream)
  {
    super.write(paramOutputStream);
    paramOutputStream.write_ulong(request_id);
    paramOutputStream.write_octet(response_flags);
    nullCheck(reserved);
    if (reserved.length != 3) {
      throw wrapper.badReservedLength(CompletionStatus.COMPLETED_MAYBE);
    }
    for (int i = 0; i < 3; i++) {
      paramOutputStream.write_octet(reserved[i]);
    }
    nullCheck(target);
    TargetAddressHelper.write(paramOutputStream, target);
    paramOutputStream.write_string(operation);
    if (service_contexts != null) {
      service_contexts.write((org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream, GIOPVersion.V1_2);
    } else {
      ServiceContexts.writeNullServiceContext((org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream);
    }
    ((CDROutputStream)paramOutputStream).setHeaderPadding(true);
  }
  
  public void callback(MessageHandler paramMessageHandler)
    throws IOException
  {
    paramMessageHandler.handleInput(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\RequestMessage_1_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.protocol.PIHandler;
import java.nio.ByteBuffer;
import org.omg.CORBA.SystemException;

public class BufferManagerWriteStream
  extends BufferManagerWrite
{
  private int fragmentCount = 0;
  
  BufferManagerWriteStream(ORB paramORB)
  {
    super(paramORB);
  }
  
  public boolean sentFragment()
  {
    return fragmentCount > 0;
  }
  
  public int getBufferSize()
  {
    return orb.getORBData().getGIOPFragmentSize();
  }
  
  public void overflow(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    MessageBase.setFlag(byteBuffer, 2);
    try
    {
      sendFragment(false);
    }
    catch (SystemException localSystemException)
    {
      orb.getPIHandler().invokeClientPIEndingPoint(2, localSystemException);
      throw localSystemException;
    }
    paramByteBufferWithInfo.position(0);
    buflen = byteBuffer.limit();
    fragmented = true;
    FragmentMessage localFragmentMessage = ((CDROutputObject)outputObject).getMessageHeader().createFragmentMessage();
    localFragmentMessage.write((CDROutputObject)outputObject);
  }
  
  /* Error */
  private void sendFragment(boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 113	com/sun/corba/se/impl/encoding/BufferManagerWriteStream:outputObject	Ljava/lang/Object;
    //   4: checkcast 63	com/sun/corba/se/pept/encoding/OutputObject
    //   7: invokeinterface 127 1 0
    //   12: invokeinterface 128 1 0
    //   17: astore_2
    //   18: aload_2
    //   19: invokeinterface 129 1 0
    //   24: aload_2
    //   25: aload_0
    //   26: getfield 113	com/sun/corba/se/impl/encoding/BufferManagerWriteStream:outputObject	Ljava/lang/Object;
    //   29: checkcast 63	com/sun/corba/se/pept/encoding/OutputObject
    //   32: invokeinterface 131 2 0
    //   37: aload_0
    //   38: dup
    //   39: getfield 110	com/sun/corba/se/impl/encoding/BufferManagerWriteStream:fragmentCount	I
    //   42: iconst_1
    //   43: iadd
    //   44: putfield 110	com/sun/corba/se/impl/encoding/BufferManagerWriteStream:fragmentCount	I
    //   47: aload_2
    //   48: invokeinterface 130 1 0
    //   53: goto +12 -> 65
    //   56: astore_3
    //   57: aload_2
    //   58: invokeinterface 130 1 0
    //   63: aload_3
    //   64: athrow
    //   65: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	66	0	this	BufferManagerWriteStream
    //   0	66	1	paramBoolean	boolean
    //   17	41	2	localConnection	com.sun.corba.se.pept.transport.Connection
    //   56	8	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   24	47	56	finally
  }
  
  public void sendMessage()
  {
    sendFragment(true);
    sentFullMessage = true;
  }
  
  public void close() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\BufferManagerWriteStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;

public class BufferManagerWriteGrow
  extends BufferManagerWrite
{
  BufferManagerWriteGrow(ORB paramORB)
  {
    super(paramORB);
  }
  
  public boolean sentFragment()
  {
    return false;
  }
  
  public int getBufferSize()
  {
    return orb.getORBData().getGIOPBufferSize();
  }
  
  public void overflow(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    paramByteBufferWithInfo.growBuffer(orb);
    fragmented = false;
  }
  
  /* Error */
  public void sendMessage()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 66	com/sun/corba/se/impl/encoding/BufferManagerWriteGrow:outputObject	Ljava/lang/Object;
    //   4: checkcast 38	com/sun/corba/se/pept/encoding/OutputObject
    //   7: invokeinterface 71 1 0
    //   12: invokeinterface 72 1 0
    //   17: astore_1
    //   18: aload_1
    //   19: invokeinterface 73 1 0
    //   24: aload_1
    //   25: aload_0
    //   26: getfield 66	com/sun/corba/se/impl/encoding/BufferManagerWriteGrow:outputObject	Ljava/lang/Object;
    //   29: checkcast 38	com/sun/corba/se/pept/encoding/OutputObject
    //   32: invokeinterface 75 2 0
    //   37: aload_0
    //   38: iconst_1
    //   39: putfield 64	com/sun/corba/se/impl/encoding/BufferManagerWriteGrow:sentFullMessage	Z
    //   42: aload_1
    //   43: invokeinterface 74 1 0
    //   48: goto +12 -> 60
    //   51: astore_2
    //   52: aload_1
    //   53: invokeinterface 74 1 0
    //   58: aload_2
    //   59: athrow
    //   60: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	61	0	this	BufferManagerWriteGrow
    //   17	36	1	localConnection	com.sun.corba.se.pept.transport.Connection
    //   51	8	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   24	42	51	finally
  }
  
  public void close() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\BufferManagerWriteGrow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import java.nio.ByteBuffer;

public abstract interface BufferManagerRead
{
  public abstract void processFragment(ByteBuffer paramByteBuffer, FragmentMessage paramFragmentMessage);
  
  public abstract ByteBufferWithInfo underflow(ByteBufferWithInfo paramByteBufferWithInfo);
  
  public abstract void init(Message paramMessage);
  
  public abstract MarkAndResetHandler getMarkAndResetHandler();
  
  public abstract void cancelProcessing(int paramInt);
  
  public abstract void close(ByteBufferWithInfo paramByteBufferWithInfo);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\BufferManagerRead.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
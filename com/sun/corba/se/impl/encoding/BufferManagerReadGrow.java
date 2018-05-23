package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.spi.orb.ORB;
import java.nio.ByteBuffer;

public class BufferManagerReadGrow
  implements BufferManagerRead, MarkAndResetHandler
{
  private ORB orb;
  private ORBUtilSystemException wrapper;
  private Object streamMemento;
  private RestorableInputStream inputStream;
  private boolean markEngaged = false;
  
  BufferManagerReadGrow(ORB paramORB)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.encoding");
  }
  
  public void processFragment(ByteBuffer paramByteBuffer, FragmentMessage paramFragmentMessage) {}
  
  public void init(Message paramMessage) {}
  
  public ByteBufferWithInfo underflow(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    throw wrapper.unexpectedEof();
  }
  
  public void cancelProcessing(int paramInt) {}
  
  public MarkAndResetHandler getMarkAndResetHandler()
  {
    return this;
  }
  
  public void mark(RestorableInputStream paramRestorableInputStream)
  {
    markEngaged = true;
    inputStream = paramRestorableInputStream;
    streamMemento = inputStream.createStreamMemento();
  }
  
  public void fragmentationOccured(ByteBufferWithInfo paramByteBufferWithInfo) {}
  
  public void reset()
  {
    if (!markEngaged) {
      return;
    }
    markEngaged = false;
    inputStream.restoreInternalState(streamMemento);
    streamMemento = null;
  }
  
  public void close(ByteBufferWithInfo paramByteBufferWithInfo) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\BufferManagerReadGrow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;

public abstract class BufferManagerWrite
{
  protected ORB orb;
  protected ORBUtilSystemException wrapper;
  protected Object outputObject;
  protected boolean sentFullMessage = false;
  
  BufferManagerWrite(ORB paramORB)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.encoding");
  }
  
  public abstract boolean sentFragment();
  
  public boolean sentFullMessage()
  {
    return sentFullMessage;
  }
  
  public abstract int getBufferSize();
  
  public abstract void overflow(ByteBufferWithInfo paramByteBufferWithInfo);
  
  public abstract void sendMessage();
  
  public void setOutputObject(Object paramObject)
  {
    outputObject = paramObject;
  }
  
  public abstract void close();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\BufferManagerWrite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
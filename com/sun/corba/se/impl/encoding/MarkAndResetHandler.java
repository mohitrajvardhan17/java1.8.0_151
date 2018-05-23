package com.sun.corba.se.impl.encoding;

abstract interface MarkAndResetHandler
{
  public abstract void mark(RestorableInputStream paramRestorableInputStream);
  
  public abstract void fragmentationOccured(ByteBufferWithInfo paramByteBufferWithInfo);
  
  public abstract void reset();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\MarkAndResetHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.corba.se.impl.encoding;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class BufferQueue
{
  private LinkedList list = new LinkedList();
  
  public BufferQueue() {}
  
  public void enqueue(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    list.addLast(paramByteBufferWithInfo);
  }
  
  public ByteBufferWithInfo dequeue()
    throws NoSuchElementException
  {
    return (ByteBufferWithInfo)list.removeFirst();
  }
  
  public int size()
  {
    return list.size();
  }
  
  public void push(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    list.addFirst(paramByteBufferWithInfo);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\BufferQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
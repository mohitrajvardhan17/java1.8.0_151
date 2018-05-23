package com.sun.xml.internal.stream.util;

import java.lang.ref.SoftReference;

public class ThreadLocalBufferAllocator
{
  private static ThreadLocal tlba = new ThreadLocal();
  
  public ThreadLocalBufferAllocator() {}
  
  public static BufferAllocator getBufferAllocator()
  {
    SoftReference localSoftReference = (SoftReference)tlba.get();
    if ((localSoftReference == null) || (localSoftReference.get() == null))
    {
      localSoftReference = new SoftReference(new BufferAllocator());
      tlba.set(localSoftReference);
    }
    return (BufferAllocator)localSoftReference.get();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\util\ThreadLocalBufferAllocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
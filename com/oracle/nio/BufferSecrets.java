package com.oracle.nio;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import sun.misc.JavaNioAccess;
import sun.misc.SharedSecrets;
import sun.nio.ch.DirectBuffer;

public final class BufferSecrets<A>
{
  private static final JavaNioAccess javaNioAccess = ;
  private static final BufferSecrets<?> theBufferSecrets = new BufferSecrets();
  
  private BufferSecrets() {}
  
  public static <A> BufferSecrets<A> instance()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new BufferSecretsPermission("access"));
    }
    return theBufferSecrets;
  }
  
  public ByteBuffer newDirectByteBuffer(long paramLong, int paramInt, A paramA)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Negative capacity: " + paramInt);
    }
    return javaNioAccess.newDirectByteBuffer(paramLong, paramInt, paramA);
  }
  
  public long address(Buffer paramBuffer)
  {
    if ((paramBuffer instanceof DirectBuffer)) {
      return ((DirectBuffer)paramBuffer).address();
    }
    if (paramBuffer == null) {
      throw new NullPointerException();
    }
    throw new UnsupportedOperationException();
  }
  
  public A attachment(Buffer paramBuffer)
  {
    if ((paramBuffer instanceof DirectBuffer)) {
      return (A)((DirectBuffer)paramBuffer).attachment();
    }
    if (paramBuffer == null) {
      throw new NullPointerException();
    }
    return null;
  }
  
  public void truncate(Buffer paramBuffer)
  {
    javaNioAccess.truncate(paramBuffer);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\nio\BufferSecrets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
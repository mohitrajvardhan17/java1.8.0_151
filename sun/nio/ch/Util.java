package sun.nio.ch;

import java.io.FileDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.misc.VM;
import sun.security.action.GetPropertyAction;

public class Util
{
  private static final int TEMP_BUF_POOL_SIZE = IOUtil.IOV_MAX;
  private static final long MAX_CACHED_BUFFER_SIZE = getMaxCachedBufferSize();
  private static ThreadLocal<BufferCache> bufferCache = new ThreadLocal()
  {
    protected Util.BufferCache initialValue()
    {
      return new Util.BufferCache();
    }
  };
  private static Unsafe unsafe = Unsafe.getUnsafe();
  private static int pageSize = -1;
  private static volatile Constructor<?> directByteBufferConstructor = null;
  private static volatile Constructor<?> directByteBufferRConstructor = null;
  private static volatile String bugLevel = null;
  
  public Util() {}
  
  private static long getMaxCachedBufferSize()
  {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        return System.getProperty("jdk.nio.maxCachedBufferSize");
      }
    });
    if (str != null) {
      try
      {
        long l = Long.parseLong(str);
        if (l >= 0L) {
          return l;
        }
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    return Long.MAX_VALUE;
  }
  
  private static boolean isBufferTooLarge(int paramInt)
  {
    return paramInt > MAX_CACHED_BUFFER_SIZE;
  }
  
  private static boolean isBufferTooLarge(ByteBuffer paramByteBuffer)
  {
    return isBufferTooLarge(paramByteBuffer.capacity());
  }
  
  public static ByteBuffer getTemporaryDirectBuffer(int paramInt)
  {
    if (isBufferTooLarge(paramInt)) {
      return ByteBuffer.allocateDirect(paramInt);
    }
    BufferCache localBufferCache = (BufferCache)bufferCache.get();
    ByteBuffer localByteBuffer = localBufferCache.get(paramInt);
    if (localByteBuffer != null) {
      return localByteBuffer;
    }
    if (!localBufferCache.isEmpty())
    {
      localByteBuffer = localBufferCache.removeFirst();
      free(localByteBuffer);
    }
    return ByteBuffer.allocateDirect(paramInt);
  }
  
  public static void releaseTemporaryDirectBuffer(ByteBuffer paramByteBuffer)
  {
    offerFirstTemporaryDirectBuffer(paramByteBuffer);
  }
  
  static void offerFirstTemporaryDirectBuffer(ByteBuffer paramByteBuffer)
  {
    if (isBufferTooLarge(paramByteBuffer))
    {
      free(paramByteBuffer);
      return;
    }
    assert (paramByteBuffer != null);
    BufferCache localBufferCache = (BufferCache)bufferCache.get();
    if (!localBufferCache.offerFirst(paramByteBuffer)) {
      free(paramByteBuffer);
    }
  }
  
  static void offerLastTemporaryDirectBuffer(ByteBuffer paramByteBuffer)
  {
    if (isBufferTooLarge(paramByteBuffer))
    {
      free(paramByteBuffer);
      return;
    }
    assert (paramByteBuffer != null);
    BufferCache localBufferCache = (BufferCache)bufferCache.get();
    if (!localBufferCache.offerLast(paramByteBuffer)) {
      free(paramByteBuffer);
    }
  }
  
  private static void free(ByteBuffer paramByteBuffer)
  {
    ((DirectBuffer)paramByteBuffer).cleaner().clean();
  }
  
  static ByteBuffer[] subsequence(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 0) && (paramInt2 == paramArrayOfByteBuffer.length)) {
      return paramArrayOfByteBuffer;
    }
    int i = paramInt2;
    ByteBuffer[] arrayOfByteBuffer = new ByteBuffer[i];
    for (int j = 0; j < i; j++) {
      arrayOfByteBuffer[j] = paramArrayOfByteBuffer[(paramInt1 + j)];
    }
    return arrayOfByteBuffer;
  }
  
  static <E> Set<E> ungrowableSet(Set<E> paramSet)
  {
    new Set()
    {
      public int size()
      {
        return val$s.size();
      }
      
      public boolean isEmpty()
      {
        return val$s.isEmpty();
      }
      
      public boolean contains(Object paramAnonymousObject)
      {
        return val$s.contains(paramAnonymousObject);
      }
      
      public Object[] toArray()
      {
        return val$s.toArray();
      }
      
      public <T> T[] toArray(T[] paramAnonymousArrayOfT)
      {
        return val$s.toArray(paramAnonymousArrayOfT);
      }
      
      public String toString()
      {
        return val$s.toString();
      }
      
      public Iterator<E> iterator()
      {
        return val$s.iterator();
      }
      
      public boolean equals(Object paramAnonymousObject)
      {
        return val$s.equals(paramAnonymousObject);
      }
      
      public int hashCode()
      {
        return val$s.hashCode();
      }
      
      public void clear()
      {
        val$s.clear();
      }
      
      public boolean remove(Object paramAnonymousObject)
      {
        return val$s.remove(paramAnonymousObject);
      }
      
      public boolean containsAll(Collection<?> paramAnonymousCollection)
      {
        return val$s.containsAll(paramAnonymousCollection);
      }
      
      public boolean removeAll(Collection<?> paramAnonymousCollection)
      {
        return val$s.removeAll(paramAnonymousCollection);
      }
      
      public boolean retainAll(Collection<?> paramAnonymousCollection)
      {
        return val$s.retainAll(paramAnonymousCollection);
      }
      
      public boolean add(E paramAnonymousE)
      {
        throw new UnsupportedOperationException();
      }
      
      public boolean addAll(Collection<? extends E> paramAnonymousCollection)
      {
        throw new UnsupportedOperationException();
      }
    };
  }
  
  private static byte _get(long paramLong)
  {
    return unsafe.getByte(paramLong);
  }
  
  private static void _put(long paramLong, byte paramByte)
  {
    unsafe.putByte(paramLong, paramByte);
  }
  
  static void erase(ByteBuffer paramByteBuffer)
  {
    unsafe.setMemory(((DirectBuffer)paramByteBuffer).address(), paramByteBuffer.capacity(), (byte)0);
  }
  
  static Unsafe unsafe()
  {
    return unsafe;
  }
  
  static int pageSize()
  {
    if (pageSize == -1) {
      pageSize = unsafe().pageSize();
    }
    return pageSize;
  }
  
  private static void initDBBConstructor()
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        try
        {
          Class localClass = Class.forName("java.nio.DirectByteBuffer");
          Constructor localConstructor = localClass.getDeclaredConstructor(new Class[] { Integer.TYPE, Long.TYPE, FileDescriptor.class, Runnable.class });
          localConstructor.setAccessible(true);
          Util.access$302(localConstructor);
        }
        catch (ClassNotFoundException|NoSuchMethodException|IllegalArgumentException|ClassCastException localClassNotFoundException)
        {
          throw new InternalError(localClassNotFoundException);
        }
        return null;
      }
    });
  }
  
  static MappedByteBuffer newMappedByteBuffer(int paramInt, long paramLong, FileDescriptor paramFileDescriptor, Runnable paramRunnable)
  {
    if (directByteBufferConstructor == null) {
      initDBBConstructor();
    }
    MappedByteBuffer localMappedByteBuffer;
    try
    {
      localMappedByteBuffer = (MappedByteBuffer)directByteBufferConstructor.newInstance(new Object[] { new Integer(paramInt), new Long(paramLong), paramFileDescriptor, paramRunnable });
    }
    catch (InstantiationException|IllegalAccessException|InvocationTargetException localInstantiationException)
    {
      throw new InternalError(localInstantiationException);
    }
    return localMappedByteBuffer;
  }
  
  private static void initDBBRConstructor()
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        try
        {
          Class localClass = Class.forName("java.nio.DirectByteBufferR");
          Constructor localConstructor = localClass.getDeclaredConstructor(new Class[] { Integer.TYPE, Long.TYPE, FileDescriptor.class, Runnable.class });
          localConstructor.setAccessible(true);
          Util.access$402(localConstructor);
        }
        catch (ClassNotFoundException|NoSuchMethodException|IllegalArgumentException|ClassCastException localClassNotFoundException)
        {
          throw new InternalError(localClassNotFoundException);
        }
        return null;
      }
    });
  }
  
  static MappedByteBuffer newMappedByteBufferR(int paramInt, long paramLong, FileDescriptor paramFileDescriptor, Runnable paramRunnable)
  {
    if (directByteBufferRConstructor == null) {
      initDBBRConstructor();
    }
    MappedByteBuffer localMappedByteBuffer;
    try
    {
      localMappedByteBuffer = (MappedByteBuffer)directByteBufferRConstructor.newInstance(new Object[] { new Integer(paramInt), new Long(paramLong), paramFileDescriptor, paramRunnable });
    }
    catch (InstantiationException|IllegalAccessException|InvocationTargetException localInstantiationException)
    {
      throw new InternalError(localInstantiationException);
    }
    return localMappedByteBuffer;
  }
  
  static boolean atBugLevel(String paramString)
  {
    if (bugLevel == null)
    {
      if (!VM.isBooted()) {
        return false;
      }
      String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.nio.ch.bugLevel"));
      bugLevel = str != null ? str : "";
    }
    return bugLevel.equals(paramString);
  }
  
  private static class BufferCache
  {
    private ByteBuffer[] buffers = new ByteBuffer[Util.TEMP_BUF_POOL_SIZE];
    private int count;
    private int start;
    
    private int next(int paramInt)
    {
      return (paramInt + 1) % Util.TEMP_BUF_POOL_SIZE;
    }
    
    BufferCache() {}
    
    ByteBuffer get(int paramInt)
    {
      assert (!Util.isBufferTooLarge(paramInt));
      if (count == 0) {
        return null;
      }
      ByteBuffer[] arrayOfByteBuffer = buffers;
      Object localObject = arrayOfByteBuffer[start];
      if (((ByteBuffer)localObject).capacity() < paramInt)
      {
        localObject = null;
        int i = start;
        while ((i = next(i)) != start)
        {
          ByteBuffer localByteBuffer = arrayOfByteBuffer[i];
          if (localByteBuffer == null) {
            break;
          }
          if (localByteBuffer.capacity() >= paramInt)
          {
            localObject = localByteBuffer;
            break;
          }
        }
        if (localObject == null) {
          return null;
        }
        arrayOfByteBuffer[i] = arrayOfByteBuffer[start];
      }
      arrayOfByteBuffer[start] = null;
      start = next(start);
      count -= 1;
      ((ByteBuffer)localObject).rewind();
      ((ByteBuffer)localObject).limit(paramInt);
      return (ByteBuffer)localObject;
    }
    
    boolean offerFirst(ByteBuffer paramByteBuffer)
    {
      assert (!Util.isBufferTooLarge(paramByteBuffer));
      if (count >= Util.TEMP_BUF_POOL_SIZE) {
        return false;
      }
      start = ((start + Util.TEMP_BUF_POOL_SIZE - 1) % Util.TEMP_BUF_POOL_SIZE);
      buffers[start] = paramByteBuffer;
      count += 1;
      return true;
    }
    
    boolean offerLast(ByteBuffer paramByteBuffer)
    {
      assert (!Util.isBufferTooLarge(paramByteBuffer));
      if (count >= Util.TEMP_BUF_POOL_SIZE) {
        return false;
      }
      int i = (start + count) % Util.TEMP_BUF_POOL_SIZE;
      buffers[i] = paramByteBuffer;
      count += 1;
      return true;
    }
    
    boolean isEmpty()
    {
      return count == 0;
    }
    
    ByteBuffer removeFirst()
    {
      assert (count > 0);
      ByteBuffer localByteBuffer = buffers[start];
      buffers[start] = null;
      start = next(start);
      count -= 1;
      return localByteBuffer;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.tracing.dtrace;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;

class SystemResource
  extends WeakReference<Activation>
{
  private long handle;
  private static ReferenceQueue<Activation> referenceQueue = referenceQueue = new ReferenceQueue();
  static HashSet<SystemResource> resources = new HashSet();
  
  SystemResource(Activation paramActivation, long paramLong)
  {
    super(paramActivation, referenceQueue);
    handle = paramLong;
    flush();
    resources.add(this);
  }
  
  void dispose()
  {
    JVM.dispose(handle);
    resources.remove(this);
    handle = 0L;
  }
  
  static void flush()
  {
    SystemResource localSystemResource = null;
    while ((localSystemResource = (SystemResource)referenceQueue.poll()) != null) {
      if (handle != 0L) {
        localSystemResource.dispose();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tracing\dtrace\SystemResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
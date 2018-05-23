package sun.net.www.http;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.net.ProgressSource;
import sun.net.www.MeteredStream;

public class KeepAliveStream
  extends MeteredStream
  implements Hurryable
{
  HttpClient hc;
  boolean hurried;
  protected boolean queuedForCleanup = false;
  private static final KeepAliveStreamCleaner queue = new KeepAliveStreamCleaner();
  private static Thread cleanerThread;
  
  public KeepAliveStream(InputStream paramInputStream, ProgressSource paramProgressSource, long paramLong, HttpClient paramHttpClient)
  {
    super(paramInputStream, paramProgressSource, paramLong);
    hc = paramHttpClient;
  }
  
  /* Error */
  public void close()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 141	sun/net/www/http/KeepAliveStream:closed	Z
    //   4: ifeq +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: getfield 143	sun/net/www/http/KeepAliveStream:queuedForCleanup	Z
    //   12: ifeq +4 -> 16
    //   15: return
    //   16: aload_0
    //   17: getfield 140	sun/net/www/http/KeepAliveStream:expected	J
    //   20: aload_0
    //   21: getfield 139	sun/net/www/http/KeepAliveStream:count	J
    //   24: lcmp
    //   25: ifle +104 -> 129
    //   28: aload_0
    //   29: getfield 140	sun/net/www/http/KeepAliveStream:expected	J
    //   32: aload_0
    //   33: getfield 139	sun/net/www/http/KeepAliveStream:count	J
    //   36: lsub
    //   37: lstore_1
    //   38: lload_1
    //   39: aload_0
    //   40: invokevirtual 167	sun/net/www/http/KeepAliveStream:available	()I
    //   43: i2l
    //   44: lcmp
    //   45: ifgt +40 -> 85
    //   48: aload_0
    //   49: getfield 140	sun/net/www/http/KeepAliveStream:expected	J
    //   52: aload_0
    //   53: getfield 139	sun/net/www/http/KeepAliveStream:count	J
    //   56: lsub
    //   57: dup2
    //   58: lstore_1
    //   59: lconst_0
    //   60: lcmp
    //   61: ifle +68 -> 129
    //   64: aload_0
    //   65: lload_1
    //   66: aload_0
    //   67: invokevirtual 167	sun/net/www/http/KeepAliveStream:available	()I
    //   70: i2l
    //   71: invokestatic 155	java/lang/Math:min	(JJ)J
    //   74: invokevirtual 168	sun/net/www/http/KeepAliveStream:skip	(J)J
    //   77: lconst_0
    //   78: lcmp
    //   79: ifgt -31 -> 48
    //   82: goto +47 -> 129
    //   85: aload_0
    //   86: getfield 140	sun/net/www/http/KeepAliveStream:expected	J
    //   89: getstatic 149	sun/net/www/http/KeepAliveStreamCleaner:MAX_DATA_REMAINING	I
    //   92: i2l
    //   93: lcmp
    //   94: ifgt +28 -> 122
    //   97: aload_0
    //   98: getfield 142	sun/net/www/http/KeepAliveStream:hurried	Z
    //   101: ifne +21 -> 122
    //   104: new 87	sun/net/www/http/KeepAliveCleanerEntry
    //   107: dup
    //   108: aload_0
    //   109: aload_0
    //   110: getfield 147	sun/net/www/http/KeepAliveStream:hc	Lsun/net/www/http/HttpClient;
    //   113: invokespecial 166	sun/net/www/http/KeepAliveCleanerEntry:<init>	(Lsun/net/www/http/KeepAliveStream;Lsun/net/www/http/HttpClient;)V
    //   116: invokestatic 169	sun/net/www/http/KeepAliveStream:queueForCleanup	(Lsun/net/www/http/KeepAliveCleanerEntry;)V
    //   119: goto +10 -> 129
    //   122: aload_0
    //   123: getfield 147	sun/net/www/http/KeepAliveStream:hc	Lsun/net/www/http/HttpClient;
    //   126: invokevirtual 161	sun/net/www/http/HttpClient:closeServer	()V
    //   129: aload_0
    //   130: getfield 141	sun/net/www/http/KeepAliveStream:closed	Z
    //   133: ifne +24 -> 157
    //   136: aload_0
    //   137: getfield 142	sun/net/www/http/KeepAliveStream:hurried	Z
    //   140: ifne +17 -> 157
    //   143: aload_0
    //   144: getfield 143	sun/net/www/http/KeepAliveStream:queuedForCleanup	Z
    //   147: ifne +10 -> 157
    //   150: aload_0
    //   151: getfield 147	sun/net/www/http/KeepAliveStream:hc	Lsun/net/www/http/HttpClient;
    //   154: invokevirtual 162	sun/net/www/http/HttpClient:finished	()V
    //   157: aload_0
    //   158: getfield 146	sun/net/www/http/KeepAliveStream:pi	Lsun/net/ProgressSource;
    //   161: ifnull +10 -> 171
    //   164: aload_0
    //   165: getfield 146	sun/net/www/http/KeepAliveStream:pi	Lsun/net/ProgressSource;
    //   168: invokevirtual 159	sun/net/ProgressSource:finishTracking	()V
    //   171: aload_0
    //   172: getfield 143	sun/net/www/http/KeepAliveStream:queuedForCleanup	Z
    //   175: ifne +60 -> 235
    //   178: aload_0
    //   179: aconst_null
    //   180: putfield 144	sun/net/www/http/KeepAliveStream:in	Ljava/io/InputStream;
    //   183: aload_0
    //   184: aconst_null
    //   185: putfield 147	sun/net/www/http/KeepAliveStream:hc	Lsun/net/www/http/HttpClient;
    //   188: aload_0
    //   189: iconst_1
    //   190: putfield 141	sun/net/www/http/KeepAliveStream:closed	Z
    //   193: goto +42 -> 235
    //   196: astore_3
    //   197: aload_0
    //   198: getfield 146	sun/net/www/http/KeepAliveStream:pi	Lsun/net/ProgressSource;
    //   201: ifnull +10 -> 211
    //   204: aload_0
    //   205: getfield 146	sun/net/www/http/KeepAliveStream:pi	Lsun/net/ProgressSource;
    //   208: invokevirtual 159	sun/net/ProgressSource:finishTracking	()V
    //   211: aload_0
    //   212: getfield 143	sun/net/www/http/KeepAliveStream:queuedForCleanup	Z
    //   215: ifne +18 -> 233
    //   218: aload_0
    //   219: aconst_null
    //   220: putfield 144	sun/net/www/http/KeepAliveStream:in	Ljava/io/InputStream;
    //   223: aload_0
    //   224: aconst_null
    //   225: putfield 147	sun/net/www/http/KeepAliveStream:hc	Lsun/net/www/http/HttpClient;
    //   228: aload_0
    //   229: iconst_1
    //   230: putfield 141	sun/net/www/http/KeepAliveStream:closed	Z
    //   233: aload_3
    //   234: athrow
    //   235: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	236	0	this	KeepAliveStream
    //   37	29	1	l	long
    //   196	38	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   16	157	196	finally
  }
  
  public boolean markSupported()
  {
    return false;
  }
  
  public void mark(int paramInt) {}
  
  public void reset()
    throws IOException
  {
    throw new IOException("mark/reset not supported");
  }
  
  public synchronized boolean hurry()
  {
    try
    {
      if ((closed) || (count >= expected)) {
        return false;
      }
      if (in.available() < expected - count) {
        return false;
      }
      int i = (int)(expected - count);
      byte[] arrayOfByte = new byte[i];
      DataInputStream localDataInputStream = new DataInputStream(in);
      localDataInputStream.readFully(arrayOfByte);
      in = new ByteArrayInputStream(arrayOfByte);
      hurried = true;
      return true;
    }
    catch (IOException localIOException) {}
    return false;
  }
  
  private static void queueForCleanup(KeepAliveCleanerEntry paramKeepAliveCleanerEntry)
  {
    synchronized (queue)
    {
      if (!paramKeepAliveCleanerEntry.getQueuedForCleanup())
      {
        if (!queue.offer(paramKeepAliveCleanerEntry))
        {
          paramKeepAliveCleanerEntry.getHttpClient().closeServer();
          return;
        }
        paramKeepAliveCleanerEntry.setQueuedForCleanup();
        queue.notifyAll();
      }
      int i = cleanerThread == null ? 1 : 0;
      if ((i == 0) && (!cleanerThread.isAlive())) {
        i = 1;
      }
      if (i != 0) {
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Void run()
          {
            Object localObject = Thread.currentThread().getThreadGroup();
            ThreadGroup localThreadGroup = null;
            while ((localThreadGroup = ((ThreadGroup)localObject).getParent()) != null) {
              localObject = localThreadGroup;
            }
            KeepAliveStream.access$002(new Thread((ThreadGroup)localObject, KeepAliveStream.queue, "Keep-Alive-SocketCleaner"));
            KeepAliveStream.cleanerThread.setDaemon(true);
            KeepAliveStream.cleanerThread.setPriority(8);
            KeepAliveStream.cleanerThread.setContextClassLoader(null);
            KeepAliveStream.cleanerThread.start();
            return null;
          }
        });
      }
    }
  }
  
  protected long remainingToRead()
  {
    return expected - count;
  }
  
  protected void setClosed()
  {
    in = null;
    hc = null;
    closed = true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\http\KeepAliveStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
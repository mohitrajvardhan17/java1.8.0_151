package com.sun.jmx.remote.internal;

import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationFilter;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.QueryEval;
import javax.management.QueryExp;
import javax.management.remote.NotificationResult;

public class ArrayNotificationBuffer
  implements NotificationBuffer
{
  private boolean disposed = false;
  private static final Object globalLock = new Object();
  private static final HashMap<MBeanServer, ArrayNotificationBuffer> mbsToBuffer = new HashMap(1);
  private final Collection<ShareBuffer> sharers = new HashSet(1);
  private final NotificationListener bufferListener = new BufferListener(null);
  private static final QueryExp broadcasterQuery = new BroadcasterQuery(null);
  private static final NotificationFilter creationFilter;
  private final NotificationListener creationListener = new NotificationListener()
  {
    public void handleNotification(Notification paramAnonymousNotification, Object paramAnonymousObject)
    {
      ArrayNotificationBuffer.logger.debug("creationListener", "handleNotification called");
      ArrayNotificationBuffer.this.createdNotification((MBeanServerNotification)paramAnonymousNotification);
    }
  };
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "ArrayNotificationBuffer");
  private final MBeanServer mBeanServer;
  private final ArrayQueue<NamedNotification> queue;
  private int queueSize;
  private long earliestSequenceNumber;
  private long nextSequenceNumber;
  private Set<ObjectName> createdDuringQuery;
  static final String broadcasterClass = NotificationBroadcaster.class.getName();
  
  public static NotificationBuffer getNotificationBuffer(MBeanServer paramMBeanServer, Map<String, ?> paramMap)
  {
    if (paramMap == null) {
      paramMap = Collections.emptyMap();
    }
    int i = EnvHelp.getNotifBufferSize(paramMap);
    ArrayNotificationBuffer localArrayNotificationBuffer;
    int j;
    ShareBuffer localShareBuffer;
    synchronized (globalLock)
    {
      localArrayNotificationBuffer = (ArrayNotificationBuffer)mbsToBuffer.get(paramMBeanServer);
      j = localArrayNotificationBuffer == null ? 1 : 0;
      if (j != 0)
      {
        localArrayNotificationBuffer = new ArrayNotificationBuffer(paramMBeanServer, i);
        mbsToBuffer.put(paramMBeanServer, localArrayNotificationBuffer);
      }
      ArrayNotificationBuffer tmp71_70 = localArrayNotificationBuffer;
      tmp71_70.getClass();
      localShareBuffer = new ShareBuffer(tmp71_70, i);
    }
    if (j != 0) {
      localArrayNotificationBuffer.createListeners();
    }
    return localShareBuffer;
  }
  
  static void removeNotificationBuffer(MBeanServer paramMBeanServer)
  {
    synchronized (globalLock)
    {
      mbsToBuffer.remove(paramMBeanServer);
    }
  }
  
  void addSharer(ShareBuffer paramShareBuffer)
  {
    synchronized (globalLock)
    {
      synchronized (this)
      {
        if (paramShareBuffer.getSize() > queueSize) {
          resize(paramShareBuffer.getSize());
        }
      }
      sharers.add(paramShareBuffer);
    }
  }
  
  private void removeSharer(ShareBuffer paramShareBuffer)
  {
    boolean bool;
    synchronized (globalLock)
    {
      sharers.remove(paramShareBuffer);
      bool = sharers.isEmpty();
      if (bool)
      {
        removeNotificationBuffer(mBeanServer);
      }
      else
      {
        int i = 0;
        Iterator localIterator = sharers.iterator();
        while (localIterator.hasNext())
        {
          ShareBuffer localShareBuffer = (ShareBuffer)localIterator.next();
          int j = localShareBuffer.getSize();
          if (j > i) {
            i = j;
          }
        }
        if (i < queueSize) {
          resize(i);
        }
      }
    }
    if (bool)
    {
      synchronized (this)
      {
        disposed = true;
        notifyAll();
      }
      destroyListeners();
    }
  }
  
  private synchronized void resize(int paramInt)
  {
    if (paramInt == queueSize) {
      return;
    }
    while (queue.size() > paramInt) {
      dropNotification();
    }
    queue.resize(paramInt);
    queueSize = paramInt;
  }
  
  private ArrayNotificationBuffer(MBeanServer paramMBeanServer, int paramInt)
  {
    if (logger.traceOn()) {
      logger.trace("Constructor", "queueSize=" + paramInt);
    }
    if ((paramMBeanServer == null) || (paramInt < 1)) {
      throw new IllegalArgumentException("Bad args");
    }
    mBeanServer = paramMBeanServer;
    queueSize = paramInt;
    queue = new ArrayQueue(paramInt);
    earliestSequenceNumber = System.currentTimeMillis();
    nextSequenceNumber = earliestSequenceNumber;
    logger.trace("Constructor", "ends");
  }
  
  private synchronized boolean isDisposed()
  {
    return disposed;
  }
  
  public void dispose()
  {
    throw new UnsupportedOperationException();
  }
  
  /* Error */
  public NotificationResult fetchNotifications(NotificationBufferFilter paramNotificationBufferFilter, long paramLong1, long paramLong2, int paramInt)
    throws InterruptedException
  {
    // Byte code:
    //   0: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   3: ldc 38
    //   5: ldc 56
    //   7: invokevirtual 557	com/sun/jmx/remote/util/ClassLogger:trace	(Ljava/lang/String;Ljava/lang/String;)V
    //   10: lload_2
    //   11: lconst_0
    //   12: lcmp
    //   13: iflt +10 -> 23
    //   16: aload_0
    //   17: invokespecial 519	com/sun/jmx/remote/internal/ArrayNotificationBuffer:isDisposed	()Z
    //   20: ifeq +39 -> 59
    //   23: aload_0
    //   24: dup
    //   25: astore 7
    //   27: monitorenter
    //   28: new 329	javax/management/remote/NotificationResult
    //   31: dup
    //   32: aload_0
    //   33: invokevirtual 513	com/sun/jmx/remote/internal/ArrayNotificationBuffer:earliestSequenceNumber	()J
    //   36: aload_0
    //   37: invokevirtual 514	com/sun/jmx/remote/internal/ArrayNotificationBuffer:nextSequenceNumber	()J
    //   40: iconst_0
    //   41: anewarray 330	javax/management/remote/TargetedNotification
    //   44: invokespecial 599	javax/management/remote/NotificationResult:<init>	(JJ[Ljavax/management/remote/TargetedNotification;)V
    //   47: aload 7
    //   49: monitorexit
    //   50: areturn
    //   51: astore 8
    //   53: aload 7
    //   55: monitorexit
    //   56: aload 8
    //   58: athrow
    //   59: aload_1
    //   60: ifnull +21 -> 81
    //   63: lload_2
    //   64: lconst_0
    //   65: lcmp
    //   66: iflt +15 -> 81
    //   69: lload 4
    //   71: lconst_0
    //   72: lcmp
    //   73: iflt +8 -> 81
    //   76: iload 6
    //   78: ifge +23 -> 101
    //   81: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   84: ldc 38
    //   86: ldc 11
    //   88: invokevirtual 557	com/sun/jmx/remote/util/ClassLogger:trace	(Ljava/lang/String;Ljava/lang/String;)V
    //   91: new 297	java/lang/IllegalArgumentException
    //   94: dup
    //   95: ldc 12
    //   97: invokespecial 566	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   100: athrow
    //   101: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   104: invokevirtual 552	com/sun/jmx/remote/util/ClassLogger:debugOn	()Z
    //   107: ifeq +59 -> 166
    //   110: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   113: ldc 38
    //   115: new 303	java/lang/StringBuilder
    //   118: dup
    //   119: invokespecial 572	java/lang/StringBuilder:<init>	()V
    //   122: ldc 39
    //   124: invokevirtual 577	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   127: aload_1
    //   128: invokevirtual 576	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   131: ldc 8
    //   133: invokevirtual 577	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   136: lload_2
    //   137: invokevirtual 575	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   140: ldc 9
    //   142: invokevirtual 577	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   145: lload 4
    //   147: invokevirtual 575	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   150: ldc 7
    //   152: invokevirtual 577	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   155: iload 6
    //   157: invokevirtual 574	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   160: invokevirtual 573	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   163: invokevirtual 557	com/sun/jmx/remote/util/ClassLogger:trace	(Ljava/lang/String;Ljava/lang/String;)V
    //   166: lload_2
    //   167: aload_0
    //   168: invokevirtual 514	com/sun/jmx/remote/internal/ArrayNotificationBuffer:nextSequenceNumber	()J
    //   171: lcmp
    //   172: ifle +56 -> 228
    //   175: new 303	java/lang/StringBuilder
    //   178: dup
    //   179: invokespecial 572	java/lang/StringBuilder:<init>	()V
    //   182: ldc 18
    //   184: invokevirtual 577	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   187: lload_2
    //   188: invokevirtual 575	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   191: ldc 3
    //   193: invokevirtual 577	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   196: aload_0
    //   197: invokevirtual 514	com/sun/jmx/remote/internal/ArrayNotificationBuffer:nextSequenceNumber	()J
    //   200: invokevirtual 575	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   203: invokevirtual 573	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   206: astore 7
    //   208: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   211: ldc 38
    //   213: aload 7
    //   215: invokevirtual 557	com/sun/jmx/remote/util/ClassLogger:trace	(Ljava/lang/String;Ljava/lang/String;)V
    //   218: new 297	java/lang/IllegalArgumentException
    //   221: dup
    //   222: aload 7
    //   224: invokespecial 566	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   227: athrow
    //   228: invokestatic 578	java/lang/System:currentTimeMillis	()J
    //   231: lload 4
    //   233: ladd
    //   234: lstore 7
    //   236: lload 7
    //   238: lconst_0
    //   239: lcmp
    //   240: ifge +8 -> 248
    //   243: ldc2_w 274
    //   246: lstore 7
    //   248: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   251: invokevirtual 552	com/sun/jmx/remote/util/ClassLogger:debugOn	()Z
    //   254: ifeq +31 -> 285
    //   257: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   260: ldc 38
    //   262: new 303	java/lang/StringBuilder
    //   265: dup
    //   266: invokespecial 572	java/lang/StringBuilder:<init>	()V
    //   269: ldc 35
    //   271: invokevirtual 577	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   274: lload 7
    //   276: invokevirtual 575	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   279: invokevirtual 573	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   282: invokevirtual 555	com/sun/jmx/remote/util/ClassLogger:debug	(Ljava/lang/String;Ljava/lang/String;)V
    //   285: ldc2_w 270
    //   288: lstore 9
    //   290: lload_2
    //   291: lstore 11
    //   293: new 312	java/util/ArrayList
    //   296: dup
    //   297: invokespecial 584	java/util/ArrayList:<init>	()V
    //   300: astore 13
    //   302: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   305: ldc 38
    //   307: ldc 44
    //   309: invokevirtual 555	com/sun/jmx/remote/util/ClassLogger:debug	(Ljava/lang/String;Ljava/lang/String;)V
    //   312: aload_0
    //   313: dup
    //   314: astore 15
    //   316: monitorenter
    //   317: lload 9
    //   319: lconst_0
    //   320: lcmp
    //   321: ifge +71 -> 392
    //   324: aload_0
    //   325: invokevirtual 513	com/sun/jmx/remote/internal/ArrayNotificationBuffer:earliestSequenceNumber	()J
    //   328: lstore 9
    //   330: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   333: invokevirtual 552	com/sun/jmx/remote/util/ClassLogger:debugOn	()Z
    //   336: ifeq +31 -> 367
    //   339: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   342: ldc 38
    //   344: new 303	java/lang/StringBuilder
    //   347: dup
    //   348: invokespecial 572	java/lang/StringBuilder:<init>	()V
    //   351: ldc 34
    //   353: invokevirtual 577	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   356: lload 9
    //   358: invokevirtual 575	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   361: invokevirtual 573	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   364: invokevirtual 555	com/sun/jmx/remote/util/ClassLogger:debug	(Ljava/lang/String;Ljava/lang/String;)V
    //   367: lload 11
    //   369: lload 9
    //   371: lcmp
    //   372: ifge +26 -> 398
    //   375: lload 9
    //   377: lstore 11
    //   379: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   382: ldc 38
    //   384: ldc 47
    //   386: invokevirtual 555	com/sun/jmx/remote/util/ClassLogger:debug	(Ljava/lang/String;Ljava/lang/String;)V
    //   389: goto +9 -> 398
    //   392: aload_0
    //   393: invokevirtual 513	com/sun/jmx/remote/internal/ArrayNotificationBuffer:earliestSequenceNumber	()J
    //   396: lstore 9
    //   398: lload 11
    //   400: lload 9
    //   402: lcmp
    //   403: ifge +52 -> 455
    //   406: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   409: ldc 38
    //   411: new 303	java/lang/StringBuilder
    //   414: dup
    //   415: invokespecial 572	java/lang/StringBuilder:<init>	()V
    //   418: ldc 46
    //   420: invokevirtual 577	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   423: lload 11
    //   425: invokevirtual 575	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   428: ldc 2
    //   430: invokevirtual 577	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   433: lload 9
    //   435: invokevirtual 575	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   438: ldc 5
    //   440: invokevirtual 577	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   443: invokevirtual 573	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   446: invokevirtual 557	com/sun/jmx/remote/util/ClassLogger:trace	(Ljava/lang/String;Ljava/lang/String;)V
    //   449: aload 15
    //   451: monitorexit
    //   452: goto +480 -> 932
    //   455: lload 11
    //   457: aload_0
    //   458: invokevirtual 514	com/sun/jmx/remote/internal/ArrayNotificationBuffer:nextSequenceNumber	()J
    //   461: lcmp
    //   462: ifge +164 -> 626
    //   465: aload_0
    //   466: lload 11
    //   468: invokevirtual 521	com/sun/jmx/remote/internal/ArrayNotificationBuffer:notificationAt	(J)Lcom/sun/jmx/remote/internal/ArrayNotificationBuffer$NamedNotification;
    //   471: astore 14
    //   473: aload_1
    //   474: instanceof 291
    //   477: ifne +81 -> 558
    //   480: aload_0
    //   481: getfield 507	com/sun/jmx/remote/internal/ArrayNotificationBuffer:mBeanServer	Ljavax/management/MBeanServer;
    //   484: aload 14
    //   486: invokevirtual 542	com/sun/jmx/remote/internal/ArrayNotificationBuffer$NamedNotification:getObjectName	()Ljavax/management/ObjectName;
    //   489: ldc 22
    //   491: invokestatic 551	com/sun/jmx/remote/internal/ServerNotifForwarder:checkMBeanPermission	(Ljavax/management/MBeanServer;Ljavax/management/ObjectName;Ljava/lang/String;)V
    //   494: goto +64 -> 558
    //   497: astore 16
    //   499: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   502: invokevirtual 552	com/sun/jmx/remote/util/ClassLogger:debugOn	()Z
    //   505: ifeq +41 -> 546
    //   508: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   511: ldc 38
    //   513: new 303	java/lang/StringBuilder
    //   516: dup
    //   517: invokespecial 572	java/lang/StringBuilder:<init>	()V
    //   520: ldc 26
    //   522: invokevirtual 577	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   525: aload 14
    //   527: invokevirtual 576	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   530: ldc 4
    //   532: invokevirtual 577	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   535: aload 16
    //   537: invokevirtual 576	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   540: invokevirtual 573	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   543: invokevirtual 555	com/sun/jmx/remote/util/ClassLogger:debug	(Ljava/lang/String;Ljava/lang/String;)V
    //   546: lload 11
    //   548: lconst_1
    //   549: ladd
    //   550: lstore 11
    //   552: aload 15
    //   554: monitorexit
    //   555: goto -253 -> 302
    //   558: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   561: invokevirtual 552	com/sun/jmx/remote/util/ClassLogger:debugOn	()Z
    //   564: ifeq +222 -> 786
    //   567: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   570: ldc 38
    //   572: new 303	java/lang/StringBuilder
    //   575: dup
    //   576: invokespecial 572	java/lang/StringBuilder:<init>	()V
    //   579: ldc 26
    //   581: invokevirtual 577	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   584: aload 14
    //   586: invokevirtual 576	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   589: invokevirtual 573	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   592: invokevirtual 555	com/sun/jmx/remote/util/ClassLogger:debug	(Ljava/lang/String;Ljava/lang/String;)V
    //   595: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   598: ldc 38
    //   600: new 303	java/lang/StringBuilder
    //   603: dup
    //   604: invokespecial 572	java/lang/StringBuilder:<init>	()V
    //   607: ldc 45
    //   609: invokevirtual 577	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   612: lload 11
    //   614: invokevirtual 575	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   617: invokevirtual 573	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   620: invokevirtual 555	com/sun/jmx/remote/util/ClassLogger:debug	(Ljava/lang/String;Ljava/lang/String;)V
    //   623: goto +163 -> 786
    //   626: aload 13
    //   628: invokeinterface 607 1 0
    //   633: ifle +19 -> 652
    //   636: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   639: ldc 38
    //   641: ldc 48
    //   643: invokevirtual 555	com/sun/jmx/remote/util/ClassLogger:debug	(Ljava/lang/String;Ljava/lang/String;)V
    //   646: aload 15
    //   648: monitorexit
    //   649: goto +283 -> 932
    //   652: lload 7
    //   654: invokestatic 578	java/lang/System:currentTimeMillis	()J
    //   657: lsub
    //   658: lstore 16
    //   660: lload 16
    //   662: lconst_0
    //   663: lcmp
    //   664: ifgt +19 -> 683
    //   667: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   670: ldc 38
    //   672: ldc 57
    //   674: invokevirtual 555	com/sun/jmx/remote/util/ClassLogger:debug	(Ljava/lang/String;Ljava/lang/String;)V
    //   677: aload 15
    //   679: monitorexit
    //   680: goto +252 -> 932
    //   683: aload_0
    //   684: invokespecial 519	com/sun/jmx/remote/internal/ArrayNotificationBuffer:isDisposed	()Z
    //   687: ifeq +45 -> 732
    //   690: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   693: invokevirtual 552	com/sun/jmx/remote/util/ClassLogger:debugOn	()Z
    //   696: ifeq +13 -> 709
    //   699: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   702: ldc 38
    //   704: ldc 32
    //   706: invokevirtual 555	com/sun/jmx/remote/util/ClassLogger:debug	(Ljava/lang/String;Ljava/lang/String;)V
    //   709: new 329	javax/management/remote/NotificationResult
    //   712: dup
    //   713: aload_0
    //   714: invokevirtual 513	com/sun/jmx/remote/internal/ArrayNotificationBuffer:earliestSequenceNumber	()J
    //   717: aload_0
    //   718: invokevirtual 514	com/sun/jmx/remote/internal/ArrayNotificationBuffer:nextSequenceNumber	()J
    //   721: iconst_0
    //   722: anewarray 330	javax/management/remote/TargetedNotification
    //   725: invokespecial 599	javax/management/remote/NotificationResult:<init>	(JJ[Ljavax/management/remote/TargetedNotification;)V
    //   728: aload 15
    //   730: monitorexit
    //   731: areturn
    //   732: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   735: invokevirtual 552	com/sun/jmx/remote/util/ClassLogger:debugOn	()Z
    //   738: ifeq +36 -> 774
    //   741: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   744: ldc 38
    //   746: new 303	java/lang/StringBuilder
    //   749: dup
    //   750: invokespecial 572	java/lang/StringBuilder:<init>	()V
    //   753: ldc 58
    //   755: invokevirtual 577	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   758: lload 16
    //   760: invokevirtual 575	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   763: ldc 6
    //   765: invokevirtual 577	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   768: invokevirtual 573	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   771: invokevirtual 555	com/sun/jmx/remote/util/ClassLogger:debug	(Ljava/lang/String;Ljava/lang/String;)V
    //   774: aload_0
    //   775: lload 16
    //   777: invokevirtual 569	java/lang/Object:wait	(J)V
    //   780: aload 15
    //   782: monitorexit
    //   783: goto -481 -> 302
    //   786: aload 15
    //   788: monitorexit
    //   789: goto +11 -> 800
    //   792: astore 18
    //   794: aload 15
    //   796: monitorexit
    //   797: aload 18
    //   799: athrow
    //   800: aload 14
    //   802: invokevirtual 542	com/sun/jmx/remote/internal/ArrayNotificationBuffer$NamedNotification:getObjectName	()Ljavax/management/ObjectName;
    //   805: astore 15
    //   807: aload 14
    //   809: invokevirtual 541	com/sun/jmx/remote/internal/ArrayNotificationBuffer$NamedNotification:getNotification	()Ljavax/management/Notification;
    //   812: astore 16
    //   814: new 312	java/util/ArrayList
    //   817: dup
    //   818: invokespecial 584	java/util/ArrayList:<init>	()V
    //   821: astore 17
    //   823: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   826: ldc 38
    //   828: ldc 24
    //   830: invokevirtual 555	com/sun/jmx/remote/util/ClassLogger:debug	(Ljava/lang/String;Ljava/lang/String;)V
    //   833: aload_1
    //   834: aload 17
    //   836: aload 15
    //   838: aload 16
    //   840: invokeinterface 600 4 0
    //   845: aload 17
    //   847: invokeinterface 607 1 0
    //   852: ifle +71 -> 923
    //   855: iload 6
    //   857: ifgt +16 -> 873
    //   860: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   863: ldc 38
    //   865: ldc 52
    //   867: invokevirtual 555	com/sun/jmx/remote/util/ClassLogger:debug	(Ljava/lang/String;Ljava/lang/String;)V
    //   870: goto +62 -> 932
    //   873: iinc 6 -1
    //   876: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   879: invokevirtual 552	com/sun/jmx/remote/util/ClassLogger:debugOn	()Z
    //   882: ifeq +31 -> 913
    //   885: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   888: ldc 38
    //   890: new 303	java/lang/StringBuilder
    //   893: dup
    //   894: invokespecial 572	java/lang/StringBuilder:<init>	()V
    //   897: ldc 19
    //   899: invokevirtual 577	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   902: aload 17
    //   904: invokevirtual 576	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   907: invokevirtual 573	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   910: invokevirtual 555	com/sun/jmx/remote/util/ClassLogger:debug	(Ljava/lang/String;Ljava/lang/String;)V
    //   913: aload 13
    //   915: aload 17
    //   917: invokeinterface 608 2 0
    //   922: pop
    //   923: lload 11
    //   925: lconst_1
    //   926: ladd
    //   927: lstore 11
    //   929: goto -627 -> 302
    //   932: aload 13
    //   934: invokeinterface 607 1 0
    //   939: istore 14
    //   941: iload 14
    //   943: anewarray 330	javax/management/remote/TargetedNotification
    //   946: astore 15
    //   948: aload 13
    //   950: aload 15
    //   952: invokeinterface 609 2 0
    //   957: pop
    //   958: new 329	javax/management/remote/NotificationResult
    //   961: dup
    //   962: lload 9
    //   964: lload 11
    //   966: aload 15
    //   968: invokespecial 599	javax/management/remote/NotificationResult:<init>	(JJ[Ljavax/management/remote/TargetedNotification;)V
    //   971: astore 16
    //   973: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   976: invokevirtual 552	com/sun/jmx/remote/util/ClassLogger:debugOn	()Z
    //   979: ifeq +16 -> 995
    //   982: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   985: ldc 38
    //   987: aload 16
    //   989: invokevirtual 598	javax/management/remote/NotificationResult:toString	()Ljava/lang/String;
    //   992: invokevirtual 555	com/sun/jmx/remote/util/ClassLogger:debug	(Ljava/lang/String;Ljava/lang/String;)V
    //   995: getstatic 501	com/sun/jmx/remote/internal/ArrayNotificationBuffer:logger	Lcom/sun/jmx/remote/util/ClassLogger;
    //   998: ldc 38
    //   1000: ldc 36
    //   1002: invokevirtual 557	com/sun/jmx/remote/util/ClassLogger:trace	(Ljava/lang/String;Ljava/lang/String;)V
    //   1005: aload 16
    //   1007: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1008	0	this	ArrayNotificationBuffer
    //   0	1008	1	paramNotificationBufferFilter	NotificationBufferFilter
    //   0	1008	2	paramLong1	long
    //   0	1008	4	paramLong2	long
    //   0	1008	6	paramInt	int
    //   25	198	7	Ljava/lang/Object;	Object
    //   234	419	7	l1	long
    //   51	6	8	localObject1	Object
    //   288	675	9	l2	long
    //   291	674	11	l3	long
    //   300	649	13	localArrayList1	java.util.ArrayList
    //   471	337	14	localNamedNotification	NamedNotification
    //   939	3	14	i	int
    //   497	39	16	localInstanceNotFoundException	InstanceNotFoundException
    //   658	118	16	l4	long
    //   812	194	16	localObject2	Object
    //   821	95	17	localArrayList2	java.util.ArrayList
    //   792	6	18	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   28	50	51	finally
    //   51	56	51	finally
    //   480	494	497	javax/management/InstanceNotFoundException
    //   480	494	497	java/lang/SecurityException
    //   317	452	792	finally
    //   455	555	792	finally
    //   558	649	792	finally
    //   652	680	792	finally
    //   683	731	792	finally
    //   732	783	792	finally
    //   786	789	792	finally
    //   792	797	792	finally
  }
  
  synchronized long earliestSequenceNumber()
  {
    return earliestSequenceNumber;
  }
  
  synchronized long nextSequenceNumber()
  {
    return nextSequenceNumber;
  }
  
  synchronized void addNotification(NamedNotification paramNamedNotification)
  {
    if (logger.traceOn()) {
      logger.trace("addNotification", paramNamedNotification.toString());
    }
    while (queue.size() >= queueSize)
    {
      dropNotification();
      if (logger.debugOn()) {
        logger.debug("addNotification", "dropped oldest notif, earliestSeq=" + earliestSequenceNumber);
      }
    }
    queue.add(paramNamedNotification);
    nextSequenceNumber += 1L;
    if (logger.debugOn()) {
      logger.debug("addNotification", "nextSeq=" + nextSequenceNumber);
    }
    notifyAll();
  }
  
  private void dropNotification()
  {
    queue.remove(0);
    earliestSequenceNumber += 1L;
  }
  
  synchronized NamedNotification notificationAt(long paramLong)
  {
    long l = paramLong - earliestSequenceNumber;
    if ((l < 0L) || (l > 2147483647L))
    {
      String str = "Bad sequence number: " + paramLong + " (earliest " + earliestSequenceNumber + ")";
      logger.trace("notificationAt", str);
      throw new IllegalArgumentException(str);
    }
    return (NamedNotification)queue.get((int)l);
  }
  
  private void createListeners()
  {
    logger.debug("createListeners", "starts");
    synchronized (this)
    {
      createdDuringQuery = new HashSet();
    }
    Object localObject3;
    try
    {
      addNotificationListener(MBeanServerDelegate.DELEGATE_NAME, creationListener, creationFilter, null);
      logger.debug("createListeners", "added creationListener");
    }
    catch (Exception localException)
    {
      localObject3 = new IllegalArgumentException("Can't add listener to MBean server delegate: " + localException);
      EnvHelp.initCause((Throwable)localObject3, localException);
      logger.fine("createListeners", "Can't add listener to MBean server delegate: " + localException);
      logger.debug("createListeners", localException);
      throw ((Throwable)localObject3);
    }
    Object localObject1 = queryNames(null, broadcasterQuery);
    localObject1 = new HashSet((Collection)localObject1);
    synchronized (this)
    {
      ((Set)localObject1).addAll(createdDuringQuery);
      createdDuringQuery = null;
    }
    ??? = ((Set)localObject1).iterator();
    while (((Iterator)???).hasNext())
    {
      localObject3 = (ObjectName)((Iterator)???).next();
      addBufferListener((ObjectName)localObject3);
    }
    logger.debug("createListeners", "ends");
  }
  
  private void addBufferListener(ObjectName paramObjectName)
  {
    checkNoLocks();
    if (logger.debugOn()) {
      logger.debug("addBufferListener", paramObjectName.toString());
    }
    try
    {
      addNotificationListener(paramObjectName, bufferListener, null, paramObjectName);
    }
    catch (Exception localException)
    {
      logger.trace("addBufferListener", localException);
    }
  }
  
  private void removeBufferListener(ObjectName paramObjectName)
  {
    checkNoLocks();
    if (logger.debugOn()) {
      logger.debug("removeBufferListener", paramObjectName.toString());
    }
    try
    {
      removeNotificationListener(paramObjectName, bufferListener);
    }
    catch (Exception localException)
    {
      logger.trace("removeBufferListener", localException);
    }
  }
  
  private void addNotificationListener(final ObjectName paramObjectName, final NotificationListener paramNotificationListener, final NotificationFilter paramNotificationFilter, final Object paramObject)
    throws Exception
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Void run()
          throws InstanceNotFoundException
        {
          mBeanServer.addNotificationListener(paramObjectName, paramNotificationListener, paramNotificationFilter, paramObject);
          return null;
        }
      });
    }
    catch (Exception localException)
    {
      throw extractException(localException);
    }
  }
  
  private void removeNotificationListener(final ObjectName paramObjectName, final NotificationListener paramNotificationListener)
    throws Exception
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Void run()
          throws Exception
        {
          mBeanServer.removeNotificationListener(paramObjectName, paramNotificationListener);
          return null;
        }
      });
    }
    catch (Exception localException)
    {
      throw extractException(localException);
    }
  }
  
  private Set<ObjectName> queryNames(final ObjectName paramObjectName, final QueryExp paramQueryExp)
  {
    PrivilegedAction local3 = new PrivilegedAction()
    {
      public Set<ObjectName> run()
      {
        return mBeanServer.queryNames(paramObjectName, paramQueryExp);
      }
    };
    try
    {
      return (Set)AccessController.doPrivileged(local3);
    }
    catch (RuntimeException localRuntimeException)
    {
      logger.fine("queryNames", "Failed to query names: " + localRuntimeException);
      logger.debug("queryNames", localRuntimeException);
      throw localRuntimeException;
    }
  }
  
  private static boolean isInstanceOf(MBeanServer paramMBeanServer, final ObjectName paramObjectName, final String paramString)
  {
    PrivilegedExceptionAction local4 = new PrivilegedExceptionAction()
    {
      public Boolean run()
        throws InstanceNotFoundException
      {
        return Boolean.valueOf(val$mbs.isInstanceOf(paramObjectName, paramString));
      }
    };
    try
    {
      return ((Boolean)AccessController.doPrivileged(local4)).booleanValue();
    }
    catch (Exception localException)
    {
      logger.fine("isInstanceOf", "failed: " + localException);
      logger.debug("isInstanceOf", localException);
    }
    return false;
  }
  
  private void createdNotification(MBeanServerNotification paramMBeanServerNotification)
  {
    if (!paramMBeanServerNotification.getType().equals("JMX.mbean.registered"))
    {
      logger.warning("createNotification", "bad type: " + paramMBeanServerNotification.getType());
      return;
    }
    ObjectName localObjectName = paramMBeanServerNotification.getMBeanName();
    if (logger.debugOn()) {
      logger.debug("createdNotification", "for: " + localObjectName);
    }
    synchronized (this)
    {
      if (createdDuringQuery != null)
      {
        createdDuringQuery.add(localObjectName);
        return;
      }
    }
    if (isInstanceOf(mBeanServer, localObjectName, broadcasterClass))
    {
      addBufferListener(localObjectName);
      if (isDisposed()) {
        removeBufferListener(localObjectName);
      }
    }
  }
  
  private void destroyListeners()
  {
    checkNoLocks();
    logger.debug("destroyListeners", "starts");
    try
    {
      removeNotificationListener(MBeanServerDelegate.DELEGATE_NAME, creationListener);
    }
    catch (Exception localException)
    {
      logger.warning("remove listener from MBeanServer delegate", localException);
    }
    Set localSet = queryNames(null, broadcasterQuery);
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      ObjectName localObjectName = (ObjectName)localIterator.next();
      if (logger.debugOn()) {
        logger.debug("destroyListeners", "remove listener from " + localObjectName);
      }
      removeBufferListener(localObjectName);
    }
    logger.debug("destroyListeners", "ends");
  }
  
  private void checkNoLocks()
  {
    if ((Thread.holdsLock(this)) || (Thread.holdsLock(globalLock))) {
      logger.warning("checkNoLocks", "lock protocol violation");
    }
  }
  
  private static Exception extractException(Exception paramException)
  {
    while ((paramException instanceof PrivilegedActionException)) {
      paramException = ((PrivilegedActionException)paramException).getException();
    }
    return paramException;
  }
  
  static
  {
    NotificationFilterSupport localNotificationFilterSupport = new NotificationFilterSupport();
    localNotificationFilterSupport.enableType("JMX.mbean.registered");
    creationFilter = localNotificationFilterSupport;
  }
  
  private static class BroadcasterQuery
    extends QueryEval
    implements QueryExp
  {
    private static final long serialVersionUID = 7378487660587592048L;
    
    private BroadcasterQuery() {}
    
    public boolean apply(ObjectName paramObjectName)
    {
      MBeanServer localMBeanServer = QueryEval.getMBeanServer();
      return ArrayNotificationBuffer.isInstanceOf(localMBeanServer, paramObjectName, ArrayNotificationBuffer.broadcasterClass);
    }
  }
  
  private class BufferListener
    implements NotificationListener
  {
    private BufferListener() {}
    
    public void handleNotification(Notification paramNotification, Object paramObject)
    {
      if (ArrayNotificationBuffer.logger.debugOn()) {
        ArrayNotificationBuffer.logger.debug("BufferListener.handleNotification", "notif=" + paramNotification + "; handback=" + paramObject);
      }
      ObjectName localObjectName = (ObjectName)paramObject;
      addNotification(new ArrayNotificationBuffer.NamedNotification(localObjectName, paramNotification));
    }
  }
  
  private static class NamedNotification
  {
    private final ObjectName sender;
    private final Notification notification;
    
    NamedNotification(ObjectName paramObjectName, Notification paramNotification)
    {
      sender = paramObjectName;
      notification = paramNotification;
    }
    
    ObjectName getObjectName()
    {
      return sender;
    }
    
    Notification getNotification()
    {
      return notification;
    }
    
    public String toString()
    {
      return "NamedNotification(" + sender + ", " + notification + ")";
    }
  }
  
  private class ShareBuffer
    implements NotificationBuffer
  {
    private final int size;
    
    ShareBuffer(int paramInt)
    {
      size = paramInt;
      addSharer(this);
    }
    
    public NotificationResult fetchNotifications(NotificationBufferFilter paramNotificationBufferFilter, long paramLong1, long paramLong2, int paramInt)
      throws InterruptedException
    {
      ArrayNotificationBuffer localArrayNotificationBuffer = ArrayNotificationBuffer.this;
      return localArrayNotificationBuffer.fetchNotifications(paramNotificationBufferFilter, paramLong1, paramLong2, paramInt);
    }
    
    public void dispose()
    {
      ArrayNotificationBuffer.this.removeSharer(this);
    }
    
    int getSize()
    {
      return size;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\internal\ArrayNotificationBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
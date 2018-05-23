package sun.awt.image;

import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Vector;
import sun.awt.AppContext;

class ImageFetcher
  extends Thread
{
  static final int HIGH_PRIORITY = 8;
  static final int LOW_PRIORITY = 3;
  static final int ANIM_PRIORITY = 2;
  static final int TIMEOUT = 5000;
  
  private ImageFetcher(ThreadGroup paramThreadGroup, int paramInt)
  {
    super(paramThreadGroup, "Image Fetcher " + paramInt);
    setDaemon(true);
  }
  
  public static boolean add(ImageFetchable paramImageFetchable)
  {
    FetcherInfo localFetcherInfo = FetcherInfo.getFetcherInfo();
    synchronized (waitList)
    {
      if (!waitList.contains(paramImageFetchable))
      {
        waitList.addElement(paramImageFetchable);
        if ((numWaiting == 0) && (numFetchers < fetchers.length)) {
          createFetchers(localFetcherInfo);
        }
        if (numFetchers > 0)
        {
          waitList.notify();
        }
        else
        {
          waitList.removeElement(paramImageFetchable);
          return false;
        }
      }
    }
    return true;
  }
  
  public static void remove(ImageFetchable paramImageFetchable)
  {
    FetcherInfo localFetcherInfo = FetcherInfo.getFetcherInfo();
    synchronized (waitList)
    {
      if (waitList.contains(paramImageFetchable)) {
        waitList.removeElement(paramImageFetchable);
      }
    }
  }
  
  public static boolean isFetcher(Thread paramThread)
  {
    FetcherInfo localFetcherInfo = FetcherInfo.getFetcherInfo();
    synchronized (waitList)
    {
      for (int i = 0; i < fetchers.length; i++) {
        if (fetchers[i] == paramThread) {
          return true;
        }
      }
    }
    return false;
  }
  
  public static boolean amFetcher()
  {
    return isFetcher(Thread.currentThread());
  }
  
  private static ImageFetchable nextImage()
  {
    FetcherInfo localFetcherInfo = FetcherInfo.getFetcherInfo();
    synchronized (waitList)
    {
      ImageFetchable localImageFetchable1 = null;
      long l1 = System.currentTimeMillis() + 5000L;
      while (localImageFetchable1 == null)
      {
        while (waitList.size() == 0)
        {
          long l2 = System.currentTimeMillis();
          if (l2 >= l1) {
            return null;
          }
          try
          {
            numWaiting += 1;
            waitList.wait(l1 - l2);
          }
          catch (InterruptedException localInterruptedException)
          {
            ImageFetchable localImageFetchable2 = null;
            numWaiting -= 1;
            return localImageFetchable2;
          }
          finally
          {
            numWaiting -= 1;
          }
        }
        localImageFetchable1 = (ImageFetchable)waitList.elementAt(0);
        waitList.removeElement(localImageFetchable1);
      }
      return localImageFetchable1;
    }
  }
  
  /* Error */
  public void run()
  {
    // Byte code:
    //   0: invokestatic 196	sun/awt/image/FetcherInfo:getFetcherInfo	()Lsun/awt/image/FetcherInfo;
    //   3: astore_1
    //   4: aload_0
    //   5: invokespecial 197	sun/awt/image/ImageFetcher:fetchloop	()V
    //   8: aload_1
    //   9: getfield 171	sun/awt/image/FetcherInfo:waitList	Ljava/util/Vector;
    //   12: dup
    //   13: astore_2
    //   14: monitorenter
    //   15: invokestatic 184	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   18: astore_3
    //   19: iconst_0
    //   20: istore 4
    //   22: iload 4
    //   24: aload_1
    //   25: getfield 170	sun/awt/image/FetcherInfo:fetchers	[Ljava/lang/Thread;
    //   28: arraylength
    //   29: if_icmpge +38 -> 67
    //   32: aload_1
    //   33: getfield 170	sun/awt/image/FetcherInfo:fetchers	[Ljava/lang/Thread;
    //   36: iload 4
    //   38: aaload
    //   39: aload_3
    //   40: if_acmpne +21 -> 61
    //   43: aload_1
    //   44: getfield 170	sun/awt/image/FetcherInfo:fetchers	[Ljava/lang/Thread;
    //   47: iload 4
    //   49: aconst_null
    //   50: aastore
    //   51: aload_1
    //   52: dup
    //   53: getfield 168	sun/awt/image/FetcherInfo:numFetchers	I
    //   56: iconst_1
    //   57: isub
    //   58: putfield 168	sun/awt/image/FetcherInfo:numFetchers	I
    //   61: iinc 4 1
    //   64: goto -42 -> 22
    //   67: aload_2
    //   68: monitorexit
    //   69: goto +10 -> 79
    //   72: astore 5
    //   74: aload_2
    //   75: monitorexit
    //   76: aload 5
    //   78: athrow
    //   79: goto +163 -> 242
    //   82: astore_2
    //   83: aload_2
    //   84: invokevirtual 173	java/lang/Exception:printStackTrace	()V
    //   87: aload_1
    //   88: getfield 171	sun/awt/image/FetcherInfo:waitList	Ljava/util/Vector;
    //   91: dup
    //   92: astore_2
    //   93: monitorenter
    //   94: invokestatic 184	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   97: astore_3
    //   98: iconst_0
    //   99: istore 4
    //   101: iload 4
    //   103: aload_1
    //   104: getfield 170	sun/awt/image/FetcherInfo:fetchers	[Ljava/lang/Thread;
    //   107: arraylength
    //   108: if_icmpge +38 -> 146
    //   111: aload_1
    //   112: getfield 170	sun/awt/image/FetcherInfo:fetchers	[Ljava/lang/Thread;
    //   115: iload 4
    //   117: aaload
    //   118: aload_3
    //   119: if_acmpne +21 -> 140
    //   122: aload_1
    //   123: getfield 170	sun/awt/image/FetcherInfo:fetchers	[Ljava/lang/Thread;
    //   126: iload 4
    //   128: aconst_null
    //   129: aastore
    //   130: aload_1
    //   131: dup
    //   132: getfield 168	sun/awt/image/FetcherInfo:numFetchers	I
    //   135: iconst_1
    //   136: isub
    //   137: putfield 168	sun/awt/image/FetcherInfo:numFetchers	I
    //   140: iinc 4 1
    //   143: goto -42 -> 101
    //   146: aload_2
    //   147: monitorexit
    //   148: goto +10 -> 158
    //   151: astore 6
    //   153: aload_2
    //   154: monitorexit
    //   155: aload 6
    //   157: athrow
    //   158: goto +84 -> 242
    //   161: astore 7
    //   163: aload_1
    //   164: getfield 171	sun/awt/image/FetcherInfo:waitList	Ljava/util/Vector;
    //   167: dup
    //   168: astore 8
    //   170: monitorenter
    //   171: invokestatic 184	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   174: astore 9
    //   176: iconst_0
    //   177: istore 10
    //   179: iload 10
    //   181: aload_1
    //   182: getfield 170	sun/awt/image/FetcherInfo:fetchers	[Ljava/lang/Thread;
    //   185: arraylength
    //   186: if_icmpge +39 -> 225
    //   189: aload_1
    //   190: getfield 170	sun/awt/image/FetcherInfo:fetchers	[Ljava/lang/Thread;
    //   193: iload 10
    //   195: aaload
    //   196: aload 9
    //   198: if_acmpne +21 -> 219
    //   201: aload_1
    //   202: getfield 170	sun/awt/image/FetcherInfo:fetchers	[Ljava/lang/Thread;
    //   205: iload 10
    //   207: aconst_null
    //   208: aastore
    //   209: aload_1
    //   210: dup
    //   211: getfield 168	sun/awt/image/FetcherInfo:numFetchers	I
    //   214: iconst_1
    //   215: isub
    //   216: putfield 168	sun/awt/image/FetcherInfo:numFetchers	I
    //   219: iinc 10 1
    //   222: goto -43 -> 179
    //   225: aload 8
    //   227: monitorexit
    //   228: goto +11 -> 239
    //   231: astore 11
    //   233: aload 8
    //   235: monitorexit
    //   236: aload 11
    //   238: athrow
    //   239: aload 7
    //   241: athrow
    //   242: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	243	0	this	ImageFetcher
    //   3	207	1	localFetcherInfo	FetcherInfo
    //   18	101	3	localThread1	Thread
    //   20	121	4	i	int
    //   72	5	5	localObject1	Object
    //   151	5	6	localObject2	Object
    //   161	79	7	localObject3	Object
    //   174	23	9	localThread2	Thread
    //   177	43	10	j	int
    //   231	6	11	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   15	69	72	finally
    //   72	76	72	finally
    //   4	8	82	java/lang/Exception
    //   94	148	151	finally
    //   151	155	151	finally
    //   4	8	161	finally
    //   82	87	161	finally
    //   161	163	161	finally
    //   171	228	231	finally
    //   231	236	231	finally
  }
  
  private void fetchloop()
  {
    Thread localThread = Thread.currentThread();
    while (isFetcher(localThread))
    {
      Thread.interrupted();
      localThread.setPriority(8);
      ImageFetchable localImageFetchable = nextImage();
      if (localImageFetchable == null) {
        return;
      }
      try
      {
        localImageFetchable.doFetch();
      }
      catch (Exception localException)
      {
        System.err.println("Uncaught error fetching image:");
        localException.printStackTrace();
      }
      stoppingAnimation(localThread);
    }
  }
  
  static void startingAnimation()
  {
    FetcherInfo localFetcherInfo = FetcherInfo.getFetcherInfo();
    Thread localThread = Thread.currentThread();
    synchronized (waitList)
    {
      for (int i = 0; i < fetchers.length; i++) {
        if (fetchers[i] == localThread)
        {
          fetchers[i] = null;
          numFetchers -= 1;
          localThread.setName("Image Animator " + i);
          if (waitList.size() > numWaiting) {
            createFetchers(localFetcherInfo);
          }
          return;
        }
      }
    }
    localThread.setPriority(2);
    localThread.setName("Image Animator");
  }
  
  private static void stoppingAnimation(Thread paramThread)
  {
    FetcherInfo localFetcherInfo = FetcherInfo.getFetcherInfo();
    synchronized (waitList)
    {
      int i = -1;
      for (int j = 0; j < fetchers.length; j++)
      {
        if (fetchers[j] == paramThread) {
          return;
        }
        if (fetchers[j] == null) {
          i = j;
        }
      }
      if (i >= 0)
      {
        fetchers[i] = paramThread;
        numFetchers += 1;
        paramThread.setName("Image Fetcher " + i);
        return;
      }
    }
  }
  
  private static void createFetchers(FetcherInfo paramFetcherInfo)
  {
    AppContext localAppContext = AppContext.getAppContext();
    Object localObject1 = localAppContext.getThreadGroup();
    Object localObject2;
    try
    {
      if (((ThreadGroup)localObject1).getParent() != null)
      {
        localObject2 = localObject1;
      }
      else
      {
        localObject1 = Thread.currentThread().getThreadGroup();
        for (ThreadGroup localThreadGroup = ((ThreadGroup)localObject1).getParent(); (localThreadGroup != null) && (localThreadGroup.getParent() != null); localThreadGroup = ((ThreadGroup)localObject1).getParent()) {
          localObject1 = localThreadGroup;
        }
        localObject2 = localObject1;
      }
    }
    catch (SecurityException localSecurityException)
    {
      localObject2 = localAppContext.getThreadGroup();
    }
    final Object localObject3 = localObject2;
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        for (int i = 0; i < val$info.fetchers.length; i++) {
          if (val$info.fetchers[i] == null)
          {
            ImageFetcher localImageFetcher = new ImageFetcher(localObject3, i, null);
            try
            {
              localImageFetcher.start();
              val$info.fetchers[i] = localImageFetcher;
              val$info.numFetchers += 1;
            }
            catch (Error localError) {}
          }
        }
        return null;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\ImageFetcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
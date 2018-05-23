package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.ListenerThread;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.threadpool.Work;

public class ListenerThreadImpl
  implements ListenerThread, Work
{
  private ORB orb;
  private Acceptor acceptor;
  private Selector selector;
  private boolean keepRunning;
  private long enqueueTime;
  
  public ListenerThreadImpl(ORB paramORB, Acceptor paramAcceptor, Selector paramSelector)
  {
    orb = paramORB;
    acceptor = paramAcceptor;
    selector = paramSelector;
    keepRunning = true;
  }
  
  public Acceptor getAcceptor()
  {
    return acceptor;
  }
  
  public void close()
  {
    if (orb.transportDebugFlag) {
      dprint(".close: " + acceptor);
    }
    keepRunning = false;
  }
  
  /* Error */
  public void doWork()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 114	com/sun/corba/se/impl/transport/ListenerThreadImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   4: getfield 115	com/sun/corba/se/spi/orb/ORB:transportDebugFlag	Z
    //   7: ifeq +29 -> 36
    //   10: aload_0
    //   11: new 72	java/lang/StringBuilder
    //   14: dup
    //   15: invokespecial 123	java/lang/StringBuilder:<init>	()V
    //   18: ldc 5
    //   20: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   23: aload_0
    //   24: getfield 112	com/sun/corba/se/impl/transport/ListenerThreadImpl:acceptor	Lcom/sun/corba/se/pept/transport/Acceptor;
    //   27: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   30: invokevirtual 124	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   33: invokespecial 119	com/sun/corba/se/impl/transport/ListenerThreadImpl:dprint	(Ljava/lang/String;)V
    //   36: aload_0
    //   37: getfield 111	com/sun/corba/se/impl/transport/ListenerThreadImpl:keepRunning	Z
    //   40: ifeq +164 -> 204
    //   43: aload_0
    //   44: getfield 114	com/sun/corba/se/impl/transport/ListenerThreadImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   47: getfield 115	com/sun/corba/se/spi/orb/ORB:transportDebugFlag	Z
    //   50: ifeq +29 -> 79
    //   53: aload_0
    //   54: new 72	java/lang/StringBuilder
    //   57: dup
    //   58: invokespecial 123	java/lang/StringBuilder:<init>	()V
    //   61: ldc 3
    //   63: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   66: aload_0
    //   67: getfield 112	com/sun/corba/se/impl/transport/ListenerThreadImpl:acceptor	Lcom/sun/corba/se/pept/transport/Acceptor;
    //   70: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   73: invokevirtual 124	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   76: invokespecial 119	com/sun/corba/se/impl/transport/ListenerThreadImpl:dprint	(Ljava/lang/String;)V
    //   79: aload_0
    //   80: getfield 112	com/sun/corba/se/impl/transport/ListenerThreadImpl:acceptor	Lcom/sun/corba/se/pept/transport/Acceptor;
    //   83: invokeinterface 128 1 0
    //   88: aload_0
    //   89: getfield 114	com/sun/corba/se/impl/transport/ListenerThreadImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   92: getfield 115	com/sun/corba/se/spi/orb/ORB:transportDebugFlag	Z
    //   95: ifeq +29 -> 124
    //   98: aload_0
    //   99: new 72	java/lang/StringBuilder
    //   102: dup
    //   103: invokespecial 123	java/lang/StringBuilder:<init>	()V
    //   106: ldc 2
    //   108: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   111: aload_0
    //   112: getfield 112	com/sun/corba/se/impl/transport/ListenerThreadImpl:acceptor	Lcom/sun/corba/se/pept/transport/Acceptor;
    //   115: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   118: invokevirtual 124	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   121: invokespecial 119	com/sun/corba/se/impl/transport/ListenerThreadImpl:dprint	(Ljava/lang/String;)V
    //   124: goto -88 -> 36
    //   127: astore_1
    //   128: aload_0
    //   129: getfield 114	com/sun/corba/se/impl/transport/ListenerThreadImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   132: getfield 115	com/sun/corba/se/spi/orb/ORB:transportDebugFlag	Z
    //   135: ifeq +30 -> 165
    //   138: aload_0
    //   139: new 72	java/lang/StringBuilder
    //   142: dup
    //   143: invokespecial 123	java/lang/StringBuilder:<init>	()V
    //   146: ldc 4
    //   148: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   151: aload_0
    //   152: getfield 112	com/sun/corba/se/impl/transport/ListenerThreadImpl:acceptor	Lcom/sun/corba/se/pept/transport/Acceptor;
    //   155: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   158: invokevirtual 124	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   161: aload_1
    //   162: invokespecial 120	com/sun/corba/se/impl/transport/ListenerThreadImpl:dprint	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   165: aload_0
    //   166: getfield 114	com/sun/corba/se/impl/transport/ListenerThreadImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   169: invokevirtual 121	com/sun/corba/se/spi/orb/ORB:getTransportManager	()Lcom/sun/corba/se/pept/transport/TransportManager;
    //   172: iconst_0
    //   173: invokeinterface 132 2 0
    //   178: aload_0
    //   179: invokevirtual 118	com/sun/corba/se/impl/transport/ListenerThreadImpl:getAcceptor	()Lcom/sun/corba/se/pept/transport/Acceptor;
    //   182: invokeinterface 130 1 0
    //   187: invokeinterface 131 2 0
    //   192: aload_0
    //   193: invokevirtual 118	com/sun/corba/se/impl/transport/ListenerThreadImpl:getAcceptor	()Lcom/sun/corba/se/pept/transport/Acceptor;
    //   196: invokeinterface 129 1 0
    //   201: goto -165 -> 36
    //   204: aload_0
    //   205: getfield 114	com/sun/corba/se/impl/transport/ListenerThreadImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   208: getfield 115	com/sun/corba/se/spi/orb/ORB:transportDebugFlag	Z
    //   211: ifeq +71 -> 282
    //   214: aload_0
    //   215: new 72	java/lang/StringBuilder
    //   218: dup
    //   219: invokespecial 123	java/lang/StringBuilder:<init>	()V
    //   222: ldc 6
    //   224: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   227: aload_0
    //   228: getfield 112	com/sun/corba/se/impl/transport/ListenerThreadImpl:acceptor	Lcom/sun/corba/se/pept/transport/Acceptor;
    //   231: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   234: invokevirtual 124	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   237: invokespecial 119	com/sun/corba/se/impl/transport/ListenerThreadImpl:dprint	(Ljava/lang/String;)V
    //   240: goto +42 -> 282
    //   243: astore_2
    //   244: aload_0
    //   245: getfield 114	com/sun/corba/se/impl/transport/ListenerThreadImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   248: getfield 115	com/sun/corba/se/spi/orb/ORB:transportDebugFlag	Z
    //   251: ifeq +29 -> 280
    //   254: aload_0
    //   255: new 72	java/lang/StringBuilder
    //   258: dup
    //   259: invokespecial 123	java/lang/StringBuilder:<init>	()V
    //   262: ldc 6
    //   264: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   267: aload_0
    //   268: getfield 112	com/sun/corba/se/impl/transport/ListenerThreadImpl:acceptor	Lcom/sun/corba/se/pept/transport/Acceptor;
    //   271: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   274: invokevirtual 124	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   277: invokespecial 119	com/sun/corba/se/impl/transport/ListenerThreadImpl:dprint	(Ljava/lang/String;)V
    //   280: aload_2
    //   281: athrow
    //   282: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	283	0	this	ListenerThreadImpl
    //   127	35	1	localThrowable	Throwable
    //   243	38	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   43	124	127	java/lang/Throwable
    //   0	204	243	finally
  }
  
  public void setEnqueueTime(long paramLong)
  {
    enqueueTime = paramLong;
  }
  
  public long getEnqueueTime()
  {
    return enqueueTime;
  }
  
  public String getName()
  {
    return "ListenerThread";
  }
  
  private void dprint(String paramString)
  {
    ORBUtility.dprint("ListenerThreadImpl", paramString);
  }
  
  private void dprint(String paramString, Throwable paramThrowable)
  {
    dprint(paramString);
    paramThrowable.printStackTrace(System.out);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\transport\ListenerThreadImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
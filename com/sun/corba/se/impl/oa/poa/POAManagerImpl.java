package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.PIHandler;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.LocalObject;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAManagerPackage.State;

public class POAManagerImpl
  extends LocalObject
  implements POAManager
{
  private final POAFactory factory;
  private PIHandler pihandler;
  private State state;
  private Set poas = new HashSet(4);
  private int nInvocations = 0;
  private int nWaiters = 0;
  private int myId = 0;
  private boolean debug;
  private boolean explicitStateChange;
  
  private String stateToString(State paramState)
  {
    switch (paramState.value())
    {
    case 0: 
      return "State[HOLDING]";
    case 1: 
      return "State[ACTIVE]";
    case 2: 
      return "State[DISCARDING]";
    case 3: 
      return "State[INACTIVE]";
    }
    return "State[UNKNOWN]";
  }
  
  public String toString()
  {
    return "POAManagerImpl[myId=" + myId + " state=" + stateToString(state) + " nInvocations=" + nInvocations + " nWaiters=" + nWaiters + "]";
  }
  
  POAFactory getFactory()
  {
    return factory;
  }
  
  PIHandler getPIHandler()
  {
    return pihandler;
  }
  
  /* Error */
  private void countedWait()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   4: ifeq +38 -> 42
    //   7: aload_0
    //   8: new 156	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   15: ldc 6
    //   17: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   20: aload_0
    //   21: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   24: ldc 2
    //   26: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   29: aload_0
    //   30: getfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   33: invokevirtual 276	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   36: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   39: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   42: aload_0
    //   43: dup
    //   44: getfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   47: iconst_1
    //   48: iadd
    //   49: putfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   52: aload_0
    //   53: invokevirtual 273	java/lang/Object:wait	()V
    //   56: aload_0
    //   57: dup
    //   58: getfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   61: iconst_1
    //   62: isub
    //   63: putfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   66: aload_0
    //   67: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   70: ifeq +152 -> 222
    //   73: aload_0
    //   74: new 156	java/lang/StringBuilder
    //   77: dup
    //   78: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   81: ldc 15
    //   83: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   86: aload_0
    //   87: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   90: ldc 2
    //   92: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   95: aload_0
    //   96: getfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   99: invokevirtual 276	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   102: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   105: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   108: goto +114 -> 222
    //   111: astore_1
    //   112: aload_0
    //   113: dup
    //   114: getfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   117: iconst_1
    //   118: isub
    //   119: putfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   122: aload_0
    //   123: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   126: ifeq +96 -> 222
    //   129: aload_0
    //   130: new 156	java/lang/StringBuilder
    //   133: dup
    //   134: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   137: ldc 15
    //   139: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   142: aload_0
    //   143: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   146: ldc 2
    //   148: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   151: aload_0
    //   152: getfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   155: invokevirtual 276	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   158: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   161: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   164: goto +58 -> 222
    //   167: astore_2
    //   168: aload_0
    //   169: dup
    //   170: getfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   173: iconst_1
    //   174: isub
    //   175: putfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   178: aload_0
    //   179: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   182: ifeq +38 -> 220
    //   185: aload_0
    //   186: new 156	java/lang/StringBuilder
    //   189: dup
    //   190: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   193: ldc 15
    //   195: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   198: aload_0
    //   199: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   202: ldc 2
    //   204: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   207: aload_0
    //   208: getfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   211: invokevirtual 276	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   214: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   217: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   220: aload_2
    //   221: athrow
    //   222: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	223	0	this	POAManagerImpl
    //   111	1	1	localInterruptedException	InterruptedException
    //   167	54	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	56	111	java/lang/InterruptedException
    //   0	56	167	finally
  }
  
  private void notifyWaiters()
  {
    if (debug) {
      ORBUtility.dprint(this, "Calling notifyWaiters on POAManager " + this + " nWaiters=" + nWaiters);
    }
    if (nWaiters > 0) {
      notifyAll();
    }
  }
  
  public int getManagerId()
  {
    return myId;
  }
  
  POAManagerImpl(POAFactory paramPOAFactory, PIHandler paramPIHandler)
  {
    factory = paramPOAFactory;
    paramPOAFactory.addPoaManager(this);
    pihandler = paramPIHandler;
    myId = paramPOAFactory.newPOAManagerId();
    state = State.HOLDING;
    debug = getORBpoaDebugFlag;
    explicitStateChange = false;
    if (debug) {
      ORBUtility.dprint(this, "Creating POAManagerImpl " + this);
    }
  }
  
  synchronized void addPOA(POA paramPOA)
  {
    if (state.value() == 3)
    {
      POASystemException localPOASystemException = factory.getWrapper();
      throw localPOASystemException.addPoaInactive(CompletionStatus.COMPLETED_NO);
    }
    poas.add(paramPOA);
  }
  
  synchronized void removePOA(POA paramPOA)
  {
    poas.remove(paramPOA);
    if (poas.isEmpty()) {
      factory.removePoaManager(this);
    }
  }
  
  public short getORTState()
  {
    switch (state.value())
    {
    case 0: 
      return 0;
    case 1: 
      return 1;
    case 3: 
      return 3;
    case 2: 
      return 2;
    }
    return 4;
  }
  
  /* Error */
  public synchronized void activate()
    throws AdapterInactive
  {
    // Byte code:
    //   0: aload_0
    //   1: iconst_1
    //   2: putfield 244	com/sun/corba/se/impl/oa/poa/POAManagerImpl:explicitStateChange	Z
    //   5: aload_0
    //   6: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   9: ifeq +26 -> 35
    //   12: aload_0
    //   13: new 156	java/lang/StringBuilder
    //   16: dup
    //   17: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   20: ldc 4
    //   22: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   25: aload_0
    //   26: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   29: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   32: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   35: aload_0
    //   36: getfield 248	com/sun/corba/se/impl/oa/poa/POAManagerImpl:state	Lorg/omg/PortableServer/POAManagerPackage/State;
    //   39: invokevirtual 284	org/omg/PortableServer/POAManagerPackage/State:value	()I
    //   42: iconst_3
    //   43: if_icmpne +11 -> 54
    //   46: new 164	org/omg/PortableServer/POAManagerPackage/AdapterInactive
    //   49: dup
    //   50: invokespecial 283	org/omg/PortableServer/POAManagerPackage/AdapterInactive:<init>	()V
    //   53: athrow
    //   54: aload_0
    //   55: getstatic 251	org/omg/PortableServer/POAManagerPackage/State:ACTIVE	Lorg/omg/PortableServer/POAManagerPackage/State;
    //   58: putfield 248	com/sun/corba/se/impl/oa/poa/POAManagerImpl:state	Lorg/omg/PortableServer/POAManagerPackage/State;
    //   61: aload_0
    //   62: getfield 246	com/sun/corba/se/impl/oa/poa/POAManagerImpl:pihandler	Lcom/sun/corba/se/spi/protocol/PIHandler;
    //   65: aload_0
    //   66: getfield 240	com/sun/corba/se/impl/oa/poa/POAManagerImpl:myId	I
    //   69: aload_0
    //   70: invokevirtual 263	com/sun/corba/se/impl/oa/poa/POAManagerImpl:getORTState	()S
    //   73: invokeinterface 285 3 0
    //   78: aload_0
    //   79: invokespecial 267	com/sun/corba/se/impl/oa/poa/POAManagerImpl:notifyWaiters	()V
    //   82: aload_0
    //   83: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   86: ifeq +62 -> 148
    //   89: aload_0
    //   90: new 156	java/lang/StringBuilder
    //   93: dup
    //   94: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   97: ldc 13
    //   99: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   102: aload_0
    //   103: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   106: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   109: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   112: goto +36 -> 148
    //   115: astore_1
    //   116: aload_0
    //   117: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   120: ifeq +26 -> 146
    //   123: aload_0
    //   124: new 156	java/lang/StringBuilder
    //   127: dup
    //   128: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   131: ldc 13
    //   133: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   136: aload_0
    //   137: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   140: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   143: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   146: aload_1
    //   147: athrow
    //   148: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	149	0	this	POAManagerImpl
    //   115	32	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   35	82	115	finally
  }
  
  /* Error */
  public synchronized void hold_requests(boolean paramBoolean)
    throws AdapterInactive
  {
    // Byte code:
    //   0: aload_0
    //   1: iconst_1
    //   2: putfield 244	com/sun/corba/se/impl/oa/poa/POAManagerImpl:explicitStateChange	Z
    //   5: aload_0
    //   6: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   9: ifeq +26 -> 35
    //   12: aload_0
    //   13: new 156	java/lang/StringBuilder
    //   16: dup
    //   17: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   20: ldc 10
    //   22: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   25: aload_0
    //   26: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   29: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   32: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   35: aload_0
    //   36: getfield 248	com/sun/corba/se/impl/oa/poa/POAManagerImpl:state	Lorg/omg/PortableServer/POAManagerPackage/State;
    //   39: invokevirtual 284	org/omg/PortableServer/POAManagerPackage/State:value	()I
    //   42: iconst_3
    //   43: if_icmpne +11 -> 54
    //   46: new 164	org/omg/PortableServer/POAManagerPackage/AdapterInactive
    //   49: dup
    //   50: invokespecial 283	org/omg/PortableServer/POAManagerPackage/AdapterInactive:<init>	()V
    //   53: athrow
    //   54: aload_0
    //   55: getstatic 253	org/omg/PortableServer/POAManagerPackage/State:HOLDING	Lorg/omg/PortableServer/POAManagerPackage/State;
    //   58: putfield 248	com/sun/corba/se/impl/oa/poa/POAManagerImpl:state	Lorg/omg/PortableServer/POAManagerPackage/State;
    //   61: aload_0
    //   62: getfield 246	com/sun/corba/se/impl/oa/poa/POAManagerImpl:pihandler	Lcom/sun/corba/se/spi/protocol/PIHandler;
    //   65: aload_0
    //   66: getfield 240	com/sun/corba/se/impl/oa/poa/POAManagerImpl:myId	I
    //   69: aload_0
    //   70: invokevirtual 263	com/sun/corba/se/impl/oa/poa/POAManagerImpl:getORTState	()S
    //   73: invokeinterface 285 3 0
    //   78: aload_0
    //   79: invokespecial 267	com/sun/corba/se/impl/oa/poa/POAManagerImpl:notifyWaiters	()V
    //   82: iload_1
    //   83: ifeq +27 -> 110
    //   86: aload_0
    //   87: getfield 248	com/sun/corba/se/impl/oa/poa/POAManagerImpl:state	Lorg/omg/PortableServer/POAManagerPackage/State;
    //   90: invokevirtual 284	org/omg/PortableServer/POAManagerPackage/State:value	()I
    //   93: ifne +17 -> 110
    //   96: aload_0
    //   97: getfield 241	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nInvocations	I
    //   100: ifle +10 -> 110
    //   103: aload_0
    //   104: invokespecial 266	com/sun/corba/se/impl/oa/poa/POAManagerImpl:countedWait	()V
    //   107: goto -21 -> 86
    //   110: aload_0
    //   111: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   114: ifeq +62 -> 176
    //   117: aload_0
    //   118: new 156	java/lang/StringBuilder
    //   121: dup
    //   122: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   125: ldc 19
    //   127: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   130: aload_0
    //   131: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   134: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   137: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   140: goto +36 -> 176
    //   143: astore_2
    //   144: aload_0
    //   145: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   148: ifeq +26 -> 174
    //   151: aload_0
    //   152: new 156	java/lang/StringBuilder
    //   155: dup
    //   156: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   159: ldc 19
    //   161: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   164: aload_0
    //   165: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   168: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   171: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   174: aload_2
    //   175: athrow
    //   176: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	177	0	this	POAManagerImpl
    //   0	177	1	paramBoolean	boolean
    //   143	32	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   35	110	143	finally
  }
  
  /* Error */
  public synchronized void discard_requests(boolean paramBoolean)
    throws AdapterInactive
  {
    // Byte code:
    //   0: aload_0
    //   1: iconst_1
    //   2: putfield 244	com/sun/corba/se/impl/oa/poa/POAManagerImpl:explicitStateChange	Z
    //   5: aload_0
    //   6: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   9: ifeq +26 -> 35
    //   12: aload_0
    //   13: new 156	java/lang/StringBuilder
    //   16: dup
    //   17: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   20: ldc 10
    //   22: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   25: aload_0
    //   26: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   29: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   32: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   35: aload_0
    //   36: getfield 248	com/sun/corba/se/impl/oa/poa/POAManagerImpl:state	Lorg/omg/PortableServer/POAManagerPackage/State;
    //   39: invokevirtual 284	org/omg/PortableServer/POAManagerPackage/State:value	()I
    //   42: iconst_3
    //   43: if_icmpne +11 -> 54
    //   46: new 164	org/omg/PortableServer/POAManagerPackage/AdapterInactive
    //   49: dup
    //   50: invokespecial 283	org/omg/PortableServer/POAManagerPackage/AdapterInactive:<init>	()V
    //   53: athrow
    //   54: aload_0
    //   55: getstatic 252	org/omg/PortableServer/POAManagerPackage/State:DISCARDING	Lorg/omg/PortableServer/POAManagerPackage/State;
    //   58: putfield 248	com/sun/corba/se/impl/oa/poa/POAManagerImpl:state	Lorg/omg/PortableServer/POAManagerPackage/State;
    //   61: aload_0
    //   62: getfield 246	com/sun/corba/se/impl/oa/poa/POAManagerImpl:pihandler	Lcom/sun/corba/se/spi/protocol/PIHandler;
    //   65: aload_0
    //   66: getfield 240	com/sun/corba/se/impl/oa/poa/POAManagerImpl:myId	I
    //   69: aload_0
    //   70: invokevirtual 263	com/sun/corba/se/impl/oa/poa/POAManagerImpl:getORTState	()S
    //   73: invokeinterface 285 3 0
    //   78: aload_0
    //   79: invokespecial 267	com/sun/corba/se/impl/oa/poa/POAManagerImpl:notifyWaiters	()V
    //   82: iload_1
    //   83: ifeq +28 -> 111
    //   86: aload_0
    //   87: getfield 248	com/sun/corba/se/impl/oa/poa/POAManagerImpl:state	Lorg/omg/PortableServer/POAManagerPackage/State;
    //   90: invokevirtual 284	org/omg/PortableServer/POAManagerPackage/State:value	()I
    //   93: iconst_2
    //   94: if_icmpne +17 -> 111
    //   97: aload_0
    //   98: getfield 241	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nInvocations	I
    //   101: ifle +10 -> 111
    //   104: aload_0
    //   105: invokespecial 266	com/sun/corba/se/impl/oa/poa/POAManagerImpl:countedWait	()V
    //   108: goto -22 -> 86
    //   111: aload_0
    //   112: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   115: ifeq +62 -> 177
    //   118: aload_0
    //   119: new 156	java/lang/StringBuilder
    //   122: dup
    //   123: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   126: ldc 19
    //   128: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   131: aload_0
    //   132: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   135: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   138: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   141: goto +36 -> 177
    //   144: astore_2
    //   145: aload_0
    //   146: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   149: ifeq +26 -> 175
    //   152: aload_0
    //   153: new 156	java/lang/StringBuilder
    //   156: dup
    //   157: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   160: ldc 19
    //   162: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   165: aload_0
    //   166: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   169: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   172: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   175: aload_2
    //   176: athrow
    //   177: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	178	0	this	POAManagerImpl
    //   0	178	1	paramBoolean	boolean
    //   144	32	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   35	111	144	finally
  }
  
  public void deactivate(boolean paramBoolean1, boolean paramBoolean2)
    throws AdapterInactive
  {
    explicitStateChange = true;
    try
    {
      synchronized (this)
      {
        if (debug) {
          ORBUtility.dprint(this, "Calling deactivate on POAManager " + this);
        }
        if (state.value() == 3) {
          throw new AdapterInactive();
        }
        state = State.INACTIVE;
        pihandler.adapterManagerStateChanged(myId, getORTState());
        notifyWaiters();
      }
      ??? = new POAManagerDeactivator(this, paramBoolean1, debug);
      if (paramBoolean2)
      {
        ((POAManagerDeactivator)???).run();
      }
      else
      {
        Thread localThread = new Thread((Runnable)???);
        localThread.start();
      }
    }
    finally
    {
      synchronized (this)
      {
        if (debug) {
          ORBUtility.dprint(this, "Exiting deactivate on POAManager " + this);
        }
      }
    }
  }
  
  public State get_state()
  {
    return state;
  }
  
  /* Error */
  synchronized void checkIfActive()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   4: ifeq +26 -> 30
    //   7: aload_0
    //   8: new 156	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   15: ldc 5
    //   17: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   20: aload_0
    //   21: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   24: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   27: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   30: aload_0
    //   31: invokespecial 265	com/sun/corba/se/impl/oa/poa/POAManagerImpl:checkState	()V
    //   34: aload_0
    //   35: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   38: ifeq +62 -> 100
    //   41: aload_0
    //   42: new 156	java/lang/StringBuilder
    //   45: dup
    //   46: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   49: ldc 14
    //   51: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   54: aload_0
    //   55: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   58: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   61: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   64: goto +36 -> 100
    //   67: astore_1
    //   68: aload_0
    //   69: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   72: ifeq +26 -> 98
    //   75: aload_0
    //   76: new 156	java/lang/StringBuilder
    //   79: dup
    //   80: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   83: ldc 14
    //   85: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   88: aload_0
    //   89: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   92: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   95: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   98: aload_1
    //   99: athrow
    //   100: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	101	0	this	POAManagerImpl
    //   67	32	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	34	67	finally
  }
  
  private void checkState()
  {
    while (state.value() != 1) {
      switch (state.value())
      {
      case 0: 
      case 2: 
      case 3: 
        while (state.value() == 0)
        {
          countedWait();
          continue;
          throw factory.getWrapper().poaDiscarding();
          throw factory.getWrapper().poaInactive();
        }
      }
    }
  }
  
  /* Error */
  synchronized void enter()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   4: ifeq +26 -> 30
    //   7: aload_0
    //   8: new 156	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   15: ldc 8
    //   17: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   20: aload_0
    //   21: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   24: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   27: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   30: aload_0
    //   31: invokespecial 265	com/sun/corba/se/impl/oa/poa/POAManagerImpl:checkState	()V
    //   34: aload_0
    //   35: dup
    //   36: getfield 241	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nInvocations	I
    //   39: iconst_1
    //   40: iadd
    //   41: putfield 241	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nInvocations	I
    //   44: aload_0
    //   45: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   48: ifeq +62 -> 110
    //   51: aload_0
    //   52: new 156	java/lang/StringBuilder
    //   55: dup
    //   56: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   59: ldc 17
    //   61: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   64: aload_0
    //   65: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   68: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   71: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   74: goto +36 -> 110
    //   77: astore_1
    //   78: aload_0
    //   79: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   82: ifeq +26 -> 108
    //   85: aload_0
    //   86: new 156	java/lang/StringBuilder
    //   89: dup
    //   90: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   93: ldc 17
    //   95: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   98: aload_0
    //   99: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   102: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   105: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   108: aload_1
    //   109: athrow
    //   110: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	111	0	this	POAManagerImpl
    //   77	32	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	44	77	finally
  }
  
  /* Error */
  synchronized void exit()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   4: ifeq +26 -> 30
    //   7: aload_0
    //   8: new 156	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   15: ldc 9
    //   17: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   20: aload_0
    //   21: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   24: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   27: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   30: aload_0
    //   31: dup
    //   32: getfield 241	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nInvocations	I
    //   35: iconst_1
    //   36: isub
    //   37: putfield 241	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nInvocations	I
    //   40: aload_0
    //   41: getfield 241	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nInvocations	I
    //   44: ifne +7 -> 51
    //   47: aload_0
    //   48: invokespecial 267	com/sun/corba/se/impl/oa/poa/POAManagerImpl:notifyWaiters	()V
    //   51: aload_0
    //   52: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   55: ifeq +62 -> 117
    //   58: aload_0
    //   59: new 156	java/lang/StringBuilder
    //   62: dup
    //   63: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   66: ldc 18
    //   68: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   71: aload_0
    //   72: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   75: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   78: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   81: goto +36 -> 117
    //   84: astore_1
    //   85: aload_0
    //   86: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   89: ifeq +26 -> 115
    //   92: aload_0
    //   93: new 156	java/lang/StringBuilder
    //   96: dup
    //   97: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   100: ldc 18
    //   102: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   105: aload_0
    //   106: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   109: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   112: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   115: aload_1
    //   116: athrow
    //   117: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	118	0	this	POAManagerImpl
    //   84	32	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	51	84	finally
  }
  
  public synchronized void implicitActivation()
  {
    if (!explicitStateChange) {
      try
      {
        activate();
      }
      catch (AdapterInactive localAdapterInactive) {}
    }
  }
  
  private class POAManagerDeactivator
    implements Runnable
  {
    private boolean etherealize_objects;
    private POAManagerImpl pmi;
    private boolean debug;
    
    POAManagerDeactivator(POAManagerImpl paramPOAManagerImpl, boolean paramBoolean1, boolean paramBoolean2)
    {
      etherealize_objects = paramBoolean1;
      pmi = paramPOAManagerImpl;
      debug = paramBoolean2;
    }
    
    public void run()
    {
      try
      {
        synchronized (pmi)
        {
          if (debug) {
            ORBUtility.dprint(this, "Calling run with etherealize_objects=" + etherealize_objects + " pmi=" + pmi);
          }
          while (pmi.nInvocations > 0) {
            POAManagerImpl.this.countedWait();
          }
        }
        if (etherealize_objects)
        {
          ??? = null;
          synchronized (pmi)
          {
            if (debug) {
              ORBUtility.dprint(this, "run: Preparing to etherealize with pmi=" + pmi);
            }
            ??? = new HashSet(pmi.poas).iterator();
          }
          while (((Iterator)???).hasNext()) {
            ((POAImpl)((Iterator)???).next()).etherealizeAll();
          }
          synchronized (pmi)
          {
            if (debug) {
              ORBUtility.dprint(this, "run: removing POAManager and clearing poas with pmi=" + pmi);
            }
            factory.removePoaManager(pmi);
            poas.clear();
          }
        }
      }
      finally
      {
        if (debug) {
          synchronized (pmi)
          {
            ORBUtility.dprint(this, "Exiting run");
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\POAManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
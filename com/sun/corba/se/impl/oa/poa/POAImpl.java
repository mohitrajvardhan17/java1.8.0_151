package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.ior.ObjectAdapterIdArray;
import com.sun.corba.se.impl.ior.POAObjectKeyTemplate;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.concurrent.CondVar;
import com.sun.corba.se.impl.orbutil.concurrent.ReentrantMutex;
import com.sun.corba.se.impl.orbutil.concurrent.Sync;
import com.sun.corba.se.impl.orbutil.concurrent.SyncUtil;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.TaggedProfile;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapterBase;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.protocol.ForwardException;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableServer.AdapterActivator;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.IdAssignmentPolicy;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.IdUniquenessPolicy;
import org.omg.PortableServer.IdUniquenessPolicyValue;
import org.omg.PortableServer.ImplicitActivationPolicy;
import org.omg.PortableServer.ImplicitActivationPolicyValue;
import org.omg.PortableServer.LifespanPolicy;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAPackage.AdapterNonExistent;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongAdapter;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.RequestProcessingPolicy;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantManager;
import org.omg.PortableServer.ServantRetentionPolicy;
import org.omg.PortableServer.ServantRetentionPolicyValue;
import org.omg.PortableServer.ThreadPolicy;
import org.omg.PortableServer.ThreadPolicyValue;

public class POAImpl
  extends ObjectAdapterBase
  implements POA
{
  private boolean debug;
  private static final int STATE_START = 0;
  private static final int STATE_INIT = 1;
  private static final int STATE_INIT_DONE = 2;
  private static final int STATE_RUN = 3;
  private static final int STATE_DESTROYING = 4;
  private static final int STATE_DESTROYED = 5;
  private int state;
  private POAPolicyMediator mediator;
  private int numLevels;
  private ObjectAdapterId poaId;
  private String name;
  private POAManagerImpl manager;
  private int uniquePOAId;
  private POAImpl parent;
  private Map children;
  private AdapterActivator activator;
  private int invocationCount;
  Sync poaMutex;
  private CondVar adapterActivatorCV;
  private CondVar invokeCV;
  private CondVar beingDestroyedCV;
  protected ThreadLocal isDestroying;
  
  private String stateToString()
  {
    switch (state)
    {
    case 0: 
      return "START";
    case 1: 
      return "INIT";
    case 2: 
      return "INIT_DONE";
    case 3: 
      return "RUN";
    case 4: 
      return "DESTROYING";
    case 5: 
      return "DESTROYED";
    }
    return "UNKNOWN(" + state + ")";
  }
  
  public String toString()
  {
    return "POA[" + poaId.toString() + ", uniquePOAId=" + uniquePOAId + ", state=" + stateToString() + ", invocationCount=" + invocationCount + "]";
  }
  
  boolean getDebug()
  {
    return debug;
  }
  
  static POAFactory getPOAFactory(ORB paramORB)
  {
    return (POAFactory)paramORB.getRequestDispatcherRegistry().getObjectAdapterFactory(32);
  }
  
  static POAImpl makeRootPOA(ORB paramORB)
  {
    POAManagerImpl localPOAManagerImpl = new POAManagerImpl(getPOAFactory(paramORB), paramORB.getPIHandler());
    POAImpl localPOAImpl = new POAImpl("RootPOA", null, paramORB, 0);
    localPOAImpl.initialize(localPOAManagerImpl, Policies.rootPOAPolicies);
    return localPOAImpl;
  }
  
  int getPOAId()
  {
    return uniquePOAId;
  }
  
  void lock()
  {
    SyncUtil.acquire(poaMutex);
    if (debug) {
      ORBUtility.dprint(this, "LOCKED poa " + this);
    }
  }
  
  void unlock()
  {
    if (debug) {
      ORBUtility.dprint(this, "UNLOCKED poa " + this);
    }
    poaMutex.release();
  }
  
  Policies getPolicies()
  {
    return mediator.getPolicies();
  }
  
  private POAImpl(String paramString, POAImpl paramPOAImpl, ORB paramORB, int paramInt)
  {
    super(paramORB);
    debug = poaDebugFlag;
    if (debug) {
      ORBUtility.dprint(this, "Creating POA with name=" + paramString + " parent=" + paramPOAImpl);
    }
    state = paramInt;
    name = paramString;
    parent = paramPOAImpl;
    children = new HashMap();
    activator = null;
    uniquePOAId = getPOAFactory(paramORB).newPOAId();
    if (paramPOAImpl == null)
    {
      numLevels = 1;
    }
    else
    {
      numLevels += 1;
      children.put(paramString, this);
    }
    String[] arrayOfString = new String[numLevels];
    POAImpl localPOAImpl = this;
    int i = numLevels - 1;
    while (localPOAImpl != null)
    {
      arrayOfString[(i--)] = name;
      localPOAImpl = parent;
    }
    poaId = new ObjectAdapterIdArray(arrayOfString);
    invocationCount = 0;
    poaMutex = new ReentrantMutex(poaConcurrencyDebugFlag);
    adapterActivatorCV = new CondVar(poaMutex, poaConcurrencyDebugFlag);
    invokeCV = new CondVar(poaMutex, poaConcurrencyDebugFlag);
    beingDestroyedCV = new CondVar(poaMutex, poaConcurrencyDebugFlag);
    isDestroying = new ThreadLocal()
    {
      protected Object initialValue()
      {
        return Boolean.FALSE;
      }
    };
  }
  
  private void initialize(POAManagerImpl paramPOAManagerImpl, Policies paramPolicies)
  {
    if (debug) {
      ORBUtility.dprint(this, "Initializing poa " + this + " with POAManager=" + paramPOAManagerImpl + " policies=" + paramPolicies);
    }
    manager = paramPOAManagerImpl;
    paramPOAManagerImpl.addPOA(this);
    mediator = POAPolicyMediatorFactory.create(paramPolicies, this);
    int i = mediator.getServerId();
    int j = mediator.getScid();
    String str = getORB().getORBData().getORBId();
    POAObjectKeyTemplate localPOAObjectKeyTemplate = new POAObjectKeyTemplate(getORB(), j, i, str, poaId);
    if (debug) {
      ORBUtility.dprint(this, "Initializing poa: oktemp=" + localPOAObjectKeyTemplate);
    }
    boolean bool = true;
    initializeTemplate(localPOAObjectKeyTemplate, bool, paramPolicies, null, null, localPOAObjectKeyTemplate.getObjectAdapterId());
    if (state == 0) {
      state = 3;
    } else if (state == 1) {
      state = 2;
    } else {
      throw lifecycleWrapper().illegalPoaStateTrans();
    }
  }
  
  private boolean waitUntilRunning()
  {
    if (debug) {
      ORBUtility.dprint(this, "Calling waitUntilRunning on poa " + this);
    }
    while (state < 3) {
      try
      {
        adapterActivatorCV.await();
      }
      catch (InterruptedException localInterruptedException) {}
    }
    if (debug) {
      ORBUtility.dprint(this, "Exiting waitUntilRunning on poa " + this);
    }
    return state == 3;
  }
  
  private boolean destroyIfNotInitDone()
  {
    try
    {
      lock();
      if (debug) {
        ORBUtility.dprint(this, "Calling destroyIfNotInitDone on poa " + this);
      }
      DestroyThread localDestroyThread1 = state == 2 ? 1 : 0;
      if (localDestroyThread1 != 0)
      {
        state = 3;
      }
      else
      {
        localDestroyThread2 = new DestroyThread(false, debug);
        localDestroyThread2.doIt(this, true);
      }
      DestroyThread localDestroyThread2 = localDestroyThread1;
      return localDestroyThread2;
    }
    finally
    {
      adapterActivatorCV.broadcast();
      if (debug) {
        ORBUtility.dprint(this, "Exiting destroyIfNotInitDone on poa " + this);
      }
      unlock();
    }
  }
  
  private byte[] internalReferenceToId(org.omg.CORBA.Object paramObject)
    throws WrongAdapter
  {
    IOR localIOR = ORBUtility.getIOR(paramObject);
    IORTemplateList localIORTemplateList1 = localIOR.getIORTemplates();
    ObjectReferenceFactory localObjectReferenceFactory = getCurrentFactory();
    IORTemplateList localIORTemplateList2 = IORFactories.getIORTemplateList(localObjectReferenceFactory);
    if (!localIORTemplateList2.isEquivalent(localIORTemplateList1)) {
      throw new WrongAdapter();
    }
    Iterator localIterator = localIOR.iterator();
    if (!localIterator.hasNext()) {
      throw iorWrapper().noProfilesInIor();
    }
    TaggedProfile localTaggedProfile = (TaggedProfile)localIterator.next();
    ObjectId localObjectId = localTaggedProfile.getObjectId();
    return localObjectId.getId();
  }
  
  /* Error */
  void etherealizeAll()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 769	com/sun/corba/se/impl/oa/poa/POAImpl:lock	()V
    //   4: aload_0
    //   5: getfield 734	com/sun/corba/se/impl/oa/poa/POAImpl:debug	Z
    //   8: ifeq +26 -> 34
    //   11: aload_0
    //   12: new 457	java/lang/StringBuilder
    //   15: dup
    //   16: invokespecial 826	java/lang/StringBuilder:<init>	()V
    //   19: ldc 28
    //   21: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: aload_0
    //   25: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   28: invokevirtual 827	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   31: invokestatic 807	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   34: aload_0
    //   35: getfield 737	com/sun/corba/se/impl/oa/poa/POAImpl:mediator	Lcom/sun/corba/se/impl/oa/poa/POAPolicyMediator;
    //   38: invokeinterface 840 1 0
    //   43: aload_0
    //   44: getfield 734	com/sun/corba/se/impl/oa/poa/POAImpl:debug	Z
    //   47: ifeq +26 -> 73
    //   50: aload_0
    //   51: new 457	java/lang/StringBuilder
    //   54: dup
    //   55: invokespecial 826	java/lang/StringBuilder:<init>	()V
    //   58: ldc 57
    //   60: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   63: aload_0
    //   64: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   67: invokevirtual 827	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   70: invokestatic 807	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   73: aload_0
    //   74: invokevirtual 770	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
    //   77: goto +40 -> 117
    //   80: astore_1
    //   81: aload_0
    //   82: getfield 734	com/sun/corba/se/impl/oa/poa/POAImpl:debug	Z
    //   85: ifeq +26 -> 111
    //   88: aload_0
    //   89: new 457	java/lang/StringBuilder
    //   92: dup
    //   93: invokespecial 826	java/lang/StringBuilder:<init>	()V
    //   96: ldc 57
    //   98: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   101: aload_0
    //   102: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   105: invokevirtual 827	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   108: invokestatic 807	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   111: aload_0
    //   112: invokevirtual 770	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
    //   115: aload_1
    //   116: athrow
    //   117: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	118	0	this	POAImpl
    //   80	36	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	43	80	finally
  }
  
  /* Error */
  public POA create_POA(String paramString, POAManager paramPOAManager, Policy[] paramArrayOfPolicy)
    throws org.omg.PortableServer.POAPackage.AdapterAlreadyExists, org.omg.PortableServer.POAPackage.InvalidPolicy
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 769	com/sun/corba/se/impl/oa/poa/POAImpl:lock	()V
    //   4: aload_0
    //   5: getfield 734	com/sun/corba/se/impl/oa/poa/POAImpl:debug	Z
    //   8: ifeq +53 -> 61
    //   11: aload_0
    //   12: new 457	java/lang/StringBuilder
    //   15: dup
    //   16: invokespecial 826	java/lang/StringBuilder:<init>	()V
    //   19: ldc 21
    //   21: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: aload_1
    //   25: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   28: ldc 12
    //   30: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   33: aload_2
    //   34: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   37: ldc 9
    //   39: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   42: aload_3
    //   43: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   46: ldc 15
    //   48: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   51: aload_0
    //   52: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   55: invokevirtual 827	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   58: invokestatic 807	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   61: aload_0
    //   62: getfield 732	com/sun/corba/se/impl/oa/poa/POAImpl:state	I
    //   65: iconst_3
    //   66: if_icmple +11 -> 77
    //   69: aload_0
    //   70: invokevirtual 774	com/sun/corba/se/impl/oa/poa/POAImpl:omgLifecycleWrapper	()Lcom/sun/corba/se/impl/logging/OMGSystemException;
    //   73: invokevirtual 755	com/sun/corba/se/impl/logging/OMGSystemException:createPoaDestroy	()Lorg/omg/CORBA/BAD_INV_ORDER;
    //   76: athrow
    //   77: aload_0
    //   78: getfield 745	com/sun/corba/se/impl/oa/poa/POAImpl:children	Ljava/util/Map;
    //   81: aload_1
    //   82: invokeinterface 869 2 0
    //   87: checkcast 422	com/sun/corba/se/impl/oa/poa/POAImpl
    //   90: checkcast 422	com/sun/corba/se/impl/oa/poa/POAImpl
    //   93: astore 4
    //   95: aload 4
    //   97: ifnonnull +19 -> 116
    //   100: new 422	com/sun/corba/se/impl/oa/poa/POAImpl
    //   103: dup
    //   104: aload_1
    //   105: aload_0
    //   106: aload_0
    //   107: invokevirtual 777	com/sun/corba/se/impl/oa/poa/POAImpl:getORB	()Lcom/sun/corba/se/spi/orb/ORB;
    //   110: iconst_0
    //   111: invokespecial 786	com/sun/corba/se/impl/oa/poa/POAImpl:<init>	(Ljava/lang/String;Lcom/sun/corba/se/impl/oa/poa/POAImpl;Lcom/sun/corba/se/spi/orb/ORB;I)V
    //   114: astore 4
    //   116: aload 4
    //   118: invokevirtual 769	com/sun/corba/se/impl/oa/poa/POAImpl:lock	()V
    //   121: aload_0
    //   122: getfield 734	com/sun/corba/se/impl/oa/poa/POAImpl:debug	Z
    //   125: ifeq +27 -> 152
    //   128: aload_0
    //   129: new 457	java/lang/StringBuilder
    //   132: dup
    //   133: invokespecial 826	java/lang/StringBuilder:<init>	()V
    //   136: ldc 22
    //   138: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   141: aload 4
    //   143: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   146: invokevirtual 827	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   149: invokestatic 807	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   152: aload 4
    //   154: getfield 732	com/sun/corba/se/impl/oa/poa/POAImpl:state	I
    //   157: ifeq +20 -> 177
    //   160: aload 4
    //   162: getfield 732	com/sun/corba/se/impl/oa/poa/POAImpl:state	I
    //   165: iconst_1
    //   166: if_icmpeq +11 -> 177
    //   169: new 472	org/omg/PortableServer/POAPackage/AdapterAlreadyExists
    //   172: dup
    //   173: invokespecial 834	org/omg/PortableServer/POAPackage/AdapterAlreadyExists:<init>	()V
    //   176: athrow
    //   177: aload_2
    //   178: checkcast 425	com/sun/corba/se/impl/oa/poa/POAManagerImpl
    //   181: astore 5
    //   183: aload 5
    //   185: ifnonnull +26 -> 211
    //   188: new 425	com/sun/corba/se/impl/oa/poa/POAManagerImpl
    //   191: dup
    //   192: aload_0
    //   193: getfield 736	com/sun/corba/se/impl/oa/poa/POAImpl:manager	Lcom/sun/corba/se/impl/oa/poa/POAManagerImpl;
    //   196: invokevirtual 795	com/sun/corba/se/impl/oa/poa/POAManagerImpl:getFactory	()Lcom/sun/corba/se/impl/oa/poa/POAFactory;
    //   199: aload_0
    //   200: getfield 736	com/sun/corba/se/impl/oa/poa/POAImpl:manager	Lcom/sun/corba/se/impl/oa/poa/POAManagerImpl;
    //   203: invokevirtual 796	com/sun/corba/se/impl/oa/poa/POAManagerImpl:getPIHandler	()Lcom/sun/corba/se/spi/protocol/PIHandler;
    //   206: invokespecial 798	com/sun/corba/se/impl/oa/poa/POAManagerImpl:<init>	(Lcom/sun/corba/se/impl/oa/poa/POAFactory;Lcom/sun/corba/se/spi/protocol/PIHandler;)V
    //   209: astore 5
    //   211: aload_0
    //   212: invokevirtual 777	com/sun/corba/se/impl/oa/poa/POAImpl:getORB	()Lcom/sun/corba/se/spi/orb/ORB;
    //   215: invokevirtual 820	com/sun/corba/se/spi/orb/ORB:getCopierManager	()Lcom/sun/corba/se/spi/copyobject/CopierManager;
    //   218: invokeinterface 854 1 0
    //   223: istore 6
    //   225: new 428	com/sun/corba/se/impl/oa/poa/Policies
    //   228: dup
    //   229: aload_3
    //   230: iload 6
    //   232: invokespecial 802	com/sun/corba/se/impl/oa/poa/Policies:<init>	([Lorg/omg/CORBA/Policy;I)V
    //   235: astore 7
    //   237: aload 4
    //   239: aload 5
    //   241: aload 7
    //   243: invokespecial 783	com/sun/corba/se/impl/oa/poa/POAImpl:initialize	(Lcom/sun/corba/se/impl/oa/poa/POAManagerImpl;Lcom/sun/corba/se/impl/oa/poa/Policies;)V
    //   246: aload 4
    //   248: astore 8
    //   250: aload 4
    //   252: invokevirtual 770	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
    //   255: aload_0
    //   256: invokevirtual 770	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
    //   259: aload 8
    //   261: areturn
    //   262: astore 9
    //   264: aload 4
    //   266: invokevirtual 770	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
    //   269: aload 9
    //   271: athrow
    //   272: astore 10
    //   274: aload_0
    //   275: invokevirtual 770	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
    //   278: aload 10
    //   280: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	281	0	this	POAImpl
    //   0	281	1	paramString	String
    //   0	281	2	paramPOAManager	POAManager
    //   0	281	3	paramArrayOfPolicy	Policy[]
    //   93	172	4	localPOAImpl1	POAImpl
    //   181	59	5	localPOAManagerImpl	POAManagerImpl
    //   223	8	6	i	int
    //   235	7	7	localPolicies	Policies
    //   248	12	8	localPOAImpl2	POAImpl
    //   262	8	9	localObject1	Object
    //   272	7	10	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   116	250	262	finally
    //   262	264	262	finally
    //   0	255	272	finally
    //   262	274	272	finally
  }
  
  public POA find_POA(String paramString, boolean paramBoolean)
    throws AdapterNonExistent
  {
    POAImpl localPOAImpl = null;
    AdapterActivator localAdapterActivator = null;
    lock();
    if (debug) {
      ORBUtility.dprint(this, "Calling find_POA(name=" + paramString + " activate=" + paramBoolean + ") on poa " + this);
    }
    localPOAImpl = (POAImpl)children.get(paramString);
    if (localPOAImpl != null)
    {
      if (debug) {
        ORBUtility.dprint(this, "Calling find_POA: found poa " + localPOAImpl);
      }
      try
      {
        localPOAImpl.lock();
        unlock();
        if (!localPOAImpl.waitUntilRunning()) {
          throw omgLifecycleWrapper().poaDestroyed();
        }
      }
      finally
      {
        localPOAImpl.unlock();
      }
    }
    else
    {
      try
      {
        if (debug) {
          ORBUtility.dprint(this, "Calling find_POA: no poa found");
        }
        if ((paramBoolean) && (activator != null))
        {
          localPOAImpl = new POAImpl(paramString, this, getORB(), 1);
          if (debug) {
            ORBUtility.dprint(this, "Calling find_POA: created poa " + localPOAImpl);
          }
          localAdapterActivator = activator;
        }
        else
        {
          throw new AdapterNonExistent();
        }
      }
      finally
      {
        unlock();
      }
    }
    if (localAdapterActivator != null)
    {
      boolean bool1 = false;
      boolean bool2 = false;
      if (debug) {
        ORBUtility.dprint(this, "Calling find_POA: calling AdapterActivator");
      }
      try
      {
        synchronized (localAdapterActivator)
        {
          bool1 = localAdapterActivator.unknown_adapter(this, paramString);
        }
      }
      catch (SystemException localSystemException)
      {
        throw omgLifecycleWrapper().adapterActivatorException(localSystemException, paramString, poaId.toString());
      }
      catch (Throwable localThrowable)
      {
        lifecycleWrapper().unexpectedException(localThrowable, toString());
        if ((localThrowable instanceof ThreadDeath)) {
          throw ((ThreadDeath)localThrowable);
        }
      }
      finally
      {
        bool2 = localPOAImpl.destroyIfNotInitDone();
      }
      if (bool1)
      {
        if (!bool2) {
          throw omgLifecycleWrapper().adapterActivatorException(paramString, poaId.toString());
        }
      }
      else
      {
        if (debug) {
          ORBUtility.dprint(this, "Calling find_POA: AdapterActivator returned false");
        }
        throw new AdapterNonExistent();
      }
    }
    return localPOAImpl;
  }
  
  public void destroy(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean2) && (getORB().isDuringDispatch())) {
      throw lifecycleWrapper().destroyDeadlock();
    }
    DestroyThread localDestroyThread = new DestroyThread(paramBoolean1, debug);
    localDestroyThread.doIt(this, paramBoolean2);
  }
  
  public ThreadPolicy create_thread_policy(ThreadPolicyValue paramThreadPolicyValue)
  {
    return new ThreadPolicyImpl(paramThreadPolicyValue);
  }
  
  public LifespanPolicy create_lifespan_policy(LifespanPolicyValue paramLifespanPolicyValue)
  {
    return new LifespanPolicyImpl(paramLifespanPolicyValue);
  }
  
  public IdUniquenessPolicy create_id_uniqueness_policy(IdUniquenessPolicyValue paramIdUniquenessPolicyValue)
  {
    return new IdUniquenessPolicyImpl(paramIdUniquenessPolicyValue);
  }
  
  public IdAssignmentPolicy create_id_assignment_policy(IdAssignmentPolicyValue paramIdAssignmentPolicyValue)
  {
    return new IdAssignmentPolicyImpl(paramIdAssignmentPolicyValue);
  }
  
  public ImplicitActivationPolicy create_implicit_activation_policy(ImplicitActivationPolicyValue paramImplicitActivationPolicyValue)
  {
    return new ImplicitActivationPolicyImpl(paramImplicitActivationPolicyValue);
  }
  
  public ServantRetentionPolicy create_servant_retention_policy(ServantRetentionPolicyValue paramServantRetentionPolicyValue)
  {
    return new ServantRetentionPolicyImpl(paramServantRetentionPolicyValue);
  }
  
  public RequestProcessingPolicy create_request_processing_policy(RequestProcessingPolicyValue paramRequestProcessingPolicyValue)
  {
    return new RequestProcessingPolicyImpl(paramRequestProcessingPolicyValue);
  }
  
  public String the_name()
  {
    try
    {
      lock();
      String str = name;
      return str;
    }
    finally
    {
      unlock();
    }
  }
  
  public POA the_parent()
  {
    try
    {
      lock();
      POAImpl localPOAImpl = parent;
      return localPOAImpl;
    }
    finally
    {
      unlock();
    }
  }
  
  public POA[] the_children()
  {
    try
    {
      lock();
      Collection localCollection = children.values();
      int i = localCollection.size();
      POA[] arrayOfPOA = new POA[i];
      int j = 0;
      Iterator localIterator = localCollection.iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (POA)localIterator.next();
        arrayOfPOA[(j++)] = localObject1;
      }
      Object localObject1 = arrayOfPOA;
      return (POA[])localObject1;
    }
    finally
    {
      unlock();
    }
  }
  
  public POAManager the_POAManager()
  {
    try
    {
      lock();
      POAManagerImpl localPOAManagerImpl = manager;
      return localPOAManagerImpl;
    }
    finally
    {
      unlock();
    }
  }
  
  public AdapterActivator the_activator()
  {
    try
    {
      lock();
      AdapterActivator localAdapterActivator = activator;
      return localAdapterActivator;
    }
    finally
    {
      unlock();
    }
  }
  
  /* Error */
  public void the_activator(AdapterActivator paramAdapterActivator)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 769	com/sun/corba/se/impl/oa/poa/POAImpl:lock	()V
    //   4: aload_0
    //   5: getfield 734	com/sun/corba/se/impl/oa/poa/POAImpl:debug	Z
    //   8: ifeq +35 -> 43
    //   11: aload_0
    //   12: new 457	java/lang/StringBuilder
    //   15: dup
    //   16: invokespecial 826	java/lang/StringBuilder:<init>	()V
    //   19: ldc 46
    //   21: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: aload_0
    //   25: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   28: ldc 4
    //   30: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   33: aload_1
    //   34: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   37: invokevirtual 827	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   40: invokestatic 807	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   43: aload_0
    //   44: aload_1
    //   45: putfield 746	com/sun/corba/se/impl/oa/poa/POAImpl:activator	Lorg/omg/PortableServer/AdapterActivator;
    //   48: aload_0
    //   49: invokevirtual 770	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
    //   52: goto +10 -> 62
    //   55: astore_2
    //   56: aload_0
    //   57: invokevirtual 770	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
    //   60: aload_2
    //   61: athrow
    //   62: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	63	0	this	POAImpl
    //   0	63	1	paramAdapterActivator	AdapterActivator
    //   55	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	48	55	finally
  }
  
  public ServantManager get_servant_manager()
    throws WrongPolicy
  {
    try
    {
      lock();
      ServantManager localServantManager = mediator.getServantManager();
      return localServantManager;
    }
    finally
    {
      unlock();
    }
  }
  
  /* Error */
  public void set_servant_manager(ServantManager paramServantManager)
    throws WrongPolicy
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 769	com/sun/corba/se/impl/oa/poa/POAImpl:lock	()V
    //   4: aload_0
    //   5: getfield 734	com/sun/corba/se/impl/oa/poa/POAImpl:debug	Z
    //   8: ifeq +35 -> 43
    //   11: aload_0
    //   12: new 457	java/lang/StringBuilder
    //   15: dup
    //   16: invokespecial 826	java/lang/StringBuilder:<init>	()V
    //   19: ldc 45
    //   21: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: aload_0
    //   25: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   28: ldc 11
    //   30: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   33: aload_1
    //   34: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   37: invokevirtual 827	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   40: invokestatic 807	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   43: aload_0
    //   44: getfield 737	com/sun/corba/se/impl/oa/poa/POAImpl:mediator	Lcom/sun/corba/se/impl/oa/poa/POAPolicyMediator;
    //   47: aload_1
    //   48: invokeinterface 851 2 0
    //   53: aload_0
    //   54: invokevirtual 770	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
    //   57: goto +10 -> 67
    //   60: astore_2
    //   61: aload_0
    //   62: invokevirtual 770	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
    //   65: aload_2
    //   66: athrow
    //   67: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	68	0	this	POAImpl
    //   0	68	1	paramServantManager	ServantManager
    //   60	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	53	60	finally
  }
  
  public Servant get_servant()
    throws NoServant, WrongPolicy
  {
    try
    {
      lock();
      Servant localServant = mediator.getDefaultServant();
      return localServant;
    }
    finally
    {
      unlock();
    }
  }
  
  /* Error */
  public void set_servant(Servant paramServant)
    throws WrongPolicy
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 769	com/sun/corba/se/impl/oa/poa/POAImpl:lock	()V
    //   4: aload_0
    //   5: getfield 734	com/sun/corba/se/impl/oa/poa/POAImpl:debug	Z
    //   8: ifeq +35 -> 43
    //   11: aload_0
    //   12: new 457	java/lang/StringBuilder
    //   15: dup
    //   16: invokespecial 826	java/lang/StringBuilder:<init>	()V
    //   19: ldc 44
    //   21: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: aload_0
    //   25: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   28: ldc 5
    //   30: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   33: aload_1
    //   34: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   37: invokevirtual 827	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   40: invokestatic 807	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   43: aload_0
    //   44: getfield 737	com/sun/corba/se/impl/oa/poa/POAImpl:mediator	Lcom/sun/corba/se/impl/oa/poa/POAPolicyMediator;
    //   47: aload_1
    //   48: invokeinterface 845 2 0
    //   53: aload_0
    //   54: invokevirtual 770	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
    //   57: goto +10 -> 67
    //   60: astore_2
    //   61: aload_0
    //   62: invokevirtual 770	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
    //   65: aload_2
    //   66: athrow
    //   67: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	68	0	this	POAImpl
    //   0	68	1	paramServant	Servant
    //   60	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	53	60	finally
  }
  
  public byte[] activate_object(Servant paramServant)
    throws ServantAlreadyActive, WrongPolicy
  {
    try
    {
      lock();
      if (debug) {
        ORBUtility.dprint(this, "Calling activate_object on poa " + this + " (servant=" + paramServant + ")");
      }
      byte[] arrayOfByte1 = mediator.newSystemId();
      try
      {
        mediator.activateObject(arrayOfByte1, paramServant);
      }
      catch (ObjectAlreadyActive localObjectAlreadyActive) {}
      byte[] arrayOfByte2 = arrayOfByte1;
      return arrayOfByte2;
    }
    finally
    {
      if (debug) {
        ORBUtility.dprint(this, "Exiting activate_object on poa " + this);
      }
      unlock();
    }
  }
  
  public void activate_object_with_id(byte[] paramArrayOfByte, Servant paramServant)
    throws ObjectAlreadyActive, ServantAlreadyActive, WrongPolicy
  {
    try
    {
      lock();
      if (debug) {
        ORBUtility.dprint(this, "Calling activate_object_with_id on poa " + this + " (servant=" + paramServant + " id=" + paramArrayOfByte + ")");
      }
      byte[] arrayOfByte = (byte[])paramArrayOfByte.clone();
      mediator.activateObject(arrayOfByte, paramServant);
    }
    finally
    {
      if (debug) {
        ORBUtility.dprint(this, "Exiting activate_object_with_id on poa " + this);
      }
      unlock();
    }
  }
  
  /* Error */
  public void deactivate_object(byte[] paramArrayOfByte)
    throws ObjectNotActive, WrongPolicy
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 769	com/sun/corba/se/impl/oa/poa/POAImpl:lock	()V
    //   4: aload_0
    //   5: getfield 734	com/sun/corba/se/impl/oa/poa/POAImpl:debug	Z
    //   8: ifeq +40 -> 48
    //   11: aload_0
    //   12: new 457	java/lang/StringBuilder
    //   15: dup
    //   16: invokespecial 826	java/lang/StringBuilder:<init>	()V
    //   19: ldc 25
    //   21: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: aload_0
    //   25: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   28: ldc 1
    //   30: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   33: aload_1
    //   34: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   37: ldc 14
    //   39: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   42: invokevirtual 827	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   45: invokestatic 807	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   48: aload_0
    //   49: getfield 737	com/sun/corba/se/impl/oa/poa/POAImpl:mediator	Lcom/sun/corba/se/impl/oa/poa/POAPolicyMediator;
    //   52: aload_1
    //   53: invokeinterface 847 2 0
    //   58: pop
    //   59: aload_0
    //   60: getfield 734	com/sun/corba/se/impl/oa/poa/POAImpl:debug	Z
    //   63: ifeq +26 -> 89
    //   66: aload_0
    //   67: new 457	java/lang/StringBuilder
    //   70: dup
    //   71: invokespecial 826	java/lang/StringBuilder:<init>	()V
    //   74: ldc 54
    //   76: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   79: aload_0
    //   80: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   83: invokevirtual 827	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   86: invokestatic 807	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   89: aload_0
    //   90: invokevirtual 770	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
    //   93: goto +40 -> 133
    //   96: astore_2
    //   97: aload_0
    //   98: getfield 734	com/sun/corba/se/impl/oa/poa/POAImpl:debug	Z
    //   101: ifeq +26 -> 127
    //   104: aload_0
    //   105: new 457	java/lang/StringBuilder
    //   108: dup
    //   109: invokespecial 826	java/lang/StringBuilder:<init>	()V
    //   112: ldc 54
    //   114: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   117: aload_0
    //   118: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   121: invokevirtual 827	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   124: invokestatic 807	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   127: aload_0
    //   128: invokevirtual 770	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
    //   131: aload_2
    //   132: athrow
    //   133: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	134	0	this	POAImpl
    //   0	134	1	paramArrayOfByte	byte[]
    //   96	36	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	59	96	finally
  }
  
  public org.omg.CORBA.Object create_reference(String paramString)
    throws WrongPolicy
  {
    try
    {
      lock();
      if (debug) {
        ORBUtility.dprint(this, "Calling create_reference(repId=" + paramString + ") on poa " + this);
      }
      org.omg.CORBA.Object localObject = makeObject(paramString, mediator.newSystemId());
      return localObject;
    }
    finally
    {
      unlock();
    }
  }
  
  public org.omg.CORBA.Object create_reference_with_id(byte[] paramArrayOfByte, String paramString)
  {
    try
    {
      lock();
      if (debug) {
        ORBUtility.dprint(this, "Calling create_reference_with_id(oid=" + paramArrayOfByte + " repId=" + paramString + ") on poa " + this);
      }
      byte[] arrayOfByte = (byte[])paramArrayOfByte.clone();
      org.omg.CORBA.Object localObject = makeObject(paramString, arrayOfByte);
      return localObject;
    }
    finally
    {
      unlock();
    }
  }
  
  public byte[] servant_to_id(Servant paramServant)
    throws ServantNotActive, WrongPolicy
  {
    try
    {
      lock();
      if (debug) {
        ORBUtility.dprint(this, "Calling servant_to_id(servant=" + paramServant + ") on poa " + this);
      }
      byte[] arrayOfByte = mediator.servantToId(paramServant);
      return arrayOfByte;
    }
    finally
    {
      unlock();
    }
  }
  
  public org.omg.CORBA.Object servant_to_reference(Servant paramServant)
    throws ServantNotActive, WrongPolicy
  {
    try
    {
      lock();
      if (debug) {
        ORBUtility.dprint(this, "Calling servant_to_reference(servant=" + paramServant + ") on poa " + this);
      }
      byte[] arrayOfByte = mediator.servantToId(paramServant);
      String str = paramServant._all_interfaces(this, arrayOfByte)[0];
      org.omg.CORBA.Object localObject = create_reference_with_id(arrayOfByte, str);
      return localObject;
    }
    finally
    {
      unlock();
    }
  }
  
  public Servant reference_to_servant(org.omg.CORBA.Object paramObject)
    throws ObjectNotActive, WrongPolicy, WrongAdapter
  {
    try
    {
      lock();
      if (debug) {
        ORBUtility.dprint(this, "Calling reference_to_servant(reference=" + paramObject + ") on poa " + this);
      }
      if (state >= 4) {
        throw lifecycleWrapper().adapterDestroyed();
      }
      byte[] arrayOfByte = internalReferenceToId(paramObject);
      Servant localServant = mediator.idToServant(arrayOfByte);
      return localServant;
    }
    finally
    {
      unlock();
    }
  }
  
  public byte[] reference_to_id(org.omg.CORBA.Object paramObject)
    throws WrongAdapter, WrongPolicy
  {
    try
    {
      lock();
      if (debug) {
        ORBUtility.dprint(this, "Calling reference_to_id(reference=" + paramObject + ") on poa " + this);
      }
      if (state >= 4) {
        throw lifecycleWrapper().adapterDestroyed();
      }
      byte[] arrayOfByte = internalReferenceToId(paramObject);
      return arrayOfByte;
    }
    finally
    {
      unlock();
    }
  }
  
  public Servant id_to_servant(byte[] paramArrayOfByte)
    throws ObjectNotActive, WrongPolicy
  {
    try
    {
      lock();
      if (debug) {
        ORBUtility.dprint(this, "Calling id_to_servant(id=" + paramArrayOfByte + ") on poa " + this);
      }
      if (state >= 4) {
        throw lifecycleWrapper().adapterDestroyed();
      }
      Servant localServant = mediator.idToServant(paramArrayOfByte);
      return localServant;
    }
    finally
    {
      unlock();
    }
  }
  
  public org.omg.CORBA.Object id_to_reference(byte[] paramArrayOfByte)
    throws ObjectNotActive, WrongPolicy
  {
    try
    {
      lock();
      if (debug) {
        ORBUtility.dprint(this, "Calling id_to_reference(id=" + paramArrayOfByte + ") on poa " + this);
      }
      if (state >= 4) {
        throw lifecycleWrapper().adapterDestroyed();
      }
      Servant localServant = mediator.idToServant(paramArrayOfByte);
      String str = localServant._all_interfaces(this, paramArrayOfByte)[0];
      org.omg.CORBA.Object localObject = makeObject(str, paramArrayOfByte);
      return localObject;
    }
    finally
    {
      unlock();
    }
  }
  
  public byte[] id()
  {
    try
    {
      lock();
      byte[] arrayOfByte = getAdapterId();
      return arrayOfByte;
    }
    finally
    {
      unlock();
    }
  }
  
  public Policy getEffectivePolicy(int paramInt)
  {
    return mediator.getPolicies().get_effective_policy(paramInt);
  }
  
  public int getManagerId()
  {
    return manager.getManagerId();
  }
  
  public short getState()
  {
    return manager.getORTState();
  }
  
  public String[] getInterfaces(Object paramObject, byte[] paramArrayOfByte)
  {
    Servant localServant = (Servant)paramObject;
    return localServant._all_interfaces(this, paramArrayOfByte);
  }
  
  protected ObjectCopierFactory getObjectCopierFactory()
  {
    int i = mediator.getPolicies().getCopierId();
    CopierManager localCopierManager = getORB().getCopierManager();
    return localCopierManager.getObjectCopierFactory(i);
  }
  
  /* Error */
  public void enter()
    throws com.sun.corba.se.spi.oa.OADestroyed
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 769	com/sun/corba/se/impl/oa/poa/POAImpl:lock	()V
    //   4: aload_0
    //   5: getfield 734	com/sun/corba/se/impl/oa/poa/POAImpl:debug	Z
    //   8: ifeq +26 -> 34
    //   11: aload_0
    //   12: new 457	java/lang/StringBuilder
    //   15: dup
    //   16: invokespecial 826	java/lang/StringBuilder:<init>	()V
    //   19: ldc 27
    //   21: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: aload_0
    //   25: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   28: invokevirtual 827	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   31: invokestatic 807	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   34: aload_0
    //   35: getfield 732	com/sun/corba/se/impl/oa/poa/POAImpl:state	I
    //   38: iconst_4
    //   39: if_icmpne +30 -> 69
    //   42: aload_0
    //   43: getfield 744	com/sun/corba/se/impl/oa/poa/POAImpl:isDestroying	Ljava/lang/ThreadLocal;
    //   46: invokevirtual 832	java/lang/ThreadLocal:get	()Ljava/lang/Object;
    //   49: getstatic 750	java/lang/Boolean:FALSE	Ljava/lang/Boolean;
    //   52: if_acmpne +17 -> 69
    //   55: aload_0
    //   56: getfield 739	com/sun/corba/se/impl/oa/poa/POAImpl:beingDestroyedCV	Lcom/sun/corba/se/impl/orbutil/concurrent/CondVar;
    //   59: invokevirtual 808	com/sun/corba/se/impl/orbutil/concurrent/CondVar:await	()V
    //   62: goto -28 -> 34
    //   65: astore_1
    //   66: goto -32 -> 34
    //   69: aload_0
    //   70: invokespecial 772	com/sun/corba/se/impl/oa/poa/POAImpl:waitUntilRunning	()Z
    //   73: ifne +11 -> 84
    //   76: new 444	com/sun/corba/se/spi/oa/OADestroyed
    //   79: dup
    //   80: invokespecial 814	com/sun/corba/se/spi/oa/OADestroyed:<init>	()V
    //   83: athrow
    //   84: aload_0
    //   85: dup
    //   86: getfield 730	com/sun/corba/se/impl/oa/poa/POAImpl:invocationCount	I
    //   89: iconst_1
    //   90: iadd
    //   91: putfield 730	com/sun/corba/se/impl/oa/poa/POAImpl:invocationCount	I
    //   94: aload_0
    //   95: getfield 734	com/sun/corba/se/impl/oa/poa/POAImpl:debug	Z
    //   98: ifeq +26 -> 124
    //   101: aload_0
    //   102: new 457	java/lang/StringBuilder
    //   105: dup
    //   106: invokespecial 826	java/lang/StringBuilder:<init>	()V
    //   109: ldc 56
    //   111: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   114: aload_0
    //   115: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   118: invokevirtual 827	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   121: invokestatic 807	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   124: aload_0
    //   125: invokevirtual 770	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
    //   128: goto +40 -> 168
    //   131: astore_2
    //   132: aload_0
    //   133: getfield 734	com/sun/corba/se/impl/oa/poa/POAImpl:debug	Z
    //   136: ifeq +26 -> 162
    //   139: aload_0
    //   140: new 457	java/lang/StringBuilder
    //   143: dup
    //   144: invokespecial 826	java/lang/StringBuilder:<init>	()V
    //   147: ldc 56
    //   149: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   152: aload_0
    //   153: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   156: invokevirtual 827	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   159: invokestatic 807	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   162: aload_0
    //   163: invokevirtual 770	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
    //   166: aload_2
    //   167: athrow
    //   168: aload_0
    //   169: getfield 736	com/sun/corba/se/impl/oa/poa/POAImpl:manager	Lcom/sun/corba/se/impl/oa/poa/POAManagerImpl;
    //   172: invokevirtual 793	com/sun/corba/se/impl/oa/poa/POAManagerImpl:enter	()V
    //   175: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	176	0	this	POAImpl
    //   65	1	1	localInterruptedException	InterruptedException
    //   131	36	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   55	62	65	java/lang/InterruptedException
    //   0	94	131	finally
  }
  
  /* Error */
  public void exit()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 769	com/sun/corba/se/impl/oa/poa/POAImpl:lock	()V
    //   4: aload_0
    //   5: getfield 734	com/sun/corba/se/impl/oa/poa/POAImpl:debug	Z
    //   8: ifeq +26 -> 34
    //   11: aload_0
    //   12: new 457	java/lang/StringBuilder
    //   15: dup
    //   16: invokespecial 826	java/lang/StringBuilder:<init>	()V
    //   19: ldc 29
    //   21: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: aload_0
    //   25: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   28: invokevirtual 827	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   31: invokestatic 807	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   34: aload_0
    //   35: dup
    //   36: getfield 730	com/sun/corba/se/impl/oa/poa/POAImpl:invocationCount	I
    //   39: iconst_1
    //   40: isub
    //   41: putfield 730	com/sun/corba/se/impl/oa/poa/POAImpl:invocationCount	I
    //   44: aload_0
    //   45: getfield 730	com/sun/corba/se/impl/oa/poa/POAImpl:invocationCount	I
    //   48: ifne +18 -> 66
    //   51: aload_0
    //   52: getfield 732	com/sun/corba/se/impl/oa/poa/POAImpl:state	I
    //   55: iconst_4
    //   56: if_icmpne +10 -> 66
    //   59: aload_0
    //   60: getfield 740	com/sun/corba/se/impl/oa/poa/POAImpl:invokeCV	Lcom/sun/corba/se/impl/orbutil/concurrent/CondVar;
    //   63: invokevirtual 809	com/sun/corba/se/impl/orbutil/concurrent/CondVar:broadcast	()V
    //   66: aload_0
    //   67: getfield 734	com/sun/corba/se/impl/oa/poa/POAImpl:debug	Z
    //   70: ifeq +26 -> 96
    //   73: aload_0
    //   74: new 457	java/lang/StringBuilder
    //   77: dup
    //   78: invokespecial 826	java/lang/StringBuilder:<init>	()V
    //   81: ldc 58
    //   83: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   86: aload_0
    //   87: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   90: invokevirtual 827	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   93: invokestatic 807	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   96: aload_0
    //   97: invokevirtual 770	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
    //   100: goto +40 -> 140
    //   103: astore_1
    //   104: aload_0
    //   105: getfield 734	com/sun/corba/se/impl/oa/poa/POAImpl:debug	Z
    //   108: ifeq +26 -> 134
    //   111: aload_0
    //   112: new 457	java/lang/StringBuilder
    //   115: dup
    //   116: invokespecial 826	java/lang/StringBuilder:<init>	()V
    //   119: ldc 58
    //   121: invokevirtual 831	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   124: aload_0
    //   125: invokevirtual 830	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   128: invokevirtual 827	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   131: invokestatic 807	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   134: aload_0
    //   135: invokevirtual 770	com/sun/corba/se/impl/oa/poa/POAImpl:unlock	()V
    //   138: aload_1
    //   139: athrow
    //   140: aload_0
    //   141: getfield 736	com/sun/corba/se/impl/oa/poa/POAImpl:manager	Lcom/sun/corba/se/impl/oa/poa/POAManagerImpl;
    //   144: invokevirtual 794	com/sun/corba/se/impl/oa/poa/POAManagerImpl:exit	()V
    //   147: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	148	0	this	POAImpl
    //   103	36	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	66	103	finally
  }
  
  public void getInvocationServant(OAInvocationInfo paramOAInvocationInfo)
  {
    try
    {
      lock();
      if (debug) {
        ORBUtility.dprint(this, "Calling getInvocationServant on poa " + this);
      }
      Object localObject1 = null;
      try
      {
        localObject1 = mediator.getInvocationServant(paramOAInvocationInfo.id(), paramOAInvocationInfo.getOperation());
      }
      catch (ForwardRequest localForwardRequest)
      {
        throw new ForwardException(getORB(), forward_reference);
      }
      paramOAInvocationInfo.setServant(localObject1);
    }
    finally
    {
      if (debug) {
        ORBUtility.dprint(this, "Exiting getInvocationServant on poa " + this);
      }
      unlock();
    }
  }
  
  public org.omg.CORBA.Object getLocalServant(byte[] paramArrayOfByte)
  {
    return null;
  }
  
  public void returnServant()
  {
    try
    {
      lock();
      if (debug) {
        ORBUtility.dprint(this, "Calling returnServant on poa " + this);
      }
      mediator.returnServant();
    }
    catch (Throwable localThrowable)
    {
      if (debug) {
        ORBUtility.dprint(this, "Exception " + localThrowable + " in returnServant on poa " + this);
      }
      if ((localThrowable instanceof Error)) {
        throw ((Error)localThrowable);
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
    }
    finally
    {
      if (debug) {
        ORBUtility.dprint(this, "Exiting returnServant on poa " + this);
      }
      unlock();
    }
  }
  
  static class DestroyThread
    extends Thread
  {
    private boolean wait;
    private boolean etherealize;
    private boolean debug;
    private POAImpl thePoa;
    
    public DestroyThread(boolean paramBoolean1, boolean paramBoolean2)
    {
      etherealize = paramBoolean1;
      debug = paramBoolean2;
    }
    
    public void doIt(POAImpl paramPOAImpl, boolean paramBoolean)
    {
      if (debug) {
        ORBUtility.dprint(this, "Calling DestroyThread.doIt(thePOA=" + paramPOAImpl + " wait=" + paramBoolean + " etherealize=" + etherealize);
      }
      thePoa = paramPOAImpl;
      wait = paramBoolean;
      if (paramBoolean)
      {
        run();
      }
      else
      {
        try
        {
          setDaemon(true);
        }
        catch (Exception localException) {}
        start();
      }
    }
    
    public void run()
    {
      HashSet localHashSet = new HashSet();
      performDestroy(thePoa, localHashSet);
      Iterator localIterator = localHashSet.iterator();
      ObjectReferenceTemplate[] arrayOfObjectReferenceTemplate = new ObjectReferenceTemplate[localHashSet.size()];
      int i = 0;
      while (localIterator.hasNext()) {
        arrayOfObjectReferenceTemplate[(i++)] = ((ObjectReferenceTemplate)localIterator.next());
      }
      thePoa.getORB().getPIHandler().adapterStateChanged(arrayOfObjectReferenceTemplate, (short)4);
    }
    
    private boolean prepareForDestruction(POAImpl paramPOAImpl, Set paramSet)
    {
      POAImpl[] arrayOfPOAImpl = null;
      try
      {
        paramPOAImpl.lock();
        if (debug) {
          ORBUtility.dprint(this, "Calling performDestroy on poa " + paramPOAImpl);
        }
        if (state <= 3)
        {
          state = 4;
        }
        else
        {
          if (wait) {
            while (state != 5) {
              try
              {
                beingDestroyedCV.await();
              }
              catch (InterruptedException localInterruptedException) {}
            }
          }
          i = 0;
          return i;
        }
        isDestroying.set(Boolean.TRUE);
        arrayOfPOAImpl = (POAImpl[])children.values().toArray(new POAImpl[0]);
      }
      finally
      {
        paramPOAImpl.unlock();
      }
      for (int i = 0; i < arrayOfPOAImpl.length; i++) {
        performDestroy(arrayOfPOAImpl[i], paramSet);
      }
      return true;
    }
    
    public void performDestroy(POAImpl paramPOAImpl, Set paramSet)
    {
      if (!prepareForDestruction(paramPOAImpl, paramSet)) {
        return;
      }
      POAImpl localPOAImpl = parent;
      int i = localPOAImpl == null ? 1 : 0;
      try
      {
        if (i == 0) {
          localPOAImpl.lock();
        }
        try
        {
          paramPOAImpl.lock();
          completeDestruction(paramPOAImpl, localPOAImpl, paramSet);
        }
        finally
        {
          paramPOAImpl.unlock();
          if (i != 0) {
            manager.getFactory().registerRootPOA();
          }
        }
      }
      finally
      {
        if (i == 0)
        {
          localPOAImpl.unlock();
          parent = null;
        }
      }
    }
    
    private void completeDestruction(POAImpl paramPOAImpl1, POAImpl paramPOAImpl2, Set paramSet)
    {
      if (debug) {
        ORBUtility.dprint(this, "Calling completeDestruction on poa " + paramPOAImpl1);
      }
      try
      {
        while (invocationCount != 0) {
          try
          {
            invokeCV.await();
          }
          catch (InterruptedException localInterruptedException) {}
        }
        if (mediator != null)
        {
          if (etherealize) {
            mediator.etherealizeAll();
          }
          mediator.clearAOM();
        }
        if (manager != null) {
          manager.removePOA(paramPOAImpl1);
        }
        if (paramPOAImpl2 != null) {
          children.remove(name);
        }
        paramSet.add(paramPOAImpl1.getAdapterTemplate());
      }
      catch (Throwable localThrowable)
      {
        if ((localThrowable instanceof ThreadDeath)) {
          throw ((ThreadDeath)localThrowable);
        }
        paramPOAImpl1.lifecycleWrapper().unexpectedException(localThrowable, paramPOAImpl1.toString());
      }
      finally
      {
        state = 5;
        beingDestroyedCV.broadcast();
        isDestroying.set(Boolean.FALSE);
        if (debug) {
          ORBUtility.dprint(this, "Exiting completeDestruction on poa " + paramPOAImpl1);
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\POAImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
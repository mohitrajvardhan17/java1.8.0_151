package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.corba.ServerRequestImpl;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo.CodeSetContext;
import com.sun.corba.se.impl.encoding.MarshalInputStream;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry.Entry;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.oa.NullServant;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.CorbaProtocolHandler;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.ForwardException;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.servicecontext.CodeSetServiceContext;
import com.sun.corba.se.spi.servicecontext.ORBVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.SendingContextServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
import com.sun.corba.se.spi.transport.CorbaConnection;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ServerRequest;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.UnknownException;

public class CorbaServerRequestDispatcherImpl
  implements CorbaServerRequestDispatcher
{
  protected ORB orb;
  private ORBUtilSystemException wrapper;
  private POASystemException poaWrapper;
  
  public CorbaServerRequestDispatcherImpl(ORB paramORB)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    poaWrapper = POASystemException.get(paramORB, "rpc.protocol");
  }
  
  public IOR locate(ObjectKey paramObjectKey)
  {
    try
    {
      if (orb.subcontractDebugFlag) {
        dprint(".locate->");
      }
      ObjectKeyTemplate localObjectKeyTemplate = paramObjectKey.getTemplate();
      try
      {
        checkServerId(paramObjectKey);
      }
      catch (ForwardException localForwardException)
      {
        IOR localIOR2 = localForwardException.getIOR();
        return localIOR2;
      }
      findObjectAdapter(localObjectKeyTemplate);
      IOR localIOR1 = null;
      return localIOR1;
    }
    finally
    {
      if (orb.subcontractDebugFlag) {
        dprint(".locate<-");
      }
    }
  }
  
  public void dispatch(MessageMediator paramMessageMediator)
  {
    CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    try
    {
      if (orb.subcontractDebugFlag) {
        dprint(".dispatch->: " + opAndId(localCorbaMessageMediator));
      }
      consumeServiceContexts(localCorbaMessageMediator);
      ((MarshalInputStream)localCorbaMessageMediator.getInputObject()).performORBVersionSpecificInit();
      ObjectKey localObjectKey = localCorbaMessageMediator.getObjectKey();
      try
      {
        checkServerId(localObjectKey);
      }
      catch (ForwardException localForwardException1)
      {
        if (orb.subcontractDebugFlag) {
          dprint(".dispatch: " + opAndId(localCorbaMessageMediator) + ": bad server id");
        }
        localCorbaMessageMediator.getProtocolHandler().createLocationForward(localCorbaMessageMediator, localForwardException1.getIOR(), null);
        return;
      }
      String str = localCorbaMessageMediator.getOperationName();
      ObjectAdapter localObjectAdapter = null;
      try
      {
        byte[] arrayOfByte = localObjectKey.getId().getId();
        localObject1 = localObjectKey.getTemplate();
        localObjectAdapter = findObjectAdapter((ObjectKeyTemplate)localObject1);
        localObject2 = getServantWithPI(localCorbaMessageMediator, localObjectAdapter, arrayOfByte, (ObjectKeyTemplate)localObject1, str);
        dispatchToServant(localObject2, localCorbaMessageMediator, arrayOfByte, localObjectAdapter);
      }
      catch (ForwardException localForwardException2)
      {
        if (orb.subcontractDebugFlag) {
          dprint(".dispatch: " + opAndId(localCorbaMessageMediator) + ": ForwardException caught");
        }
        localCorbaMessageMediator.getProtocolHandler().createLocationForward(localCorbaMessageMediator, localForwardException2.getIOR(), null);
      }
      catch (OADestroyed localOADestroyed)
      {
        if (orb.subcontractDebugFlag) {
          dprint(".dispatch: " + opAndId(localCorbaMessageMediator) + ": OADestroyed exception caught");
        }
        dispatch(localCorbaMessageMediator);
      }
      catch (RequestCanceledException localRequestCanceledException)
      {
        if (orb.subcontractDebugFlag) {
          dprint(".dispatch: " + opAndId(localCorbaMessageMediator) + ": RequestCanceledException caught");
        }
        throw localRequestCanceledException;
      }
      catch (UnknownException localUnknownException)
      {
        if (orb.subcontractDebugFlag) {
          dprint(".dispatch: " + opAndId(localCorbaMessageMediator) + ": UnknownException caught " + localUnknownException);
        }
        if ((originalEx instanceof RequestCanceledException)) {
          throw ((RequestCanceledException)originalEx);
        }
        Object localObject1 = new ServiceContexts(orb);
        Object localObject2 = new UEInfoServiceContext(originalEx);
        ((ServiceContexts)localObject1).put((ServiceContext)localObject2);
        UNKNOWN localUNKNOWN = wrapper.unknownExceptionInDispatch(CompletionStatus.COMPLETED_MAYBE, localUnknownException);
        localCorbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(localCorbaMessageMediator, localUNKNOWN, (ServiceContexts)localObject1);
      }
      catch (Throwable localThrowable)
      {
        if (orb.subcontractDebugFlag) {
          dprint(".dispatch: " + opAndId(localCorbaMessageMediator) + ": other exception " + localThrowable);
        }
        localCorbaMessageMediator.getProtocolHandler().handleThrowableDuringServerDispatch(localCorbaMessageMediator, localThrowable, CompletionStatus.COMPLETED_MAYBE);
      }
      return;
    }
    finally
    {
      if (orb.subcontractDebugFlag) {
        dprint(".dispatch<-: " + opAndId(localCorbaMessageMediator));
      }
    }
  }
  
  /* Error */
  private void releaseServant(ObjectAdapter paramObjectAdapter)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 546	com/sun/corba/se/impl/protocol/CorbaServerRequestDispatcherImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   4: getfield 548	com/sun/corba/se/spi/orb/ORB:subcontractDebugFlag	Z
    //   7: ifeq +9 -> 16
    //   10: aload_0
    //   11: ldc 34
    //   13: invokevirtual 570	com/sun/corba/se/impl/protocol/CorbaServerRequestDispatcherImpl:dprint	(Ljava/lang/String;)V
    //   16: aload_1
    //   17: ifnonnull +36 -> 53
    //   20: aload_0
    //   21: getfield 546	com/sun/corba/se/impl/protocol/CorbaServerRequestDispatcherImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   24: getfield 548	com/sun/corba/se/spi/orb/ORB:subcontractDebugFlag	Z
    //   27: ifeq +9 -> 36
    //   30: aload_0
    //   31: ldc 35
    //   33: invokevirtual 570	com/sun/corba/se/impl/protocol/CorbaServerRequestDispatcherImpl:dprint	(Ljava/lang/String;)V
    //   36: aload_0
    //   37: getfield 546	com/sun/corba/se/impl/protocol/CorbaServerRequestDispatcherImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   40: getfield 548	com/sun/corba/se/spi/orb/ORB:subcontractDebugFlag	Z
    //   43: ifeq +9 -> 52
    //   46: aload_0
    //   47: ldc 36
    //   49: invokevirtual 570	com/sun/corba/se/impl/protocol/CorbaServerRequestDispatcherImpl:dprint	(Ljava/lang/String;)V
    //   52: return
    //   53: aload_1
    //   54: invokeinterface 628 1 0
    //   59: aload_1
    //   60: invokeinterface 627 1 0
    //   65: aload_0
    //   66: getfield 546	com/sun/corba/se/impl/protocol/CorbaServerRequestDispatcherImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   69: invokevirtual 589	com/sun/corba/se/spi/orb/ORB:popInvocationInfo	()Lcom/sun/corba/se/spi/oa/OAInvocationInfo;
    //   72: pop
    //   73: goto +20 -> 93
    //   76: astore_2
    //   77: aload_1
    //   78: invokeinterface 627 1 0
    //   83: aload_0
    //   84: getfield 546	com/sun/corba/se/impl/protocol/CorbaServerRequestDispatcherImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   87: invokevirtual 589	com/sun/corba/se/spi/orb/ORB:popInvocationInfo	()Lcom/sun/corba/se/spi/oa/OAInvocationInfo;
    //   90: pop
    //   91: aload_2
    //   92: athrow
    //   93: aload_0
    //   94: getfield 546	com/sun/corba/se/impl/protocol/CorbaServerRequestDispatcherImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   97: getfield 548	com/sun/corba/se/spi/orb/ORB:subcontractDebugFlag	Z
    //   100: ifeq +31 -> 131
    //   103: aload_0
    //   104: ldc 36
    //   106: invokevirtual 570	com/sun/corba/se/impl/protocol/CorbaServerRequestDispatcherImpl:dprint	(Ljava/lang/String;)V
    //   109: goto +22 -> 131
    //   112: astore_3
    //   113: aload_0
    //   114: getfield 546	com/sun/corba/se/impl/protocol/CorbaServerRequestDispatcherImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   117: getfield 548	com/sun/corba/se/spi/orb/ORB:subcontractDebugFlag	Z
    //   120: ifeq +9 -> 129
    //   123: aload_0
    //   124: ldc 36
    //   126: invokevirtual 570	com/sun/corba/se/impl/protocol/CorbaServerRequestDispatcherImpl:dprint	(Ljava/lang/String;)V
    //   129: aload_3
    //   130: athrow
    //   131: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	132	0	this	CorbaServerRequestDispatcherImpl
    //   0	132	1	paramObjectAdapter	ObjectAdapter
    //   76	16	2	localObject1	Object
    //   112	18	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   53	59	76	finally
    //   0	36	112	finally
    //   53	93	112	finally
  }
  
  private Object getServant(ObjectAdapter paramObjectAdapter, byte[] paramArrayOfByte, String paramString)
    throws OADestroyed
  {
    try
    {
      if (orb.subcontractDebugFlag) {
        dprint(".getServant->");
      }
      OAInvocationInfo localOAInvocationInfo = paramObjectAdapter.makeInvocationInfo(paramArrayOfByte);
      localOAInvocationInfo.setOperation(paramString);
      orb.pushInvocationInfo(localOAInvocationInfo);
      paramObjectAdapter.getInvocationServant(localOAInvocationInfo);
      Object localObject1 = localOAInvocationInfo.getServantContainer();
      return localObject1;
    }
    finally
    {
      if (orb.subcontractDebugFlag) {
        dprint(".getServant<-");
      }
    }
  }
  
  protected Object getServantWithPI(CorbaMessageMediator paramCorbaMessageMediator, ObjectAdapter paramObjectAdapter, byte[] paramArrayOfByte, ObjectKeyTemplate paramObjectKeyTemplate, String paramString)
    throws OADestroyed
  {
    try
    {
      if (orb.subcontractDebugFlag) {
        dprint(".getServantWithPI->");
      }
      orb.getPIHandler().initializeServerPIInfo(paramCorbaMessageMediator, paramObjectAdapter, paramArrayOfByte, paramObjectKeyTemplate);
      orb.getPIHandler().invokeServerPIStartingPoint();
      paramObjectAdapter.enter();
      if (paramCorbaMessageMediator != null) {
        paramCorbaMessageMediator.setExecuteReturnServantInResponseConstructor(true);
      }
      Object localObject1 = getServant(paramObjectAdapter, paramArrayOfByte, paramString);
      String str = "unknown";
      if ((localObject1 instanceof NullServant)) {
        handleNullServant(paramString, (NullServant)localObject1);
      } else {
        str = paramObjectAdapter.getInterfaces(localObject1, paramArrayOfByte)[0];
      }
      orb.getPIHandler().setServerPIInfo(localObject1, str);
      if (((localObject1 != null) && (!(localObject1 instanceof org.omg.CORBA.DynamicImplementation)) && (!(localObject1 instanceof org.omg.PortableServer.DynamicImplementation))) || (SpecialMethod.getSpecialMethod(paramString) != null)) {
        orb.getPIHandler().invokeServerPIIntermediatePoint();
      }
      Object localObject2 = localObject1;
      return localObject2;
    }
    finally
    {
      if (orb.subcontractDebugFlag) {
        dprint(".getServantWithPI<-");
      }
    }
  }
  
  protected void checkServerId(ObjectKey paramObjectKey)
  {
    try
    {
      if (orb.subcontractDebugFlag) {
        dprint(".checkServerId->");
      }
      ObjectKeyTemplate localObjectKeyTemplate = paramObjectKey.getTemplate();
      int i = localObjectKeyTemplate.getServerId();
      int j = localObjectKeyTemplate.getSubcontractId();
      if (!orb.isLocalServerId(j, i))
      {
        if (orb.subcontractDebugFlag) {
          dprint(".checkServerId: bad server id");
        }
        orb.handleBadServerId(paramObjectKey);
      }
    }
    finally
    {
      if (orb.subcontractDebugFlag) {
        dprint(".checkServerId<-");
      }
    }
  }
  
  private ObjectAdapter findObjectAdapter(ObjectKeyTemplate paramObjectKeyTemplate)
  {
    try
    {
      if (orb.subcontractDebugFlag) {
        dprint(".findObjectAdapter->");
      }
      RequestDispatcherRegistry localRequestDispatcherRegistry = orb.getRequestDispatcherRegistry();
      int i = paramObjectKeyTemplate.getSubcontractId();
      ObjectAdapterFactory localObjectAdapterFactory = localRequestDispatcherRegistry.getObjectAdapterFactory(i);
      if (localObjectAdapterFactory == null) {
        throw wrapper.noObjectAdapterFactory();
      }
      ObjectAdapterId localObjectAdapterId = paramObjectKeyTemplate.getObjectAdapterId();
      ObjectAdapter localObjectAdapter1 = localObjectAdapterFactory.find(localObjectAdapterId);
      if (localObjectAdapter1 == null) {
        throw wrapper.badAdapterId();
      }
      ObjectAdapter localObjectAdapter2 = localObjectAdapter1;
      return localObjectAdapter2;
    }
    finally
    {
      if (orb.subcontractDebugFlag) {
        dprint(".findObjectAdapter<-");
      }
    }
  }
  
  protected void handleNullServant(String paramString, NullServant paramNullServant)
  {
    try
    {
      if (orb.subcontractDebugFlag) {
        dprint(".handleNullServant->: " + paramString);
      }
      SpecialMethod localSpecialMethod = SpecialMethod.getSpecialMethod(paramString);
      if ((localSpecialMethod == null) || (!localSpecialMethod.isNonExistentMethod()))
      {
        if (orb.subcontractDebugFlag) {
          dprint(".handleNullServant: " + paramString + ": throwing OBJECT_NOT_EXIST");
        }
        throw paramNullServant.getException();
      }
    }
    finally
    {
      if (orb.subcontractDebugFlag) {
        dprint(".handleNullServant<-: " + paramString);
      }
    }
  }
  
  protected void consumeServiceContexts(CorbaMessageMediator paramCorbaMessageMediator)
  {
    try
    {
      if (orb.subcontractDebugFlag) {
        dprint(".consumeServiceContexts->: " + opAndId(paramCorbaMessageMediator));
      }
      ServiceContexts localServiceContexts = paramCorbaMessageMediator.getRequestServiceContexts();
      GIOPVersion localGIOPVersion = paramCorbaMessageMediator.getGIOPVersion();
      boolean bool = processCodeSetContext(paramCorbaMessageMediator, localServiceContexts);
      if (orb.subcontractDebugFlag)
      {
        dprint(".consumeServiceContexts: " + opAndId(paramCorbaMessageMediator) + ": GIOP version: " + localGIOPVersion);
        dprint(".consumeServiceContexts: " + opAndId(paramCorbaMessageMediator) + ": as code set context? " + bool);
      }
      ServiceContext localServiceContext = localServiceContexts.get(6);
      Object localObject1;
      if (localServiceContext != null)
      {
        SendingContextServiceContext localSendingContextServiceContext = (SendingContextServiceContext)localServiceContext;
        localObject1 = localSendingContextServiceContext.getIOR();
        try
        {
          ((CorbaConnection)paramCorbaMessageMediator.getConnection()).setCodeBaseIOR((IOR)localObject1);
        }
        catch (ThreadDeath localThreadDeath)
        {
          throw localThreadDeath;
        }
        catch (Throwable localThrowable)
        {
          throw wrapper.badStringifiedIor(localThrowable);
        }
      }
      int i = 0;
      if ((localGIOPVersion.equals(GIOPVersion.V1_0)) && (bool))
      {
        if (orb.subcontractDebugFlag) {
          dprint(".consumeServiceCOntexts: " + opAndId(paramCorbaMessageMediator) + ": Determined to be an old Sun ORB");
        }
        orb.setORBVersion(ORBVersionFactory.getOLD());
      }
      else
      {
        i = 1;
      }
      localServiceContext = localServiceContexts.get(1313165056);
      if (localServiceContext != null)
      {
        localObject1 = (ORBVersionServiceContext)localServiceContext;
        ORBVersion localORBVersion = ((ORBVersionServiceContext)localObject1).getVersion();
        orb.setORBVersion(localORBVersion);
        i = 0;
      }
      if (i != 0)
      {
        if (orb.subcontractDebugFlag) {
          dprint(".consumeServiceContexts: " + opAndId(paramCorbaMessageMediator) + ": Determined to be a foreign ORB");
        }
        orb.setORBVersion(ORBVersionFactory.getFOREIGN());
      }
    }
    finally
    {
      if (orb.subcontractDebugFlag) {
        dprint(".consumeServiceContexts<-: " + opAndId(paramCorbaMessageMediator));
      }
    }
  }
  
  protected CorbaMessageMediator dispatchToServant(Object paramObject, CorbaMessageMediator paramCorbaMessageMediator, byte[] paramArrayOfByte, ObjectAdapter paramObjectAdapter)
  {
    try
    {
      if (orb.subcontractDebugFlag) {
        dprint(".dispatchToServant->: " + opAndId(paramCorbaMessageMediator));
      }
      CorbaMessageMediator localCorbaMessageMediator = null;
      String str = paramCorbaMessageMediator.getOperationName();
      SpecialMethod localSpecialMethod = SpecialMethod.getSpecialMethod(str);
      if (localSpecialMethod != null)
      {
        if (orb.subcontractDebugFlag) {
          dprint(".dispatchToServant: " + opAndId(paramCorbaMessageMediator) + ": Handling special method");
        }
        localCorbaMessageMediator = localSpecialMethod.invoke(paramObject, paramCorbaMessageMediator, paramArrayOfByte, paramObjectAdapter);
        localObject1 = localCorbaMessageMediator;
        return (CorbaMessageMediator)localObject1;
      }
      Object localObject2;
      if ((paramObject instanceof org.omg.CORBA.DynamicImplementation))
      {
        if (orb.subcontractDebugFlag) {
          dprint(".dispatchToServant: " + opAndId(paramCorbaMessageMediator) + ": Handling old style DSI type servant");
        }
        localObject1 = (org.omg.CORBA.DynamicImplementation)paramObject;
        localObject2 = new ServerRequestImpl(paramCorbaMessageMediator, orb);
        ((org.omg.CORBA.DynamicImplementation)localObject1).invoke((ServerRequest)localObject2);
        localCorbaMessageMediator = handleDynamicResult((ServerRequestImpl)localObject2, paramCorbaMessageMediator);
      }
      else if ((paramObject instanceof org.omg.PortableServer.DynamicImplementation))
      {
        if (orb.subcontractDebugFlag) {
          dprint(".dispatchToServant: " + opAndId(paramCorbaMessageMediator) + ": Handling POA DSI type servant");
        }
        localObject1 = (org.omg.PortableServer.DynamicImplementation)paramObject;
        localObject2 = new ServerRequestImpl(paramCorbaMessageMediator, orb);
        ((org.omg.PortableServer.DynamicImplementation)localObject1).invoke((ServerRequest)localObject2);
        localCorbaMessageMediator = handleDynamicResult((ServerRequestImpl)localObject2, paramCorbaMessageMediator);
      }
      else
      {
        if (orb.subcontractDebugFlag) {
          dprint(".dispatchToServant: " + opAndId(paramCorbaMessageMediator) + ": Handling invoke handler type servant");
        }
        localObject1 = (InvokeHandler)paramObject;
        localObject2 = ((InvokeHandler)localObject1)._invoke(str, (InputStream)paramCorbaMessageMediator.getInputObject(), paramCorbaMessageMediator);
        localCorbaMessageMediator = (CorbaMessageMediator)((OutputObject)localObject2).getMessageMediator();
      }
      Object localObject1 = localCorbaMessageMediator;
      return (CorbaMessageMediator)localObject1;
    }
    finally
    {
      if (orb.subcontractDebugFlag) {
        dprint(".dispatchToServant<-: " + opAndId(paramCorbaMessageMediator));
      }
    }
  }
  
  protected CorbaMessageMediator handleDynamicResult(ServerRequestImpl paramServerRequestImpl, CorbaMessageMediator paramCorbaMessageMediator)
  {
    try
    {
      if (orb.subcontractDebugFlag) {
        dprint(".handleDynamicResult->: " + opAndId(paramCorbaMessageMediator));
      }
      CorbaMessageMediator localCorbaMessageMediator = null;
      Any localAny = paramServerRequestImpl.checkResultCalled();
      if (localAny == null)
      {
        if (orb.subcontractDebugFlag) {
          dprint(".handleDynamicResult: " + opAndId(paramCorbaMessageMediator) + ": handling normal result");
        }
        localCorbaMessageMediator = sendingReply(paramCorbaMessageMediator);
        localObject1 = (OutputStream)localCorbaMessageMediator.getOutputObject();
        paramServerRequestImpl.marshalReplyParams((OutputStream)localObject1);
      }
      else
      {
        if (orb.subcontractDebugFlag) {
          dprint(".handleDynamicResult: " + opAndId(paramCorbaMessageMediator) + ": handling error");
        }
        localCorbaMessageMediator = sendingReply(paramCorbaMessageMediator, localAny);
      }
      Object localObject1 = localCorbaMessageMediator;
      return (CorbaMessageMediator)localObject1;
    }
    finally
    {
      if (orb.subcontractDebugFlag) {
        dprint(".handleDynamicResult<-: " + opAndId(paramCorbaMessageMediator));
      }
    }
  }
  
  protected CorbaMessageMediator sendingReply(CorbaMessageMediator paramCorbaMessageMediator)
  {
    try
    {
      if (orb.subcontractDebugFlag) {
        dprint(".sendingReply->: " + opAndId(paramCorbaMessageMediator));
      }
      ServiceContexts localServiceContexts = new ServiceContexts(orb);
      CorbaMessageMediator localCorbaMessageMediator = paramCorbaMessageMediator.getProtocolHandler().createResponse(paramCorbaMessageMediator, localServiceContexts);
      return localCorbaMessageMediator;
    }
    finally
    {
      if (orb.subcontractDebugFlag) {
        dprint(".sendingReply<-: " + opAndId(paramCorbaMessageMediator));
      }
    }
  }
  
  protected CorbaMessageMediator sendingReply(CorbaMessageMediator paramCorbaMessageMediator, Any paramAny)
  {
    try
    {
      if (orb.subcontractDebugFlag) {
        dprint(".sendingReply/Any->: " + opAndId(paramCorbaMessageMediator));
      }
      ServiceContexts localServiceContexts = new ServiceContexts(orb);
      String str = null;
      try
      {
        str = paramAny.type().id();
      }
      catch (BadKind localBadKind)
      {
        throw wrapper.problemWithExceptionTypecode(localBadKind);
      }
      CorbaMessageMediator localCorbaMessageMediator;
      if (ORBUtility.isSystemException(str))
      {
        if (orb.subcontractDebugFlag) {
          dprint(".sendingReply/Any: " + opAndId(paramCorbaMessageMediator) + ": handling system exception");
        }
        localObject1 = paramAny.create_input_stream();
        SystemException localSystemException = ORBUtility.readSystemException((InputStream)localObject1);
        localCorbaMessageMediator = paramCorbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(paramCorbaMessageMediator, localSystemException, localServiceContexts);
      }
      else
      {
        if (orb.subcontractDebugFlag) {
          dprint(".sendingReply/Any: " + opAndId(paramCorbaMessageMediator) + ": handling user exception");
        }
        localCorbaMessageMediator = paramCorbaMessageMediator.getProtocolHandler().createUserExceptionResponse(paramCorbaMessageMediator, localServiceContexts);
        localObject1 = (OutputStream)localCorbaMessageMediator.getOutputObject();
        paramAny.write_value((OutputStream)localObject1);
      }
      Object localObject1 = localCorbaMessageMediator;
      return (CorbaMessageMediator)localObject1;
    }
    finally
    {
      if (orb.subcontractDebugFlag) {
        dprint(".sendingReply/Any<-: " + opAndId(paramCorbaMessageMediator));
      }
    }
  }
  
  protected boolean processCodeSetContext(CorbaMessageMediator paramCorbaMessageMediator, ServiceContexts paramServiceContexts)
  {
    try
    {
      if (orb.subcontractDebugFlag) {
        dprint(".processCodeSetContext->: " + opAndId(paramCorbaMessageMediator));
      }
      ServiceContext localServiceContext = paramServiceContexts.get(1);
      if (localServiceContext != null)
      {
        boolean bool1;
        if (paramCorbaMessageMediator.getConnection() == null)
        {
          bool1 = true;
          return bool1;
        }
        if (paramCorbaMessageMediator.getGIOPVersion().equals(GIOPVersion.V1_0))
        {
          bool1 = true;
          return bool1;
        }
        CodeSetServiceContext localCodeSetServiceContext = (CodeSetServiceContext)localServiceContext;
        CodeSetComponentInfo.CodeSetContext localCodeSetContext = localCodeSetServiceContext.getCodeSetContext();
        if (((CorbaConnection)paramCorbaMessageMediator.getConnection()).getCodeSetContext() == null)
        {
          if (orb.subcontractDebugFlag) {
            dprint(".processCodeSetContext: " + opAndId(paramCorbaMessageMediator) + ": Setting code sets to: " + localCodeSetContext);
          }
          ((CorbaConnection)paramCorbaMessageMediator.getConnection()).setCodeSetContext(localCodeSetContext);
          if (localCodeSetContext.getCharCodeSet() != OSFCodeSetRegistry.ISO_8859_1.getNumber()) {
            ((MarshalInputStream)paramCorbaMessageMediator.getInputObject()).resetCodeSetConverters();
          }
        }
      }
      boolean bool2 = localServiceContext != null;
      return bool2;
    }
    finally
    {
      if (orb.subcontractDebugFlag) {
        dprint(".processCodeSetContext<-: " + opAndId(paramCorbaMessageMediator));
      }
    }
  }
  
  protected void dprint(String paramString)
  {
    ORBUtility.dprint("CorbaServerRequestDispatcherImpl", paramString);
  }
  
  protected String opAndId(CorbaMessageMediator paramCorbaMessageMediator)
  {
    return ORBUtility.operationNameAndRequestId(paramCorbaMessageMediator);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\CorbaServerRequestDispatcherImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
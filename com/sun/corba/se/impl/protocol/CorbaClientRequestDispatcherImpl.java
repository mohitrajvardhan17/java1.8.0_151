package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo.CodeSetContext;
import com.sun.corba.se.impl.encoding.CodeSetConversion;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.pept.transport.TransportManager;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.CodeSetsComponent;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.servicecontext.CodeSetServiceContext;
import com.sun.corba.se.spi.servicecontext.MaxStreamFormatVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.ORBVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.SendingContextServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
import com.sun.corba.se.spi.servicecontext.UnknownServiceContext;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoListIterator;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.UnknownException;
import sun.corba.EncapsInputStreamFactory;

public class CorbaClientRequestDispatcherImpl
  implements ClientRequestDispatcher
{
  private ConcurrentMap<ContactInfo, Object> locks = new ConcurrentHashMap();
  
  public CorbaClientRequestDispatcherImpl() {}
  
  public OutputObject beginRequest(Object paramObject, String paramString, boolean paramBoolean, ContactInfo paramContactInfo)
  {
    ORB localORB = null;
    try
    {
      CorbaContactInfo localCorbaContactInfo = (CorbaContactInfo)paramContactInfo;
      localORB = (ORB)paramContactInfo.getBroker();
      if (subcontractDebugFlag) {
        dprint(".beginRequest->: op/" + paramString);
      }
      localORB.getPIHandler().initiateClientPIRequest(false);
      CorbaConnection localCorbaConnection = null;
      Object localObject1 = locks.get(paramContactInfo);
      if (localObject1 == null)
      {
        Object localObject2 = new Object();
        localObject1 = locks.putIfAbsent(paramContactInfo, localObject2);
        if (localObject1 == null) {
          localObject1 = localObject2;
        }
      }
      Object localObject5;
      synchronized (localObject1)
      {
        if (paramContactInfo.isConnectionBased())
        {
          if (paramContactInfo.shouldCacheConnection()) {
            localCorbaConnection = (CorbaConnection)localORB.getTransportManager().getOutboundConnectionCache(paramContactInfo).get(paramContactInfo);
          }
          if (localCorbaConnection != null)
          {
            if (subcontractDebugFlag) {
              dprint(".beginRequest: op/" + paramString + ": Using cached connection: " + localCorbaConnection);
            }
          }
          else
          {
            try
            {
              localCorbaConnection = (CorbaConnection)paramContactInfo.createConnection();
              if (subcontractDebugFlag) {
                dprint(".beginRequest: op/" + paramString + ": Using created connection: " + localCorbaConnection);
              }
            }
            catch (RuntimeException localRuntimeException)
            {
              if (subcontractDebugFlag) {
                dprint(".beginRequest: op/" + paramString + ": failed to create connection: " + localRuntimeException);
              }
              boolean bool = getContactInfoListIterator(localORB).reportException(paramContactInfo, localRuntimeException);
              if (bool)
              {
                if (getContactInfoListIterator(localORB).hasNext())
                {
                  paramContactInfo = (ContactInfo)getContactInfoListIterator(localORB).next();
                  unregisterWaiter(localORB);
                  localObject5 = beginRequest(paramObject, paramString, paramBoolean, paramContactInfo);
                  return (OutputObject)localObject5;
                }
                throw localRuntimeException;
              }
              throw localRuntimeException;
            }
            if (localCorbaConnection.shouldRegisterReadEvent())
            {
              localORB.getTransportManager().getSelector(0).registerForEvent(localCorbaConnection.getEventHandler());
              localCorbaConnection.setState("ESTABLISHED");
            }
            if (paramContactInfo.shouldCacheConnection())
            {
              localObject3 = localORB.getTransportManager().getOutboundConnectionCache(paramContactInfo);
              ((OutboundConnectionCache)localObject3).stampTime(localCorbaConnection);
              ((OutboundConnectionCache)localObject3).put(paramContactInfo, localCorbaConnection);
            }
          }
        }
      }
      ??? = (CorbaMessageMediator)paramContactInfo.createMessageMediator(localORB, paramContactInfo, localCorbaConnection, paramString, paramBoolean);
      if (subcontractDebugFlag) {
        dprint(".beginRequest: " + opAndId((CorbaMessageMediator)???) + ": created message mediator: " + ???);
      }
      localORB.getInvocationInfo().setMessageMediator((MessageMediator)???);
      if ((localCorbaConnection != null) && (localCorbaConnection.getCodeSetContext() == null)) {
        performCodeSetNegotiation((CorbaMessageMediator)???);
      }
      addServiceContexts((CorbaMessageMediator)???);
      Object localObject3 = paramContactInfo.createOutputObject((MessageMediator)???);
      if (subcontractDebugFlag) {
        dprint(".beginRequest: " + opAndId((CorbaMessageMediator)???) + ": created output object: " + localObject3);
      }
      registerWaiter((CorbaMessageMediator)???);
      synchronized (localObject1)
      {
        if ((paramContactInfo.isConnectionBased()) && (paramContactInfo.shouldCacheConnection()))
        {
          localObject5 = localORB.getTransportManager().getOutboundConnectionCache(paramContactInfo);
          ((OutboundConnectionCache)localObject5).reclaim();
        }
      }
      localORB.getPIHandler().setClientPIInfo((CorbaMessageMediator)???);
      try
      {
        localORB.getPIHandler().invokeClientPIStartingPoint();
      }
      catch (RemarshalException localRemarshalException)
      {
        if (subcontractDebugFlag) {
          dprint(".beginRequest: " + opAndId((CorbaMessageMediator)???) + ": Remarshal");
        }
        if (getContactInfoListIterator(localORB).hasNext())
        {
          paramContactInfo = (ContactInfo)getContactInfoListIterator(localORB).next();
          if (subcontractDebugFlag) {
            dprint("RemarshalException: hasNext true\ncontact info " + paramContactInfo);
          }
          localORB.getPIHandler().makeCompletedClientRequest(3, null);
          unregisterWaiter(localORB);
          localORB.getPIHandler().cleanupClientPIRequest();
          localObject5 = beginRequest(paramObject, paramString, paramBoolean, paramContactInfo);
          return (OutputObject)localObject5;
        }
        if (subcontractDebugFlag) {
          dprint("RemarshalException: hasNext false");
        }
        localObject5 = ORBUtilSystemException.get(localORB, "rpc.protocol");
        throw ((ORBUtilSystemException)localObject5).remarshalWithNowhereToGo();
      }
      ((CorbaMessageMediator)???).initializeMessage();
      if (subcontractDebugFlag) {
        dprint(".beginRequest: " + opAndId((CorbaMessageMediator)???) + ": initialized message");
      }
      Object localObject4 = localObject3;
      return (OutputObject)localObject4;
    }
    finally
    {
      if (subcontractDebugFlag) {
        dprint(".beginRequest<-: op/" + paramString);
      }
    }
  }
  
  public InputObject marshalingComplete(Object paramObject, OutputObject paramOutputObject)
    throws ApplicationException, RemarshalException
  {
    ORB localORB = null;
    CorbaMessageMediator localCorbaMessageMediator = null;
    try
    {
      localCorbaMessageMediator = (CorbaMessageMediator)paramOutputObject.getMessageMediator();
      localORB = (ORB)localCorbaMessageMediator.getBroker();
      if (subcontractDebugFlag) {
        dprint(".marshalingComplete->: " + opAndId(localCorbaMessageMediator));
      }
      InputObject localInputObject1 = marshalingComplete1(localORB, localCorbaMessageMediator);
      InputObject localInputObject2 = processResponse(localORB, localCorbaMessageMediator, localInputObject1);
      return localInputObject2;
    }
    finally
    {
      if (subcontractDebugFlag) {
        dprint(".marshalingComplete<-: " + opAndId(localCorbaMessageMediator));
      }
    }
  }
  
  public InputObject marshalingComplete1(ORB paramORB, CorbaMessageMediator paramCorbaMessageMediator)
    throws ApplicationException, RemarshalException
  {
    try
    {
      paramCorbaMessageMediator.finishSendingRequest();
      if (subcontractDebugFlag) {
        dprint(".marshalingComplete: " + opAndId(paramCorbaMessageMediator) + ": finished sending request");
      }
      return paramCorbaMessageMediator.waitForResponse();
    }
    catch (RuntimeException localRuntimeException)
    {
      if (subcontractDebugFlag) {
        dprint(".marshalingComplete: " + opAndId(paramCorbaMessageMediator) + ": exception: " + localRuntimeException.toString());
      }
      boolean bool = getContactInfoListIterator(paramORB).reportException(paramCorbaMessageMediator.getContactInfo(), localRuntimeException);
      Exception localException = paramORB.getPIHandler().invokeClientPIEndingPoint(2, localRuntimeException);
      if (bool)
      {
        if (localException == localRuntimeException) {
          continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, new RemarshalException());
        } else {
          continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, localException);
        }
      }
      else
      {
        if ((localException instanceof RuntimeException)) {
          throw ((RuntimeException)localException);
        }
        if ((localException instanceof RemarshalException)) {
          throw ((RemarshalException)localException);
        }
        throw localRuntimeException;
      }
    }
    return null;
  }
  
  protected InputObject processResponse(ORB paramORB, CorbaMessageMediator paramCorbaMessageMediator, InputObject paramInputObject)
    throws ApplicationException, RemarshalException
  {
    ORBUtilSystemException localORBUtilSystemException = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    if (subcontractDebugFlag) {
      dprint(".processResponse: " + opAndId(paramCorbaMessageMediator) + ": response received");
    }
    if (paramCorbaMessageMediator.getConnection() != null) {
      ((CorbaConnection)paramCorbaMessageMediator.getConnection()).setPostInitialContexts();
    }
    Object localObject1 = null;
    if (paramCorbaMessageMediator.isOneWay())
    {
      getContactInfoListIterator(paramORB).reportSuccess(paramCorbaMessageMediator.getContactInfo());
      localObject1 = paramORB.getPIHandler().invokeClientPIEndingPoint(0, (Exception)localObject1);
      continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, (Exception)localObject1);
      return null;
    }
    consumeServiceContexts(paramORB, paramCorbaMessageMediator);
    ((CDRInputObject)paramInputObject).performORBVersionSpecificInit();
    Object localObject2;
    Object localObject3;
    if (paramCorbaMessageMediator.isSystemExceptionReply())
    {
      localObject2 = paramCorbaMessageMediator.getSystemExceptionReply();
      if (subcontractDebugFlag) {
        dprint(".processResponse: " + opAndId(paramCorbaMessageMediator) + ": received system exception: " + localObject2);
      }
      boolean bool = getContactInfoListIterator(paramORB).reportException(paramCorbaMessageMediator.getContactInfo(), (RuntimeException)localObject2);
      if (bool)
      {
        localObject1 = paramORB.getPIHandler().invokeClientPIEndingPoint(2, (Exception)localObject2);
        if (localObject2 == localObject1)
        {
          localObject1 = null;
          continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, new RemarshalException());
          throw localORBUtilSystemException.statementNotReachable1();
        }
        continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, (Exception)localObject1);
        throw localORBUtilSystemException.statementNotReachable2();
      }
      localObject3 = paramCorbaMessageMediator.getReplyServiceContexts();
      if (localObject3 != null)
      {
        UEInfoServiceContext localUEInfoServiceContext = (UEInfoServiceContext)((ServiceContexts)localObject3).get(9);
        if (localUEInfoServiceContext != null)
        {
          Throwable localThrowable = localUEInfoServiceContext.getUE();
          UnknownException localUnknownException = new UnknownException(localThrowable);
          localObject1 = paramORB.getPIHandler().invokeClientPIEndingPoint(2, localUnknownException);
          continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, (Exception)localObject1);
          throw localORBUtilSystemException.statementNotReachable3();
        }
      }
      localObject1 = paramORB.getPIHandler().invokeClientPIEndingPoint(2, (Exception)localObject2);
      continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, (Exception)localObject1);
      throw localORBUtilSystemException.statementNotReachable4();
    }
    if (paramCorbaMessageMediator.isUserExceptionReply())
    {
      if (subcontractDebugFlag) {
        dprint(".processResponse: " + opAndId(paramCorbaMessageMediator) + ": received user exception");
      }
      getContactInfoListIterator(paramORB).reportSuccess(paramCorbaMessageMediator.getContactInfo());
      localObject2 = peekUserExceptionId(paramInputObject);
      Exception localException = null;
      if (paramCorbaMessageMediator.isDIIRequest())
      {
        localObject1 = paramCorbaMessageMediator.unmarshalDIIUserException((String)localObject2, (org.omg.CORBA_2_3.portable.InputStream)paramInputObject);
        localException = paramORB.getPIHandler().invokeClientPIEndingPoint(1, (Exception)localObject1);
        paramCorbaMessageMediator.setDIIException(localException);
      }
      else
      {
        localObject3 = new ApplicationException((String)localObject2, (org.omg.CORBA.portable.InputStream)paramInputObject);
        localObject1 = localObject3;
        localException = paramORB.getPIHandler().invokeClientPIEndingPoint(1, (Exception)localObject3);
      }
      if (localException != localObject1) {
        continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, localException);
      }
      if ((localException instanceof ApplicationException)) {
        throw ((ApplicationException)localException);
      }
      return paramInputObject;
    }
    if (paramCorbaMessageMediator.isLocationForwardReply())
    {
      if (subcontractDebugFlag) {
        dprint(".processResponse: " + opAndId(paramCorbaMessageMediator) + ": received location forward");
      }
      getContactInfoListIterator(paramORB).reportRedirect((CorbaContactInfo)paramCorbaMessageMediator.getContactInfo(), paramCorbaMessageMediator.getForwardedIOR());
      localObject2 = paramORB.getPIHandler().invokeClientPIEndingPoint(3, null);
      if (!(localObject2 instanceof RemarshalException)) {
        localObject1 = localObject2;
      }
      if (localObject1 != null) {
        continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, (Exception)localObject1);
      }
      continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, new RemarshalException());
      throw localORBUtilSystemException.statementNotReachable5();
    }
    if (paramCorbaMessageMediator.isDifferentAddrDispositionRequestedReply())
    {
      if (subcontractDebugFlag) {
        dprint(".processResponse: " + opAndId(paramCorbaMessageMediator) + ": received different addressing dispostion request");
      }
      getContactInfoListIterator(paramORB).reportAddrDispositionRetry((CorbaContactInfo)paramCorbaMessageMediator.getContactInfo(), paramCorbaMessageMediator.getAddrDispositionReply());
      localObject2 = paramORB.getPIHandler().invokeClientPIEndingPoint(5, null);
      if (!(localObject2 instanceof RemarshalException)) {
        localObject1 = localObject2;
      }
      if (localObject1 != null) {
        continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, (Exception)localObject1);
      }
      continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, new RemarshalException());
      throw localORBUtilSystemException.statementNotReachable6();
    }
    if (subcontractDebugFlag) {
      dprint(".processResponse: " + opAndId(paramCorbaMessageMediator) + ": received normal response");
    }
    getContactInfoListIterator(paramORB).reportSuccess(paramCorbaMessageMediator.getContactInfo());
    paramCorbaMessageMediator.handleDIIReply((org.omg.CORBA_2_3.portable.InputStream)paramInputObject);
    localObject1 = paramORB.getPIHandler().invokeClientPIEndingPoint(0, null);
    continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, (Exception)localObject1);
    return paramInputObject;
  }
  
  protected void continueOrThrowSystemOrRemarshal(CorbaMessageMediator paramCorbaMessageMediator, Exception paramException)
    throws SystemException, RemarshalException
  {
    ORB localORB = (ORB)paramCorbaMessageMediator.getBroker();
    if (paramException != null)
    {
      if ((paramException instanceof RemarshalException))
      {
        localORB.getInvocationInfo().setIsRetryInvocation(true);
        unregisterWaiter(localORB);
        if (subcontractDebugFlag) {
          dprint(".continueOrThrowSystemOrRemarshal: " + opAndId(paramCorbaMessageMediator) + ": throwing Remarshal");
        }
        throw ((RemarshalException)paramException);
      }
      if (subcontractDebugFlag) {
        dprint(".continueOrThrowSystemOrRemarshal: " + opAndId(paramCorbaMessageMediator) + ": throwing sex:" + paramException);
      }
      throw ((SystemException)paramException);
    }
  }
  
  protected CorbaContactInfoListIterator getContactInfoListIterator(ORB paramORB)
  {
    return (CorbaContactInfoListIterator)((CorbaInvocationInfo)paramORB.getInvocationInfo()).getContactInfoListIterator();
  }
  
  protected void registerWaiter(CorbaMessageMediator paramCorbaMessageMediator)
  {
    if (paramCorbaMessageMediator.getConnection() != null) {
      paramCorbaMessageMediator.getConnection().registerWaiter(paramCorbaMessageMediator);
    }
  }
  
  protected void unregisterWaiter(ORB paramORB)
  {
    MessageMediator localMessageMediator = paramORB.getInvocationInfo().getMessageMediator();
    if ((localMessageMediator != null) && (localMessageMediator.getConnection() != null)) {
      localMessageMediator.getConnection().unregisterWaiter(localMessageMediator);
    }
  }
  
  protected void addServiceContexts(CorbaMessageMediator paramCorbaMessageMediator)
  {
    ORB localORB = (ORB)paramCorbaMessageMediator.getBroker();
    CorbaConnection localCorbaConnection = (CorbaConnection)paramCorbaMessageMediator.getConnection();
    GIOPVersion localGIOPVersion = paramCorbaMessageMediator.getGIOPVersion();
    ServiceContexts localServiceContexts = paramCorbaMessageMediator.getRequestServiceContexts();
    addCodeSetServiceContext(localCorbaConnection, localServiceContexts, localGIOPVersion);
    localServiceContexts.put(MaxStreamFormatVersionServiceContext.singleton);
    ORBVersionServiceContext localORBVersionServiceContext = new ORBVersionServiceContext(ORBVersionFactory.getORBVersion());
    localServiceContexts.put(localORBVersionServiceContext);
    if ((localCorbaConnection != null) && (!localCorbaConnection.isPostInitialContexts()))
    {
      SendingContextServiceContext localSendingContextServiceContext = new SendingContextServiceContext(localORB.getFVDCodeBaseIOR());
      localServiceContexts.put(localSendingContextServiceContext);
    }
  }
  
  protected void consumeServiceContexts(ORB paramORB, CorbaMessageMediator paramCorbaMessageMediator)
  {
    ServiceContexts localServiceContexts = paramCorbaMessageMediator.getReplyServiceContexts();
    ORBUtilSystemException localORBUtilSystemException = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    if (localServiceContexts == null) {
      return;
    }
    ServiceContext localServiceContext = localServiceContexts.get(6);
    Object localObject1;
    Object localObject2;
    if (localServiceContext != null)
    {
      localObject1 = (SendingContextServiceContext)localServiceContext;
      localObject2 = ((SendingContextServiceContext)localObject1).getIOR();
      try
      {
        if (paramCorbaMessageMediator.getConnection() != null) {
          ((CorbaConnection)paramCorbaMessageMediator.getConnection()).setCodeBaseIOR((IOR)localObject2);
        }
      }
      catch (ThreadDeath localThreadDeath)
      {
        throw localThreadDeath;
      }
      catch (Throwable localThrowable)
      {
        throw localORBUtilSystemException.badStringifiedIor(localThrowable);
      }
    }
    localServiceContext = localServiceContexts.get(1313165056);
    if (localServiceContext != null)
    {
      localObject1 = (ORBVersionServiceContext)localServiceContext;
      localObject2 = ((ORBVersionServiceContext)localObject1).getVersion();
      paramORB.setORBVersion((ORBVersion)localObject2);
    }
    getExceptionDetailMessage(paramCorbaMessageMediator, localORBUtilSystemException);
  }
  
  protected void getExceptionDetailMessage(CorbaMessageMediator paramCorbaMessageMediator, ORBUtilSystemException paramORBUtilSystemException)
  {
    ServiceContext localServiceContext = paramCorbaMessageMediator.getReplyServiceContexts().get(14);
    if (localServiceContext == null) {
      return;
    }
    if (!(localServiceContext instanceof UnknownServiceContext)) {
      throw paramORBUtilSystemException.badExceptionDetailMessageServiceContextType();
    }
    byte[] arrayOfByte = ((UnknownServiceContext)localServiceContext).getData();
    EncapsInputStream localEncapsInputStream = EncapsInputStreamFactory.newEncapsInputStream((ORB)paramCorbaMessageMediator.getBroker(), arrayOfByte, arrayOfByte.length);
    localEncapsInputStream.consumeEndian();
    String str = "----------BEGIN server-side stack trace----------\n" + localEncapsInputStream.read_wstring() + "\n----------END server-side stack trace----------";
    paramCorbaMessageMediator.setReplyExceptionDetailMessage(str);
  }
  
  public void endRequest(Broker paramBroker, Object paramObject, InputObject paramInputObject)
  {
    ORB localORB = (ORB)paramBroker;
    try
    {
      if (subcontractDebugFlag) {
        dprint(".endRequest->");
      }
      MessageMediator localMessageMediator = localORB.getInvocationInfo().getMessageMediator();
      if (localMessageMediator != null)
      {
        if (localMessageMediator.getConnection() != null) {
          ((CorbaMessageMediator)localMessageMediator).sendCancelRequestIfFinalFragmentNotSent();
        }
        InputObject localInputObject = localMessageMediator.getInputObject();
        if (localInputObject != null) {
          localInputObject.close();
        }
        OutputObject localOutputObject = localMessageMediator.getOutputObject();
        if (localOutputObject != null) {
          localOutputObject.close();
        }
      }
      unregisterWaiter(localORB);
      localORB.getPIHandler().cleanupClientPIRequest();
    }
    catch (IOException localIOException)
    {
      if (subcontractDebugFlag) {
        dprint(".endRequest: ignoring IOException - " + localIOException.toString());
      }
    }
    finally
    {
      if (subcontractDebugFlag) {
        dprint(".endRequest<-");
      }
    }
  }
  
  protected void performCodeSetNegotiation(CorbaMessageMediator paramCorbaMessageMediator)
  {
    CorbaConnection localCorbaConnection = (CorbaConnection)paramCorbaMessageMediator.getConnection();
    IOR localIOR = ((CorbaContactInfo)paramCorbaMessageMediator.getContactInfo()).getEffectiveTargetIOR();
    GIOPVersion localGIOPVersion = paramCorbaMessageMediator.getGIOPVersion();
    if ((localCorbaConnection != null) && (localCorbaConnection.getCodeSetContext() == null) && (!localGIOPVersion.equals(GIOPVersion.V1_0))) {
      synchronized (localCorbaConnection)
      {
        if (localCorbaConnection.getCodeSetContext() != null) {
          return;
        }
        IIOPProfileTemplate localIIOPProfileTemplate = (IIOPProfileTemplate)localIOR.getProfile().getTaggedProfileTemplate();
        Iterator localIterator = localIIOPProfileTemplate.iteratorById(1);
        if (!localIterator.hasNext()) {
          return;
        }
        CodeSetComponentInfo localCodeSetComponentInfo = ((CodeSetsComponent)localIterator.next()).getCodeSetComponentInfo();
        CodeSetComponentInfo.CodeSetContext localCodeSetContext = CodeSetConversion.impl().negotiate(localCorbaConnection.getBroker().getORBData().getCodeSetComponentInfo(), localCodeSetComponentInfo);
        localCorbaConnection.setCodeSetContext(localCodeSetContext);
      }
    }
  }
  
  protected void addCodeSetServiceContext(CorbaConnection paramCorbaConnection, ServiceContexts paramServiceContexts, GIOPVersion paramGIOPVersion)
  {
    if ((paramGIOPVersion.equals(GIOPVersion.V1_0)) || (paramCorbaConnection == null)) {
      return;
    }
    CodeSetComponentInfo.CodeSetContext localCodeSetContext = null;
    if ((paramCorbaConnection.getBroker().getORBData().alwaysSendCodeSetServiceContext()) || (!paramCorbaConnection.isPostInitialContexts())) {
      localCodeSetContext = paramCorbaConnection.getCodeSetContext();
    }
    if (localCodeSetContext == null) {
      return;
    }
    CodeSetServiceContext localCodeSetServiceContext = new CodeSetServiceContext(localCodeSetContext);
    paramServiceContexts.put(localCodeSetServiceContext);
  }
  
  protected String peekUserExceptionId(InputObject paramInputObject)
  {
    CDRInputObject localCDRInputObject = (CDRInputObject)paramInputObject;
    localCDRInputObject.mark(Integer.MAX_VALUE);
    String str = localCDRInputObject.read_string();
    localCDRInputObject.reset();
    return str;
  }
  
  protected void dprint(String paramString)
  {
    ORBUtility.dprint("CorbaClientRequestDispatcherImpl", paramString);
  }
  
  protected String opAndId(CorbaMessageMediator paramCorbaMessageMediator)
  {
    return ORBUtility.operationNameAndRequestId(paramCorbaMessageMediator);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\CorbaClientRequestDispatcherImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
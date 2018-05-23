package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.org.glassfish.gmbal.AMXClient;
import com.sun.org.glassfish.gmbal.GmbalMBean;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.org.glassfish.gmbal.ManagedObjectManager.RegistrationDebugLevel;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ServiceDefinition;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSEndpoint.CompletionCallback;
import com.sun.xml.internal.ws.api.server.WSEndpoint.PipeHead;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.xml.namespace.QName;
import javax.xml.ws.EndpointReference;
import org.w3c.dom.Element;

public class WSEndpointMOMProxy
  extends WSEndpoint
  implements ManagedObjectManager
{
  @NotNull
  private final WSEndpointImpl wsEndpoint;
  private ManagedObjectManager managedObjectManager;
  
  WSEndpointMOMProxy(@NotNull WSEndpointImpl paramWSEndpointImpl)
  {
    wsEndpoint = paramWSEndpointImpl;
  }
  
  public ManagedObjectManager getManagedObjectManager()
  {
    if (managedObjectManager == null) {
      managedObjectManager = wsEndpoint.obtainManagedObjectManager();
    }
    return managedObjectManager;
  }
  
  void setManagedObjectManager(ManagedObjectManager paramManagedObjectManager)
  {
    managedObjectManager = paramManagedObjectManager;
  }
  
  public boolean isInitialized()
  {
    return managedObjectManager != null;
  }
  
  public WSEndpointImpl getWsEndpoint()
  {
    return wsEndpoint;
  }
  
  public void suspendJMXRegistration()
  {
    getManagedObjectManager().suspendJMXRegistration();
  }
  
  public void resumeJMXRegistration()
  {
    getManagedObjectManager().resumeJMXRegistration();
  }
  
  public boolean isManagedObject(Object paramObject)
  {
    return getManagedObjectManager().isManagedObject(paramObject);
  }
  
  public GmbalMBean createRoot()
  {
    return getManagedObjectManager().createRoot();
  }
  
  public GmbalMBean createRoot(Object paramObject)
  {
    return getManagedObjectManager().createRoot(paramObject);
  }
  
  public GmbalMBean createRoot(Object paramObject, String paramString)
  {
    return getManagedObjectManager().createRoot(paramObject, paramString);
  }
  
  public Object getRoot()
  {
    return getManagedObjectManager().getRoot();
  }
  
  public GmbalMBean register(Object paramObject1, Object paramObject2, String paramString)
  {
    return getManagedObjectManager().register(paramObject1, paramObject2, paramString);
  }
  
  public GmbalMBean register(Object paramObject1, Object paramObject2)
  {
    return getManagedObjectManager().register(paramObject1, paramObject2);
  }
  
  public GmbalMBean registerAtRoot(Object paramObject, String paramString)
  {
    return getManagedObjectManager().registerAtRoot(paramObject, paramString);
  }
  
  public GmbalMBean registerAtRoot(Object paramObject)
  {
    return getManagedObjectManager().registerAtRoot(paramObject);
  }
  
  public void unregister(Object paramObject)
  {
    getManagedObjectManager().unregister(paramObject);
  }
  
  public ObjectName getObjectName(Object paramObject)
  {
    return getManagedObjectManager().getObjectName(paramObject);
  }
  
  public AMXClient getAMXClient(Object paramObject)
  {
    return getManagedObjectManager().getAMXClient(paramObject);
  }
  
  public Object getObject(ObjectName paramObjectName)
  {
    return getManagedObjectManager().getObject(paramObjectName);
  }
  
  public void stripPrefix(String... paramVarArgs)
  {
    getManagedObjectManager().stripPrefix(paramVarArgs);
  }
  
  public void stripPackagePrefix()
  {
    getManagedObjectManager().stripPackagePrefix();
  }
  
  public String getDomain()
  {
    return getManagedObjectManager().getDomain();
  }
  
  public void setMBeanServer(MBeanServer paramMBeanServer)
  {
    getManagedObjectManager().setMBeanServer(paramMBeanServer);
  }
  
  public MBeanServer getMBeanServer()
  {
    return getManagedObjectManager().getMBeanServer();
  }
  
  public void setResourceBundle(ResourceBundle paramResourceBundle)
  {
    getManagedObjectManager().setResourceBundle(paramResourceBundle);
  }
  
  public ResourceBundle getResourceBundle()
  {
    return getManagedObjectManager().getResourceBundle();
  }
  
  public void addAnnotation(AnnotatedElement paramAnnotatedElement, Annotation paramAnnotation)
  {
    getManagedObjectManager().addAnnotation(paramAnnotatedElement, paramAnnotation);
  }
  
  public void setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel paramRegistrationDebugLevel)
  {
    getManagedObjectManager().setRegistrationDebug(paramRegistrationDebugLevel);
  }
  
  public void setRuntimeDebug(boolean paramBoolean)
  {
    getManagedObjectManager().setRuntimeDebug(paramBoolean);
  }
  
  public void setTypelibDebug(int paramInt)
  {
    getManagedObjectManager().setTypelibDebug(paramInt);
  }
  
  public void setJMXRegistrationDebug(boolean paramBoolean)
  {
    getManagedObjectManager().setJMXRegistrationDebug(paramBoolean);
  }
  
  public String dumpSkeleton(Object paramObject)
  {
    return getManagedObjectManager().dumpSkeleton(paramObject);
  }
  
  public void suppressDuplicateRootReport(boolean paramBoolean)
  {
    getManagedObjectManager().suppressDuplicateRootReport(paramBoolean);
  }
  
  public void close()
    throws IOException
  {
    getManagedObjectManager().close();
  }
  
  public boolean equalsProxiedInstance(WSEndpoint paramWSEndpoint)
  {
    if (wsEndpoint == null) {
      return paramWSEndpoint == null;
    }
    return wsEndpoint.equals(paramWSEndpoint);
  }
  
  public Codec createCodec()
  {
    return wsEndpoint.createCodec();
  }
  
  public QName getServiceName()
  {
    return wsEndpoint.getServiceName();
  }
  
  public QName getPortName()
  {
    return wsEndpoint.getPortName();
  }
  
  public Class getImplementationClass()
  {
    return wsEndpoint.getImplementationClass();
  }
  
  public WSBinding getBinding()
  {
    return wsEndpoint.getBinding();
  }
  
  public Container getContainer()
  {
    return wsEndpoint.getContainer();
  }
  
  public WSDLPort getPort()
  {
    return wsEndpoint.getPort();
  }
  
  public void setExecutor(Executor paramExecutor)
  {
    wsEndpoint.setExecutor(paramExecutor);
  }
  
  public void schedule(Packet paramPacket, WSEndpoint.CompletionCallback paramCompletionCallback, FiberContextSwitchInterceptor paramFiberContextSwitchInterceptor)
  {
    wsEndpoint.schedule(paramPacket, paramCompletionCallback, paramFiberContextSwitchInterceptor);
  }
  
  public WSEndpoint.PipeHead createPipeHead()
  {
    return wsEndpoint.createPipeHead();
  }
  
  public void dispose()
  {
    if (wsEndpoint != null) {
      wsEndpoint.dispose();
    }
  }
  
  public ServiceDefinition getServiceDefinition()
  {
    return wsEndpoint.getServiceDefinition();
  }
  
  public Set getComponentRegistry()
  {
    return wsEndpoint.getComponentRegistry();
  }
  
  public SEIModel getSEIModel()
  {
    return wsEndpoint.getSEIModel();
  }
  
  public PolicyMap getPolicyMap()
  {
    return wsEndpoint.getPolicyMap();
  }
  
  public void closeManagedObjectManager()
  {
    wsEndpoint.closeManagedObjectManager();
  }
  
  public ServerTubeAssemblerContext getAssemblerContext()
  {
    return wsEndpoint.getAssemblerContext();
  }
  
  public EndpointReference getEndpointReference(Class paramClass, String paramString1, String paramString2, Element... paramVarArgs)
  {
    return wsEndpoint.getEndpointReference(paramClass, paramString1, paramString2, paramVarArgs);
  }
  
  public EndpointReference getEndpointReference(Class paramClass, String paramString1, String paramString2, List paramList1, List paramList2)
  {
    return wsEndpoint.getEndpointReference(paramClass, paramString1, paramString2, paramList1, paramList2);
  }
  
  public OperationDispatcher getOperationDispatcher()
  {
    return wsEndpoint.getOperationDispatcher();
  }
  
  public Packet createServiceResponseForException(ThrowableContainerPropertySet paramThrowableContainerPropertySet, Packet paramPacket, SOAPVersion paramSOAPVersion, WSDLPort paramWSDLPort, SEIModel paramSEIModel, WSBinding paramWSBinding)
  {
    return wsEndpoint.createServiceResponseForException(paramThrowableContainerPropertySet, paramPacket, paramSOAPVersion, paramWSDLPort, paramSEIModel, paramWSBinding);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\WSEndpointMOMProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
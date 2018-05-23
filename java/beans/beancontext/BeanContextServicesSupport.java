package java.beans.beancontext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TooManyListenersException;

public class BeanContextServicesSupport
  extends BeanContextSupport
  implements BeanContextServices
{
  private static final long serialVersionUID = -8494482757288719206L;
  protected transient HashMap services;
  protected transient int serializable = 0;
  protected transient BCSSProxyServiceProvider proxy;
  protected transient ArrayList bcsListeners;
  
  public BeanContextServicesSupport(BeanContextServices paramBeanContextServices, Locale paramLocale, boolean paramBoolean1, boolean paramBoolean2)
  {
    super(paramBeanContextServices, paramLocale, paramBoolean1, paramBoolean2);
  }
  
  public BeanContextServicesSupport(BeanContextServices paramBeanContextServices, Locale paramLocale, boolean paramBoolean)
  {
    this(paramBeanContextServices, paramLocale, paramBoolean, true);
  }
  
  public BeanContextServicesSupport(BeanContextServices paramBeanContextServices, Locale paramLocale)
  {
    this(paramBeanContextServices, paramLocale, false, true);
  }
  
  public BeanContextServicesSupport(BeanContextServices paramBeanContextServices)
  {
    this(paramBeanContextServices, null, false, true);
  }
  
  public BeanContextServicesSupport()
  {
    this(null, null, false, true);
  }
  
  public void initialize()
  {
    super.initialize();
    services = new HashMap(serializable + 1);
    bcsListeners = new ArrayList(1);
  }
  
  public BeanContextServices getBeanContextServicesPeer()
  {
    return (BeanContextServices)getBeanContextChildPeer();
  }
  
  protected BeanContextSupport.BCSChild createBCSChild(Object paramObject1, Object paramObject2)
  {
    return new BCSSChild(paramObject1, paramObject2);
  }
  
  protected BCSSServiceProvider createBCSSServiceProvider(Class paramClass, BeanContextServiceProvider paramBeanContextServiceProvider)
  {
    return new BCSSServiceProvider(paramClass, paramBeanContextServiceProvider);
  }
  
  public void addBeanContextServicesListener(BeanContextServicesListener paramBeanContextServicesListener)
  {
    if (paramBeanContextServicesListener == null) {
      throw new NullPointerException("bcsl");
    }
    synchronized (bcsListeners)
    {
      if (bcsListeners.contains(paramBeanContextServicesListener)) {
        return;
      }
      bcsListeners.add(paramBeanContextServicesListener);
    }
  }
  
  public void removeBeanContextServicesListener(BeanContextServicesListener paramBeanContextServicesListener)
  {
    if (paramBeanContextServicesListener == null) {
      throw new NullPointerException("bcsl");
    }
    synchronized (bcsListeners)
    {
      if (!bcsListeners.contains(paramBeanContextServicesListener)) {
        return;
      }
      bcsListeners.remove(paramBeanContextServicesListener);
    }
  }
  
  public boolean addService(Class paramClass, BeanContextServiceProvider paramBeanContextServiceProvider)
  {
    return addService(paramClass, paramBeanContextServiceProvider, true);
  }
  
  protected boolean addService(Class paramClass, BeanContextServiceProvider paramBeanContextServiceProvider, boolean paramBoolean)
  {
    if (paramClass == null) {
      throw new NullPointerException("serviceClass");
    }
    if (paramBeanContextServiceProvider == null) {
      throw new NullPointerException("bcsp");
    }
    synchronized (BeanContext.globalHierarchyLock)
    {
      if (services.containsKey(paramClass)) {
        return false;
      }
      services.put(paramClass, createBCSSServiceProvider(paramClass, paramBeanContextServiceProvider));
      if ((paramBeanContextServiceProvider instanceof Serializable)) {
        serializable += 1;
      }
      if (!paramBoolean) {
        return true;
      }
      BeanContextServiceAvailableEvent localBeanContextServiceAvailableEvent = new BeanContextServiceAvailableEvent(getBeanContextServicesPeer(), paramClass);
      fireServiceAdded(localBeanContextServiceAvailableEvent);
      synchronized (children)
      {
        Iterator localIterator = children.keySet().iterator();
        while (localIterator.hasNext())
        {
          Object localObject1 = localIterator.next();
          if ((localObject1 instanceof BeanContextServices)) {
            ((BeanContextServicesListener)localObject1).serviceAvailable(localBeanContextServiceAvailableEvent);
          }
        }
      }
      return true;
    }
  }
  
  public void revokeService(Class paramClass, BeanContextServiceProvider paramBeanContextServiceProvider, boolean paramBoolean)
  {
    if (paramClass == null) {
      throw new NullPointerException("serviceClass");
    }
    if (paramBeanContextServiceProvider == null) {
      throw new NullPointerException("bcsp");
    }
    synchronized (BeanContext.globalHierarchyLock)
    {
      if (!services.containsKey(paramClass)) {
        return;
      }
      BCSSServiceProvider localBCSSServiceProvider = (BCSSServiceProvider)services.get(paramClass);
      if (!localBCSSServiceProvider.getServiceProvider().equals(paramBeanContextServiceProvider)) {
        throw new IllegalArgumentException("service provider mismatch");
      }
      services.remove(paramClass);
      if ((paramBeanContextServiceProvider instanceof Serializable)) {
        serializable -= 1;
      }
      Iterator localIterator = bcsChildren();
      while (localIterator.hasNext()) {
        ((BCSSChild)localIterator.next()).revokeService(paramClass, false, paramBoolean);
      }
      fireServiceRevoked(paramClass, paramBoolean);
    }
  }
  
  public synchronized boolean hasService(Class paramClass)
  {
    if (paramClass == null) {
      throw new NullPointerException("serviceClass");
    }
    synchronized (BeanContext.globalHierarchyLock)
    {
      if (services.containsKey(paramClass)) {
        return true;
      }
      BeanContextServices localBeanContextServices = null;
      try
      {
        localBeanContextServices = (BeanContextServices)getBeanContext();
      }
      catch (ClassCastException localClassCastException)
      {
        return false;
      }
      return localBeanContextServices == null ? false : localBeanContextServices.hasService(paramClass);
    }
  }
  
  public Object getService(BeanContextChild paramBeanContextChild, Object paramObject1, Class paramClass, Object paramObject2, BeanContextServiceRevokedListener paramBeanContextServiceRevokedListener)
    throws TooManyListenersException
  {
    if (paramBeanContextChild == null) {
      throw new NullPointerException("child");
    }
    if (paramClass == null) {
      throw new NullPointerException("serviceClass");
    }
    if (paramObject1 == null) {
      throw new NullPointerException("requestor");
    }
    if (paramBeanContextServiceRevokedListener == null) {
      throw new NullPointerException("bcsrl");
    }
    Object localObject1 = null;
    BeanContextServices localBeanContextServices = getBeanContextServicesPeer();
    synchronized (BeanContext.globalHierarchyLock)
    {
      BCSSChild localBCSSChild;
      synchronized (children)
      {
        localBCSSChild = (BCSSChild)children.get(paramBeanContextChild);
      }
      if (localBCSSChild == null) {
        throw new IllegalArgumentException("not a child of this context");
      }
      ??? = (BCSSServiceProvider)services.get(paramClass);
      if (??? != null)
      {
        BeanContextServiceProvider localBeanContextServiceProvider = ((BCSSServiceProvider)???).getServiceProvider();
        localObject1 = localBeanContextServiceProvider.getService(localBeanContextServices, paramObject1, paramClass, paramObject2);
        if (localObject1 != null)
        {
          try
          {
            localBCSSChild.usingService(paramObject1, localObject1, paramClass, localBeanContextServiceProvider, false, paramBeanContextServiceRevokedListener);
          }
          catch (TooManyListenersException localTooManyListenersException2)
          {
            localBeanContextServiceProvider.releaseService(localBeanContextServices, paramObject1, localObject1);
            throw localTooManyListenersException2;
          }
          catch (UnsupportedOperationException localUnsupportedOperationException2)
          {
            localBeanContextServiceProvider.releaseService(localBeanContextServices, paramObject1, localObject1);
            throw localUnsupportedOperationException2;
          }
          return localObject1;
        }
      }
      if (proxy != null)
      {
        localObject1 = proxy.getService(localBeanContextServices, paramObject1, paramClass, paramObject2);
        if (localObject1 != null)
        {
          try
          {
            localBCSSChild.usingService(paramObject1, localObject1, paramClass, proxy, true, paramBeanContextServiceRevokedListener);
          }
          catch (TooManyListenersException localTooManyListenersException1)
          {
            proxy.releaseService(localBeanContextServices, paramObject1, localObject1);
            throw localTooManyListenersException1;
          }
          catch (UnsupportedOperationException localUnsupportedOperationException1)
          {
            proxy.releaseService(localBeanContextServices, paramObject1, localObject1);
            throw localUnsupportedOperationException1;
          }
          return localObject1;
        }
      }
    }
    return null;
  }
  
  public void releaseService(BeanContextChild paramBeanContextChild, Object paramObject1, Object paramObject2)
  {
    if (paramBeanContextChild == null) {
      throw new NullPointerException("child");
    }
    if (paramObject1 == null) {
      throw new NullPointerException("requestor");
    }
    if (paramObject2 == null) {
      throw new NullPointerException("service");
    }
    synchronized (BeanContext.globalHierarchyLock)
    {
      BCSSChild localBCSSChild;
      synchronized (children)
      {
        localBCSSChild = (BCSSChild)children.get(paramBeanContextChild);
      }
      if (localBCSSChild != null) {
        localBCSSChild.releaseService(paramObject1, paramObject2);
      } else {
        throw new IllegalArgumentException("child actual is not a child of this BeanContext");
      }
    }
  }
  
  public Iterator getCurrentServiceClasses()
  {
    return new BeanContextSupport.BCSIterator(services.keySet().iterator());
  }
  
  public Iterator getCurrentServiceSelectors(Class paramClass)
  {
    BCSSServiceProvider localBCSSServiceProvider = (BCSSServiceProvider)services.get(paramClass);
    return localBCSSServiceProvider != null ? new BeanContextSupport.BCSIterator(localBCSSServiceProvider.getServiceProvider().getCurrentServiceSelectors(getBeanContextServicesPeer(), paramClass)) : null;
  }
  
  public void serviceAvailable(BeanContextServiceAvailableEvent paramBeanContextServiceAvailableEvent)
  {
    synchronized (BeanContext.globalHierarchyLock)
    {
      if (services.containsKey(paramBeanContextServiceAvailableEvent.getServiceClass())) {
        return;
      }
      fireServiceAdded(paramBeanContextServiceAvailableEvent);
      Iterator localIterator;
      synchronized (children)
      {
        localIterator = children.keySet().iterator();
      }
      while (localIterator.hasNext())
      {
        ??? = localIterator.next();
        if ((??? instanceof BeanContextServices)) {
          ((BeanContextServicesListener)???).serviceAvailable(paramBeanContextServiceAvailableEvent);
        }
      }
    }
  }
  
  public void serviceRevoked(BeanContextServiceRevokedEvent paramBeanContextServiceRevokedEvent)
  {
    synchronized (BeanContext.globalHierarchyLock)
    {
      if (services.containsKey(paramBeanContextServiceRevokedEvent.getServiceClass())) {
        return;
      }
      fireServiceRevoked(paramBeanContextServiceRevokedEvent);
      Iterator localIterator;
      synchronized (children)
      {
        localIterator = children.keySet().iterator();
      }
      while (localIterator.hasNext())
      {
        ??? = localIterator.next();
        if ((??? instanceof BeanContextServices)) {
          ((BeanContextServicesListener)???).serviceRevoked(paramBeanContextServiceRevokedEvent);
        }
      }
    }
  }
  
  protected static final BeanContextServicesListener getChildBeanContextServicesListener(Object paramObject)
  {
    try
    {
      return (BeanContextServicesListener)paramObject;
    }
    catch (ClassCastException localClassCastException) {}
    return null;
  }
  
  protected void childJustRemovedHook(Object paramObject, BeanContextSupport.BCSChild paramBCSChild)
  {
    BCSSChild localBCSSChild = (BCSSChild)paramBCSChild;
    localBCSSChild.cleanupReferences();
  }
  
  protected synchronized void releaseBeanContextResources()
  {
    super.releaseBeanContextResources();
    Object[] arrayOfObject;
    synchronized (children)
    {
      if (children.isEmpty()) {
        return;
      }
      arrayOfObject = children.values().toArray();
    }
    for (int i = 0; i < arrayOfObject.length; i++) {
      ((BCSSChild)arrayOfObject[i]).revokeAllDelegatedServicesNow();
    }
    proxy = null;
  }
  
  protected synchronized void initializeBeanContextResources()
  {
    super.initializeBeanContextResources();
    BeanContext localBeanContext = getBeanContext();
    if (localBeanContext == null) {
      return;
    }
    try
    {
      BeanContextServices localBeanContextServices = (BeanContextServices)localBeanContext;
      proxy = new BCSSProxyServiceProvider(localBeanContextServices);
    }
    catch (ClassCastException localClassCastException) {}
  }
  
  protected final void fireServiceAdded(Class paramClass)
  {
    BeanContextServiceAvailableEvent localBeanContextServiceAvailableEvent = new BeanContextServiceAvailableEvent(getBeanContextServicesPeer(), paramClass);
    fireServiceAdded(localBeanContextServiceAvailableEvent);
  }
  
  protected final void fireServiceAdded(BeanContextServiceAvailableEvent paramBeanContextServiceAvailableEvent)
  {
    Object[] arrayOfObject;
    synchronized (bcsListeners)
    {
      arrayOfObject = bcsListeners.toArray();
    }
    for (int i = 0; i < arrayOfObject.length; i++) {
      ((BeanContextServicesListener)arrayOfObject[i]).serviceAvailable(paramBeanContextServiceAvailableEvent);
    }
  }
  
  protected final void fireServiceRevoked(BeanContextServiceRevokedEvent paramBeanContextServiceRevokedEvent)
  {
    Object[] arrayOfObject;
    synchronized (bcsListeners)
    {
      arrayOfObject = bcsListeners.toArray();
    }
    for (int i = 0; i < arrayOfObject.length; i++) {
      ((BeanContextServiceRevokedListener)arrayOfObject[i]).serviceRevoked(paramBeanContextServiceRevokedEvent);
    }
  }
  
  protected final void fireServiceRevoked(Class paramClass, boolean paramBoolean)
  {
    BeanContextServiceRevokedEvent localBeanContextServiceRevokedEvent = new BeanContextServiceRevokedEvent(getBeanContextServicesPeer(), paramClass, paramBoolean);
    Object[] arrayOfObject;
    synchronized (bcsListeners)
    {
      arrayOfObject = bcsListeners.toArray();
    }
    for (int i = 0; i < arrayOfObject.length; i++) {
      ((BeanContextServicesListener)arrayOfObject[i]).serviceRevoked(localBeanContextServiceRevokedEvent);
    }
  }
  
  protected synchronized void bcsPreSerializationHook(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeInt(serializable);
    if (serializable <= 0) {
      return;
    }
    int i = 0;
    Iterator localIterator = services.entrySet().iterator();
    while ((localIterator.hasNext()) && (i < serializable))
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      BCSSServiceProvider localBCSSServiceProvider = null;
      try
      {
        localBCSSServiceProvider = (BCSSServiceProvider)localEntry.getValue();
      }
      catch (ClassCastException localClassCastException) {}
      continue;
      if ((localBCSSServiceProvider.getServiceProvider() instanceof Serializable))
      {
        paramObjectOutputStream.writeObject(localEntry.getKey());
        paramObjectOutputStream.writeObject(localBCSSServiceProvider);
        i++;
      }
    }
    if (i != serializable) {
      throw new IOException("wrote different number of service providers than expected");
    }
  }
  
  protected synchronized void bcsPreDeserializationHook(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    serializable = paramObjectInputStream.readInt();
    for (int i = serializable; i > 0; i--) {
      services.put(paramObjectInputStream.readObject(), paramObjectInputStream.readObject());
    }
  }
  
  private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    serialize(paramObjectOutputStream, bcsListeners);
  }
  
  private synchronized void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    deserialize(paramObjectInputStream, bcsListeners);
  }
  
  protected class BCSSChild
    extends BeanContextSupport.BCSChild
  {
    private static final long serialVersionUID = -3263851306889194873L;
    private transient HashMap serviceClasses;
    private transient HashMap serviceRequestors;
    
    BCSSChild(Object paramObject1, Object paramObject2)
    {
      super(paramObject1, paramObject2);
    }
    
    synchronized void usingService(Object paramObject1, Object paramObject2, Class paramClass, BeanContextServiceProvider paramBeanContextServiceProvider, boolean paramBoolean, BeanContextServiceRevokedListener paramBeanContextServiceRevokedListener)
      throws TooManyListenersException, UnsupportedOperationException
    {
      BCSSCServiceClassRef localBCSSCServiceClassRef = null;
      if (serviceClasses == null) {
        serviceClasses = new HashMap(1);
      } else {
        localBCSSCServiceClassRef = (BCSSCServiceClassRef)serviceClasses.get(paramClass);
      }
      if (localBCSSCServiceClassRef == null)
      {
        localBCSSCServiceClassRef = new BCSSCServiceClassRef(paramClass, paramBeanContextServiceProvider, paramBoolean);
        serviceClasses.put(paramClass, localBCSSCServiceClassRef);
      }
      else
      {
        localBCSSCServiceClassRef.verifyAndMaybeSetProvider(paramBeanContextServiceProvider, paramBoolean);
        localBCSSCServiceClassRef.verifyRequestor(paramObject1, paramBeanContextServiceRevokedListener);
      }
      localBCSSCServiceClassRef.addRequestor(paramObject1, paramBeanContextServiceRevokedListener);
      localBCSSCServiceClassRef.addRef(paramBoolean);
      BCSSCServiceRef localBCSSCServiceRef = null;
      Object localObject = null;
      if (serviceRequestors == null) {
        serviceRequestors = new HashMap(1);
      } else {
        localObject = (Map)serviceRequestors.get(paramObject1);
      }
      if (localObject == null)
      {
        localObject = new HashMap(1);
        serviceRequestors.put(paramObject1, localObject);
      }
      else
      {
        localBCSSCServiceRef = (BCSSCServiceRef)((Map)localObject).get(paramObject2);
      }
      if (localBCSSCServiceRef == null)
      {
        localBCSSCServiceRef = new BCSSCServiceRef(localBCSSCServiceClassRef, paramBoolean);
        ((Map)localObject).put(paramObject2, localBCSSCServiceRef);
      }
      else
      {
        localBCSSCServiceRef.addRef();
      }
    }
    
    synchronized void releaseService(Object paramObject1, Object paramObject2)
    {
      if (serviceRequestors == null) {
        return;
      }
      Map localMap = (Map)serviceRequestors.get(paramObject1);
      if (localMap == null) {
        return;
      }
      BCSSCServiceRef localBCSSCServiceRef = (BCSSCServiceRef)localMap.get(paramObject2);
      if (localBCSSCServiceRef == null) {
        return;
      }
      BCSSCServiceClassRef localBCSSCServiceClassRef = localBCSSCServiceRef.getServiceClassRef();
      boolean bool = localBCSSCServiceRef.isDelegated();
      BeanContextServiceProvider localBeanContextServiceProvider = bool ? localBCSSCServiceClassRef.getDelegateProvider() : localBCSSCServiceClassRef.getServiceProvider();
      localBeanContextServiceProvider.releaseService(getBeanContextServicesPeer(), paramObject1, paramObject2);
      localBCSSCServiceClassRef.releaseRef(bool);
      localBCSSCServiceClassRef.removeRequestor(paramObject1);
      if (localBCSSCServiceRef.release() == 0)
      {
        localMap.remove(paramObject2);
        if (localMap.isEmpty())
        {
          serviceRequestors.remove(paramObject1);
          localBCSSCServiceClassRef.removeRequestor(paramObject1);
        }
        if (serviceRequestors.isEmpty()) {
          serviceRequestors = null;
        }
        if (localBCSSCServiceClassRef.isEmpty()) {
          serviceClasses.remove(localBCSSCServiceClassRef.getServiceClass());
        }
        if (serviceClasses.isEmpty()) {
          serviceClasses = null;
        }
      }
    }
    
    synchronized void revokeService(Class paramClass, boolean paramBoolean1, boolean paramBoolean2)
    {
      if (serviceClasses == null) {
        return;
      }
      BCSSCServiceClassRef localBCSSCServiceClassRef = (BCSSCServiceClassRef)serviceClasses.get(paramClass);
      if (localBCSSCServiceClassRef == null) {
        return;
      }
      Iterator localIterator1 = localBCSSCServiceClassRef.cloneOfEntries();
      BeanContextServiceRevokedEvent localBeanContextServiceRevokedEvent = new BeanContextServiceRevokedEvent(getBeanContextServicesPeer(), paramClass, paramBoolean2);
      boolean bool = false;
      while ((localIterator1.hasNext()) && (serviceRequestors != null))
      {
        Map.Entry localEntry1 = (Map.Entry)localIterator1.next();
        BeanContextServiceRevokedListener localBeanContextServiceRevokedListener = (BeanContextServiceRevokedListener)localEntry1.getValue();
        if (paramBoolean2)
        {
          Object localObject = localEntry1.getKey();
          Map localMap = (Map)serviceRequestors.get(localObject);
          if (localMap != null)
          {
            Iterator localIterator2 = localMap.entrySet().iterator();
            while (localIterator2.hasNext())
            {
              Map.Entry localEntry2 = (Map.Entry)localIterator2.next();
              BCSSCServiceRef localBCSSCServiceRef = (BCSSCServiceRef)localEntry2.getValue();
              if ((localBCSSCServiceRef.getServiceClassRef().equals(localBCSSCServiceClassRef)) && (paramBoolean1 == localBCSSCServiceRef.isDelegated())) {
                localIterator2.remove();
              }
            }
            if ((bool = localMap.isEmpty())) {
              serviceRequestors.remove(localObject);
            }
          }
          if (bool) {
            localBCSSCServiceClassRef.removeRequestor(localObject);
          }
        }
        localBeanContextServiceRevokedListener.serviceRevoked(localBeanContextServiceRevokedEvent);
      }
      if ((paramBoolean2) && (serviceClasses != null))
      {
        if (localBCSSCServiceClassRef.isEmpty()) {
          serviceClasses.remove(paramClass);
        }
        if (serviceClasses.isEmpty()) {
          serviceClasses = null;
        }
      }
      if ((serviceRequestors != null) && (serviceRequestors.isEmpty())) {
        serviceRequestors = null;
      }
    }
    
    void cleanupReferences()
    {
      if (serviceRequestors == null) {
        return;
      }
      Iterator localIterator1 = serviceRequestors.entrySet().iterator();
      while (localIterator1.hasNext())
      {
        Map.Entry localEntry1 = (Map.Entry)localIterator1.next();
        Object localObject1 = localEntry1.getKey();
        Iterator localIterator2 = ((Map)localEntry1.getValue()).entrySet().iterator();
        localIterator1.remove();
        while (localIterator2.hasNext())
        {
          Map.Entry localEntry2 = (Map.Entry)localIterator2.next();
          Object localObject2 = localEntry2.getKey();
          BCSSCServiceRef localBCSSCServiceRef = (BCSSCServiceRef)localEntry2.getValue();
          BCSSCServiceClassRef localBCSSCServiceClassRef = localBCSSCServiceRef.getServiceClassRef();
          BeanContextServiceProvider localBeanContextServiceProvider = localBCSSCServiceRef.isDelegated() ? localBCSSCServiceClassRef.getDelegateProvider() : localBCSSCServiceClassRef.getServiceProvider();
          localBCSSCServiceClassRef.removeRequestor(localObject1);
          localIterator2.remove();
          while (localBCSSCServiceRef.release() >= 0) {
            localBeanContextServiceProvider.releaseService(getBeanContextServicesPeer(), localObject1, localObject2);
          }
        }
      }
      serviceRequestors = null;
      serviceClasses = null;
    }
    
    void revokeAllDelegatedServicesNow()
    {
      if (serviceClasses == null) {
        return;
      }
      Iterator localIterator1 = new HashSet(serviceClasses.values()).iterator();
      while (localIterator1.hasNext())
      {
        BCSSCServiceClassRef localBCSSCServiceClassRef = (BCSSCServiceClassRef)localIterator1.next();
        if (localBCSSCServiceClassRef.isDelegated())
        {
          Iterator localIterator2 = localBCSSCServiceClassRef.cloneOfEntries();
          BeanContextServiceRevokedEvent localBeanContextServiceRevokedEvent = new BeanContextServiceRevokedEvent(getBeanContextServicesPeer(), localBCSSCServiceClassRef.getServiceClass(), true);
          boolean bool = false;
          while (localIterator2.hasNext())
          {
            Map.Entry localEntry1 = (Map.Entry)localIterator2.next();
            BeanContextServiceRevokedListener localBeanContextServiceRevokedListener = (BeanContextServiceRevokedListener)localEntry1.getValue();
            Object localObject = localEntry1.getKey();
            Map localMap = (Map)serviceRequestors.get(localObject);
            if (localMap != null)
            {
              Iterator localIterator3 = localMap.entrySet().iterator();
              while (localIterator3.hasNext())
              {
                Map.Entry localEntry2 = (Map.Entry)localIterator3.next();
                BCSSCServiceRef localBCSSCServiceRef = (BCSSCServiceRef)localEntry2.getValue();
                if ((localBCSSCServiceRef.getServiceClassRef().equals(localBCSSCServiceClassRef)) && (localBCSSCServiceRef.isDelegated())) {
                  localIterator3.remove();
                }
              }
              if ((bool = localMap.isEmpty())) {
                serviceRequestors.remove(localObject);
              }
            }
            if (bool) {
              localBCSSCServiceClassRef.removeRequestor(localObject);
            }
            localBeanContextServiceRevokedListener.serviceRevoked(localBeanContextServiceRevokedEvent);
            if (localBCSSCServiceClassRef.isEmpty()) {
              serviceClasses.remove(localBCSSCServiceClassRef.getServiceClass());
            }
          }
        }
      }
      if (serviceClasses.isEmpty()) {
        serviceClasses = null;
      }
      if ((serviceRequestors != null) && (serviceRequestors.isEmpty())) {
        serviceRequestors = null;
      }
    }
    
    class BCSSCServiceClassRef
    {
      Class serviceClass;
      BeanContextServiceProvider serviceProvider;
      int serviceRefs;
      BeanContextServiceProvider delegateProvider;
      int delegateRefs;
      HashMap requestors = new HashMap(1);
      
      BCSSCServiceClassRef(Class paramClass, BeanContextServiceProvider paramBeanContextServiceProvider, boolean paramBoolean)
      {
        serviceClass = paramClass;
        if (paramBoolean) {
          delegateProvider = paramBeanContextServiceProvider;
        } else {
          serviceProvider = paramBeanContextServiceProvider;
        }
      }
      
      void addRequestor(Object paramObject, BeanContextServiceRevokedListener paramBeanContextServiceRevokedListener)
        throws TooManyListenersException
      {
        BeanContextServiceRevokedListener localBeanContextServiceRevokedListener = (BeanContextServiceRevokedListener)requestors.get(paramObject);
        if ((localBeanContextServiceRevokedListener != null) && (!localBeanContextServiceRevokedListener.equals(paramBeanContextServiceRevokedListener))) {
          throw new TooManyListenersException();
        }
        requestors.put(paramObject, paramBeanContextServiceRevokedListener);
      }
      
      void removeRequestor(Object paramObject)
      {
        requestors.remove(paramObject);
      }
      
      void verifyRequestor(Object paramObject, BeanContextServiceRevokedListener paramBeanContextServiceRevokedListener)
        throws TooManyListenersException
      {
        BeanContextServiceRevokedListener localBeanContextServiceRevokedListener = (BeanContextServiceRevokedListener)requestors.get(paramObject);
        if ((localBeanContextServiceRevokedListener != null) && (!localBeanContextServiceRevokedListener.equals(paramBeanContextServiceRevokedListener))) {
          throw new TooManyListenersException();
        }
      }
      
      void verifyAndMaybeSetProvider(BeanContextServiceProvider paramBeanContextServiceProvider, boolean paramBoolean)
      {
        BeanContextServiceProvider localBeanContextServiceProvider;
        if (paramBoolean)
        {
          localBeanContextServiceProvider = delegateProvider;
          if ((localBeanContextServiceProvider == null) || (paramBeanContextServiceProvider == null)) {
            delegateProvider = paramBeanContextServiceProvider;
          }
        }
        else
        {
          localBeanContextServiceProvider = serviceProvider;
          if ((localBeanContextServiceProvider == null) || (paramBeanContextServiceProvider == null))
          {
            serviceProvider = paramBeanContextServiceProvider;
            return;
          }
        }
        if (!localBeanContextServiceProvider.equals(paramBeanContextServiceProvider)) {
          throw new UnsupportedOperationException("existing service reference obtained from different BeanContextServiceProvider not supported");
        }
      }
      
      Iterator cloneOfEntries()
      {
        return ((HashMap)requestors.clone()).entrySet().iterator();
      }
      
      Iterator entries()
      {
        return requestors.entrySet().iterator();
      }
      
      boolean isEmpty()
      {
        return requestors.isEmpty();
      }
      
      Class getServiceClass()
      {
        return serviceClass;
      }
      
      BeanContextServiceProvider getServiceProvider()
      {
        return serviceProvider;
      }
      
      BeanContextServiceProvider getDelegateProvider()
      {
        return delegateProvider;
      }
      
      boolean isDelegated()
      {
        return delegateProvider != null;
      }
      
      void addRef(boolean paramBoolean)
      {
        if (paramBoolean) {
          delegateRefs += 1;
        } else {
          serviceRefs += 1;
        }
      }
      
      void releaseRef(boolean paramBoolean)
      {
        if (paramBoolean)
        {
          if (--delegateRefs == 0) {
            delegateProvider = null;
          }
        }
        else if (--serviceRefs <= 0) {
          serviceProvider = null;
        }
      }
      
      int getRefs()
      {
        return serviceRefs + delegateRefs;
      }
      
      int getDelegateRefs()
      {
        return delegateRefs;
      }
      
      int getServiceRefs()
      {
        return serviceRefs;
      }
    }
    
    class BCSSCServiceRef
    {
      BeanContextServicesSupport.BCSSChild.BCSSCServiceClassRef serviceClassRef;
      int refCnt = 1;
      boolean delegated = false;
      
      BCSSCServiceRef(BeanContextServicesSupport.BCSSChild.BCSSCServiceClassRef paramBCSSCServiceClassRef, boolean paramBoolean)
      {
        serviceClassRef = paramBCSSCServiceClassRef;
        delegated = paramBoolean;
      }
      
      void addRef()
      {
        refCnt += 1;
      }
      
      int release()
      {
        return --refCnt;
      }
      
      BeanContextServicesSupport.BCSSChild.BCSSCServiceClassRef getServiceClassRef()
      {
        return serviceClassRef;
      }
      
      boolean isDelegated()
      {
        return delegated;
      }
    }
  }
  
  protected class BCSSProxyServiceProvider
    implements BeanContextServiceProvider, BeanContextServiceRevokedListener
  {
    private BeanContextServices nestingCtxt;
    
    BCSSProxyServiceProvider(BeanContextServices paramBeanContextServices)
    {
      nestingCtxt = paramBeanContextServices;
    }
    
    public Object getService(BeanContextServices paramBeanContextServices, Object paramObject1, Class paramClass, Object paramObject2)
    {
      Object localObject = null;
      try
      {
        localObject = nestingCtxt.getService(paramBeanContextServices, paramObject1, paramClass, paramObject2, this);
      }
      catch (TooManyListenersException localTooManyListenersException)
      {
        return null;
      }
      return localObject;
    }
    
    public void releaseService(BeanContextServices paramBeanContextServices, Object paramObject1, Object paramObject2)
    {
      nestingCtxt.releaseService(paramBeanContextServices, paramObject1, paramObject2);
    }
    
    public Iterator getCurrentServiceSelectors(BeanContextServices paramBeanContextServices, Class paramClass)
    {
      return nestingCtxt.getCurrentServiceSelectors(paramClass);
    }
    
    public void serviceRevoked(BeanContextServiceRevokedEvent paramBeanContextServiceRevokedEvent)
    {
      Iterator localIterator = bcsChildren();
      while (localIterator.hasNext()) {
        ((BeanContextServicesSupport.BCSSChild)localIterator.next()).revokeService(paramBeanContextServiceRevokedEvent.getServiceClass(), true, paramBeanContextServiceRevokedEvent.isCurrentServiceInvalidNow());
      }
    }
  }
  
  protected static class BCSSServiceProvider
    implements Serializable
  {
    private static final long serialVersionUID = 861278251667444782L;
    protected BeanContextServiceProvider serviceProvider;
    
    BCSSServiceProvider(Class paramClass, BeanContextServiceProvider paramBeanContextServiceProvider)
    {
      serviceProvider = paramBeanContextServiceProvider;
    }
    
    protected BeanContextServiceProvider getServiceProvider()
    {
      return serviceProvider;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\beancontext\BeanContextServicesSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
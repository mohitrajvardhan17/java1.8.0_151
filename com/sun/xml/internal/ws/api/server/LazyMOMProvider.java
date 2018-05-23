package com.sun.xml.internal.ws.api.server;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public enum LazyMOMProvider
{
  INSTANCE;
  
  private final Set<WSEndpointScopeChangeListener> endpointsWaitingForMOM = new HashSet();
  private final Set<DefaultScopeChangeListener> listeners = new HashSet();
  private volatile Scope scope = Scope.STANDALONE;
  
  private LazyMOMProvider() {}
  
  public void initMOMForScope(Scope paramScope)
  {
    if ((scope == Scope.GLASSFISH_JMX) || ((paramScope == Scope.STANDALONE) && ((scope == Scope.GLASSFISH_JMX) || (scope == Scope.GLASSFISH_NO_JMX))) || (scope == paramScope)) {
      return;
    }
    scope = paramScope;
    fireScopeChanged();
  }
  
  private void fireScopeChanged()
  {
    Iterator localIterator = endpointsWaitingForMOM.iterator();
    ScopeChangeListener localScopeChangeListener;
    while (localIterator.hasNext())
    {
      localScopeChangeListener = (ScopeChangeListener)localIterator.next();
      localScopeChangeListener.scopeChanged(scope);
    }
    localIterator = listeners.iterator();
    while (localIterator.hasNext())
    {
      localScopeChangeListener = (ScopeChangeListener)localIterator.next();
      localScopeChangeListener.scopeChanged(scope);
    }
  }
  
  public void registerListener(DefaultScopeChangeListener paramDefaultScopeChangeListener)
  {
    listeners.add(paramDefaultScopeChangeListener);
    if (!isProviderInDefaultScope()) {
      paramDefaultScopeChangeListener.scopeChanged(scope);
    }
  }
  
  private boolean isProviderInDefaultScope()
  {
    return scope == Scope.STANDALONE;
  }
  
  public Scope getScope()
  {
    return scope;
  }
  
  public void registerEndpoint(WSEndpointScopeChangeListener paramWSEndpointScopeChangeListener)
  {
    endpointsWaitingForMOM.add(paramWSEndpointScopeChangeListener);
    if (!isProviderInDefaultScope()) {
      paramWSEndpointScopeChangeListener.scopeChanged(scope);
    }
  }
  
  public void unregisterEndpoint(WSEndpointScopeChangeListener paramWSEndpointScopeChangeListener)
  {
    endpointsWaitingForMOM.remove(paramWSEndpointScopeChangeListener);
  }
  
  public static abstract interface DefaultScopeChangeListener
    extends LazyMOMProvider.ScopeChangeListener
  {}
  
  public static enum Scope
  {
    STANDALONE,  GLASSFISH_NO_JMX,  GLASSFISH_JMX;
    
    private Scope() {}
  }
  
  public static abstract interface ScopeChangeListener
  {
    public abstract void scopeChanged(LazyMOMProvider.Scope paramScope);
  }
  
  public static abstract interface WSEndpointScopeChangeListener
    extends LazyMOMProvider.ScopeChangeListener
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\LazyMOMProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
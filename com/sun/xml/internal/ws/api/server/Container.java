package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.ComponentEx;
import com.sun.xml.internal.ws.api.ComponentRegistry;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class Container
  implements ComponentRegistry, ComponentEx
{
  private final Set<Component> components = new CopyOnWriteArraySet();
  public static final Container NONE = new NoneContainer(null);
  
  protected Container() {}
  
  public <S> S getSPI(Class<S> paramClass)
  {
    if (components == null) {
      return null;
    }
    Iterator localIterator = components.iterator();
    while (localIterator.hasNext())
    {
      Component localComponent = (Component)localIterator.next();
      Object localObject = localComponent.getSPI(paramClass);
      if (localObject != null) {
        return (S)localObject;
      }
    }
    return null;
  }
  
  public Set<Component> getComponents()
  {
    return components;
  }
  
  @NotNull
  public <E> Iterable<E> getIterableSPI(Class<E> paramClass)
  {
    Object localObject = getSPI(paramClass);
    if (localObject != null)
    {
      List localList = Collections.singletonList(localObject);
      return localList;
    }
    return Collections.emptySet();
  }
  
  private static final class NoneContainer
    extends Container
  {
    private NoneContainer() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\Container.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.runtime.Coordinator;
import javax.xml.bind.annotation.adapters.XmlAdapter;

final class AdaptedAccessor<BeanT, InMemValueT, OnWireValueT>
  extends Accessor<BeanT, OnWireValueT>
{
  private final Accessor<BeanT, InMemValueT> core;
  private final Class<? extends XmlAdapter<OnWireValueT, InMemValueT>> adapter;
  private XmlAdapter<OnWireValueT, InMemValueT> staticAdapter;
  
  AdaptedAccessor(Class<OnWireValueT> paramClass, Accessor<BeanT, InMemValueT> paramAccessor, Class<? extends XmlAdapter<OnWireValueT, InMemValueT>> paramClass1)
  {
    super(paramClass);
    core = paramAccessor;
    adapter = paramClass1;
  }
  
  public boolean isAdapted()
  {
    return true;
  }
  
  public OnWireValueT get(BeanT paramBeanT)
    throws AccessorException
  {
    Object localObject = core.get(paramBeanT);
    XmlAdapter localXmlAdapter = getAdapter();
    try
    {
      return (OnWireValueT)localXmlAdapter.marshal(localObject);
    }
    catch (Exception localException)
    {
      throw new AccessorException(localException);
    }
  }
  
  public void set(BeanT paramBeanT, OnWireValueT paramOnWireValueT)
    throws AccessorException
  {
    XmlAdapter localXmlAdapter = getAdapter();
    try
    {
      core.set(paramBeanT, paramOnWireValueT == null ? null : localXmlAdapter.unmarshal(paramOnWireValueT));
    }
    catch (Exception localException)
    {
      throw new AccessorException(localException);
    }
  }
  
  public Object getUnadapted(BeanT paramBeanT)
    throws AccessorException
  {
    return core.getUnadapted(paramBeanT);
  }
  
  public void setUnadapted(BeanT paramBeanT, Object paramObject)
    throws AccessorException
  {
    core.setUnadapted(paramBeanT, paramObject);
  }
  
  private XmlAdapter<OnWireValueT, InMemValueT> getAdapter()
  {
    Coordinator localCoordinator = Coordinator._getInstance();
    if (localCoordinator != null) {
      return localCoordinator.getAdapter(adapter);
    }
    synchronized (this)
    {
      if (staticAdapter == null) {
        staticAdapter = ((XmlAdapter)ClassFactory.create(adapter));
      }
    }
    return staticAdapter;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\AdaptedAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
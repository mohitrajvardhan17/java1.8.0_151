package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import org.xml.sax.SAXException;

public final class Scope<BeanT, PropT, ItemT, PackT>
{
  public final UnmarshallingContext context;
  private BeanT bean;
  private Accessor<BeanT, PropT> acc;
  private PackT pack;
  private Lister<BeanT, PropT, ItemT, PackT> lister;
  
  Scope(UnmarshallingContext paramUnmarshallingContext)
  {
    context = paramUnmarshallingContext;
  }
  
  public boolean hasStarted()
  {
    return bean != null;
  }
  
  public void reset()
  {
    if (bean == null)
    {
      assert (clean());
      return;
    }
    bean = null;
    acc = null;
    pack = null;
    lister = null;
  }
  
  public void finish()
    throws AccessorException
  {
    if (hasStarted())
    {
      lister.endPacking(pack, bean, acc);
      reset();
    }
    assert (clean());
  }
  
  private boolean clean()
  {
    return (bean == null) && (acc == null) && (pack == null) && (lister == null);
  }
  
  public void add(Accessor<BeanT, PropT> paramAccessor, Lister<BeanT, PropT, ItemT, PackT> paramLister, ItemT paramItemT)
    throws SAXException
  {
    try
    {
      if (!hasStarted())
      {
        bean = context.getCurrentState().getTarget();
        acc = paramAccessor;
        lister = paramLister;
        pack = paramLister.startPacking(bean, paramAccessor);
      }
      paramLister.addToPack(pack, paramItemT);
    }
    catch (AccessorException localAccessorException)
    {
      Loader.handleGenericException(localAccessorException, true);
      lister = Lister.getErrorInstance();
      acc = Accessor.getErrorInstance();
    }
  }
  
  public void start(Accessor<BeanT, PropT> paramAccessor, Lister<BeanT, PropT, ItemT, PackT> paramLister)
    throws SAXException
  {
    try
    {
      if (!hasStarted())
      {
        bean = context.getCurrentState().getTarget();
        acc = paramAccessor;
        lister = paramLister;
        pack = paramLister.startPacking(bean, paramAccessor);
      }
    }
    catch (AccessorException localAccessorException)
    {
      Loader.handleGenericException(localAccessorException, true);
      lister = Lister.getErrorInstance();
      acc = Accessor.getErrorInstance();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\Scope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
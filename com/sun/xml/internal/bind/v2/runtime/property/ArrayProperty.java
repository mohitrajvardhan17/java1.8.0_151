package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import java.lang.reflect.Type;

abstract class ArrayProperty<BeanT, ListT, ItemT>
  extends PropertyImpl<BeanT>
{
  protected final Accessor<BeanT, ListT> acc;
  protected final Lister<BeanT, ListT, ItemT, Object> lister;
  
  protected ArrayProperty(JAXBContextImpl paramJAXBContextImpl, RuntimePropertyInfo paramRuntimePropertyInfo)
  {
    super(paramJAXBContextImpl, paramRuntimePropertyInfo);
    assert (paramRuntimePropertyInfo.isCollection());
    lister = Lister.create((Type)Utils.REFLECTION_NAVIGATOR.erasure(paramRuntimePropertyInfo.getRawType()), paramRuntimePropertyInfo.id(), paramRuntimePropertyInfo.getAdapter());
    assert (lister != null);
    acc = paramRuntimePropertyInfo.getAccessor().optimize(paramJAXBContextImpl);
    assert (acc != null);
  }
  
  public void reset(BeanT paramBeanT)
    throws AccessorException
  {
    lister.reset(paramBeanT, acc);
  }
  
  public final String getIdValue(BeanT paramBeanT)
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\ArrayProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

final class RuntimeElementInfoImpl
  extends ElementInfoImpl<Type, Class, Field, Method>
  implements RuntimeElementInfo
{
  private final Class<? extends XmlAdapter> adapterType;
  
  public RuntimeElementInfoImpl(RuntimeModelBuilder paramRuntimeModelBuilder, RegistryInfoImpl paramRegistryInfoImpl, Method paramMethod)
    throws IllegalAnnotationException
  {
    super(paramRuntimeModelBuilder, paramRegistryInfoImpl, paramMethod);
    Adapter localAdapter = getProperty().getAdapter();
    if (localAdapter != null) {
      adapterType = ((Class)adapterType);
    } else {
      adapterType = null;
    }
  }
  
  protected ElementInfoImpl<Type, Class, Field, Method>.PropertyImpl createPropertyImpl()
  {
    return new RuntimePropertyImpl();
  }
  
  public RuntimeElementPropertyInfo getProperty()
  {
    return (RuntimeElementPropertyInfo)super.getProperty();
  }
  
  public Class<? extends JAXBElement> getType()
  {
    return (Class)Utils.REFLECTION_NAVIGATOR.erasure(super.getType());
  }
  
  public RuntimeClassInfo getScope()
  {
    return (RuntimeClassInfo)super.getScope();
  }
  
  public RuntimeNonElement getContentType()
  {
    return (RuntimeNonElement)super.getContentType();
  }
  
  class RuntimePropertyImpl
    extends ElementInfoImpl<Type, Class, Field, Method>.PropertyImpl
    implements RuntimeElementPropertyInfo, RuntimeTypeRef
  {
    RuntimePropertyImpl()
    {
      super();
    }
    
    public Accessor getAccessor()
    {
      if (adapterType == null) {
        return Accessor.JAXB_ELEMENT_VALUE;
      }
      return Accessor.JAXB_ELEMENT_VALUE.adapt((Class)getAdapterdefaultType, adapterType);
    }
    
    public Type getRawType()
    {
      return Collection.class;
    }
    
    public Type getIndividualType()
    {
      return (Type)getContentType().getType();
    }
    
    public boolean elementOnlyContent()
    {
      return false;
    }
    
    public List<? extends RuntimeTypeRef> getTypes()
    {
      return Collections.singletonList(this);
    }
    
    public List<? extends RuntimeNonElement> ref()
    {
      return super.ref();
    }
    
    public RuntimeNonElement getTarget()
    {
      return (RuntimeNonElement)super.getTarget();
    }
    
    public RuntimePropertyInfo getSource()
    {
      return this;
    }
    
    public Transducer getTransducer()
    {
      return RuntimeModelBuilder.createTransducer(this);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\RuntimeElementInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
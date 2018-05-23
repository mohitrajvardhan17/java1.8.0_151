package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElementRef;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlList;
import javax.xml.namespace.QName;

abstract class SingleTypePropertyInfoImpl<T, C, F, M>
  extends PropertyInfoImpl<T, C, F, M>
{
  private NonElement<T, C> type;
  private final Accessor acc;
  private Transducer xducer;
  
  public SingleTypePropertyInfoImpl(ClassInfoImpl<T, C, F, M> paramClassInfoImpl, PropertySeed<T, C, F, M> paramPropertySeed)
  {
    super(paramClassInfoImpl, paramPropertySeed);
    if ((this instanceof RuntimePropertyInfo))
    {
      Accessor localAccessor = ((RuntimeClassInfoImpl.RuntimePropertySeed)paramPropertySeed).getAccessor();
      if ((getAdapter() != null) && (!isCollection())) {
        localAccessor = localAccessor.adapt(((RuntimePropertyInfo)this).getAdapter());
      }
      acc = localAccessor;
    }
    else
    {
      acc = null;
    }
  }
  
  public List<? extends NonElement<T, C>> ref()
  {
    return Collections.singletonList(getTarget());
  }
  
  public NonElement<T, C> getTarget()
  {
    if (type == null)
    {
      assert (parent.builder != null) : "this method must be called during the build stage";
      type = parent.builder.getTypeInfo(getIndividualType(), this);
    }
    return type;
  }
  
  public PropertyInfo<T, C> getSource()
  {
    return this;
  }
  
  public void link()
  {
    super.link();
    if ((!NonElement.ANYTYPE_NAME.equals(type.getTypeName())) && (!type.isSimpleType()) && (id() != ID.IDREF)) {
      parent.builder.reportError(new IllegalAnnotationException(Messages.SIMPLE_TYPE_IS_REQUIRED.format(new Object[0]), seed));
    }
    if ((!isCollection()) && (seed.hasAnnotation(XmlList.class))) {
      parent.builder.reportError(new IllegalAnnotationException(Messages.XMLLIST_ON_SINGLE_PROPERTY.format(new Object[0]), this));
    }
  }
  
  public Accessor getAccessor()
  {
    return acc;
  }
  
  public Transducer getTransducer()
  {
    if (xducer == null)
    {
      xducer = RuntimeModelBuilder.createTransducer((RuntimeNonElementRef)this);
      if (xducer == null) {
        xducer = RuntimeBuiltinLeafInfoImpl.STRING;
      }
    }
    return xducer;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\SingleTypePropertyInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
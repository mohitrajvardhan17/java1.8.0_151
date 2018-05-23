package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.core.ElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import java.util.AbstractList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElement.DEFAULT;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlList;
import javax.xml.namespace.QName;

class ElementPropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT>
  extends ERPropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT>
  implements ElementPropertyInfo<TypeT, ClassDeclT>
{
  private List<TypeRefImpl<TypeT, ClassDeclT>> types;
  private final List<TypeInfo<TypeT, ClassDeclT>> ref = new AbstractList()
  {
    public TypeInfo<TypeT, ClassDeclT> get(int paramAnonymousInt)
    {
      return ((TypeRefImpl)getTypes().get(paramAnonymousInt)).getTarget();
    }
    
    public int size()
    {
      return getTypes().size();
    }
  };
  private Boolean isRequired;
  private final boolean isValueList = seed.hasAnnotation(XmlList.class);
  
  ElementPropertyInfoImpl(ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> paramClassInfoImpl, PropertySeed<TypeT, ClassDeclT, FieldT, MethodT> paramPropertySeed)
  {
    super(paramClassInfoImpl, paramPropertySeed);
  }
  
  public List<? extends TypeRefImpl<TypeT, ClassDeclT>> getTypes()
  {
    if (types == null)
    {
      types = new FinalArrayList();
      XmlElement[] arrayOfXmlElement = null;
      XmlElement localXmlElement1 = (XmlElement)seed.readAnnotation(XmlElement.class);
      XmlElements localXmlElements = (XmlElements)seed.readAnnotation(XmlElements.class);
      if ((localXmlElement1 != null) && (localXmlElements != null)) {
        parent.builder.reportError(new IllegalAnnotationException(Messages.MUTUALLY_EXCLUSIVE_ANNOTATIONS.format(new Object[] { nav().getClassName(parent.getClazz()) + '#' + seed.getName(), localXmlElement1.annotationType().getName(), localXmlElements.annotationType().getName() }), localXmlElement1, localXmlElements));
      }
      isRequired = Boolean.valueOf(true);
      if (localXmlElement1 != null) {
        arrayOfXmlElement = new XmlElement[] { localXmlElement1 };
      } else if (localXmlElements != null) {
        arrayOfXmlElement = localXmlElements.value();
      }
      Object localObject1;
      if (arrayOfXmlElement == null)
      {
        localObject1 = getIndividualType();
        if ((!nav().isPrimitive(localObject1)) || (isCollection())) {
          isRequired = Boolean.valueOf(false);
        }
        types.add(createTypeRef(calcXmlName((XmlElement)null), localObject1, isCollection(), null));
      }
      else
      {
        for (XmlElement localXmlElement2 : arrayOfXmlElement)
        {
          QName localQName = calcXmlName(localXmlElement2);
          Object localObject2 = reader().getClassValue(localXmlElement2, "type");
          if (nav().isSameType(localObject2, nav().ref(XmlElement.DEFAULT.class))) {
            localObject2 = getIndividualType();
          }
          if (((!nav().isPrimitive(localObject2)) || (isCollection())) && (!localXmlElement2.required())) {
            isRequired = Boolean.valueOf(false);
          }
          types.add(createTypeRef(localQName, localObject2, localXmlElement2.nillable(), getDefaultValue(localXmlElement2.defaultValue())));
        }
      }
      types = Collections.unmodifiableList(types);
      assert (!types.contains(null));
    }
    return types;
  }
  
  private String getDefaultValue(String paramString)
  {
    if (paramString.equals("\000")) {
      return null;
    }
    return paramString;
  }
  
  protected TypeRefImpl<TypeT, ClassDeclT> createTypeRef(QName paramQName, TypeT paramTypeT, boolean paramBoolean, String paramString)
  {
    return new TypeRefImpl(this, paramQName, paramTypeT, paramBoolean, paramString);
  }
  
  public boolean isValueList()
  {
    return isValueList;
  }
  
  public boolean isRequired()
  {
    if (isRequired == null) {
      getTypes();
    }
    return isRequired.booleanValue();
  }
  
  public List<? extends TypeInfo<TypeT, ClassDeclT>> ref()
  {
    return ref;
  }
  
  public final PropertyKind kind()
  {
    return PropertyKind.ELEMENT;
  }
  
  protected void link()
  {
    super.link();
    Iterator localIterator = getTypes().iterator();
    TypeRefImpl localTypeRefImpl;
    while (localIterator.hasNext())
    {
      localTypeRefImpl = (TypeRefImpl)localIterator.next();
      localTypeRefImpl.link();
    }
    if (isValueList())
    {
      if (id() != ID.IDREF)
      {
        localIterator = types.iterator();
        while (localIterator.hasNext())
        {
          localTypeRefImpl = (TypeRefImpl)localIterator.next();
          if (!localTypeRefImpl.getTarget().isSimpleType())
          {
            parent.builder.reportError(new IllegalAnnotationException(Messages.XMLLIST_NEEDS_SIMPLETYPE.format(new Object[] { nav().getTypeName(localTypeRefImpl.getTarget().getType()) }), this));
            break;
          }
        }
      }
      if (!isCollection()) {
        parent.builder.reportError(new IllegalAnnotationException(Messages.XMLLIST_ON_SINGLE_PROPERTY.format(new Object[0]), this));
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\ElementPropertyInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
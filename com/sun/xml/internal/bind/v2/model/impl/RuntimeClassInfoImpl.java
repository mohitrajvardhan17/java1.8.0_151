package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.AccessorFactory;
import com.sun.xml.internal.bind.AccessorFactoryImpl;
import com.sun.xml.internal.bind.InternalAccessorFactory;
import com.sun.xml.internal.bind.XmlAccessorFactory;
import com.sun.xml.internal.bind.annotation.XmlLocation;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeValuePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Location;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor.FieldReflection;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class RuntimeClassInfoImpl
  extends ClassInfoImpl<Type, Class, Field, Method>
  implements RuntimeClassInfo, RuntimeElement
{
  private Accessor<?, Locator> xmlLocationAccessor;
  private AccessorFactory accessorFactory = createAccessorFactory(paramClass);
  private boolean supressAccessorWarnings = false;
  private Accessor<?, Map<QName, String>> attributeWildcardAccessor;
  private boolean computedTransducer = false;
  private Transducer xducer = null;
  
  public RuntimeClassInfoImpl(RuntimeModelBuilder paramRuntimeModelBuilder, Locatable paramLocatable, Class paramClass)
  {
    super(paramRuntimeModelBuilder, paramLocatable, paramClass);
  }
  
  protected AccessorFactory createAccessorFactory(Class paramClass)
  {
    Object localObject = null;
    JAXBContextImpl localJAXBContextImpl = builder).context;
    if (localJAXBContextImpl != null)
    {
      supressAccessorWarnings = supressAccessorWarnings;
      if (xmlAccessorFactorySupport)
      {
        XmlAccessorFactory localXmlAccessorFactory = findXmlAccessorFactoryAnnotation(paramClass);
        if (localXmlAccessorFactory != null) {
          try
          {
            localObject = (AccessorFactory)localXmlAccessorFactory.value().newInstance();
          }
          catch (InstantiationException localInstantiationException)
          {
            builder.reportError(new IllegalAnnotationException(Messages.ACCESSORFACTORY_INSTANTIATION_EXCEPTION.format(new Object[] { localXmlAccessorFactory.getClass().getName(), nav().getClassName(paramClass) }), this));
          }
          catch (IllegalAccessException localIllegalAccessException)
          {
            builder.reportError(new IllegalAnnotationException(Messages.ACCESSORFACTORY_ACCESS_EXCEPTION.format(new Object[] { localXmlAccessorFactory.getClass().getName(), nav().getClassName(paramClass) }), this));
          }
        }
      }
    }
    if (localObject == null) {
      localObject = AccessorFactoryImpl.getInstance();
    }
    return (AccessorFactory)localObject;
  }
  
  protected XmlAccessorFactory findXmlAccessorFactoryAnnotation(Class paramClass)
  {
    XmlAccessorFactory localXmlAccessorFactory = (XmlAccessorFactory)reader().getClassAnnotation(XmlAccessorFactory.class, paramClass, this);
    if (localXmlAccessorFactory == null) {
      localXmlAccessorFactory = (XmlAccessorFactory)reader().getPackageAnnotation(XmlAccessorFactory.class, paramClass, this);
    }
    return localXmlAccessorFactory;
  }
  
  public Method getFactoryMethod()
  {
    return super.getFactoryMethod();
  }
  
  public final RuntimeClassInfoImpl getBaseClass()
  {
    return (RuntimeClassInfoImpl)super.getBaseClass();
  }
  
  protected ReferencePropertyInfoImpl createReferenceProperty(PropertySeed<Type, Class, Field, Method> paramPropertySeed)
  {
    return new RuntimeReferencePropertyInfoImpl(this, paramPropertySeed);
  }
  
  protected AttributePropertyInfoImpl createAttributeProperty(PropertySeed<Type, Class, Field, Method> paramPropertySeed)
  {
    return new RuntimeAttributePropertyInfoImpl(this, paramPropertySeed);
  }
  
  protected ValuePropertyInfoImpl createValueProperty(PropertySeed<Type, Class, Field, Method> paramPropertySeed)
  {
    return new RuntimeValuePropertyInfoImpl(this, paramPropertySeed);
  }
  
  protected ElementPropertyInfoImpl createElementProperty(PropertySeed<Type, Class, Field, Method> paramPropertySeed)
  {
    return new RuntimeElementPropertyInfoImpl(this, paramPropertySeed);
  }
  
  protected MapPropertyInfoImpl createMapProperty(PropertySeed<Type, Class, Field, Method> paramPropertySeed)
  {
    return new RuntimeMapPropertyInfoImpl(this, paramPropertySeed);
  }
  
  public List<? extends RuntimePropertyInfo> getProperties()
  {
    return super.getProperties();
  }
  
  public RuntimePropertyInfo getProperty(String paramString)
  {
    return (RuntimePropertyInfo)super.getProperty(paramString);
  }
  
  public void link()
  {
    getTransducer();
    super.link();
  }
  
  public <B> Accessor<B, Map<QName, String>> getAttributeWildcard()
  {
    for (RuntimeClassInfoImpl localRuntimeClassInfoImpl = this; localRuntimeClassInfoImpl != null; localRuntimeClassInfoImpl = localRuntimeClassInfoImpl.getBaseClass()) {
      if (attributeWildcard != null)
      {
        if (attributeWildcardAccessor == null) {
          attributeWildcardAccessor = localRuntimeClassInfoImpl.createAttributeWildcardAccessor();
        }
        return attributeWildcardAccessor;
      }
    }
    return null;
  }
  
  public Transducer getTransducer()
  {
    if (!computedTransducer)
    {
      computedTransducer = true;
      xducer = calcTransducer();
    }
    return xducer;
  }
  
  private Transducer calcTransducer()
  {
    RuntimeValuePropertyInfo localRuntimeValuePropertyInfo = null;
    if (hasAttributeWildcard()) {
      return null;
    }
    for (RuntimeClassInfoImpl localRuntimeClassInfoImpl = this; localRuntimeClassInfoImpl != null; localRuntimeClassInfoImpl = localRuntimeClassInfoImpl.getBaseClass())
    {
      Iterator localIterator = localRuntimeClassInfoImpl.getProperties().iterator();
      while (localIterator.hasNext())
      {
        RuntimePropertyInfo localRuntimePropertyInfo = (RuntimePropertyInfo)localIterator.next();
        if (localRuntimePropertyInfo.kind() == PropertyKind.VALUE) {
          localRuntimeValuePropertyInfo = (RuntimeValuePropertyInfo)localRuntimePropertyInfo;
        } else {
          return null;
        }
      }
    }
    if (localRuntimeValuePropertyInfo == null) {
      return null;
    }
    if (!localRuntimeValuePropertyInfo.getTarget().isSimpleType()) {
      return null;
    }
    return new TransducerImpl((Class)getClazz(), TransducedAccessor.get(builder).context, localRuntimeValuePropertyInfo));
  }
  
  private Accessor<?, Map<QName, String>> createAttributeWildcardAccessor()
  {
    assert (attributeWildcard != null);
    return ((RuntimePropertySeed)attributeWildcard).getAccessor();
  }
  
  protected RuntimePropertySeed createFieldSeed(Field paramField)
  {
    boolean bool = Modifier.isStatic(paramField.getModifiers());
    Accessor localAccessor;
    try
    {
      if (supressAccessorWarnings) {
        localAccessor = ((InternalAccessorFactory)accessorFactory).createFieldAccessor((Class)clazz, paramField, bool, supressAccessorWarnings);
      } else {
        localAccessor = accessorFactory.createFieldAccessor((Class)clazz, paramField, bool);
      }
    }
    catch (JAXBException localJAXBException)
    {
      builder.reportError(new IllegalAnnotationException(Messages.CUSTOM_ACCESSORFACTORY_FIELD_ERROR.format(new Object[] { nav().getClassName(clazz), localJAXBException.toString() }), this));
      localAccessor = Accessor.getErrorInstance();
    }
    return new RuntimePropertySeed(super.createFieldSeed(paramField), localAccessor);
  }
  
  public RuntimePropertySeed createAccessorSeed(Method paramMethod1, Method paramMethod2)
  {
    Accessor localAccessor;
    try
    {
      localAccessor = accessorFactory.createPropertyAccessor((Class)clazz, paramMethod1, paramMethod2);
    }
    catch (JAXBException localJAXBException)
    {
      builder.reportError(new IllegalAnnotationException(Messages.CUSTOM_ACCESSORFACTORY_PROPERTY_ERROR.format(new Object[] { nav().getClassName(clazz), localJAXBException.toString() }), this));
      localAccessor = Accessor.getErrorInstance();
    }
    return new RuntimePropertySeed(super.createAccessorSeed(paramMethod1, paramMethod2), localAccessor);
  }
  
  protected void checkFieldXmlLocation(Field paramField)
  {
    if (reader().hasFieldAnnotation(XmlLocation.class, paramField)) {
      xmlLocationAccessor = new Accessor.FieldReflection(paramField);
    }
  }
  
  public Accessor<?, Locator> getLocatorField()
  {
    return xmlLocationAccessor;
  }
  
  static final class RuntimePropertySeed
    implements PropertySeed<Type, Class, Field, Method>
  {
    private final Accessor acc;
    private final PropertySeed<Type, Class, Field, Method> core;
    
    public RuntimePropertySeed(PropertySeed<Type, Class, Field, Method> paramPropertySeed, Accessor paramAccessor)
    {
      core = paramPropertySeed;
      acc = paramAccessor;
    }
    
    public String getName()
    {
      return core.getName();
    }
    
    public <A extends Annotation> A readAnnotation(Class<A> paramClass)
    {
      return core.readAnnotation(paramClass);
    }
    
    public boolean hasAnnotation(Class<? extends Annotation> paramClass)
    {
      return core.hasAnnotation(paramClass);
    }
    
    public Type getRawType()
    {
      return (Type)core.getRawType();
    }
    
    public Location getLocation()
    {
      return core.getLocation();
    }
    
    public Locatable getUpstream()
    {
      return core.getUpstream();
    }
    
    public Accessor getAccessor()
    {
      return acc;
    }
  }
  
  private static final class TransducerImpl<BeanT>
    implements Transducer<BeanT>
  {
    private final TransducedAccessor<BeanT> xacc;
    private final Class<BeanT> ownerClass;
    
    public TransducerImpl(Class<BeanT> paramClass, TransducedAccessor<BeanT> paramTransducedAccessor)
    {
      xacc = paramTransducedAccessor;
      ownerClass = paramClass;
    }
    
    public boolean useNamespace()
    {
      return xacc.useNamespace();
    }
    
    public boolean isDefault()
    {
      return false;
    }
    
    public void declareNamespace(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
      throws AccessorException
    {
      try
      {
        xacc.declareNamespace(paramBeanT, paramXMLSerializer);
      }
      catch (SAXException localSAXException)
      {
        throw new AccessorException(localSAXException);
      }
    }
    
    @NotNull
    public CharSequence print(BeanT paramBeanT)
      throws AccessorException
    {
      try
      {
        CharSequence localCharSequence = xacc.print(paramBeanT);
        if (localCharSequence == null) {
          throw new AccessorException(Messages.THERE_MUST_BE_VALUE_IN_XMLVALUE.format(new Object[] { paramBeanT }));
        }
        return localCharSequence;
      }
      catch (SAXException localSAXException)
      {
        throw new AccessorException(localSAXException);
      }
    }
    
    public BeanT parse(CharSequence paramCharSequence)
      throws AccessorException, SAXException
    {
      UnmarshallingContext localUnmarshallingContext = UnmarshallingContext.getInstance();
      Object localObject;
      if (localUnmarshallingContext != null) {
        localObject = localUnmarshallingContext.createInstance(ownerClass);
      } else {
        localObject = ClassFactory.create(ownerClass);
      }
      xacc.parse(localObject, paramCharSequence);
      return (BeanT)localObject;
    }
    
    public void writeText(XMLSerializer paramXMLSerializer, BeanT paramBeanT, String paramString)
      throws IOException, SAXException, XMLStreamException, AccessorException
    {
      if (!xacc.hasValue(paramBeanT)) {
        throw new AccessorException(Messages.THERE_MUST_BE_VALUE_IN_XMLVALUE.format(new Object[] { paramBeanT }));
      }
      xacc.writeText(paramXMLSerializer, paramBeanT, paramString);
    }
    
    public void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, BeanT paramBeanT, String paramString)
      throws IOException, SAXException, XMLStreamException, AccessorException
    {
      if (!xacc.hasValue(paramBeanT)) {
        throw new AccessorException(Messages.THERE_MUST_BE_VALUE_IN_XMLVALUE.format(new Object[] { paramBeanT }));
      }
      xacc.writeLeafElement(paramXMLSerializer, paramName, paramBeanT, paramString);
    }
    
    public QName getTypeName(BeanT paramBeanT)
    {
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\RuntimeClassInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
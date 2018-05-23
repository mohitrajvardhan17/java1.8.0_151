package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.property.AttributeProperty;
import com.sun.xml.internal.bind.v2.runtime.property.Property;
import com.sun.xml.internal.bind.v2.runtime.property.PropertyFactory;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.StructureLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiTypeLoader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

public final class ClassBeanInfoImpl<BeanT>
  extends JaxBeanInfo<BeanT>
  implements AttributeAccessor<BeanT>
{
  public final Property<BeanT>[] properties;
  private Property<? super BeanT> idProperty;
  private Loader loader;
  private Loader loaderWithTypeSubst;
  private RuntimeClassInfo ci;
  private final Accessor<? super BeanT, Map<QName, String>> inheritedAttWildcard;
  private final Transducer<BeanT> xducer;
  public final ClassBeanInfoImpl<? super BeanT> superClazz;
  private final Accessor<? super BeanT, Locator> xmlLocatorField;
  private final Name tagName;
  private boolean retainPropertyInfo = false;
  private AttributeProperty<BeanT>[] attributeProperties;
  private Property<BeanT>[] uriProperties;
  private final Method factoryMethod;
  private static final AttributeProperty[] EMPTY_PROPERTIES = new AttributeProperty[0];
  private static final Logger logger = Util.getClassLogger();
  
  ClassBeanInfoImpl(JAXBContextImpl paramJAXBContextImpl, RuntimeClassInfo paramRuntimeClassInfo)
  {
    super(paramJAXBContextImpl, paramRuntimeClassInfo, (Class)paramRuntimeClassInfo.getClazz(), paramRuntimeClassInfo.getTypeName(), paramRuntimeClassInfo.isElement(), false, true);
    ci = paramRuntimeClassInfo;
    inheritedAttWildcard = paramRuntimeClassInfo.getAttributeWildcard();
    xducer = paramRuntimeClassInfo.getTransducer();
    factoryMethod = paramRuntimeClassInfo.getFactoryMethod();
    retainPropertyInfo = retainPropertyInfo;
    if (factoryMethod != null)
    {
      int i = factoryMethod.getDeclaringClass().getModifiers();
      if ((!Modifier.isPublic(i)) || (!Modifier.isPublic(factoryMethod.getModifiers()))) {
        try
        {
          factoryMethod.setAccessible(true);
        }
        catch (SecurityException localSecurityException)
        {
          logger.log(Level.FINE, "Unable to make the method of " + factoryMethod + " accessible", localSecurityException);
          throw localSecurityException;
        }
      }
    }
    if (paramRuntimeClassInfo.getBaseClass() == null) {
      superClazz = null;
    } else {
      superClazz = paramJAXBContextImpl.getOrCreate(paramRuntimeClassInfo.getBaseClass());
    }
    if ((superClazz != null) && (superClazz.xmlLocatorField != null)) {
      xmlLocatorField = superClazz.xmlLocatorField;
    } else {
      xmlLocatorField = paramRuntimeClassInfo.getLocatorField();
    }
    List localList = paramRuntimeClassInfo.getProperties();
    properties = new Property[localList.size()];
    int j = 0;
    boolean bool = true;
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      RuntimePropertyInfo localRuntimePropertyInfo = (RuntimePropertyInfo)localIterator.next();
      Property localProperty = PropertyFactory.create(paramJAXBContextImpl, localRuntimePropertyInfo);
      if (localRuntimePropertyInfo.id() == ID.ID) {
        idProperty = localProperty;
      }
      properties[(j++)] = localProperty;
      bool &= localRuntimePropertyInfo.elementOnlyContent();
      checkOverrideProperties(localProperty);
    }
    hasElementOnlyContentModel(bool);
    if (paramRuntimeClassInfo.isElement()) {
      tagName = nameBuilder.createElementName(paramRuntimeClassInfo.getElementName());
    } else {
      tagName = null;
    }
    setLifecycleFlags();
  }
  
  private void checkOverrideProperties(Property paramProperty)
  {
    ClassBeanInfoImpl localClassBeanInfoImpl = this;
    while ((localClassBeanInfoImpl = superClazz) != null)
    {
      Property[] arrayOfProperty1 = properties;
      if (arrayOfProperty1 == null) {
        break;
      }
      for (Property localProperty : arrayOfProperty1) {
        if (localProperty != null)
        {
          String str = localProperty.getFieldName();
          if ((str != null) && (str.equals(paramProperty.getFieldName()))) {
            localProperty.setHiddenByOverride(true);
          }
        }
      }
    }
  }
  
  protected void link(JAXBContextImpl paramJAXBContextImpl)
  {
    if (uriProperties != null) {
      return;
    }
    super.link(paramJAXBContextImpl);
    if (superClazz != null) {
      superClazz.link(paramJAXBContextImpl);
    }
    getLoader(paramJAXBContextImpl, true);
    if (superClazz != null)
    {
      if (idProperty == null) {
        idProperty = superClazz.idProperty;
      }
      if (!superClazz.hasElementOnlyContentModel()) {
        hasElementOnlyContentModel(false);
      }
    }
    FinalArrayList localFinalArrayList1 = new FinalArrayList();
    FinalArrayList localFinalArrayList2 = new FinalArrayList();
    for (ClassBeanInfoImpl localClassBeanInfoImpl = this; localClassBeanInfoImpl != null; localClassBeanInfoImpl = superClazz) {
      for (int i = 0; i < properties.length; i++)
      {
        Property localProperty = properties[i];
        if ((localProperty instanceof AttributeProperty)) {
          localFinalArrayList1.add((AttributeProperty)localProperty);
        }
        if (localProperty.hasSerializeURIAction()) {
          localFinalArrayList2.add(localProperty);
        }
      }
    }
    if (c14nSupport) {
      Collections.sort(localFinalArrayList1);
    }
    if (localFinalArrayList1.isEmpty()) {
      attributeProperties = EMPTY_PROPERTIES;
    } else {
      attributeProperties = ((AttributeProperty[])localFinalArrayList1.toArray(new AttributeProperty[localFinalArrayList1.size()]));
    }
    if (localFinalArrayList2.isEmpty()) {
      uriProperties = EMPTY_PROPERTIES;
    } else {
      uriProperties = ((Property[])localFinalArrayList2.toArray(new Property[localFinalArrayList2.size()]));
    }
  }
  
  public void wrapUp()
  {
    for (Property localProperty : properties) {
      localProperty.wrapUp();
    }
    ci = null;
    super.wrapUp();
  }
  
  public String getElementNamespaceURI(BeanT paramBeanT)
  {
    return tagName.nsUri;
  }
  
  public String getElementLocalName(BeanT paramBeanT)
  {
    return tagName.localName;
  }
  
  public BeanT createInstance(UnmarshallingContext paramUnmarshallingContext)
    throws IllegalAccessException, InvocationTargetException, InstantiationException, SAXException
  {
    Object localObject1 = null;
    if (factoryMethod == null)
    {
      localObject1 = ClassFactory.create0(jaxbType);
    }
    else
    {
      Object localObject2 = ClassFactory.create(factoryMethod);
      if (jaxbType.isInstance(localObject2)) {
        localObject1 = localObject2;
      } else {
        throw new InstantiationException("The factory method didn't return a correct object");
      }
    }
    if (xmlLocatorField != null) {
      try
      {
        xmlLocatorField.set(localObject1, new LocatorImpl(paramUnmarshallingContext.getLocator()));
      }
      catch (AccessorException localAccessorException)
      {
        paramUnmarshallingContext.handleError(localAccessorException);
      }
    }
    return (BeanT)localObject1;
  }
  
  public boolean reset(BeanT paramBeanT, UnmarshallingContext paramUnmarshallingContext)
    throws SAXException
  {
    try
    {
      if (superClazz != null) {
        superClazz.reset(paramBeanT, paramUnmarshallingContext);
      }
      for (Property localProperty : properties) {
        localProperty.reset(paramBeanT);
      }
      return true;
    }
    catch (AccessorException localAccessorException)
    {
      paramUnmarshallingContext.handleError(localAccessorException);
    }
    return false;
  }
  
  public String getId(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
    throws SAXException
  {
    if (idProperty != null) {
      try
      {
        return idProperty.getIdValue(paramBeanT);
      }
      catch (AccessorException localAccessorException)
      {
        paramXMLSerializer.reportError(null, localAccessorException);
      }
    }
    return null;
  }
  
  public void serializeRoot(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
    throws SAXException, IOException, XMLStreamException
  {
    if (tagName == null)
    {
      Class localClass = paramBeanT.getClass();
      String str;
      if (localClass.isAnnotationPresent(XmlRootElement.class)) {
        str = Messages.UNABLE_TO_MARSHAL_UNBOUND_CLASS.format(new Object[] { localClass.getName() });
      } else {
        str = Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(new Object[] { localClass.getName() });
      }
      paramXMLSerializer.reportError(new ValidationEventImpl(1, str, null, null));
    }
    else
    {
      paramXMLSerializer.startElement(tagName, paramBeanT);
      paramXMLSerializer.childAsSoleContent(paramBeanT, null);
      paramXMLSerializer.endElement();
      if (retainPropertyInfo) {
        currentProperty.remove();
      }
    }
  }
  
  public void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
    throws SAXException, IOException, XMLStreamException
  {
    if (superClazz != null) {
      superClazz.serializeBody(paramBeanT, paramXMLSerializer);
    }
    try
    {
      for (Property localProperty : properties)
      {
        if (retainPropertyInfo) {
          currentProperty.set(localProperty);
        }
        boolean bool = localProperty.isHiddenByOverride();
        if ((!bool) || (paramBeanT.getClass().equals(jaxbType)))
        {
          localProperty.serializeBody(paramBeanT, paramXMLSerializer, null);
        }
        else if (bool)
        {
          Class localClass = paramBeanT.getClass();
          if (Utils.REFLECTION_NAVIGATOR.getDeclaredField(localClass, localProperty.getFieldName()) == null) {
            localProperty.serializeBody(paramBeanT, paramXMLSerializer, null);
          }
        }
      }
    }
    catch (AccessorException localAccessorException)
    {
      paramXMLSerializer.reportError(null, localAccessorException);
    }
  }
  
  public void serializeAttributes(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
    throws SAXException, IOException, XMLStreamException
  {
    for (Object localObject2 : attributeProperties) {
      try
      {
        if (retainPropertyInfo)
        {
          Property localProperty = paramXMLSerializer.getCurrentProperty();
          currentProperty.set(localObject2);
          ((AttributeProperty)localObject2).serializeAttributes(paramBeanT, paramXMLSerializer);
          currentProperty.set(localProperty);
        }
        else
        {
          ((AttributeProperty)localObject2).serializeAttributes(paramBeanT, paramXMLSerializer);
        }
        if (attName.equals("http://www.w3.org/2001/XMLSchema-instance", "nil")) {
          isNilIncluded = true;
        }
      }
      catch (AccessorException localAccessorException2)
      {
        paramXMLSerializer.reportError(null, localAccessorException2);
      }
    }
    try
    {
      if (inheritedAttWildcard != null)
      {
        ??? = (Map)inheritedAttWildcard.get(paramBeanT);
        paramXMLSerializer.attWildcardAsAttributes((Map)???, null);
      }
    }
    catch (AccessorException localAccessorException1)
    {
      paramXMLSerializer.reportError(null, localAccessorException1);
    }
  }
  
  public void serializeURIs(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
    throws SAXException
  {
    try
    {
      Object localObject1;
      if (retainPropertyInfo)
      {
        localObject1 = paramXMLSerializer.getCurrentProperty();
        for (Property localProperty : uriProperties)
        {
          currentProperty.set(localProperty);
          localProperty.serializeURIs(paramBeanT, paramXMLSerializer);
        }
        currentProperty.set(localObject1);
      }
      else
      {
        for (Object localObject2 : uriProperties) {
          ((Property)localObject2).serializeURIs(paramBeanT, paramXMLSerializer);
        }
      }
      if (inheritedAttWildcard != null)
      {
        localObject1 = (Map)inheritedAttWildcard.get(paramBeanT);
        paramXMLSerializer.attWildcardAsURIs((Map)localObject1, null);
      }
    }
    catch (AccessorException localAccessorException)
    {
      paramXMLSerializer.reportError(null, localAccessorException);
    }
  }
  
  public Loader getLoader(JAXBContextImpl paramJAXBContextImpl, boolean paramBoolean)
  {
    if (loader == null)
    {
      StructureLoader localStructureLoader = new StructureLoader(this);
      loader = localStructureLoader;
      if (ci.hasSubClasses()) {
        loaderWithTypeSubst = new XsiTypeLoader(this);
      } else {
        loaderWithTypeSubst = loader;
      }
      localStructureLoader.init(paramJAXBContextImpl, this, ci.getAttributeWildcard());
    }
    if (paramBoolean) {
      return loaderWithTypeSubst;
    }
    return loader;
  }
  
  public Transducer<BeanT> getTransducer()
  {
    return xducer;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\ClassBeanInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.NameBuilder;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LeafPropertyLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LeafPropertyXsiLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Single;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class SingleElementLeafProperty<BeanT>
  extends PropertyImpl<BeanT>
{
  private final Name tagName;
  private final boolean nillable;
  private final Accessor acc;
  private final String defaultValue;
  private final TransducedAccessor<BeanT> xacc;
  private final boolean improvedXsiTypeHandling;
  private final boolean idRef;
  
  public SingleElementLeafProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeElementPropertyInfo paramRuntimeElementPropertyInfo)
  {
    super(paramJAXBContextImpl, paramRuntimeElementPropertyInfo);
    RuntimeTypeRef localRuntimeTypeRef = (RuntimeTypeRef)paramRuntimeElementPropertyInfo.getTypes().get(0);
    tagName = nameBuilder.createElementName(localRuntimeTypeRef.getTagName());
    assert (tagName != null);
    nillable = localRuntimeTypeRef.isNillable();
    defaultValue = localRuntimeTypeRef.getDefaultValue();
    acc = paramRuntimeElementPropertyInfo.getAccessor().optimize(paramJAXBContextImpl);
    xacc = TransducedAccessor.get(paramJAXBContextImpl, localRuntimeTypeRef);
    assert (xacc != null);
    improvedXsiTypeHandling = improvedXsiTypeHandling;
    idRef = (localRuntimeTypeRef.getSource().id() == ID.IDREF);
  }
  
  public void reset(BeanT paramBeanT)
    throws AccessorException
  {
    acc.set(paramBeanT, null);
  }
  
  public String getIdValue(BeanT paramBeanT)
    throws AccessorException, SAXException
  {
    return xacc.print(paramBeanT).toString();
  }
  
  public void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, Object paramObject)
    throws SAXException, AccessorException, IOException, XMLStreamException
  {
    boolean bool = xacc.hasValue(paramBeanT);
    Object localObject = null;
    try
    {
      localObject = acc.getUnadapted(paramBeanT);
    }
    catch (AccessorException localAccessorException) {}
    Class localClass = acc.getValueType();
    if (xsiTypeNeeded(paramBeanT, paramXMLSerializer, localObject, localClass))
    {
      paramXMLSerializer.startElement(tagName, paramObject);
      paramXMLSerializer.childAsXsiType(localObject, fieldName, grammar.getBeanInfo(localClass), false);
      paramXMLSerializer.endElement();
    }
    else if (bool)
    {
      xacc.writeLeafElement(paramXMLSerializer, tagName, paramBeanT, fieldName);
    }
    else if (nillable)
    {
      paramXMLSerializer.startElement(tagName, null);
      paramXMLSerializer.writeXsiNilTrue();
      paramXMLSerializer.endElement();
    }
  }
  
  private boolean xsiTypeNeeded(BeanT paramBeanT, XMLSerializer paramXMLSerializer, Object paramObject, Class paramClass)
  {
    if (!improvedXsiTypeHandling) {
      return false;
    }
    if (acc.isAdapted()) {
      return false;
    }
    if (paramObject == null) {
      return false;
    }
    if (paramObject.getClass().equals(paramClass)) {
      return false;
    }
    if (idRef) {
      return false;
    }
    if (paramClass.isPrimitive()) {
      return false;
    }
    return (acc.isValueTypeAbstractable()) || (isNillableAbstract(paramBeanT, grammar, paramObject, paramClass));
  }
  
  private boolean isNillableAbstract(BeanT paramBeanT, JAXBContextImpl paramJAXBContextImpl, Object paramObject, Class paramClass)
  {
    if (!nillable) {
      return false;
    }
    if (paramClass != Object.class) {
      return false;
    }
    if (paramBeanT.getClass() != JAXBElement.class) {
      return false;
    }
    JAXBElement localJAXBElement = (JAXBElement)paramBeanT;
    Class localClass1 = paramObject.getClass();
    Class localClass2 = localJAXBElement.getDeclaredType();
    if (localClass2.equals(localClass1)) {
      return false;
    }
    if (!localClass2.isAssignableFrom(localClass1)) {
      return false;
    }
    if (!Modifier.isAbstract(localClass2.getModifiers())) {
      return false;
    }
    return acc.isAbstractable(localClass2);
  }
  
  public void buildChildElementUnmarshallers(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap)
  {
    Object localObject = new LeafPropertyLoader(xacc);
    if (defaultValue != null) {
      localObject = new DefaultValueLoaderDecorator((Loader)localObject, defaultValue);
    }
    if ((nillable) || (context.allNillable)) {
      localObject = new XsiNilLoader.Single((Loader)localObject, acc);
    }
    if (improvedXsiTypeHandling) {
      localObject = new LeafPropertyXsiLoader((Loader)localObject, xacc, acc);
    }
    paramQNameMap.put(tagName, new ChildLoader((Loader)localObject, null));
  }
  
  public PropertyKind getKind()
  {
    return PropertyKind.ELEMENT;
  }
  
  public Accessor getElementPropertyAccessor(String paramString1, String paramString2)
  {
    if (tagName.equals(paramString1, paramString2)) {
      return acc;
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\SingleElementLeafProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
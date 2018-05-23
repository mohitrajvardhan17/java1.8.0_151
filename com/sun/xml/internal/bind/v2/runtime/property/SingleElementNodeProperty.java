package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.NameBuilder;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Single;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class SingleElementNodeProperty<BeanT, ValueT>
  extends PropertyImpl<BeanT>
{
  private final Accessor<BeanT, ValueT> acc;
  private final boolean nillable;
  private final QName[] acceptedElements;
  private final Map<Class, TagAndType> typeNames = new HashMap();
  private RuntimeElementPropertyInfo prop;
  private final Name nullTagName;
  
  public SingleElementNodeProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeElementPropertyInfo paramRuntimeElementPropertyInfo)
  {
    super(paramJAXBContextImpl, paramRuntimeElementPropertyInfo);
    acc = paramRuntimeElementPropertyInfo.getAccessor().optimize(paramJAXBContextImpl);
    prop = paramRuntimeElementPropertyInfo;
    QName localQName = null;
    boolean bool = false;
    acceptedElements = new QName[paramRuntimeElementPropertyInfo.getTypes().size()];
    for (int i = 0; i < acceptedElements.length; i++) {
      acceptedElements[i] = ((RuntimeTypeRef)paramRuntimeElementPropertyInfo.getTypes().get(i)).getTagName();
    }
    Iterator localIterator = paramRuntimeElementPropertyInfo.getTypes().iterator();
    while (localIterator.hasNext())
    {
      RuntimeTypeRef localRuntimeTypeRef = (RuntimeTypeRef)localIterator.next();
      JaxBeanInfo localJaxBeanInfo = paramJAXBContextImpl.getOrCreate(localRuntimeTypeRef.getTarget());
      if (localQName == null) {
        localQName = localRuntimeTypeRef.getTagName();
      }
      typeNames.put(jaxbType, new TagAndType(nameBuilder.createElementName(localRuntimeTypeRef.getTagName()), localJaxBeanInfo));
      bool |= localRuntimeTypeRef.isNillable();
    }
    nullTagName = nameBuilder.createElementName(localQName);
    nillable = bool;
  }
  
  public void wrapUp()
  {
    super.wrapUp();
    prop = null;
  }
  
  public void reset(BeanT paramBeanT)
    throws AccessorException
  {
    acc.set(paramBeanT, null);
  }
  
  public String getIdValue(BeanT paramBeanT)
  {
    return null;
  }
  
  public void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, Object paramObject)
    throws SAXException, AccessorException, IOException, XMLStreamException
  {
    Object localObject = acc.get(paramBeanT);
    if (localObject != null)
    {
      Class localClass = localObject.getClass();
      TagAndType localTagAndType = (TagAndType)typeNames.get(localClass);
      if (localTagAndType == null)
      {
        Iterator localIterator = typeNames.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          if (((Class)localEntry.getKey()).isAssignableFrom(localClass))
          {
            localTagAndType = (TagAndType)localEntry.getValue();
            break;
          }
        }
      }
      int i = ((paramBeanT instanceof JAXBElement)) && (((JAXBElement)paramBeanT).isNil()) ? 1 : 0;
      if (localTagAndType == null)
      {
        paramXMLSerializer.startElement(typeNames.values().iterator().next()).tagName, null);
        paramXMLSerializer.childAsXsiType(localObject, fieldName, grammar.getBeanInfo(Object.class), (i != 0) && (nillable));
      }
      else
      {
        paramXMLSerializer.startElement(tagName, null);
        paramXMLSerializer.childAsXsiType(localObject, fieldName, beanInfo, (i != 0) && (nillable));
      }
      paramXMLSerializer.endElement();
    }
    else if (nillable)
    {
      paramXMLSerializer.startElement(nullTagName, null);
      paramXMLSerializer.writeXsiNilTrue();
      paramXMLSerializer.endElement();
    }
  }
  
  public void buildChildElementUnmarshallers(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap)
  {
    JAXBContextImpl localJAXBContextImpl = context;
    Iterator localIterator = prop.getTypes().iterator();
    while (localIterator.hasNext())
    {
      TypeRef localTypeRef = (TypeRef)localIterator.next();
      JaxBeanInfo localJaxBeanInfo = localJAXBContextImpl.getOrCreate((RuntimeTypeInfo)localTypeRef.getTarget());
      Object localObject = localJaxBeanInfo.getLoader(localJAXBContextImpl, !Modifier.isFinal(jaxbType.getModifiers()));
      if (localTypeRef.getDefaultValue() != null) {
        localObject = new DefaultValueLoaderDecorator((Loader)localObject, localTypeRef.getDefaultValue());
      }
      if ((nillable) || (context.allNillable)) {
        localObject = new XsiNilLoader.Single((Loader)localObject, acc);
      }
      paramQNameMap.put(localTypeRef.getTagName(), new ChildLoader((Loader)localObject, acc));
    }
  }
  
  public PropertyKind getKind()
  {
    return PropertyKind.ELEMENT;
  }
  
  public Accessor getElementPropertyAccessor(String paramString1, String paramString2)
  {
    for (QName localQName : acceptedElements) {
      if ((localQName.getNamespaceURI().equals(paramString1)) && (localQName.getLocalPart().equals(paramString2))) {
        return acc;
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\SingleElementNodeProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
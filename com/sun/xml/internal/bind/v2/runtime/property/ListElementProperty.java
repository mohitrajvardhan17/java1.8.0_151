package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.NameBuilder;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.ListTransducedAccessorImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LeafPropertyLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class ListElementProperty<BeanT, ListT, ItemT>
  extends ArrayProperty<BeanT, ListT, ItemT>
{
  private final Name tagName;
  private final String defaultValue;
  private final TransducedAccessor<BeanT> xacc;
  
  public ListElementProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeElementPropertyInfo paramRuntimeElementPropertyInfo)
  {
    super(paramJAXBContextImpl, paramRuntimeElementPropertyInfo);
    assert (paramRuntimeElementPropertyInfo.isValueList());
    assert (paramRuntimeElementPropertyInfo.getTypes().size() == 1);
    RuntimeTypeRef localRuntimeTypeRef = (RuntimeTypeRef)paramRuntimeElementPropertyInfo.getTypes().get(0);
    tagName = nameBuilder.createElementName(localRuntimeTypeRef.getTagName());
    defaultValue = localRuntimeTypeRef.getDefaultValue();
    Transducer localTransducer = localRuntimeTypeRef.getTransducer();
    xacc = new ListTransducedAccessorImpl(localTransducer, acc, lister);
  }
  
  public PropertyKind getKind()
  {
    return PropertyKind.ELEMENT;
  }
  
  public void buildChildElementUnmarshallers(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap)
  {
    Object localObject = new LeafPropertyLoader(xacc);
    localObject = new DefaultValueLoaderDecorator((Loader)localObject, defaultValue);
    paramQNameMap.put(tagName, new ChildLoader((Loader)localObject, null));
  }
  
  public void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, Object paramObject)
    throws SAXException, AccessorException, IOException, XMLStreamException
  {
    Object localObject = acc.get(paramBeanT);
    if (localObject != null) {
      if (xacc.useNamespace())
      {
        paramXMLSerializer.startElement(tagName, null);
        xacc.declareNamespace(paramBeanT, paramXMLSerializer);
        paramXMLSerializer.endNamespaceDecls(localObject);
        paramXMLSerializer.endAttributes();
        xacc.writeText(paramXMLSerializer, paramBeanT, fieldName);
        paramXMLSerializer.endElement();
      }
      else
      {
        xacc.writeLeafElement(paramXMLSerializer, tagName, paramBeanT, fieldName);
      }
    }
  }
  
  public Accessor getElementPropertyAccessor(String paramString1, String paramString2)
  {
    if ((tagName != null) && (tagName.equals(paramString1, paramString2))) {
      return acc;
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\ListElementProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeLeafInfo;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TextLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiTypeLoader;
import java.io.IOException;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class LeafBeanInfoImpl<BeanT>
  extends JaxBeanInfo<BeanT>
{
  private final Loader loader;
  private final Loader loaderWithSubst;
  private final Transducer<BeanT> xducer;
  private final Name tagName;
  
  public LeafBeanInfoImpl(JAXBContextImpl paramJAXBContextImpl, RuntimeLeafInfo paramRuntimeLeafInfo)
  {
    super(paramJAXBContextImpl, paramRuntimeLeafInfo, paramRuntimeLeafInfo.getClazz(), paramRuntimeLeafInfo.getTypeNames(), paramRuntimeLeafInfo.isElement(), true, false);
    xducer = paramRuntimeLeafInfo.getTransducer();
    loader = new TextLoader(xducer);
    loaderWithSubst = new XsiTypeLoader(this);
    if (isElement()) {
      tagName = nameBuilder.createElementName(paramRuntimeLeafInfo.getElementName());
    } else {
      tagName = null;
    }
  }
  
  public QName getTypeName(BeanT paramBeanT)
  {
    QName localQName = xducer.getTypeName(paramBeanT);
    if (localQName != null) {
      return localQName;
    }
    return super.getTypeName(paramBeanT);
  }
  
  public final String getElementNamespaceURI(BeanT paramBeanT)
  {
    return tagName.nsUri;
  }
  
  public final String getElementLocalName(BeanT paramBeanT)
  {
    return tagName.localName;
  }
  
  public BeanT createInstance(UnmarshallingContext paramUnmarshallingContext)
  {
    throw new UnsupportedOperationException();
  }
  
  public final boolean reset(BeanT paramBeanT, UnmarshallingContext paramUnmarshallingContext)
  {
    return false;
  }
  
  public final String getId(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
  {
    return null;
  }
  
  public final void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
    throws SAXException, IOException, XMLStreamException
  {
    try
    {
      xducer.writeText(paramXMLSerializer, paramBeanT, null);
    }
    catch (AccessorException localAccessorException)
    {
      paramXMLSerializer.reportError(null, localAccessorException);
    }
  }
  
  public final void serializeAttributes(BeanT paramBeanT, XMLSerializer paramXMLSerializer) {}
  
  public final void serializeRoot(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
    throws SAXException, IOException, XMLStreamException
  {
    if (tagName == null)
    {
      paramXMLSerializer.reportError(new ValidationEventImpl(1, Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(new Object[] { paramBeanT.getClass().getName() }), null, null));
    }
    else
    {
      paramXMLSerializer.startElement(tagName, paramBeanT);
      paramXMLSerializer.childAsSoleContent(paramBeanT, null);
      paramXMLSerializer.endElement();
    }
  }
  
  public final void serializeURIs(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
    throws SAXException
  {
    if (xducer.useNamespace()) {
      try
      {
        xducer.declareNamespace(paramBeanT, paramXMLSerializer);
      }
      catch (AccessorException localAccessorException)
      {
        paramXMLSerializer.reportError(null, localAccessorException);
      }
    }
  }
  
  public final Loader getLoader(JAXBContextImpl paramJAXBContextImpl, boolean paramBoolean)
  {
    if (paramBoolean) {
      return loaderWithSubst;
    }
    return loader;
  }
  
  public Transducer<BeanT> getTransducer()
  {
    return xducer;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\LeafBeanInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
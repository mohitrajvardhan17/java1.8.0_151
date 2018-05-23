package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.model.runtime.RuntimeArrayInfo;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext.State;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class ArrayBeanInfoImpl
  extends JaxBeanInfo
{
  private final Class itemType = jaxbType.getComponentType();
  private final JaxBeanInfo itemBeanInfo;
  private Loader loader;
  
  public ArrayBeanInfoImpl(JAXBContextImpl paramJAXBContextImpl, RuntimeArrayInfo paramRuntimeArrayInfo)
  {
    super(paramJAXBContextImpl, paramRuntimeArrayInfo, paramRuntimeArrayInfo.getType(), paramRuntimeArrayInfo.getTypeName(), false, true, false);
    itemBeanInfo = paramJAXBContextImpl.getOrCreate(paramRuntimeArrayInfo.getItemType());
  }
  
  protected void link(JAXBContextImpl paramJAXBContextImpl)
  {
    getLoader(paramJAXBContextImpl, false);
    super.link(paramJAXBContextImpl);
  }
  
  protected Object toArray(List paramList)
  {
    int i = paramList.size();
    Object localObject = Array.newInstance(itemType, i);
    for (int j = 0; j < i; j++) {
      Array.set(localObject, j, paramList.get(j));
    }
    return localObject;
  }
  
  public void serializeBody(Object paramObject, XMLSerializer paramXMLSerializer)
    throws SAXException, IOException, XMLStreamException
  {
    int i = Array.getLength(paramObject);
    for (int j = 0; j < i; j++)
    {
      Object localObject = Array.get(paramObject, j);
      paramXMLSerializer.startElement("", "item", null, null);
      if (localObject == null) {
        paramXMLSerializer.writeXsiNilTrue();
      } else {
        paramXMLSerializer.childAsXsiType(localObject, "arrayItem", itemBeanInfo, false);
      }
      paramXMLSerializer.endElement();
    }
  }
  
  public final String getElementNamespaceURI(Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
  
  public final String getElementLocalName(Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
  
  public final Object createInstance(UnmarshallingContext paramUnmarshallingContext)
  {
    return new ArrayList();
  }
  
  public final boolean reset(Object paramObject, UnmarshallingContext paramUnmarshallingContext)
  {
    return false;
  }
  
  public final String getId(Object paramObject, XMLSerializer paramXMLSerializer)
  {
    return null;
  }
  
  public final void serializeAttributes(Object paramObject, XMLSerializer paramXMLSerializer) {}
  
  public final void serializeRoot(Object paramObject, XMLSerializer paramXMLSerializer)
    throws SAXException, IOException, XMLStreamException
  {
    paramXMLSerializer.reportError(new ValidationEventImpl(1, Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(new Object[] { paramObject.getClass().getName() }), null, null));
  }
  
  public final void serializeURIs(Object paramObject, XMLSerializer paramXMLSerializer) {}
  
  public final Transducer getTransducer()
  {
    return null;
  }
  
  public final Loader getLoader(JAXBContextImpl paramJAXBContextImpl, boolean paramBoolean)
  {
    if (loader == null) {
      loader = new ArrayLoader(paramJAXBContextImpl);
    }
    return loader;
  }
  
  private final class ArrayLoader
    extends Loader
    implements Receiver
  {
    private final Loader itemLoader;
    
    public ArrayLoader(JAXBContextImpl paramJAXBContextImpl)
    {
      super();
      itemLoader = itemBeanInfo.getLoader(paramJAXBContextImpl, true);
    }
    
    public void startElement(UnmarshallingContext.State paramState, TagName paramTagName)
    {
      paramState.setTarget(new ArrayList());
    }
    
    public void leaveElement(UnmarshallingContext.State paramState, TagName paramTagName)
    {
      paramState.setTarget(toArray((List)paramState.getTarget()));
    }
    
    public void childElement(UnmarshallingContext.State paramState, TagName paramTagName)
      throws SAXException
    {
      if (paramTagName.matches("", "item"))
      {
        paramState.setLoader(itemLoader);
        paramState.setReceiver(this);
      }
      else
      {
        super.childElement(paramState, paramTagName);
      }
    }
    
    public Collection<QName> getExpectedChildElements()
    {
      return Collections.singleton(new QName("", "item"));
    }
    
    public void receive(UnmarshallingContext.State paramState, Object paramObject)
    {
      ((List)paramState.getTarget()).add(paramObject);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\ArrayBeanInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
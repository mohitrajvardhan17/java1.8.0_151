package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeReferencePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext.State;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.WildcardLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import com.sun.xml.internal.bind.v2.util.QNameMap.Entry;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

class ArrayReferenceNodeProperty<BeanT, ListT, ItemT>
  extends ArrayERProperty<BeanT, ListT, ItemT>
{
  private final QNameMap<JaxBeanInfo> expectedElements = new QNameMap();
  private final boolean isMixed;
  private final DomHandler domHandler;
  private final WildcardMode wcMode;
  
  public ArrayReferenceNodeProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeReferencePropertyInfo paramRuntimeReferencePropertyInfo)
  {
    super(paramJAXBContextImpl, paramRuntimeReferencePropertyInfo, paramRuntimeReferencePropertyInfo.getXmlName(), paramRuntimeReferencePropertyInfo.isCollectionNillable());
    Iterator localIterator = paramRuntimeReferencePropertyInfo.getElements().iterator();
    while (localIterator.hasNext())
    {
      RuntimeElement localRuntimeElement = (RuntimeElement)localIterator.next();
      JaxBeanInfo localJaxBeanInfo = paramJAXBContextImpl.getOrCreate(localRuntimeElement);
      expectedElements.put(localRuntimeElement.getElementName().getNamespaceURI(), localRuntimeElement.getElementName().getLocalPart(), localJaxBeanInfo);
    }
    isMixed = paramRuntimeReferencePropertyInfo.isMixed();
    if (paramRuntimeReferencePropertyInfo.getWildcard() != null)
    {
      domHandler = ((DomHandler)ClassFactory.create((Class)paramRuntimeReferencePropertyInfo.getDOMHandler()));
      wcMode = paramRuntimeReferencePropertyInfo.getWildcard();
    }
    else
    {
      domHandler = null;
      wcMode = null;
    }
  }
  
  protected final void serializeListBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, ListT paramListT)
    throws IOException, XMLStreamException, SAXException
  {
    ListIterator localListIterator = lister.iterator(paramListT, paramXMLSerializer);
    while (localListIterator.hasNext()) {
      try
      {
        Object localObject = localListIterator.next();
        if (localObject != null) {
          if ((isMixed) && (localObject.getClass() == String.class))
          {
            paramXMLSerializer.text((String)localObject, null);
          }
          else
          {
            JaxBeanInfo localJaxBeanInfo = grammar.getBeanInfo(localObject, true);
            if ((jaxbType == Object.class) && (domHandler != null)) {
              paramXMLSerializer.writeDom(localObject, domHandler, paramBeanT, fieldName);
            } else {
              localJaxBeanInfo.serializeRoot(localObject, paramXMLSerializer);
            }
          }
        }
      }
      catch (JAXBException localJAXBException)
      {
        paramXMLSerializer.reportError(fieldName, localJAXBException);
      }
    }
  }
  
  public void createBodyUnmarshaller(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap)
  {
    int i = paramUnmarshallerChain.allocateOffset();
    ArrayERProperty.ReceiverImpl localReceiverImpl = new ArrayERProperty.ReceiverImpl(this, i);
    Iterator localIterator = expectedElements.entrySet().iterator();
    while (localIterator.hasNext())
    {
      QNameMap.Entry localEntry = (QNameMap.Entry)localIterator.next();
      JaxBeanInfo localJaxBeanInfo = (JaxBeanInfo)localEntry.getValue();
      paramQNameMap.put(nsUri, localName, new ChildLoader(localJaxBeanInfo.getLoader(context, true), localReceiverImpl));
    }
    if (isMixed) {
      paramQNameMap.put(TEXT_HANDLER, new ChildLoader(new MixedTextLoader(localReceiverImpl), null));
    }
    if (domHandler != null) {
      paramQNameMap.put(CATCH_ALL, new ChildLoader(new WildcardLoader(domHandler, wcMode), localReceiverImpl));
    }
  }
  
  public PropertyKind getKind()
  {
    return PropertyKind.REFERENCE;
  }
  
  public Accessor getElementPropertyAccessor(String paramString1, String paramString2)
  {
    if (wrapperTagName != null)
    {
      if (wrapperTagName.equals(paramString1, paramString2)) {
        return acc;
      }
    }
    else if (expectedElements.containsKey(paramString1, paramString2)) {
      return acc;
    }
    return null;
  }
  
  private static final class MixedTextLoader
    extends Loader
  {
    private final Receiver recv;
    
    public MixedTextLoader(Receiver paramReceiver)
    {
      super();
      recv = paramReceiver;
    }
    
    public void text(UnmarshallingContext.State paramState, CharSequence paramCharSequence)
      throws SAXException
    {
      if (paramCharSequence.length() != 0) {
        recv.receive(paramState, paramCharSequence.toString());
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\ArrayReferenceNodeProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
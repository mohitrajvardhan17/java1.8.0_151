package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeReferencePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.ElementBeanInfoImpl;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.WildcardLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import com.sun.xml.internal.bind.v2.util.QNameMap.Entry;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class SingleReferenceNodeProperty<BeanT, ValueT>
  extends PropertyImpl<BeanT>
{
  private final Accessor<BeanT, ValueT> acc;
  private final QNameMap<JaxBeanInfo> expectedElements = new QNameMap();
  private final DomHandler domHandler;
  private final WildcardMode wcMode;
  
  public SingleReferenceNodeProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeReferencePropertyInfo paramRuntimeReferencePropertyInfo)
  {
    super(paramJAXBContextImpl, paramRuntimeReferencePropertyInfo);
    acc = paramRuntimeReferencePropertyInfo.getAccessor().optimize(paramJAXBContextImpl);
    Iterator localIterator = paramRuntimeReferencePropertyInfo.getElements().iterator();
    while (localIterator.hasNext())
    {
      RuntimeElement localRuntimeElement = (RuntimeElement)localIterator.next();
      expectedElements.put(localRuntimeElement.getElementName(), paramJAXBContextImpl.getOrCreate(localRuntimeElement));
    }
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
    if (localObject != null) {
      try
      {
        JaxBeanInfo localJaxBeanInfo = grammar.getBeanInfo(localObject, true);
        if ((jaxbType == Object.class) && (domHandler != null)) {
          paramXMLSerializer.writeDom(localObject, domHandler, paramBeanT, fieldName);
        } else {
          localJaxBeanInfo.serializeRoot(localObject, paramXMLSerializer);
        }
      }
      catch (JAXBException localJAXBException)
      {
        paramXMLSerializer.reportError(fieldName, localJAXBException);
      }
    }
  }
  
  public void buildChildElementUnmarshallers(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap)
  {
    Iterator localIterator = expectedElements.entrySet().iterator();
    while (localIterator.hasNext())
    {
      QNameMap.Entry localEntry = (QNameMap.Entry)localIterator.next();
      paramQNameMap.put(nsUri, localName, new ChildLoader(((JaxBeanInfo)localEntry.getValue()).getLoader(context, true), acc));
    }
    if (domHandler != null) {
      paramQNameMap.put(CATCH_ALL, new ChildLoader(new WildcardLoader(domHandler, wcMode), acc));
    }
  }
  
  public PropertyKind getKind()
  {
    return PropertyKind.REFERENCE;
  }
  
  public Accessor getElementPropertyAccessor(String paramString1, String paramString2)
  {
    JaxBeanInfo localJaxBeanInfo = (JaxBeanInfo)expectedElements.get(paramString1, paramString2);
    if (localJaxBeanInfo != null)
    {
      if ((localJaxBeanInfo instanceof ElementBeanInfoImpl))
      {
        final ElementBeanInfoImpl localElementBeanInfoImpl = (ElementBeanInfoImpl)localJaxBeanInfo;
        new Accessor(expectedType)
        {
          public Object get(BeanT paramAnonymousBeanT)
            throws AccessorException
          {
            Object localObject = acc.get(paramAnonymousBeanT);
            if ((localObject instanceof JAXBElement)) {
              return ((JAXBElement)localObject).getValue();
            }
            return localObject;
          }
          
          public void set(BeanT paramAnonymousBeanT, Object paramAnonymousObject)
            throws AccessorException
          {
            if (paramAnonymousObject != null) {
              try
              {
                paramAnonymousObject = localElementBeanInfoImpl.createInstanceFromValue(paramAnonymousObject);
              }
              catch (IllegalAccessException localIllegalAccessException)
              {
                throw new AccessorException(localIllegalAccessException);
              }
              catch (InvocationTargetException localInvocationTargetException)
              {
                throw new AccessorException(localInvocationTargetException);
              }
              catch (InstantiationException localInstantiationException)
              {
                throw new AccessorException(localInstantiationException);
              }
            }
            acc.set(paramAnonymousBeanT, paramAnonymousObject);
          }
        };
      }
      return acc;
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\SingleReferenceNodeProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
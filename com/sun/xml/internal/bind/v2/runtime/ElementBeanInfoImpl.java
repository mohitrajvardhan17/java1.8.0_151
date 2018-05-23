package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.property.Property;
import com.sun.xml.internal.bind.v2.runtime.property.PropertyFactory;
import com.sun.xml.internal.bind.v2.runtime.property.UnmarshallerChain;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Discarder;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Intercepter;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext.State;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import com.sun.xml.internal.bind.v2.util.QNameMap.Entry;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBElement.GlobalScope;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class ElementBeanInfoImpl
  extends JaxBeanInfo<JAXBElement>
{
  private Loader loader;
  private final Property property;
  private final QName tagName;
  public final Class expectedType;
  private final Class scope;
  private final Constructor<? extends JAXBElement> constructor;
  
  ElementBeanInfoImpl(JAXBContextImpl paramJAXBContextImpl, RuntimeElementInfo paramRuntimeElementInfo)
  {
    super(paramJAXBContextImpl, paramRuntimeElementInfo, paramRuntimeElementInfo.getType(), true, false, true);
    property = PropertyFactory.create(paramJAXBContextImpl, paramRuntimeElementInfo.getProperty());
    tagName = paramRuntimeElementInfo.getElementName();
    expectedType = ((Class)Utils.REFLECTION_NAVIGATOR.erasure(paramRuntimeElementInfo.getContentInMemoryType()));
    scope = (paramRuntimeElementInfo.getScope() == null ? JAXBElement.GlobalScope.class : (Class)paramRuntimeElementInfo.getScope().getClazz());
    Class localClass = (Class)Utils.REFLECTION_NAVIGATOR.erasure(paramRuntimeElementInfo.getType());
    if (localClass == JAXBElement.class) {
      constructor = null;
    } else {
      try
      {
        constructor = localClass.getConstructor(new Class[] { expectedType });
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        NoSuchMethodError localNoSuchMethodError = new NoSuchMethodError("Failed to find the constructor for " + localClass + " with " + expectedType);
        localNoSuchMethodError.initCause(localNoSuchMethodException);
        throw localNoSuchMethodError;
      }
    }
  }
  
  protected ElementBeanInfoImpl(final JAXBContextImpl paramJAXBContextImpl)
  {
    super(paramJAXBContextImpl, null, JAXBElement.class, true, false, true);
    tagName = null;
    expectedType = null;
    scope = null;
    constructor = null;
    property = new Property()
    {
      public void reset(JAXBElement paramAnonymousJAXBElement)
      {
        throw new UnsupportedOperationException();
      }
      
      public void serializeBody(JAXBElement paramAnonymousJAXBElement, XMLSerializer paramAnonymousXMLSerializer, Object paramAnonymousObject)
        throws SAXException, IOException, XMLStreamException
      {
        Class localClass = paramAnonymousJAXBElement.getScope();
        if (paramAnonymousJAXBElement.isGlobalScope()) {
          localClass = null;
        }
        QName localQName = paramAnonymousJAXBElement.getName();
        ElementBeanInfoImpl localElementBeanInfoImpl = paramJAXBContextImpl.getElement(localClass, localQName);
        if (localElementBeanInfoImpl == null)
        {
          JaxBeanInfo localJaxBeanInfo;
          try
          {
            localJaxBeanInfo = paramJAXBContextImpl.getBeanInfo(paramAnonymousJAXBElement.getDeclaredType(), true);
          }
          catch (JAXBException localJAXBException)
          {
            paramAnonymousXMLSerializer.reportError(null, localJAXBException);
            return;
          }
          Object localObject = paramAnonymousJAXBElement.getValue();
          paramAnonymousXMLSerializer.startElement(localQName.getNamespaceURI(), localQName.getLocalPart(), localQName.getPrefix(), null);
          if (localObject == null) {
            paramAnonymousXMLSerializer.writeXsiNilTrue();
          } else {
            paramAnonymousXMLSerializer.childAsXsiType(localObject, "value", localJaxBeanInfo, false);
          }
          paramAnonymousXMLSerializer.endElement();
        }
        else
        {
          try
          {
            property.serializeBody(paramAnonymousJAXBElement, paramAnonymousXMLSerializer, paramAnonymousJAXBElement);
          }
          catch (AccessorException localAccessorException)
          {
            paramAnonymousXMLSerializer.reportError(null, localAccessorException);
          }
        }
      }
      
      public void serializeURIs(JAXBElement paramAnonymousJAXBElement, XMLSerializer paramAnonymousXMLSerializer) {}
      
      public boolean hasSerializeURIAction()
      {
        return false;
      }
      
      public String getIdValue(JAXBElement paramAnonymousJAXBElement)
      {
        return null;
      }
      
      public PropertyKind getKind()
      {
        return PropertyKind.ELEMENT;
      }
      
      public void buildChildElementUnmarshallers(UnmarshallerChain paramAnonymousUnmarshallerChain, QNameMap<ChildLoader> paramAnonymousQNameMap) {}
      
      public Accessor getElementPropertyAccessor(String paramAnonymousString1, String paramAnonymousString2)
      {
        throw new UnsupportedOperationException();
      }
      
      public void wrapUp() {}
      
      public RuntimePropertyInfo getInfo()
      {
        return property.getInfo();
      }
      
      public boolean isHiddenByOverride()
      {
        return false;
      }
      
      public void setHiddenByOverride(boolean paramAnonymousBoolean)
      {
        throw new UnsupportedOperationException("Not supported on jaxbelements.");
      }
      
      public String getFieldName()
      {
        return null;
      }
    };
  }
  
  public String getElementNamespaceURI(JAXBElement paramJAXBElement)
  {
    return paramJAXBElement.getName().getNamespaceURI();
  }
  
  public String getElementLocalName(JAXBElement paramJAXBElement)
  {
    return paramJAXBElement.getName().getLocalPart();
  }
  
  public Loader getLoader(JAXBContextImpl paramJAXBContextImpl, boolean paramBoolean)
  {
    if (loader == null)
    {
      UnmarshallerChain localUnmarshallerChain = new UnmarshallerChain(paramJAXBContextImpl);
      QNameMap localQNameMap = new QNameMap();
      property.buildChildElementUnmarshallers(localUnmarshallerChain, localQNameMap);
      if (localQNameMap.size() == 1) {
        loader = new IntercepterLoader(getOnegetValueloader);
      } else {
        loader = Discarder.INSTANCE;
      }
    }
    return loader;
  }
  
  public final JAXBElement createInstance(UnmarshallingContext paramUnmarshallingContext)
    throws IllegalAccessException, InvocationTargetException, InstantiationException
  {
    return createInstanceFromValue(null);
  }
  
  public final JAXBElement createInstanceFromValue(Object paramObject)
    throws IllegalAccessException, InvocationTargetException, InstantiationException
  {
    if (constructor == null) {
      return new JAXBElement(tagName, expectedType, scope, paramObject);
    }
    return (JAXBElement)constructor.newInstance(new Object[] { paramObject });
  }
  
  public boolean reset(JAXBElement paramJAXBElement, UnmarshallingContext paramUnmarshallingContext)
  {
    paramJAXBElement.setValue(null);
    return true;
  }
  
  public String getId(JAXBElement paramJAXBElement, XMLSerializer paramXMLSerializer)
  {
    Object localObject = paramJAXBElement.getValue();
    if ((localObject instanceof String)) {
      return (String)localObject;
    }
    return null;
  }
  
  public void serializeBody(JAXBElement paramJAXBElement, XMLSerializer paramXMLSerializer)
    throws SAXException, IOException, XMLStreamException
  {
    try
    {
      property.serializeBody(paramJAXBElement, paramXMLSerializer, null);
    }
    catch (AccessorException localAccessorException)
    {
      paramXMLSerializer.reportError(null, localAccessorException);
    }
  }
  
  public void serializeRoot(JAXBElement paramJAXBElement, XMLSerializer paramXMLSerializer)
    throws SAXException, IOException, XMLStreamException
  {
    serializeBody(paramJAXBElement, paramXMLSerializer);
  }
  
  public void serializeAttributes(JAXBElement paramJAXBElement, XMLSerializer paramXMLSerializer) {}
  
  public void serializeURIs(JAXBElement paramJAXBElement, XMLSerializer paramXMLSerializer) {}
  
  public final Transducer<JAXBElement> getTransducer()
  {
    return null;
  }
  
  public void wrapUp()
  {
    super.wrapUp();
    property.wrapUp();
  }
  
  public void link(JAXBContextImpl paramJAXBContextImpl)
  {
    super.link(paramJAXBContextImpl);
    getLoader(paramJAXBContextImpl, true);
  }
  
  private final class IntercepterLoader
    extends Loader
    implements Intercepter
  {
    private final Loader core;
    
    public IntercepterLoader(Loader paramLoader)
    {
      core = paramLoader;
    }
    
    public final void startElement(UnmarshallingContext.State paramState, TagName paramTagName)
      throws SAXException
    {
      paramState.setLoader(core);
      paramState.setIntercepter(this);
      UnmarshallingContext localUnmarshallingContext = paramState.getContext();
      Object localObject = localUnmarshallingContext.getOuterPeer();
      if ((localObject != null) && (jaxbType != localObject.getClass())) {
        localObject = null;
      }
      if (localObject != null) {
        reset((JAXBElement)localObject, localUnmarshallingContext);
      }
      if (localObject == null) {
        localObject = localUnmarshallingContext.createInstance(ElementBeanInfoImpl.this);
      }
      fireBeforeUnmarshal(ElementBeanInfoImpl.this, localObject, paramState);
      localUnmarshallingContext.recordOuterPeer(localObject);
      UnmarshallingContext.State localState = paramState.getPrev();
      localState.setBackup(localState.getTarget());
      localState.setTarget(localObject);
      core.startElement(paramState, paramTagName);
    }
    
    public Object intercept(UnmarshallingContext.State paramState, Object paramObject)
      throws SAXException
    {
      JAXBElement localJAXBElement = (JAXBElement)paramState.getTarget();
      paramState.setTarget(paramState.getBackup());
      paramState.setBackup(null);
      if (paramState.isNil())
      {
        localJAXBElement.setNil(true);
        paramState.setNil(false);
      }
      if (paramObject != null) {
        localJAXBElement.setValue(paramObject);
      }
      fireAfterUnmarshal(ElementBeanInfoImpl.this, localJAXBElement, paramState);
      return localJAXBElement;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\ElementBeanInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
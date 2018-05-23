package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeMapPropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.NameBuilder;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext.State;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class SingleMapNodeProperty<BeanT, ValueT extends Map>
  extends PropertyImpl<BeanT>
{
  private final Accessor<BeanT, ValueT> acc;
  private final Name tagName;
  private final Name entryTag;
  private final Name keyTag;
  private final Name valueTag;
  private final boolean nillable;
  private JaxBeanInfo keyBeanInfo;
  private JaxBeanInfo valueBeanInfo;
  private final Class<? extends ValueT> mapImplClass;
  private static final Class[] knownImplClasses = { HashMap.class, TreeMap.class, LinkedHashMap.class };
  private Loader keyLoader;
  private Loader valueLoader;
  private final Loader itemsLoader = new Loader(false)
  {
    private ThreadLocal<BeanT> target = new ThreadLocal();
    private ThreadLocal<ValueT> map = new ThreadLocal();
    private int depthCounter = 0;
    
    public void startElement(UnmarshallingContext.State paramAnonymousState, TagName paramAnonymousTagName)
      throws SAXException
    {
      try
      {
        target.set(paramAnonymousState.getPrev().getTarget());
        map.set(acc.get(target.get()));
        depthCounter += 1;
        if (map.get() == null) {
          map.set(ClassFactory.create(mapImplClass));
        }
        ((Map)map.get()).clear();
        paramAnonymousState.setTarget(map.get());
      }
      catch (AccessorException localAccessorException)
      {
        handleGenericException(localAccessorException, true);
        paramAnonymousState.setTarget(new HashMap());
      }
    }
    
    public void leaveElement(UnmarshallingContext.State paramAnonymousState, TagName paramAnonymousTagName)
      throws SAXException
    {
      super.leaveElement(paramAnonymousState, paramAnonymousTagName);
      try
      {
        acc.set(target.get(), map.get());
        if (--depthCounter == 0)
        {
          target.remove();
          map.remove();
        }
      }
      catch (AccessorException localAccessorException)
      {
        handleGenericException(localAccessorException, true);
      }
    }
    
    public void childElement(UnmarshallingContext.State paramAnonymousState, TagName paramAnonymousTagName)
      throws SAXException
    {
      if (paramAnonymousTagName.matches(entryTag)) {
        paramAnonymousState.setLoader(entryLoader);
      } else {
        super.childElement(paramAnonymousState, paramAnonymousTagName);
      }
    }
    
    public Collection<QName> getExpectedChildElements()
    {
      return Collections.singleton(entryTag.toQName());
    }
  };
  private final Loader entryLoader = new Loader(false)
  {
    public void startElement(UnmarshallingContext.State paramAnonymousState, TagName paramAnonymousTagName)
    {
      paramAnonymousState.setTarget(new Object[2]);
    }
    
    public void leaveElement(UnmarshallingContext.State paramAnonymousState, TagName paramAnonymousTagName)
    {
      Object[] arrayOfObject = (Object[])paramAnonymousState.getTarget();
      Map localMap = (Map)paramAnonymousState.getPrev().getTarget();
      localMap.put(arrayOfObject[0], arrayOfObject[1]);
    }
    
    public void childElement(UnmarshallingContext.State paramAnonymousState, TagName paramAnonymousTagName)
      throws SAXException
    {
      if (paramAnonymousTagName.matches(keyTag))
      {
        paramAnonymousState.setLoader(keyLoader);
        paramAnonymousState.setReceiver(SingleMapNodeProperty.keyReceiver);
        return;
      }
      if (paramAnonymousTagName.matches(valueTag))
      {
        paramAnonymousState.setLoader(valueLoader);
        paramAnonymousState.setReceiver(SingleMapNodeProperty.valueReceiver);
        return;
      }
      super.childElement(paramAnonymousState, paramAnonymousTagName);
    }
    
    public Collection<QName> getExpectedChildElements()
    {
      return Arrays.asList(new QName[] { keyTag.toQName(), valueTag.toQName() });
    }
  };
  private static final Receiver keyReceiver = new ReceiverImpl(0);
  private static final Receiver valueReceiver = new ReceiverImpl(1);
  
  public SingleMapNodeProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeMapPropertyInfo paramRuntimeMapPropertyInfo)
  {
    super(paramJAXBContextImpl, paramRuntimeMapPropertyInfo);
    acc = paramRuntimeMapPropertyInfo.getAccessor().optimize(paramJAXBContextImpl);
    tagName = nameBuilder.createElementName(paramRuntimeMapPropertyInfo.getXmlName());
    entryTag = nameBuilder.createElementName("", "entry");
    keyTag = nameBuilder.createElementName("", "key");
    valueTag = nameBuilder.createElementName("", "value");
    nillable = paramRuntimeMapPropertyInfo.isCollectionNillable();
    keyBeanInfo = paramJAXBContextImpl.getOrCreate(paramRuntimeMapPropertyInfo.getKeyType());
    valueBeanInfo = paramJAXBContextImpl.getOrCreate(paramRuntimeMapPropertyInfo.getValueType());
    Class localClass = (Class)Utils.REFLECTION_NAVIGATOR.erasure(paramRuntimeMapPropertyInfo.getRawType());
    mapImplClass = ClassFactory.inferImplClass(localClass, knownImplClasses);
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
  
  public PropertyKind getKind()
  {
    return PropertyKind.MAP;
  }
  
  public void buildChildElementUnmarshallers(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap)
  {
    keyLoader = keyBeanInfo.getLoader(context, true);
    valueLoader = valueBeanInfo.getLoader(context, true);
    paramQNameMap.put(tagName, new ChildLoader(itemsLoader, null));
  }
  
  public void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, Object paramObject)
    throws SAXException, AccessorException, IOException, XMLStreamException
  {
    Map localMap = (Map)acc.get(paramBeanT);
    if (localMap != null)
    {
      bareStartTag(paramXMLSerializer, tagName, localMap);
      Iterator localIterator = localMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        bareStartTag(paramXMLSerializer, entryTag, null);
        Object localObject1 = localEntry.getKey();
        if (localObject1 != null)
        {
          paramXMLSerializer.startElement(keyTag, localObject1);
          paramXMLSerializer.childAsXsiType(localObject1, fieldName, keyBeanInfo, false);
          paramXMLSerializer.endElement();
        }
        Object localObject2 = localEntry.getValue();
        if (localObject2 != null)
        {
          paramXMLSerializer.startElement(valueTag, localObject2);
          paramXMLSerializer.childAsXsiType(localObject2, fieldName, valueBeanInfo, false);
          paramXMLSerializer.endElement();
        }
        paramXMLSerializer.endElement();
      }
      paramXMLSerializer.endElement();
    }
    else if (nillable)
    {
      paramXMLSerializer.startElement(tagName, null);
      paramXMLSerializer.writeXsiNilTrue();
      paramXMLSerializer.endElement();
    }
  }
  
  private void bareStartTag(XMLSerializer paramXMLSerializer, Name paramName, Object paramObject)
    throws IOException, XMLStreamException, SAXException
  {
    paramXMLSerializer.startElement(paramName, paramObject);
    paramXMLSerializer.endNamespaceDecls(paramObject);
    paramXMLSerializer.endAttributes();
  }
  
  public Accessor getElementPropertyAccessor(String paramString1, String paramString2)
  {
    if (tagName.equals(paramString1, paramString2)) {
      return acc;
    }
    return null;
  }
  
  private static final class ReceiverImpl
    implements Receiver
  {
    private final int index;
    
    public ReceiverImpl(int paramInt)
    {
      index = paramInt;
    }
    
    public void receive(UnmarshallingContext.State paramState, Object paramObject)
    {
      ((Object[])paramState.getTarget())[index] = paramObject;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\SingleMapNodeProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
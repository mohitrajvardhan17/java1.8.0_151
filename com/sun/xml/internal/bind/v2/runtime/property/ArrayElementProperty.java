package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.NameBuilder;
import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister.IDREFSIterator;
import com.sun.xml.internal.bind.v2.runtime.reflect.NullSafeAccessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TextLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

abstract class ArrayElementProperty<BeanT, ListT, ItemT>
  extends ArrayERProperty<BeanT, ListT, ItemT>
{
  private final Map<Class, TagAndType> typeMap = new HashMap();
  private Map<TypeRef<Type, Class>, JaxBeanInfo> refs = new HashMap();
  protected RuntimeElementPropertyInfo prop;
  private final Name nillableTagName;
  
  protected ArrayElementProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeElementPropertyInfo paramRuntimeElementPropertyInfo)
  {
    super(paramJAXBContextImpl, paramRuntimeElementPropertyInfo, paramRuntimeElementPropertyInfo.getXmlName(), paramRuntimeElementPropertyInfo.isCollectionNillable());
    prop = paramRuntimeElementPropertyInfo;
    List localList = paramRuntimeElementPropertyInfo.getTypes();
    Name localName = null;
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      RuntimeTypeRef localRuntimeTypeRef = (RuntimeTypeRef)localIterator.next();
      Class localClass = (Class)localRuntimeTypeRef.getTarget().getType();
      if (localClass.isPrimitive()) {
        localClass = (Class)RuntimeUtil.primitiveToBox.get(localClass);
      }
      JaxBeanInfo localJaxBeanInfo = paramJAXBContextImpl.getOrCreate(localRuntimeTypeRef.getTarget());
      TagAndType localTagAndType = new TagAndType(nameBuilder.createElementName(localRuntimeTypeRef.getTagName()), localJaxBeanInfo);
      typeMap.put(localClass, localTagAndType);
      refs.put(localRuntimeTypeRef, localJaxBeanInfo);
      if ((localRuntimeTypeRef.isNillable()) && (localName == null)) {
        localName = tagName;
      }
    }
    nillableTagName = localName;
  }
  
  public void wrapUp()
  {
    super.wrapUp();
    refs = null;
    prop = null;
  }
  
  protected void serializeListBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, ListT paramListT)
    throws IOException, XMLStreamException, SAXException, AccessorException
  {
    ListIterator localListIterator = lister.iterator(paramListT, paramXMLSerializer);
    boolean bool = localListIterator instanceof Lister.IDREFSIterator;
    while (localListIterator.hasNext()) {
      try
      {
        Object localObject = localListIterator.next();
        if (localObject != null)
        {
          Class localClass = localObject.getClass();
          if (bool) {
            localClass = ((Lister.IDREFSIterator)localListIterator).last().getClass();
          }
          for (TagAndType localTagAndType = (TagAndType)typeMap.get(localClass); (localTagAndType == null) && (localClass != null); localTagAndType = (TagAndType)typeMap.get(localClass)) {
            localClass = localClass.getSuperclass();
          }
          if (localTagAndType == null)
          {
            paramXMLSerializer.startElement(typeMap.values().iterator().next()).tagName, null);
            paramXMLSerializer.childAsXsiType(localObject, fieldName, grammar.getBeanInfo(Object.class), false);
          }
          else
          {
            paramXMLSerializer.startElement(tagName, null);
            serializeItem(beanInfo, localObject, paramXMLSerializer);
          }
          paramXMLSerializer.endElement();
        }
        else if (nillableTagName != null)
        {
          paramXMLSerializer.startElement(nillableTagName, null);
          paramXMLSerializer.writeXsiNilTrue();
          paramXMLSerializer.endElement();
        }
      }
      catch (JAXBException localJAXBException)
      {
        paramXMLSerializer.reportError(fieldName, localJAXBException);
      }
    }
  }
  
  protected abstract void serializeItem(JaxBeanInfo paramJaxBeanInfo, ItemT paramItemT, XMLSerializer paramXMLSerializer)
    throws SAXException, AccessorException, IOException, XMLStreamException;
  
  public void createBodyUnmarshaller(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap)
  {
    int i = paramUnmarshallerChain.allocateOffset();
    ArrayERProperty.ReceiverImpl localReceiverImpl = new ArrayERProperty.ReceiverImpl(this, i);
    Iterator localIterator = prop.getTypes().iterator();
    while (localIterator.hasNext())
    {
      RuntimeTypeRef localRuntimeTypeRef = (RuntimeTypeRef)localIterator.next();
      Name localName = context.nameBuilder.createElementName(localRuntimeTypeRef.getTagName());
      Object localObject = createItemUnmarshaller(paramUnmarshallerChain, localRuntimeTypeRef);
      if ((localRuntimeTypeRef.isNillable()) || (context.allNillable)) {
        localObject = new XsiNilLoader.Array((Loader)localObject);
      }
      if (localRuntimeTypeRef.getDefaultValue() != null) {
        localObject = new DefaultValueLoaderDecorator((Loader)localObject, localRuntimeTypeRef.getDefaultValue());
      }
      paramQNameMap.put(localName, new ChildLoader((Loader)localObject, localReceiverImpl));
    }
  }
  
  public final PropertyKind getKind()
  {
    return PropertyKind.ELEMENT;
  }
  
  private Loader createItemUnmarshaller(UnmarshallerChain paramUnmarshallerChain, RuntimeTypeRef paramRuntimeTypeRef)
  {
    if (PropertyFactory.isLeaf(paramRuntimeTypeRef.getSource()))
    {
      Transducer localTransducer = paramRuntimeTypeRef.getTransducer();
      return new TextLoader(localTransducer);
    }
    return ((JaxBeanInfo)refs.get(paramRuntimeTypeRef)).getLoader(context, true);
  }
  
  public Accessor getElementPropertyAccessor(String paramString1, String paramString2)
  {
    if (wrapperTagName != null)
    {
      if (wrapperTagName.equals(paramString1, paramString2)) {
        return acc;
      }
    }
    else
    {
      Iterator localIterator = typeMap.values().iterator();
      while (localIterator.hasNext())
      {
        TagAndType localTagAndType = (TagAndType)localIterator.next();
        if (tagName.equals(paramString1, paramString2)) {
          return new NullSafeAccessor(acc, lister);
        }
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\ArrayElementProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
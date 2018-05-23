package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.ClassBeanInfoImpl;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.property.AttributeProperty;
import com.sun.xml.internal.bind.v2.runtime.property.Property;
import com.sun.xml.internal.bind.v2.runtime.property.StructureLoaderBuilder;
import com.sun.xml.internal.bind.v2.runtime.property.UnmarshallerChain;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public final class StructureLoader
  extends Loader
{
  private final QNameMap<ChildLoader> childUnmarshallers = new QNameMap();
  private ChildLoader catchAll;
  private ChildLoader textHandler;
  private QNameMap<TransducedAccessor> attUnmarshallers;
  private Accessor<Object, Map<QName, String>> attCatchAll;
  private final JaxBeanInfo beanInfo;
  private int frameSize;
  private static final QNameMap<TransducedAccessor> EMPTY = new QNameMap();
  
  public StructureLoader(ClassBeanInfoImpl paramClassBeanInfoImpl)
  {
    super(true);
    beanInfo = paramClassBeanInfoImpl;
  }
  
  public void init(JAXBContextImpl paramJAXBContextImpl, ClassBeanInfoImpl paramClassBeanInfoImpl, Accessor<?, Map<QName, String>> paramAccessor)
  {
    UnmarshallerChain localUnmarshallerChain = new UnmarshallerChain(paramJAXBContextImpl);
    for (ClassBeanInfoImpl localClassBeanInfoImpl = paramClassBeanInfoImpl; localClassBeanInfoImpl != null; localClassBeanInfoImpl = superClazz) {
      for (int i = properties.length - 1; i >= 0; i--)
      {
        Property localProperty = properties[i];
        switch (localProperty.getKind())
        {
        case ATTRIBUTE: 
          if (attUnmarshallers == null) {
            attUnmarshallers = new QNameMap();
          }
          AttributeProperty localAttributeProperty = (AttributeProperty)localProperty;
          attUnmarshallers.put(attName.toQName(), xacc);
          break;
        case ELEMENT: 
        case REFERENCE: 
        case MAP: 
        case VALUE: 
          localProperty.buildChildElementUnmarshallers(localUnmarshallerChain, childUnmarshallers);
        }
      }
    }
    frameSize = localUnmarshallerChain.getScopeSize();
    textHandler = ((ChildLoader)childUnmarshallers.get(StructureLoaderBuilder.TEXT_HANDLER));
    catchAll = ((ChildLoader)childUnmarshallers.get(StructureLoaderBuilder.CATCH_ALL));
    if (paramAccessor != null)
    {
      attCatchAll = paramAccessor;
      if (attUnmarshallers == null) {
        attUnmarshallers = EMPTY;
      }
    }
    else
    {
      attCatchAll = null;
    }
  }
  
  public void startElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    UnmarshallingContext localUnmarshallingContext = paramState.getContext();
    assert (!beanInfo.isImmutable());
    Object localObject1 = localUnmarshallingContext.getInnerPeer();
    if ((localObject1 != null) && (beanInfo.jaxbType != localObject1.getClass())) {
      localObject1 = null;
    }
    if (localObject1 != null) {
      beanInfo.reset(localObject1, localUnmarshallingContext);
    }
    if (localObject1 == null) {
      localObject1 = localUnmarshallingContext.createInstance(beanInfo);
    }
    localUnmarshallingContext.recordInnerPeer(localObject1);
    paramState.setTarget(localObject1);
    fireBeforeUnmarshal(beanInfo, localObject1, paramState);
    localUnmarshallingContext.startScope(frameSize);
    if (attUnmarshallers != null)
    {
      Attributes localAttributes = atts;
      for (int i = 0; i < localAttributes.getLength(); i++)
      {
        String str1 = localAttributes.getURI(i);
        String str2 = localAttributes.getLocalName(i);
        if ("".equals(str2)) {
          str2 = localAttributes.getQName(i);
        }
        String str3 = localAttributes.getValue(i);
        TransducedAccessor localTransducedAccessor = (TransducedAccessor)attUnmarshallers.get(str1, str2);
        try
        {
          if (localTransducedAccessor != null)
          {
            localTransducedAccessor.parse(localObject1, str3);
          }
          else if (attCatchAll != null)
          {
            String str4 = localAttributes.getQName(i);
            if (localAttributes.getURI(i).equals("http://www.w3.org/2001/XMLSchema-instance")) {
              continue;
            }
            Object localObject2 = paramState.getTarget();
            Object localObject3 = (Map)attCatchAll.get(localObject2);
            if (localObject3 == null)
            {
              if (attCatchAll.valueType.isAssignableFrom(HashMap.class))
              {
                localObject3 = new HashMap();
              }
              else
              {
                localUnmarshallingContext.handleError(Messages.UNABLE_TO_CREATE_MAP.format(new Object[] { attCatchAll.valueType }));
                return;
              }
              attCatchAll.set(localObject2, localObject3);
            }
            int j = str4.indexOf(':');
            String str5;
            if (j < 0) {
              str5 = "";
            } else {
              str5 = str4.substring(0, j);
            }
            ((Map)localObject3).put(new QName(str1, str2, str5), str3);
          }
        }
        catch (AccessorException localAccessorException)
        {
          handleGenericException(localAccessorException, true);
        }
      }
    }
  }
  
  public void childElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    ChildLoader localChildLoader = (ChildLoader)childUnmarshallers.get(uri, local);
    if (localChildLoader == null)
    {
      localChildLoader = catchAll;
      if (localChildLoader == null)
      {
        super.childElement(paramState, paramTagName);
        return;
      }
    }
    paramState.setLoader(loader);
    paramState.setReceiver(receiver);
  }
  
  public Collection<QName> getExpectedChildElements()
  {
    return childUnmarshallers.keySet();
  }
  
  public Collection<QName> getExpectedAttributes()
  {
    return attUnmarshallers.keySet();
  }
  
  public void text(UnmarshallingContext.State paramState, CharSequence paramCharSequence)
    throws SAXException
  {
    if (textHandler != null) {
      textHandler.loader.text(paramState, paramCharSequence);
    }
  }
  
  public void leaveElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    paramState.getContext().endScope(frameSize);
    fireAfterUnmarshal(beanInfo, paramState.getTarget(), paramState.getPrev());
  }
  
  public JaxBeanInfo getBeanInfo()
  {
    return beanInfo;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\StructureLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
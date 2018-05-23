package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.NameBuilder;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Scope;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext.State;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.util.Collection;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

abstract class ArrayERProperty<BeanT, ListT, ItemT>
  extends ArrayProperty<BeanT, ListT, ItemT>
{
  protected final Name wrapperTagName;
  protected final boolean isWrapperNillable;
  
  protected ArrayERProperty(JAXBContextImpl paramJAXBContextImpl, RuntimePropertyInfo paramRuntimePropertyInfo, QName paramQName, boolean paramBoolean)
  {
    super(paramJAXBContextImpl, paramRuntimePropertyInfo);
    if (paramQName == null) {
      wrapperTagName = null;
    } else {
      wrapperTagName = nameBuilder.createElementName(paramQName);
    }
    isWrapperNillable = paramBoolean;
  }
  
  public final void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, Object paramObject)
    throws SAXException, AccessorException, IOException, XMLStreamException
  {
    Object localObject = acc.get(paramBeanT);
    if (localObject != null)
    {
      if (wrapperTagName != null)
      {
        paramXMLSerializer.startElement(wrapperTagName, null);
        paramXMLSerializer.endNamespaceDecls(localObject);
        paramXMLSerializer.endAttributes();
      }
      serializeListBody(paramBeanT, paramXMLSerializer, localObject);
      if (wrapperTagName != null) {
        paramXMLSerializer.endElement();
      }
    }
    else if (isWrapperNillable)
    {
      paramXMLSerializer.startElement(wrapperTagName, null);
      paramXMLSerializer.writeXsiNilTrue();
      paramXMLSerializer.endElement();
    }
  }
  
  protected abstract void serializeListBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, ListT paramListT)
    throws IOException, XMLStreamException, SAXException, AccessorException;
  
  protected abstract void createBodyUnmarshaller(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap);
  
  public final void buildChildElementUnmarshallers(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap)
  {
    if (wrapperTagName != null)
    {
      UnmarshallerChain localUnmarshallerChain = new UnmarshallerChain(context);
      QNameMap localQNameMap = new QNameMap();
      createBodyUnmarshaller(localUnmarshallerChain, localQNameMap);
      Object localObject = new ItemsLoader(acc, lister, localQNameMap);
      if ((isWrapperNillable) || (context.allNillable)) {
        localObject = new XsiNilLoader((Loader)localObject);
      }
      paramQNameMap.put(wrapperTagName, new ChildLoader((Loader)localObject, null));
    }
    else
    {
      createBodyUnmarshaller(paramUnmarshallerChain, paramQNameMap);
    }
  }
  
  private static final class ItemsLoader
    extends Loader
  {
    private final Accessor acc;
    private final Lister lister;
    private final QNameMap<ChildLoader> children;
    
    public ItemsLoader(Accessor paramAccessor, Lister paramLister, QNameMap<ChildLoader> paramQNameMap)
    {
      super();
      acc = paramAccessor;
      lister = paramLister;
      children = paramQNameMap;
    }
    
    public void startElement(UnmarshallingContext.State paramState, TagName paramTagName)
      throws SAXException
    {
      UnmarshallingContext localUnmarshallingContext = paramState.getContext();
      localUnmarshallingContext.startScope(1);
      paramState.setTarget(paramState.getPrev().getTarget());
      localUnmarshallingContext.getScope(0).start(acc, lister);
    }
    
    public void childElement(UnmarshallingContext.State paramState, TagName paramTagName)
      throws SAXException
    {
      ChildLoader localChildLoader = (ChildLoader)children.get(uri, local);
      if (localChildLoader == null) {
        localChildLoader = (ChildLoader)children.get(StructureLoaderBuilder.CATCH_ALL);
      }
      if (localChildLoader == null)
      {
        super.childElement(paramState, paramTagName);
        return;
      }
      paramState.setLoader(loader);
      paramState.setReceiver(receiver);
    }
    
    public void leaveElement(UnmarshallingContext.State paramState, TagName paramTagName)
      throws SAXException
    {
      paramState.getContext().endScope(1);
    }
    
    public Collection<QName> getExpectedChildElements()
    {
      return children.keySet();
    }
  }
  
  protected final class ReceiverImpl
    implements Receiver
  {
    private final int offset;
    
    protected ReceiverImpl(int paramInt)
    {
      offset = paramInt;
    }
    
    public void receive(UnmarshallingContext.State paramState, Object paramObject)
      throws SAXException
    {
      paramState.getContext().getScope(offset).add(acc, lister, paramObject);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\ArrayERProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
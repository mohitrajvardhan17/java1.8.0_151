package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.SAXException2;
import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.impl.RuntimeModelBuilder;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElementRef;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.OptimizedTransducedAccessorFactory;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx.Snapshot;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Patcher;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public abstract class TransducedAccessor<BeanT>
{
  public TransducedAccessor() {}
  
  public boolean useNamespace()
  {
    return false;
  }
  
  public void declareNamespace(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
    throws AccessorException, SAXException
  {}
  
  @Nullable
  public abstract CharSequence print(@NotNull BeanT paramBeanT)
    throws AccessorException, SAXException;
  
  public abstract void parse(BeanT paramBeanT, CharSequence paramCharSequence)
    throws AccessorException, SAXException;
  
  public abstract boolean hasValue(BeanT paramBeanT)
    throws AccessorException;
  
  public static <T> TransducedAccessor<T> get(JAXBContextImpl paramJAXBContextImpl, RuntimeNonElementRef paramRuntimeNonElementRef)
  {
    Transducer localTransducer = RuntimeModelBuilder.createTransducer(paramRuntimeNonElementRef);
    RuntimePropertyInfo localRuntimePropertyInfo = paramRuntimeNonElementRef.getSource();
    if (localRuntimePropertyInfo.isCollection()) {
      return new ListTransducedAccessorImpl(localTransducer, localRuntimePropertyInfo.getAccessor(), Lister.create((Type)Utils.REFLECTION_NAVIGATOR.erasure(localRuntimePropertyInfo.getRawType()), localRuntimePropertyInfo.id(), localRuntimePropertyInfo.getAdapter()));
    }
    if (localRuntimePropertyInfo.id() == ID.IDREF) {
      return new IDREFTransducedAccessorImpl(localRuntimePropertyInfo.getAccessor());
    }
    if ((localTransducer.isDefault()) && (paramJAXBContextImpl != null) && (!fastBoot))
    {
      TransducedAccessor localTransducedAccessor = OptimizedTransducedAccessorFactory.get(localRuntimePropertyInfo);
      if (localTransducedAccessor != null) {
        return localTransducedAccessor;
      }
    }
    if (localTransducer.useNamespace()) {
      return new CompositeContextDependentTransducedAccessorImpl(paramJAXBContextImpl, localTransducer, localRuntimePropertyInfo.getAccessor());
    }
    return new CompositeTransducedAccessorImpl(paramJAXBContextImpl, localTransducer, localRuntimePropertyInfo.getAccessor());
  }
  
  public abstract void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, BeanT paramBeanT, String paramString)
    throws SAXException, AccessorException, IOException, XMLStreamException;
  
  public abstract void writeText(XMLSerializer paramXMLSerializer, BeanT paramBeanT, String paramString)
    throws AccessorException, SAXException, IOException, XMLStreamException;
  
  static class CompositeContextDependentTransducedAccessorImpl<BeanT, ValueT>
    extends TransducedAccessor.CompositeTransducedAccessorImpl<BeanT, ValueT>
  {
    public CompositeContextDependentTransducedAccessorImpl(JAXBContextImpl paramJAXBContextImpl, Transducer<ValueT> paramTransducer, Accessor<BeanT, ValueT> paramAccessor)
    {
      super(paramTransducer, paramAccessor);
      assert (paramTransducer.useNamespace());
    }
    
    public boolean useNamespace()
    {
      return true;
    }
    
    public void declareNamespace(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
      throws AccessorException
    {
      Object localObject = acc.get(paramBeanT);
      if (localObject != null) {
        xducer.declareNamespace(localObject, paramXMLSerializer);
      }
    }
    
    public void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, BeanT paramBeanT, String paramString)
      throws SAXException, AccessorException, IOException, XMLStreamException
    {
      paramXMLSerializer.startElement(paramName, null);
      declareNamespace(paramBeanT, paramXMLSerializer);
      paramXMLSerializer.endNamespaceDecls(null);
      paramXMLSerializer.endAttributes();
      xducer.writeText(paramXMLSerializer, acc.get(paramBeanT), paramString);
      paramXMLSerializer.endElement();
    }
  }
  
  public static class CompositeTransducedAccessorImpl<BeanT, ValueT>
    extends TransducedAccessor<BeanT>
  {
    protected final Transducer<ValueT> xducer;
    protected final Accessor<BeanT, ValueT> acc;
    
    public CompositeTransducedAccessorImpl(JAXBContextImpl paramJAXBContextImpl, Transducer<ValueT> paramTransducer, Accessor<BeanT, ValueT> paramAccessor)
    {
      xducer = paramTransducer;
      acc = paramAccessor.optimize(paramJAXBContextImpl);
    }
    
    public CharSequence print(BeanT paramBeanT)
      throws AccessorException
    {
      Object localObject = acc.get(paramBeanT);
      if (localObject == null) {
        return null;
      }
      return xducer.print(localObject);
    }
    
    public void parse(BeanT paramBeanT, CharSequence paramCharSequence)
      throws AccessorException, SAXException
    {
      acc.set(paramBeanT, xducer.parse(paramCharSequence));
    }
    
    public boolean hasValue(BeanT paramBeanT)
      throws AccessorException
    {
      return acc.getUnadapted(paramBeanT) != null;
    }
    
    public void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, BeanT paramBeanT, String paramString)
      throws SAXException, AccessorException, IOException, XMLStreamException
    {
      xducer.writeLeafElement(paramXMLSerializer, paramName, acc.get(paramBeanT), paramString);
    }
    
    public void writeText(XMLSerializer paramXMLSerializer, BeanT paramBeanT, String paramString)
      throws AccessorException, SAXException, IOException, XMLStreamException
    {
      xducer.writeText(paramXMLSerializer, acc.get(paramBeanT), paramString);
    }
  }
  
  private static final class IDREFTransducedAccessorImpl<BeanT, TargetT>
    extends DefaultTransducedAccessor<BeanT>
  {
    private final Accessor<BeanT, TargetT> acc;
    private final Class<TargetT> targetType;
    
    public IDREFTransducedAccessorImpl(Accessor<BeanT, TargetT> paramAccessor)
    {
      acc = paramAccessor;
      targetType = paramAccessor.getValueType();
    }
    
    public String print(BeanT paramBeanT)
      throws AccessorException, SAXException
    {
      Object localObject = acc.get(paramBeanT);
      if (localObject == null) {
        return null;
      }
      XMLSerializer localXMLSerializer = XMLSerializer.getInstance();
      try
      {
        String str = grammar.getBeanInfo(localObject, true).getId(localObject, localXMLSerializer);
        if (str == null) {
          localXMLSerializer.errorMissingId(localObject);
        }
        return str;
      }
      catch (JAXBException localJAXBException)
      {
        localXMLSerializer.reportError(null, localJAXBException);
      }
      return null;
    }
    
    private void assign(BeanT paramBeanT, TargetT paramTargetT, UnmarshallingContext paramUnmarshallingContext)
      throws AccessorException
    {
      if (!targetType.isInstance(paramTargetT)) {
        paramUnmarshallingContext.handleError(Messages.UNASSIGNABLE_TYPE.format(new Object[] { targetType, paramTargetT.getClass() }));
      } else {
        acc.set(paramBeanT, paramTargetT);
      }
    }
    
    public void parse(final BeanT paramBeanT, CharSequence paramCharSequence)
      throws AccessorException, SAXException
    {
      final String str = WhiteSpaceProcessor.trim(paramCharSequence).toString();
      final UnmarshallingContext localUnmarshallingContext = UnmarshallingContext.getInstance();
      final Callable localCallable = localUnmarshallingContext.getObjectFromId(str, acc.valueType);
      if (localCallable == null)
      {
        localUnmarshallingContext.errorUnresolvedIDREF(paramBeanT, str, localUnmarshallingContext.getLocator());
        return;
      }
      Object localObject;
      try
      {
        localObject = localCallable.call();
      }
      catch (SAXException localSAXException)
      {
        throw localSAXException;
      }
      catch (RuntimeException localRuntimeException)
      {
        throw localRuntimeException;
      }
      catch (Exception localException)
      {
        throw new SAXException2(localException);
      }
      if (localObject != null)
      {
        assign(paramBeanT, localObject, localUnmarshallingContext);
      }
      else
      {
        final LocatorEx.Snapshot localSnapshot = new LocatorEx.Snapshot(localUnmarshallingContext.getLocator());
        localUnmarshallingContext.addPatcher(new Patcher()
        {
          public void run()
            throws SAXException
          {
            try
            {
              Object localObject = localCallable.call();
              if (localObject == null) {
                localUnmarshallingContext.errorUnresolvedIDREF(paramBeanT, str, localSnapshot);
              } else {
                TransducedAccessor.IDREFTransducedAccessorImpl.this.assign(paramBeanT, localObject, localUnmarshallingContext);
              }
            }
            catch (AccessorException localAccessorException)
            {
              localUnmarshallingContext.handleError(localAccessorException);
            }
            catch (SAXException localSAXException)
            {
              throw localSAXException;
            }
            catch (RuntimeException localRuntimeException)
            {
              throw localRuntimeException;
            }
            catch (Exception localException)
            {
              throw new SAXException2(localException);
            }
          }
        });
      }
    }
    
    public boolean hasValue(BeanT paramBeanT)
      throws AccessorException
    {
      return acc.get(paramBeanT) != null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\TransducedAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
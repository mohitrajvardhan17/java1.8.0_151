package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext.State;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class ValueListBeanInfoImpl
  extends JaxBeanInfo
{
  private final Class itemType = jaxbType.getComponentType();
  private final Transducer xducer;
  private final Loader loader = new Loader(true)
  {
    public void text(UnmarshallingContext.State paramAnonymousState, CharSequence paramAnonymousCharSequence)
      throws SAXException
    {
      FinalArrayList localFinalArrayList = new FinalArrayList();
      int i = 0;
      int j = paramAnonymousCharSequence.length();
      for (;;)
      {
        for (int k = i; (k < j) && (!WhiteSpaceProcessor.isWhiteSpace(paramAnonymousCharSequence.charAt(k))); k++) {}
        CharSequence localCharSequence = paramAnonymousCharSequence.subSequence(i, k);
        if (!localCharSequence.equals(""))
        {
          try
          {
            localFinalArrayList.add(xducer.parse(localCharSequence));
          }
          catch (AccessorException localAccessorException)
          {
            handleGenericException(localAccessorException, true);
          }
          continue;
        }
        if (k == j) {
          break;
        }
        while ((k < j) && (WhiteSpaceProcessor.isWhiteSpace(paramAnonymousCharSequence.charAt(k)))) {
          k++;
        }
        if (k == j) {
          break;
        }
        i = k;
      }
      paramAnonymousState.setTarget(ValueListBeanInfoImpl.this.toArray(localFinalArrayList));
    }
  };
  
  public ValueListBeanInfoImpl(JAXBContextImpl paramJAXBContextImpl, Class paramClass)
    throws JAXBException
  {
    super(paramJAXBContextImpl, null, paramClass, false, true, false);
    xducer = paramJAXBContextImpl.getBeanInfo(paramClass.getComponentType(), true).getTransducer();
    assert (xducer != null);
  }
  
  private Object toArray(List paramList)
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
      try
      {
        xducer.writeText(paramXMLSerializer, localObject, "arrayItem");
      }
      catch (AccessorException localAccessorException)
      {
        paramXMLSerializer.reportError("arrayItem", localAccessorException);
      }
    }
  }
  
  public final void serializeURIs(Object paramObject, XMLSerializer paramXMLSerializer)
    throws SAXException
  {
    if (xducer.useNamespace())
    {
      int i = Array.getLength(paramObject);
      for (int j = 0; j < i; j++)
      {
        Object localObject = Array.get(paramObject, j);
        try
        {
          xducer.declareNamespace(localObject, paramXMLSerializer);
        }
        catch (AccessorException localAccessorException)
        {
          paramXMLSerializer.reportError("arrayItem", localAccessorException);
        }
      }
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
    throw new UnsupportedOperationException();
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
    throws SAXException
  {
    paramXMLSerializer.reportError(new ValidationEventImpl(1, Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(new Object[] { paramObject.getClass().getName() }), null, null));
  }
  
  public final Transducer getTransducer()
  {
    return null;
  }
  
  public final Loader getLoader(JAXBContextImpl paramJAXBContextImpl, boolean paramBoolean)
  {
    return loader;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\ValueListBeanInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
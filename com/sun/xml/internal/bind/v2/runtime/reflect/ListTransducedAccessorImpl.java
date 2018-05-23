package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

public final class ListTransducedAccessorImpl<BeanT, ListT, ItemT, PackT>
  extends DefaultTransducedAccessor<BeanT>
{
  private final Transducer<ItemT> xducer;
  private final Lister<BeanT, ListT, ItemT, PackT> lister;
  private final Accessor<BeanT, ListT> acc;
  
  public ListTransducedAccessorImpl(Transducer<ItemT> paramTransducer, Accessor<BeanT, ListT> paramAccessor, Lister<BeanT, ListT, ItemT, PackT> paramLister)
  {
    xducer = paramTransducer;
    lister = paramLister;
    acc = paramAccessor;
  }
  
  public boolean useNamespace()
  {
    return xducer.useNamespace();
  }
  
  public void declareNamespace(BeanT paramBeanT, XMLSerializer paramXMLSerializer)
    throws AccessorException, SAXException
  {
    Object localObject1 = acc.get(paramBeanT);
    if (localObject1 != null)
    {
      ListIterator localListIterator = lister.iterator(localObject1, paramXMLSerializer);
      while (localListIterator.hasNext()) {
        try
        {
          Object localObject2 = localListIterator.next();
          if (localObject2 != null) {
            xducer.declareNamespace(localObject2, paramXMLSerializer);
          }
        }
        catch (JAXBException localJAXBException)
        {
          paramXMLSerializer.reportError(null, localJAXBException);
        }
      }
    }
  }
  
  public String print(BeanT paramBeanT)
    throws AccessorException, SAXException
  {
    Object localObject1 = acc.get(paramBeanT);
    if (localObject1 == null) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    XMLSerializer localXMLSerializer = XMLSerializer.getInstance();
    ListIterator localListIterator = lister.iterator(localObject1, localXMLSerializer);
    while (localListIterator.hasNext()) {
      try
      {
        Object localObject2 = localListIterator.next();
        if (localObject2 != null)
        {
          if (localStringBuilder.length() > 0) {
            localStringBuilder.append(' ');
          }
          localStringBuilder.append(xducer.print(localObject2));
        }
      }
      catch (JAXBException localJAXBException)
      {
        localXMLSerializer.reportError(null, localJAXBException);
      }
    }
    return localStringBuilder.toString();
  }
  
  private void processValue(BeanT paramBeanT, CharSequence paramCharSequence)
    throws AccessorException, SAXException
  {
    Object localObject = lister.startPacking(paramBeanT, acc);
    int i = 0;
    int j = paramCharSequence.length();
    for (;;)
    {
      for (int k = i; (k < j) && (!WhiteSpaceProcessor.isWhiteSpace(paramCharSequence.charAt(k))); k++) {}
      CharSequence localCharSequence = paramCharSequence.subSequence(i, k);
      if (!localCharSequence.equals("")) {
        lister.addToPack(localObject, xducer.parse(localCharSequence));
      }
      if (k == j) {
        break;
      }
      while ((k < j) && (WhiteSpaceProcessor.isWhiteSpace(paramCharSequence.charAt(k)))) {
        k++;
      }
      if (k == j) {
        break;
      }
      i = k;
    }
    lister.endPacking(localObject, paramBeanT, acc);
  }
  
  public void parse(BeanT paramBeanT, CharSequence paramCharSequence)
    throws AccessorException, SAXException
  {
    processValue(paramBeanT, paramCharSequence);
  }
  
  public boolean hasValue(BeanT paramBeanT)
    throws AccessorException
  {
    return acc.get(paramBeanT) != null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\ListTransducedAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
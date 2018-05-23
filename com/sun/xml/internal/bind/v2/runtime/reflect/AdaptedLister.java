package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.Coordinator;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.xml.sax.SAXException;

final class AdaptedLister<BeanT, PropT, InMemItemT, OnWireItemT, PackT>
  extends Lister<BeanT, PropT, OnWireItemT, PackT>
{
  private final Lister<BeanT, PropT, InMemItemT, PackT> core;
  private final Class<? extends XmlAdapter<OnWireItemT, InMemItemT>> adapter;
  
  AdaptedLister(Lister<BeanT, PropT, InMemItemT, PackT> paramLister, Class<? extends XmlAdapter<OnWireItemT, InMemItemT>> paramClass)
  {
    core = paramLister;
    adapter = paramClass;
  }
  
  private XmlAdapter<OnWireItemT, InMemItemT> getAdapter()
  {
    return Coordinator._getInstance().getAdapter(adapter);
  }
  
  public ListIterator<OnWireItemT> iterator(PropT paramPropT, XMLSerializer paramXMLSerializer)
  {
    return new ListIteratorImpl(core.iterator(paramPropT, paramXMLSerializer), paramXMLSerializer);
  }
  
  public PackT startPacking(BeanT paramBeanT, Accessor<BeanT, PropT> paramAccessor)
    throws AccessorException
  {
    return (PackT)core.startPacking(paramBeanT, paramAccessor);
  }
  
  public void addToPack(PackT paramPackT, OnWireItemT paramOnWireItemT)
    throws AccessorException
  {
    Object localObject;
    try
    {
      localObject = getAdapter().unmarshal(paramOnWireItemT);
    }
    catch (Exception localException)
    {
      throw new AccessorException(localException);
    }
    core.addToPack(paramPackT, localObject);
  }
  
  public void endPacking(PackT paramPackT, BeanT paramBeanT, Accessor<BeanT, PropT> paramAccessor)
    throws AccessorException
  {
    core.endPacking(paramPackT, paramBeanT, paramAccessor);
  }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, PropT> paramAccessor)
    throws AccessorException
  {
    core.reset(paramBeanT, paramAccessor);
  }
  
  private final class ListIteratorImpl
    implements ListIterator<OnWireItemT>
  {
    private final ListIterator<InMemItemT> core;
    private final XMLSerializer serializer;
    
    public ListIteratorImpl(XMLSerializer paramXMLSerializer)
    {
      core = paramXMLSerializer;
      XMLSerializer localXMLSerializer;
      serializer = localXMLSerializer;
    }
    
    public boolean hasNext()
    {
      return core.hasNext();
    }
    
    public OnWireItemT next()
      throws SAXException, JAXBException
    {
      Object localObject = core.next();
      try
      {
        return (OnWireItemT)AdaptedLister.this.getAdapter().marshal(localObject);
      }
      catch (Exception localException)
      {
        serializer.reportError(null, localException);
      }
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\AdaptedLister.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
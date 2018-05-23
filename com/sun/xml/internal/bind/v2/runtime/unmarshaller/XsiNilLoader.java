package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.util.Collection;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class XsiNilLoader
  extends ProxyLoader
{
  private final Loader defaultLoader;
  
  public XsiNilLoader(Loader paramLoader)
  {
    defaultLoader = paramLoader;
    assert (paramLoader != null);
  }
  
  protected Loader selectLoader(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    int i = atts.getIndex("http://www.w3.org/2001/XMLSchema-instance", "nil");
    if (i != -1)
    {
      Boolean localBoolean = DatatypeConverterImpl._parseBoolean(atts.getValue(i));
      if ((localBoolean != null) && (localBoolean.booleanValue()))
      {
        onNil(paramState);
        int j = atts.getLength() - 1 > 0 ? 1 : 0;
        if ((j == 0) || (!(paramState.getPrev().getTarget() instanceof JAXBElement))) {
          return Discarder.INSTANCE;
        }
      }
    }
    return defaultLoader;
  }
  
  public Collection<QName> getExpectedChildElements()
  {
    return defaultLoader.getExpectedChildElements();
  }
  
  public Collection<QName> getExpectedAttributes()
  {
    return defaultLoader.getExpectedAttributes();
  }
  
  protected void onNil(UnmarshallingContext.State paramState)
    throws SAXException
  {}
  
  public static final class Array
    extends XsiNilLoader
  {
    public Array(Loader paramLoader)
    {
      super();
    }
    
    protected void onNil(UnmarshallingContext.State paramState)
    {
      paramState.setTarget(null);
    }
  }
  
  public static final class Single
    extends XsiNilLoader
  {
    private final Accessor acc;
    
    public Single(Loader paramLoader, Accessor paramAccessor)
    {
      super();
      acc = paramAccessor;
    }
    
    protected void onNil(UnmarshallingContext.State paramState)
      throws SAXException
    {
      try
      {
        acc.set(paramState.getPrev().getTarget(), null);
        paramState.getPrev().setNil(true);
      }
      catch (AccessorException localAccessorException)
      {
        handleGenericException(localAccessorException, true);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\XsiNilLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
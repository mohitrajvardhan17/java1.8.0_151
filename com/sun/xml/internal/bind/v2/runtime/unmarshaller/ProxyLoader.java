package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;

public abstract class ProxyLoader
  extends Loader
{
  public ProxyLoader()
  {
    super(false);
  }
  
  public final void startElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    Loader localLoader = selectLoader(paramState, paramTagName);
    paramState.setLoader(localLoader);
    localLoader.startElement(paramState, paramTagName);
  }
  
  protected abstract Loader selectLoader(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException;
  
  public final void leaveElement(UnmarshallingContext.State paramState, TagName paramTagName)
  {
    throw new IllegalStateException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\ProxyLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
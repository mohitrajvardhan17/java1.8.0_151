package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;

public final class DefaultValueLoaderDecorator
  extends Loader
{
  private final Loader l;
  private final String defaultValue;
  
  public DefaultValueLoaderDecorator(Loader paramLoader, String paramString)
  {
    l = paramLoader;
    defaultValue = paramString;
  }
  
  public void startElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    if (paramState.getElementDefaultValue() == null) {
      paramState.setElementDefaultValue(defaultValue);
    }
    paramState.setLoader(l);
    l.startElement(paramState, paramTagName);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\DefaultValueLoaderDecorator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
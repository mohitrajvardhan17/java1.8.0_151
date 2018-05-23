package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import javax.xml.bind.annotation.DomHandler;
import org.xml.sax.SAXException;

public final class WildcardLoader
  extends ProxyLoader
{
  private final DomLoader dom;
  private final WildcardMode mode;
  
  public WildcardLoader(DomHandler paramDomHandler, WildcardMode paramWildcardMode)
  {
    dom = new DomLoader(paramDomHandler);
    mode = paramWildcardMode;
  }
  
  protected Loader selectLoader(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    UnmarshallingContext localUnmarshallingContext = paramState.getContext();
    if (mode.allowTypedObject)
    {
      Loader localLoader = localUnmarshallingContext.selectRootLoader(paramState, paramTagName);
      if (localLoader != null) {
        return localLoader;
      }
    }
    if (mode.allowDom) {
      return dom;
    }
    return Discarder.INSTANCE;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\WildcardLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
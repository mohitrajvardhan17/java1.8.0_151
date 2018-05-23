package com.sun.xml.internal.ws.api.model.wsdl;

import java.util.List;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public abstract interface WSDLExtensible
  extends WSDLObject
{
  public abstract Iterable<WSDLExtension> getExtensions();
  
  public abstract <T extends WSDLExtension> Iterable<T> getExtensions(Class<T> paramClass);
  
  public abstract <T extends WSDLExtension> T getExtension(Class<T> paramClass);
  
  public abstract void addExtension(WSDLExtension paramWSDLExtension);
  
  public abstract boolean areRequiredExtensionsUnderstood();
  
  public abstract void addNotUnderstoodExtension(QName paramQName, Locator paramLocator);
  
  public abstract List<? extends WSDLExtension> getNotUnderstoodExtensions();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLExtensible.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
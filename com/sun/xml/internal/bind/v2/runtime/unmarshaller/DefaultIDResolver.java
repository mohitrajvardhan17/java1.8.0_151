package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.IDResolver;
import java.util.HashMap;
import java.util.concurrent.Callable;
import javax.xml.bind.ValidationEventHandler;
import org.xml.sax.SAXException;

final class DefaultIDResolver
  extends IDResolver
{
  private HashMap<String, Object> idmap = null;
  
  DefaultIDResolver() {}
  
  public void startDocument(ValidationEventHandler paramValidationEventHandler)
    throws SAXException
  {
    if (idmap != null) {
      idmap.clear();
    }
  }
  
  public void bind(String paramString, Object paramObject)
  {
    if (idmap == null) {
      idmap = new HashMap();
    }
    idmap.put(paramString, paramObject);
  }
  
  public Callable resolve(final String paramString, Class paramClass)
  {
    new Callable()
    {
      public Object call()
        throws Exception
      {
        if (idmap == null) {
          return null;
        }
        return idmap.get(paramString);
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\DefaultIDResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
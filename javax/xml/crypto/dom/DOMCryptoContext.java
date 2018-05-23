package javax.xml.crypto.dom;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.XMLCryptoContext;
import org.w3c.dom.Element;

public class DOMCryptoContext
  implements XMLCryptoContext
{
  private HashMap<String, String> nsMap = new HashMap();
  private HashMap<String, Element> idMap = new HashMap();
  private HashMap<Object, Object> objMap = new HashMap();
  private String baseURI;
  private KeySelector ks;
  private URIDereferencer dereferencer;
  private HashMap<String, Object> propMap = new HashMap();
  private String defaultPrefix;
  
  protected DOMCryptoContext() {}
  
  public String getNamespacePrefix(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      throw new NullPointerException("namespaceURI cannot be null");
    }
    String str = (String)nsMap.get(paramString1);
    return str != null ? str : paramString2;
  }
  
  public String putNamespacePrefix(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      throw new NullPointerException("namespaceURI is null");
    }
    return (String)nsMap.put(paramString1, paramString2);
  }
  
  public String getDefaultNamespacePrefix()
  {
    return defaultPrefix;
  }
  
  public void setDefaultNamespacePrefix(String paramString)
  {
    defaultPrefix = paramString;
  }
  
  public String getBaseURI()
  {
    return baseURI;
  }
  
  public void setBaseURI(String paramString)
  {
    if (paramString != null) {
      URI.create(paramString);
    }
    baseURI = paramString;
  }
  
  public URIDereferencer getURIDereferencer()
  {
    return dereferencer;
  }
  
  public void setURIDereferencer(URIDereferencer paramURIDereferencer)
  {
    dereferencer = paramURIDereferencer;
  }
  
  public Object getProperty(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("name is null");
    }
    return propMap.get(paramString);
  }
  
  public Object setProperty(String paramString, Object paramObject)
  {
    if (paramString == null) {
      throw new NullPointerException("name is null");
    }
    return propMap.put(paramString, paramObject);
  }
  
  public KeySelector getKeySelector()
  {
    return ks;
  }
  
  public void setKeySelector(KeySelector paramKeySelector)
  {
    ks = paramKeySelector;
  }
  
  public Element getElementById(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("idValue is null");
    }
    return (Element)idMap.get(paramString);
  }
  
  public void setIdAttributeNS(Element paramElement, String paramString1, String paramString2)
  {
    if (paramElement == null) {
      throw new NullPointerException("element is null");
    }
    if (paramString2 == null) {
      throw new NullPointerException("localName is null");
    }
    String str = paramElement.getAttributeNS(paramString1, paramString2);
    if ((str == null) || (str.length() == 0)) {
      throw new IllegalArgumentException(paramString2 + " is not an attribute");
    }
    idMap.put(str, paramElement);
  }
  
  public Iterator iterator()
  {
    return Collections.unmodifiableMap(idMap).entrySet().iterator();
  }
  
  public Object get(Object paramObject)
  {
    return objMap.get(paramObject);
  }
  
  public Object put(Object paramObject1, Object paramObject2)
  {
    return objMap.put(paramObject1, paramObject2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dom\DOMCryptoContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
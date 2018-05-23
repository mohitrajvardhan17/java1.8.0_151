package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class HTTPInputSource
  extends XMLInputSource
{
  protected boolean fFollowRedirects = true;
  protected Map<String, String> fHTTPRequestProperties = new HashMap();
  
  public HTTPInputSource(String paramString1, String paramString2, String paramString3)
  {
    super(paramString1, paramString2, paramString3);
  }
  
  public HTTPInputSource(XMLResourceIdentifier paramXMLResourceIdentifier)
  {
    super(paramXMLResourceIdentifier);
  }
  
  public HTTPInputSource(String paramString1, String paramString2, String paramString3, InputStream paramInputStream, String paramString4)
  {
    super(paramString1, paramString2, paramString3, paramInputStream, paramString4);
  }
  
  public HTTPInputSource(String paramString1, String paramString2, String paramString3, Reader paramReader, String paramString4)
  {
    super(paramString1, paramString2, paramString3, paramReader, paramString4);
  }
  
  public boolean getFollowHTTPRedirects()
  {
    return fFollowRedirects;
  }
  
  public void setFollowHTTPRedirects(boolean paramBoolean)
  {
    fFollowRedirects = paramBoolean;
  }
  
  public String getHTTPRequestProperty(String paramString)
  {
    return (String)fHTTPRequestProperties.get(paramString);
  }
  
  public Iterator<Map.Entry<String, String>> getHTTPRequestProperties()
  {
    return fHTTPRequestProperties.entrySet().iterator();
  }
  
  public void setHTTPRequestProperty(String paramString1, String paramString2)
  {
    if (paramString2 != null) {
      fHTTPRequestProperties.put(paramString1, paramString2);
    } else {
      fHTTPRequestProperties.remove(paramString1);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\HTTPInputSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
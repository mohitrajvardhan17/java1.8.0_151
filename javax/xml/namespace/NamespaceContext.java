package javax.xml.namespace;

import java.util.Iterator;

public abstract interface NamespaceContext
{
  public abstract String getNamespaceURI(String paramString);
  
  public abstract String getPrefix(String paramString);
  
  public abstract Iterator getPrefixes(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\namespace\NamespaceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
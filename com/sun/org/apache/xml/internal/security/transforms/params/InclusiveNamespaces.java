package com.sun.org.apache.xml.internal.security.transforms.params;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class InclusiveNamespaces
  extends ElementProxy
  implements TransformParam
{
  public static final String _TAG_EC_INCLUSIVENAMESPACES = "InclusiveNamespaces";
  public static final String _ATT_EC_PREFIXLIST = "PrefixList";
  public static final String ExclusiveCanonicalizationNamespace = "http://www.w3.org/2001/10/xml-exc-c14n#";
  
  public InclusiveNamespaces(Document paramDocument, String paramString)
  {
    this(paramDocument, prefixStr2Set(paramString));
  }
  
  public InclusiveNamespaces(Document paramDocument, Set<String> paramSet)
  {
    super(paramDocument);
    Object localObject = null;
    if ((paramSet instanceof SortedSet)) {
      localObject = (SortedSet)paramSet;
    } else {
      localObject = new TreeSet(paramSet);
    }
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = ((SortedSet)localObject).iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (str.equals("xmlns")) {
        localStringBuilder.append("#default ");
      } else {
        localStringBuilder.append(str + " ");
      }
    }
    constructionElement.setAttributeNS(null, "PrefixList", localStringBuilder.toString().trim());
  }
  
  public InclusiveNamespaces(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
  }
  
  public String getInclusiveNamespaces()
  {
    return constructionElement.getAttributeNS(null, "PrefixList");
  }
  
  public static SortedSet<String> prefixStr2Set(String paramString)
  {
    TreeSet localTreeSet = new TreeSet();
    if ((paramString == null) || (paramString.length() == 0)) {
      return localTreeSet;
    }
    String[] arrayOfString1 = paramString.split("\\s");
    for (String str : arrayOfString1) {
      if (str.equals("#default")) {
        localTreeSet.add("xmlns");
      } else {
        localTreeSet.add(str);
      }
    }
    return localTreeSet;
  }
  
  public String getBaseNamespace()
  {
    return "http://www.w3.org/2001/10/xml-exc-c14n#";
  }
  
  public String getBaseLocalName()
  {
    return "InclusiveNamespaces";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\params\InclusiveNamespaces.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
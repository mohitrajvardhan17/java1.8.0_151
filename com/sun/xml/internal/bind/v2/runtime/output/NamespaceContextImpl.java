package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.NameList;
import com.sun.xml.internal.bind.v2.runtime.NamespaceContext2;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class NamespaceContextImpl
  implements NamespaceContext2
{
  private final XMLSerializer owner;
  private String[] prefixes = new String[4];
  private String[] nsUris = new String[4];
  private int size;
  private Element current;
  private final Element top;
  private NamespacePrefixMapper prefixMapper = defaultNamespacePrefixMapper;
  public boolean collectionMode;
  private static final NamespacePrefixMapper defaultNamespacePrefixMapper = new NamespacePrefixMapper()
  {
    public String getPreferredPrefix(String paramAnonymousString1, String paramAnonymousString2, boolean paramAnonymousBoolean)
    {
      if (paramAnonymousString1.equals("http://www.w3.org/2001/XMLSchema-instance")) {
        return "xsi";
      }
      if (paramAnonymousString1.equals("http://www.w3.org/2001/XMLSchema")) {
        return "xs";
      }
      if (paramAnonymousString1.equals("http://www.w3.org/2005/05/xmlmime")) {
        return "xmime";
      }
      return paramAnonymousString2;
    }
  };
  
  public NamespaceContextImpl(XMLSerializer paramXMLSerializer)
  {
    owner = paramXMLSerializer;
    current = (top = new Element(this, null, null));
    put("http://www.w3.org/XML/1998/namespace", "xml");
  }
  
  public void setPrefixMapper(NamespacePrefixMapper paramNamespacePrefixMapper)
  {
    if (paramNamespacePrefixMapper == null) {
      paramNamespacePrefixMapper = defaultNamespacePrefixMapper;
    }
    prefixMapper = paramNamespacePrefixMapper;
  }
  
  public NamespacePrefixMapper getPrefixMapper()
  {
    return prefixMapper;
  }
  
  public void reset()
  {
    current = top;
    size = 1;
    collectionMode = false;
  }
  
  public int declareNsUri(String paramString1, String paramString2, boolean paramBoolean)
  {
    paramString2 = prefixMapper.getPreferredPrefix(paramString1, paramString2, paramBoolean);
    String str;
    if (paramString1.length() == 0)
    {
      for (i = size - 1; i >= 0; i--)
      {
        if (nsUris[i].length() == 0) {
          return i;
        }
        if (prefixes[i].length() == 0)
        {
          assert ((current.defaultPrefixIndex == -1) && (current.oldDefaultNamespaceUriIndex == -1));
          str = nsUris[i];
          String[] arrayOfString = owner.nameList.namespaceURIs;
          if (current.baseIndex <= i)
          {
            nsUris[i] = "";
            j = put(str, null);
            for (int k = arrayOfString.length - 1; k >= 0; k--) {
              if (arrayOfString[k].equals(str))
              {
                owner.knownUri2prefixIndexMap[k] = j;
                break;
              }
            }
            if (current.elementLocalName != null) {
              current.setTagName(j, current.elementLocalName, current.getOuterPeer());
            }
            return i;
          }
          for (int j = arrayOfString.length - 1; j >= 0; j--) {
            if (arrayOfString[j].equals(str))
            {
              current.defaultPrefixIndex = i;
              current.oldDefaultNamespaceUriIndex = j;
              owner.knownUri2prefixIndexMap[j] = size;
              break;
            }
          }
          if (current.elementLocalName != null) {
            current.setTagName(size, current.elementLocalName, current.getOuterPeer());
          }
          put(nsUris[i], null);
          return put("", "");
        }
      }
      return put("", "");
    }
    for (int i = size - 1; i >= 0; i--)
    {
      str = prefixes[i];
      if ((nsUris[i].equals(paramString1)) && ((!paramBoolean) || (str.length() > 0))) {
        return i;
      }
      if (str.equals(paramString2)) {
        paramString2 = null;
      }
    }
    if ((paramString2 == null) && (paramBoolean)) {
      paramString2 = makeUniquePrefix();
    }
    return put(paramString1, paramString2);
  }
  
  public int force(@NotNull String paramString1, @NotNull String paramString2)
  {
    for (int i = size - 1; i >= 0; i--) {
      if (prefixes[i].equals(paramString2))
      {
        if (!nsUris[i].equals(paramString1)) {
          break;
        }
        return i;
      }
    }
    return put(paramString1, paramString2);
  }
  
  public int put(@NotNull String paramString1, @Nullable String paramString2)
  {
    if (size == nsUris.length)
    {
      String[] arrayOfString1 = new String[nsUris.length * 2];
      String[] arrayOfString2 = new String[prefixes.length * 2];
      System.arraycopy(nsUris, 0, arrayOfString1, 0, nsUris.length);
      System.arraycopy(prefixes, 0, arrayOfString2, 0, prefixes.length);
      nsUris = arrayOfString1;
      prefixes = arrayOfString2;
    }
    if (paramString2 == null) {
      if (size == 1) {
        paramString2 = "";
      } else {
        paramString2 = makeUniquePrefix();
      }
    }
    nsUris[size] = paramString1;
    prefixes[size] = paramString2;
    return size++;
  }
  
  private String makeUniquePrefix()
  {
    for (String str = 5 + "ns" + size; getNamespaceURI(str) != null; str = str + '_') {}
    return str;
  }
  
  public Element getCurrent()
  {
    return current;
  }
  
  public int getPrefixIndex(String paramString)
  {
    for (int i = size - 1; i >= 0; i--) {
      if (nsUris[i].equals(paramString)) {
        return i;
      }
    }
    throw new IllegalStateException();
  }
  
  public String getPrefix(int paramInt)
  {
    return prefixes[paramInt];
  }
  
  public String getNamespaceURI(int paramInt)
  {
    return nsUris[paramInt];
  }
  
  public String getNamespaceURI(String paramString)
  {
    for (int i = size - 1; i >= 0; i--) {
      if (prefixes[i].equals(paramString)) {
        return nsUris[i];
      }
    }
    return null;
  }
  
  public String getPrefix(String paramString)
  {
    if (collectionMode) {
      return declareNamespace(paramString, null, false);
    }
    for (int i = size - 1; i >= 0; i--) {
      if (nsUris[i].equals(paramString)) {
        return prefixes[i];
      }
    }
    return null;
  }
  
  public Iterator<String> getPrefixes(String paramString)
  {
    String str = getPrefix(paramString);
    if (str == null) {
      return Collections.emptySet().iterator();
    }
    return Collections.singleton(paramString).iterator();
  }
  
  public String declareNamespace(String paramString1, String paramString2, boolean paramBoolean)
  {
    int i = declareNsUri(paramString1, paramString2, paramBoolean);
    return getPrefix(i);
  }
  
  public int count()
  {
    return size;
  }
  
  public final class Element
  {
    public final NamespaceContextImpl context;
    private final Element prev;
    private Element next;
    private int oldDefaultNamespaceUriIndex;
    private int defaultPrefixIndex;
    private int baseIndex;
    private final int depth;
    private int elementNamePrefix;
    private String elementLocalName;
    private Name elementName;
    private Object outerPeer;
    private Object innerPeer;
    
    private Element(NamespaceContextImpl paramNamespaceContextImpl, Element paramElement)
    {
      context = paramNamespaceContextImpl;
      prev = paramElement;
      depth = (paramElement == null ? 0 : depth + 1);
    }
    
    public boolean isRootElement()
    {
      return depth == 1;
    }
    
    public Element push()
    {
      if (next == null) {
        next = new Element(NamespaceContextImpl.this, context, this);
      }
      next.onPushed();
      return next;
    }
    
    public Element pop()
    {
      if (oldDefaultNamespaceUriIndex >= 0) {
        context.owner.knownUri2prefixIndexMap[oldDefaultNamespaceUriIndex] = defaultPrefixIndex;
      }
      context.size = baseIndex;
      context.current = prev;
      outerPeer = (innerPeer = null);
      return prev;
    }
    
    private void onPushed()
    {
      oldDefaultNamespaceUriIndex = (defaultPrefixIndex = -1);
      baseIndex = context.size;
      context.current = this;
    }
    
    public void setTagName(int paramInt, String paramString, Object paramObject)
    {
      assert (paramString != null);
      elementNamePrefix = paramInt;
      elementLocalName = paramString;
      elementName = null;
      outerPeer = paramObject;
    }
    
    public void setTagName(Name paramName, Object paramObject)
    {
      assert (paramName != null);
      elementName = paramName;
      outerPeer = paramObject;
    }
    
    public void startElement(XmlOutput paramXmlOutput, Object paramObject)
      throws IOException, XMLStreamException
    {
      innerPeer = paramObject;
      if (elementName != null) {
        paramXmlOutput.beginStartTag(elementName);
      } else {
        paramXmlOutput.beginStartTag(elementNamePrefix, elementLocalName);
      }
    }
    
    public void endElement(XmlOutput paramXmlOutput)
      throws IOException, SAXException, XMLStreamException
    {
      if (elementName != null)
      {
        paramXmlOutput.endTag(elementName);
        elementName = null;
      }
      else
      {
        paramXmlOutput.endTag(elementNamePrefix, elementLocalName);
      }
    }
    
    public final int count()
    {
      return context.size - baseIndex;
    }
    
    public final String getPrefix(int paramInt)
    {
      return context.prefixes[(baseIndex + paramInt)];
    }
    
    public final String getNsUri(int paramInt)
    {
      return context.nsUris[(baseIndex + paramInt)];
    }
    
    public int getBase()
    {
      return baseIndex;
    }
    
    public Object getOuterPeer()
    {
      return outerPeer;
    }
    
    public Object getInnerPeer()
    {
      return innerPeer;
    }
    
    public Element getParent()
    {
      return prev;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\NamespaceContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
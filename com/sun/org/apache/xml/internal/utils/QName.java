package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xml.internal.res.XMLMessages;
import java.io.Serializable;
import java.util.Stack;
import java.util.StringTokenizer;
import org.w3c.dom.Element;

public class QName
  implements Serializable
{
  static final long serialVersionUID = 467434581652829920L;
  protected String _localName;
  protected String _namespaceURI;
  protected String _prefix;
  public static final String S_XMLNAMESPACEURI = "http://www.w3.org/XML/1998/namespace";
  private int m_hashCode;
  
  public QName() {}
  
  public QName(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, false);
  }
  
  public QName(String paramString1, String paramString2, boolean paramBoolean)
  {
    if (paramString2 == null) {
      throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_NULL", null));
    }
    if ((paramBoolean) && (!XML11Char.isXML11ValidNCName(paramString2))) {
      throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null));
    }
    _namespaceURI = paramString1;
    _localName = paramString2;
    m_hashCode = toString().hashCode();
  }
  
  public QName(String paramString1, String paramString2, String paramString3)
  {
    this(paramString1, paramString2, paramString3, false);
  }
  
  public QName(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    if (paramString3 == null) {
      throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_NULL", null));
    }
    if (paramBoolean)
    {
      if (!XML11Char.isXML11ValidNCName(paramString3)) {
        throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null));
      }
      if ((null != paramString2) && (!XML11Char.isXML11ValidNCName(paramString2))) {
        throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_PREFIX_INVALID", null));
      }
    }
    _namespaceURI = paramString1;
    _prefix = paramString2;
    _localName = paramString3;
    m_hashCode = toString().hashCode();
  }
  
  public QName(String paramString)
  {
    this(paramString, false);
  }
  
  public QName(String paramString, boolean paramBoolean)
  {
    if (paramString == null) {
      throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_NULL", null));
    }
    if ((paramBoolean) && (!XML11Char.isXML11ValidNCName(paramString))) {
      throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null));
    }
    _namespaceURI = null;
    _localName = paramString;
    m_hashCode = toString().hashCode();
  }
  
  public QName(String paramString, Stack paramStack)
  {
    this(paramString, paramStack, false);
  }
  
  public QName(String paramString, Stack paramStack, boolean paramBoolean)
  {
    String str1 = null;
    String str2 = null;
    int i = paramString.indexOf(':');
    if (i > 0)
    {
      str2 = paramString.substring(0, i);
      if (str2.equals("xml"))
      {
        str1 = "http://www.w3.org/XML/1998/namespace";
      }
      else
      {
        if (str2.equals("xmlns")) {
          return;
        }
        int j = paramStack.size();
        for (int k = j - 1; k >= 0; k--) {
          for (NameSpace localNameSpace = (NameSpace)paramStack.elementAt(k); null != localNameSpace; localNameSpace = m_next) {
            if ((null != m_prefix) && (str2.equals(m_prefix)))
            {
              str1 = m_uri;
              k = -1;
              break;
            }
          }
        }
      }
      if (null == str1) {
        throw new RuntimeException(XMLMessages.createXMLMessage("ER_PREFIX_MUST_RESOLVE", new Object[] { str2 }));
      }
    }
    _localName = (i < 0 ? paramString : paramString.substring(i + 1));
    if ((paramBoolean) && ((_localName == null) || (!XML11Char.isXML11ValidNCName(_localName)))) {
      throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null));
    }
    _namespaceURI = str1;
    _prefix = str2;
    m_hashCode = toString().hashCode();
  }
  
  public QName(String paramString, Element paramElement, PrefixResolver paramPrefixResolver)
  {
    this(paramString, paramElement, paramPrefixResolver, false);
  }
  
  public QName(String paramString, Element paramElement, PrefixResolver paramPrefixResolver, boolean paramBoolean)
  {
    _namespaceURI = null;
    int i = paramString.indexOf(':');
    if ((i > 0) && (null != paramElement))
    {
      String str = paramString.substring(0, i);
      _prefix = str;
      if (str.equals("xml"))
      {
        _namespaceURI = "http://www.w3.org/XML/1998/namespace";
      }
      else
      {
        if (str.equals("xmlns")) {
          return;
        }
        _namespaceURI = paramPrefixResolver.getNamespaceForPrefix(str, paramElement);
      }
      if (null == _namespaceURI) {
        throw new RuntimeException(XMLMessages.createXMLMessage("ER_PREFIX_MUST_RESOLVE", new Object[] { str }));
      }
    }
    _localName = (i < 0 ? paramString : paramString.substring(i + 1));
    if ((paramBoolean) && ((_localName == null) || (!XML11Char.isXML11ValidNCName(_localName)))) {
      throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null));
    }
    m_hashCode = toString().hashCode();
  }
  
  public QName(String paramString, PrefixResolver paramPrefixResolver)
  {
    this(paramString, paramPrefixResolver, false);
  }
  
  public QName(String paramString, PrefixResolver paramPrefixResolver, boolean paramBoolean)
  {
    String str = null;
    _namespaceURI = null;
    int i = paramString.indexOf(':');
    if (i > 0)
    {
      str = paramString.substring(0, i);
      if (str.equals("xml")) {
        _namespaceURI = "http://www.w3.org/XML/1998/namespace";
      } else {
        _namespaceURI = paramPrefixResolver.getNamespaceForPrefix(str);
      }
      if (null == _namespaceURI) {
        throw new RuntimeException(XMLMessages.createXMLMessage("ER_PREFIX_MUST_RESOLVE", new Object[] { str }));
      }
      _localName = paramString.substring(i + 1);
    }
    else
    {
      if (i == 0) {
        throw new RuntimeException(XMLMessages.createXMLMessage("ER_NAME_CANT_START_WITH_COLON", null));
      }
      _localName = paramString;
    }
    if ((paramBoolean) && ((_localName == null) || (!XML11Char.isXML11ValidNCName(_localName)))) {
      throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null));
    }
    m_hashCode = toString().hashCode();
    _prefix = str;
  }
  
  public String getNamespaceURI()
  {
    return _namespaceURI;
  }
  
  public String getPrefix()
  {
    return _prefix;
  }
  
  public String getLocalName()
  {
    return _localName;
  }
  
  public String toString()
  {
    return _namespaceURI != null ? "{" + _namespaceURI + "}" + _localName : _prefix != null ? _prefix + ":" + _localName : _localName;
  }
  
  public String toNamespacedString()
  {
    return _namespaceURI != null ? "{" + _namespaceURI + "}" + _localName : _localName;
  }
  
  public String getNamespace()
  {
    return getNamespaceURI();
  }
  
  public String getLocalPart()
  {
    return getLocalName();
  }
  
  public int hashCode()
  {
    return m_hashCode;
  }
  
  public boolean equals(String paramString1, String paramString2)
  {
    String str = getNamespaceURI();
    return (getLocalName().equals(paramString2)) && ((null != str) && (null != paramString1) ? str.equals(paramString1) : (null == str) && (null == paramString1));
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof QName))
    {
      QName localQName = (QName)paramObject;
      String str1 = getNamespaceURI();
      String str2 = localQName.getNamespaceURI();
      return (getLocalName().equals(localQName.getLocalName())) && ((null != str1) && (null != str2) ? str1.equals(str2) : (null == str1) && (null == str2));
    }
    return false;
  }
  
  public static QName getQNameFromString(String paramString)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "{}", false);
    String str1 = localStringTokenizer.nextToken();
    String str2 = localStringTokenizer.hasMoreTokens() ? localStringTokenizer.nextToken() : null;
    QName localQName;
    if (null == str2) {
      localQName = new QName(null, str1);
    } else {
      localQName = new QName(str1, str2);
    }
    return localQName;
  }
  
  public static boolean isXMLNSDecl(String paramString)
  {
    return (paramString.startsWith("xmlns")) && ((paramString.equals("xmlns")) || (paramString.startsWith("xmlns:")));
  }
  
  public static String getPrefixFromXMLNSDecl(String paramString)
  {
    int i = paramString.indexOf(':');
    return i >= 0 ? paramString.substring(i + 1) : "";
  }
  
  public static String getLocalPart(String paramString)
  {
    int i = paramString.indexOf(':');
    return i < 0 ? paramString : paramString.substring(i + 1);
  }
  
  public static String getPrefixPart(String paramString)
  {
    int i = paramString.indexOf(':');
    return i >= 0 ? paramString.substring(0, i) : "";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\QName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
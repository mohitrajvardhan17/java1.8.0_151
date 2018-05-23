package com.sun.xml.internal.fastinfoset.sax;

import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap.Entry;
import com.sun.xml.internal.fastinfoset.util.StringIntMap;
import com.sun.xml.internal.fastinfoset.vocab.SerializerVocabulary;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SAXDocumentSerializerWithPrefixMapping
  extends SAXDocumentSerializer
{
  protected Map _namespaceToPrefixMapping;
  protected Map _prefixToPrefixMapping;
  protected String _lastCheckedNamespace;
  protected String _lastCheckedPrefix;
  protected StringIntMap _declaredNamespaces;
  
  public SAXDocumentSerializerWithPrefixMapping(Map paramMap)
  {
    super(true);
    _namespaceToPrefixMapping = new HashMap(paramMap);
    _prefixToPrefixMapping = new HashMap();
    _namespaceToPrefixMapping.put("", "");
    _namespaceToPrefixMapping.put("http://www.w3.org/XML/1998/namespace", "xml");
    _declaredNamespaces = new StringIntMap(4);
  }
  
  public final void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    try
    {
      if (!_elementHasNamespaces)
      {
        encodeTermination();
        mark();
        _elementHasNamespaces = true;
        write(56);
        _declaredNamespaces.clear();
        _declaredNamespaces.obtainIndex(paramString2);
      }
      else if (_declaredNamespaces.obtainIndex(paramString2) != -1)
      {
        str = getPrefix(paramString2);
        if (str != null) {
          _prefixToPrefixMapping.put(paramString1, str);
        }
        return;
      }
      String str = getPrefix(paramString2);
      if (str != null)
      {
        encodeNamespaceAttribute(str, paramString2);
        _prefixToPrefixMapping.put(paramString1, str);
      }
      else
      {
        putPrefix(paramString2, paramString1);
        encodeNamespaceAttribute(paramString1, paramString2);
      }
    }
    catch (IOException localIOException)
    {
      throw new SAXException("startElement", localIOException);
    }
  }
  
  protected final void encodeElement(String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    LocalNameQualifiedNamesMap.Entry localEntry = _v.elementName.obtainEntry(paramString3);
    if (_valueIndex > 0)
    {
      if (encodeElementMapEntry(localEntry, paramString1)) {
        return;
      }
      if (_v.elementName.isQNameFromReadOnlyMap(_value[0]))
      {
        localEntry = _v.elementName.obtainDynamicEntry(paramString3);
        if ((_valueIndex > 0) && (encodeElementMapEntry(localEntry, paramString1))) {
          return;
        }
      }
    }
    encodeLiteralElementQualifiedNameOnThirdBit(paramString1, getPrefix(paramString1), paramString3, localEntry);
  }
  
  protected boolean encodeElementMapEntry(LocalNameQualifiedNamesMap.Entry paramEntry, String paramString)
    throws IOException
  {
    QualifiedName[] arrayOfQualifiedName = _value;
    for (int i = 0; i < _valueIndex; i++) {
      if ((paramString == namespaceName) || (paramString.equals(namespaceName)))
      {
        encodeNonZeroIntegerOnThirdBit(index);
        return true;
      }
    }
    return false;
  }
  
  protected final void encodeAttributes(Attributes paramAttributes)
    throws IOException, FastInfosetException
  {
    Object localObject;
    String str1;
    boolean bool1;
    if ((paramAttributes instanceof EncodingAlgorithmAttributes))
    {
      EncodingAlgorithmAttributes localEncodingAlgorithmAttributes = (EncodingAlgorithmAttributes)paramAttributes;
      for (int j = 0; j < localEncodingAlgorithmAttributes.getLength(); j++)
      {
        String str3 = paramAttributes.getURI(j);
        if (encodeAttribute(str3, paramAttributes.getQName(j), paramAttributes.getLocalName(j)))
        {
          localObject = localEncodingAlgorithmAttributes.getAlgorithmData(j);
          if (localObject == null)
          {
            str1 = localEncodingAlgorithmAttributes.getValue(j);
            bool1 = isAttributeValueLengthMatchesLimit(str1.length());
            boolean bool2 = localEncodingAlgorithmAttributes.getToIndex(j);
            String str2 = localEncodingAlgorithmAttributes.getAlpababet(j);
            if (str2 == null)
            {
              if ((str3 == "http://www.w3.org/2001/XMLSchema-instance") || (str3.equals("http://www.w3.org/2001/XMLSchema-instance"))) {
                str1 = convertQName(str1);
              }
              encodeNonIdentifyingStringOnFirstBit(str1, _v.attributeValue, bool1, bool2);
            }
            else if (str2 == "0123456789-:TZ ")
            {
              encodeDateTimeNonIdentifyingStringOnFirstBit(str1, bool1, bool2);
            }
            else if (str2 == "0123456789-+.E ")
            {
              encodeNumericNonIdentifyingStringOnFirstBit(str1, bool1, bool2);
            }
            else
            {
              encodeNonIdentifyingStringOnFirstBit(str1, _v.attributeValue, bool1, bool2);
            }
          }
          else
          {
            encodeNonIdentifyingStringOnFirstBit(localEncodingAlgorithmAttributes.getAlgorithmURI(j), localEncodingAlgorithmAttributes.getAlgorithmIndex(j), localObject);
          }
        }
      }
    }
    else
    {
      for (int i = 0; i < paramAttributes.getLength(); i++)
      {
        localObject = paramAttributes.getURI(i);
        if (encodeAttribute(paramAttributes.getURI(i), paramAttributes.getQName(i), paramAttributes.getLocalName(i)))
        {
          str1 = paramAttributes.getValue(i);
          bool1 = isAttributeValueLengthMatchesLimit(str1.length());
          if ((localObject == "http://www.w3.org/2001/XMLSchema-instance") || (((String)localObject).equals("http://www.w3.org/2001/XMLSchema-instance"))) {
            str1 = convertQName(str1);
          }
          encodeNonIdentifyingStringOnFirstBit(str1, _v.attributeValue, bool1, false);
        }
      }
    }
    _b = 240;
    _terminate = true;
  }
  
  private String convertQName(String paramString)
  {
    int i = paramString.indexOf(':');
    String str1 = "";
    String str2 = paramString;
    if (i != -1)
    {
      str1 = paramString.substring(0, i);
      str2 = paramString.substring(i + 1);
    }
    String str3 = (String)_prefixToPrefixMapping.get(str1);
    if (str3 != null)
    {
      if (str3.length() == 0) {
        return str2;
      }
      return str3 + ":" + str2;
    }
    return paramString;
  }
  
  protected final boolean encodeAttribute(String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    LocalNameQualifiedNamesMap.Entry localEntry = _v.attributeName.obtainEntry(paramString3);
    if (_valueIndex > 0)
    {
      if (encodeAttributeMapEntry(localEntry, paramString1)) {
        return true;
      }
      if (_v.attributeName.isQNameFromReadOnlyMap(_value[0]))
      {
        localEntry = _v.attributeName.obtainDynamicEntry(paramString3);
        if ((_valueIndex > 0) && (encodeAttributeMapEntry(localEntry, paramString1))) {
          return true;
        }
      }
    }
    return encodeLiteralAttributeQualifiedNameOnSecondBit(paramString1, getPrefix(paramString1), paramString3, localEntry);
  }
  
  protected boolean encodeAttributeMapEntry(LocalNameQualifiedNamesMap.Entry paramEntry, String paramString)
    throws IOException
  {
    QualifiedName[] arrayOfQualifiedName = _value;
    for (int i = 0; i < _valueIndex; i++) {
      if ((paramString == namespaceName) || (paramString.equals(namespaceName)))
      {
        encodeNonZeroIntegerOnSecondBitFirstBitZero(index);
        return true;
      }
    }
    return false;
  }
  
  protected final String getPrefix(String paramString)
  {
    if (_lastCheckedNamespace == paramString) {
      return _lastCheckedPrefix;
    }
    _lastCheckedNamespace = paramString;
    return _lastCheckedPrefix = (String)_namespaceToPrefixMapping.get(paramString);
  }
  
  protected final void putPrefix(String paramString1, String paramString2)
  {
    _namespaceToPrefixMapping.put(paramString1, paramString2);
    _lastCheckedNamespace = paramString1;
    _lastCheckedPrefix = paramString2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\sax\SAXDocumentSerializerWithPrefixMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
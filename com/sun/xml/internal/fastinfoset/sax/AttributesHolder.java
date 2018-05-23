package com.sun.xml.internal.fastinfoset.sax;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import java.io.IOException;
import java.util.Map;

public class AttributesHolder
  implements EncodingAlgorithmAttributes
{
  private static final int DEFAULT_CAPACITY = 8;
  private Map _registeredEncodingAlgorithms;
  private int _attributeCount;
  private QualifiedName[] _names = new QualifiedName[8];
  private String[] _values = new String[8];
  private String[] _algorithmURIs = new String[8];
  private int[] _algorithmIds = new int[8];
  private Object[] _algorithmData = new Object[8];
  
  public AttributesHolder() {}
  
  public AttributesHolder(Map paramMap)
  {
    this();
    _registeredEncodingAlgorithms = paramMap;
  }
  
  public final int getLength()
  {
    return _attributeCount;
  }
  
  public final String getLocalName(int paramInt)
  {
    return _names[paramInt].localName;
  }
  
  public final String getQName(int paramInt)
  {
    return _names[paramInt].getQNameString();
  }
  
  public final String getType(int paramInt)
  {
    return "CDATA";
  }
  
  public final String getURI(int paramInt)
  {
    return _names[paramInt].namespaceName;
  }
  
  public final String getValue(int paramInt)
  {
    String str = _values[paramInt];
    if (str != null) {
      return str;
    }
    if ((_algorithmData[paramInt] == null) || ((_algorithmIds[paramInt] >= 32) && (_registeredEncodingAlgorithms == null))) {
      return null;
    }
    try
    {
      return _values[paramInt] = convertEncodingAlgorithmDataToString(_algorithmIds[paramInt], _algorithmURIs[paramInt], _algorithmData[paramInt]).toString();
    }
    catch (IOException localIOException)
    {
      return null;
    }
    catch (FastInfosetException localFastInfosetException) {}
    return null;
  }
  
  public final int getIndex(String paramString)
  {
    int i = paramString.indexOf(':');
    String str1 = "";
    String str2 = paramString;
    if (i >= 0)
    {
      str1 = paramString.substring(0, i);
      str2 = paramString.substring(i + 1);
    }
    for (i = 0; i < _attributeCount; i++)
    {
      QualifiedName localQualifiedName = _names[i];
      if ((str2.equals(localName)) && (str1.equals(prefix))) {
        return i;
      }
    }
    return -1;
  }
  
  public final String getType(String paramString)
  {
    int i = getIndex(paramString);
    if (i >= 0) {
      return "CDATA";
    }
    return null;
  }
  
  public final String getValue(String paramString)
  {
    int i = getIndex(paramString);
    if (i >= 0) {
      return _values[i];
    }
    return null;
  }
  
  public final int getIndex(String paramString1, String paramString2)
  {
    for (int i = 0; i < _attributeCount; i++)
    {
      QualifiedName localQualifiedName = _names[i];
      if ((paramString2.equals(localName)) && (paramString1.equals(namespaceName))) {
        return i;
      }
    }
    return -1;
  }
  
  public final String getType(String paramString1, String paramString2)
  {
    int i = getIndex(paramString1, paramString2);
    if (i >= 0) {
      return "CDATA";
    }
    return null;
  }
  
  public final String getValue(String paramString1, String paramString2)
  {
    int i = getIndex(paramString1, paramString2);
    if (i >= 0) {
      return _values[i];
    }
    return null;
  }
  
  public final void clear()
  {
    for (int i = 0; i < _attributeCount; i++)
    {
      _values[i] = null;
      _algorithmData[i] = null;
    }
    _attributeCount = 0;
  }
  
  public final String getAlgorithmURI(int paramInt)
  {
    return _algorithmURIs[paramInt];
  }
  
  public final int getAlgorithmIndex(int paramInt)
  {
    return _algorithmIds[paramInt];
  }
  
  public final Object getAlgorithmData(int paramInt)
  {
    return _algorithmData[paramInt];
  }
  
  public String getAlpababet(int paramInt)
  {
    return null;
  }
  
  public boolean getToIndex(int paramInt)
  {
    return false;
  }
  
  public final void addAttribute(QualifiedName paramQualifiedName, String paramString)
  {
    if (_attributeCount == _names.length) {
      resize();
    }
    _names[_attributeCount] = paramQualifiedName;
    _values[(_attributeCount++)] = paramString;
  }
  
  public final void addAttributeWithAlgorithmData(QualifiedName paramQualifiedName, String paramString, int paramInt, Object paramObject)
  {
    if (_attributeCount == _names.length) {
      resize();
    }
    _names[_attributeCount] = paramQualifiedName;
    _values[_attributeCount] = null;
    _algorithmURIs[_attributeCount] = paramString;
    _algorithmIds[_attributeCount] = paramInt;
    _algorithmData[(_attributeCount++)] = paramObject;
  }
  
  public final QualifiedName getQualifiedName(int paramInt)
  {
    return _names[paramInt];
  }
  
  public final String getPrefix(int paramInt)
  {
    return _names[paramInt].prefix;
  }
  
  private final void resize()
  {
    int i = _attributeCount * 3 / 2 + 1;
    QualifiedName[] arrayOfQualifiedName = new QualifiedName[i];
    String[] arrayOfString1 = new String[i];
    String[] arrayOfString2 = new String[i];
    int[] arrayOfInt = new int[i];
    Object[] arrayOfObject = new Object[i];
    System.arraycopy(_names, 0, arrayOfQualifiedName, 0, _attributeCount);
    System.arraycopy(_values, 0, arrayOfString1, 0, _attributeCount);
    System.arraycopy(_algorithmURIs, 0, arrayOfString2, 0, _attributeCount);
    System.arraycopy(_algorithmIds, 0, arrayOfInt, 0, _attributeCount);
    System.arraycopy(_algorithmData, 0, arrayOfObject, 0, _attributeCount);
    _names = arrayOfQualifiedName;
    _values = arrayOfString1;
    _algorithmURIs = arrayOfString2;
    _algorithmIds = arrayOfInt;
    _algorithmData = arrayOfObject;
  }
  
  private final StringBuffer convertEncodingAlgorithmDataToString(int paramInt, String paramString, Object paramObject)
    throws FastInfosetException, IOException
  {
    Object localObject = null;
    if (paramInt < 9)
    {
      localObject = BuiltInEncodingAlgorithmFactory.getAlgorithm(paramInt);
    }
    else
    {
      if (paramInt == 9) {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.CDATAAlgorithmNotSupported"));
      }
      if (paramInt >= 32)
      {
        if (paramString == null) {
          throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent") + paramInt);
        }
        localObject = (EncodingAlgorithm)_registeredEncodingAlgorithms.get(paramString);
        if (localObject == null) {
          throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmNotRegistered") + paramString);
        }
      }
      else
      {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
      }
    }
    StringBuffer localStringBuffer = new StringBuffer();
    ((EncodingAlgorithm)localObject).convertToCharacters(paramObject, localStringBuffer);
    return localStringBuffer;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\sax\AttributesHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
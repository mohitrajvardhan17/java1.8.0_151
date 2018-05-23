package com.sun.xml.internal.org.jvnet.fastinfoset.sax.helpers;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import java.io.IOException;
import java.util.Map;
import org.xml.sax.Attributes;

public class EncodingAlgorithmAttributesImpl
  implements EncodingAlgorithmAttributes
{
  private static final int DEFAULT_CAPACITY = 8;
  private static final int URI_OFFSET = 0;
  private static final int LOCALNAME_OFFSET = 1;
  private static final int QNAME_OFFSET = 2;
  private static final int TYPE_OFFSET = 3;
  private static final int VALUE_OFFSET = 4;
  private static final int ALGORITHMURI_OFFSET = 5;
  private static final int SIZE = 6;
  private Map _registeredEncodingAlgorithms;
  private int _length;
  private String[] _data = new String[48];
  private int[] _algorithmIds = new int[8];
  private Object[] _algorithmData = new Object[8];
  private String[] _alphabets = new String[8];
  private boolean[] _toIndex = new boolean[8];
  
  public EncodingAlgorithmAttributesImpl()
  {
    this(null, null);
  }
  
  public EncodingAlgorithmAttributesImpl(Attributes paramAttributes)
  {
    this(null, paramAttributes);
  }
  
  public EncodingAlgorithmAttributesImpl(Map paramMap, Attributes paramAttributes)
  {
    _registeredEncodingAlgorithms = paramMap;
    if (paramAttributes != null) {
      if ((paramAttributes instanceof EncodingAlgorithmAttributes)) {
        setAttributes((EncodingAlgorithmAttributes)paramAttributes);
      } else {
        setAttributes(paramAttributes);
      }
    }
  }
  
  public final void clear()
  {
    for (int i = 0; i < _length; i++)
    {
      _data[(i * 6 + 4)] = null;
      _algorithmData[i] = null;
    }
    _length = 0;
  }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    if (_length >= _algorithmData.length) {
      resize();
    }
    int i = _length * 6;
    _data[(i++)] = replaceNull(paramString1);
    _data[(i++)] = replaceNull(paramString2);
    _data[(i++)] = replaceNull(paramString3);
    _data[(i++)] = replaceNull(paramString4);
    _data[(i++)] = replaceNull(paramString5);
    _toIndex[_length] = false;
    _alphabets[_length] = null;
    _length += 1;
  }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean, String paramString6)
  {
    if (_length >= _algorithmData.length) {
      resize();
    }
    int i = _length * 6;
    _data[(i++)] = replaceNull(paramString1);
    _data[(i++)] = replaceNull(paramString2);
    _data[(i++)] = replaceNull(paramString3);
    _data[(i++)] = replaceNull(paramString4);
    _data[(i++)] = replaceNull(paramString5);
    _toIndex[_length] = paramBoolean;
    _alphabets[_length] = paramString6;
    _length += 1;
  }
  
  public void addAttributeWithBuiltInAlgorithmData(String paramString1, String paramString2, String paramString3, int paramInt, Object paramObject)
  {
    if (_length >= _algorithmData.length) {
      resize();
    }
    int i = _length * 6;
    _data[(i++)] = replaceNull(paramString1);
    _data[(i++)] = replaceNull(paramString2);
    _data[(i++)] = replaceNull(paramString3);
    _data[(i++)] = "CDATA";
    _data[(i++)] = "";
    _data[(i++)] = null;
    _algorithmIds[_length] = paramInt;
    _algorithmData[_length] = paramObject;
    _toIndex[_length] = false;
    _alphabets[_length] = null;
    _length += 1;
  }
  
  public void addAttributeWithAlgorithmData(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt, Object paramObject)
  {
    if (_length >= _algorithmData.length) {
      resize();
    }
    int i = _length * 6;
    _data[(i++)] = replaceNull(paramString1);
    _data[(i++)] = replaceNull(paramString2);
    _data[(i++)] = replaceNull(paramString3);
    _data[(i++)] = "CDATA";
    _data[(i++)] = "";
    _data[(i++)] = paramString4;
    _algorithmIds[_length] = paramInt;
    _algorithmData[_length] = paramObject;
    _toIndex[_length] = false;
    _alphabets[_length] = null;
    _length += 1;
  }
  
  public void replaceWithAttributeAlgorithmData(int paramInt1, String paramString, int paramInt2, Object paramObject)
  {
    if ((paramInt1 < 0) || (paramInt1 >= _length)) {
      return;
    }
    int i = paramInt1 * 6;
    _data[(i + 4)] = null;
    _data[(i + 5)] = paramString;
    _algorithmIds[paramInt1] = paramInt2;
    _algorithmData[paramInt1] = paramObject;
    _toIndex[paramInt1] = false;
    _alphabets[paramInt1] = null;
  }
  
  public void setAttributes(Attributes paramAttributes)
  {
    _length = paramAttributes.getLength();
    if (_length > 0)
    {
      if (_length >= _algorithmData.length) {
        resizeNoCopy();
      }
      int i = 0;
      for (int j = 0; j < _length; j++)
      {
        _data[(i++)] = paramAttributes.getURI(j);
        _data[(i++)] = paramAttributes.getLocalName(j);
        _data[(i++)] = paramAttributes.getQName(j);
        _data[(i++)] = paramAttributes.getType(j);
        _data[(i++)] = paramAttributes.getValue(j);
        i++;
        _toIndex[j] = false;
        _alphabets[j] = null;
      }
    }
  }
  
  public void setAttributes(EncodingAlgorithmAttributes paramEncodingAlgorithmAttributes)
  {
    _length = paramEncodingAlgorithmAttributes.getLength();
    if (_length > 0)
    {
      if (_length >= _algorithmData.length) {
        resizeNoCopy();
      }
      int i = 0;
      for (int j = 0; j < _length; j++)
      {
        _data[(i++)] = paramEncodingAlgorithmAttributes.getURI(j);
        _data[(i++)] = paramEncodingAlgorithmAttributes.getLocalName(j);
        _data[(i++)] = paramEncodingAlgorithmAttributes.getQName(j);
        _data[(i++)] = paramEncodingAlgorithmAttributes.getType(j);
        _data[(i++)] = paramEncodingAlgorithmAttributes.getValue(j);
        _data[(i++)] = paramEncodingAlgorithmAttributes.getAlgorithmURI(j);
        _algorithmIds[j] = paramEncodingAlgorithmAttributes.getAlgorithmIndex(j);
        _algorithmData[j] = paramEncodingAlgorithmAttributes.getAlgorithmData(j);
        _toIndex[j] = false;
        _alphabets[j] = null;
      }
    }
  }
  
  public final int getLength()
  {
    return _length;
  }
  
  public final String getLocalName(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < _length)) {
      return _data[(paramInt * 6 + 1)];
    }
    return null;
  }
  
  public final String getQName(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < _length)) {
      return _data[(paramInt * 6 + 2)];
    }
    return null;
  }
  
  public final String getType(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < _length)) {
      return _data[(paramInt * 6 + 3)];
    }
    return null;
  }
  
  public final String getURI(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < _length)) {
      return _data[(paramInt * 6 + 0)];
    }
    return null;
  }
  
  public final String getValue(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < _length))
    {
      String str = _data[(paramInt * 6 + 4)];
      if (str != null) {
        return str;
      }
    }
    else
    {
      return null;
    }
    if ((_algorithmData[paramInt] == null) || (_registeredEncodingAlgorithms == null)) {
      return null;
    }
    try
    {
      return _data[(paramInt * 6 + 4)] = convertEncodingAlgorithmDataToString(_algorithmIds[paramInt], _data[(paramInt * 6 + 5)], _algorithmData[paramInt]).toString();
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
    for (int i = 0; i < _length; i++) {
      if (paramString.equals(_data[(i * 6 + 2)])) {
        return i;
      }
    }
    return -1;
  }
  
  public final String getType(String paramString)
  {
    int i = getIndex(paramString);
    if (i >= 0) {
      return _data[(i * 6 + 3)];
    }
    return null;
  }
  
  public final String getValue(String paramString)
  {
    int i = getIndex(paramString);
    if (i >= 0) {
      return getValue(i);
    }
    return null;
  }
  
  public final int getIndex(String paramString1, String paramString2)
  {
    for (int i = 0; i < _length; i++) {
      if ((paramString2.equals(_data[(i * 6 + 1)])) && (paramString1.equals(_data[(i * 6 + 0)]))) {
        return i;
      }
    }
    return -1;
  }
  
  public final String getType(String paramString1, String paramString2)
  {
    int i = getIndex(paramString1, paramString2);
    if (i >= 0) {
      return _data[(i * 6 + 3)];
    }
    return null;
  }
  
  public final String getValue(String paramString1, String paramString2)
  {
    int i = getIndex(paramString1, paramString2);
    if (i >= 0) {
      return getValue(i);
    }
    return null;
  }
  
  public final String getAlgorithmURI(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < _length)) {
      return _data[(paramInt * 6 + 5)];
    }
    return null;
  }
  
  public final int getAlgorithmIndex(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < _length)) {
      return _algorithmIds[paramInt];
    }
    return -1;
  }
  
  public final Object getAlgorithmData(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < _length)) {
      return _algorithmData[paramInt];
    }
    return null;
  }
  
  public final String getAlpababet(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < _length)) {
      return _alphabets[paramInt];
    }
    return null;
  }
  
  public final boolean getToIndex(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < _length)) {
      return _toIndex[paramInt];
    }
    return false;
  }
  
  private final String replaceNull(String paramString)
  {
    return paramString != null ? paramString : "";
  }
  
  private final void resizeNoCopy()
  {
    int i = _length * 3 / 2 + 1;
    _data = new String[i * 6];
    _algorithmIds = new int[i];
    _algorithmData = new Object[i];
  }
  
  private final void resize()
  {
    int i = _length * 3 / 2 + 1;
    String[] arrayOfString1 = new String[i * 6];
    int[] arrayOfInt = new int[i];
    Object[] arrayOfObject = new Object[i];
    String[] arrayOfString2 = new String[i];
    boolean[] arrayOfBoolean = new boolean[i];
    System.arraycopy(_data, 0, arrayOfString1, 0, _length * 6);
    System.arraycopy(_algorithmIds, 0, arrayOfInt, 0, _length);
    System.arraycopy(_algorithmData, 0, arrayOfObject, 0, _length);
    System.arraycopy(_alphabets, 0, arrayOfString2, 0, _length);
    System.arraycopy(_toIndex, 0, arrayOfBoolean, 0, _length);
    _data = arrayOfString1;
    _algorithmIds = arrayOfInt;
    _algorithmData = arrayOfObject;
    _alphabets = arrayOfString2;
    _toIndex = arrayOfBoolean;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\sax\helpers\EncodingAlgorithmAttributesImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.util.EncodingMap;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class Encodings
{
  static final int DEFAULT_LAST_PRINTABLE = 127;
  static final int LAST_PRINTABLE_UNICODE = 65535;
  static final String[] UNICODE_ENCODINGS = { "Unicode", "UnicodeBig", "UnicodeLittle", "GB2312", "UTF8", "UTF-16" };
  static final String DEFAULT_ENCODING = "UTF8";
  private static final Map<String, EncodingInfo> _encodings = new ConcurrentHashMap();
  static final String JIS_DANGER_CHARS = "\\~¢£¥¬—―‖…‾‾∥∯〜＼～￠￡￢￣";
  
  Encodings() {}
  
  static EncodingInfo getEncodingInfo(String paramString, boolean paramBoolean)
    throws UnsupportedEncodingException
  {
    EncodingInfo localEncodingInfo = null;
    if (paramString == null)
    {
      if ((localEncodingInfo = (EncodingInfo)_encodings.get("UTF8")) != null) {
        return localEncodingInfo;
      }
      localEncodingInfo = new EncodingInfo(EncodingMap.getJava2IANAMapping("UTF8"), "UTF8", 65535);
      _encodings.put("UTF8", localEncodingInfo);
      return localEncodingInfo;
    }
    paramString = paramString.toUpperCase(Locale.ENGLISH);
    String str = EncodingMap.getIANA2JavaMapping(paramString);
    if (str == null)
    {
      if (paramBoolean)
      {
        EncodingInfo.testJavaEncodingName(paramString);
        if ((localEncodingInfo = (EncodingInfo)_encodings.get(paramString)) != null) {
          return localEncodingInfo;
        }
        for (i = 0; i < UNICODE_ENCODINGS.length; i++) {
          if (UNICODE_ENCODINGS[i].equalsIgnoreCase(paramString))
          {
            localEncodingInfo = new EncodingInfo(EncodingMap.getJava2IANAMapping(paramString), paramString, 65535);
            break;
          }
        }
        if (i == UNICODE_ENCODINGS.length) {
          localEncodingInfo = new EncodingInfo(EncodingMap.getJava2IANAMapping(paramString), paramString, 127);
        }
        _encodings.put(paramString, localEncodingInfo);
        return localEncodingInfo;
      }
      throw new UnsupportedEncodingException(paramString);
    }
    if ((localEncodingInfo = (EncodingInfo)_encodings.get(str)) != null) {
      return localEncodingInfo;
    }
    for (int i = 0; i < UNICODE_ENCODINGS.length; i++) {
      if (UNICODE_ENCODINGS[i].equalsIgnoreCase(str))
      {
        localEncodingInfo = new EncodingInfo(paramString, str, 65535);
        break;
      }
    }
    if (i == UNICODE_ENCODINGS.length) {
      localEncodingInfo = new EncodingInfo(paramString, str, 127);
    }
    _encodings.put(str, localEncodingInfo);
    return localEncodingInfo;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serialize\Encodings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
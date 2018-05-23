package com.sun.corba.se.impl.encoding;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Map;
import java.util.WeakHashMap;

class CodeSetCache
{
  private ThreadLocal converterCaches = new ThreadLocal()
  {
    public Object initialValue()
    {
      return new Map[] { new WeakHashMap(), new WeakHashMap() };
    }
  };
  private static final int BTC_CACHE_MAP = 0;
  private static final int CTB_CACHE_MAP = 1;
  
  CodeSetCache() {}
  
  CharsetDecoder getByteToCharConverter(Object paramObject)
  {
    Map localMap = ((Map[])(Map[])converterCaches.get())[0];
    return (CharsetDecoder)localMap.get(paramObject);
  }
  
  CharsetEncoder getCharToByteConverter(Object paramObject)
  {
    Map localMap = ((Map[])(Map[])converterCaches.get())[1];
    return (CharsetEncoder)localMap.get(paramObject);
  }
  
  CharsetDecoder setConverter(Object paramObject, CharsetDecoder paramCharsetDecoder)
  {
    Map localMap = ((Map[])(Map[])converterCaches.get())[0];
    localMap.put(paramObject, paramCharsetDecoder);
    return paramCharsetDecoder;
  }
  
  CharsetEncoder setConverter(Object paramObject, CharsetEncoder paramCharsetEncoder)
  {
    Map localMap = ((Map[])(Map[])converterCaches.get())[1];
    localMap.put(paramObject, paramCharsetEncoder);
    return paramCharsetEncoder;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\CodeSetCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
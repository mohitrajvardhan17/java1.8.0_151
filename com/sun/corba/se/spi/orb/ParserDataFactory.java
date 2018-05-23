package com.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.orb.NormalParserData;
import com.sun.corba.se.impl.orb.PrefixParserData;

public class ParserDataFactory
{
  public ParserDataFactory() {}
  
  public static ParserData make(String paramString1, Operation paramOperation, String paramString2, Object paramObject1, Object paramObject2, String paramString3)
  {
    return new NormalParserData(paramString1, paramOperation, paramString2, paramObject1, paramObject2, paramString3);
  }
  
  public static ParserData make(String paramString1, Operation paramOperation, String paramString2, Object paramObject1, Object paramObject2, StringPair[] paramArrayOfStringPair, Class paramClass)
  {
    return new PrefixParserData(paramString1, paramOperation, paramString2, paramObject1, paramObject2, paramArrayOfStringPair, paramClass);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orb\ParserDataFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
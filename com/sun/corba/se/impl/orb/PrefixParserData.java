package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.PropertyParser;
import com.sun.corba.se.spi.orb.StringPair;
import java.util.Properties;

public class PrefixParserData
  extends ParserDataBase
{
  private StringPair[] testData;
  private Class componentType;
  
  public PrefixParserData(String paramString1, Operation paramOperation, String paramString2, Object paramObject1, Object paramObject2, StringPair[] paramArrayOfStringPair, Class paramClass)
  {
    super(paramString1, paramOperation, paramString2, paramObject1, paramObject2);
    testData = paramArrayOfStringPair;
    componentType = paramClass;
  }
  
  public void addToParser(PropertyParser paramPropertyParser)
  {
    paramPropertyParser.addPrefix(getPropertyName(), getOperation(), getFieldName(), componentType);
  }
  
  public void addToProperties(Properties paramProperties)
  {
    for (int i = 0; i < testData.length; i++)
    {
      StringPair localStringPair = testData[i];
      String str = getPropertyName();
      if (str.charAt(str.length() - 1) != '.') {
        str = str + ".";
      }
      paramProperties.setProperty(str + localStringPair.getFirst(), localStringPair.getSecond());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orb\PrefixParserData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
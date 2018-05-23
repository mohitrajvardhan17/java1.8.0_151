package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.PropertyParser;
import java.util.Properties;

public class NormalParserData
  extends ParserDataBase
{
  private String testData;
  
  public NormalParserData(String paramString1, Operation paramOperation, String paramString2, Object paramObject1, Object paramObject2, String paramString3)
  {
    super(paramString1, paramOperation, paramString2, paramObject1, paramObject2);
    testData = paramString3;
  }
  
  public void addToParser(PropertyParser paramPropertyParser)
  {
    paramPropertyParser.add(getPropertyName(), getOperation(), getFieldName());
  }
  
  public void addToProperties(Properties paramProperties)
  {
    paramProperties.setProperty(getPropertyName(), testData);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orb\NormalParserData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
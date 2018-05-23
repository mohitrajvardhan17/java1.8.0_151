package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.Operation;
import java.util.Properties;

public class NormalParserAction
  extends ParserActionBase
{
  public NormalParserAction(String paramString1, Operation paramOperation, String paramString2)
  {
    super(paramString1, false, paramOperation, paramString2);
  }
  
  public Object apply(Properties paramProperties)
  {
    String str = paramProperties.getProperty(getPropertyName());
    if (str != null) {
      return getOperation().operate(str);
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orb\NormalParserAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
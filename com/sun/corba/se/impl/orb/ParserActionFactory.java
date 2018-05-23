package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.Operation;

public class ParserActionFactory
{
  private ParserActionFactory() {}
  
  public static ParserAction makeNormalAction(String paramString1, Operation paramOperation, String paramString2)
  {
    return new NormalParserAction(paramString1, paramOperation, paramString2);
  }
  
  public static ParserAction makePrefixAction(String paramString1, Operation paramOperation, String paramString2, Class paramClass)
  {
    return new PrefixParserAction(paramString1, paramOperation, paramString2, paramClass);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orb\ParserActionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.xml.internal.fastinfoset.stax.events;

public class Util
{
  public Util() {}
  
  public static boolean isEmptyString(String paramString)
  {
    return (paramString == null) || (paramString.equals(""));
  }
  
  public static final String getEventTypeString(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return "START_ELEMENT";
    case 2: 
      return "END_ELEMENT";
    case 3: 
      return "PROCESSING_INSTRUCTION";
    case 4: 
      return "CHARACTERS";
    case 5: 
      return "COMMENT";
    case 7: 
      return "START_DOCUMENT";
    case 8: 
      return "END_DOCUMENT";
    case 9: 
      return "ENTITY_REFERENCE";
    case 10: 
      return "ATTRIBUTE";
    case 11: 
      return "DTD";
    case 12: 
      return "CDATA";
    }
    return "UNKNOWN_EVENT_TYPE";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
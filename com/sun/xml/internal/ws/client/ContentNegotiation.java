package com.sun.xml.internal.ws.client;

public enum ContentNegotiation
{
  none,  pessimistic,  optimistic;
  
  public static final String PROPERTY = "com.sun.xml.internal.ws.client.ContentNegotiation";
  
  private ContentNegotiation() {}
  
  public static ContentNegotiation obtainFromSystemProperty()
  {
    try
    {
      String str = System.getProperty("com.sun.xml.internal.ws.client.ContentNegotiation");
      if (str == null) {
        return none;
      }
      return valueOf(str);
    }
    catch (Exception localException) {}
    return none;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\ContentNegotiation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
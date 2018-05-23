package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

class UniqueValue
{
  private static int part = 0;
  
  UniqueValue() {}
  
  public static String getUniqueBoundaryValue()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("----=_Part_").append(part++).append("_").append(localStringBuffer.hashCode()).append('.').append(System.currentTimeMillis());
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\UniqueValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
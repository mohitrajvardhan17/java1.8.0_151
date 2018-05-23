package com.sun.corba.se.spi.orb;

public class StringPair
{
  private String first;
  private String second;
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof StringPair)) {
      return false;
    }
    StringPair localStringPair = (StringPair)paramObject;
    return (first.equals(first)) && (second.equals(second));
  }
  
  public int hashCode()
  {
    return first.hashCode() ^ second.hashCode();
  }
  
  public StringPair(String paramString1, String paramString2)
  {
    first = paramString1;
    second = paramString2;
  }
  
  public String getFirst()
  {
    return first;
  }
  
  public String getSecond()
  {
    return second;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orb\StringPair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
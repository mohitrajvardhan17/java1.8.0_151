package com.sun.corba.se.impl.encoding;

public final class OSFCodeSetRegistry
{
  public static final int ISO_8859_1_VALUE = 65537;
  public static final int UTF_16_VALUE = 65801;
  public static final int UTF_8_VALUE = 83951617;
  public static final int UCS_2_VALUE = 65792;
  public static final int ISO_646_VALUE = 65568;
  public static final Entry ISO_8859_1 = new Entry("ISO-8859-1", 65537, true, 1, null);
  static final Entry UTF_16BE = new Entry("UTF-16BE", -1, true, 2, null);
  static final Entry UTF_16LE = new Entry("UTF-16LE", -2, true, 2, null);
  public static final Entry UTF_16 = new Entry("UTF-16", 65801, true, 4, null);
  public static final Entry UTF_8 = new Entry("UTF-8", 83951617, false, 6, null);
  public static final Entry UCS_2 = new Entry("UCS-2", 65792, true, 2, null);
  public static final Entry ISO_646 = new Entry("US-ASCII", 65568, true, 1, null);
  
  private OSFCodeSetRegistry() {}
  
  public static Entry lookupEntry(int paramInt)
  {
    switch (paramInt)
    {
    case 65537: 
      return ISO_8859_1;
    case 65801: 
      return UTF_16;
    case 83951617: 
      return UTF_8;
    case 65568: 
      return ISO_646;
    case 65792: 
      return UCS_2;
    }
    return null;
  }
  
  public static final class Entry
  {
    private String javaName;
    private int encodingNum;
    private boolean isFixedWidth;
    private int maxBytesPerChar;
    
    private Entry(String paramString, int paramInt1, boolean paramBoolean, int paramInt2)
    {
      javaName = paramString;
      encodingNum = paramInt1;
      isFixedWidth = paramBoolean;
      maxBytesPerChar = paramInt2;
    }
    
    public String getName()
    {
      return javaName;
    }
    
    public int getNumber()
    {
      return encodingNum;
    }
    
    public boolean isFixedWidth()
    {
      return isFixedWidth;
    }
    
    public int getMaxBytesPerChar()
    {
      return maxBytesPerChar;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof Entry)) {
        return false;
      }
      Entry localEntry = (Entry)paramObject;
      return (javaName.equals(javaName)) && (encodingNum == encodingNum) && (isFixedWidth == isFixedWidth) && (maxBytesPerChar == maxBytesPerChar);
    }
    
    public int hashCode()
    {
      return encodingNum;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\OSFCodeSetRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
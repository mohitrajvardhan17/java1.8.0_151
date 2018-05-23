package com.sun.org.apache.xml.internal.dtm;

public abstract interface DTMWSFilter
{
  public static final short NOTSTRIP = 1;
  public static final short STRIP = 2;
  public static final short INHERIT = 3;
  
  public abstract short getShouldStripSpace(int paramInt, DTM paramDTM);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\DTMWSFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
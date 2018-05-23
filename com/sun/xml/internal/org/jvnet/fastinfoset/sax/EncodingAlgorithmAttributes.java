package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import org.xml.sax.Attributes;

public abstract interface EncodingAlgorithmAttributes
  extends Attributes
{
  public abstract String getAlgorithmURI(int paramInt);
  
  public abstract int getAlgorithmIndex(int paramInt);
  
  public abstract Object getAlgorithmData(int paramInt);
  
  public abstract String getAlpababet(int paramInt);
  
  public abstract boolean getToIndex(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\sax\EncodingAlgorithmAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
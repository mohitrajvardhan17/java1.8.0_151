package com.sun.org.apache.xalan.internal.xsltc.runtime.output;

abstract interface OutputBuffer
{
  public abstract String close();
  
  public abstract OutputBuffer append(char paramChar);
  
  public abstract OutputBuffer append(String paramString);
  
  public abstract OutputBuffer append(char[] paramArrayOfChar, int paramInt1, int paramInt2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\runtime\output\OutputBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
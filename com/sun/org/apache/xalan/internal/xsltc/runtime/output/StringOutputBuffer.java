package com.sun.org.apache.xalan.internal.xsltc.runtime.output;

class StringOutputBuffer
  implements OutputBuffer
{
  private StringBuffer _buffer = new StringBuffer();
  
  public StringOutputBuffer() {}
  
  public String close()
  {
    return _buffer.toString();
  }
  
  public OutputBuffer append(String paramString)
  {
    _buffer.append(paramString);
    return this;
  }
  
  public OutputBuffer append(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    _buffer.append(paramArrayOfChar, paramInt1, paramInt2);
    return this;
  }
  
  public OutputBuffer append(char paramChar)
  {
    _buffer.append(paramChar);
    return this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\runtime\output\StringOutputBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
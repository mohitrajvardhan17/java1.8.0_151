package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.runtime.output.Pcdata;
import com.sun.xml.internal.bind.v2.runtime.output.UTF8XmlOutput;
import java.io.IOException;

public final class IntArrayData
  extends Pcdata
{
  private int[] data;
  private int start;
  private int len;
  private StringBuilder literal;
  
  public IntArrayData(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    set(paramArrayOfInt, paramInt1, paramInt2);
  }
  
  public IntArrayData() {}
  
  public void set(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    data = paramArrayOfInt;
    start = paramInt1;
    len = paramInt2;
    literal = null;
  }
  
  public int length()
  {
    return getLiteral().length();
  }
  
  public char charAt(int paramInt)
  {
    return getLiteral().charAt(paramInt);
  }
  
  public CharSequence subSequence(int paramInt1, int paramInt2)
  {
    return getLiteral().subSequence(paramInt1, paramInt2);
  }
  
  private StringBuilder getLiteral()
  {
    if (literal != null) {
      return literal;
    }
    literal = new StringBuilder();
    int i = start;
    for (int j = len; j > 0; j--)
    {
      if (literal.length() > 0) {
        literal.append(' ');
      }
      literal.append(data[(i++)]);
    }
    return literal;
  }
  
  public String toString()
  {
    return literal.toString();
  }
  
  public void writeTo(UTF8XmlOutput paramUTF8XmlOutput)
    throws IOException
  {
    int i = start;
    for (int j = len; j > 0; j--)
    {
      if (j != len) {
        paramUTF8XmlOutput.write(32);
      }
      paramUTF8XmlOutput.text(data[(i++)]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\IntArrayData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.runtime.output.Pcdata;
import com.sun.xml.internal.bind.v2.runtime.output.UTF8XmlOutput;
import java.io.IOException;

public class IntData
  extends Pcdata
{
  private int data;
  private int length;
  private static final int[] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE };
  
  public IntData() {}
  
  public void reset(int paramInt)
  {
    data = paramInt;
    if (paramInt == Integer.MIN_VALUE) {
      length = 11;
    } else {
      length = (paramInt < 0 ? stringSizeOfInt(-paramInt) + 1 : stringSizeOfInt(paramInt));
    }
  }
  
  private static int stringSizeOfInt(int paramInt)
  {
    for (int i = 0;; i++) {
      if (paramInt <= sizeTable[i]) {
        return i + 1;
      }
    }
  }
  
  public String toString()
  {
    return String.valueOf(data);
  }
  
  public int length()
  {
    return length;
  }
  
  public char charAt(int paramInt)
  {
    return toString().charAt(paramInt);
  }
  
  public CharSequence subSequence(int paramInt1, int paramInt2)
  {
    return toString().substring(paramInt1, paramInt2);
  }
  
  public void writeTo(UTF8XmlOutput paramUTF8XmlOutput)
    throws IOException
  {
    paramUTF8XmlOutput.text(data);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\IntData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
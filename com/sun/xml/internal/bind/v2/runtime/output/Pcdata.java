package com.sun.xml.internal.bind.v2.runtime.output;

import java.io.IOException;

public abstract class Pcdata
  implements CharSequence
{
  public Pcdata() {}
  
  public abstract void writeTo(UTF8XmlOutput paramUTF8XmlOutput)
    throws IOException;
  
  public void writeTo(char[] paramArrayOfChar, int paramInt)
  {
    toString().getChars(0, length(), paramArrayOfChar, paramInt);
  }
  
  public abstract String toString();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\Pcdata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
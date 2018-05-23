package com.sun.xml.internal.bind.marshaller;

import java.io.IOException;
import java.io.Writer;

public class DumbEscapeHandler
  implements CharacterEscapeHandler
{
  public static final CharacterEscapeHandler theInstance = new DumbEscapeHandler();
  
  private DumbEscapeHandler() {}
  
  public void escape(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean, Writer paramWriter)
    throws IOException
  {
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++) {
      switch (paramArrayOfChar[j])
      {
      case '&': 
        paramWriter.write("&amp;");
        break;
      case '<': 
        paramWriter.write("&lt;");
        break;
      case '>': 
        paramWriter.write("&gt;");
        break;
      case '"': 
        if (paramBoolean) {
          paramWriter.write("&quot;");
        } else {
          paramWriter.write(34);
        }
        break;
      default: 
        if (paramArrayOfChar[j] > '')
        {
          paramWriter.write("&#");
          paramWriter.write(Integer.toString(paramArrayOfChar[j]));
          paramWriter.write(59);
        }
        else
        {
          paramWriter.write(paramArrayOfChar[j]);
        }
        break;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\marshaller\DumbEscapeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
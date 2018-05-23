package com.sun.xml.internal.bind.marshaller;

import java.io.IOException;
import java.io.Writer;

public class MinimumEscapeHandler
  implements CharacterEscapeHandler
{
  public static final CharacterEscapeHandler theInstance = new MinimumEscapeHandler();
  
  private MinimumEscapeHandler() {}
  
  public void escape(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean, Writer paramWriter)
    throws IOException
  {
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++)
    {
      int k = paramArrayOfChar[j];
      if ((k == 38) || (k == 60) || (k == 62) || (k == 13) || ((k == 34) && (paramBoolean)))
      {
        if (j != paramInt1) {
          paramWriter.write(paramArrayOfChar, paramInt1, j - paramInt1);
        }
        paramInt1 = j + 1;
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
          paramWriter.write("&quot;");
        }
      }
    }
    if (paramInt1 != i) {
      paramWriter.write(paramArrayOfChar, paramInt1, i - paramInt1);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\marshaller\MinimumEscapeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.org.apache.regexp.internal;

import java.io.Serializable;

public class REProgram
  implements Serializable
{
  static final int OPT_HASBACKREFS = 1;
  char[] instruction;
  int lenInstruction;
  char[] prefix;
  int flags;
  int maxParens = -1;
  
  public REProgram(char[] paramArrayOfChar)
  {
    this(paramArrayOfChar, paramArrayOfChar.length);
  }
  
  public REProgram(int paramInt, char[] paramArrayOfChar)
  {
    this(paramArrayOfChar, paramArrayOfChar.length);
  }
  
  public REProgram(char[] paramArrayOfChar, int paramInt)
  {
    setInstructions(paramArrayOfChar, paramInt);
  }
  
  public char[] getInstructions()
  {
    if (lenInstruction != 0)
    {
      char[] arrayOfChar = new char[lenInstruction];
      System.arraycopy(instruction, 0, arrayOfChar, 0, lenInstruction);
      return arrayOfChar;
    }
    return null;
  }
  
  public void setInstructions(char[] paramArrayOfChar, int paramInt)
  {
    instruction = paramArrayOfChar;
    lenInstruction = paramInt;
    flags = 0;
    prefix = null;
    if ((paramArrayOfChar != null) && (paramInt != 0))
    {
      if ((paramInt >= 3) && (paramArrayOfChar[0] == '|'))
      {
        i = paramArrayOfChar[2];
        if ((paramArrayOfChar[(i + 0)] == 'E') && (paramInt >= 6) && (paramArrayOfChar[3] == 'A'))
        {
          int j = paramArrayOfChar[4];
          prefix = new char[j];
          System.arraycopy(paramArrayOfChar, 6, prefix, 0, j);
        }
      }
      for (int i = 0; i < paramInt; i += 3) {
        switch (paramArrayOfChar[(i + 0)])
        {
        case '[': 
          i += paramArrayOfChar[(i + 1)] * '\002';
          break;
        case 'A': 
          i += paramArrayOfChar[(i + 1)];
          break;
        case '#': 
          flags |= 0x1;
          return;
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\regexp\internal\REProgram.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
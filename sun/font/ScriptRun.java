package sun.font;

public final class ScriptRun
{
  private char[] text;
  private int textStart;
  private int textLimit;
  private int scriptStart;
  private int scriptLimit;
  private int scriptCode;
  private int[] stack;
  private int parenSP;
  static final int SURROGATE_START = 65536;
  static final int LEAD_START = 55296;
  static final int LEAD_LIMIT = 56320;
  static final int TAIL_START = 56320;
  static final int TAIL_LIMIT = 57344;
  static final int LEAD_SURROGATE_SHIFT = 10;
  static final int SURROGATE_OFFSET = -56613888;
  static final int DONE = -1;
  private static int[] pairedChars = { 40, 41, 60, 62, 91, 93, 123, 125, 171, 187, 8216, 8217, 8220, 8221, 8249, 8250, 12296, 12297, 12298, 12299, 12300, 12301, 12302, 12303, 12304, 12305, 12308, 12309, 12310, 12311, 12312, 12313, 12314, 12315 };
  private static final int pairedCharPower = 1 << highBit(pairedChars.length);
  private static final int pairedCharExtra = pairedChars.length - pairedCharPower;
  
  public ScriptRun() {}
  
  public ScriptRun(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    init(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void init(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if ((paramArrayOfChar == null) || (paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfChar.length - paramInt1)) {
      throw new IllegalArgumentException();
    }
    text = paramArrayOfChar;
    textStart = paramInt1;
    textLimit = (paramInt1 + paramInt2);
    scriptStart = textStart;
    scriptLimit = textStart;
    scriptCode = -1;
    parenSP = 0;
  }
  
  public final int getScriptStart()
  {
    return scriptStart;
  }
  
  public final int getScriptLimit()
  {
    return scriptLimit;
  }
  
  public final int getScriptCode()
  {
    return scriptCode;
  }
  
  public final boolean next()
  {
    int i = parenSP;
    if (scriptLimit >= textLimit) {
      return false;
    }
    scriptCode = 0;
    scriptStart = scriptLimit;
    int j;
    while ((j = nextCodePoint()) != -1)
    {
      int k = ScriptRunData.getScript(j);
      int m = k == 0 ? getPairIndex(j) : -1;
      if (m >= 0) {
        if ((m & 0x1) == 0)
        {
          if (stack == null)
          {
            stack = new int[32];
          }
          else if (parenSP == stack.length)
          {
            int[] arrayOfInt = new int[stack.length + 32];
            System.arraycopy(stack, 0, arrayOfInt, 0, stack.length);
            stack = arrayOfInt;
          }
          stack[(parenSP++)] = m;
          stack[(parenSP++)] = scriptCode;
        }
        else if (parenSP > 0)
        {
          int n = m & 0xFFFFFFFE;
          while ((parenSP -= 2 >= 0) && (stack[parenSP] != n)) {}
          if (parenSP >= 0) {
            k = stack[(parenSP + 1)];
          } else {
            parenSP = 0;
          }
          if (parenSP < i) {
            i = parenSP;
          }
        }
      }
      if (sameScript(scriptCode, k))
      {
        if ((scriptCode <= 1) && (k > 1))
        {
          scriptCode = k;
          while (i < parenSP)
          {
            stack[(i + 1)] = scriptCode;
            i += 2;
          }
        }
        if ((m > 0) && ((m & 0x1) != 0) && (parenSP > 0)) {
          parenSP -= 2;
        }
      }
      else
      {
        pushback(j);
        break;
      }
    }
    return true;
  }
  
  private final int nextCodePoint()
  {
    if (scriptLimit >= textLimit) {
      return -1;
    }
    int i = text[(scriptLimit++)];
    if ((i >= 55296) && (i < 56320) && (scriptLimit < textLimit))
    {
      int j = text[scriptLimit];
      if ((j >= 56320) && (j < 57344))
      {
        scriptLimit += 1;
        i = (i << 10) + j + -56613888;
      }
    }
    return i;
  }
  
  private final void pushback(int paramInt)
  {
    if (paramInt >= 0) {
      if (paramInt >= 65536) {
        scriptLimit -= 2;
      } else {
        scriptLimit -= 1;
      }
    }
  }
  
  private static boolean sameScript(int paramInt1, int paramInt2)
  {
    return (paramInt1 == paramInt2) || (paramInt1 <= 1) || (paramInt2 <= 1);
  }
  
  private static final byte highBit(int paramInt)
  {
    if (paramInt <= 0) {
      return -32;
    }
    byte b = 0;
    if (paramInt >= 65536)
    {
      paramInt >>= 16;
      b = (byte)(b + 16);
    }
    if (paramInt >= 256)
    {
      paramInt >>= 8;
      b = (byte)(b + 8);
    }
    if (paramInt >= 16)
    {
      paramInt >>= 4;
      b = (byte)(b + 4);
    }
    if (paramInt >= 4)
    {
      paramInt >>= 2;
      b = (byte)(b + 2);
    }
    if (paramInt >= 2)
    {
      paramInt >>= 1;
      b = (byte)(b + 1);
    }
    return b;
  }
  
  private static int getPairIndex(int paramInt)
  {
    int i = pairedCharPower;
    int j = 0;
    if (paramInt >= pairedChars[pairedCharExtra]) {
      j = pairedCharExtra;
    }
    while (i > 1)
    {
      i >>= 1;
      if (paramInt >= pairedChars[(j + i)]) {
        j += i;
      }
    }
    if (pairedChars[j] != paramInt) {
      j = -1;
    }
    return j;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\ScriptRun.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
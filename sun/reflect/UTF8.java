package sun.reflect;

class UTF8
{
  UTF8() {}
  
  static byte[] encode(String paramString)
  {
    int i = paramString.length();
    byte[] arrayOfByte = new byte[utf8Length(paramString)];
    int j = 0;
    try
    {
      for (int k = 0; k < i; k++)
      {
        int m = paramString.charAt(k) & 0xFFFF;
        if ((m >= 1) && (m <= 127))
        {
          arrayOfByte[(j++)] = ((byte)m);
        }
        else if ((m == 0) || ((m >= 128) && (m <= 2047)))
        {
          arrayOfByte[(j++)] = ((byte)(192 + (m >> 6)));
          arrayOfByte[(j++)] = ((byte)(128 + (m & 0x3F)));
        }
        else
        {
          arrayOfByte[(j++)] = ((byte)(224 + (m >> 12)));
          arrayOfByte[(j++)] = ((byte)(128 + (m >> 6 & 0x3F)));
          arrayOfByte[(j++)] = ((byte)(128 + (m & 0x3F)));
        }
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new InternalError("Bug in sun.reflect bootstrap UTF-8 encoder", localArrayIndexOutOfBoundsException);
    }
    return arrayOfByte;
  }
  
  private static int utf8Length(String paramString)
  {
    int i = paramString.length();
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      int m = paramString.charAt(k) & 0xFFFF;
      if ((m >= 1) && (m <= 127)) {
        j++;
      } else if ((m == 0) || ((m >= 128) && (m <= 2047))) {
        j += 2;
      } else {
        j += 3;
      }
    }
    return j;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\UTF8.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.io;

import java.util.Arrays;

public class StreamTokenizer
{
  private Reader reader = null;
  private InputStream input = null;
  private char[] buf = new char[20];
  private int peekc = Integer.MAX_VALUE;
  private static final int NEED_CHAR = Integer.MAX_VALUE;
  private static final int SKIP_LF = 2147483646;
  private boolean pushedBack;
  private boolean forceLower;
  private int LINENO = 1;
  private boolean eolIsSignificantP = false;
  private boolean slashSlashCommentsP = false;
  private boolean slashStarCommentsP = false;
  private byte[] ctype = new byte['Ā'];
  private static final byte CT_WHITESPACE = 1;
  private static final byte CT_DIGIT = 2;
  private static final byte CT_ALPHA = 4;
  private static final byte CT_QUOTE = 8;
  private static final byte CT_COMMENT = 16;
  public int ttype = -4;
  public static final int TT_EOF = -1;
  public static final int TT_EOL = 10;
  public static final int TT_NUMBER = -2;
  public static final int TT_WORD = -3;
  private static final int TT_NOTHING = -4;
  public String sval;
  public double nval;
  
  private StreamTokenizer()
  {
    wordChars(97, 122);
    wordChars(65, 90);
    wordChars(160, 255);
    whitespaceChars(0, 32);
    commentChar(47);
    quoteChar(34);
    quoteChar(39);
    parseNumbers();
  }
  
  @Deprecated
  public StreamTokenizer(InputStream paramInputStream)
  {
    this();
    if (paramInputStream == null) {
      throw new NullPointerException();
    }
    input = paramInputStream;
  }
  
  public StreamTokenizer(Reader paramReader)
  {
    this();
    if (paramReader == null) {
      throw new NullPointerException();
    }
  }
  
  public void resetSyntax()
  {
    int i = ctype.length;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      ctype[i] = 0;
    }
  }
  
  public void wordChars(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      paramInt1 = 0;
    }
    if (paramInt2 >= ctype.length) {
      paramInt2 = ctype.length - 1;
    }
    while (paramInt1 <= paramInt2)
    {
      int tmp36_33 = (paramInt1++);
      byte[] tmp36_29 = ctype;
      tmp36_29[tmp36_33] = ((byte)(tmp36_29[tmp36_33] | 0x4));
    }
  }
  
  public void whitespaceChars(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      paramInt1 = 0;
    }
    if (paramInt2 >= ctype.length) {
      paramInt2 = ctype.length - 1;
    }
    while (paramInt1 <= paramInt2) {
      ctype[(paramInt1++)] = 1;
    }
  }
  
  public void ordinaryChars(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      paramInt1 = 0;
    }
    if (paramInt2 >= ctype.length) {
      paramInt2 = ctype.length - 1;
    }
    while (paramInt1 <= paramInt2) {
      ctype[(paramInt1++)] = 0;
    }
  }
  
  public void ordinaryChar(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < ctype.length)) {
      ctype[paramInt] = 0;
    }
  }
  
  public void commentChar(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < ctype.length)) {
      ctype[paramInt] = 16;
    }
  }
  
  public void quoteChar(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < ctype.length)) {
      ctype[paramInt] = 8;
    }
  }
  
  public void parseNumbers()
  {
    for (int i = 48; i <= 57; i++)
    {
      int tmp14_13 = i;
      byte[] tmp14_10 = ctype;
      tmp14_10[tmp14_13] = ((byte)(tmp14_10[tmp14_13] | 0x2));
    }
    byte[] tmp32_27 = ctype;
    tmp32_27[46] = ((byte)(tmp32_27[46] | 0x2));
    byte[] tmp44_39 = ctype;
    tmp44_39[45] = ((byte)(tmp44_39[45] | 0x2));
  }
  
  public void eolIsSignificant(boolean paramBoolean)
  {
    eolIsSignificantP = paramBoolean;
  }
  
  public void slashStarComments(boolean paramBoolean)
  {
    slashStarCommentsP = paramBoolean;
  }
  
  public void slashSlashComments(boolean paramBoolean)
  {
    slashSlashCommentsP = paramBoolean;
  }
  
  public void lowerCaseMode(boolean paramBoolean)
  {
    forceLower = paramBoolean;
  }
  
  private int read()
    throws IOException
  {
    if (reader != null) {
      return reader.read();
    }
    if (input != null) {
      return input.read();
    }
    throw new IllegalStateException();
  }
  
  public int nextToken()
    throws IOException
  {
    if (pushedBack)
    {
      pushedBack = false;
      return ttype;
    }
    byte[] arrayOfByte = ctype;
    sval = null;
    int i = peekc;
    if (i < 0) {
      i = Integer.MAX_VALUE;
    }
    if (i == 2147483646)
    {
      i = read();
      if (i < 0) {
        return ttype = -1;
      }
      if (i == 10) {
        i = Integer.MAX_VALUE;
      }
    }
    if (i == Integer.MAX_VALUE)
    {
      i = read();
      if (i < 0) {
        return ttype = -1;
      }
    }
    ttype = i;
    peekc = Integer.MAX_VALUE;
    for (int j = i < 256 ? arrayOfByte[i] : 4; (j & 0x1) != 0; j = i < 256 ? arrayOfByte[i] : 4)
    {
      if (i == 13)
      {
        LINENO += 1;
        if (eolIsSignificantP)
        {
          peekc = 2147483646;
          return ttype = 10;
        }
        i = read();
        if (i == 10) {
          i = read();
        }
      }
      else
      {
        if (i == 10)
        {
          LINENO += 1;
          if (eolIsSignificantP) {
            return ttype = 10;
          }
        }
        i = read();
      }
      if (i < 0) {
        return ttype = -1;
      }
    }
    int k;
    int i1;
    if ((j & 0x2) != 0)
    {
      k = 0;
      if (i == 45)
      {
        i = read();
        if ((i != 46) && ((i < 48) || (i > 57)))
        {
          peekc = i;
          return ttype = 45;
        }
        k = 1;
      }
      double d1 = 0.0D;
      i1 = 0;
      int i2 = 0;
      for (;;)
      {
        if ((i == 46) && (i2 == 0))
        {
          i2 = 1;
        }
        else
        {
          if ((48 > i) || (i > 57)) {
            break;
          }
          d1 = d1 * 10.0D + (i - 48);
          i1 += i2;
        }
        i = read();
      }
      peekc = i;
      if (i1 != 0)
      {
        double d2 = 10.0D;
        i1--;
        while (i1 > 0)
        {
          d2 *= 10.0D;
          i1--;
        }
        d1 /= d2;
      }
      nval = (k != 0 ? -d1 : d1);
      return ttype = -2;
    }
    if ((j & 0x4) != 0)
    {
      k = 0;
      do
      {
        if (k >= buf.length) {
          buf = Arrays.copyOf(buf, buf.length * 2);
        }
        buf[(k++)] = ((char)i);
        i = read();
        j = i < 256 ? arrayOfByte[i] : i < 0 ? 1 : 4;
      } while ((j & 0x6) != 0);
      peekc = i;
      sval = String.copyValueOf(buf, 0, k);
      if (forceLower) {
        sval = sval.toLowerCase();
      }
      return ttype = -3;
    }
    if ((j & 0x8) != 0)
    {
      ttype = i;
      k = 0;
      int m = read();
      while ((m >= 0) && (m != ttype) && (m != 10) && (m != 13))
      {
        if (m == 92)
        {
          i = read();
          int n = i;
          if ((i >= 48) && (i <= 55))
          {
            i -= 48;
            i1 = read();
            if ((48 <= i1) && (i1 <= 55))
            {
              i = (i << 3) + (i1 - 48);
              i1 = read();
              if ((48 <= i1) && (i1 <= 55) && (n <= 51))
              {
                i = (i << 3) + (i1 - 48);
                m = read();
              }
              else
              {
                m = i1;
              }
            }
            else
            {
              m = i1;
            }
          }
          else
          {
            switch (i)
            {
            case 97: 
              i = 7;
              break;
            case 98: 
              i = 8;
              break;
            case 102: 
              i = 12;
              break;
            case 110: 
              i = 10;
              break;
            case 114: 
              i = 13;
              break;
            case 116: 
              i = 9;
              break;
            case 118: 
              i = 11;
            }
            m = read();
          }
        }
        else
        {
          i = m;
          m = read();
        }
        if (k >= buf.length) {
          buf = Arrays.copyOf(buf, buf.length * 2);
        }
        buf[(k++)] = ((char)i);
      }
      peekc = (m == ttype ? Integer.MAX_VALUE : m);
      sval = String.copyValueOf(buf, 0, k);
      return ttype;
    }
    if ((i == 47) && ((slashSlashCommentsP) || (slashStarCommentsP)))
    {
      i = read();
      if ((i == 42) && (slashStarCommentsP))
      {
        for (k = 0; ((i = read()) != 47) || (k != 42); k = i)
        {
          if (i == 13)
          {
            LINENO += 1;
            i = read();
            if (i == 10) {
              i = read();
            }
          }
          else if (i == 10)
          {
            LINENO += 1;
            i = read();
          }
          if (i < 0) {
            return ttype = -1;
          }
        }
        return nextToken();
      }
      if ((i == 47) && (slashSlashCommentsP))
      {
        while (((i = read()) != 10) && (i != 13) && (i >= 0)) {}
        peekc = i;
        return nextToken();
      }
      if ((arrayOfByte[47] & 0x10) != 0)
      {
        while (((i = read()) != 10) && (i != 13) && (i >= 0)) {}
        peekc = i;
        return nextToken();
      }
      peekc = i;
      return ttype = 47;
    }
    if ((j & 0x10) != 0)
    {
      while (((i = read()) != 10) && (i != 13) && (i >= 0)) {}
      peekc = i;
      return nextToken();
    }
    return ttype = i;
  }
  
  public void pushBack()
  {
    if (ttype != -4) {
      pushedBack = true;
    }
  }
  
  public int lineno()
  {
    return LINENO;
  }
  
  public String toString()
  {
    String str;
    switch (ttype)
    {
    case -1: 
      str = "EOF";
      break;
    case 10: 
      str = "EOL";
      break;
    case -3: 
      str = sval;
      break;
    case -2: 
      str = "n=" + nval;
      break;
    case -4: 
      str = "NOTHING";
      break;
    case 0: 
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    default: 
      if ((ttype < 256) && ((ctype[ttype] & 0x8) != 0))
      {
        str = sval;
      }
      else
      {
        char[] arrayOfChar = new char[3];
        arrayOfChar[0] = (arrayOfChar[2] = 39);
        arrayOfChar[1] = ((char)ttype);
        str = new String(arrayOfChar);
      }
      break;
    }
    return "Token[" + str + "], line " + LINENO;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\StreamTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
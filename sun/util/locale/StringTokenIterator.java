package sun.util.locale;

public class StringTokenIterator
{
  private String text;
  private String dlms;
  private char delimiterChar;
  private String token;
  private int start;
  private int end;
  private boolean done;
  
  public StringTokenIterator(String paramString1, String paramString2)
  {
    text = paramString1;
    if (paramString2.length() == 1) {
      delimiterChar = paramString2.charAt(0);
    } else {
      dlms = paramString2;
    }
    setStart(0);
  }
  
  public String first()
  {
    setStart(0);
    return token;
  }
  
  public String current()
  {
    return token;
  }
  
  public int currentStart()
  {
    return start;
  }
  
  public int currentEnd()
  {
    return end;
  }
  
  public boolean isDone()
  {
    return done;
  }
  
  public String next()
  {
    if (hasNext())
    {
      start = (end + 1);
      end = nextDelimiter(start);
      token = text.substring(start, end);
    }
    else
    {
      start = end;
      token = null;
      done = true;
    }
    return token;
  }
  
  public boolean hasNext()
  {
    return end < text.length();
  }
  
  public StringTokenIterator setStart(int paramInt)
  {
    if (paramInt > text.length()) {
      throw new IndexOutOfBoundsException();
    }
    start = paramInt;
    end = nextDelimiter(start);
    token = text.substring(start, end);
    done = false;
    return this;
  }
  
  public StringTokenIterator setText(String paramString)
  {
    text = paramString;
    setStart(0);
    return this;
  }
  
  private int nextDelimiter(int paramInt)
  {
    int i = text.length();
    int j;
    if (dlms == null)
    {
      for (j = paramInt; j < i; j++) {
        if (text.charAt(j) == delimiterChar) {
          return j;
        }
      }
    }
    else
    {
      j = dlms.length();
      for (int k = paramInt; k < i; k++)
      {
        int m = text.charAt(k);
        for (int n = 0; n < j; n++) {
          if (m == dlms.charAt(n)) {
            return k;
          }
        }
      }
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\StringTokenIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.text.normalizer;

import java.text.ParsePosition;

public class RuleCharacterIterator
{
  private String text;
  private ParsePosition pos;
  private SymbolTable sym;
  private char[] buf;
  private int bufPos;
  private boolean isEscaped;
  public static final int DONE = -1;
  public static final int PARSE_VARIABLES = 1;
  public static final int PARSE_ESCAPES = 2;
  public static final int SKIP_WHITESPACE = 4;
  
  public RuleCharacterIterator(String paramString, SymbolTable paramSymbolTable, ParsePosition paramParsePosition)
  {
    if ((paramString == null) || (paramParsePosition.getIndex() > paramString.length())) {
      throw new IllegalArgumentException();
    }
    text = paramString;
    sym = paramSymbolTable;
    pos = paramParsePosition;
    buf = null;
  }
  
  public boolean atEnd()
  {
    return (buf == null) && (pos.getIndex() == text.length());
  }
  
  public int next(int paramInt)
  {
    int i = -1;
    isEscaped = false;
    Object localObject;
    do
    {
      for (;;)
      {
        i = _current();
        _advance(UTF16.getCharCount(i));
        if ((i != 36) || (buf != null) || ((paramInt & 0x1) == 0) || (sym == null)) {
          break;
        }
        localObject = sym.parseReference(text, pos, text.length());
        if (localObject == null) {
          return i;
        }
        bufPos = 0;
        buf = sym.lookup((String)localObject);
        if (buf == null) {
          throw new IllegalArgumentException("Undefined variable: " + (String)localObject);
        }
        if (buf.length == 0) {
          buf = null;
        }
      }
    } while (((paramInt & 0x4) != 0) && (UCharacterProperty.isRuleWhiteSpace(i)));
    if ((i == 92) && ((paramInt & 0x2) != 0))
    {
      localObject = new int[] { 0 };
      i = Utility.unescapeAt(lookahead(), (int[])localObject);
      jumpahead(localObject[0]);
      isEscaped = true;
      if (i < 0) {
        throw new IllegalArgumentException("Invalid escape");
      }
    }
    return i;
  }
  
  public boolean isEscaped()
  {
    return isEscaped;
  }
  
  public boolean inVariable()
  {
    return buf != null;
  }
  
  public Object getPos(Object paramObject)
  {
    if (paramObject == null) {
      return new Object[] { buf, { pos.getIndex(), bufPos } };
    }
    Object[] arrayOfObject = (Object[])paramObject;
    arrayOfObject[0] = buf;
    int[] arrayOfInt = (int[])arrayOfObject[1];
    arrayOfInt[0] = pos.getIndex();
    arrayOfInt[1] = bufPos;
    return paramObject;
  }
  
  public void setPos(Object paramObject)
  {
    Object[] arrayOfObject = (Object[])paramObject;
    buf = ((char[])arrayOfObject[0]);
    int[] arrayOfInt = (int[])arrayOfObject[1];
    pos.setIndex(arrayOfInt[0]);
    bufPos = arrayOfInt[1];
  }
  
  public void skipIgnored(int paramInt)
  {
    if ((paramInt & 0x4) != 0) {
      for (;;)
      {
        int i = _current();
        if (!UCharacterProperty.isRuleWhiteSpace(i)) {
          break;
        }
        _advance(UTF16.getCharCount(i));
      }
    }
  }
  
  public String lookahead()
  {
    if (buf != null) {
      return new String(buf, bufPos, buf.length - bufPos);
    }
    return text.substring(pos.getIndex());
  }
  
  public void jumpahead(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    if (buf != null)
    {
      bufPos += paramInt;
      if (bufPos > buf.length) {
        throw new IllegalArgumentException();
      }
      if (bufPos == buf.length) {
        buf = null;
      }
    }
    else
    {
      int i = pos.getIndex() + paramInt;
      pos.setIndex(i);
      if (i > text.length()) {
        throw new IllegalArgumentException();
      }
    }
  }
  
  private int _current()
  {
    if (buf != null) {
      return UTF16.charAt(buf, 0, buf.length, bufPos);
    }
    int i = pos.getIndex();
    return i < text.length() ? UTF16.charAt(text, i) : -1;
  }
  
  private void _advance(int paramInt)
  {
    if (buf != null)
    {
      bufPos += paramInt;
      if (bufPos == buf.length) {
        buf = null;
      }
    }
    else
    {
      pos.setIndex(pos.getIndex() + paramInt);
      if (pos.getIndex() > text.length()) {
        pos.setIndex(text.length());
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\normalizer\RuleCharacterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.text.normalizer;

import java.util.Iterator;
import java.util.TreeSet;

public class UnicodeSetIterator
{
  public static int IS_STRING = -1;
  public int codepoint;
  public int codepointEnd;
  public String string;
  private UnicodeSet set;
  private int endRange = 0;
  private int range = 0;
  protected int endElement;
  protected int nextElement;
  private Iterator<String> stringIterator = null;
  
  public UnicodeSetIterator(UnicodeSet paramUnicodeSet)
  {
    reset(paramUnicodeSet);
  }
  
  public boolean nextRange()
  {
    if (nextElement <= endElement)
    {
      codepointEnd = endElement;
      codepoint = nextElement;
      nextElement = (endElement + 1);
      return true;
    }
    if (range < endRange)
    {
      loadRange(++range);
      codepointEnd = endElement;
      codepoint = nextElement;
      nextElement = (endElement + 1);
      return true;
    }
    if (stringIterator == null) {
      return false;
    }
    codepoint = IS_STRING;
    string = ((String)stringIterator.next());
    if (!stringIterator.hasNext()) {
      stringIterator = null;
    }
    return true;
  }
  
  public void reset(UnicodeSet paramUnicodeSet)
  {
    set = paramUnicodeSet;
    reset();
  }
  
  public void reset()
  {
    endRange = (set.getRangeCount() - 1);
    range = 0;
    endElement = -1;
    nextElement = 0;
    if (endRange >= 0) {
      loadRange(range);
    }
    stringIterator = null;
    if (set.strings != null)
    {
      stringIterator = set.strings.iterator();
      if (!stringIterator.hasNext()) {
        stringIterator = null;
      }
    }
  }
  
  protected void loadRange(int paramInt)
  {
    nextElement = set.getRangeStart(paramInt);
    endElement = set.getRangeEnd(paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\normalizer\UnicodeSetIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
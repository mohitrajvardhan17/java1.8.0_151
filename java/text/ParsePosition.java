package java.text;

public class ParsePosition
{
  int index = 0;
  int errorIndex = -1;
  
  public int getIndex()
  {
    return index;
  }
  
  public void setIndex(int paramInt)
  {
    index = paramInt;
  }
  
  public ParsePosition(int paramInt)
  {
    index = paramInt;
  }
  
  public void setErrorIndex(int paramInt)
  {
    errorIndex = paramInt;
  }
  
  public int getErrorIndex()
  {
    return errorIndex;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (!(paramObject instanceof ParsePosition)) {
      return false;
    }
    ParsePosition localParsePosition = (ParsePosition)paramObject;
    return (index == index) && (errorIndex == errorIndex);
  }
  
  public int hashCode()
  {
    return errorIndex << 16 | index;
  }
  
  public String toString()
  {
    return getClass().getName() + "[index=" + index + ",errorIndex=" + errorIndex + ']';
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\ParsePosition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
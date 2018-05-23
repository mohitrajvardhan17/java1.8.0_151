package java.text;

public class FieldPosition
{
  int field = 0;
  int endIndex = 0;
  int beginIndex = 0;
  private Format.Field attribute;
  
  public FieldPosition(int paramInt)
  {
    field = paramInt;
  }
  
  public FieldPosition(Format.Field paramField)
  {
    this(paramField, -1);
  }
  
  public FieldPosition(Format.Field paramField, int paramInt)
  {
    attribute = paramField;
    field = paramInt;
  }
  
  public Format.Field getFieldAttribute()
  {
    return attribute;
  }
  
  public int getField()
  {
    return field;
  }
  
  public int getBeginIndex()
  {
    return beginIndex;
  }
  
  public int getEndIndex()
  {
    return endIndex;
  }
  
  public void setBeginIndex(int paramInt)
  {
    beginIndex = paramInt;
  }
  
  public void setEndIndex(int paramInt)
  {
    endIndex = paramInt;
  }
  
  Format.FieldDelegate getFieldDelegate()
  {
    return new Delegate(null);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (!(paramObject instanceof FieldPosition)) {
      return false;
    }
    FieldPosition localFieldPosition = (FieldPosition)paramObject;
    if (attribute == null)
    {
      if (attribute != null) {
        return false;
      }
    }
    else if (!attribute.equals(attribute)) {
      return false;
    }
    return (beginIndex == beginIndex) && (endIndex == endIndex) && (field == field);
  }
  
  public int hashCode()
  {
    return field << 24 | beginIndex << 16 | endIndex;
  }
  
  public String toString()
  {
    return getClass().getName() + "[field=" + field + ",attribute=" + attribute + ",beginIndex=" + beginIndex + ",endIndex=" + endIndex + ']';
  }
  
  private boolean matchesField(Format.Field paramField)
  {
    if (attribute != null) {
      return attribute.equals(paramField);
    }
    return false;
  }
  
  private boolean matchesField(Format.Field paramField, int paramInt)
  {
    if (attribute != null) {
      return attribute.equals(paramField);
    }
    return paramInt == field;
  }
  
  private class Delegate
    implements Format.FieldDelegate
  {
    private boolean encounteredField;
    
    private Delegate() {}
    
    public void formatted(Format.Field paramField, Object paramObject, int paramInt1, int paramInt2, StringBuffer paramStringBuffer)
    {
      if ((!encounteredField) && (FieldPosition.this.matchesField(paramField)))
      {
        setBeginIndex(paramInt1);
        setEndIndex(paramInt2);
        encounteredField = (paramInt1 != paramInt2);
      }
    }
    
    public void formatted(int paramInt1, Format.Field paramField, Object paramObject, int paramInt2, int paramInt3, StringBuffer paramStringBuffer)
    {
      if ((!encounteredField) && (FieldPosition.this.matchesField(paramField, paramInt1)))
      {
        setBeginIndex(paramInt2);
        setEndIndex(paramInt3);
        encounteredField = (paramInt2 != paramInt3);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\FieldPosition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
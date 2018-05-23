package java.text;

import java.io.Serializable;

public abstract class Format
  implements Serializable, Cloneable
{
  private static final long serialVersionUID = -299282585814624189L;
  
  protected Format() {}
  
  public final String format(Object paramObject)
  {
    return format(paramObject, new StringBuffer(), new FieldPosition(0)).toString();
  }
  
  public abstract StringBuffer format(Object paramObject, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);
  
  public AttributedCharacterIterator formatToCharacterIterator(Object paramObject)
  {
    return createAttributedCharacterIterator(format(paramObject));
  }
  
  public abstract Object parseObject(String paramString, ParsePosition paramParsePosition);
  
  public Object parseObject(String paramString)
    throws ParseException
  {
    ParsePosition localParsePosition = new ParsePosition(0);
    Object localObject = parseObject(paramString, localParsePosition);
    if (index == 0) {
      throw new ParseException("Format.parseObject(String) failed", errorIndex);
    }
    return localObject;
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  AttributedCharacterIterator createAttributedCharacterIterator(String paramString)
  {
    AttributedString localAttributedString = new AttributedString(paramString);
    return localAttributedString.getIterator();
  }
  
  AttributedCharacterIterator createAttributedCharacterIterator(AttributedCharacterIterator[] paramArrayOfAttributedCharacterIterator)
  {
    AttributedString localAttributedString = new AttributedString(paramArrayOfAttributedCharacterIterator);
    return localAttributedString.getIterator();
  }
  
  AttributedCharacterIterator createAttributedCharacterIterator(String paramString, AttributedCharacterIterator.Attribute paramAttribute, Object paramObject)
  {
    AttributedString localAttributedString = new AttributedString(paramString);
    localAttributedString.addAttribute(paramAttribute, paramObject);
    return localAttributedString.getIterator();
  }
  
  AttributedCharacterIterator createAttributedCharacterIterator(AttributedCharacterIterator paramAttributedCharacterIterator, AttributedCharacterIterator.Attribute paramAttribute, Object paramObject)
  {
    AttributedString localAttributedString = new AttributedString(paramAttributedCharacterIterator);
    localAttributedString.addAttribute(paramAttribute, paramObject);
    return localAttributedString.getIterator();
  }
  
  public static class Field
    extends AttributedCharacterIterator.Attribute
  {
    private static final long serialVersionUID = 276966692217360283L;
    
    protected Field(String paramString)
    {
      super();
    }
  }
  
  static abstract interface FieldDelegate
  {
    public abstract void formatted(Format.Field paramField, Object paramObject, int paramInt1, int paramInt2, StringBuffer paramStringBuffer);
    
    public abstract void formatted(int paramInt1, Format.Field paramField, Object paramObject, int paramInt2, int paramInt3, StringBuffer paramStringBuffer);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\Format.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
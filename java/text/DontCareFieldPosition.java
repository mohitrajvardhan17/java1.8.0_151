package java.text;

class DontCareFieldPosition
  extends FieldPosition
{
  static final FieldPosition INSTANCE = new DontCareFieldPosition();
  private final Format.FieldDelegate noDelegate = new Format.FieldDelegate()
  {
    public void formatted(Format.Field paramAnonymousField, Object paramAnonymousObject, int paramAnonymousInt1, int paramAnonymousInt2, StringBuffer paramAnonymousStringBuffer) {}
    
    public void formatted(int paramAnonymousInt1, Format.Field paramAnonymousField, Object paramAnonymousObject, int paramAnonymousInt2, int paramAnonymousInt3, StringBuffer paramAnonymousStringBuffer) {}
  };
  
  private DontCareFieldPosition()
  {
    super(0);
  }
  
  Format.FieldDelegate getFieldDelegate()
  {
    return noDelegate;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\DontCareFieldPosition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
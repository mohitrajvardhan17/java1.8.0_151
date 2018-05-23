package java.beans;

public class IndexedPropertyChangeEvent
  extends PropertyChangeEvent
{
  private static final long serialVersionUID = -320227448495806870L;
  private int index;
  
  public IndexedPropertyChangeEvent(Object paramObject1, String paramString, Object paramObject2, Object paramObject3, int paramInt)
  {
    super(paramObject1, paramString, paramObject2, paramObject3);
    index = paramInt;
  }
  
  public int getIndex()
  {
    return index;
  }
  
  void appendTo(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append("; index=").append(getIndex());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\IndexedPropertyChangeEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
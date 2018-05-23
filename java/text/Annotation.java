package java.text;

public class Annotation
{
  private Object value;
  
  public Annotation(Object paramObject)
  {
    value = paramObject;
  }
  
  public Object getValue()
  {
    return value;
  }
  
  public String toString()
  {
    return getClass().getName() + "[value=" + value + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\Annotation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
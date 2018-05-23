package java.beans;

public class Expression
  extends Statement
{
  private static Object unbound = new Object();
  private Object value = unbound;
  
  @ConstructorProperties({"target", "methodName", "arguments"})
  public Expression(Object paramObject, String paramString, Object[] paramArrayOfObject)
  {
    super(paramObject, paramString, paramArrayOfObject);
  }
  
  public Expression(Object paramObject1, Object paramObject2, String paramString, Object[] paramArrayOfObject)
  {
    this(paramObject2, paramString, paramArrayOfObject);
    setValue(paramObject1);
  }
  
  public void execute()
    throws Exception
  {
    setValue(invoke());
  }
  
  public Object getValue()
    throws Exception
  {
    if (value == unbound) {
      setValue(invoke());
    }
    return value;
  }
  
  public void setValue(Object paramObject)
  {
    value = paramObject;
  }
  
  String instanceName(Object paramObject)
  {
    return paramObject == unbound ? "<unbound>" : super.instanceName(paramObject);
  }
  
  public String toString()
  {
    return instanceName(value) + "=" + super.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\Expression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.naming;

public class Binding
  extends NameClassPair
{
  private Object boundObj;
  private static final long serialVersionUID = 8839217842691845890L;
  
  public Binding(String paramString, Object paramObject)
  {
    super(paramString, null);
    boundObj = paramObject;
  }
  
  public Binding(String paramString, Object paramObject, boolean paramBoolean)
  {
    super(paramString, null, paramBoolean);
    boundObj = paramObject;
  }
  
  public Binding(String paramString1, String paramString2, Object paramObject)
  {
    super(paramString1, paramString2);
    boundObj = paramObject;
  }
  
  public Binding(String paramString1, String paramString2, Object paramObject, boolean paramBoolean)
  {
    super(paramString1, paramString2, paramBoolean);
    boundObj = paramObject;
  }
  
  public String getClassName()
  {
    String str = super.getClassName();
    if (str != null) {
      return str;
    }
    if (boundObj != null) {
      return boundObj.getClass().getName();
    }
    return null;
  }
  
  public Object getObject()
  {
    return boundObj;
  }
  
  public void setObject(Object paramObject)
  {
    boundObj = paramObject;
  }
  
  public String toString()
  {
    return super.toString() + ":" + getObject();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\Binding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
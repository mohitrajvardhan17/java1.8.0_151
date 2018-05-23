package javax.script;

import java.util.Map;

public abstract interface Bindings
  extends Map<String, Object>
{
  public abstract Object put(String paramString, Object paramObject);
  
  public abstract void putAll(Map<? extends String, ? extends Object> paramMap);
  
  public abstract boolean containsKey(Object paramObject);
  
  public abstract Object get(Object paramObject);
  
  public abstract Object remove(Object paramObject);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\script\Bindings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
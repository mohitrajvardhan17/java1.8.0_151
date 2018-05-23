package javax.script;

public abstract interface Invocable
{
  public abstract Object invokeMethod(Object paramObject, String paramString, Object... paramVarArgs)
    throws ScriptException, NoSuchMethodException;
  
  public abstract Object invokeFunction(String paramString, Object... paramVarArgs)
    throws ScriptException, NoSuchMethodException;
  
  public abstract <T> T getInterface(Class<T> paramClass);
  
  public abstract <T> T getInterface(Object paramObject, Class<T> paramClass);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\script\Invocable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
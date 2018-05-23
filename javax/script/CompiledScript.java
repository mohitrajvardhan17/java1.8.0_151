package javax.script;

public abstract class CompiledScript
{
  public CompiledScript() {}
  
  public abstract Object eval(ScriptContext paramScriptContext)
    throws ScriptException;
  
  public Object eval(Bindings paramBindings)
    throws ScriptException
  {
    Object localObject = getEngine().getContext();
    if (paramBindings != null)
    {
      SimpleScriptContext localSimpleScriptContext = new SimpleScriptContext();
      localSimpleScriptContext.setBindings(paramBindings, 100);
      localSimpleScriptContext.setBindings(((ScriptContext)localObject).getBindings(200), 200);
      localSimpleScriptContext.setWriter(((ScriptContext)localObject).getWriter());
      localSimpleScriptContext.setReader(((ScriptContext)localObject).getReader());
      localSimpleScriptContext.setErrorWriter(((ScriptContext)localObject).getErrorWriter());
      localObject = localSimpleScriptContext;
    }
    return eval((ScriptContext)localObject);
  }
  
  public Object eval()
    throws ScriptException
  {
    return eval(getEngine().getContext());
  }
  
  public abstract ScriptEngine getEngine();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\script\CompiledScript.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
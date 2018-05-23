package javax.script;

import java.io.Reader;

public abstract class AbstractScriptEngine
  implements ScriptEngine
{
  protected ScriptContext context = new SimpleScriptContext();
  
  public AbstractScriptEngine() {}
  
  public AbstractScriptEngine(Bindings paramBindings)
  {
    this();
    if (paramBindings == null) {
      throw new NullPointerException("n is null");
    }
    context.setBindings(paramBindings, 100);
  }
  
  public void setContext(ScriptContext paramScriptContext)
  {
    if (paramScriptContext == null) {
      throw new NullPointerException("null context");
    }
    context = paramScriptContext;
  }
  
  public ScriptContext getContext()
  {
    return context;
  }
  
  public Bindings getBindings(int paramInt)
  {
    if (paramInt == 200) {
      return context.getBindings(200);
    }
    if (paramInt == 100) {
      return context.getBindings(100);
    }
    throw new IllegalArgumentException("Invalid scope value.");
  }
  
  public void setBindings(Bindings paramBindings, int paramInt)
  {
    if (paramInt == 200) {
      context.setBindings(paramBindings, 200);
    } else if (paramInt == 100) {
      context.setBindings(paramBindings, 100);
    } else {
      throw new IllegalArgumentException("Invalid scope value.");
    }
  }
  
  public void put(String paramString, Object paramObject)
  {
    Bindings localBindings = getBindings(100);
    if (localBindings != null) {
      localBindings.put(paramString, paramObject);
    }
  }
  
  public Object get(String paramString)
  {
    Bindings localBindings = getBindings(100);
    if (localBindings != null) {
      return localBindings.get(paramString);
    }
    return null;
  }
  
  public Object eval(Reader paramReader, Bindings paramBindings)
    throws ScriptException
  {
    ScriptContext localScriptContext = getScriptContext(paramBindings);
    return eval(paramReader, localScriptContext);
  }
  
  public Object eval(String paramString, Bindings paramBindings)
    throws ScriptException
  {
    ScriptContext localScriptContext = getScriptContext(paramBindings);
    return eval(paramString, localScriptContext);
  }
  
  public Object eval(Reader paramReader)
    throws ScriptException
  {
    return eval(paramReader, context);
  }
  
  public Object eval(String paramString)
    throws ScriptException
  {
    return eval(paramString, context);
  }
  
  protected ScriptContext getScriptContext(Bindings paramBindings)
  {
    SimpleScriptContext localSimpleScriptContext = new SimpleScriptContext();
    Bindings localBindings = getBindings(200);
    if (localBindings != null) {
      localSimpleScriptContext.setBindings(localBindings, 200);
    }
    if (paramBindings != null) {
      localSimpleScriptContext.setBindings(paramBindings, 100);
    } else {
      throw new NullPointerException("Engine scope Bindings may not be null.");
    }
    localSimpleScriptContext.setReader(context.getReader());
    localSimpleScriptContext.setWriter(context.getWriter());
    localSimpleScriptContext.setErrorWriter(context.getErrorWriter());
    return localSimpleScriptContext;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\script\AbstractScriptEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
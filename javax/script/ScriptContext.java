package javax.script;

import java.io.Reader;
import java.io.Writer;
import java.util.List;

public abstract interface ScriptContext
{
  public static final int ENGINE_SCOPE = 100;
  public static final int GLOBAL_SCOPE = 200;
  
  public abstract void setBindings(Bindings paramBindings, int paramInt);
  
  public abstract Bindings getBindings(int paramInt);
  
  public abstract void setAttribute(String paramString, Object paramObject, int paramInt);
  
  public abstract Object getAttribute(String paramString, int paramInt);
  
  public abstract Object removeAttribute(String paramString, int paramInt);
  
  public abstract Object getAttribute(String paramString);
  
  public abstract int getAttributesScope(String paramString);
  
  public abstract Writer getWriter();
  
  public abstract Writer getErrorWriter();
  
  public abstract void setWriter(Writer paramWriter);
  
  public abstract void setErrorWriter(Writer paramWriter);
  
  public abstract Reader getReader();
  
  public abstract void setReader(Reader paramReader);
  
  public abstract List<Integer> getScopes();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\script\ScriptContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
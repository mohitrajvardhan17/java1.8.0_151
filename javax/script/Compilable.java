package javax.script;

import java.io.Reader;

public abstract interface Compilable
{
  public abstract CompiledScript compile(String paramString)
    throws ScriptException;
  
  public abstract CompiledScript compile(Reader paramReader)
    throws ScriptException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\script\Compilable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
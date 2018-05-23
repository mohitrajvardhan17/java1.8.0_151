package javax.script;

import java.util.List;

public abstract interface ScriptEngineFactory
{
  public abstract String getEngineName();
  
  public abstract String getEngineVersion();
  
  public abstract List<String> getExtensions();
  
  public abstract List<String> getMimeTypes();
  
  public abstract List<String> getNames();
  
  public abstract String getLanguageName();
  
  public abstract String getLanguageVersion();
  
  public abstract Object getParameter(String paramString);
  
  public abstract String getMethodCallSyntax(String paramString1, String paramString2, String... paramVarArgs);
  
  public abstract String getOutputStatement(String paramString);
  
  public abstract String getProgram(String... paramVarArgs);
  
  public abstract ScriptEngine getScriptEngine();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\script\ScriptEngineFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
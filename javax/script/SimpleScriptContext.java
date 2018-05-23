package javax.script;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SimpleScriptContext
  implements ScriptContext
{
  protected Writer writer = new PrintWriter(System.out, true);
  protected Writer errorWriter = new PrintWriter(System.err, true);
  protected Reader reader = new InputStreamReader(System.in);
  protected Bindings engineScope = new SimpleBindings();
  protected Bindings globalScope = null;
  private static List<Integer> scopes = Collections.unmodifiableList(scopes);
  
  public SimpleScriptContext() {}
  
  public void setBindings(Bindings paramBindings, int paramInt)
  {
    switch (paramInt)
    {
    case 100: 
      if (paramBindings == null) {
        throw new NullPointerException("Engine scope cannot be null.");
      }
      engineScope = paramBindings;
      break;
    case 200: 
      globalScope = paramBindings;
      break;
    default: 
      throw new IllegalArgumentException("Invalid scope value.");
    }
  }
  
  public Object getAttribute(String paramString)
  {
    checkName(paramString);
    if (engineScope.containsKey(paramString)) {
      return getAttribute(paramString, 100);
    }
    if ((globalScope != null) && (globalScope.containsKey(paramString))) {
      return getAttribute(paramString, 200);
    }
    return null;
  }
  
  public Object getAttribute(String paramString, int paramInt)
  {
    checkName(paramString);
    switch (paramInt)
    {
    case 100: 
      return engineScope.get(paramString);
    case 200: 
      if (globalScope != null) {
        return globalScope.get(paramString);
      }
      return null;
    }
    throw new IllegalArgumentException("Illegal scope value.");
  }
  
  public Object removeAttribute(String paramString, int paramInt)
  {
    checkName(paramString);
    switch (paramInt)
    {
    case 100: 
      if (getBindings(100) != null) {
        return getBindings(100).remove(paramString);
      }
      return null;
    case 200: 
      if (getBindings(200) != null) {
        return getBindings(200).remove(paramString);
      }
      return null;
    }
    throw new IllegalArgumentException("Illegal scope value.");
  }
  
  public void setAttribute(String paramString, Object paramObject, int paramInt)
  {
    checkName(paramString);
    switch (paramInt)
    {
    case 100: 
      engineScope.put(paramString, paramObject);
      return;
    case 200: 
      if (globalScope != null) {
        globalScope.put(paramString, paramObject);
      }
      return;
    }
    throw new IllegalArgumentException("Illegal scope value.");
  }
  
  public Writer getWriter()
  {
    return writer;
  }
  
  public Reader getReader()
  {
    return reader;
  }
  
  public void setReader(Reader paramReader)
  {
    reader = paramReader;
  }
  
  public void setWriter(Writer paramWriter)
  {
    writer = paramWriter;
  }
  
  public Writer getErrorWriter()
  {
    return errorWriter;
  }
  
  public void setErrorWriter(Writer paramWriter)
  {
    errorWriter = paramWriter;
  }
  
  public int getAttributesScope(String paramString)
  {
    checkName(paramString);
    if (engineScope.containsKey(paramString)) {
      return 100;
    }
    if ((globalScope != null) && (globalScope.containsKey(paramString))) {
      return 200;
    }
    return -1;
  }
  
  public Bindings getBindings(int paramInt)
  {
    if (paramInt == 100) {
      return engineScope;
    }
    if (paramInt == 200) {
      return globalScope;
    }
    throw new IllegalArgumentException("Illegal scope value.");
  }
  
  public List<Integer> getScopes()
  {
    return scopes;
  }
  
  private void checkName(String paramString)
  {
    Objects.requireNonNull(paramString);
    if (paramString.isEmpty()) {
      throw new IllegalArgumentException("name cannot be empty");
    }
  }
  
  static
  {
    scopes.add(Integer.valueOf(100));
    scopes.add(Integer.valueOf(200));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\script\SimpleScriptContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
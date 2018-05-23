package javax.tools;

import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.Callable;

public abstract interface DocumentationTool
  extends Tool, OptionChecker
{
  public abstract DocumentationTask getTask(Writer paramWriter, JavaFileManager paramJavaFileManager, DiagnosticListener<? super JavaFileObject> paramDiagnosticListener, Class<?> paramClass, Iterable<String> paramIterable, Iterable<? extends JavaFileObject> paramIterable1);
  
  public abstract StandardJavaFileManager getStandardFileManager(DiagnosticListener<? super JavaFileObject> paramDiagnosticListener, Locale paramLocale, Charset paramCharset);
  
  public static abstract interface DocumentationTask
    extends Callable<Boolean>
  {
    public abstract void setLocale(Locale paramLocale);
    
    public abstract Boolean call();
  }
  
  public static enum Location
    implements JavaFileManager.Location
  {
    DOCUMENTATION_OUTPUT,  DOCLET_PATH,  TAGLET_PATH;
    
    private Location() {}
    
    public String getName()
    {
      return name();
    }
    
    public boolean isOutputLocation()
    {
      switch (DocumentationTool.1.$SwitchMap$javax$tools$DocumentationTool$Location[ordinal()])
      {
      case 1: 
        return true;
      }
      return false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\tools\DocumentationTool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
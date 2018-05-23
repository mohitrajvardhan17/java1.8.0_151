package javax.tools;

import java.util.Locale;

public abstract interface Diagnostic<S>
{
  public static final long NOPOS = -1L;
  
  public abstract Kind getKind();
  
  public abstract S getSource();
  
  public abstract long getPosition();
  
  public abstract long getStartPosition();
  
  public abstract long getEndPosition();
  
  public abstract long getLineNumber();
  
  public abstract long getColumnNumber();
  
  public abstract String getCode();
  
  public abstract String getMessage(Locale paramLocale);
  
  public static enum Kind
  {
    ERROR,  WARNING,  MANDATORY_WARNING,  NOTE,  OTHER;
    
    private Kind() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\tools\Diagnostic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
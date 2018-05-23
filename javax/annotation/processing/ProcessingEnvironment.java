package javax.annotation.processing;

import java.util.Locale;
import java.util.Map;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public abstract interface ProcessingEnvironment
{
  public abstract Map<String, String> getOptions();
  
  public abstract Messager getMessager();
  
  public abstract Filer getFiler();
  
  public abstract Elements getElementUtils();
  
  public abstract Types getTypeUtils();
  
  public abstract SourceVersion getSourceVersion();
  
  public abstract Locale getLocale();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\annotation\processing\ProcessingEnvironment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
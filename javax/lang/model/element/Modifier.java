package javax.lang.model.element;

import java.util.Locale;

public enum Modifier
{
  PUBLIC,  PROTECTED,  PRIVATE,  ABSTRACT,  DEFAULT,  STATIC,  FINAL,  TRANSIENT,  VOLATILE,  SYNCHRONIZED,  NATIVE,  STRICTFP;
  
  private Modifier() {}
  
  public String toString()
  {
    return name().toLowerCase(Locale.US);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\element\Modifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
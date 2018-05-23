package javax.tools;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;

public abstract interface JavaFileObject
  extends FileObject
{
  public abstract Kind getKind();
  
  public abstract boolean isNameCompatible(String paramString, Kind paramKind);
  
  public abstract NestingKind getNestingKind();
  
  public abstract Modifier getAccessLevel();
  
  public static enum Kind
  {
    SOURCE(".java"),  CLASS(".class"),  HTML(".html"),  OTHER("");
    
    public final String extension;
    
    private Kind(String paramString)
    {
      paramString.getClass();
      extension = paramString;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\tools\JavaFileObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
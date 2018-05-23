package javax.tools;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import javax.lang.model.SourceVersion;

public abstract interface Tool
{
  public abstract int run(InputStream paramInputStream, OutputStream paramOutputStream1, OutputStream paramOutputStream2, String... paramVarArgs);
  
  public abstract Set<SourceVersion> getSourceVersions();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\tools\Tool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
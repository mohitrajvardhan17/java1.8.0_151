package javax.annotation.processing;

import java.io.IOException;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject;

public abstract interface Filer
{
  public abstract JavaFileObject createSourceFile(CharSequence paramCharSequence, Element... paramVarArgs)
    throws IOException;
  
  public abstract JavaFileObject createClassFile(CharSequence paramCharSequence, Element... paramVarArgs)
    throws IOException;
  
  public abstract FileObject createResource(JavaFileManager.Location paramLocation, CharSequence paramCharSequence1, CharSequence paramCharSequence2, Element... paramVarArgs)
    throws IOException;
  
  public abstract FileObject getResource(JavaFileManager.Location paramLocation, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\annotation\processing\Filer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
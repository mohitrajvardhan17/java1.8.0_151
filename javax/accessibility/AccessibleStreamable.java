package javax.accessibility;

import java.awt.datatransfer.DataFlavor;
import java.io.InputStream;

public abstract interface AccessibleStreamable
{
  public abstract DataFlavor[] getMimeTypes();
  
  public abstract InputStream getStream(DataFlavor paramDataFlavor);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\accessibility\AccessibleStreamable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
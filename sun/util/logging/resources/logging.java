package sun.util.logging.resources;

import java.util.ListResourceBundle;

public final class logging
  extends ListResourceBundle
{
  public logging() {}
  
  protected final Object[][] getContents()
  {
    return new Object[][] { { "ALL", "All" }, { "CONFIG", "Config" }, { "FINE", "Fine" }, { "FINER", "Finer" }, { "FINEST", "Finest" }, { "INFO", "Info" }, { "OFF", "Off" }, { "SEVERE", "Severe" }, { "WARNING", "Warning" } };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\logging\resources\logging.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
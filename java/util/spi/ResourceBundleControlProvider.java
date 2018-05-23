package java.util.spi;

import java.util.ResourceBundle.Control;

public abstract interface ResourceBundleControlProvider
{
  public abstract ResourceBundle.Control getControl(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\spi\ResourceBundleControlProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
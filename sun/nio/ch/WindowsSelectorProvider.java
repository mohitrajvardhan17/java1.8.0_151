package sun.nio.ch;

import java.io.IOException;
import java.nio.channels.spi.AbstractSelector;

public class WindowsSelectorProvider
  extends SelectorProviderImpl
{
  public WindowsSelectorProvider() {}
  
  public AbstractSelector openSelector()
    throws IOException
  {
    return new WindowsSelectorImpl(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\WindowsSelectorProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
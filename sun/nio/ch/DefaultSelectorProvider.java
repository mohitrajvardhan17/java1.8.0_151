package sun.nio.ch;

import java.nio.channels.spi.SelectorProvider;

public class DefaultSelectorProvider
{
  private DefaultSelectorProvider() {}
  
  public static SelectorProvider create()
  {
    return new WindowsSelectorProvider();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\DefaultSelectorProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
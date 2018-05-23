package sun.net;

import java.net.URL;

class DefaultProgressMeteringPolicy
  implements ProgressMeteringPolicy
{
  DefaultProgressMeteringPolicy() {}
  
  public boolean shouldMeterInput(URL paramURL, String paramString)
  {
    return false;
  }
  
  public int getProgressUpdateThreshold()
  {
    return 8192;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\DefaultProgressMeteringPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
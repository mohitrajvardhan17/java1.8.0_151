package sun.net;

import java.net.URL;

public abstract interface ProgressMeteringPolicy
{
  public abstract boolean shouldMeterInput(URL paramURL, String paramString);
  
  public abstract int getProgressUpdateThreshold();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\ProgressMeteringPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
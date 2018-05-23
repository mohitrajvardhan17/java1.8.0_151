package com.sun.xml.internal.ws.api.ha;

public class HaInfo
{
  private final String replicaInstance;
  private final String key;
  private final boolean failOver;
  
  public HaInfo(String paramString1, String paramString2, boolean paramBoolean)
  {
    key = paramString1;
    replicaInstance = paramString2;
    failOver = paramBoolean;
  }
  
  public String getReplicaInstance()
  {
    return replicaInstance;
  }
  
  public String getKey()
  {
    return key;
  }
  
  public boolean isFailOver()
  {
    return failOver;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\ha\HaInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
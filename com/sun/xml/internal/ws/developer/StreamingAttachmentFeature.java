package com.sun.xml.internal.ws.developer;

import com.sun.istack.internal.Nullable;
import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEConfig;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public final class StreamingAttachmentFeature
  extends WebServiceFeature
{
  public static final String ID = "http://jax-ws.dev.java.net/features/mime";
  private MIMEConfig config;
  private String dir;
  private boolean parseEagerly;
  private long memoryThreshold;
  
  public StreamingAttachmentFeature() {}
  
  @FeatureConstructor({"dir", "parseEagerly", "memoryThreshold"})
  public StreamingAttachmentFeature(@Nullable String paramString, boolean paramBoolean, long paramLong)
  {
    enabled = true;
    dir = paramString;
    parseEagerly = paramBoolean;
    memoryThreshold = paramLong;
  }
  
  @ManagedAttribute
  public String getID()
  {
    return "http://jax-ws.dev.java.net/features/mime";
  }
  
  @ManagedAttribute
  public MIMEConfig getConfig()
  {
    if (config == null)
    {
      config = new MIMEConfig();
      config.setDir(dir);
      config.setParseEagerly(parseEagerly);
      config.setMemoryThreshold(memoryThreshold);
      config.validate();
    }
    return config;
  }
  
  public void setDir(String paramString)
  {
    dir = paramString;
  }
  
  public void setParseEagerly(boolean paramBoolean)
  {
    parseEagerly = paramBoolean;
  }
  
  public void setMemoryThreshold(long paramLong)
  {
    memoryThreshold = paramLong;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\developer\StreamingAttachmentFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
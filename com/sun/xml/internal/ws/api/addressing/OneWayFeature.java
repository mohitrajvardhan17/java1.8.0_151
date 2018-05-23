package com.sun.xml.internal.ws.api.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import java.net.URL;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public class OneWayFeature
  extends WebServiceFeature
{
  public static final String ID = "http://java.sun.com/xml/ns/jaxws/addressing/oneway";
  private String messageId;
  private WSEndpointReference replyTo;
  private WSEndpointReference sslReplyTo;
  private WSEndpointReference from;
  private WSEndpointReference faultTo;
  private WSEndpointReference sslFaultTo;
  private String relatesToID;
  private boolean useAsyncWithSyncInvoke = false;
  
  public OneWayFeature()
  {
    enabled = true;
  }
  
  public OneWayFeature(boolean paramBoolean)
  {
    enabled = paramBoolean;
  }
  
  public OneWayFeature(boolean paramBoolean, WSEndpointReference paramWSEndpointReference)
  {
    enabled = paramBoolean;
    replyTo = paramWSEndpointReference;
  }
  
  @FeatureConstructor({"enabled", "replyTo", "from", "relatesTo"})
  public OneWayFeature(boolean paramBoolean, WSEndpointReference paramWSEndpointReference1, WSEndpointReference paramWSEndpointReference2, String paramString)
  {
    enabled = paramBoolean;
    replyTo = paramWSEndpointReference1;
    from = paramWSEndpointReference2;
    relatesToID = paramString;
  }
  
  public OneWayFeature(AddressingPropertySet paramAddressingPropertySet, AddressingVersion paramAddressingVersion)
  {
    enabled = true;
    messageId = paramAddressingPropertySet.getMessageId();
    relatesToID = paramAddressingPropertySet.getRelatesTo();
    replyTo = makeEPR(paramAddressingPropertySet.getReplyTo(), paramAddressingVersion);
    faultTo = makeEPR(paramAddressingPropertySet.getFaultTo(), paramAddressingVersion);
  }
  
  private WSEndpointReference makeEPR(String paramString, AddressingVersion paramAddressingVersion)
  {
    if (paramString == null) {
      return null;
    }
    return new WSEndpointReference(paramString, paramAddressingVersion);
  }
  
  public String getMessageId()
  {
    return messageId;
  }
  
  @ManagedAttribute
  public String getID()
  {
    return "http://java.sun.com/xml/ns/jaxws/addressing/oneway";
  }
  
  public boolean hasSslEprs()
  {
    return (sslReplyTo != null) || (sslFaultTo != null);
  }
  
  @ManagedAttribute
  public WSEndpointReference getReplyTo()
  {
    return replyTo;
  }
  
  public WSEndpointReference getReplyTo(boolean paramBoolean)
  {
    return (paramBoolean) && (sslReplyTo != null) ? sslReplyTo : replyTo;
  }
  
  public void setReplyTo(WSEndpointReference paramWSEndpointReference)
  {
    replyTo = paramWSEndpointReference;
  }
  
  public WSEndpointReference getSslReplyTo()
  {
    return sslReplyTo;
  }
  
  public void setSslReplyTo(WSEndpointReference paramWSEndpointReference)
  {
    sslReplyTo = paramWSEndpointReference;
  }
  
  @ManagedAttribute
  public WSEndpointReference getFrom()
  {
    return from;
  }
  
  public void setFrom(WSEndpointReference paramWSEndpointReference)
  {
    from = paramWSEndpointReference;
  }
  
  @ManagedAttribute
  public String getRelatesToID()
  {
    return relatesToID;
  }
  
  public void setRelatesToID(String paramString)
  {
    relatesToID = paramString;
  }
  
  public WSEndpointReference getFaultTo()
  {
    return faultTo;
  }
  
  public WSEndpointReference getFaultTo(boolean paramBoolean)
  {
    return (paramBoolean) && (sslFaultTo != null) ? sslFaultTo : faultTo;
  }
  
  public void setFaultTo(WSEndpointReference paramWSEndpointReference)
  {
    faultTo = paramWSEndpointReference;
  }
  
  public WSEndpointReference getSslFaultTo()
  {
    return sslFaultTo;
  }
  
  public void setSslFaultTo(WSEndpointReference paramWSEndpointReference)
  {
    sslFaultTo = paramWSEndpointReference;
  }
  
  public boolean isUseAsyncWithSyncInvoke()
  {
    return useAsyncWithSyncInvoke;
  }
  
  public void setUseAsyncWithSyncInvoke(boolean paramBoolean)
  {
    useAsyncWithSyncInvoke = paramBoolean;
  }
  
  public static WSEndpointReference enableSslForEpr(@NotNull WSEndpointReference paramWSEndpointReference, @Nullable String paramString, int paramInt)
  {
    if (!paramWSEndpointReference.isAnonymous())
    {
      String str1 = paramWSEndpointReference.getAddress();
      URL localURL;
      try
      {
        localURL = new URL(str1);
      }
      catch (Exception localException1)
      {
        throw new RuntimeException(localException1);
      }
      String str2 = localURL.getProtocol();
      if (!str2.equalsIgnoreCase("https"))
      {
        str2 = "https";
        String str3 = localURL.getHost();
        if (paramString != null) {
          str3 = paramString;
        }
        int i = localURL.getPort();
        if (paramInt > 0) {
          i = paramInt;
        }
        try
        {
          localURL = new URL(str2, str3, i, localURL.getFile());
        }
        catch (Exception localException2)
        {
          throw new RuntimeException(localException2);
        }
        str1 = localURL.toExternalForm();
        return new WSEndpointReference(str1, paramWSEndpointReference.getVersion());
      }
    }
    return paramWSEndpointReference;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\addressing\OneWayFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
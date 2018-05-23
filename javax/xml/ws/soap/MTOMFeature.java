package javax.xml.ws.soap;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public final class MTOMFeature
  extends WebServiceFeature
{
  public static final String ID = "http://www.w3.org/2004/08/soap/features/http-optimization";
  protected int threshold;
  
  public MTOMFeature()
  {
    enabled = true;
    threshold = 0;
  }
  
  public MTOMFeature(boolean paramBoolean)
  {
    enabled = paramBoolean;
    threshold = 0;
  }
  
  public MTOMFeature(int paramInt)
  {
    if (paramInt < 0) {
      throw new WebServiceException("MTOMFeature.threshold must be >= 0, actual value: " + paramInt);
    }
    enabled = true;
    threshold = paramInt;
  }
  
  public MTOMFeature(boolean paramBoolean, int paramInt)
  {
    if (paramInt < 0) {
      throw new WebServiceException("MTOMFeature.threshold must be >= 0, actual value: " + paramInt);
    }
    enabled = paramBoolean;
    threshold = paramInt;
  }
  
  public String getID()
  {
    return "http://www.w3.org/2004/08/soap/features/http-optimization";
  }
  
  public int getThreshold()
  {
    return threshold;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\soap\MTOMFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
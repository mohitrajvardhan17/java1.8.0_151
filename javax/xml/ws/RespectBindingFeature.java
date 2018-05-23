package javax.xml.ws;

public final class RespectBindingFeature
  extends WebServiceFeature
{
  public static final String ID = "javax.xml.ws.RespectBindingFeature";
  
  public RespectBindingFeature()
  {
    enabled = true;
  }
  
  public RespectBindingFeature(boolean paramBoolean)
  {
    enabled = paramBoolean;
  }
  
  public String getID()
  {
    return "javax.xml.ws.RespectBindingFeature";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\RespectBindingFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
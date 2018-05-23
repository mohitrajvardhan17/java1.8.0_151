package javax.xml.ws;

public abstract class WebServiceFeature
{
  protected boolean enabled = false;
  
  public abstract String getID();
  
  protected WebServiceFeature() {}
  
  public boolean isEnabled()
  {
    return enabled;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\WebServiceFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
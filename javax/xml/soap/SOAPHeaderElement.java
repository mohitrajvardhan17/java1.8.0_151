package javax.xml.soap;

public abstract interface SOAPHeaderElement
  extends SOAPElement
{
  public abstract void setActor(String paramString);
  
  public abstract void setRole(String paramString)
    throws SOAPException;
  
  public abstract String getActor();
  
  public abstract String getRole();
  
  public abstract void setMustUnderstand(boolean paramBoolean);
  
  public abstract boolean getMustUnderstand();
  
  public abstract void setRelay(boolean paramBoolean)
    throws SOAPException;
  
  public abstract boolean getRelay();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\soap\SOAPHeaderElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
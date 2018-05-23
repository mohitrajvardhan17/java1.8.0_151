package javax.xml.stream.events;

public abstract interface EntityDeclaration
  extends XMLEvent
{
  public abstract String getPublicId();
  
  public abstract String getSystemId();
  
  public abstract String getName();
  
  public abstract String getNotationName();
  
  public abstract String getReplacementText();
  
  public abstract String getBaseURI();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\events\EntityDeclaration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
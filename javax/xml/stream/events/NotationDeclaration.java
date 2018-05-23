package javax.xml.stream.events;

public abstract interface NotationDeclaration
  extends XMLEvent
{
  public abstract String getName();
  
  public abstract String getPublicId();
  
  public abstract String getSystemId();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\events\NotationDeclaration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.xml.stream.events;

public abstract interface Namespace
  extends Attribute
{
  public abstract String getPrefix();
  
  public abstract String getNamespaceURI();
  
  public abstract boolean isDefaultNamespaceDeclaration();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\events\Namespace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
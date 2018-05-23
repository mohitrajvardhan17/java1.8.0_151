package javax.xml.stream.events;

public abstract interface EntityReference
  extends XMLEvent
{
  public abstract EntityDeclaration getDeclaration();
  
  public abstract String getName();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\events\EntityReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
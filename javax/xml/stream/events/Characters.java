package javax.xml.stream.events;

public abstract interface Characters
  extends XMLEvent
{
  public abstract String getData();
  
  public abstract boolean isWhiteSpace();
  
  public abstract boolean isCData();
  
  public abstract boolean isIgnorableWhiteSpace();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\events\Characters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
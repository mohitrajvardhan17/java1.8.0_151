package javax.xml.stream.events;

public abstract interface StartDocument
  extends XMLEvent
{
  public abstract String getSystemId();
  
  public abstract String getCharacterEncodingScheme();
  
  public abstract boolean encodingSet();
  
  public abstract boolean isStandalone();
  
  public abstract boolean standaloneSet();
  
  public abstract String getVersion();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\events\StartDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
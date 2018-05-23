package javax.xml.stream.events;

public abstract interface ProcessingInstruction
  extends XMLEvent
{
  public abstract String getTarget();
  
  public abstract String getData();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\events\ProcessingInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
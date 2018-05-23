package javax.xml.stream.events;

import java.util.List;

public abstract interface DTD
  extends XMLEvent
{
  public abstract String getDocumentTypeDeclaration();
  
  public abstract Object getProcessedDTD();
  
  public abstract List getNotations();
  
  public abstract List getEntities();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\events\DTD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.xml.bind.util;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

public class ValidationEventCollector
  implements ValidationEventHandler
{
  private final List<ValidationEvent> events = new ArrayList();
  
  public ValidationEventCollector() {}
  
  public ValidationEvent[] getEvents()
  {
    return (ValidationEvent[])events.toArray(new ValidationEvent[events.size()]);
  }
  
  public void reset()
  {
    events.clear();
  }
  
  public boolean hasEvents()
  {
    return !events.isEmpty();
  }
  
  public boolean handleEvent(ValidationEvent paramValidationEvent)
  {
    events.add(paramValidationEvent);
    boolean bool = true;
    switch (paramValidationEvent.getSeverity())
    {
    case 0: 
      bool = true;
      break;
    case 1: 
      bool = true;
      break;
    case 2: 
      bool = false;
      break;
    default: 
      _assert(false, Messages.format("ValidationEventCollector.UnrecognizedSeverity", Integer.valueOf(paramValidationEvent.getSeverity())));
    }
    return bool;
  }
  
  private static void _assert(boolean paramBoolean, String paramString)
  {
    if (!paramBoolean) {
      throw new InternalError(paramString);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\util\ValidationEventCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
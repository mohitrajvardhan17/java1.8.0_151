package javax.print.event;

import javax.print.PrintService;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.PrintServiceAttributeSet;

public class PrintServiceAttributeEvent
  extends PrintEvent
{
  private static final long serialVersionUID = -7565987018140326600L;
  private PrintServiceAttributeSet attributes;
  
  public PrintServiceAttributeEvent(PrintService paramPrintService, PrintServiceAttributeSet paramPrintServiceAttributeSet)
  {
    super(paramPrintService);
    attributes = AttributeSetUtilities.unmodifiableView(paramPrintServiceAttributeSet);
  }
  
  public PrintService getPrintService()
  {
    return (PrintService)getSource();
  }
  
  public PrintServiceAttributeSet getAttributes()
  {
    return attributes;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\event\PrintServiceAttributeEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
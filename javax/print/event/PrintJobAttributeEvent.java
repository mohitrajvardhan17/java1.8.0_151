package javax.print.event;

import javax.print.DocPrintJob;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.PrintJobAttributeSet;

public class PrintJobAttributeEvent
  extends PrintEvent
{
  private static final long serialVersionUID = -6534469883874742101L;
  private PrintJobAttributeSet attributes;
  
  public PrintJobAttributeEvent(DocPrintJob paramDocPrintJob, PrintJobAttributeSet paramPrintJobAttributeSet)
  {
    super(paramDocPrintJob);
    attributes = AttributeSetUtilities.unmodifiableView(paramPrintJobAttributeSet);
  }
  
  public DocPrintJob getPrintJob()
  {
    return (DocPrintJob)getSource();
  }
  
  public PrintJobAttributeSet getAttributes()
  {
    return attributes;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\event\PrintJobAttributeEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.print;

import java.awt.print.PrinterJob;
import javax.print.attribute.PrintRequestAttribute;

public class PrinterJobWrapper
  implements PrintRequestAttribute
{
  private static final long serialVersionUID = -8792124426995707237L;
  private PrinterJob job;
  
  public PrinterJobWrapper(PrinterJob paramPrinterJob)
  {
    job = paramPrinterJob;
  }
  
  public PrinterJob getPrinterJob()
  {
    return job;
  }
  
  public final Class getCategory()
  {
    return PrinterJobWrapper.class;
  }
  
  public final String getName()
  {
    return "printerjob-wrapper";
  }
  
  public String toString()
  {
    return "printerjob-wrapper: " + job.toString();
  }
  
  public int hashCode()
  {
    return job.hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\PrinterJobWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.print.event;

public abstract class PrintJobAdapter
  implements PrintJobListener
{
  public PrintJobAdapter() {}
  
  public void printDataTransferCompleted(PrintJobEvent paramPrintJobEvent) {}
  
  public void printJobCompleted(PrintJobEvent paramPrintJobEvent) {}
  
  public void printJobFailed(PrintJobEvent paramPrintJobEvent) {}
  
  public void printJobCanceled(PrintJobEvent paramPrintJobEvent) {}
  
  public void printJobNoMoreEvents(PrintJobEvent paramPrintJobEvent) {}
  
  public void printJobRequiresAttention(PrintJobEvent paramPrintJobEvent) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\event\PrintJobAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
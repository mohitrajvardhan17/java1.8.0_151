package javax.print.event;

public abstract interface PrintJobListener
{
  public abstract void printDataTransferCompleted(PrintJobEvent paramPrintJobEvent);
  
  public abstract void printJobCompleted(PrintJobEvent paramPrintJobEvent);
  
  public abstract void printJobFailed(PrintJobEvent paramPrintJobEvent);
  
  public abstract void printJobCanceled(PrintJobEvent paramPrintJobEvent);
  
  public abstract void printJobNoMoreEvents(PrintJobEvent paramPrintJobEvent);
  
  public abstract void printJobRequiresAttention(PrintJobEvent paramPrintJobEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\event\PrintJobListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.print;

public abstract interface CancelablePrintJob
  extends DocPrintJob
{
  public abstract void cancel()
    throws PrintException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\CancelablePrintJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
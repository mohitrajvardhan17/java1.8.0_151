package javax.print;

import javax.print.attribute.PrintRequestAttributeSet;

public abstract interface MultiDocPrintJob
  extends DocPrintJob
{
  public abstract void print(MultiDoc paramMultiDoc, PrintRequestAttributeSet paramPrintRequestAttributeSet)
    throws PrintException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\MultiDocPrintJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
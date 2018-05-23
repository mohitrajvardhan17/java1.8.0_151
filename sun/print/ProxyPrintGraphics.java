package sun.print;

import java.awt.Graphics;
import java.awt.PrintGraphics;
import java.awt.PrintJob;

public class ProxyPrintGraphics
  extends ProxyGraphics
  implements PrintGraphics
{
  private PrintJob printJob;
  
  public ProxyPrintGraphics(Graphics paramGraphics, PrintJob paramPrintJob)
  {
    super(paramGraphics);
    printJob = paramPrintJob;
  }
  
  public PrintJob getPrintJob()
  {
    return printJob;
  }
  
  public Graphics create()
  {
    return new ProxyPrintGraphics(getGraphics().create(), printJob);
  }
  
  public Graphics create(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Graphics localGraphics = getGraphics().create(paramInt1, paramInt2, paramInt3, paramInt4);
    return new ProxyPrintGraphics(localGraphics, printJob);
  }
  
  public Graphics getGraphics()
  {
    return super.getGraphics();
  }
  
  public void dispose()
  {
    super.dispose();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\ProxyPrintGraphics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
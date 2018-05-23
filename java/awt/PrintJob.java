package java.awt;

public abstract class PrintJob
{
  public PrintJob() {}
  
  public abstract Graphics getGraphics();
  
  public abstract Dimension getPageDimension();
  
  public abstract int getPageResolution();
  
  public abstract boolean lastPageFirst();
  
  public abstract void end();
  
  public void finalize()
  {
    end();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\PrintJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
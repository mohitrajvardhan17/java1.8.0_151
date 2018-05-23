package java.sql;

public class DataTruncation
  extends SQLWarning
{
  private int index;
  private boolean parameter;
  private boolean read;
  private int dataSize;
  private int transferSize;
  private static final long serialVersionUID = 6464298989504059473L;
  
  public DataTruncation(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, int paramInt3)
  {
    super("Data truncation", paramBoolean2 == true ? "01004" : "22001");
    index = paramInt1;
    parameter = paramBoolean1;
    read = paramBoolean2;
    dataSize = paramInt2;
    transferSize = paramInt3;
  }
  
  public DataTruncation(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, int paramInt3, Throwable paramThrowable)
  {
    super("Data truncation", paramBoolean2 == true ? "01004" : "22001", paramThrowable);
    index = paramInt1;
    parameter = paramBoolean1;
    read = paramBoolean2;
    dataSize = paramInt2;
    transferSize = paramInt3;
  }
  
  public int getIndex()
  {
    return index;
  }
  
  public boolean getParameter()
  {
    return parameter;
  }
  
  public boolean getRead()
  {
    return read;
  }
  
  public int getDataSize()
  {
    return dataSize;
  }
  
  public int getTransferSize()
  {
    return transferSize;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\DataTruncation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
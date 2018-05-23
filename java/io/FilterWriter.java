package java.io;

public abstract class FilterWriter
  extends Writer
{
  protected Writer out;
  
  protected FilterWriter(Writer paramWriter)
  {
    super(paramWriter);
    out = paramWriter;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    out.write(paramInt);
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    out.write(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void write(String paramString, int paramInt1, int paramInt2)
    throws IOException
  {
    out.write(paramString, paramInt1, paramInt2);
  }
  
  public void flush()
    throws IOException
  {
    out.flush();
  }
  
  public void close()
    throws IOException
  {
    out.close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\FilterWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.java2d.pipe;

public abstract interface AATileGenerator
{
  public abstract int getTileWidth();
  
  public abstract int getTileHeight();
  
  public abstract int getTypicalAlpha();
  
  public abstract void nextTile();
  
  public abstract void getAlpha(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  public abstract void dispose();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\AATileGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
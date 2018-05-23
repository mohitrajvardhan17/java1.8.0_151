package sun.dc.pr;

import sun.dc.DuctusRenderingEngine;
import sun.dc.path.FastPathProducer;
import sun.dc.path.PathConsumer;
import sun.dc.path.PathError;
import sun.dc.path.PathException;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.java2d.pipe.AATileGenerator;

public class Rasterizer
  implements AATileGenerator
{
  public static final int EOFILL = 1;
  public static final int NZFILL = 2;
  public static final int STROKE = 3;
  public static final int ROUND = 10;
  public static final int SQUARE = 20;
  public static final int BUTT = 30;
  public static final int BEVEL = 40;
  public static final int MITER = 50;
  public static final int TILE_SIZE = 1 << PathFiller.tileSizeL2S;
  public static final int TILE_SIZE_L2S = PathFiller.tileSizeL2S;
  public static final int MAX_ALPHA = 1000000;
  public static final int MAX_MITER = 10;
  public static final int MAX_WN = 63;
  public static final int TILE_IS_ALL_0 = 0;
  public static final int TILE_IS_ALL_1 = 1;
  public static final int TILE_IS_GENERAL = 2;
  private static final int BEG = 1;
  private static final int PAC_FILL = 2;
  private static final int PAC_STROKE = 3;
  private static final int PATH = 4;
  private static final int SUBPATH = 5;
  private static final int RAS = 6;
  private int state = 1;
  private PathFiller filler = new PathFiller();
  private PathStroker stroker = new PathStroker(filler);
  private PathDasher dasher = new PathDasher(stroker);
  private PathConsumer curPC;
  
  public Rasterizer()
  {
    Disposer.addRecord(this, new ConsumerDisposer(filler, stroker, dasher));
  }
  
  public void setUsage(int paramInt)
    throws PRError
  {
    if (state != 1) {
      throw new PRError("setUsage: unexpected");
    }
    if (paramInt == 1)
    {
      filler.setFillMode(1);
      curPC = filler;
      state = 2;
    }
    else if (paramInt == 2)
    {
      filler.setFillMode(2);
      curPC = filler;
      state = 2;
    }
    else if (paramInt == 3)
    {
      curPC = stroker;
      filler.setFillMode(2);
      stroker.setPenDiameter(1.0F);
      stroker.setPenT4(null);
      stroker.setCaps(10);
      stroker.setCorners(10, 0.0F);
      state = 3;
    }
    else
    {
      throw new PRError("setUsage: unknown usage type");
    }
  }
  
  public void setPenDiameter(float paramFloat)
    throws PRError
  {
    if (state != 3) {
      throw new PRError("setPenDiameter: unexpected");
    }
    stroker.setPenDiameter(paramFloat);
  }
  
  public void setPenT4(float[] paramArrayOfFloat)
    throws PRError
  {
    if (state != 3) {
      throw new PRError("setPenT4: unexpected");
    }
    stroker.setPenT4(paramArrayOfFloat);
  }
  
  public void setPenFitting(float paramFloat, int paramInt)
    throws PRError
  {
    if (state != 3) {
      throw new PRError("setPenFitting: unexpected");
    }
    stroker.setPenFitting(paramFloat, paramInt);
  }
  
  public void setPenDisplacement(float paramFloat1, float paramFloat2)
    throws PRError
  {
    if (state != 3) {
      throw new PRError("setPenDisplacement: unexpected");
    }
    float[] arrayOfFloat = { 1.0F, 0.0F, 0.0F, 1.0F, paramFloat1, paramFloat2 };
    stroker.setOutputT6(arrayOfFloat);
  }
  
  public void setCaps(int paramInt)
    throws PRError
  {
    if (state != 3) {
      throw new PRError("setCaps: unexpected");
    }
    stroker.setCaps(paramInt);
  }
  
  public void setCorners(int paramInt, float paramFloat)
    throws PRError
  {
    if (state != 3) {
      throw new PRError("setCorners: unexpected");
    }
    stroker.setCorners(paramInt, paramFloat);
  }
  
  public void setDash(float[] paramArrayOfFloat, float paramFloat)
    throws PRError
  {
    if (state != 3) {
      throw new PRError("setDash: unexpected");
    }
    dasher.setDash(paramArrayOfFloat, paramFloat);
    curPC = dasher;
  }
  
  public void setDashT4(float[] paramArrayOfFloat)
    throws PRError
  {
    if (state != 3) {
      throw new PRError("setDashT4: unexpected");
    }
    dasher.setDashT4(paramArrayOfFloat);
  }
  
  public void beginPath(float[] paramArrayOfFloat)
    throws PRError
  {
    beginPath();
  }
  
  public void beginPath()
    throws PRError
  {
    if ((state != 2) && (state != 3)) {
      throw new PRError("beginPath: unexpected");
    }
    try
    {
      curPC.beginPath();
      state = 4;
    }
    catch (PathError localPathError)
    {
      throw new PRError(localPathError.getMessage());
    }
  }
  
  public void beginSubpath(float paramFloat1, float paramFloat2)
    throws PRError
  {
    if ((state != 4) && (state != 5)) {
      throw new PRError("beginSubpath: unexpected");
    }
    try
    {
      curPC.beginSubpath(paramFloat1, paramFloat2);
      state = 5;
    }
    catch (PathError localPathError)
    {
      throw new PRError(localPathError.getMessage());
    }
  }
  
  public void appendLine(float paramFloat1, float paramFloat2)
    throws PRError
  {
    if (state != 5) {
      throw new PRError("appendLine: unexpected");
    }
    try
    {
      curPC.appendLine(paramFloat1, paramFloat2);
    }
    catch (PathError localPathError)
    {
      throw new PRError(localPathError.getMessage());
    }
  }
  
  public void appendQuadratic(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    throws PRError
  {
    if (state != 5) {
      throw new PRError("appendQuadratic: unexpected");
    }
    try
    {
      curPC.appendQuadratic(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    }
    catch (PathError localPathError)
    {
      throw new PRError(localPathError.getMessage());
    }
  }
  
  public void appendCubic(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
    throws PRError
  {
    if (state != 5) {
      throw new PRError("appendCubic: unexpected");
    }
    try
    {
      curPC.appendCubic(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
    }
    catch (PathError localPathError)
    {
      throw new PRError(localPathError.getMessage());
    }
  }
  
  public void closedSubpath()
    throws PRError
  {
    if (state != 5) {
      throw new PRError("closedSubpath: unexpected");
    }
    try
    {
      curPC.closedSubpath();
    }
    catch (PathError localPathError)
    {
      throw new PRError(localPathError.getMessage());
    }
  }
  
  public void endPath()
    throws PRError, PRException
  {
    if ((state != 4) && (state != 5)) {
      throw new PRError("endPath: unexpected");
    }
    try
    {
      curPC.endPath();
      state = 6;
    }
    catch (PathError localPathError)
    {
      throw new PRError(localPathError.getMessage());
    }
    catch (PathException localPathException)
    {
      throw new PRException(localPathException.getMessage());
    }
  }
  
  public void useProxy(FastPathProducer paramFastPathProducer)
    throws PRError, PRException
  {
    if ((state != 2) && (state != 3)) {
      throw new PRError("useProxy: unexpected");
    }
    try
    {
      curPC.useProxy(paramFastPathProducer);
      state = 6;
    }
    catch (PathError localPathError)
    {
      throw new PRError(localPathError.getMessage());
    }
    catch (PathException localPathException)
    {
      throw new PRException(localPathException.getMessage());
    }
  }
  
  public void getAlphaBox(int[] paramArrayOfInt)
    throws PRError
  {
    filler.getAlphaBox(paramArrayOfInt);
  }
  
  public void setOutputArea(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2)
    throws PRError, PRException
  {
    filler.setOutputArea(paramFloat1, paramFloat2, paramInt1, paramInt2);
  }
  
  public int getTileState()
    throws PRError
  {
    return filler.getTileState();
  }
  
  public void writeAlpha(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    throws PRError, PRException, InterruptedException
  {
    filler.writeAlpha(paramArrayOfByte, paramInt1, paramInt2, paramInt3);
  }
  
  public void writeAlpha(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3)
    throws PRError, PRException, InterruptedException
  {
    filler.writeAlpha(paramArrayOfChar, paramInt1, paramInt2, paramInt3);
  }
  
  public void nextTile()
    throws PRError
  {
    filler.nextTile();
  }
  
  public void reset()
  {
    state = 1;
    filler.reset();
    stroker.reset();
    dasher.reset();
  }
  
  public int getTileWidth()
  {
    return TILE_SIZE;
  }
  
  public int getTileHeight()
  {
    return TILE_SIZE;
  }
  
  public int getTypicalAlpha()
  {
    int i = filler.getTileState();
    switch (i)
    {
    case 0: 
      i = 0;
      break;
    case 1: 
      i = 255;
      break;
    case 2: 
      i = 128;
    }
    return i;
  }
  
  public void getAlpha(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    synchronized (Rasterizer.class)
    {
      try
      {
        filler.writeAlpha(paramArrayOfByte, 1, paramInt2, paramInt1);
      }
      catch (PRException localPRException)
      {
        throw new InternalError("Ductus AA error: " + localPRException.getMessage());
      }
      catch (InterruptedException localInterruptedException)
      {
        Thread.currentThread().interrupt();
      }
    }
  }
  
  public void dispose()
  {
    DuctusRenderingEngine.dropRasterizer(this);
  }
  
  private static class ConsumerDisposer
    implements DisposerRecord
  {
    PathConsumer filler;
    PathConsumer stroker;
    PathConsumer dasher;
    
    public ConsumerDisposer(PathConsumer paramPathConsumer1, PathConsumer paramPathConsumer2, PathConsumer paramPathConsumer3)
    {
      filler = paramPathConsumer1;
      stroker = paramPathConsumer2;
      dasher = paramPathConsumer3;
    }
    
    public void dispose()
    {
      filler.dispose();
      stroker.dispose();
      dasher.dispose();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\dc\pr\Rasterizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
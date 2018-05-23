package java.awt.geom;

import java.util.NoSuchElementException;

public class FlatteningPathIterator
  implements PathIterator
{
  static final int GROW_SIZE = 24;
  PathIterator src;
  double squareflat;
  int limit;
  double[] hold = new double[14];
  double curx;
  double cury;
  double movx;
  double movy;
  int holdType;
  int holdEnd;
  int holdIndex;
  int[] levels;
  int levelIndex;
  boolean done;
  
  public FlatteningPathIterator(PathIterator paramPathIterator, double paramDouble)
  {
    this(paramPathIterator, paramDouble, 10);
  }
  
  public FlatteningPathIterator(PathIterator paramPathIterator, double paramDouble, int paramInt)
  {
    if (paramDouble < 0.0D) {
      throw new IllegalArgumentException("flatness must be >= 0");
    }
    if (paramInt < 0) {
      throw new IllegalArgumentException("limit must be >= 0");
    }
    src = paramPathIterator;
    squareflat = (paramDouble * paramDouble);
    limit = paramInt;
    levels = new int[paramInt + 1];
    next(false);
  }
  
  public double getFlatness()
  {
    return Math.sqrt(squareflat);
  }
  
  public int getRecursionLimit()
  {
    return limit;
  }
  
  public int getWindingRule()
  {
    return src.getWindingRule();
  }
  
  public boolean isDone()
  {
    return done;
  }
  
  void ensureHoldCapacity(int paramInt)
  {
    if (holdIndex - paramInt < 0)
    {
      int i = hold.length - holdIndex;
      int j = hold.length + 24;
      double[] arrayOfDouble = new double[j];
      System.arraycopy(hold, holdIndex, arrayOfDouble, holdIndex + 24, i);
      hold = arrayOfDouble;
      holdIndex += 24;
      holdEnd += 24;
    }
  }
  
  public void next()
  {
    next(true);
  }
  
  private void next(boolean paramBoolean)
  {
    if (holdIndex >= holdEnd)
    {
      if (paramBoolean) {
        src.next();
      }
      if (src.isDone())
      {
        done = true;
        return;
      }
      holdType = src.currentSegment(hold);
      levelIndex = 0;
      levels[0] = 0;
    }
    int i;
    switch (holdType)
    {
    case 0: 
    case 1: 
      curx = hold[0];
      cury = hold[1];
      if (holdType == 0)
      {
        movx = curx;
        movy = cury;
      }
      holdIndex = 0;
      holdEnd = 0;
      break;
    case 4: 
      curx = movx;
      cury = movy;
      holdIndex = 0;
      holdEnd = 0;
      break;
    case 2: 
      if (holdIndex >= holdEnd)
      {
        holdIndex = (hold.length - 6);
        holdEnd = (hold.length - 2);
        hold[(holdIndex + 0)] = curx;
        hold[(holdIndex + 1)] = cury;
        hold[(holdIndex + 2)] = hold[0];
        hold[(holdIndex + 3)] = hold[1];
        hold[(holdIndex + 4)] = (curx = hold[2]);
        hold[(holdIndex + 5)] = (cury = hold[3]);
      }
      i = levels[levelIndex];
      while ((i < limit) && (QuadCurve2D.getFlatnessSq(hold, holdIndex) >= squareflat))
      {
        ensureHoldCapacity(4);
        QuadCurve2D.subdivide(hold, holdIndex, hold, holdIndex - 4, hold, holdIndex);
        holdIndex -= 4;
        i++;
        levels[levelIndex] = i;
        levelIndex += 1;
        levels[levelIndex] = i;
      }
      holdIndex += 4;
      levelIndex -= 1;
      break;
    case 3: 
      if (holdIndex >= holdEnd)
      {
        holdIndex = (hold.length - 8);
        holdEnd = (hold.length - 2);
        hold[(holdIndex + 0)] = curx;
        hold[(holdIndex + 1)] = cury;
        hold[(holdIndex + 2)] = hold[0];
        hold[(holdIndex + 3)] = hold[1];
        hold[(holdIndex + 4)] = hold[2];
        hold[(holdIndex + 5)] = hold[3];
        hold[(holdIndex + 6)] = (curx = hold[4]);
        hold[(holdIndex + 7)] = (cury = hold[5]);
      }
      i = levels[levelIndex];
      while ((i < limit) && (CubicCurve2D.getFlatnessSq(hold, holdIndex) >= squareflat))
      {
        ensureHoldCapacity(6);
        CubicCurve2D.subdivide(hold, holdIndex, hold, holdIndex - 6, hold, holdIndex);
        holdIndex -= 6;
        i++;
        levels[levelIndex] = i;
        levelIndex += 1;
        levels[levelIndex] = i;
      }
      holdIndex += 6;
      levelIndex -= 1;
    }
  }
  
  public int currentSegment(float[] paramArrayOfFloat)
  {
    if (isDone()) {
      throw new NoSuchElementException("flattening iterator out of bounds");
    }
    int i = holdType;
    if (i != 4)
    {
      paramArrayOfFloat[0] = ((float)hold[(holdIndex + 0)]);
      paramArrayOfFloat[1] = ((float)hold[(holdIndex + 1)]);
      if (i != 0) {
        i = 1;
      }
    }
    return i;
  }
  
  public int currentSegment(double[] paramArrayOfDouble)
  {
    if (isDone()) {
      throw new NoSuchElementException("flattening iterator out of bounds");
    }
    int i = holdType;
    if (i != 4)
    {
      paramArrayOfDouble[0] = hold[(holdIndex + 0)];
      paramArrayOfDouble[1] = hold[(holdIndex + 1)];
      if (i != 0) {
        i = 1;
      }
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\geom\FlatteningPathIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
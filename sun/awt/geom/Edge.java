package sun.awt.geom;

final class Edge
{
  static final int INIT_PARTS = 4;
  static final int GROW_PARTS = 10;
  Curve curve;
  int ctag;
  int etag;
  double activey;
  int equivalence;
  private Edge lastEdge;
  private int lastResult;
  private double lastLimit;
  
  public Edge(Curve paramCurve, int paramInt)
  {
    this(paramCurve, paramInt, 0);
  }
  
  public Edge(Curve paramCurve, int paramInt1, int paramInt2)
  {
    curve = paramCurve;
    ctag = paramInt1;
    etag = paramInt2;
  }
  
  public Curve getCurve()
  {
    return curve;
  }
  
  public int getCurveTag()
  {
    return ctag;
  }
  
  public int getEdgeTag()
  {
    return etag;
  }
  
  public void setEdgeTag(int paramInt)
  {
    etag = paramInt;
  }
  
  public int getEquivalence()
  {
    return equivalence;
  }
  
  public void setEquivalence(int paramInt)
  {
    equivalence = paramInt;
  }
  
  public int compareTo(Edge paramEdge, double[] paramArrayOfDouble)
  {
    if ((paramEdge == lastEdge) && (paramArrayOfDouble[0] < lastLimit))
    {
      if (paramArrayOfDouble[1] > lastLimit) {
        paramArrayOfDouble[1] = lastLimit;
      }
      return lastResult;
    }
    if ((this == lastEdge) && (paramArrayOfDouble[0] < lastLimit))
    {
      if (paramArrayOfDouble[1] > lastLimit) {
        paramArrayOfDouble[1] = lastLimit;
      }
      return 0 - lastResult;
    }
    int i = curve.compareTo(curve, paramArrayOfDouble);
    lastEdge = paramEdge;
    lastLimit = paramArrayOfDouble[1];
    lastResult = i;
    return i;
  }
  
  public void record(double paramDouble, int paramInt)
  {
    activey = paramDouble;
    etag = paramInt;
  }
  
  public boolean isActiveFor(double paramDouble, int paramInt)
  {
    return (etag == paramInt) && (activey >= paramDouble);
  }
  
  public String toString()
  {
    return "Edge[" + curve + ", " + (ctag == 0 ? "L" : "R") + ", " + (etag == -1 ? "O" : etag == 1 ? "I" : "N") + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\geom\Edge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
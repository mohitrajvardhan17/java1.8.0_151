package sun.awt.geom;

final class CurveLink
{
  Curve curve;
  double ytop;
  double ybot;
  int etag;
  CurveLink next;
  
  public CurveLink(Curve paramCurve, double paramDouble1, double paramDouble2, int paramInt)
  {
    curve = paramCurve;
    ytop = paramDouble1;
    ybot = paramDouble2;
    etag = paramInt;
    if ((ytop < paramCurve.getYTop()) || (ybot > paramCurve.getYBot())) {
      throw new InternalError("bad curvelink [" + ytop + "=>" + ybot + "] for " + paramCurve);
    }
  }
  
  public boolean absorb(CurveLink paramCurveLink)
  {
    return absorb(curve, ytop, ybot, etag);
  }
  
  public boolean absorb(Curve paramCurve, double paramDouble1, double paramDouble2, int paramInt)
  {
    if ((curve != paramCurve) || (etag != paramInt) || (ybot < paramDouble1) || (ytop > paramDouble2)) {
      return false;
    }
    if ((paramDouble1 < paramCurve.getYTop()) || (paramDouble2 > paramCurve.getYBot())) {
      throw new InternalError("bad curvelink [" + paramDouble1 + "=>" + paramDouble2 + "] for " + paramCurve);
    }
    ytop = Math.min(ytop, paramDouble1);
    ybot = Math.max(ybot, paramDouble2);
    return true;
  }
  
  public boolean isEmpty()
  {
    return ytop == ybot;
  }
  
  public Curve getCurve()
  {
    return curve;
  }
  
  public Curve getSubCurve()
  {
    if ((ytop == curve.getYTop()) && (ybot == curve.getYBot())) {
      return curve.getWithDirection(etag);
    }
    return curve.getSubCurve(ytop, ybot, etag);
  }
  
  public Curve getMoveto()
  {
    return new Order0(getXTop(), getYTop());
  }
  
  public double getXTop()
  {
    return curve.XforY(ytop);
  }
  
  public double getYTop()
  {
    return ytop;
  }
  
  public double getXBot()
  {
    return curve.XforY(ybot);
  }
  
  public double getYBot()
  {
    return ybot;
  }
  
  public double getX()
  {
    return curve.XforY(ytop);
  }
  
  public int getEdgeTag()
  {
    return etag;
  }
  
  public void setNext(CurveLink paramCurveLink)
  {
    next = paramCurveLink;
  }
  
  public CurveLink getNext()
  {
    return next;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\geom\CurveLink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
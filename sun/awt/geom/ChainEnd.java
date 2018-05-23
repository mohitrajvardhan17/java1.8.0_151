package sun.awt.geom;

final class ChainEnd
{
  CurveLink head;
  CurveLink tail;
  ChainEnd partner;
  int etag;
  
  public ChainEnd(CurveLink paramCurveLink, ChainEnd paramChainEnd)
  {
    head = paramCurveLink;
    tail = paramCurveLink;
    partner = paramChainEnd;
    etag = paramCurveLink.getEdgeTag();
  }
  
  public CurveLink getChain()
  {
    return head;
  }
  
  public void setOtherEnd(ChainEnd paramChainEnd)
  {
    partner = paramChainEnd;
  }
  
  public ChainEnd getPartner()
  {
    return partner;
  }
  
  public CurveLink linkTo(ChainEnd paramChainEnd)
  {
    if ((etag == 0) || (etag == 0)) {
      throw new InternalError("ChainEnd linked more than once!");
    }
    if (etag == etag) {
      throw new InternalError("Linking chains of the same type!");
    }
    ChainEnd localChainEnd1;
    ChainEnd localChainEnd2;
    if (etag == 1)
    {
      localChainEnd1 = this;
      localChainEnd2 = paramChainEnd;
    }
    else
    {
      localChainEnd1 = paramChainEnd;
      localChainEnd2 = this;
    }
    etag = 0;
    etag = 0;
    tail.setNext(head);
    tail = tail;
    if (partner == paramChainEnd) {
      return head;
    }
    ChainEnd localChainEnd3 = partner;
    ChainEnd localChainEnd4 = partner;
    partner = localChainEnd4;
    partner = localChainEnd3;
    if (head.getYTop() < head.getYTop())
    {
      tail.setNext(head);
      head = head;
    }
    else
    {
      tail.setNext(head);
      tail = tail;
    }
    return null;
  }
  
  public void addLink(CurveLink paramCurveLink)
  {
    if (etag == 1)
    {
      tail.setNext(paramCurveLink);
      tail = paramCurveLink;
    }
    else
    {
      paramCurveLink.setNext(head);
      head = paramCurveLink;
    }
  }
  
  public double getX()
  {
    if (etag == 1) {
      return tail.getXBot();
    }
    return head.getXBot();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\geom\ChainEnd.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
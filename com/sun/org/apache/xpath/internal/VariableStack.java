package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class VariableStack
  implements Cloneable
{
  public static final int CLEARLIMITATION = 1024;
  XObject[] _stackFrames = new XObject[' '];
  int _frameTop;
  private int _currentFrameBottom;
  int[] _links = new int['က'];
  int _linksTop;
  private static XObject[] m_nulls = new XObject['Ѐ'];
  
  public VariableStack()
  {
    reset();
  }
  
  public synchronized Object clone()
    throws CloneNotSupportedException
  {
    VariableStack localVariableStack = (VariableStack)super.clone();
    _stackFrames = ((XObject[])_stackFrames.clone());
    _links = ((int[])_links.clone());
    return localVariableStack;
  }
  
  public XObject elementAt(int paramInt)
  {
    return _stackFrames[paramInt];
  }
  
  public int size()
  {
    return _frameTop;
  }
  
  public void reset()
  {
    _frameTop = 0;
    _linksTop = 0;
    _links[(_linksTop++)] = 0;
    _stackFrames = new XObject[_stackFrames.length];
  }
  
  public void setStackFrame(int paramInt)
  {
    _currentFrameBottom = paramInt;
  }
  
  public int getStackFrame()
  {
    return _currentFrameBottom;
  }
  
  public int link(int paramInt)
  {
    _currentFrameBottom = _frameTop;
    _frameTop += paramInt;
    Object localObject;
    if (_frameTop >= _stackFrames.length)
    {
      localObject = new XObject[_stackFrames.length + 4096 + paramInt];
      System.arraycopy(_stackFrames, 0, localObject, 0, _stackFrames.length);
      _stackFrames = ((XObject[])localObject);
    }
    if (_linksTop + 1 >= _links.length)
    {
      localObject = new int[_links.length + 2048];
      System.arraycopy(_links, 0, localObject, 0, _links.length);
      _links = ((int[])localObject);
    }
    _links[(_linksTop++)] = _currentFrameBottom;
    return _currentFrameBottom;
  }
  
  public void unlink()
  {
    _frameTop = _links[(--_linksTop)];
    _currentFrameBottom = _links[(_linksTop - 1)];
  }
  
  public void unlink(int paramInt)
  {
    _frameTop = _links[(--_linksTop)];
    _currentFrameBottom = paramInt;
  }
  
  public void setLocalVariable(int paramInt, XObject paramXObject)
  {
    _stackFrames[(paramInt + _currentFrameBottom)] = paramXObject;
  }
  
  public void setLocalVariable(int paramInt1, XObject paramXObject, int paramInt2)
  {
    _stackFrames[(paramInt1 + paramInt2)] = paramXObject;
  }
  
  public XObject getLocalVariable(XPathContext paramXPathContext, int paramInt)
    throws TransformerException
  {
    paramInt += _currentFrameBottom;
    XObject localXObject = _stackFrames[paramInt];
    if (null == localXObject) {
      throw new TransformerException(XSLMessages.createXPATHMessage("ER_VARIABLE_ACCESSED_BEFORE_BIND", null), paramXPathContext.getSAXLocator());
    }
    if (localXObject.getType() == 600) {
      return _stackFrames[paramInt] = localXObject.execute(paramXPathContext);
    }
    return localXObject;
  }
  
  public XObject getLocalVariable(int paramInt1, int paramInt2)
    throws TransformerException
  {
    paramInt1 += paramInt2;
    XObject localXObject = _stackFrames[paramInt1];
    return localXObject;
  }
  
  public XObject getLocalVariable(XPathContext paramXPathContext, int paramInt, boolean paramBoolean)
    throws TransformerException
  {
    paramInt += _currentFrameBottom;
    XObject localXObject = _stackFrames[paramInt];
    if (null == localXObject) {
      throw new TransformerException(XSLMessages.createXPATHMessage("ER_VARIABLE_ACCESSED_BEFORE_BIND", null), paramXPathContext.getSAXLocator());
    }
    if (localXObject.getType() == 600) {
      return _stackFrames[paramInt] = localXObject.execute(paramXPathContext);
    }
    return paramBoolean ? localXObject : localXObject.getFresh();
  }
  
  public boolean isLocalSet(int paramInt)
    throws TransformerException
  {
    return _stackFrames[(paramInt + _currentFrameBottom)] != null;
  }
  
  public void clearLocalSlots(int paramInt1, int paramInt2)
  {
    paramInt1 += _currentFrameBottom;
    System.arraycopy(m_nulls, 0, _stackFrames, paramInt1, paramInt2);
  }
  
  public void setGlobalVariable(int paramInt, XObject paramXObject)
  {
    _stackFrames[paramInt] = paramXObject;
  }
  
  public XObject getGlobalVariable(XPathContext paramXPathContext, int paramInt)
    throws TransformerException
  {
    XObject localXObject = _stackFrames[paramInt];
    if (localXObject.getType() == 600) {
      return _stackFrames[paramInt] = localXObject.execute(paramXPathContext);
    }
    return localXObject;
  }
  
  public XObject getGlobalVariable(XPathContext paramXPathContext, int paramInt, boolean paramBoolean)
    throws TransformerException
  {
    XObject localXObject = _stackFrames[paramInt];
    if (localXObject.getType() == 600) {
      return _stackFrames[paramInt] = localXObject.execute(paramXPathContext);
    }
    return paramBoolean ? localXObject : localXObject.getFresh();
  }
  
  public XObject getVariableOrParam(XPathContext paramXPathContext, QName paramQName)
    throws TransformerException
  {
    throw new TransformerException(XSLMessages.createXPATHMessage("ER_VAR_NOT_RESOLVABLE", new Object[] { paramQName.toString() }));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\VariableStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.imageio.metadata;

import javax.imageio.IIOException;
import org.w3c.dom.Node;

public class IIOInvalidTreeException
  extends IIOException
{
  protected Node offendingNode = null;
  
  public IIOInvalidTreeException(String paramString, Node paramNode)
  {
    super(paramString);
    offendingNode = paramNode;
  }
  
  public IIOInvalidTreeException(String paramString, Throwable paramThrowable, Node paramNode)
  {
    super(paramString, paramThrowable);
    offendingNode = paramNode;
  }
  
  public Node getOffendingNode()
  {
    return offendingNode;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\metadata\IIOInvalidTreeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.awt.font;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

public final class TransformAttribute
  implements Serializable
{
  private AffineTransform transform;
  public static final TransformAttribute IDENTITY = new TransformAttribute(null);
  static final long serialVersionUID = 3356247357827709530L;
  
  public TransformAttribute(AffineTransform paramAffineTransform)
  {
    if ((paramAffineTransform != null) && (!paramAffineTransform.isIdentity())) {
      transform = new AffineTransform(paramAffineTransform);
    }
  }
  
  public AffineTransform getTransform()
  {
    AffineTransform localAffineTransform = transform;
    return localAffineTransform == null ? new AffineTransform() : new AffineTransform(localAffineTransform);
  }
  
  public boolean isIdentity()
  {
    return transform == null;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws ClassNotFoundException, IOException
  {
    if (transform == null) {
      transform = new AffineTransform();
    }
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private Object readResolve()
    throws ObjectStreamException
  {
    if ((transform == null) || (transform.isIdentity())) {
      return IDENTITY;
    }
    return this;
  }
  
  public int hashCode()
  {
    return transform == null ? 0 : transform.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject != null) {
      try
      {
        TransformAttribute localTransformAttribute = (TransformAttribute)paramObject;
        if (transform == null) {
          return transform == null;
        }
        return transform.equals(transform);
      }
      catch (ClassCastException localClassCastException) {}
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\font\TransformAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
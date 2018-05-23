package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBException;

public class IllegalAnnotationException
  extends JAXBException
{
  private final List<List<Location>> pos;
  private static final long serialVersionUID = 1L;
  
  public IllegalAnnotationException(String paramString, Locatable paramLocatable)
  {
    super(paramString);
    pos = build(new Locatable[] { paramLocatable });
  }
  
  public IllegalAnnotationException(String paramString, Annotation paramAnnotation)
  {
    this(paramString, cast(paramAnnotation));
  }
  
  public IllegalAnnotationException(String paramString, Locatable paramLocatable1, Locatable paramLocatable2)
  {
    super(paramString);
    pos = build(new Locatable[] { paramLocatable1, paramLocatable2 });
  }
  
  public IllegalAnnotationException(String paramString, Annotation paramAnnotation1, Annotation paramAnnotation2)
  {
    this(paramString, cast(paramAnnotation1), cast(paramAnnotation2));
  }
  
  public IllegalAnnotationException(String paramString, Annotation paramAnnotation, Locatable paramLocatable)
  {
    this(paramString, cast(paramAnnotation), paramLocatable);
  }
  
  public IllegalAnnotationException(String paramString, Throwable paramThrowable, Locatable paramLocatable)
  {
    super(paramString, paramThrowable);
    pos = build(new Locatable[] { paramLocatable });
  }
  
  private static Locatable cast(Annotation paramAnnotation)
  {
    if ((paramAnnotation instanceof Locatable)) {
      return (Locatable)paramAnnotation;
    }
    return null;
  }
  
  private List<List<Location>> build(Locatable... paramVarArgs)
  {
    ArrayList localArrayList = new ArrayList();
    for (Locatable localLocatable : paramVarArgs) {
      if (localLocatable != null)
      {
        List localList = convert(localLocatable);
        if ((localList != null) && (!localList.isEmpty())) {
          localArrayList.add(localList);
        }
      }
    }
    return Collections.unmodifiableList(localArrayList);
  }
  
  private List<Location> convert(Locatable paramLocatable)
  {
    if (paramLocatable == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    while (paramLocatable != null)
    {
      localArrayList.add(paramLocatable.getLocation());
      paramLocatable = paramLocatable.getUpstream();
    }
    return Collections.unmodifiableList(localArrayList);
  }
  
  public List<List<Location>> getSourcePos()
  {
    return pos;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(getMessage());
    Iterator localIterator1 = pos.iterator();
    while (localIterator1.hasNext())
    {
      List localList = (List)localIterator1.next();
      localStringBuilder.append("\n\tthis problem is related to the following location:");
      Iterator localIterator2 = localList.iterator();
      while (localIterator2.hasNext())
      {
        Location localLocation = (Location)localIterator2.next();
        localStringBuilder.append("\n\t\tat ").append(localLocation.toString());
      }
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\IllegalAnnotationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
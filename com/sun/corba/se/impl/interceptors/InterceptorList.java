package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.omg.PortableInterceptor.IORInterceptor;
import org.omg.PortableInterceptor.Interceptor;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.ServerRequestInterceptor;

public class InterceptorList
{
  static final int INTERCEPTOR_TYPE_CLIENT = 0;
  static final int INTERCEPTOR_TYPE_SERVER = 1;
  static final int INTERCEPTOR_TYPE_IOR = 2;
  static final int NUM_INTERCEPTOR_TYPES = 3;
  static final Class[] classTypes = { ClientRequestInterceptor.class, ServerRequestInterceptor.class, IORInterceptor.class };
  private boolean locked = false;
  private InterceptorsSystemException wrapper;
  private Interceptor[][] interceptors = new Interceptor[3][];
  
  InterceptorList(InterceptorsSystemException paramInterceptorsSystemException)
  {
    wrapper = paramInterceptorsSystemException;
    initInterceptorArrays();
  }
  
  void register_interceptor(Interceptor paramInterceptor, int paramInt)
    throws DuplicateName
  {
    if (locked) {
      throw wrapper.interceptorListLocked();
    }
    String str = paramInterceptor.name();
    boolean bool = str.equals("");
    int i = 0;
    Interceptor[] arrayOfInterceptor = interceptors[paramInt];
    if (!bool)
    {
      int j = arrayOfInterceptor.length;
      for (int k = 0; k < j; k++)
      {
        Interceptor localInterceptor = arrayOfInterceptor[k];
        if (localInterceptor.name().equals(str))
        {
          i = 1;
          break;
        }
      }
    }
    if (i == 0)
    {
      growInterceptorArray(paramInt);
      interceptors[paramInt][(interceptors[paramInt].length - 1)] = paramInterceptor;
    }
    else
    {
      throw new DuplicateName(str);
    }
  }
  
  void lock()
  {
    locked = true;
  }
  
  Interceptor[] getInterceptors(int paramInt)
  {
    return interceptors[paramInt];
  }
  
  boolean hasInterceptorsOfType(int paramInt)
  {
    return interceptors[paramInt].length > 0;
  }
  
  private void initInterceptorArrays()
  {
    for (int i = 0; i < 3; i++)
    {
      Class localClass = classTypes[i];
      interceptors[i] = ((Interceptor[])(Interceptor[])Array.newInstance(localClass, 0));
    }
  }
  
  private void growInterceptorArray(int paramInt)
  {
    Class localClass = classTypes[paramInt];
    int i = interceptors[paramInt].length;
    Interceptor[] arrayOfInterceptor = (Interceptor[])Array.newInstance(localClass, i + 1);
    System.arraycopy(interceptors[paramInt], 0, arrayOfInterceptor, 0, i);
    interceptors[paramInt] = arrayOfInterceptor;
  }
  
  void destroyAll()
  {
    int i = interceptors.length;
    for (int j = 0; j < i; j++)
    {
      int k = interceptors[j].length;
      for (int m = 0; m < k; m++) {
        interceptors[j][m].destroy();
      }
    }
  }
  
  void sortInterceptors()
  {
    ArrayList localArrayList1 = null;
    ArrayList localArrayList2 = null;
    int i = interceptors.length;
    for (int j = 0; j < i; j++)
    {
      int k = interceptors[j].length;
      if (k > 0)
      {
        localArrayList1 = new ArrayList();
        localArrayList2 = new ArrayList();
      }
      Object localObject;
      for (int m = 0; m < k; m++)
      {
        localObject = interceptors[j][m];
        if ((localObject instanceof Comparable)) {
          localArrayList1.add(localObject);
        } else {
          localArrayList2.add(localObject);
        }
      }
      if ((k > 0) && (localArrayList1.size() > 0))
      {
        Collections.sort(localArrayList1);
        Iterator localIterator = localArrayList1.iterator();
        localObject = localArrayList2.iterator();
        for (int n = 0; n < k; n++) {
          if (localIterator.hasNext()) {
            interceptors[j][n] = ((Interceptor)localIterator.next());
          } else if (((Iterator)localObject).hasNext()) {
            interceptors[j][n] = ((Interceptor)((Iterator)localObject).next());
          } else {
            throw wrapper.sortSizeMismatch();
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\interceptors\InterceptorList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
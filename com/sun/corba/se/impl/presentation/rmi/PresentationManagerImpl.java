package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import com.sun.corba.se.impl.orbutil.graph.GraphImpl;
import com.sun.corba.se.impl.orbutil.graph.Node;
import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import com.sun.corba.se.spi.presentation.rmi.IDLNameTranslator;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.ClassData;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactory;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactoryFactory;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.rmi.CORBA.Tie;

public final class PresentationManagerImpl
  implements PresentationManager
{
  private Map classToClassData;
  private Map methodToDMM;
  private PresentationManager.StubFactoryFactory staticStubFactoryFactory;
  private PresentationManager.StubFactoryFactory dynamicStubFactoryFactory;
  private ORBUtilSystemException wrapper = null;
  private boolean useDynamicStubs;
  
  public PresentationManagerImpl(boolean paramBoolean)
  {
    useDynamicStubs = paramBoolean;
    wrapper = ORBUtilSystemException.get("rpc.presentation");
    classToClassData = new HashMap();
    methodToDMM = new HashMap();
  }
  
  public synchronized DynamicMethodMarshaller getDynamicMethodMarshaller(Method paramMethod)
  {
    if (paramMethod == null) {
      return null;
    }
    Object localObject = (DynamicMethodMarshaller)methodToDMM.get(paramMethod);
    if (localObject == null)
    {
      localObject = new DynamicMethodMarshallerImpl(paramMethod);
      methodToDMM.put(paramMethod, localObject);
    }
    return (DynamicMethodMarshaller)localObject;
  }
  
  public synchronized PresentationManager.ClassData getClassData(Class paramClass)
  {
    Object localObject = (PresentationManager.ClassData)classToClassData.get(paramClass);
    if (localObject == null)
    {
      localObject = new ClassDataImpl(paramClass);
      classToClassData.put(paramClass, localObject);
    }
    return (PresentationManager.ClassData)localObject;
  }
  
  public PresentationManager.StubFactoryFactory getStubFactoryFactory(boolean paramBoolean)
  {
    if (paramBoolean) {
      return dynamicStubFactoryFactory;
    }
    return staticStubFactoryFactory;
  }
  
  public void setStubFactoryFactory(boolean paramBoolean, PresentationManager.StubFactoryFactory paramStubFactoryFactory)
  {
    if (paramBoolean) {
      dynamicStubFactoryFactory = paramStubFactoryFactory;
    } else {
      staticStubFactoryFactory = paramStubFactoryFactory;
    }
  }
  
  public Tie getTie()
  {
    return dynamicStubFactoryFactory.getTie(null);
  }
  
  public boolean useDynamicStubs()
  {
    return useDynamicStubs;
  }
  
  private Set getRootSet(Class paramClass, NodeImpl paramNodeImpl, Graph paramGraph)
  {
    Set localSet = null;
    if (paramClass.isInterface())
    {
      paramGraph.add(paramNodeImpl);
      localSet = paramGraph.getRoots();
    }
    else
    {
      Class localClass = paramClass;
      HashSet localHashSet = new HashSet();
      while ((localClass != null) && (!localClass.equals(Object.class)))
      {
        NodeImpl localNodeImpl = new NodeImpl(localClass);
        paramGraph.add(localNodeImpl);
        localHashSet.add(localNodeImpl);
        localClass = localClass.getSuperclass();
      }
      paramGraph.getRoots();
      paramGraph.removeAll(localHashSet);
      localSet = paramGraph.getRoots();
    }
    return localSet;
  }
  
  private Class[] getInterfaces(Set paramSet)
  {
    Class[] arrayOfClass = new Class[paramSet.size()];
    Iterator localIterator = paramSet.iterator();
    int i = 0;
    while (localIterator.hasNext())
    {
      NodeImpl localNodeImpl = (NodeImpl)localIterator.next();
      arrayOfClass[(i++)] = localNodeImpl.getInterface();
    }
    return arrayOfClass;
  }
  
  private String[] makeTypeIds(NodeImpl paramNodeImpl, Graph paramGraph, Set paramSet)
  {
    HashSet localHashSet = new HashSet(paramGraph);
    localHashSet.removeAll(paramSet);
    ArrayList localArrayList = new ArrayList();
    if (paramSet.size() > 1) {
      localArrayList.add(paramNodeImpl.getTypeId());
    }
    addNodes(localArrayList, paramSet);
    addNodes(localArrayList, localHashSet);
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
  
  private void addNodes(List paramList, Set paramSet)
  {
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      NodeImpl localNodeImpl = (NodeImpl)localIterator.next();
      String str = localNodeImpl.getTypeId();
      paramList.add(str);
    }
  }
  
  private class ClassDataImpl
    implements PresentationManager.ClassData
  {
    private Class cls;
    private IDLNameTranslator nameTranslator;
    private String[] typeIds;
    private PresentationManager.StubFactory sfactory;
    private InvocationHandlerFactory ihfactory;
    private Map dictionary;
    
    public ClassDataImpl(Class paramClass)
    {
      cls = paramClass;
      GraphImpl localGraphImpl = new GraphImpl();
      PresentationManagerImpl.NodeImpl localNodeImpl = new PresentationManagerImpl.NodeImpl(paramClass);
      Set localSet = PresentationManagerImpl.this.getRootSet(paramClass, localNodeImpl, localGraphImpl);
      Class[] arrayOfClass = PresentationManagerImpl.this.getInterfaces(localSet);
      nameTranslator = IDLNameTranslatorImpl.get(arrayOfClass);
      typeIds = PresentationManagerImpl.this.makeTypeIds(localNodeImpl, localGraphImpl, localSet);
      ihfactory = new InvocationHandlerFactoryImpl(PresentationManagerImpl.this, this);
      dictionary = new HashMap();
    }
    
    public Class getMyClass()
    {
      return cls;
    }
    
    public IDLNameTranslator getIDLNameTranslator()
    {
      return nameTranslator;
    }
    
    public String[] getTypeIds()
    {
      return typeIds;
    }
    
    public InvocationHandlerFactory getInvocationHandlerFactory()
    {
      return ihfactory;
    }
    
    public Map getDictionary()
    {
      return dictionary;
    }
  }
  
  private static class NodeImpl
    implements Node
  {
    private Class interf;
    
    public Class getInterface()
    {
      return interf;
    }
    
    public NodeImpl(Class paramClass)
    {
      interf = paramClass;
    }
    
    public String getTypeId()
    {
      return "RMI:" + interf.getName() + ":0000000000000000";
    }
    
    public Set getChildren()
    {
      HashSet localHashSet = new HashSet();
      Class[] arrayOfClass = interf.getInterfaces();
      for (int i = 0; i < arrayOfClass.length; i++)
      {
        Class localClass = arrayOfClass[i];
        if ((Remote.class.isAssignableFrom(localClass)) && (!Remote.class.equals(localClass))) {
          localHashSet.add(new NodeImpl(localClass));
        }
      }
      return localHashSet;
    }
    
    public String toString()
    {
      return "NodeImpl[" + interf + "]";
    }
    
    public int hashCode()
    {
      return interf.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof NodeImpl)) {
        return false;
      }
      NodeImpl localNodeImpl = (NodeImpl)paramObject;
      return interf.equals(interf);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\presentation\rmi\PresentationManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
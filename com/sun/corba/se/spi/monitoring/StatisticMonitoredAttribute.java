package com.sun.corba.se.spi.monitoring;

public class StatisticMonitoredAttribute
  extends MonitoredAttributeBase
{
  private StatisticsAccumulator statisticsAccumulator;
  private Object mutex;
  
  public StatisticMonitoredAttribute(String paramString1, String paramString2, StatisticsAccumulator paramStatisticsAccumulator, Object paramObject)
  {
    super(paramString1);
    MonitoredAttributeInfoFactory localMonitoredAttributeInfoFactory = MonitoringFactories.getMonitoredAttributeInfoFactory();
    MonitoredAttributeInfo localMonitoredAttributeInfo = localMonitoredAttributeInfoFactory.createMonitoredAttributeInfo(paramString2, String.class, false, true);
    setMonitoredAttributeInfo(localMonitoredAttributeInfo);
    statisticsAccumulator = paramStatisticsAccumulator;
    mutex = paramObject;
  }
  
  /* Error */
  public Object getValue()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 48	com/sun/corba/se/spi/monitoring/StatisticMonitoredAttribute:mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 47	com/sun/corba/se/spi/monitoring/StatisticMonitoredAttribute:statisticsAccumulator	Lcom/sun/corba/se/spi/monitoring/StatisticsAccumulator;
    //   11: invokevirtual 53	com/sun/corba/se/spi/monitoring/StatisticsAccumulator:getValue	()Ljava/lang/String;
    //   14: aload_1
    //   15: monitorexit
    //   16: areturn
    //   17: astore_2
    //   18: aload_1
    //   19: monitorexit
    //   20: aload_2
    //   21: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	22	0	this	StatisticMonitoredAttribute
    //   5	14	1	Ljava/lang/Object;	Object
    //   17	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	16	17	finally
    //   17	20	17	finally
  }
  
  public void clearState()
  {
    synchronized (mutex)
    {
      statisticsAccumulator.clearState();
    }
  }
  
  public StatisticsAccumulator getStatisticsAccumulator()
  {
    return statisticsAccumulator;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\monitoring\StatisticMonitoredAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
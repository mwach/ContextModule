/**
 * 
 */
package com.safran.arena.stubs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import com.safran.arena.DataRepositoryInterface;
import com.safran.arena.impl.Client;
import com.safran.arena.impl.ModuleImpl;

import eu.arena_fp7._1.AbstractDataFusionType;
import eu.arena_fp7._1.DataFilter;
import eu.arena_fp7._1.DataProducer;
import eu.arena_fp7._1.ObjectFactory;
import eu.arena_fp7._1.Threat;

/**
 * This stub is a fake repository. It memorizes all that it sees, records nothing on disk, and reply to data requests
 * according to what it has in memory. Remark: a request for objects of a class is considered stricly, objects of the
 * subclasses are not returned. It can be considered as a bug, indeed, but this is only a fake repository for now and no
 * need for better has been recorded up to now. <br>
 * Note for understanding the code: the internal stocking of data is hierarchical, using quite arbitrary ordering to
 * optimize the requests. Thus the getData() method has been split in one per hierarchy layer.
 * <p>
 * This module is coded by extending ModuleImpl, which is a generic multi-purpose Module shell.
 * 
 * @author F270116
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class RepositoryStubService extends ModuleImpl implements DataRepositoryInterface {

    private Client _client;
    // map<source, X> X=map<class, Y> Y=TreeSet<object> sorted by startdate ?
    private HashMap<String, HashMap<String, TreeSet<AbstractDataFusionType>>> _DB = new HashMap<String, HashMap<String, TreeSet<AbstractDataFusionType>>>();

    /**
     * Sort by date first, then by source, then by ID. Null are allowed, but there should be no null !
     * 
     * @author F270116
     * 
     */
    protected class AbstractDataFusionTypeSortByDate implements Comparator<AbstractDataFusionType> {

        @Override
        public int compare(AbstractDataFusionType left, AbstractDataFusionType right) {
            // leftDate is StartValidityPeriod, but if this is null it defaults
            // to TimeStamp.
            Long leftD = left.getStartValidityPeriod();
            if (leftD == null) {
                leftD = left.getTimestamp();
            }
            Long rightD = right.getStartValidityPeriod();
            if (rightD == null) {
                rightD = right.getTimestamp();
            }
            if (leftD == null) {
                if (rightD == null) {
                    return 0;
                } else {
                    return 1;
                }
            }
            if (rightD == null) {
                return -1;
            }
            int res = leftD.compareTo(rightD);
            if (res != 0) {
                return res;
            }
            String leftS = left.getDataSourceId();
            String rightS = right.getDataSourceId();
            if (leftS == null) {
                if (rightS == null) {
                    return 0;
                } else {
                    return -1;
                }
            }
            if (rightS == null) {
                return 1;
            }
            res = leftS.compareTo(rightS);
            if (res != 0) {
                return res;
            }
            leftS = left.getId();
            rightS = right.getId();
            if (leftS == null) {
                if (rightS == null) {
                    return 0;
                } else {
                    return -1;
                }
            }
            if (rightS == null) {
                return 1;
            }
            res = leftS.compareTo(rightS);
            return res;
        }

    }

    private AbstractDataFusionTypeSortByDate _objectSorterByDate = new AbstractDataFusionTypeSortByDate();

    /**
     * Initializations
     */
    public void init() {
        _client = new Client();
        _client.connectToServer();

    }

    /**
     * registers the module as Repository, obviously, but also as DataConsumer since it needs to listen to every data to
     * record them.
     */
    public void registerService() {
        _client.registerModule(this);
        _client.registerModuleAsRepository(this);
        _client.registerModuleAsDataConsumer(this);
    }

    public void unregisterService() {
        _client.unregisterModule(this);
    }

    public RepositoryStubService(String moduleName) {
        super(moduleName);
    }

    private List<AbstractDataFusionType> getData(TreeSet<AbstractDataFusionType> dataSet, DataFilter filter) {
        List<AbstractDataFusionType> returnedObjects = new ArrayList<AbstractDataFusionType>();
        // following trick is dirty, pure souls, do turn your eyes elsewhere !
        // ;)
        Threat startDummy = new Threat();
        startDummy.setTimestamp(filter.getStartDate() == null ? 0 : filter.getStartDate());
        Threat endDummy = new Threat();
        endDummy.setTimestamp(filter.getEndDate() == null ? Long.MAX_VALUE : filter.getEndDate());
        endDummy.setDataSourceId('\255' + "");
        returnedObjects.addAll(dataSet.subSet(startDummy, endDummy));
        return returnedObjects;
    }

    private List<AbstractDataFusionType> getData(HashMap<String, TreeSet<AbstractDataFusionType>> classMap, DataFilter filter) {
        List<AbstractDataFusionType> returnedObjects = new ArrayList<AbstractDataFusionType>();
        if (filter.getDataFusionType() == null) {
            for (TreeSet<AbstractDataFusionType> dataSet : classMap.values()) {
                returnedObjects.addAll(getData(dataSet, filter));
            }
        } else {
            TreeSet<AbstractDataFusionType> dataSet = classMap.get(filter.getDataFusionType());
            if (dataSet != null) {
                returnedObjects.addAll(getData(dataSet, filter));
            }
        }

        return returnedObjects;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.safran.arena.impl.ModuleImpl#getData(java.lang.Class, java.lang.String, java.util.List)
     */
    @Override
    public List<AbstractDataFusionType> getData(Class<?> type, String dataSourceID, List<DataFilter> filters) {

        // iterate on filters
        // for each filter, apply datasourcefilter and call specialised getData
        // on resulting maps
        List<AbstractDataFusionType> returnedObjects = super.getData(type, dataSourceID, filters);
        synchronized (_DB) {
            for (DataFilter filter : filters) {
                if (filter.getProducerId() == null) {
                    for (HashMap<String, TreeSet<AbstractDataFusionType>> classMap : _DB.values()) {
                        returnedObjects.addAll(getData(classMap, filter));
                    }
                } else {
                    HashMap<String, TreeSet<AbstractDataFusionType>> classMap = _DB.get(filter.getProducerId());
                    if (classMap != null) {
                        returnedObjects.addAll(getData(classMap, filter));
                    }
                }
            }
        }
        return returnedObjects;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.safran.arena.impl.ModuleImpl#onDataAvailable(java.lang.Class, java.lang.String,
     * eu.arena_fp7._1.AbstractDataFusionType)
     */
    @Override
    public void onDataAvailable(Class dataType, String dataSourceId, AbstractDataFusionType data) {

        super.onDataAvailable(dataType, dataSourceId, data);
        synchronized (_DB) {

            HashMap<String, TreeSet<AbstractDataFusionType>> classMap = _DB.get(dataSourceId);
            if (classMap == null) {
                classMap = new HashMap<String, TreeSet<AbstractDataFusionType>>();
                _DB.put(dataSourceId, classMap);
            }
            TreeSet<AbstractDataFusionType> dataSet = classMap.get(dataType.getSimpleName());
            if (dataSet == null) {
                dataSet = new TreeSet<AbstractDataFusionType>(_objectSorterByDate);
                classMap.put(dataType.getSimpleName(), dataSet);
            }
            dataSet.add(data);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.safran.arena.impl.ModuleImpl#onDataChanged(java.lang.Class, java.lang.String,
     * eu.arena_fp7._1.AbstractDataFusionType)
     */
    @Override
    public void onDataChanged(Class dataType, String dataSourceId, AbstractDataFusionType data) {
        super.onDataChanged(dataType, dataSourceId, data);
        synchronized (_DB) {

            HashMap<String, TreeSet<AbstractDataFusionType>> classMap = _DB.get(dataSourceId);
            if (classMap == null) {
                classMap = new HashMap<String, TreeSet<AbstractDataFusionType>>();
                _DB.put(dataSourceId, classMap);
            }
            TreeSet<AbstractDataFusionType> dataSet = classMap.get(dataType.getSimpleName());
            if (dataSet == null) {
                dataSet = new TreeSet<AbstractDataFusionType>(_objectSorterByDate);
                classMap.put(dataType.getSimpleName(), dataSet);
            }
            dataSet.remove(data);
            dataSet.add(data);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.safran.arena.impl.ModuleImpl#onDataDeleted(java.lang.Class, java.lang.String,
     * eu.arena_fp7._1.AbstractDataFusionType)
     */
    @Override
    public void onDataDeleted(Class dataType, String dataSourceId, AbstractDataFusionType data) {
        super.onDataDeleted(dataType, dataSourceId, data);
        synchronized (_DB) {
            HashMap<String, TreeSet<AbstractDataFusionType>> classMap = _DB.get(dataSourceId);
            if (classMap == null) {
                // not in collection,
                return;
            }
            TreeSet<AbstractDataFusionType> dataSet = classMap.get(dataType.getSimpleName());
            if (dataSet == null) {
                // not in collection,
                return;
            }
            dataSet.remove(data);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.safran.arena.impl.ModuleImpl#getDataProducers()
     */
    @Override
    public List<DataProducer> getDataProducers() {
        ObjectFactory factory = new ObjectFactory();
        List<DataProducer> producers = super.getDataProducers();
        for (String producerName : _DB.keySet()) {
            DataProducer producer = factory.createDataProducer();
            producer.setId(producerName);
            producers.add(producer);
        }
        return producers;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        RepositoryStubService stub = new RepositoryStubService("Repository Stub " + Math.random());
        stub.init();
        stub.registerService();

    }

}

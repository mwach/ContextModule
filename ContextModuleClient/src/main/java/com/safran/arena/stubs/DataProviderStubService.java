/**
 * 
 */
package com.safran.arena.stubs;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.safran.arena.DataProducerInterface;
import com.safran.arena.impl.Client;
import com.safran.arena.impl.ModuleImpl;

import eu.arena_fp7._1.AbstractDataFusionType;
import eu.arena_fp7._1.ObjectFactory;

/**
 * This stub acts like a dummy data provider. It registers, unregisters, and sends dummy data.
 * <p>
 * If it is called with one or more command line parameters, it tries to understand them as directories containning XML
 * files, read them, build {@link AbstractDataFusionType} object from them, and push them on the platform.
 * 
 * @author F270116
 * 
 */
public class DataProviderStubService extends ModuleImpl implements DataProducerInterface {

    private Client _client;

    public DataProviderStubService(String moduleName) {
        super(moduleName);
    }

    /**
     * Initializations
     */
    public void init() {
        _client = new Client();
        _client.connectToServer();

    }

    /**
     * This module is only a Data Provider. This method registers this module as such.
     */
    public void registerService() {
        _client.registerModule(this);
        _client.registerModuleAsDataProvider(this);
    }

    public void unregisterService() {
        _client.unregisterModule(this);
    }

    /**
     * Method to push a directory content on the Integration Platform. Current limitation : no time scheduling is done !
     * 
     * @param directory
     */
    public void flushDirectory(File directory) {
        JAXBContext jaxbContext;
        Unmarshaller unmarshaller;
        try {
            jaxbContext = JAXBContext.newInstance("eu.arena_fp7._1:org.uncertml._2");
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e1) {
            e1.printStackTrace();
            return;
        }

        File list[] = directory.listFiles().clone();
        Arrays.sort(list, new Comparator<File>() {

            @Override
            public int compare(File left, File right) {

                return left.getName().compareTo(right.getName());
            }

        });
        for (File file : list) {
            try {
                JAXBElement<?> je = (JAXBElement<?>) unmarshaller.unmarshal(file);
                Object o = je.getValue();
                if (o instanceof AbstractDataFusionType) {
                    AbstractDataFusionType data = (AbstractDataFusionType) o;
                    if (data.getDataSourceId() == null || data.getDataSourceId().isEmpty()) {
                        data.setDataSourceId(getModuleName());
                    }
                    _client.publish(this.getModuleName(), data);
                } else {
                    System.err.println("Error in file " + file + " : object is not AbstractDataFusionType");
                }

            } catch (JAXBException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        ObjectFactory factory = new ObjectFactory();

        DataProviderStubService stub = new DataProviderStubService("DataProvider Stub " + Math.random());
        stub.init();
        stub.registerService();

        if (args.length > 0) {
            for (String directoryPath : args) {
                File directory = new File(directoryPath);
                if (!directory.canRead()) {
                    System.err.println("Cannot read directory " + directoryPath);
                } else if (!directory.isDirectory()) {
                    System.err.println("Not a directory directory " + directoryPath);
                } else {
                    stub.flushDirectory(directory);
                }

            }
        } else {

            for (int i = 0; i < 10; i++) {
                eu.arena_fp7._1.Object o = factory.createObject();
                o.setId("toto" + i);
                stub._client.publish(stub.getClass().toString(), o);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        stub.unregisterService();
    }

}

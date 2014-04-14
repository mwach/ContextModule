package itti.com.pl.arena.cm.service;

import java.util.List;

import eu.arena_fp7._1.AbstractNamedValue;
import eu.arena_fp7._1.BooleanNamedValue;
import eu.arena_fp7._1.Object;
import eu.arena_fp7._1.SimpleNamedValue;

/**
 * General external interface for the ContextModule defines method, which can be used by external component to access CM
 * resources
 * 
 * @author mawa
 * 
 */
public interface LocalContextModule extends ContextModule{

    /**
     * Prepares instance of the {@link AbstractNamedValue} class
     * 
     * @param id
     *            ID of the object
     * @param value
     *            value of the object
     * @return object containing provided values
     */
    SimpleNamedValue createSimpleNamedValue(String id, String value);

    /**
     * Prepares instance of the {@link AbstractNamedValue} class
     * 
     * @param featureName
     *            ID of the object
     * @param value
     *            value of the object
     * @return object containing provided values
     */
    BooleanNamedValue createBooleanNamedValue(String id, String featureName, boolean status);

    /**
     * Prepares instance of the {@link AbstractNamedValue} class
     * 
     * @param id
     *            ID of the object
     * @param value
     *            value of the object
     * @return object containing provided values
     */
    AbstractNamedValue createCoordinate(String id, double x, double y, double z);

    /**
     * Prepares instance of the {@link AbstractNamedValue} class
     * 
     * @param id
     *            ID of the object
     * @param vector
     * @param value
     *            value of the object
     * @return object containing provided values
     */
    Object createObject(String id, String href, List<AbstractNamedValue> vector);

    void setBrokerUrl(String brokerUrl);

    void init();

    void shutdown();
}

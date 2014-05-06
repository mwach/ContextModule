package itti.com.pl.arena.cm.jms;

import itti.com.pl.arena.cm.utils.helper.StringHelper;

import java.util.List;

import com.safran.arena.impl.ModuleImpl;

import eu.arena_fp7._1.AbstractNamedValue;
import eu.arena_fp7._1.BooleanNamedValue;
import eu.arena_fp7._1.Object;
import eu.arena_fp7._1.ObjectFactory;
import eu.arena_fp7._1.RealWorldCoordinate;
import eu.arena_fp7._1.SimpleNamedValue;

public class CMModuleImpl extends ModuleImpl {

    private ObjectFactory factory;

    public CMModuleImpl(String moduleName) {
        super(moduleName);

        factory = new ObjectFactory();
    }

    private ObjectFactory getFactory() {
        return factory;
    }

    /**
     * Prepares instance of the {@link AbstractNamedValue} class
     * 
     * @param id
     *            ID of the object
     * @param value
     *            value of the object
     * @return object containing provided values
     */
    public SimpleNamedValue createSimpleNamedValue(String id, String featureName, String value) {
        SimpleNamedValue snv = getFactory().createSimpleNamedValue();
        snv.setDataSourceId(getModuleName());
        // snv.setId(String.format("CM_RESP_%s", StringHelper.toString(id)));
        snv.setId(StringHelper.toString(id));
        snv.setFeatureName(featureName);
        snv.setValue(StringHelper.toString(value));
        return snv;
    }

    /**
     * Prepares instance of the {@link AbstractNamedValue} class
     * 
     * @param id
     *            ID of the object
     * @param featureName
     *            name of the feature/service
     * @param status
     *            status of the feature
     * @return object containing provided values
     */
    public BooleanNamedValue createBooleanNamedValue(String id, String featureName, boolean status) {
        BooleanNamedValue bnv = getFactory().createBooleanNamedValue();
        bnv.setDataSourceId(getModuleName());
        bnv.setFeatureName(featureName);
        // bnv.setId(String.format("CM_RESP_%s", StringHelper.toString(id)));
        bnv.setId(StringHelper.toString(id));
        bnv.setFeatureValue(status);
        return bnv;
    }

    /**
     * Prepares instance of the {@link AbstractNamedValue} class
     * 
     * @param id
     *            ID of the object
     * @param x value of the longitude 
     * @param y value of the latitude
     * @param z value of the altitude
     *            
     * @return object containing provided values
     */
    public AbstractNamedValue createCoordinate(String id, double x, double y, double z) {
        RealWorldCoordinate rwc = getFactory().createRealWorldCoordinate();
        rwc.setDataSourceId(getModuleName());
        rwc.setId(StringHelper.toString(id));
        rwc.setX(x);
        rwc.setY(y);
        rwc.setZ(z);
        return rwc;
    }

    /**
     * Prepares instance of the {@link AbstractNamedValue} class
     * 
     * @param id
     *            ID of the object
     * @param vector list of features
     * @return object containing provided values
     */
    public Object createObject(String id, List<AbstractNamedValue> vector) {
        Object object = getFactory().createObject();
        object.setFeatureVector(getFactory().createFeatureVector());
        // object.setId(String.format("CM_RESP_%s", StringHelper.toString(id)));
        object.setId(StringHelper.toString(id));
        object.setDataSourceId(getModuleName());
        object.getFeatureVector().setId(StringHelper.toString(id));
        object.getFeatureVector().setDataSourceId(getModuleName());
        object.getFeatureVector().getFeature().addAll(vector);
        return object;
    }

}

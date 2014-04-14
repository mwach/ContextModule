package itti.com.pl.arena.cm.jms;

import itti.com.pl.arena.cm.Constants;
import itti.com.pl.arena.cm.utils.helper.StringHelper;

import java.util.List;

import com.safran.arena.impl.ModuleImpl;

import eu.arena_fp7._1.AbstractNamedValue;
import eu.arena_fp7._1.BooleanNamedValue;
import eu.arena_fp7._1.Object;
import eu.arena_fp7._1.ObjectFactory;
import eu.arena_fp7._1.RealWorldCoordinate;
import eu.arena_fp7._1.SimpleNamedValue;

public class CMModuleImpl extends ModuleImpl{

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
    public SimpleNamedValue createSimpleNamedValue(String id, String value) {
        SimpleNamedValue snv = getFactory().createSimpleNamedValue();
        snv.setDataSourceId(Constants.MODULE_NAME);
        snv.setId(String.format("CM_RESP_%s", StringHelper.toString(id)));
        snv.setValue(StringHelper.toString(value));
        return snv;
    }

    /**
     * Prepares instance of the {@link AbstractNamedValue} class
     * 
     * @param featureName
     *            ID of the object
     * @param value
     *            value of the object
     * @return object containing provided values
     */
    public BooleanNamedValue createBooleanNamedValue(String id, String featureName, boolean status) {
        BooleanNamedValue bnv = getFactory().createBooleanNamedValue();
        bnv.setDataSourceId(Constants.MODULE_NAME);
        bnv.setId(String.format("CM_RESP_%s", StringHelper.toString(id)));
        bnv.setFeatureName(featureName);
        bnv.setFeatureValue(status);
        return bnv;
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
    public AbstractNamedValue createCoordinate(String id, double x, double y, double z) {
        RealWorldCoordinate rwc = getFactory().createRealWorldCoordinate();
        rwc.setDataSourceId(Constants.MODULE_NAME);
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
     * @param vector
     * @param value
     *            value of the object
     * @return object containing provided values
     */
    public Object createObject(String id, String href, List<AbstractNamedValue> vector) {
        Object object = getFactory().createObject();
        object.setFeatureVector(getFactory().createFeatureVector());
        object.setId(String.format("CM_RESP_%s", StringHelper.toString(id)));
        object.setDataSourceId(Constants.MODULE_NAME);
        object.setHref(href);
        object.getFeatureVector().setId(StringHelper.toString(id));
        object.getFeatureVector().setDataSourceId(Constants.MODULE_NAME);
        object.getFeatureVector().getFeature().addAll(vector);
        return object;
    }

}

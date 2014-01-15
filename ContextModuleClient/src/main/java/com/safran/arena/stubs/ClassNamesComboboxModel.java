/**
 * 
 */
package com.safran.arena.stubs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import eu.arena_fp7._1.AbstractDataFusionType;

/**
 * This model is used to fill a ComboBox with the class names of the package eu.arena_fp7._1.
 * 
 * @author F270116
 * 
 */
@SuppressWarnings("rawtypes")
public class ClassNamesComboboxModel extends DefaultComboBoxModel {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    public static final String PACKAGE_NAME = "eu.arena_fp7._1";

    /**
     * Internal method to get the class names. This uses a utility code since java does not provide this sort of
     * introspection by default. For your curiosity, this is not provided by the language because a package can be
     * extended by multiple jars, and this can be dynamic. Consequently providing this information is time consuming,
     * requires access to mass storage, or even worse. Anyway, for the sake of our example, we can afford that since all
     * the arena classes are in one jar.
     * 
     * @return
     */
    private static String[] getClassNames() {
        String[] a = { "" };
        ArrayList<String> classNames = new ArrayList<String>();
        try {
            List<Class> classList = UtilIntrospection.getClasses(PACKAGE_NAME);
            for (Class c : classList) {
                if (AbstractDataFusionType.class.isAssignableFrom(c)) {
                    classNames.add(c.getSimpleName());
                }
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return classNames.toArray(a);

    }

    /**
	 * 
	 */
    @SuppressWarnings("unchecked")
    public ClassNamesComboboxModel() {
        super(getClassNames());
    }

}

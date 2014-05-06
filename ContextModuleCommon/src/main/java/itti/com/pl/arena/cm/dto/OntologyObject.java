package itti.com.pl.arena.cm.dto;

import java.io.Serializable;

public abstract class OntologyObject implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /*
     * ID of the object (unique per module/ontology)
     */
    private String id;

    /**
     * Creates a new ontology object
     * 
     * @param id
     *            ID of the object
     */
    public OntologyObject(String id) {
        this.id = id;
    }

    /**
     * Returns information about ID of the object
     * 
     * @return ID of the object
     */
    public String getId() {
        return id;
    }
}

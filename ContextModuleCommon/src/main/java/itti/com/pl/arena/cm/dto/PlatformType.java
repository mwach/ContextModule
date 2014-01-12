package itti.com.pl.arena.cm.dto;

/**
 * Available platform types supported by the ContextModule
 * @author cm-admin
 *
 */
public enum PlatformType {

    /**
     * Vehicle with cameras installed on it - default Platform Type used by the ContextModule
     */
    Vehicle_with_cameras,
    /**
     * Basic vehicle supported by the module. Does not support cameras
     */
    Truck
}

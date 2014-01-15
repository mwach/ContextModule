package itti.com.pl.arena.cm.geoportal.gov.pl.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public abstract class GeoportalRequestObject {

    private static final int DISPLAY_X_COORDINATE = 1024;
    private static final int DISPLAY_Y_COORDINATE = 640;
    private static final int DISPLAY_RESOLUTION = 96;

    public enum Wkid {
        W_2180(2180, 80, 60), W_4326(4326, 0.003, 0.002);

        private int value;
        private double xDeltaValue;
        private double yDeltaValue;

        private Wkid(int value, double xDeltaValue, double yDeltaValue) {
            this.value = value;
            this.xDeltaValue = xDeltaValue;
            this.yDeltaValue = yDeltaValue;
        }

        public int getValue() {
            return value;
        }

        public double getXDeltaValue() {
            return xDeltaValue;
        }

        public double getYDeltaValue() {
            return yDeltaValue;
        }

        public static Wkid getDefault() {
            return W_4326;
        }
    }

    private static final String DEFAULT_GEOMETRY = "esriGeometryPoint";
    private static final boolean DEFAULT_RETURN_GEOMETRY = false;
    private static final int DEFULT_TOLERANCE = 5;
    private static final String DEFULT_LAYERS = "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19";
    private static final String DEFAULT_FORMAT = "json";

    private Geometry geometry;
    private String geometryType;
    private MapExtent mapExtent;
    private ImageDisplay imageDisplay;
    private int sr;
    private boolean returnGeometry;
    private int tolerance;
    private String layers;
    private String format;
    private Wkid wkid;

    public GeoportalRequestObject(double longitude, double latitude) {
        this(longitude, latitude, Wkid.getDefault());
    }

    public GeoportalRequestObject(double longitude, double latitude, Wkid wkid) {
        this.geometry = new Geometry(longitude, latitude, wkid.getValue());
        this.geometryType = DEFAULT_GEOMETRY;
        this.mapExtent = new MapExtent(longitude, latitude, wkid);
        this.imageDisplay = new ImageDisplay(DISPLAY_X_COORDINATE, DISPLAY_Y_COORDINATE, DISPLAY_RESOLUTION);
        this.sr = wkid.getValue();
        this.returnGeometry = DEFAULT_RETURN_GEOMETRY;
        this.tolerance = DEFULT_TOLERANCE;
        this.layers = DEFULT_LAYERS;
        this.wkid = wkid;
        setFormat(DEFAULT_FORMAT);
    }

    public double getLongitude() {
        return geometry.getX();
    }

    public double getLatitude() {
        return geometry.getY();
    }

    public String getWkid() {
        return String.valueOf(wkid.getValue());
    }

    public Geometry getGeometry() {
        return geometry;
    }

    protected Wkid getWkidObj() {
        return wkid;
    }

    public String getGeometryType() {
        return geometryType;
    }

    public MapExtent getMapExtent() {
        return mapExtent;
    }

    public ImageDisplay getImageDisplay() {
        return imageDisplay;
    }

    public int getSr() {
        return sr;
    }

    public boolean isReturnGeometry() {
        return returnGeometry;
    }

    public int getTolerance() {
        return tolerance;
    }

    public String getLayers() {
        return layers;
    }

    public String getFormat() {
        return format;
    }

    protected void setFormat(String format) {
        this.format = format;
    }

    private static class Geometry {
        private double x;
        private double y;
        private SpatialReference spatialReference;

        public Geometry(double x, double y, int wkid) {
            this.x = x;
            this.y = y;
            this.spatialReference = new SpatialReference(wkid);
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public String getWkid() {
            return spatialReference.getWkid();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(getX()).append(getY()).append(getWkid()).hashCode();
        }

        @Override
        public boolean equals(Object obj) {

            if (!(obj instanceof Geometry)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            Geometry other = (Geometry) obj;
            return new EqualsBuilder().append(getX(), other.getX()).append(getY(), other.getY())
                    .append(getWkid(), other.getWkid()).isEquals();
        }

    }

    private static class MapExtent {

        private double xmin;
        private double ymin;
        private double xmax;
        private double ymax;
        private SpatialReference spatialReference;

        public MapExtent(double x, double y, Wkid wkid) {
            this.xmin = x - wkid.getXDeltaValue();
            this.xmax = x + wkid.getXDeltaValue();
            this.ymin = y - wkid.getYDeltaValue();
            this.ymax = y + wkid.getYDeltaValue();
            this.spatialReference = new SpatialReference(wkid.getValue());
        }

        public double getXmin() {
            return xmin;
        }

        public double getYmin() {
            return ymin;
        }

        public double getXmax() {
            return xmax;
        }

        public double getYmax() {
            return ymax;
        }

        public SpatialReference getSpatialReference() {
            return spatialReference;
        }

        @Override
        public int hashCode() {

            return new HashCodeBuilder().append(getXmax()).append(getYmax()).append(getXmin()).append(getYmin())
                    .append(getSpatialReference()).hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MapExtent)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            MapExtent other = (MapExtent) obj;
            return new EqualsBuilder().append(getXmax(), other.getXmax()).append(getYmax(), other.getYmax())
                    .append(getXmin(), other.getXmin()).append(getYmin(), other.getYmin())
                    .append(getSpatialReference(), other.getSpatialReference()).isEquals();

        }

    }

    private static class SpatialReference {

        private String wkid;

        public SpatialReference(String wkid) {
            this.wkid = wkid;
        }

        public SpatialReference(int wkid) {
            this(String.valueOf(wkid));
        }

        public String getWkid() {
            return wkid;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(getWkid()).hashCode();
        }

        @Override
        public boolean equals(Object obj) {

            if (!(obj instanceof SpatialReference)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            SpatialReference other = (SpatialReference) obj;
            return new EqualsBuilder().append(getWkid(), other.getWkid()).isEquals();
        }

    }

    static class ImageDisplay {

        private int width;
        private int height;
        private int resolution;

        public ImageDisplay(int width, int height, int resolution) {
            this.width = width;
            this.height = height;
            this.resolution = resolution;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getResolution() {
            return resolution;
        }

        @Override
        public String toString() {
            return String.format("%s,%s,%s", getWidth(), getHeight(), getResolution());
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(getWidth()).append(getHeight()).append(getResolution()).hashCode();
        }

        @Override
        public boolean equals(Object obj) {

            if (!(obj instanceof ImageDisplay)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            ImageDisplay other = (ImageDisplay) obj;
            return new EqualsBuilder().append(getHeight(), other.getHeight()).append(getWidth(), other.getWidth())
                    .append(getResolution(), other.getResolution()).isEquals();
        }

    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder().append(getGeometry()).append(getGeometryType()).append(getMapExtent())
                .append(getImageDisplay()).append(getSr()).append(isReturnGeometry()).append(getTolerance()).append(getLayers())
                .append(getFormat()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof GeoportalRequestObject)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        GeoportalRequestObject other = (GeoportalRequestObject) obj;

        return new EqualsBuilder().append(getGeometry(), other.getGeometry()).append(getGeometryType(), other.getGeometryType())
                .append(getMapExtent(), other.getMapExtent()).append(getImageDisplay(), other.getImageDisplay())
                .append(getSr(), other.getSr()).append(isReturnGeometry(), other.isReturnGeometry())
                .append(getTolerance(), other.getTolerance()).append(getLayers(), other.getLayers())
                .append(getFormat(), other.getFormat()).isEquals();
    }
}

package itti.com.pl.arena.cm.server.geoportal.gov.pl.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class GeoportalRequestImageObject extends GeoportalRequestObject {

    public enum ImageFormat {
        Png24("png24"), Png8("PNG8"), Jpg("JPEG"),

        ;

        private String imageFormat = null;

        private ImageFormat(String format) {
            this.imageFormat = format;
        }

        public String getImageFormat() {
            return imageFormat;
        }

        public static ImageFormat getDefaultImageFormat() {
            return Png8;
        }
    }

    private static final String DEFAULT_FORMAT = "image";
    private static final boolean TRANSPARENT = true;

    private ImageFormat imageFormat = null;
    private double[] bbox = null;

    public GeoportalRequestImageObject(double longitude, double latitude) {
        this(longitude, latitude, Wkid.getDefault());
    }

    public GeoportalRequestImageObject(double longitude, double latitude, Wkid wkid) {
        super(longitude, latitude, wkid);
        setFormat(DEFAULT_FORMAT);
        setImageFormat(ImageFormat.getDefaultImageFormat());
        initBbox();
    }

    private void initBbox() {
        bbox = new double[4];
        // bbox=17.974734282593246%2C53.12344164937794%2C17.97981294467757%2C53.12567982988655
        bbox[0] = getLongitude() - (getWkidObj().getXDeltaValue());
        bbox[1] = getLatitude() - (getWkidObj().getYDeltaValue());
        bbox[2] = getLongitude() + (getWkidObj().getXDeltaValue());
        bbox[3] = getLatitude() + (getWkidObj().getYDeltaValue());
    }

    public double[] getBbox() {
        return (double[]) bbox.clone();
    }

    public String getBboxString() {
        return String.format("%f,%f,%f,%f", bbox[0], bbox[1], bbox[2], bbox[3]);
    }

    public int getDpi() {
        return getImageDisplay().getResolution();
    }

    public int[] getSize() {
        return new int[] { getImageDisplay().getWidth(), getImageDisplay().getHeight() };
    }

    public String getSizeString() {
        return String.format("%d,%d", getImageDisplay().getWidth(), getImageDisplay().getHeight());
    }

    public boolean isTransparent() {
        return TRANSPARENT;
    }

    public void setImageFormat(ImageFormat imageFormat) {
        this.imageFormat = imageFormat;
    }

    public String getImageFormat() {
        return imageFormat.getImageFormat();
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder().append(getGeometry()).append(getGeometryType()).append(getMapExtent())
                .append(getImageDisplay()).append(getSr()).append(isReturnGeometry()).append(getTolerance()).append(getLayers())
                .append(getFormat()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof GeoportalRequestImageObject)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        GeoportalRequestImageObject other = (GeoportalRequestImageObject) obj;

        return new EqualsBuilder().append(getGeometry(), other.getGeometry()).append(getGeometryType(), other.getGeometryType())
                .append(getMapExtent(), other.getMapExtent()).append(getImageDisplay(), other.getImageDisplay())
                .append(getSr(), other.getSr()).append(isReturnGeometry(), other.isReturnGeometry())
                .append(getTolerance(), other.getTolerance()).append(getLayers(), other.getLayers())
                .append(getFormat(), other.getFormat()).isEquals();
    }
}

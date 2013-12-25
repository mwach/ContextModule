package itti.com.pl.arena.cm.geoportal.govpl;

/**
 * List of all available Geoportal services
 * @author mawa
 *
 */
public enum GeoportalService {

	/**
	 * DATA SERVICES
	 */

	/**
	 * Cadastral data (lot information, id in the national registry, area, shape) 
	 */
	CATASTRAL_DATA_SERVICE("http://mapy.geoportal.gov.pl/wss/service/puburl/guest/G2_GO_PUB/MapServer/identify?"),
	/**
	 * National registry of boundaries (country, province, town)
	 */
	BOUNDARIES_REGISTRY_DATA_SERVICE("http://mapy.geoportal.gov.pl/wss/service/puburl/guest/G2_PRG_PUB/MapServer/identify?"),
	/**
	 * Information about address (town, street, home number), building type, function, no of floors
	 */
	TOPOGRAPHIC_DATA_SERVICE("http://mapy.geoportal.gov.pl/wss/service/puburl/guest/kompozycjaG2_TBD/MapServer/identify?"),
	/**
	 * As for TOPOGRAPHIC, less informative
	 */
	TOPOGRAPHIC_VMAPL2_DATA_SERVICE("http://mapy.geoportal.gov.pl/wss/service/puburl/guest/kompozycjaG2_VMAPL2/MapServer/identify?"),

	//TODO: do we want to use this one: one more service returning detailed XML data about selected area
	//http://sdi.geoportal.gov.pl/wfs_dzkat/wfservice.aspx?service=wfs&request=getfeature&bbox=53.13,15.36,53.137,15.369&version=1.1.0&typename=Dzialki&srsname=EPSG:4326	;

	/**
	 * IMAGE SERVICES
	 */

	/**
	 * Topographic images (TBD layer)
	 */
	TOPOGRAPHIC_IMAGE_SERVICE("http://mapy.geoportal.gov.pl/wss/service/puburl/guest/kompozycjaG2_TBD/MapServer/export?"),
//	http://mapy.geoportal.gov.pl/wss/service/puburl/guest/kompozycjaG2_TBD/MapServer/export?dpi=96&
	//transparent=true&format=png24&bbox=17.974734282593246%2C53.12344164937794%2C17.97981294467757%2C53.12567982988655&
	//bboxSR=4326&imageSR=4326&size=1366%2C602&f=image

	/**
	 * Topographic images (scans)
	 */
	TOPO_IMAGE_SERVICE("http://mapy.geoportal.gov.pl/wss/service/imgurl/guest/TOPO/MapServer/export?"),
//	http://mapy.geoportal.gov.pl/wss/service/imgurl/guest/TOPO/MapServer/export?dpi=96&transparent=true&format=png24&bbox=17.975604273023503%2C53.124794967825004%2C17.980682935107826%2C53.12703314833362&bboxSR=4326&imageSR=4326&size=1366%2C602&f=image

	/**
	 * Orto images
	 */
	ORTO_IMAGE_SERVICE("http://mapy.geoportal.gov.pl/wss/service/imgurl/guest/ORTO/MapServer/export?"),
//	http://mapy.geoportal.gov.pl/wss/service/imgurl/guest/ORTO/MapServer/export?dpi=96&transparent=true&format=JPEG&bbox=17.974734282593246%2C53.12344164937794%2C17.97981294467757%2C53.12567982988655&bboxSR=4326&imageSR=4326&size=1366%2C602&f=image

	CADASTRAL_IMAGE_SERVICE("http://mapy.geoportal.gov.pl/wss/service/puburl/guest/G2_GO_PUB/MapServer/export?"),
//	http://mapy.geoportal.gov.pl/wss/service/puburl/guest/G2_GO_PUB/MapServer/export?dpi=96&transparent=true&format=PNG8
	//&layers=show%3A0%2C1%2C2&bbox=17.974734282593246%2C53.12344164937794%2C17.97981294467757%2C53.12567982988655
	//&bboxSR=4326&imageSR=4326&size=1366%2C602&f=image

	;


	//URL of the service
	private String serviceUrl = null;

	/**
	 * Constructor
	 * @param serviceURL URL to the service
	 */
	private GeoportalService(String serviceURL){
		this.serviceUrl = serviceURL;
	}
	
	/**
	 * Returns URL of given Geoportal service
	 * @return
	 */
	public String getServiceURL(){
		return serviceUrl;
	}
}

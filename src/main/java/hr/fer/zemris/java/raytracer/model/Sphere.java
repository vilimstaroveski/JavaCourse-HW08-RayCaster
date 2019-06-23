package hr.fer.zemris.java.raytracer.model;

/**
 * Class representing a sphere in 3D space. Sphere is defined with
 * center point and a radius. Sphere is a geometrical object that 
 * contains all points in space that are positioned on a distance
 * defined by radius, from center point.
 * 
 * @author Vilim Staroveški
 *
 */
public class Sphere extends GraphicalObject {

	/**
	 * Centre point of sphere.
	 */
	private Point3D center;

	/**
	 * Distance from centre to the points of this sphere.
	 */
	private double radius;

	/**
	 * Coefficient for diffuse component for red colour; used in
	 * lightning model to calculate point colour. Legal values are [0.0,1.0].
	 * 
	 */
	private double kdr;

	/**
	 * Coefficient for diffuse component for green colour; used in
	 * lightning model to calculate point colour. Legal values are [0.0,1.0].
	 */
	private double kdg;

	/**
	 * Coefficient for diffuse component for blue colour; used in
	 * lightning model to calculate point colour. Legal values are [0.0,1.0].
	 */
	private double kdb;

	/**
	 * Coefficient for reflective component for red colour; used in
	 * lightning model to calculate point colour. Legal values are [0.0,1.0].
	 */
	private double krr;

	/**
	 * Coefficient for reflective component for green colour; used in
	 * lightning model to calculate point colour. Legal values are [0.0,1.0].
	 */
	private double krg;

	/**
	 * Coefficient for reflective component for blue colour; used in
	 * lightning model to calculate point colour. Legal values are [0.0,1.0].
	 */
	private double krb;

	/**
	 * Coefficient for reflective component; used in
	 * lightning model to calculate point colour.
	 */
	private double krn;

	/**
	 * Creates new {@link Sphere}.
	 * @param center centre point of sphere.
	 * @param radius radius of sphere.
	 * @param kdr coefficient for diffuse component for red colour
	 * @param kdg coefficient for diffuse component for green colour
	 * @param kdb coefficient for diffuse component for blue colour
	 * @param krr coefficient for reflective component for red colour
	 * @param krg coefficient for reflective component for green colour
	 * @param krb coefficient for reflective component for blue colour
	 * @param krn coefficient for reflective component for calculating colour
	 */ 
	public Sphere(Point3D center, double radius, double kdr, double kdg,
			double kdb, double krr, double krg, double krb, double krn) {
		super();
		this.center = center;
		this.radius = radius;
		this.kdr = kdr;
		this.kdg = kdg;
		this.kdb = kdb;
		this.krr = krr;
		this.krg = krg;
		this.krb = krb;
		this.krn = krn;
	}

	/**
	 * Returns centre point of this sphere.
	 * @return centre point of this sphere.
	 */
	public Point3D getCenter() {
		return center;
	}

	/**
	 * Returns radius of this sphere.
	 * @return radius of this sphere.
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * Returns coefficient for diffuse component for red colour
	 * @return coefficient for diffuse component for red colour
	 */
	public double getKdr() {
		return kdr;
	}

	/**
	 * Returns coefficient for diffuse component for green colour
	 * @return coefficient for diffuse component for green colour
	 */
	public double getKdg() {
		return kdg;
	}

	/**
	 * Returns coefficient for diffuse component for blue colour
	 * @return coefficient for diffuse component for blue colour
	 */
	public double getKdb() {
		return kdb;
	}

	/**
	 * Returns coefficient for reflective component for red colour
	 * @return coefficient for reflective component for red colour
	 */
	public double getKrr() {
		return krr;
	}

	/**
	 * Returns coefficient for reflective component for green colour
	 * @return coefficient for reflective component for green colour
	 */
	public double getKrg() {
		return krg;
	}

	/**
	 * Returns coefficient for reflective component for blue colour
	 * @return coefficient for reflective component for blue colour
	 */
	public double getKrb() {
		return krb;
	}

	/**
	 * Returns coefficient for reflective components for calculating colour
	 * @return coefficient for reflective components for calculating colour
	 */
	public double getKrn() {
		return krn;
	}
	
	@Override
	public RayIntersection findClosestRayIntersection(Ray ray) {
		
		Point3D vectorStartCenter = new Point3D(center.x - ray.start.x, center.y
                - ray.start.y, center.z - ray.start.z);
		double koefB = vectorStartCenter.scalarProduct(ray.direction.normalize());
		if (koefB <= 0) {
			//if ray goes in oposite direction of the sphere, so no intersections
            return null;
        }
		
		double koefC = vectorStartCenter.scalarProduct(vectorStartCenter) - radius * radius;

		double discriminant = koefB * koefB - koefC;

		if (discriminant <= 0) {
			// there is no intersection
			return null;
		}
		// there is an intersection
		double koefD = koefB - Math.sqrt(discriminant);
		Point3D intersection = new Point3D(ray.start.x + koefD * ray.direction.x, 
											ray.start.y + koefD * ray.direction.y, 
											ray.start.z + koefD * ray.direction.z);
		
		Point3D normal = new Point3D((intersection.x - center.x)/2, (intersection.y - center.y)/2, (intersection.z - center.z)/2).normalize();
		
		return new SphereRayIntersection(intersection, ray.start.sub(intersection).norm(), true, this, normal);
	}
	
}

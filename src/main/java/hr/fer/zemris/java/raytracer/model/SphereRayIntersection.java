package hr.fer.zemris.java.raytracer.model;

/**
 * Class represents a point in which {@link Ray} and {@link Sphere} are intersecting.
 * This point is outer intersection.
 * 
 * @author Vilim Staroveški
 *
 */
public class SphereRayIntersection extends RayIntersection {

	/**
	 * Sphere that is intersected by ray.
	 */
	private Sphere sphere;
	
	/**
	 * Normal from this intersection point.
	 */
	private Point3D normal;
	
	/**
	 * Creates new {@link SphereRayIntersection}. 
	 * @param point point in which {@link Ray} and {@link Sphere} are intersecting.
	 * @param distance distance from begin of {@link Ray} to intersection point.
	 * @param outer if intersection point of {@link Ray} and {@link Sphere} it should be true. False otherwise.
	 * @param sphere {@link Sphere} containing this intersection.
	 * @param normal normal from this intersection.
	 */
	protected SphereRayIntersection(Point3D point, double distance,
			boolean outer, Sphere sphere, Point3D normal) {
		
		super(point, distance, outer);
		this.sphere = sphere;
		this.normal = normal;
	}

	@Override
	public Point3D getNormal() {
		return normal;
	}

	@Override
	public double getKdr() {
		return sphere.getKdr();
	}

	@Override
	public double getKdg() {
		return sphere.getKdg();
	}

	@Override
	public double getKdb() {
		return sphere.getKdb();
	}

	@Override
	public double getKrr() {
		return sphere.getKrr();
	}

	@Override
	public double getKrg() {
		return sphere.getKrg();
	}

	@Override
	public double getKrb() {
		return sphere.getKrb();
	}

	@Override
	public double getKrn() {
		return sphere.getKrn();
	}

}

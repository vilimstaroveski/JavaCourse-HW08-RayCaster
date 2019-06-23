package hr.fer.zemris.java.raytracer.model;

import static org.junit.Assert.*;
import hr.fer.zemris.java.raytracer.model.Point3D;
import hr.fer.zemris.java.raytracer.model.Ray;
import hr.fer.zemris.java.raytracer.model.Sphere;
import hr.fer.zemris.java.raytracer.model.SphereRayIntersection;

import org.junit.Test;

public class RayIntersectionTest {

	@Test
	public void testIntersection() {
		
		Sphere sphere = new Sphere(new Point3D(3, 0, 0), 1, 1, 1, 1, 1, 1, 1, 1);
		
		Ray ray = new Ray(new Point3D(0, -2, 0), new Point3D(1, 1, 0).normalize());
		
		SphereRayIntersection intersec = (SphereRayIntersection) sphere.findClosestRayIntersection(ray);
		
		assertEquals(2*Math.sqrt(2), intersec.getDistance(), 0.00001);
		assertEquals(2, intersec.getPoint().x, 0.0000001);
		assertEquals(0, intersec.getPoint().y, 0.0000001);
		assertEquals(0, intersec.getPoint().z, 0.0000001);
		
		assertEquals(-1, intersec.getNormal().x, 0.000001);
		assertEquals(0, intersec.getNormal().y, 0.000001);
		assertEquals(0, intersec.getNormal().z, 0.000001);
	}
	
	@Test
	public void testIntersection2() {
		
		Sphere sphere = new Sphere(new Point3D(-3, 2, 0), 1, 1, 1, 1, 1, 1, 1, 1);
		
		Ray ray = new Ray(new Point3D(0, 0, 0), new Point3D(-2, 2, 0).normalize());
		
		SphereRayIntersection intersec = (SphereRayIntersection) sphere.findClosestRayIntersection(ray);
		
		assertEquals(2*Math.sqrt(2), intersec.getDistance(), 0.0001);
		assertEquals(-2, intersec.getPoint().x, 0.0000001);
		assertEquals(2, intersec.getPoint().y, 0.0000001);
		assertEquals(0, intersec.getPoint().z, 0.0000001);
		
	}
	
	@Test
	public void testIntersection3() {
		
		Sphere sphere = new Sphere(new Point3D(-3, 2, 0), 1, 1, 1, 1, 1, 1, 1, 1);
		
		Ray ray = new Ray(new Point3D(0, 0, 0), new Point3D(2, 2, 0).normalize());
		
		SphereRayIntersection intersec = (SphereRayIntersection) sphere.findClosestRayIntersection(ray);
		
		assertNull(intersec);
	}
	
	@Test
	public void testIntersection4() {
		
		Sphere sphere = new Sphere(new Point3D(-3, 2, 0), 1, 1, 1, 1, 1, 1, 1, 1);
		
		Ray ray = new Ray(new Point3D(0, 0, 0), new Point3D(2, -2, 0).normalize());
		
		SphereRayIntersection intersec = (SphereRayIntersection) sphere.findClosestRayIntersection(ray);
		
		assertNull(intersec);
	}
}

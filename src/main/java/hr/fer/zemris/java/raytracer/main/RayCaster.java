package hr.fer.zemris.java.raytracer.main;


import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import hr.fer.zemris.java.raytracer.model.GraphicalObject;
import hr.fer.zemris.java.raytracer.model.IRayTracerProducer;
import hr.fer.zemris.java.raytracer.model.LightSource;
import hr.fer.zemris.java.raytracer.model.Point3D;
import hr.fer.zemris.java.raytracer.model.Ray;
import hr.fer.zemris.java.raytracer.model.RayIntersection;
import hr.fer.zemris.java.raytracer.model.Scene;
import hr.fer.zemris.java.raytracer.model.IRayTracerResultObserver;
import hr.fer.zemris.java.raytracer.viewer.RayTracerViewer;

/**
 * Program that shows 3D objects on screen.
 * 
 * @author Vilim Staroveški
 *
 */
public class RayCaster {

	/**
	 * Method called on program start.
	 * @param args command line arguments.
	 */
	public static void main(String[] args) {
		RayTracerViewer.show(getIRayTracerProducer(), new Point3D(10, 0, 0),
				new Point3D(0, 0, 0), new Point3D(0, 0, 10), 20, 20);
	}

	/**
	 * Recursive job that calculates pixels colour on screen on defined space.
	 * @author Vilim Staroveški
	 *
	 */
	public static class Job extends RecursiveAction {

		
		/**
		 * Generated serial version UID
		 */
		private static final long serialVersionUID = 8108170571344610100L;
		
		/**
		 * Vector representing direction to users eyes.
		 */
		private Point3D eye;
		
		/**
		 * Position that is observed.
		 */
		private Point3D view;
		
		/**
		 * Vector showing the way up.
		 */
		private Point3D viewUp;
		
		/**
		 *  Horizontal width of observed space
		 */
		private double horizontal;
		
		/**
		 * Vertical height of observed space
		 */
		private double vertical;
		
		/**
		 * Number of pixels in one screen row.
		 */
		private int width;
		
		/**
		 * Number of pixels in one screen column.
		 */
		private int height;
		
		/**
		 * Array of intensity of red colour for each pixel.
		 */
		private short[] red;
		
		/**
		 * Array of intensity of green colour for each pixel.
		 */
		private short[] green;
		
		/**
		 * Array of intensity of blue colour for each pixel.
		 */
		private short[] blue;
		
		/**
		 * Index of screen row from which this class is calculating pixels colours.
		 */
		private int yMin;
		
		/**
		 * Index of screen row till which this class is calculating pixels colours.
		 */
		private int yMax;

		/**
		 * Limit from which pixels colours calculation starts to calculate directly.
		 */
		private final static int treshold = 16;

		/**
		 * Creates new Job with index limits from where to where to start calculating.
		 * 
		 * @param eye vector representing direction to users eyes.
		 * @param view position that is observed.
		 * @param viewUp vector showing the way up.
		 * @param horizontal horizontal width of observed space
		 * @param vertical vertical height of observed space
		 * @param width number of pixels in one screen row.
		 * @param height number of pixels in one screen column.
		 * @param red array of intensity of red colour for each pixel.
		 * @param green array of intensity of green colour for each pixel.
		 * @param blue array of intensity of blue colour for each pixel.
		 * @param yMin index of screen row from which this class is calculating pixels colours.
		 * @param yMax index of screen row till which this class is calculating pixels colours.
		 */
		public Job(Point3D eye, Point3D view, Point3D viewUp,
				double horizontal, double vertical, int width, int height,
				short[] red, short[] green, short[] blue, int yMin, int yMax) {
			super();
			this.eye = eye;
			this.view = view;
			this.viewUp = viewUp;
			this.horizontal = horizontal;
			this.vertical = vertical;
			this.width = width;
			this.height = height;
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.yMin = yMin;
			this.yMax = yMax;
		}

		@Override
		protected void compute() {
			if (yMax - yMin + 1 < treshold) {
				computeDirect();
				return;
			}

			invokeAll(
					new Job(eye, view, viewUp, horizontal, vertical,
							width, height, red, green, blue, 
							yMin, yMin + (yMax - yMin)/ 2), 
							
					new Job(eye, view, viewUp,
							horizontal, vertical, width, height, red, green, blue, 
							yMin + (yMax - yMin) / 2, yMax));
		}

		/**
		 * Calculates colours for pixels in rows from defined limit ymin to 
		 * to defined limit ymax.
		 */
		private void computeDirect() {

			int offset = yMin * width;

			Point3D vectorOG = (view.sub(eye)).normalize();
			
			Point3D yAxis = viewUp.sub(
					vectorOG.scalarMultiply(vectorOG.scalarProduct(viewUp))).normalize();
			Point3D xAxis = vectorOG.vectorProduct(yAxis).normalize();
			
			Point3D screenCorner = view.sub(xAxis.scalarMultiply(horizontal/2)).add(yAxis.scalarMultiply(vertical/2));

			Scene scene = RayTracerViewer.createPredefinedScene();

			short[] rgb = new short[3];

			for (int y = yMin; y < yMax; y++) {
				for (int x = 0; x < width; x++) {
					
					Point3D screenPoint = screenCorner
												.add(xAxis.scalarMultiply(x * horizontal / (width - 1)))
												.sub(yAxis.scalarMultiply(y * vertical / (height - 1)));
					Ray ray = Ray.fromPoints(eye, screenPoint);
					tracer(scene, ray, rgb);
					red[offset] = rgb[0] > 255 ? 255 : rgb[0];
					green[offset] = rgb[1] > 255 ? 255 : rgb[1];
					blue[offset] = rgb[2] > 255 ? 255 : rgb[2];
					offset++;
				}
			}

		}

		/**
		 * Calculates colour of pixel.
		 * 
		 * @param scene 3D scene which is observed.
		 * @param ray ray from eye to screen point.
		 * @param rgb colour of pixel represented with components of red, green and blue colours.
		 */
		private void tracer(Scene scene, Ray ray, short[] rgb) {
			rgb[0] = 0;
			rgb[1] = 0;
			rgb[2] = 0;
			RayIntersection closest = null;
			for (GraphicalObject obj : scene.getObjects()) {
				RayIntersection intersection = obj.findClosestRayIntersection(ray);
				if (intersection == null)
					continue;

				if (closest == null || intersection.getDistance() < closest.getDistance())
					closest = intersection;
			}

			if (closest == null)
				return;
			
			determineColorFor(scene, closest, rgb);
		}
		
		/**
		 * Calculates a colour for pixel representing one small piece of object in observed scene.
		 * @param scene scene in which object is present.
		 * @param intersectionWithAnObject intersection of ray from eye to scene with some object in scene.
		 * @param rgb components of red, green and blue colour for pixel.
		 */
		private void determineColorFor(Scene scene, RayIntersection intersectionWithAnObject,
				short[] rgb) {
			rgb[0] = 15;
			rgb[1] = 15;
			rgb[2] = 15;

			for (LightSource source : scene.getLights()) {
				Ray rcrta = Ray.fromPoints(source.getPoint(), intersectionWithAnObject.getPoint());

				RayIntersection closest = null;
				for (GraphicalObject obj : scene.getObjects()) {
					RayIntersection intersection = obj
							.findClosestRayIntersection(rcrta);
					if (intersection == null)
						continue;

					if (closest == null
							|| intersection.getDistance() < closest
									.getDistance())
						closest = intersection;
				}

				if (closest != null
					&& closest.getDistance() < (source.getPoint().sub(intersectionWithAnObject.getPoint())).norm()
					&& Math.abs(closest.getDistance() 
							- (source.getPoint().sub(intersectionWithAnObject.getPoint())).norm()) > 1e-12) {
					
					continue;
					
				} else {
					Point3D n = intersectionWithAnObject.getNormal();
					Point3D l = source.getPoint().sub(intersectionWithAnObject.getPoint()).normalize();

					Point3D r = n.scalarMultiply(n.scalarProduct(l))
							.scalarMultiply(2)
							.sub(l).normalize();
					
//					Point3D r = n.scalarMultiply(Math.sqrt(2)).sub(l).normalize();

					Point3D v = eye.sub(intersectionWithAnObject.getPoint()).normalize();
					double ln = Math.max(l.scalarProduct(n), 0);
					
					rgb[0] += source.getR() * intersectionWithAnObject.getKdr() * ln;
					rgb[1] += source.getG() * intersectionWithAnObject.getKdg() * ln;
					rgb[2] += source.getB() * intersectionWithAnObject.getKdb() * ln;
					
					double rv = Math.max(r.scalarProduct(v), 0);
					
					rgb[0] += source.getR() * intersectionWithAnObject.getKrr()
								* Math.pow(rv, intersectionWithAnObject.getKrn());
					rgb[1] += source.getG() * intersectionWithAnObject.getKrg()
								* Math.pow(rv, intersectionWithAnObject.getKrn());
					rgb[2] += source.getB() * intersectionWithAnObject.getKrb()
								* Math.pow(rv, intersectionWithAnObject.getKrn());
					
				}
			}
		}
	}

	/**
	 * Returns new {@link IRayTracerProducer}.
	 * @return new {@link IRayTracerProducer}.
	 */
	private static IRayTracerProducer getIRayTracerProducer() {
		return new IRayTracerProducer() {

			@Override
			public void produce(Point3D eye, Point3D view, Point3D viewUp,
					double horizontal, double vertical, int width, int height,
					long requestNo, IRayTracerResultObserver observer) {
				System.out.println("Započinjem izračune...");
				short[] red = new short[width * height];
				short[] green = new short[width * height];
				short[] blue = new short[width * height];

				ForkJoinPool pool = new ForkJoinPool();

				pool.invoke(new Job(eye, view, viewUp, horizontal,
						vertical, width, height, red, green, blue, 0, height));

				pool.shutdown();

				System.out.println("Izračuni gotovi...");
				observer.acceptResult(red, green, blue, requestNo);
				System.out.println("Dojava gotova...");
			}
		};
	}
}
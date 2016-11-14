import java.applet.Applet;
import java.awt.event.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.io.*;
import java.awt.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.behaviors.keyboard.*;
import javax.swing.JApplet;
import com.sun.j3d.utils.image.TextureLoader;
import java.net.*;

public class SolarSystem extends JApplet {

	// create the bounds of the universe
	BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0);
	BoundingLeaf boundingLeaf = new BoundingLeaf(bounds);
	PlatformGeometry platformGeom = new PlatformGeometry();

	// creating the (single) branch group
	BranchGroup main = new BranchGroup();

	int primflags = Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS;

	public SolarSystem() {
		// create a content pane
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		Canvas3D c = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		cp.add("Center", c);

		BranchGroup universe = createSceneGraph();
		SimpleUniverse u = new SimpleUniverse(c);

		u.getViewingPlatform().setNominalViewingTransform();
		u.addBranchGraph(universe);
		//able to view whole universe
		u.getViewer().getView().setBackClipDistance(500);

		// *** create a viewing platform
		TransformGroup cameraTG = u.getViewingPlatform().getViewPlatformTransform();
		// starting postion of the viewing platform
		Vector3f translate = new Vector3f();
		Transform3D T3D = new Transform3D();
		// move along z axis by 10.0f ("move away from the screen")
		translate.set(0.0f, 0.0f, 35.0f);
		T3D.setTranslation(translate);
		cameraTG.setTransform(T3D);

	}

	public BranchGroup createSceneGraph() {
	
		
		// creating the transform group for the (one) branchgroup
		//also the main transform group
		TransformGroup solarSystem = new TransformGroup();
		
		
		solarSystem.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		PointLight light = new PointLight();
		light.setColor(new Color3f(Color.WHITE));
		light.setPosition(0.0f, 0.0f, 0.0f);
		
		Color3f amb = new Color3f(0.5f,0.5f,0.5f);
		AmbientLight a = new AmbientLight(amb);
		a.setInfluencingBounds(bounds);
		
		
		//create texture for bg
		TextureLoader tl = new TextureLoader("assets/space2.jpg", this);
		tl.getTexture();
		Texture t = tl.getTexture();
		 
		Appearance bg1 = new Appearance();
		bg1.setTexture(t);
		
	
		//create inverted sphere for bg
		Sphere bg = new Sphere(100f, Primitive.GENERATE_TEXTURE_COORDS | Primitive.GENERATE_NORMALS_INWARD,1000,bg1);
		bg.setCollidable(false);
		
		
		// control intensity
		// light.setAttenuation(0.0f,0.0f,0.0f);
		light.setInfluencingBounds(bounds);
		//adding all the elements to the main transform group
		solarSystem.addChild(light);
		solarSystem.addChild(bg);
		solarSystem.addChild(a);
		
		//adding all the planets to the main transform group
		solarSystem.addChild(Planet(2f, 0f, 0, 2500000, "assets/sunt1.png"));
		solarSystem.addChild(Planet(0.038f,11f,8790,587000,"assets/mercury.jpg"));
		solarSystem.addChild(Planet(0.095f,12.75f,2430,243000,"assets/venus.jpg"));
		solarSystem.addChild(Planet(0.1f,13.5f,3650,1000,"assets/earth.jpg"));
		solarSystem.addChild(Planet(0.053f,14.6f,68698,2010,"assets/mars.jpg"));
		solarSystem.addChild(Planet(1.12f,23.0f,401500,260,"assets/jupiter.jpg"));
		solarSystem.addChild(Planet(0.945f,31.5f,105850,350,"assets/saturn.jpg"));
		solarSystem.addChild(Planet(0.4f,34.5f,30660,700,"assets/uranus.png"));
		solarSystem.addChild(Planet(0.8f,78.75f,598600,850,"assets/neptune.jpg"));
		solarSystem.addChild(CustomShape());

		
		
		//rocket body
		Cylinder cylinder = new Cylinder(0.4f, 1.4f);
		Transform3D rB = new Transform3D();
		rB.setTranslation(new Vector3f(-11.0f,-1.2f,0.0f));
		TransformGroup rocketBody = new TransformGroup(rB);
		rocketBody.addChild(cylinder);
		
		//rocket head
		Cone cone = new Cone(0.4f,1.0f);
		Transform3D cR = new Transform3D();
		cR.setTranslation(new Vector3f(-11.0f,0.0f,0.0f));
		TransformGroup rocketHead = new TransformGroup(cR);
		rocketHead.addChild(cone);
		
		//positioning of rocket
		TransformGroup circle = new TransformGroup();
		circle.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D yAxis1 = new Transform3D();
		yAxis1.rotX(Math.PI/2);
		
		
		//rocket movement
		Alpha alpha = new Alpha(1,10000);
		RotationInterpolator rot = new RotationInterpolator(alpha, circle, yAxis1, 0.0f,(float) Math.PI*(-2.0f));
		rot.setSchedulingBounds(bounds);
		
		/*COLLISION*/
		
		cone.setCollisionBounds(new BoundingBox(new Point3d(0.0, 0.0, 0.0), new Point3d(0.0, 0.0, 0.0)));
		cone.setCollidable(true);
		
		Alpha[] cAlpha = new Alpha[1];
		cAlpha[0] = alpha;
		CollisionBehaviour2 cb = new CollisionBehaviour2(cone, cAlpha, bounds);
		solarSystem.addChild(cb);
		
		/*END COLLISON*/
		
		solarSystem.addChild(rot);
		solarSystem.addChild(circle);
		circle.addChild(rocketHead);
		circle.addChild(rocketBody);

		
		MouseRotate behavior = new MouseRotate();
		behavior.setTransformGroup(solarSystem);
		main.addChild(behavior);
		behavior.setSchedulingBounds(bounds);

		// Create the zoom behavior node
		MouseZoom behavior2 = new MouseZoom();
		behavior2.setTransformGroup(solarSystem);
		main.addChild(behavior2);
		behavior2.setSchedulingBounds(bounds);

		// Create the translate behavior node
		MouseTranslate behavior3 = new MouseTranslate();
		behavior3.setTransformGroup(solarSystem);
		main.addChild(behavior3);
		behavior3.setSchedulingBounds(bounds);

		platformGeom.addChild(boundingLeaf);

		main.addChild(solarSystem);

		main.compile();
		return main;
	}
	
	
	
	public TransformGroup Planet(float radius, float distance, long orbit, int rotate, String texture) {
		// tGroup0 rotates the planet around the sun
		//main TG for method
		TransformGroup tGroup0 = new TransformGroup();
		tGroup0.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		// set orbit speed around the sun
		// -1 allows unlimited orbits
		Alpha rotationAlpha0 = new Alpha(-1, orbit);

		// sets up rotation of the planet around the sun
		Transform3D yAxis = new Transform3D();
		// rotates the relative y axis by 90 degrees
		
		yAxis.rotX((Math.PI/2));
		//sets up to orbit a full circle around 0,0
		RotationInterpolator rotator0 = new RotationInterpolator(rotationAlpha0, tGroup0, yAxis, 0.0f,
				(float) Math.PI * (2));
		rotator0.setSchedulingBounds(bounds);

		// create new transform group
		Transform3D t = new Transform3D();
		t.setScale(new Vector3d(2.0, 2.0, 2.0));
		t.setTranslation(new Vector3d(0.0,  (-distance), 0.0));
		Transform3D helperT3D = new Transform3D();
		helperT3D.rotZ(Math.PI);
		t.mul(helperT3D);
		helperT3D.rotX(Math.PI / 2);
		t.mul(helperT3D);
		//set translations to transform group
		TransformGroup tGroup1 = new TransformGroup(t);

		 
		TransformGroup tGroup2 = new TransformGroup();
		tGroup2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	
	
		// -1 means indefinite loop count
		Alpha rotationAlpha2 = new Alpha(-1, rotate);

		Transform3D yAxis2 = new Transform3D();
		//sets up to rotate about itself
		RotationInterpolator rotator2 = new RotationInterpolator(rotationAlpha2, tGroup2, yAxis2, 0.0f,
				(float) Math.PI * (2.0f));
		rotator2.setSchedulingBounds(bounds);

		// create textures for a sphere
		TextureLoader tl = new TextureLoader(texture, new Container());
		Texture atmosphere = tl.getTexture();
		atmosphere.setBoundaryModeS(Texture.WRAP);
		atmosphere.setBoundaryModeT(Texture.WRAP);

		TextureAttributes ta = new TextureAttributes();
		ta.setTextureMode(TextureAttributes.MODULATE);
		//set appearance for planets
		Appearance app = new Appearance();
		app.setTexture(atmosphere);
		app.setTextureAttributes(ta);



		Material material = new Material();
		
		//sets the material of objects over 2f in radius
		if (radius >= 2f) {
			material.setEmissiveColor(new Color3f(Color.WHITE));
		}
		//sets how much light the object reflects
		material.setShininess(5000);
		
		app.setMaterial(material);
		
		//sets each sphere radius, how many shapes to use(how smooth the object is), and appearance
		Sphere body = new Sphere(radius, primflags, 100, app);

		//add all to the main TG for the method
		tGroup0.addChild(tGroup1);
		tGroup1.addChild(tGroup2);
		tGroup0.addChild(rotator0);
		tGroup2.addChild(rotator2);
		tGroup2.addChild(body);

		//return main method TG
		return tGroup0;
	}

	public TransformGroup CustomShape(){
		TransformGroup newShape = new TransformGroup();
	
		newShape.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		Point3f[] verts = {
				//first half of base
				new Point3f(1,1,0), new Point3f(1,-1,0), new Point3f(-1,1,0),
				//second half of base
				new Point3f(-1,-1,0), new Point3f(-1,1,0), new Point3f(1
				,-1,0),
				// each of the sides of the pyramid
				new Point3f(1,1,0)  , new Point3f(-1,1,0),  new Point3f(0,0
				,2),
				new Point3f(-1,1,0 ), new Point3f(-1,-1,0), new Point3f(0
				,0,2),
				new Point3f(-1,-1,0), new Point3f(1,-1,0), new Point3f(0
				,0,2),
				new Point3f(1,-1,0), new Point3f(1,1,0), new Point3f(0,0
				,2)};
				TriangleArray tri = new TriangleArray(18, TriangleArray.COORDINATES |
				TriangleArray.NORMALS | TriangleArray.TEXTURE_COORDINATE_2);
				tri.setCoordinates(0, verts);
				
				
				Alpha rotationAlpha0 = new Alpha(-1, 58000);

				// sets up rotation of the planet around the sun
				Transform3D yAxis = new Transform3D();
				// rotates the relative y axis by 90 degrees
				
				yAxis.rotX((Math.PI/2));
				//sets up to orbit a full circle around 0,0
				RotationInterpolator rotatorShapeOrbit = new RotationInterpolator(rotationAlpha0, newShape, yAxis, 0.0f,
						(float) Math.PI * (2));
				rotatorShapeOrbit.setSchedulingBounds(bounds);
				
				Transform3D t = new Transform3D();
				t.setScale(new Vector3d(2.0, 2.0, 2.0));
				t.setTranslation(new Vector3d(0.0,  (-8.0f), 0.0));
				Transform3D helperT3D = new Transform3D();
				helperT3D.rotZ(Math.PI);
				t.mul(helperT3D);
				helperT3D.rotX(Math.PI / 2);
				t.mul(helperT3D);
				//set translations to transform group
				TransformGroup translationTG = new TransformGroup(t);
				
				TransformGroup tGroup2 = new TransformGroup();
				tGroup2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			
			
				// -1 means indefinite loop count
				Alpha rotationAlpha2 = new Alpha(-1, 250000);

				Transform3D yAxis2 = new Transform3D();
				//sets up to rotate about itself
				RotationInterpolator rotator2 = new RotationInterpolator(rotationAlpha2, tGroup2, yAxis2, 0.0f,
						(float) Math.PI * (2.0f));
				rotator2.setSchedulingBounds(bounds);
			
				
				
				Material material = new Material();
				material.setShininess(5000);
				
				
				Shape3D s= new Shape3D(tri);
				
				newShape.addChild(translationTG);
				translationTG.addChild(tGroup2);
				newShape.addChild(rotatorShapeOrbit);
				tGroup2.addChild(rotator2);
				tGroup2.addChild(s);
				
				
			
		return newShape;
	}

	public static void main(String[] args) {

		new MainFrame(new SolarSystem(), 1000, 1000);
	}

}
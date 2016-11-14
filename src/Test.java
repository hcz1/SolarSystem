/**
 * Describe class test here.
 *
 * Time-stamp: <2015-02-18 16:20:36 rlc3>
 *
 * R. L. Crole (based on template of M. Hoffmann) 
 */

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

public class Test extends JApplet {

    // createSceneGraph method (***)	
    public BranchGroup createSceneGraph(SimpleUniverse su) {
	// creating the universe
	BranchGroup objRoot = new BranchGroup();

	// adding the TG of the picture to the universe
	// call createPicture method appearing below (**)
	objRoot.addChild(createPicture());

	// adding mouse and keyboard controls
        // call addControl method appearing below (*) 
        // in the step examples, addControl code
        // is inserted directly HERE
	addControls(su,objRoot);

	objRoot.compile();
	return objRoot;

    } // end createSceneGraph
        
    // createPicture method: create the mainTG TransformGroup (**)
    public TransformGroup createPicture() {
	
	TransformGroup mainTG = new TransformGroup();           
	ColorCube c = new ColorCube();
	mainTG.addChild(c);
	return mainTG;
    }
  
    // addControls method: set up all the motion controls (*)
    public void addControls(SimpleUniverse su, BranchGroup bg) {
    	// create a viewing platform
	TransformGroup cameraTG = su.getViewingPlatform().
	                            getViewPlatformTransform();
	// starting postion of the viewing platform
	Vector3f translate = new Vector3f();
      	Transform3D T3D = new Transform3D();
	translate.set( 0.0f, 0.3f, 0.0f);
        T3D.setTranslation(translate);
	cameraTG.setTransform(T3D);

	// bounds for mouse behaviour 
	BoundingSphere bounds = new BoundingSphere(new Point3d(), 1000.0);

	// Create the key behavior node
        KeyNavigatorBehavior keyBehavior = new 
	KeyNavigatorBehavior(cameraTG);
	keyBehavior.setSchedulingBounds(bounds);
        bg.addChild(keyBehavior);
	
 	// Create the rotate behavior node
 	MouseRotate behavior = new MouseRotate(MouseBehavior.INVERT_INPUT);
 	behavior.setTransformGroup(cameraTG);
 	behavior.setSchedulingBounds(bounds);
 	bg.addChild(behavior);

	// Create the zoom behavior node
 	MouseZoom behavior2 = new MouseZoom(MouseBehavior.INVERT_INPUT);
 	behavior2.setTransformGroup(cameraTG);
 	behavior2.setSchedulingBounds(bounds);
	bg.addChild(behavior2);
 	
 	// Create the translate behavior node
 	MouseTranslate behavior3 = new MouseTranslate(MouseBehavior.INVERT_INPUT);
 	behavior3.setTransformGroup(cameraTG);
 	behavior3.setSchedulingBounds(bounds);
 	bg.addChild(behavior3);	

    } // end addControls

    public void init(){
    }

    // create a "standard" universe using SimpleUniverse 
    public Test() {
	Container cp = getContentPane();
	cp.setLayout(new BorderLayout());
	Canvas3D c = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
	cp.add("Center", c);
	SimpleUniverse u = new SimpleUniverse(c);
	// call createSceneGraph method appearing above (***)
	BranchGroup scene = createSceneGraph(u); 
	u.addBranchGraph(scene);
    }
    
    public static void main(String[] args) {        
	new MainFrame(new Test(), 512, 512);
    }
    
}

/**
 * Collision Detection Example
 *
 * Time-stamp: <2016-03-11 17:02:13 rlc3>
 *
 * Author: R. Crole (Based upon code by F. Klawonn)
 * 
 */

import java.awt.*;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.behaviors.mouse.*; 
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.picking.behaviors.*;
import com.sun.j3d.utils.behaviors.keyboard.*;
import javax.swing.JFrame;

public class Example3D extends JFrame {

          //The canvas to be drawn upon.
  	  public Canvas3D myCanvas3D;

     // create a "standard" universe using SimpleUniverse 
     public Example3D() {

        //Mechanism for closing the window and ending the program.
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	myCanvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration() );
	getContentPane().add("Center", myCanvas3D);
        //Construct the SimpleUniverse:
        //First generate it using the Canvas.
        SimpleUniverse simpUniv = new SimpleUniverse(myCanvas3D);

    	// *** create a viewing platform
	TransformGroup cameraTG = simpUniv.getViewingPlatform().
	                            getViewPlatformTransform();
	// starting postion of the viewing platform
	Vector3f translate = new Vector3f(); 
      	Transform3D T3D = new Transform3D();
	// move along z axis by 2.5f ("move away from the screen") 
	translate.set( 0.0f, 0.0f, 2.5f);
        T3D.setTranslation(translate);
	cameraTG.setTransform(T3D);

        //The scene is generated in this method.
        BranchGroup scene = createSceneGraph();
        //Add everything to the universe.
        simpUniv.addBranchGraph(scene);
        // *** Add some light to the scene: addLight is declared below
        addLight(simpUniv);
        //Show the canvas/window.
        setTitle("Move the cube with the right mouse button.");
        setSize(700,700);
        setVisible(true);

    } // end Example3D() 

    // ------------------ begin createscenegraph -- 

    public BranchGroup createSceneGraph() {

    // Creating the branchgroup 
    BranchGroup objRoot = new BranchGroup();

    // creating the transform group for the (one) branchgroup  
    TransformGroup mainTG = new TransformGroup();
    mainTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    mainTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    // ------------------ begin build the cube -- 

    /* CUBE APPEARANCE  --------------------------------- */

    // *** A blue Appearance for the cube.
    // look up Material in the API; 
    // each parameter specifies behavior involving light
    // 1 ambient color: amount of ambient light reflected 
    // 2 emissive color: light emitted 
    // 3 diffuse color: used to calculate amount of ... 
    // 4 specular color: ... diffuse and specular reflection 
    // 5 shininess value: larger value ==> shinier 
    Color3f ambientColourCube = new Color3f(0.0f,0.0f,0.8f);
    Color3f emissiveColourCube = new Color3f(0.0f,0.0f,0.0f);
    Color3f diffuseColourCube = new Color3f(0.0f,0.0f,0.7f);
    Color3f specularColourCube = new Color3f(0.0f,0.0f,0.8f);
    float shininessCube = 128.0f;
    // Generate the appearance and apply material to cube
    Appearance cubeApp = new Appearance();
    cubeApp.setMaterial(new Material(ambientColourCube,emissiveColourCube,
                           diffuseColourCube,specularColourCube,shininessCube));

    /* END CUBE APPEARANCE  --------------------------------- */

    // *** Generate the physical cube.
    float cubeHalfLength = 0.1f;
    // centred at the origin; parameters are HALF the depth, height and width
    Box cube = new Box(cubeHalfLength,cubeHalfLength,cubeHalfLength,cubeApp);

    //Position the cube and assign it to a transformation group.
    Transform3D tfCube = new Transform3D();
    tfCube.rotY(Math.PI/6);
    Transform3D rotationX = new Transform3D();
    rotationX.rotX(-Math.PI/5);
    tfCube.mul(rotationX);
    TransformGroup tgCube = new TransformGroup(tfCube);
    tgCube.addChild(cube);

    // These properties are needed to allow the cube to moved around the scene.
    // *** PICK allows the cube to be "picked up" with the mouse, and moved around.
    tgCube.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    tgCube.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    tgCube.setCapability(TransformGroup.ENABLE_PICK_REPORTING);

    // ------------------ End build the cube -- 

    // ------------------ Begin build the cylinder -- 

    //Generate a golden Appearance for the cylinder.
    Color3f ambientColourCylinder = new Color3f(0.0f,0.0f,0.0f);
    Color3f emissiveColourCylinder = new Color3f(0.0f,0.0f,0.0f);
    Color3f diffuseColourCylinder = new Color3f(0.8f,0.4f,0.0f);
    Color3f specularColourCylinder = new Color3f(0.8f,0.8f,0.0f);
    float shininessCylinder = 100.0f;
    // Generate the appearance and apply material to cylinder 
    Appearance yellowCylinderApp = new Appearance();
    yellowCylinderApp.setMaterial(new Material(ambientColourCylinder,
                                               emissiveColourCylinder,
                                               diffuseColourCylinder,
                                               specularColourCylinder,
                                               shininessCylinder));
    //Generate the cylinder: 
    // Check the API to find out what 0.1 and 0.3 specify	
    Cylinder cyli = new Cylinder(0.1f,0.3f,yellowCylinderApp);

    //Position the cylinder and assign it to a transformation group.
    Transform3D tfCylinder = new Transform3D();
    tfCylinder.mul(rotationX);
    Transform3D positionCyl = new Transform3D();
    positionCyl.setTranslation(new Vector3f(-0.7f,0.0f,0.0f));
    tfCylinder.mul(positionCyl);
    TransformGroup tgCylinder = new TransformGroup(tfCylinder);
    tgCylinder.addChild(cyli);
    
    // --- *** begin cylinder movement --

    //This transformation group is needed for the movement of the cylinder.
    TransformGroup tgmCyl = new TransformGroup();
    tgmCyl.addChild(tgCylinder);

    //The movement from left to right (along the x axis) 
    Transform3D xAxis = new Transform3D();
    // EXERCISE: try getting the cylinder to move up and down (along the z axis) 
    float maxRight = 0.5f;

    // An alpha for the left to right movement 
    // Alpha(number_of_times_for_movement,time_movement_takes) 
    Alpha cylAlphaR = new Alpha(2,2000);
    //The starting time is first postponed until "infinity".
    cylAlphaR.setStartTime(Long.MAX_VALUE);
    //The interpolator for the movement.
    // PosInt(theAlpha, TGroup_to_attach_to, axis_of_movement_default_X_Axis, start_position, end_position)
    PositionInterpolator cylMoveR = new PositionInterpolator(cylAlphaR,tgmCyl,
                                                             xAxis,0.0f,maxRight);
    // An alpha for the right to left movement 
    Alpha cylAlphaL = new Alpha(1,2000);
    //The starting time is first postponed until "infinity".
    cylAlphaL.setStartTime(Long.MAX_VALUE);
    //The interpolator for the movement.
    PositionInterpolator cylMoveL = new PositionInterpolator(cylAlphaL,tgmCyl,
                                                             xAxis,maxRight,0.0f);

    // API: The scheduling region defines a spatial volume 
    //        that serves to enable the scheduling of Behavior nodes. 							     
    BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0),Double.MAX_VALUE);
    cylMoveR.setSchedulingBounds(bounds);
    cylMoveL.setSchedulingBounds(bounds);

    //Add the movements to the transformation group.
    tgmCyl.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    tgmCyl.addChild(cylMoveR);
    tgmCyl.addChild(cylMoveL);

    // --- end cylinder movement --

    // ------------------ End build the cylinder -- 

    // ------------------ Begin build the sphere -- 

    //An Appearance for the green sphere.
    Color3f ambientColourGSphere = new Color3f(0.0f,0.8f,0.0f);
    Color3f emissiveColourGSphere = new Color3f(0.0f,0.0f,0.0f);
    Color3f diffuseColourGSphere = new Color3f(0.0f,0.8f,0.0f);
    Color3f specularColourGSphere = new Color3f(0.0f,0.8f,0.0f);
    float shininessGSphere = 1.0f;
    Appearance greenSphereApp = new Appearance();
    greenSphereApp.setMaterial(new Material(ambientColourGSphere,
                                            emissiveColourGSphere,
                                            diffuseColourGSphere,
                                            specularColourGSphere,
                                            shininessGSphere));
    //Generate the green sphere.
    float radius = 0.2f;
    Sphere greenSphere = new Sphere(radius,greenSphereApp);

    //The same for the red sphere.
    Color3f ambientColourRSphere = new Color3f(0.6f,0.0f,0.0f);
    Color3f emissiveColourRSphere = new Color3f(0.0f,0.0f,0.0f);
    Color3f diffuseColourRSphere = new Color3f(0.6f,0.0f,0.0f);
    Color3f specularColourRSphere = new Color3f(0.8f,0.0f,0.0f);
    float shininessRSphere = 20.0f;
    Appearance redSphereApp = new Appearance();
    redSphereApp.setMaterial(new Material(ambientColourRSphere,emissiveColourRSphere,
                             diffuseColourRSphere,specularColourRSphere,shininessRSphere));
    Sphere redSphere = new Sphere(radius,redSphereApp);

    //The same for the blue sphere.
    Color3f ambientColourBSphere = new Color3f(0.0f,0.0f,0.6f);
    Color3f emissiveColourBSphere = new Color3f(0.0f,0.0f,0.0f);
    Color3f diffuseColourBSphere = new Color3f(0.0f,0.0f,0.6f);
    Color3f specularColourBSphere = new Color3f(0.0f,0.0f,0.8f);
    float shininessBSphere = 20.0f;
    Appearance blueSphereApp = new Appearance();
    blueSphereApp.setMaterial(new Material(ambientColourBSphere,emissiveColourBSphere,
                             diffuseColourBSphere,specularColourBSphere,shininessBSphere));
    Sphere blueSphere = new Sphere(radius,blueSphereApp);

    // *** A Switch for the green and the red and the blue spheres
    Switch colourSwitch = new Switch();
    colourSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);

    // The Switch node controls which of its children will be rendered.
    // Add the spheres to the Switch.
    colourSwitch.addChild(redSphere); // child 0
    colourSwitch.addChild(greenSphere); // child 1
    colourSwitch.addChild(blueSphere); // child 2

    // The green sphere should be visible in the beginning.
    colourSwitch.setWhichChild(1);

    // A transformation group for the Switch (the spheres).
    Transform3D tfSphere = new Transform3D();
    tfSphere.setTranslation(new Vector3f(0.7f,0.0f,0.0f));
    TransformGroup tgSphere = new TransformGroup(tfSphere);
    tgSphere.addChild(colourSwitch);

    // ------------------ End build the sphere -- 

    // ------------------ Begin navigation -- 

    //In order to allow navigation through the scene with the keyboard,
    //everything must be collected in a separate transformation group to which 
    //the KeyNavigatorBehavior is applied.
    KeyNavigatorBehavior knb = new KeyNavigatorBehavior(mainTG);
    knb.setSchedulingBounds(bounds);
    mainTG.addChild(knb);
    objRoot.addChild(mainTG);
    // The PickTranslateBehavior for moving the blue cube.
    PickTranslateBehavior pickTrans = new PickTranslateBehavior(objRoot,myCanvas3D,bounds);
    objRoot.addChild(pickTrans); 

    // ------------------ End navigation -- 

    /* *** COLLISION DETECTION --------------------------------- */
    
    //The CollisionBounds for the cube.
    double kk = 1.0;
    cube.setCollisionBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0),kk*radius));
     // cube.setCollisionBounds(new BoundingBox(new Point3d(-kk*cubeHalfLength,
     //                                                       -kk*cubeHalfLength,
     //                                                       -kk*cubeHalfLength),
     //                                         new Point3d(kk*cubeHalfLength,
     //                                                       kk*cubeHalfLength,
     //                                                       kk*cubeHalfLength)));
     //
     // Enabled for collision purposes (the default is true)
    cube.setCollidable(true);
    
    //The CollisionBounds for the spheres.
    double k = 1.0 ; 
    colourSwitch.setCollisionBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0),k*radius)); 
    //Enabled for collision purposes
    colourSwitch.setCollidable(true);
    
    //CollisionBehaviour1 class takes care of changing the colour of the sphere when the cube touches it.
    // cylinder is involved only for wake up on initialisation 
    CollisionBehaviour1 scb1 = new CollisionBehaviour1(cyli,colourSwitch,bounds);
    mainTG.addChild(scb1);
    
    //The CollisionBounds for the cylinder.
    cyli.setCollisionBounds(new BoundingBox(new Point3d(0.0,-0.15,0.0),
                                            new Point3d(0.1,0.15,0.1)));
    cyli.setCollidable(true);
    
    //CollisionBehaviour2 class takes care of the movement(s) of the cylinder.
    Alpha[] cylAlphas= new Alpha[2];
    cylAlphas[0] = cylAlphaR;
    cylAlphas[1] = cylAlphaL;
    CollisionBehaviour2 scb2 = new CollisionBehaviour2(cyli,cylAlphas,bounds);
    mainTG.addChild(scb2);

    /* END COLLISION DETECTION --------------------------------- */

    // make remaining edge relations between the scene graph nodes
    mainTG.addChild(tgCube);
    mainTG.addChild(tgSphere);
    mainTG.addChild(tgmCyl);
    
    // final compilation
    objRoot.compile();
    return objRoot; 

    } // ------------------ end createscenegraph

    public static void main(String[] args) {        

	Example3D colliexam = new Example3D();

    }

  //Some light is added to the scene here.
  public void addLight(SimpleUniverse su)
  {

    /* LIGHTING --------------------------------- */

    BranchGroup bgLight = new BranchGroup();

        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

	// Set up the global lights
	// first the ambient light
	// light from all directions
	// typically use white or 'gray' light
	Color3f alColor = new Color3f(0.6f, 0.6f, 0.6f);
	AmbientLight aLgt = new AmbientLight(alColor);
	aLgt.setInfluencingBounds(bounds);
        bgLight.addChild(aLgt);

       // next the directional light
       // parallel light rays come FROM infinity TOWARDS the vector lightDir1 
       Color3f lightColour1 = new Color3f(1.0f,1.0f,1.0f);
       // try out x, y and z each being + or - 1, and other coords = 0 
       // with (0,1,0) the bottom of the cube should be lit
       // Vector3f lightDir1  = new Vector3f(0.0f,1.0f,0.0f);
       Vector3f lightDir1  = new Vector3f(-1.0f,0.0f,-0.5f);
       DirectionalLight light1 = new DirectionalLight(lightColour1, lightDir1);
       // light has no effect, ie Influence, outside of the bounds 
       light1.setInfluencingBounds(bounds);
       bgLight.addChild(light1);

       Vector3f lightDir2  = new Vector3f(1.0f,-1.0f,0.5f);
       DirectionalLight light2 = new DirectionalLight(lightColour1, lightDir2);
       light2.setInfluencingBounds(bounds);
       bgLight.addChild(light2);

       su.addBranchGraph(bgLight);
        
    /* END LIGHTING --------------------------------- */ 

  } // ------------------ end addLight

}


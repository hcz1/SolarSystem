import java.util.*;
import javax.media.j3d.*;
import com.sun.j3d.utils.geometry.*;

/**
 * March 2016 
 *
* This class implements the change between two states (Switches)
* when a collision occurs.
*
* @author R. Crole (based on code by F. Klawonn)
*
*/

public class CollisionBehaviour1 extends Behavior {

   // This class uses the sphere and cylinder 
   // The children of Switch are the red/green/blue spheres 
   public Switch sphereSwitch;
   public Primitive cylinder; 

   // declare initial and process stimulus (arrays of) criteria 
   public WakeupCriterion[] initialCriteria;
   public WakeupCriterion[] procStimCriteria;

   // API: public WakeupOr(WakeupCriterion[] criteria) - logical OR of the (objects in the) criteria  
   public WakeupOr initial_wakeUpCondition;
   public WakeupOr procStim_wakeUpCondition;

   /* BEGIN THE CONSTRUCTOR FOR THIS CLASS  --------------------------------- */
   // this is called within Example3D.java in the collision detection section
    public CollisionBehaviour1(Primitive theCyli, Switch theSwitch, Bounds theBounds)
    {
       cylinder = theCyli;
       sphereSwitch = theSwitch;
       setSchedulingBounds(theBounds);
    }
   /* END THE CONSTRUCTOR FOR THIS CLASS  --------------------------------- */

  /* BEGIN INITIALISE BEHAVIOR  --------------------------------- */
   public void initialize()
   {
     initialCriteria = new WakeupCriterion[2];
     initialCriteria[0] = new WakeupOnCollisionEntry(cylinder);
     initialCriteria[1] = new WakeupOnCollisionExit(cylinder);
     // API: WakeupOr(WakeupCriterion[] criteria) 
     initial_wakeUpCondition = new WakeupOr(initialCriteria);
     // API: wakeupOn(WakeUpCondition wakeupcondition)
     // this method ensures that processStimulus is called when the 
     // [initial_]wakeUpCondition is satisfied ... 
     // ... and [initial]Criteria is passed to processStimulus as an enumeration 
     wakeupOn(initial_wakeUpCondition); 

     // NOTE: process Stimulus wake up criteria may well be different to initial criteria 
     procStimCriteria = new WakeupCriterion[2];
     procStimCriteria[0] = new WakeupOnCollisionEntry(sphereSwitch);
     procStimCriteria[1] = new WakeupOnCollisionExit(sphereSwitch);

     System.out.println(); 
     if (((WakeupCondition) initial_wakeUpCondition) instanceof WakeupOr) {
     System.out.println("*initial new wakeupOn cylinder collision ENTRY OR EXIT *"); 
     } else {
     System.out.println("*initial new wakeupOn cylinder collison ENTRY AND EXIT *"); 
     }
   }
  /* END INITIALISE BEHAVIOR  --------------------------------- */

  /* BEGIN PROCESS STIMULUS BEHAVIOR  --------------------------------- */

   // API: criteria - an enumeration of triggered wakeup criteria for this behavior
   // !! Look up Behavior class in the API !! 
    
   public void processStimulus(Enumeration criteria)
   {
       //Here we define what happens when a collision occurs.
       System.out.println("process stimulus called"); 

       /* -- EDIT HERE
       // a simple process to swap green and red spheres for a WakeupOr and WakeupAnd 
       if (sphereSwitch.getWhichChild() == 0) {sphereSwitch.setWhichChild(1); System.out.println("*red --> green*"); }
       else {sphereSwitch.setWhichChild(0); System.out.println("*green --> red*"); }
       // -- */ 

       // -- EDIT HERE
       // a more complex process for a WakeupOr
       while (criteria.hasMoreElements())

       {
         WakeupCriterion theCriterion = (WakeupCriterion) criteria.nextElement();
         if (theCriterion instanceof WakeupOnCollisionEntry) 
         {
       	    if (sphereSwitch.getWhichChild() == 0) {sphereSwitch.setWhichChild(2); System.out.println("*EN red --> blue*"); } //
       	    else     {
       		if (sphereSwitch.getWhichChild() == 2){sphereSwitch.setWhichChild(0); System.out.println("*EN blue --> red*");} 
       		else {System.out.println("*EN green --> green*");}
       	    }
         } // end if entry 
         else
         {
       	    if (theCriterion instanceof WakeupOnCollisionExit) 
           {
       	      if (sphereSwitch.getWhichChild() == 0) {sphereSwitch.setWhichChild(1); System.out.println("*EX red --> green*"); }
       	      else {
       		  if (sphereSwitch.getWhichChild() == 1){sphereSwitch.setWhichChild(2); System.out.println("*EX green --> blue*"); }
       		  else {
       		      sphereSwitch.setWhichChild(0); System.out.println("*EX blue --> red*");
       		  }
       	      }
       	  } // end if exit
         } // end else	
	 } // end while end complex process -- */ 

      procStim_wakeUpCondition = new WakeupOr(procStimCriteria);
      wakeupOn(procStim_wakeUpCondition); 

      System.out.println(); 
      if (((WakeupCondition) procStim_wakeUpCondition) instanceof WakeupOr) {
	  System.out.println("*procStim new wakeupOn collision sphere ENTRY OR EXIT *"); 
      } else {
	  System.out.println("*procStim new wakeupOn collision sphere ENTRY AND EXIT *"); 
      }

   } // end process stimulus 

   /* END PROCESS STIMULUS BEHAVIOR  --------------------------------- */
 
} 

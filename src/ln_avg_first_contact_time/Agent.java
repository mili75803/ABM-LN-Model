/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ln_avg_first_contact_time;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;
import sim.util.MutableDouble2D;
import sim.field.network.*;
import sim.util.Bag;

public abstract class Agent implements Steppable {
    //Variables
    
    int ID;
    double x;
    double y;
    double z;
    public abstract void step(SimState state);
}
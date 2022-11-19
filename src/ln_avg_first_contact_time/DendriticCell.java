/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ln_avg_first_contact_time;



import java.awt.Color;
import sim.engine.*;
import sim.field.continuous.*;
import sim.util.*;
import java.lang.Math; 
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.field.network.Network;
public class DendriticCell extends Agent
{  
    double vol_x_left_corner;
    double vol_x_right_corner;
    double vol_y_top_corner;
    double vol_y_bottom_corner;
    double LN_x_left_corner;
    double LN_x_right_corner;
    double LN_y_top_corner;
    double LN_y_bottom_corner;
    public Bag virusList;
    public boolean virusDetected = false;
    double side;
    int localID;
    public DendriticCell(double LN_x_left_corner,  double LN_x_right_corner,  double LN_y_bottom_corner,  double LN_y_top_corner, double side)
    {
        //Defining the region to move.
        
       
        
        this.LN_x_left_corner = LN_x_left_corner;
        this.LN_x_right_corner = LN_x_right_corner;
        this.LN_y_bottom_corner = LN_y_bottom_corner;
        this.LN_y_top_corner = LN_y_top_corner;
        this.side = side;
    }
  
    public Color getColor() {
        return Color.green;
    }
    
    public void step(SimState state){
    }
    
    
}
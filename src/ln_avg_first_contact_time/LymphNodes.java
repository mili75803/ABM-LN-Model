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
public class LymphNodes extends Agent
{
    
    Coordinate3D corner[];
    double x;
    double y;
    double z;
    int localID;
    double Side;
    public LymphNodes(double centerX, double centerY, double centerZ, double Side, double scale)
    {
        corner = new Coordinate3D[8];
        //setCorners(centerX, centerY, centerZ);
        this.x = centerX;
        this.y = centerY;
        this.z = centerZ;
        this.Side = Side;
    
   
        for(int i = 0; i < 8; i++)
        {
            this.corner[i] = new Coordinate3D();
        }
        /*
front
bottom-left
bottom-right
top-right
top-left
*/
        corner[0].x = centerX - scale*Side;
        corner[0].y = centerY - scale*Side;
        corner[0].z = centerZ + scale*Side;
        
        
        corner[1].x =  centerX + scale*Side;
        corner[1].y =  centerY - scale*Side;
        corner[1].z = centerZ + scale*Side;
        
        corner[2].x =  centerX + scale*Side;
        corner[2].y =  centerY + scale*Side;
        corner[2].z = centerZ + scale*Side;
        
        corner[3].x =  centerX - scale*Side;
        corner[3].y =  centerY + scale*Side;
        corner[3].z = centerZ + scale*Side;
        
        corner[4].x =  centerX - scale*Side;
        corner[4].y =  centerY - scale*Side;
        corner[4].z = centerZ - scale*Side;
        
        corner[5].x =  centerX + scale*Side;
        corner[5].y =  centerY - scale*Side;
        corner[5].z = centerZ - scale*Side;
        
        corner[6].x =  centerX + scale*Side;
        corner[6].y =  centerY + scale*Side;
        corner[6].z = centerZ - scale*Side;
        
        corner[7].x =  centerX - scale*Side;
        corner[7].y =  centerY + scale*Side;
        corner[7].z = centerZ - scale*Side;
    }
    
    
    public Color getColor() {
        return Color.blue;
    }
    
    public Color setColor(){
        return Color.black;
    } 
    
    public void step(SimState state){}
}


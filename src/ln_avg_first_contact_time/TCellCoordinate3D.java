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
public class TCellCoordinate3D {
    double x,y,z;
    public TCellCoordinate3D()
    {
        
    }
    
    public TCellCoordinate3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public void setCoordinates(TCellCoordinate3D a)
    {
        this.x = a.x;
        this.y = a.y;
        this.z = a.z;
    }

    void setCoordinates(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
}
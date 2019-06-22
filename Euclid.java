/****************************
* Author: Thomas Di Pietro  *
* Date: 16/05/19            *
* Purpose: To demonstrate   *
* Euclid's algorithm        *
****************************/
import java.util.*;
public class Euclid
{
    public static void main(String[] args) 
    {
        int gcd;

        gcd = gcd(12543, 1682);
        if (gcd == 1)
        {
            System.out.println("gcd(12543, 1682) = "+gcd);
            System.out.println("12543 and 1682 are co-prime!");
        }
        else
        {
            System.out.println("gcd(12543, 1682) = "+gcd);
            System.out.println("12543 and 1682 are NOT co-prime.");
        }
    }

    public static int gcd(int a, int b)
    {
        int t = 0;
        while (b != 0)
        {
            t = b;
            b = a%b;
            a = t;
        }       
        return a;
    }

}
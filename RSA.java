/****************************
* Author: Thomas Di Pietro  *
* Date: 16/05/19            *
* Purpose: To demonstrate   *
* the method of RSA         *
* encryption and decryption *
****************************/
import java.math.*;
import java.util.*;
import java.io.*;
public class RSA
{
    public static void main(String []args)
    {
        Scanner sc = new Scanner(System.in);
        double p, q;
        int e, phi, n, d, numLines, choice=0;
        int[] vals;
        int[][] cipherText;
        char[][] decryptText;
        String[] file;

        //--------------------------------//
        //      Generate p,q,n,phi(n)     //
        //--------------------------------//

        /*Generate random numbers between 1000 and 10000 for p and q*/
        p = Math.random() * (10000 - 1000) + 1000;
        q = Math.random() * (10000 - 1000) + 1000;

        /*Test if p and q are prime numbers, loop and select 
        new random numbers until both are prime (seperate loops)*/
        while (!lehmannPrime((int)p))
        {
            p = Math.random() * (10000 - 1000) + 1000;
        }

        while (!lehmannPrime((int)q))
        {
            q = Math.random() * (10000 - 1000) + 1000;
        }

        /*Get n & phi*/
        n = (int)p * (int)q;
        phi = ((int)p - 1) * ((int)q - 1);

        //--------------------------------//
        //          Public key e          //
        //--------------------------------//
        e = 2; /*1 < e < phi*/
        while (e < phi && gcd((int)e, (int)phi) != 1)
        {
            /*Increment e until is co-prime 
            to phi, and also less than*/
            e++;
        }

        //--------------------------------//
        //          Private key d         //
        //--------------------------------//
        vals = new int[3];
        vals = gcdExt((int)e, (int)phi);
        d = vals[1]; /*d = x from gcdExt*/

        /*if d is negative then add phi*/
        if (d < 0)
        {
            d += (int)phi;
        }

        //--------------------------------//
        //        Encrypt/Decrypt         //
        //--------------------------------//

        //Read file into string array
        numLines = getNumLines("testfile-SDES.txt");
        file = readFile("testfile-SDES.txt", numLines);

        //Encrypt/decrypt message
        cipherText = encrypt(file, e, n);
        decryptText = decrypt(cipherText, d, n);

        //--------------------------------//
        //        Print to terminal       //
        //--------------------------------//
        while (choice != 5)
        {
            System.out.println("RSA - by Thomas Di Pietro");
            System.out.print("PRINT:\n1. All variables\n2. Plain-text\n3. Encrypted-text\n4. Decrypted-text\n5. Exit\nChoice:> ");
            choice = sc.nextInt();
            switch(choice)
            {
                case 1: 
                    System.out.println();
                    System.out.println("p: "+(int)p);
                    System.out.println("q: "+(int)q);
                    System.out.println("n: "+n);
                    System.out.println("e: "+e);
                    System.out.println("phi(n): "+phi);
                    System.out.println("d: "+d);
                    System.out.println();
                    break;
                case 2:
                    for (int i = 0; i < file.length; i++)
                    {
                        System.out.println(file[i]);
                    } 
                    break;
                case 3:
                    for (int i = 0; i < file.length; i++)
                    {
                        for (int j = 0; j < cipherText[i].length; j++)
                            System.out.print(cipherText[i][j]+" ");
                    }   
                    break;
                case 4:
                    for (int i = 0; i < cipherText.length; i++)
                    {
                        for (int j = 0; j < decryptText[i].length; j++)
                            System.out.print(decryptText[i][j]);
                        System.out.println();
                    }
                    break;
                case 5:
                    System.out.println("Program Terminated");
                    break;
            }
        }
    }

    public static int[][] encrypt(String[] file, int e, int n)
    {
        /*Encrypt: c = m^e mod n
        where c = ciphertext; m = plaintext (chars)*/
        char ch;
        int encNum;
        int[][] cipherText = new int[file.length][];

        for (int i = 0; i < file.length; i++)
        {
            cipherText[i] = new int[file[i].length()];
            char[] chArr = file[i].toCharArray();
            for (int j = 0; j < file[i].length(); j++)
            {
                ch = chArr[j];
                //Encrypt
                encNum = expoModComp((int)ch, e, n);
                cipherText[i][j] = encNum;
            }
        }
        return cipherText;
    }

    public static char[][] decrypt(int[][] cipherText, int d, int n)
    {
        /*Encrypt: c = m^e mod n
        where c = ciphertext; m = plaintext (chars)*/
        int num, decNum;
        char[][] decryptText = new char[cipherText.length][];

        for (int i = 0; i < cipherText.length; i++)
        {
            decryptText[i] = new char[cipherText[i].length];

            for (int j = 0; j < cipherText[i].length; j++)
            {
                //Decrypt each int
                num = cipherText[i][j];
                decNum = expoModComp(num, d, n);
                decryptText[i][j] = (char)decNum;
            }
        }

        return decryptText;
    }    

    public static boolean lehmannPrime(int p)
    {
        /*Lehmann's algorithm
        r = a^b mod p
        where b = (p-1/2)*/

        int r, b;
        double a;
        boolean prime = true;
        int allOne = 0;

        /*run it through 20 times*/
        for (int i = 0; i < 100; i++)
        {
            a = Math.random() * ((double)p-1 - 1) + 1; /*random number between 1 and p-1*/
            b = (p - 1) / 2;
            r = expoModComp((int)a, b, p);

            /*r=1 or r-p=-1*/
            if (r != 1 && r-p != -1)
            {
                /*Definitely not prime, set fail to 1*/
                prime = false;
            } 

            /*So if r is always equal to 1
            then prime test also fails ie. if allOne == number of tests (100)*/
            if (r == 1)
            {
                allOne++; 
            }
            /*Maximum of 50 percent chance that it is not prime
            so basically if all tests dodge the if statement then is 
            probably prime, else if it is ever not -1 or 1
            then definitely not prime and test FAILED*/
        }

        if (allOne == 100)
        {
            prime = false;
        }
        return prime;
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

    public static int[] gcdExt(int a, int b)
    {
        /*vals[0] = gcd, vals[1] = x, vals[2] = y*/
        int vals[] = new int[3];
        int temp;

        /*base case*/
        if (b == 0)
        {
            vals[0] = a;
            vals[1] = 1;
            vals[2] = 0; 
        }
        else
        {
            vals = gcdExt(b, a % b);
            temp = vals[1];
            vals[1] = vals[2];
            vals[2] = temp - (a / b) * vals[2];
        }
        return vals;
    }
    public static int[] decToBinary(int dec)
    {
        /*first count number of bits
        in specific decimal*/
        int bitCheck = dec;
        int bits = 1; /*as bitCheck loop doesnt account for 1*/
        int i;

        while (bitCheck >= 2)
        {
            bitCheck = bitCheck / 2;
            bits++;
        }

        int[] binary = new int[bits];
        for (i = bits - 1; i >= 0; i--)
        {
            binary[i] = dec % 2;
            dec = dec / 2;
        }
        return binary;
    }

    public static int expoModComp(int a, int b, int n)
    {
        int c = 0;
        double f = 1;
        int k;
        int[] binaryB;

        binaryB = decToBinary(b);
        k = binaryB.length;
        /*for calc of a^bmodn*/
        for (int i = 0; i < k; i++)
        {
            c = 2 * c;
            f = (f * f) % n;

            if (binaryB[i] == 1)
            {
                c++;
                f = (f * a) % n;
            }
        }
        return (int)f;       
    }
    //FROM OOPD SEMESTER 1 2018
    public static int getNumLines(String filename)
    {
        FileInputStream fileStrm = null;
        InputStreamReader rdr;
        BufferedReader bufRdr;

        int numLines = 0;
        String line;

        try
        {
            fileStrm = new FileInputStream(filename);
            rdr = new InputStreamReader(fileStrm);
            bufRdr = new BufferedReader(rdr);

            line = bufRdr.readLine(); 
            while (line != null)
            {
                numLines++;
                line = bufRdr.readLine();
            }
            fileStrm.close(); 
        }

        catch (IOException e)
        {
            if (fileStrm != null)
            {
                try
                {
                    fileStrm.close();
                }
                catch (IOException ex2){}
            }
            System.out.println("Error in file processing: " + e.getMessage());      
        }
        return numLines;
    }

    public static String[] readFile(String filename, int numLines)
    {
        FileInputStream fileStrm = null;
        InputStreamReader rdr;
        BufferedReader bufRdr;
        String[] file = new String[numLines];

        try
        {
            fileStrm = new FileInputStream(filename);
            rdr = new InputStreamReader(fileStrm);
            bufRdr = new BufferedReader(rdr);

            //Reads file into string array
            for (int i = 0; i < numLines; i++)
            {
                file[i] = bufRdr.readLine();
            }
            fileStrm.close(); 
        }

        catch (IOException e)
        {
            if (fileStrm != null)
            {
                try
                {
                    fileStrm.close();
                }
                catch (IOException ex2){}
            }
            System.out.println("Error in file processing: " + e.getMessage());      
        }
        return file;
    }
}
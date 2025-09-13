/*
Joshua T. Price
Oklahoma State University
CS-3513 Numerical Methods for Digital Computers
09-12-2025

This Java GUI application demonstrates IEEE 754 floating-point 
arithmetic by visualizing binary representations and computational 
steps for basic operations. It is for practice learning about numerical 
errors like precision loss through interactive examples with 
detailed explanations.

How to compile using javac from the command line
>javac FPAV.java FPAV_Frame.java FPAV_Panel.java FPAV_Model.java
>java FPAV

You must compiler all 4 files before running the main function of FPAV that 
acts as the controller by called the init of the JFrame I have created. 

For Proffesor/TA: 
I appligize for how hard this must be to compile when all pasted into a
single text box. For the future project utilizing swing or and scenrio where 
multiple files add the encapsulation needed, I am comfortable using handin
with the CSX server. But that is just a simpler way to compiler the work I 
have provide. Thank you for your time! 
 */

public class FPAV {
    public static void main(String args[]){
        new FPAV_Frame();
    }
}

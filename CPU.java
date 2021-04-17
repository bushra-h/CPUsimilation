package Proj1Package;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

//execute the instructions, and handle interrupts & timer
public class CPU {
	
	//list of called functions
	
    //read data from memory address
    private static int readMem(PrintWriter pw, InputStream is, Scanner sc, OutputStream os, int address) {
        isMemViolated(address);
        //the 1 means to read
        pw.printf("1," + address + "\n");
        pw.flush();
        
        if (sc.hasNext()) {
            String temp = sc.next();
            if(!temp.isEmpty()) {
                int temp2 = Integer.parseInt(temp);
                return (temp2); 
            }
        }
        return -1;
    }
    
    //send the file name to memory
    private static void sendFile(PrintWriter pw, InputStream is, OutputStream os, String fileName) {
        pw.printf(fileName + "\n"); 
        pw.flush();
    }
    
    //child process writes data memory address
    private static void writeMem(PrintWriter pw, InputStream is, OutputStream os, int address, int data) {
        //the 2 means to write
    	pw.printf("2," + address + "," + data + "\n");  
        pw.flush();
    }
    
    //check for interrupt by timer
    private static void timerInterrupt(PrintWriter pw, InputStream is, Scanner sc, OutputStream os) {
        int counter;
        userMode = false;
        counter = SP;
        SP = sStack;
        pushStack(pw, is, os, counter);
        counter = PC;
        PC = 1000;
        pushStack(pw, is, os, counter);
    }
    
    //check if user is attempting to access the system stack and memory
    private static void isMemViolated(int address) {
        if(userMode && address > 1000) {
            System.out.println("The process has exited due to the violation of system stack.");
            System.exit(0);
        }
    }
   
  //pop from stack
    private static int popStack(PrintWriter pw, InputStream is, Scanner sc, OutputStream os) {
        int temp = readMem(pw, is, sc, os, SP);
        writeMem(pw, is, os, SP, 0);
        SP++;
        return temp;
    }
    
    //push to stack
    private static void pushStack(PrintWriter pw, InputStream is, OutputStream os, int value) {
        SP--;
        writeMem(pw, is, os, SP, value);
    }
	
    //end of called functions list
    
    
    //register list
    static int PC = 0, SP = 1000, numInstruct = 0, timer, IR, AC, X, Y;
    
    //system and user stacks
    static int sStack = 2000;
    static int uStack = 1000;
    
    //user mode is initialized to true and is set to false when it changes to kernel mode
    static boolean userMode = true; 
    static boolean intrupt = false;
    
    //end register list
    
    
    public static void main(String args[]) {
        String fileName = null;
        
        if(args.length == 2) {
             fileName = args[0];
             timer = Integer.parseInt(args[1]); 
        }
        else {
            System.out.println("The process has ended due to the submission of an incorrect file");
            System.exit(0);
        }

        try {         
        	//get the current runtime associated with this process
            Runtime run = Runtime.getRuntime();
            Process proc = run.exec("java Memory");

            OutputStream os = proc.getOutputStream();
            PrintWriter pw = new PrintWriter(os);

            InputStream is = proc.getInputStream();
            Scanner sc = new Scanner(is); 
            
            //send to the child process
            sendFile(pw, is, os, fileName);
            
            while (true) {
                //if an interrupt occurs
                if(numInstruct > 0 && (numInstruct % timer) == 0 && intrupt == false) {
                    intrupt = true;
                    timerInterrupt(pw, is, sc, os);
                }
                
                // read instruction from memory
                int value = readMem(pw, is, sc, os, PC);
                
                if (value != -1) {
                    processingInstruct(value, pw, is, sc, os);
                }
                else
                    break;
            }
            
            proc.waitFor();
            int exitVal = proc.exitValue();
            System.out.println("The process has been halted due to: " + exitVal);

        } 
        catch (IOException | InterruptedException ie) {
           ie.printStackTrace();
        }
    }



    //process fetched instruction
    private static void processingInstruct(int instr, PrintWriter pw, InputStream is, Scanner sc, OutputStream os) {
        //the instruction is loaded into the instruction register
    	IR = instr; 
        int counter;    
        
        //instruction sets
        switch(IR) {
            case 1:
                PC++; 
                counter = readMem(pw, is, sc, os, PC);
                AC = counter;
                if(intrupt == false) {
                    numInstruct++;
                }
                PC++;
                break;
                
            case 2: 
                PC++;
                counter = readMem(pw, is, sc, os, PC);
                AC = readMem(pw, is, sc, os, counter);
                if(intrupt == false) {
                    numInstruct++;
                }
                PC++;
                break;

            case 3: 
                PC++;
                counter = readMem(pw, is, sc, os, PC);
                counter = readMem(pw, is, sc, os, counter);
                AC = readMem(pw, is, sc, os, counter);
                if(intrupt == false) {
                    numInstruct++;
                }
                PC++;
                break;
                
                
            case 4: 
                PC++;
                counter = readMem(pw, is, sc, os, PC);
                AC = readMem(pw, is, sc, os, counter + X);
                if(intrupt == false) {
                    numInstruct++;
                }
                PC++;
                break;
                
            case 5: 
                PC++;
                counter = readMem(pw, is, sc, os, PC);
                AC = readMem(pw, is, sc, os, counter + Y);
                if(intrupt == false) {
                    numInstruct++;
                }
                PC++;
                break;
                
            case 6: 
                AC = readMem(pw, is, sc, os, SP + X);
                if(intrupt == false) {
                    numInstruct++;
                }
                PC++;
                break;
                
            case 7: 
                PC++;
                counter = readMem(pw, is, sc, os, PC);
                writeMem(pw, is, os, counter, AC);
                if(intrupt == false) {
                    numInstruct++;
                }
                PC++;
                break;
                
            case 8: 
                Random rand = new Random();
                int i = rand.nextInt(100) + 1;
                AC = i;
                if(intrupt == false) {
                    numInstruct++;
                }
                PC++;
                break;
                
            case 9: 
                PC++;
                counter = readMem(pw, is, sc, os, PC);
                if(counter == 1)
                {
                    System.out.print(AC);
                    if(intrupt == false) {
                        numInstruct++;
                    }
                    PC++;
                    break;

                }
                else if (counter == 2) {
                    System.out.print((char)AC);
                    if(intrupt == false) {
                        numInstruct++;
                    }
                    PC++;
                    break;
                }
                else {
                    System.out.println("port = " + counter);
                    if(intrupt == false) {
                        numInstruct++;
                    }
                    PC++;
                    System.exit(0);
                    break;
                }
                
            case 10: 
                AC = AC + X;
                if(intrupt == false) {
                    numInstruct++;
                }
                PC++;
                break;
                
            case 11: 
                AC = AC + Y;
                if(intrupt == false) {
                    numInstruct++;
                }
                PC++;
                break;
                
            case 12: 
                AC = AC - X;
                if(intrupt == false) {
                    numInstruct++;
                }
                PC++;
                break;
            case 13: 
                AC = AC - Y;
                if(intrupt == false) { 
                    numInstruct++;
                }
                PC++;
                break;
                
            case 14: 
                X = AC;
                if(intrupt == false) { 
                    numInstruct++;
                }
                PC++;
                break;
                
            case 15: 
                AC = X;
                if(intrupt == false) { 
                    numInstruct++;
                }
                PC++;
                break;
                
            case 16: 
                Y = AC;
                if(intrupt == false) {
                    numInstruct++;
                }
                PC++;
                break;
               
            case 17: 
                AC = Y;
                if(intrupt == false) {
                    numInstruct++;
                }
                PC++;
                break;
                
            case 18: 
                SP = AC;
                if(intrupt == false) {
                    numInstruct++;
                }
                PC++;
                break;
                
            case 19:  
                AC = SP;
                if(intrupt == false) {
                    numInstruct++;
                }
                PC++;
                break;
                
            case 20: 
                PC++;
                counter = readMem(pw, is, sc, os, PC);
                PC = counter;
                if(intrupt == false) {
                    numInstruct++;
                }
                break;
                
            case 21: 
                PC++;
                counter = readMem(pw, is, sc, os, PC);
                if (AC == 0) {
                    PC = counter;
                    if(intrupt == false) {
                        numInstruct++;
                    }
                    break;
                }
                if(intrupt == false) {
                    numInstruct++;
                }
                PC++;
                break;
                
            case 22: 
                PC++;
                counter = readMem(pw, is, sc, os, PC);
                if (AC != 0) 
                {
                    PC = counter;
                    if(intrupt == false) {
                        numInstruct++;
                    }
                    break;
                }
                if(intrupt == false) 
                    numInstruct++;
                PC++;
                break;
                
            case 23: 
                PC++;
                counter = readMem(pw, is, sc, os, PC);
                pushStack(pw, is, os,PC+1);
                uStack = SP;
                PC = counter;
                if(intrupt == false) {
                    numInstruct++;
                }
                break;      
                
            case 24: 
                counter = popStack(pw, is, sc, os);
                PC = counter;
                if(intrupt == false) { 
                    numInstruct++;
                }
                break;
                
            case 25: 
                X++;
                if(intrupt == false) {
                    numInstruct++;
                }
                PC++;
                break;
            
            case 26: 
                X--;
                if(intrupt == false) {
                    numInstruct++;
                }
                PC++;
                break;
            
            case 27: 
                pushStack(pw, is, os,AC);
                PC++;
                if(intrupt == false) {
                    numInstruct++;
                }
                break;
                
            case 28: 
                AC = popStack(pw, is, sc, os);
                PC++;
                if(intrupt == false) {
                    numInstruct++;
                }
                break;
                
            case 29:
                intrupt = true;
                userMode = false;
                counter = SP;
                SP = 2000;
                pushStack(pw, is, os, counter);
                counter = PC + 1;
                PC = 1500;
                pushStack(pw, is, os, counter);
                
                if(intrupt == false) {
                    numInstruct++;
                }
                break;
                
            case 30: 
                PC = popStack(pw, is, sc, os);
                SP = popStack(pw, is, sc, os);
                userMode = true;
                numInstruct++;
                intrupt = false;
                break;
                
            case 50: 
                if(intrupt == false) {
                    numInstruct++;
                }
                System.exit(0);
                break;
            
            default:
                System.out.println("Error detected");
                System.exit(0);
                break;
        }
    }
}

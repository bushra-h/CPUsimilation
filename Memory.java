package Proj1Package;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


//read instructions from the file and store it into a memory array
public class Memory {   
	//memory array
    final static int [] memory = new int[2000];
    
	private static Scanner instruction;
	private static Scanner sc; 
    
    public static int read(int address) {
        return memory[address];
    }
    
    public static void write(int address, int data) {
        memory[address] = data;
    }

    private static void readFile(File file) { 
        try {
            sc = new Scanner(file);
            String str;
            int temp, counter = 0;

            while(sc.hasNext()) {
                if(sc.hasNextInt()) {
                    temp = sc.nextInt();
                    
                    //write into memory
                    memory[counter++] = temp;
                }
                
                //special characters will be skipped
                else {
                    str = sc.next();
                    if(str.charAt(0) == '.') {
                        counter = Integer.parseInt(str.substring(1));
                    }
                    else if(str.equals("//"))
                    {
                        sc.nextLine();
                    }
                    else
                        sc.nextLine();
                }
            }
        } 
        
        //the file is not found
        catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } 
    }
	
    public static void main(String args1[]) {
        try {
            instruction = new Scanner(System.in);
            File file = null;
            
            //read the file
            if(instruction.hasNextLine()) {
                file = new File(instruction.nextLine());
                
                //if the file doesn't exist
                if(!file.exists()) {
                    System.out.println("File not found");
                    System.exit(0);
                }
            }
            
            //read file and store on the memory array
            readFile(file);
            
            String str;
            int tempVal;
         
            //depending on the instruction, perform read and write functions
            while(true)
            {
                if(instruction.hasNext())
                {
                    str = instruction.nextLine(); 
                    if(!str.isEmpty())
                    {
                    	//split the given string into array of strings by separating it into substrings
                        String [] arrString = str.split(","); 
                        
                        //if it is 1, read from the address
                        if(arrString[0].equals("1"))    
                        {
                            tempVal = Integer.parseInt(arrString[1]);
                            System.out.println(read(tempVal));
                        }
                        
                        //write data into the address
                        else{
                            int i = Integer.parseInt(arrString[1]);
                            int j = Integer.parseInt(arrString[2]);
                            write(i,j); 
                        }
                    }
                    else 
                        break;
                }
                else
                    break;
            }
            
        } 
        //NumberFormatException will convert a string into a numeric value
        catch(NumberFormatException nfe) {
            nfe.printStackTrace();
        }

    }
    

}       
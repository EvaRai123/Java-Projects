import java.util.Scanner;
public class randomex{
    public static void main(String[]args){
        int z,y,x,temp;
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter any integer you choose. First enter an integer 'z', then enter an integer 'y'");
        z=sc.nextInt();
        y=sc.nextInt();
        System.out.println("Before Swapping/nz="+z+"/ny="+y);
        temp=z;
        y=z;
        temp=y;
        System.out.println("After Swapping/nz="+z+"/ny="+y);
        
        
    }
}
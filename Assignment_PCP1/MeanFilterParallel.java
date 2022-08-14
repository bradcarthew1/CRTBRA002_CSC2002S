//Assignment_PCP1:  Parallel Mean Filter
//Course Code:      CSC2002S
//Author:   	    Bradley Carthew 
//Student Number:   CRTBRA002 
//Date:             12 August 2022

import java.io.File;
import java.util.Arrays;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.concurrent.ForkJoinPool;

public class MeanFilterParallel {
    //initialise global timing variables
    static long startTime = 0;
	static long runTime = 0;

    //start timer
	private static void tic(){
		startTime = System.currentTimeMillis();
	}

    //end timer and calculate runtime
	private static void toc(){
		runTime = (System.currentTimeMillis() - startTime) ; 
	}

    static final ForkJoinPool fjPool = new ForkJoinPool();

    static int[] mean(int[] pixels, int width){
        return fjPool.invoke(new MeanArray(pixels, 0, pixels.length, width));
    }
	
	static int[] mean_filter(int[] p_arr, int w_width){
        //initialise an integer array to store the new pixel values 
        int[] new_pixels = new int[p_arr.length];

        tic(); //start timer
        new_pixels = mean(p_arr, w_width);
        toc(); //end time and calculate runtime

        //display timing message
        System.out.println("Runtime for parallel mean filter with window of size " + Integer.toString(w_width) + "x" + Integer.toString(w_width) 
        + ": " + runTime/1000.0f + " seconds");
        
        return new_pixels; //return the new pixel values as an integer array
	}
    
    public static void main(String args[])throws IOException{
        //initialise file and image variables
        BufferedImage img = null;
        File f = null;
    
        //check to see if all command-line input has been entered 
        if (args[0] == null || args[1] == null || args[2] == null){
            System.out.println("Error: There is missing command-line input.");
            System.exit(0);
        }

        //read in the image
        try{
          f = new File(args[0]);
          img = ImageIO.read(f);
        }catch(IOException e){
          System.out.println(e);
        }

        //initialise window width
        int window_width = 0;

        //set the window width and check if command-line input is an odd number
        if (Integer.parseInt(args[1]) % 2 == 0){
            System.out.println("Error: The window size must be an odd integer value.");
            System.exit(0);
        }
        else{
            window_width = Integer.parseInt(args[1]);
        }

        //determinine the width and height of image
        int img_width = img.getWidth();
        int img_height = img.getHeight();

        //set the border width based on the window width
        int border_width = (int)Math.floor(window_width/2);

        //initialise and populate an array with the pixel values
        int count = 0;
        int[] pixels =  new int[img_height*img_width];
        for (int h = 0; h < img_height; h++){
            for (int w = 0; w < img_width; w++){
                pixels[count] = img.getRGB(w,h);
                count += 1;
            } //image width loop ends here
        }//image height loop ends here

        
        System.out.println(pixels.length);

        //invoke the mean_filter() method and initialise an array for the mean pixel values
        int[] mean_pixels = new int[(img_height - border_width)*(img_width - border_width)];
        mean_pixels = mean_filter(pixels, window_width);

        count = 0;
        //set the pixel value to the mean of the RGB values
        for (int i = border_width; i < (img_height - border_width); i++){
            for (int j = border_width; j < (img_width - border_width); j++){
                img.setRGB(j, i, mean_pixels[count]);
                count += 1;
            } //image width loop ends here
        }//image height loop ends here

        //write the new pixel values to the desired output file
        try{
            f = new File(args[2]);
            ImageIO.write(img, "jpg", f);
        }catch(IOException e){
            System.out.println(e);
        }
    }
}

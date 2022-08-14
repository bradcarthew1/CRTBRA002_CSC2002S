//Assignment_PCP1:  Parallel Mean Filter
//Course Code:      CSC2002S
//Author:   	    Bradley Carthew 
//Student Number:   CRTBRA002 
//Date:             12 August 2022

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.concurrent.ForkJoinPool;

public class MeanFilterParallel {
    //initialise timing variables
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

    static int[] sum(int[] pixels){
        return fjPool.invoke(new RGBSum(pixels, 0, pixels.length));
    }
	
	static int[] mean_filter(BufferedImage img, int window_width, int border_width, int img_height, int img_width){
        //initialise an array to store pixel values in window
        int[] window_pixels = new int[window_width*window_width];
        int count = 0;

        //initialise an array to store new pixel values
        int[] new_pixels = new int[(img_height - border_width)*(img_width - border_width)];

        tic(); //start timer

        for (int i = border_width; i < (img_height - border_width); i++){
            for (int j = border_width; j < (img_width - border_width); j++){
                for (int h = 0; h < window_width; h++){
                    for (int w = 0; w < window_width; w++){
                        //get the pixel value
                        int p = img.getRGB(w + j - border_width, h + i - border_width);
                        window_pixels[w + h*window_width] = p;
                    }//window width loop ends here
                }//window height loop ends here

                int[] sum = sum(window_pixels);

                //populate new array with new pixel values
                int new_p = (sum[0]<<16) | (sum[1]<<8) | sum[2];
                new_pixels[count] = new_p;
                count += 1;
            }
        }

        toc(); //end time and calculate runtime
        System.out.println("Runtime for mean filter with window of size " + Integer.toString(window_width) + "x" + Integer.toString(window_width) 
        + ": " + runTime/1000.0f + " seconds");

        return new_pixels;
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

        //determinine the width and height of image
        int img_width = img.getWidth();
        int img_height = img.getHeight();

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

        //set the border width
        int border_width = (int)Math.floor(window_width/2);

        //invoke the mean_filter() method and initialise an array for the new pixel values
        int[] new_pixels = new int[(img_height - border_width)*(img_width - border_width)];
        new_pixels = mean_filter(img, window_width, border_width, img_height, img_width);

        int count = 0;
        //set the pixel value to the mean of the RGB values
        for (int i = border_width; i < (img_height - border_width); i++){
            for (int j = border_width; j < (img_width - border_width); j++){
                img.setRGB(j, i, new_pixels[count]);
                count += 1;
            } //image width loop ends here
        }//image height loop ends here
    }
}

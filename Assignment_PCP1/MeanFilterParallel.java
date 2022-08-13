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
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class MeanFilterParallel {
    static final ForkJoinPool fjPool = new ForkJoinPool();

    static sum(int[][] arr){
        return fjPool.invoke(new medianArray(arr,0,arr.length));
    }

    //mean_filter() implements the mean filter in parallel 
    static void mean_filter(BufferedImage img, int img_height, int img_width, int window_width){
        //initiallise the pixel and RGB values
        int p, r = 0, b = 0, g = 0;
        //initialise a two-dimensional array to store pixel values in window
        int[][] window_pixels = new int[window_width*window_width][3];
        //initialise count to control the indeces of the square window array
        int count = 0;

        //set the border width
        int border_width = (int)Math.floor(window_width/2);

        //implementation of the parallel mean filter
        for (int i = border_width; i < (img_height - border_width); i++){
            for (int j = border_width; j < (img_width - border_width); j++){
                for (int h = 0; h < window_width; h++){
                    for (int w = 0; w < window_width; w++){
                        //get the pixel value
                        p = img.getRGB(w + j - border_width, h + i - border_width);
        
                        //extract the RGB values from thei pixel value
                        r = (p>>16) & 0xff;
                        g = (p>>8) & 0xff;
                        b = p & 0xff;
        
                        //populate the square window array with RGB values
                        window_pixels[count][0] = r;
                        window_pixels[count][1] = g;
                        window_pixels[count][2] = b;
                        count += 1;
                    }//window width loop ends here
                }//window height loop ends here

                //calculate the median using fork-join
                int[] median_values = median(window_pixels);
        
            }//image width loop ends here
        }//image height loop ends here
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

        //get number of threads from system
        //int noThreads = Runtime.getRuntime().availableProcessors();

        //determinine the width and height of image
        int img_width = img.getWidth();
        int img_height = img.getHeight();

        //initialise the window width
        int window_width = 0;

        //set the window width and check if command-line input is an odd number
        if (Integer.parseInt(args[1]) % 2 == 0){
            System.out.println("Error: The window size must be an odd integer value.");
            System.exit(0);
        }
        else{
            window_width = Integer.parseInt(args[1]);
        }

        //execute the mean filter function
        mean_filter(img, img_height, img_width, window_width);
        
        //write the new pixel values to the desired output file
        try{
            f = new File(args[2]);
            ImageIO.write(img, "jpg", f);
        }catch(IOException e){
            System.out.println(e);
        }
    }
}

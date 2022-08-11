import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountedCompleter;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class MeanFilterSerial {
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
	
    //mean_filter() implements the mean filter sequentially
    static void mean_filter(BufferedImage img, int img_height, int img_width, int window_width){
        //initiallise the pixel and RGB values
        int p, r = 0, b = 0, g = 0;
        //initialise a two-dimensional array to store pixel values in window
        int[][] window_pixels = new int[window_width*window_width][3];
        //initialise count and index variable to control the indeces of the square window array
        int count = 0;
        int index = 0;

        //set the border width
        int border_width = (int)Math.floor(window_width/2);

        //initialise a two dimensional array to store new pixel values
        int[][] new_pixels = new int[(img_height - border_width)*(img_width - border_width)][3];

        //start
        tic();

        //implementation of the serial mean filter
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

                //set count to 0, and initialize sum and mean variables for RGB values
                count = 0;
                int r_sum = 0, g_sum = 0, b_sum = 0;
                int r_mean = 0, b_mean = 0, g_mean = 0;

                //find the mean RGB values
                for (int[] element : window_pixels){
                    r_sum += element[0];
                    g_sum += element[1];
                    b_sum += element[2];
                    r_mean = r_sum/(window_width*window_width);
                    g_mean = g_sum/(window_width*window_width);
                    b_mean = b_sum/(window_width*window_width);
                }

                //populate new array with new pixel values
                new_pixels[index][0] = r_mean;
                new_pixels[index][1] = g_mean;
                new_pixels[index][2] = b_mean;
                index += 1;
                
            }//image width loop ends here
        }//image height loop ends here

        //end
        toc();
        System.out.println("Runtime for mean filter with window of size " + Integer.toString(window_width) + "x" + Integer.toString(window_width) 
        + ": " + runTime/1000.0f + " seconds");

        index = 0;
        //set the pixel value to the mean of the RGB values
        for (int i = border_width; i < (img_height - border_width); i++){
            for (int j = border_width; j < (img_width - border_width); j++){
                p = (new_pixels[index][0]<<16) | (new_pixels[index][1]<<8) | new_pixels[index][2];
                img.setRGB(j, i, p);
                index += 1;
            } //image width loop ends here
        }//image height loop ends here

    }//filter() ends here

    //main method
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

        //determinine the width and height of the image
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
    } //main() ends here
} //MeanFilterSerial class ends here

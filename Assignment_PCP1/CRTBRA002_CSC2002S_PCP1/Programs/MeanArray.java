//Assignment_PCP1:  A recursive object used to calculate the mean pixels.
//Course Code:      CSC2002S
//Author:   	    Bradley Carthew 
//Student Number:   CRTBRA002 
//Date:             12 August 2022

//imports
import java.util.concurrent.RecursiveTask;

public class MeanArray extends RecursiveTask<int[]>{
	int lo; //start position of the array 
	int hi; //end position of the array
	int[] pixels; //array containing pixel values
	int width; //the width of the sliding square window
	static final int SEQUENTIAL_CUTOFF = 720000; //a cut-off to determine when sequential compuation begins
	    
	//constructor for intial instatiation of the MeanArray class
	MeanArray(int[] a, int l, int h, int w) { 
	    lo = l; 
		hi = h; 
		pixels = a;
		width = w;
	}

	//constructor for recursive use within the MeanArray class
	protected MeanArray(int[] a, int l, int h) { 
	    lo = l; 
		hi = h; 
		pixels = a;
	}

	//sequential implementation of the mean filter
	protected int[] serialCompute(){
		int ans[] = new int[pixels.length];
		
        for (int index = lo; index < lo + hi; index++) {
            int r = 0, g = 0, b = 0;

            for (int mi = -width; mi <= width; mi++) {
                int mindex = Math.min(Math.max(mi + index, 0),
                                    pixels.length - 1);
                int pixel = pixels[mindex];
                r += (pixel & 0x00ff0000) >> 16;
                g += (pixel & 0x0000ff00) >>  8;
                b += (pixel & 0x000000ff) >>  0;
            }
          
            // Reassemble destination pixel.
            int dpixel = (0xff000000) | ((int)(r/(width*width)) << 16) | ((int)(g/(width*width)) <<  8) |((int)(b/(width*width)) <<  0);
            ans[index] = dpixel;
        }

		return ans;
	}

	protected int[] compute(){
		if (hi < SEQUENTIAL_CUTOFF){
			int[] ans = new int[pixels.length];
			ans = serialCompute();
			return ans;
		}
		else{
			MeanArray left = new MeanArray(pixels, lo, (hi+lo)/2);
		    MeanArray right = new MeanArray(pixels, (hi+lo)/2, hi);
		    left.fork();
		    int[] rightAns = right.compute();
		    int[] leftAns  = left.join();

			//combine the left and right arrays
			int[] combine = new int[rightAns.length + leftAns.length];
			System.arraycopy(leftAns, 0, combine, 0, leftAns.length);
        	System.arraycopy(rightAns, 0, combine, leftAns.length, rightAns.length);
		    return combine;      
		}
	}
}

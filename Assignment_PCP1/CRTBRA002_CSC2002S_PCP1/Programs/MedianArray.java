import java.util.concurrent.RecursiveTask;
import java.util.Arrays;

public class MedianArray extends RecursiveTask<int[]>{
    int lo;
	int hi;
	int[] pixels;
	int width;
	static final int SEQUENTIAL_CUTOFF=700000;
	    
	MedianArray(int[] a, int l, int h, int w) { 
	    lo = l; 
		hi = h; 
		pixels = a;
		width = w;
	}

	MedianArray(int[] a, int l, int h) { 
	    lo = l; 
		hi = h; 
		pixels = a;
	}

    protected int median(int[] values){
        int med_value = 0;
        int num_elements = values.length;

        //check if total number of elements is even
        if (num_elements % 2 == 0) {
           int sum = values[num_elements/2] + values[num_elements/2 - 1];
           //calculate average of middle elements
           med_value = sum/2;
        } 
        else {
           //get the middle element
           med_value = values[values.length/2];
        }        

        return med_value;
    }

    protected int[] serialCompute(){
        int ans[] = new int[pixels.length];
		
        for (int index = lo; index < lo + hi; index++) {
            int[] r = new int[width*width]; 
            int[] g = new int[width*width];
            int[] b = new int[width*width];
            int count = 0;

            for (int mi = -width; mi <= width; mi++) {
                int mindex = Math.min(Math.max(mi + index, 0),
                                    pixels.length - 1);
                int pixel = pixels[mindex];
                r[count] = (pixel & 0x00ff0000) >> 16;
                g[count] = (pixel & 0x0000ff00) >>  8;
                b[count] = (pixel & 0x000000ff) >>  0;
                count += 1;
            }

            Arrays.sort(r);
            Arrays.sort(g);
            Arrays.sort(b);
          
            // Reassemble destination pixel.
            int dpixel = (0xff000000) | ((int)(median(r)) << 16) | ((int)(median(g)) <<  8) | ((int)(median(b)) <<  0);
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
			MedianArray left = new MedianArray(pixels, lo, (hi+lo)/2);
		    MedianArray right = new MedianArray(pixels, (hi+lo)/2, hi);
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

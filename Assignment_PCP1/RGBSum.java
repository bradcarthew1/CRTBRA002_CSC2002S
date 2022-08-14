//Assignment_PCP1:  A recursive object used to calculate the sum.
//Course Code:      CSC2002S
//Author:   	    Bradley Carthew 
//Student Number:   CRTBRA002 
//Date:             12 August 2022

import java.util.concurrent.RecursiveTask;

public class RGBSum extends RecursiveTask<int[]> {
	int lo; // arguments
	int hi;
	int[] arr;
	static final int SEQUENTIAL_CUTOFF=10000000;
	int[] ans = new int[3]; // result 
	    
	RGBSum(int[] a, int l, int h) { 
	    lo=l; hi=h; arr=a;
	}

	protected int[] compute(){// return answer - instead of run
	    if((hi-lo) < SEQUENTIAL_CUTOFF) {
			int[] ans = new int[3];
		    for(int i=lo; i < hi; i++){
		        ans[0] += (arr[i]>>16) & 0xff;
				ans[1] += (arr[i]>>8) & 0xff;
				ans[2] += arr[i] & 0xff;
			}
		    return ans;
		}
		else{
		    RGBSum left = new RGBSum(arr,lo,(hi+lo)/2);
		    RGBSum right= new RGBSum(arr,(hi+lo)/2,hi);
		    left.fork(); //this
		    int[] rightAns = right.compute(); //order
		    int[] leftAns  = left.join();   //is very
			int[] result = {leftAns[0] + rightAns[0], leftAns[0] + rightAns[0], leftAns[0] + rightAns[0]};
		    return result;      //important.
		}
	}
}

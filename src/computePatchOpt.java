import java.io.BufferedReader;
import java.io.FileReader;
import org.apache.commons.io.input.ReversedLinesFileReader;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
class computePatchOpt{

	public static void main(String[] args){
		PatchSolver mySolver = new PatchSolver(args[0], args[1]);
		//System.out.println("Calculating patch on Input File " + args[0] + " and Output File " + args[1]);
		//System.out.println();

		long t1 = System.currentTimeMillis();
		mySolver.calculatePatch();
		long t2 = System.currentTimeMillis();

		mySolver.printPatch();
		//System.out.println("Cost for the optimal patch is: " + mySolver.getPatchCost());
		//System.out.println("Calculating patch took " + (t2-t1)/1000 + "." + (t2-t1)%1000 + "ms");
		mySolver.cleanup();
	}
}

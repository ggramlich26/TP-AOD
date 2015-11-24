import java.io.BufferedReader;
import java.io.FileReader;
import org.apache.commons.io.input.ReversedLinesFileReader;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
class computePatchOpt{

	public static void main(String[] args){
		PatchSolver mySolver = new PatchSolver(args[0], args[1]);
		mySolver.calculatePatch();
		mySolver.cleanup();
	}
}

import java.io.BufferedReader;
import java.io.FileReader;
import org.apache.commons.io.input.ReversedLinesFileReader;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

public class PatchSolver{
	private static final int MAXCOST = Integer.MAX_VALUE - 1000;
	private int nInput;
	private int nOutput;
	private int nMin;
	private boolean inIndex; //true if line number of input file used as index
	private String inFile;
	private String outFile;
	private int[][] multDel; //1st element: cost for mult dest. 2nd element: inLine up to that you have to delete for best cost
	private PatchList[] multDelPatchList; //String of get to the Node, that is reached by the best multiple Destruction
	private Node[] prev;
	private Node[] prevPrev;
	private ReversedLinesFileReader inReversedReader;
	private ReversedLinesFileReader outReversedReader;
	private BufferedReader inStraightReader;
	private BufferedReader outStraightReader;
	private FileReader inReader;
	private FileReader outReader;
	private int currentInLine;
	private int currentOutLine;

	public PatchSolver(String inFile, String outFile){
		this.inFile = inFile;
		this.outFile = outFile;
		try{
			inReader = new FileReader(inFile);
			outReader = new FileReader(outFile);
			BufferedReader reader = new BufferedReader(inReader);
			int lines = 0;
			while (reader.readLine() != null) lines++;
			reader.close();
			nInput = lines;
			reader = new BufferedReader(outReader);
			lines = 0;
			while (reader.readLine() != null) lines++;
			reader.close();
			nOutput = lines;
		}
		catch(FileNotFoundException e){
			System.err.println("FileNotFoundException in determining file length: " + e.getMessage());
		}
		catch(IOException e){
			System.err.println("IOException in determining file lentgh: " + e.getMessage());
		}
		if(nInput < nOutput){
			inIndex = true;
			nMin = nInput;
		}
		else{
			inIndex = false;
			nMin = nOutput;
		}

		multDel = new int[2][nOutput+1];
		multDelPatchList = new PatchList[nOutput+1];
		for(int i = 0; i < nOutput+1; i++) {
			multDel[0][i] = this.MAXCOST;
			multDel[1][i] = 0;
			multDelPatchList[i] = null;
		}

		prev = new Node[nMin+1];
		prevPrev = new Node[nMin+1];
		for(int i = 0; i < nMin+1; i++){
			prev[i] = new Node();
			prevPrev[i] = new Node();
		}
		if(nInput < nOutput){
			prev[0].setCost(0);
			prev[0].setPatchList(new PatchList(null, ""));
		}
		else{
			prev[nMin].setCost(0);
			prev[nMin].setPatchList(new PatchList(null, ""));
		}

		//initialise file readers
		if(inIndex){
			try{
				inReversedReader = new ReversedLinesFileReader(new File(inFile));
				outStraightReader = new BufferedReader(new FileReader(outFile));
			}
			catch(FileNotFoundException e){
				System.err.println("FileNotFoundException in setting up Readers: " + e.getMessage());
			}
			catch(IOException e){
				System.err.println("IOException in setting up Readers: " + e.getMessage());
			}
			currentInLine = nInput;
			currentOutLine = 1;
		}
		else{
			currentInLine = 1;
			currentOutLine = nOutput;
			try{
				inStraightReader = new BufferedReader(inReader);
				outReversedReader = new ReversedLinesFileReader(new File(outFile));
			}
			catch(FileNotFoundException e){
				System.err.println("FileNotFoundException in setting up Readers: " + e.getMessage());
			}
			catch(IOException e){
				System.err.println("IOException in setting up Readers: " + e.getMessage());
			}
		}
	}

	public void cleanup(){
		try{
			inReader.close();
			outReader.close();
			if(inIndex){
				inReversedReader.close();
				outStraightReader.close();
			}
			else{
				inStraightReader.close();
				outReversedReader.close();
			}
		}
		catch(IOException e){
			System.err.println("IOException while closing readers: " + e.getMessage());
		}
	}

	private String readForwardInFile(int line){
		if(line == 0 || line > nInput){
			return null;
		}
		String str = "";
		if(line < currentInLine){
			try{
				inStraightReader.close();
				inStraightReader = new BufferedReader(new FileReader(inFile));
			}
			catch(FileNotFoundException e){
				System.err.println("FileNotFoundException in readForwardInFile: " + e.getMessage());
			}
			catch(IOException e){
				System.err.println("IOException in readForwardInFile: " + e.getMessage());
			}
			currentInLine = 1;
		}
		while(currentInLine <= line){
			currentInLine++;
			try{
				str = inStraightReader.readLine();
			}
			catch(IOException e){
				System.err.println("IOException in readForwardInFile: " + e.getMessage());
			}
		}
		return str;
	}

	private String readBackwardsInFile(int line){
		if(line == 0 || line > nInput){
			return null;
		}
		String str = "";
		try{
			if(line > currentInLine){
				inReversedReader.close();
				inReversedReader = new ReversedLinesFileReader(new File(inFile));
				currentInLine = nInput;
			}
			while(currentInLine >= line){
				currentInLine--;
				str = inReversedReader.readLine();
			}
		}
		catch(FileNotFoundException e){
			System.err.println("FileNotFoundException in readBackwardsInFile: " + e.getMessage());
		}
		catch(IOException e){
			System.err.println("IOException in readBackwardsInFile: " + e.getMessage());
		}
		return str;
	}

	private String readForwardOutFile(int line){
		if(line == 0 || line > nOutput){
			return null;
		}
		String str = "";
		try{
			if(line < currentOutLine){
				outStraightReader.close();
				outStraightReader = new BufferedReader(new FileReader(outFile));
				currentOutLine = 1;
			}
			try{
			while(currentOutLine <= line){
				currentOutLine++;
				str = outStraightReader.readLine();
			}
			}
			catch(IOException e){
				System.err.println("FileNotFoundException while trying to read outFile: " + e.getMessage());
			}
		}
		catch(FileNotFoundException e){
			System.err.println("FileNotFoundException in readForwardOutFile: " + e.getMessage());
		}
		catch(IOException e){
			System.err.println("IOException in readForwardOutFile: " + e.getMessage());
		}
		return str;
	}

	private String readBackwardsOutFile(int line){
		if(line == 0 || line > nOutput){
			return null;
		}
		String str = "";
		try{
			if(line > currentOutLine){
				outReversedReader.close();
				outReversedReader = new ReversedLinesFileReader(new File(outFile));
				currentOutLine = nOutput;
			}
			while(currentOutLine >= line){
				currentOutLine--;
				str = outReversedReader.readLine();
			}
		}
		catch(FileNotFoundException e){
			System.err.println("FileNotFoundException in readBackwardsOutFile: " + e.getMessage());
		}
		catch(IOException e){
			System.err.println("IOException in readBackwardsOutFile: " + e.getMessage());
		}
		return str;
	}
	
	private Node[] getDependencies(int index){
		Node[] dep;
		Node n = prevPrev[index];
		if(n.getInLine() == 0){
			dep = new Node[1];
			if(inIndex){
				dep[0] = prev[index];
			}
			else{
				dep[0] = prev[index+1];
			}
		}
		else if(n.getOutLine() == 0){
			dep = new Node[1];
			if(inIndex){
				dep[0] = prev[index-1];
			}
			else{
				dep[0] = prev[index];
			}
		}
		else{
			dep = new Node[3];
			if(inIndex){
				dep[0] = prev[index-1];
				dep[1] = prev[index];
				dep[2] = prevPrev[index-1];
			}
			else{
				dep[0] = prev[index+1];
				dep[1] = prev[index];
				dep[2] = prevPrev[index+1];
				}
		}
		return dep;
	}

	private int getDelCost(int index){
		int costOffset;
		if(inIndex){
			costOffset = prev[index-1].getCost();
		}
		else{
			costOffset = prev[index].getCost();
		}
		if(costOffset + 10 < multDel[0][prevPrev[index].getOutLine()]+15){
			return costOffset + 10;
		}
		else{
			return multDel[0][prevPrev[index].getOutLine()]+15;
		}
	}

	private String getDelString(int index){
		int costOffset;
		if(inIndex){
			costOffset = prev[index-1].getCost();
		}
		else{
			costOffset = prev[index].getCost();
		}
		if(costOffset + 10 < multDel[0][prevPrev[index].getOutLine()]+15){
			return "d " + prevPrev[index].getInLine();
		}
		else{
			return "D " + (multDel[1][prevPrev[index].getOutLine()]+1) + " " + (prevPrev[index].getInLine() - multDel[1][prevPrev[index].getOutLine()]);
		}
	}

	private void updateDel(int index, int cost, String str){
		if(cost < multDel[0][prevPrev[index].getOutLine()]){
			multDel[0][prevPrev[index].getOutLine()] = cost;
			multDel[1][prevPrev[index].getOutLine()] = prevPrev[index].getInLine();
			multDelPatchList[prevPrev[index].getOutLine()] = prevPrev[index].getPatchList();
		}
	}

	private int getAddCost(int index, String oString){
		int costOffset;
		if(inIndex){
			costOffset = prev[index].getCost();
		}
		else{
			costOffset = prev[index+1].getCost();
		}
		return costOffset + 10 + oString.length();
	}

	private String getAddString(int index, String oString){
		return "+ " + prevPrev[index].getInLine() + "\n" + oString;
	}

	private int getSubstCost(int index, String iString, String oString){
		int costOffset;
		if(inIndex){
			costOffset = prevPrev[index-1].getCost();
		}
		else{
			costOffset = prevPrev[index+1].getCost();
		}
		if(iString.equals(oString)){
			return costOffset;
		}
		return costOffset + 10 + oString.length();
	}

	private String getSubstString(int index, String iString, String oString){
		if(iString.equals(oString)){
			return null;
		}
		return "= " + prevPrev[index].getInLine() + "\n" + oString;
	}

	private void appendPatch(int index, String str){
		int fatherIndex;
		if(str == null){
			if(inIndex){
				fatherIndex = index-1;
			}
			else{
				fatherIndex = index+1;
			}
			prevPrev[index].setPatchList(prevPrev[fatherIndex].getPatchList());
		}
		else{
			str += "\n";
			if(str.charAt(0) == '='){
				if(inIndex){
					fatherIndex = index-1;
				}
				else{
					fatherIndex = index+1;
				}
				prevPrev[index].setPatchList(new PatchList(prevPrev[fatherIndex].getPatchList(), str));
			}
			else if(str.charAt(0) == '+'){
				if(inIndex){
					fatherIndex = index;
				}
				else{
					fatherIndex = index+1;
				}
				prevPrev[index].setPatchList(new PatchList(prev[fatherIndex].getPatchList(), str));
			}
			else if(str.charAt(0) == 'd'){
				if(inIndex){
					fatherIndex = index-1;
				}
				else{
					fatherIndex = index;
				}
				prevPrev[index].setPatchList(new PatchList(prev[fatherIndex].getPatchList(), str));
			}
			else if(str.charAt(0) == 'D'){
				prevPrev[index].setPatchList(new PatchList(multDelPatchList[prevPrev[index].getOutLine()], str));
			}
		}
	}

	private void treatNode(int index){
		//node was uses beofore. Update line numbers and check if they are still possible. If not return.
		if(prev[index].getCost() < this.MAXCOST){
			if(inIndex){
				prevPrev[index].setOutLine(prev[index].getOutLine()+1);
				if(prevPrev[index].getOutLine()>nOutput){
					return;
				}
			}
			else{
				prevPrev[index].setInLine(prev[index].getInLine()+1);
				if(prevPrev[index].getInLine()>nInput){
					return;
				}
			}
		}
		//node was not used before. Check if one of its dependencies was used before, if yes, use it, too
		else{
			if(inIndex && prev[index-1].getCost() < this.MAXCOST){
				prevPrev[index].setInLine(prev[index-1].getInLine()+1);
				prev[index].setInLine(prev[index-1].getInLine()+1);
				prevPrev[index].setOutLine(prev[index-1].getOutLine());
			}
			else if(!inIndex && prev[index+1].getCost() < this.MAXCOST){
				prevPrev[index].setOutLine(prev[index+1].getOutLine()-1);
				prev[index].setOutLine(prev[index+1].getOutLine()-1);
				prevPrev[index].setInLine(prev[index+1].getInLine());
			}
			else{
				return;
			}
		}
		//now we're sure we have a treatable node and can calculate the dependencies, and new values.
		Node n = prevPrev[index];
		int bestCost;
		String iString, oString;
		if(inIndex){
			iString = readBackwardsInFile(n.getInLine());
			oString = readForwardOutFile(n.getOutLine());
		}
		else{
			iString = readForwardInFile(n.getInLine());
			oString = readBackwardsOutFile(n.getOutLine());
		}
		//System.out.println("Node " + n.getInLine() + "  " + n.getOutLine() + "  istring: " + iString + "  oString: " + oString);
		String str;
		if(n.getInLine() == 0){
			bestCost = (getAddCost(index, oString));
			str = getAddString(index, oString);
		}
		else if(n.getOutLine() == 0){
			bestCost = (getDelCost(index));
			str = getDelString(index);
		}
		else{
			int addCost = getAddCost(index, oString);
			int delCost = getDelCost(index);
			int substCost = getSubstCost(index, iString, oString);
			if(addCost < delCost && addCost < substCost){
				bestCost = (addCost);
				str = getAddString(index, oString);
			}
			else if(delCost < substCost){
				bestCost = (delCost);
				str = getDelString(index);
			}
			else{
				bestCost = (substCost);
				str = getSubstString(index, iString, oString);
			}
			//System.out.println("addCost: " + addCost + "  delCost: " + delCost + "  substCost: " + substCost + "  bestCost: " + bestCost + "  str: " + str);
		}
		n.setCost(bestCost);
		appendPatch(index, str);
		updateDel(index, bestCost, str);
	}

	public void printPatch(){
		if(inIndex){
			PatchList it = prev[nMin].getPatchList();
			while(it.getNext() != null){
				System.out.print(it.getValue());
				it = it.getNext();
			}
		}
		else{
			PatchList it = prev[0].getPatchList();
			while(it.getNext() != null){
				System.out.print(it.getValue());
				it = it.getNext();
			}
		}
	}

	public int getPatchCost(){
		return inIndex?prev[nMin].getCost():prev[0].getCost();
	}

	public void calculatePatch(){
		if(inIndex){
			for(int i = 0; i < nInput + nOutput; i++){
				System.out.println(i + "    " + ((int)(100*i/(1.0*nInput+nOutput))) + "%");
				for(int j = nMin; j >= 0; j--){
					treatNode(j);
				}
				Node[] tmp = prevPrev;
				prevPrev = prev;
				prev = tmp;
			}
		}
		else{
			for(int i = 0; i < nInput + nOutput; i++){
				System.out.println(i + "     " + ((int)(100*i/(1.0*nInput+nOutput))) + "%");
				for(int j = 0; j < nMin + 1; j++){
					treatNode(j);
				}
				Node[] tmp = prevPrev;
				prevPrev = prev;
				prev = tmp;
			}
		}
	}

}

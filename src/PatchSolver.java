public class PatchSolver{
	private int nInput;
	private int nOutput;
	private int nMin;
	private boolean inIndex; //true if line number of input file used as index
	private String inFile;
	private String outFile;
	private int[][] multDel;
	private Node[] prev;
	private Node[] prevPrev;

	public PatchSolver(String inFile, String outFile){
		this.inFile = inFile;
		this.outFile = outFile;
		BufferedReader reader = new BufferedReader(new FileReader(inFile));
		int lines = 0;
		while (reader.readLine() != null) lines++;
		reader.close();
		nInput = lines;
		reader = new BufferedReader(new FileReader(outFile));
		lines = 0;
		while (reader.readLine() != null) lines++;
		reader.close();
		nOutput = lines;
		if(nInput < nOutput){
			inIndex = true;
			nMin = nInput;
		}
		else{
			inIndex = false;
			nMin = nOutput;
		}

		multDel = new int[2][nOutput+1];
		for(int i = 0; i < nOutput+1; i++) {
			multDel[0][i] = Integer.MAX_VALUE;
			multDel[1][i] = 0;
		}

		prev = Node[nMin+1];
		prevPrev = Node[nMin+1];
		for(int i = 0; i < nMin+1; i++){
			prev[i] = new Node();
			prevPrev[i] = new Node();
		}
		if(nInput < nOutput){
			prev[0].setCost(0);
		}
		else{
			prev[nMin].setCost(0);
		}
	}
	
	private Node[] getDependencies(int index){
		Node[] dep;
		Node n = prevPrev[index];
		if(n.getInLine == 0){
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
		if(inIndex){
			costOffset = prev[index-1].getCost();
		}
		else{
			costOffsset = prev[index].getCost();
		}
		if(costOffset + 10 < multDel[0][prevPrev[index].getOutLine()]+15){
			return costOffset + 10;
		}
		else{
			return multDel[0][prevPrev[index].getOutLine()]+15;
		}
	}

	private int getDelString(int index){
		if(inIndex){
			costOffset = prev[index-1].getCost();
		}
		else{
			costOffsset = prev[index].getCost();
		}
		if(costOffset + 10 < multDel[0][prevPrev[index].getOutLine()]+15){
			return "d " + prevPrev[index].getInLine();
		}
		else{
			return "D " + (multDel[1][prevPrev[index].getOutLine()]+1) + " " + (prevPrev[index].getInLine() - multDel[1][prevPrev[index].getOutLine()]);
		}
	}

	private int updateDel(int index, int cost, String str){
		if(cost < multDel[0][prevPrev[index].getOutLine()]){
			multDel[0][prevPrev[index].getOutLine()] = cost;
			multDel[1][prevPrev[index].getOutLine()] = prevPrev[index].getInLine();
			multDelStr[prevPrev[index].getOutLine()] = multDest[prevPrev[index].getOutLine()] + str;
		}
	}

	private int getAddCost(int index, String oString){
		if(inIndex){
			costOffset = prev[index].getCost();
		}
		else{
			costOffset = prev[index+1].getCost();
		}
		return costOffste + 10 + oString.length();
	}

	private int getAddString(int index, String oString){
		return "+ " + prevPrev[index].getInLine() + "\n" + oString;
	}

	private int getSubstCost(int index, String iString, String oString){
		int costOffset;
		if(inIndex){
			costOffset = prevPrev[index-1];
		}
		else{
			costOffset = prevPrev[index+1];
		}
		if(iString.equals(oString)){
			return costOffset;
		}
		return costOffset + 10 + oString.length();
	}

	private int getSubstString(int index, String iString, String oString){
		if(iString.equals(oString)){
			return null;
		}
		return "= " + prevPrev[index].getInLine() + "\n" + oString;
	}

	private appendPatch(int index, String str){
		int fatherIndex;
		if(str == null){
			if(inIndex){
				fatherIndex = index-1;
			}
			else{
				fatherIndex = index+1;
			}
			prevPrev[index].setPatchString(prevPrev[fatherIndex].getPatch());
		}
		else{
			if(str.charAt(0) == '='){
				if(inIndex){
					fatherIndex = index-1;
				}
				else{
					fatherIndex = index+1;
				}
				prevPrev[index].setPatchString(prevPrev[fatherIndex].getPatch() + str);
			}
			else if(char.At(0) == '+'){
				if(inIndex){
					father = index;
				}
				else{
					fatherIndex = index+1;
				}
				prevPrev[index].setPatchString(prev[fatherIndex].getPatch() + str);
			}
			else if(str.charAt(0) == 'd'){
				if(inIndex){
					fatherIndex = index-1;
				}
				else{
					fatherIndex = index;
				}
				prevPrev[index].setPatchString(prev[fatherIndex].getPatch() + str);
			}
			else if(str.charAt(0) == 'D'){
				prevPrev[index].setPatchString(multDelStr[prevPrev[index].getOutLine()] + str);
			}
		}
	}

	private void treatNode(int index, String iString, String oString){
		if(prev[index].getCost < Integer.MAX_VALUE){
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
		else{
			if(inIndex && prev[index-1].getCost() < Integer.MAX_VALUE){
				prevPrev[index].setInLine(prev[index-1].getInLine()+1);
				prevPrev[index].setOutLine(prev[index-1].getOutLine());
			}
			else if(!inIndex && prev[index+1].getCost() < Integer.MAX_VALUE){
				prevPrev[index].setOutLine(prev[index+1].getOutLine()-1);
				prevPrev[index].setInLine(prev[index+1].getInLine());
			}
			else{
				return;
			}
		}
		//now we're sure we have a treatable node and can calculate the dependencies, and new values.
		Node n = prevPrev[index];
		int bestCost;
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
		}
		n.setCost(bestCost);
		appendPatch(index, str);
		updateDelCost(index, bestCost, str);
	}

	public void printPatch(){
		if(inIndex){
			System.out.println(prev[nMin].getPatch());
		}
		else{
			System.out.println(prev[0].getPatch());
		}
	}

	private void calculatePatch(){
		for(int j = 0; j < nInput + nOutput + 1; j++){
			for(int i = 0; i < nMin+1; i++){
				treatNode(i);
			}
			Node[] tmp = prevPrev;
			prevPrev = prev;
			prev = tmp;
		}
		printPatch();
	}

}

public class PatchList{
	private PatchList next;
	private String value;

	public PatchList(PatchList next, String value){
		this.next = next;
		this.value = value;
	}

	public String getValue(){
		return this.value;
	}

	public PatchList getNext(){
		return this.next;
	}
}

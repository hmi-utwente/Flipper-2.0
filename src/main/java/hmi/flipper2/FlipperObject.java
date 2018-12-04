package hmi.flipper2;

public class FlipperObject {
	
	private String id;
	
	private static int idCount = 100;
	
	public FlipperObject(String id) {
		this.id = (id==null) ? "fid"+idCount++ : id;
	}
	
	public String id() {
		return this.id;
	}
	
	public void addPrefix(String pfx) {
		this.id = pfx + ":" + id();
	}
	
}

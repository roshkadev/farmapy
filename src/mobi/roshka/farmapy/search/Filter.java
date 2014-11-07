package mobi.roshka.farmapy.search;

public class Filter {
	private String name;
	private int radius;
	private String category;
	
	public Filter(){};
	
	public Filter(String name, int radius, String category){
		this.name = name;
		this.radius = radius;
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getDistance() {
		return radius;
	}

	public void setDistance(int distance) {
		this.radius = distance;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public boolean isEmpty(){
		return (name == null) && (category == null) && (radius == 0);
	}
}

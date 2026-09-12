
public abstract class Animal {
	public String animalName;
	public int animalWeight;
	public String getAnimalName() {
		return animalName;
	}
	public void setAnimalName(String animalName) {
		this.animalName = animalName;
	}
	public int getAnimalWeight() {
		return animalWeight;
	}
	public void setAnimalWeight(int animalWeight) {
		this.animalWeight = animalWeight;
	}
	
	public abstract void printInfo();
	
}

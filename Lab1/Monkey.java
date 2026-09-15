public class Monkey extends Animal {
	public String favFood;

	public Monkey(String name, int weight, String favFood) {
		super();
		this.animalName = name;
		this.animalWeight = weight;
		this.favFood = favFood;
	}

	@Override
	public String getAnimalName() {
		return super.getAnimalName();
	}

	@Override
	public void setAnimalName(String animalName) {
		super.setAnimalName(animalName);
	}

	// Đã sửa thành kiểu int
	@Override
	public int getAnimalWeight() {
		return super.getAnimalWeight();
	}

	// Đã sửa thành kiểu int
	@Override
	public void setAnimalWeight(int animalWeight) {
		super.setAnimalWeight(animalWeight);
	}

	@Override
	public void printInfo() {
		// Dùng %s cho tên (String), %d cho cân nặng (int), %s cho thức ăn (String)
		System.out.printf("Con khỉ tên %s nặng %d cân và thích ăn %s\n", this.animalName, this.animalWeight, this.favFood);
	}
}
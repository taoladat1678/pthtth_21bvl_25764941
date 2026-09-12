public class Lion extends Animal {
	public int portalPerDay;

	public Lion(String name, int weight, int portalPerDay) {
		super();
		this.animalName = name;
		this.animalWeight = weight;
		this.portalPerDay = portalPerDay;
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
		// Dùng %s cho tên (String), %d cho cân nặng và thức ăn (int)
		System.out.printf("Sư tử %s nặng %d cân, ăn %d cân thịt mỗi ngày\n", this.animalName, this.animalWeight, this.portalPerDay);
	}
}
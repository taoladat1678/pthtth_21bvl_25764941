public class Snake extends Animal {
	public int snakeLength;

	public Snake(String name, int weight, int snakeLength) {
		super();
		this.animalName = name;
		this.animalWeight = weight;
		this.snakeLength = snakeLength;
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
		// Dùng %s cho tên (String), %d cho cân nặng và chiều dài (int)
		System.out.printf("Con rắn độc mang tên %s nặng %d cân, dài %d mét\n", this.animalName, this.animalWeight, this.snakeLength);
	}
}
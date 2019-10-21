public class Bunny extends Animal implements Herbivore {
	int attentionTimer = 0;
	int hungerTimer = 0;
	boolean fleeing;

	public Bunny(Vector2f origin, int width, int height) {
		super(origin, width, height);
		this.speed = .45f;
		this.hp = 10;
		this.maxHP = 10;
		this.attack = 2;
		this.hunger = 0;
		this.maxHunger = 20;
		this.viewDistance = 120;
		this.attentionSpan = 3000;
		this.fleeing = false;
	}

	public void consumePlant(Flora plant) {
		this.hunger = Math.max(0, this.hunger - plant.satiety);
		this.hp = Math.min(this.maxHP, this.hp + plant.health);
		plant.deactivate();
	}

	@Override
	public void update(int delta) {
		attentionTimer += delta;
		hungerTimer += delta;
		if (attentionChanged(attentionTimer)) {
			fleeing = false;
			attentionTimer = 0;
		}
		if (gainedHunger(hungerTimer)) hungerTimer = 0;
		dieIfTooHungry();
		if (this.target != null && !fleeing) pursuePlant();
		moveWithinConfines();
	}
}
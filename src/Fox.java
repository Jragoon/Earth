public class Fox extends Animal implements Carnivore {
	int attentionTimer = 0;
	int hungerTimer = 0;

	public Fox(Vector2f origin, int width, int height) {
		super(origin, width, height);
		this.speed = .7f;
		this.hp = 15;
		this.maxHP = 15;
		this.attack = 4;
		this.hunger = 0;
		this.maxHunger = 20;
		this.viewDistance = 60;
		this.attentionSpan = 12000;
	}

	public void consumeFlesh(Animal prey) {
		float foodValue = prey.maxHunger - prey.hunger;
		this.hunger = Math.min(0, this.hunger - foodValue);
		this.hp = Math.max(this.maxHP, this.hp + foodValue);
		prey.deactivate();
	}

	@Override
	public void update(int delta) {
		attentionTimer += delta;
		hungerTimer += delta;
		if (attentionChanged(attentionTimer)) attentionTimer = 0;
		if (gainedHunger(hungerTimer)) hungerTimer = 0;
		dieIfTooHungry();
		if (this.enemy != null) pursueEnemy();
		moveWithinConfines();
	}
}
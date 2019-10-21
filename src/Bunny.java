import java.util.Random;

public class Bunny extends Animal implements Herbivore {
	Random r = new Random();
	int attentionTimer = 0;
	int hungerTimer = 0;
	int reproducingTimer = 0;
	int cooldownTimer = 0;
	boolean fleeing;
	boolean reproducing;
	Bunny mate;

	public Bunny(Vector2f origin, int width, int height) {
		super(origin, width, height);
		this.speed = .45f;
		this.hp = 10;
		this.maxHP = 10;
		this.attack = 2;
		this.hunger = 0;
		this.maxHunger = 20;
		this.viewDistance = 100;
		this.attentionSpan = 3000;
		this.fleeing = false;
		this.reproducing = false;
		mate = null;
	}

	public void consumePlant(Flora plant) {
		this.hunger = Math.max(0, this.hunger - plant.satiety);
		this.hp = Math.min(this.maxHP, this.hp + plant.health);
		plant.deactivate();
	}

	public void mateWith(Bunny lover) {
		this.reproducingTimer = 0;
		lover.reproducingTimer = 0;
		this.reproducing = true;
		lover.reproducing = true;
		lover.mate = this;
		this.mate = lover;
	}

	public void stopMating() {
		this.reproducing = false;
		this.mate.reproducing = false;
		this.mate.cooldownTimer = 0;
		this.cooldownTimer = 0;
		this.mate.changeDirection();
		this.changeDirection();
		this.mate.mate = null;
		this.mate = null;
	}

	@Override
	public void update(int delta) {
		attentionTimer += delta;
		hungerTimer += delta;
		if (reproducing) {
			reproducingTimer += delta;
			return;
		}
		cooldownTimer += delta;
		if (attentionChanged(attentionTimer)) {
			fleeing = false;
			attentionTimer = 0;
		}
		if (gainedHunger(hungerTimer)) hungerTimer = 0;
		dieIfTooHungry();
		if (this.target != null && !fleeing && r.nextFloat() < .6f) pursuePlant();
		moveWithinConfines();
	}
}
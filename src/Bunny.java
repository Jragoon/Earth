import edu.utc.game.Game;

public class Bunny extends Animal implements Herbivore {
	public Bunny(Vector2f origin, int width, int height) {
		super(origin, width, height);
		this.speed = .3f;
		this.hp = 10;
		this.maxHP = 10;
		this.attack = 2;
		this.hunger = 0;
		this.maxHunger = 20;
		this.viewDistance = 120;
	}

	public void consumePlant(Flora plant) {
		this.hunger = Math.max(0, this.hunger - plant.satiety);
		this.hp = Math.min(this.maxHP, this.hp + plant.health);
	}

	@Override
	public void update(int delta) {
		if (this.location.x <= 0 || this.location.x + this.hitbox.width > Game.ui.getWidth()) {
			this.direction.x = -this.direction.x;
		}
		if (this.location.y <= 0 || this.location.y + this.hitbox.height > Game.ui.getHeight()) {
			this.direction.y = -this.direction.y;
		}
		this.location.x += this.direction.x * this.speed;
		this.location.y += this.direction.y * this.speed;
		this.hitbox.x = (int) this.location.x;
		this.hitbox.y = (int) this.location.y;
	}

	public void scoutForFood() {

	}
}
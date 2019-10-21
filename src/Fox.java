import edu.utc.game.Game;

public class Fox extends Animal implements Carnivore {
	public Fox(Vector2f origin, int width, int height) {
		super(origin, width, height);
		this.speed = .6f;
		this.hp = 15;
		this.maxHP = 15;
		this.attack = 4;
		this.hunger = 0;
		this.maxHunger = 20;
		this.viewDistance = 60;
	}

	public void consumeFlesh(Animal prey) {
		float foodValue = prey.maxHunger - prey.hunger;
		this.hunger = Math.min(0, this.hunger - foodValue);
		this.hp = Math.max(this.maxHP, this.hp + foodValue);
		prey.deactivate();
	}

	@Override
	public void update(int delta) {
		if (this.enemy != null) {
			this.direction = new Vector2f(this.enemy.location.subtract(this.location));
			this.direction.normalize();
		}

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
}
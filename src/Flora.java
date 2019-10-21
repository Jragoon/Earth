import edu.utc.game.GameObject;

public class Flora extends GameObject {
	private Vector2f location;

	public Flora(Vector2f origin) {
		this.hitbox.setBounds((int) origin.x, (int) origin.y, 25, 25);
		this.location = origin;
		this.setColor(1, 1, 1);
	}

	public Vector2f getLocation() {
		return this.location;
	}

	@Override
	public void update(int delta) {
		this.hitbox.x = (int) this.location.x;
		this.hitbox.y = (int) this.location.y;
	}
}
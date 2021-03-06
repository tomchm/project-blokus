package edu.cornell.blokus;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class InputController implements InputProcessor {
	public boolean clicked = false;
	public Pair pos = new Pair(0,0);

	@Override
	public boolean keyDown(int keycode) {
		return Gdx.input.isKeyJustPressed(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		pos.x = screenX;
		pos.y = screenY;
		clicked = true;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		clicked = false;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		pos.x = screenX;
		pos.y = screenY;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
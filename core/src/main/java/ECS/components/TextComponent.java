// import packages vô đây

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
public class TextComponent implements Component, Pool.Poolable {
// hình ảnh hiện tại của thực thể
public TextureRegion currImage  = null;
// lấy lại hình ảnh hiện tại
public TextureRegion getCurrImage() {
    return currImage;
}
public void setCurrImage(TextureRegion img) {
    currImage = img;
}
public void reset() {
    currImage = null;
}
}

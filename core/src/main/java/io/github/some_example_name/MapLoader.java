package io.github.some_example_name;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.graphics.Texture;

public class MapLoader {
    public static List<int[]> loadMap(String fileName) {
        FileHandle file = Gdx.files.internal("maps/" + fileName);
        String[] lines = file.readString().split("\n");
        List<int[]> map = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.trim().split(" ");
            int[] row = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                row[i] = Integer.parseInt(parts[i]);
            }
            map.add(row);
        }
        return map;
    }
    private Texture getBrickTexture(int id) {
        switch (id) {
            case 1: return new Texture("Brick1_4.png");
            case 2: return new Texture("Brick2_4.png");
            case 3: return new Texture("Brick3_4.png");
            case 4: return new Texture("Brick4_4.png");
            case 5: return new Texture("Brick5_4.png");
            case 6: return new Texture("Brick6_4.png");
            case 7: return new Texture("Brick7_4.png");
            case 8: return new Texture("Brick8_4.png");
            case 9: return new Texture("Brick9_4.png");
            case 10: return new Texture("Brick_unbreakable2.png");
            default: return null;
        }
    }

}

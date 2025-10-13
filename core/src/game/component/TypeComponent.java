// import package vô đây

import com.badlogic.ashley.core.component;

/**
 * hàm để xác định loại của thực thể
 */

public class TypeComponent {
    public static final int PLAYER_TYPE = 1 ;
    public static final int BALL_TYPE = 2;
    public static final int BLOCK_TYPE = 3;
    public static final int OTHER_TYPE = 4;
    public int type = OTHER_TYPE;
    //getters and setters
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    // reset method to reset the type to OTHER_TYPE
    public void reset() {
        type = OTHER_TYPE;
}
    // constructor
    public TypeComponent(int type) {
        this.type = type;
    }
    // default constructor
    public TypeComponent() {
        this.type = OTHER_TYPE;
    }
}

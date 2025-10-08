
// import packback vô đây sau khi code 

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
// cái này sẽ cho phép 1 thực thể sẽ gắn với 1 thực thể khác
public class LinkedEntityComponent {
    public Entity LinkedEntity;
    public void setLinkedEntity(Entity carry)  {
        System.out.println(carry);
        this.LinkedEntity = carry;
    }
}

/**
 * Sau khi có đủ cấu trúc thì import lại package vào đây
 * Mỗi "Component" trong game được dùng để lưu trữ dữ liệu hoặc đánh dấu cho một thực thể.
 * Lớp Animated_com được sử dụng để đánh dấu rằng thực thể này có khả năng hoạt hình (animation),
 * tức là có thể di chuyển, thay đổi khung hình, hoặc có trạng thái chuyển động.
 * Đây là một "marker component" - không chứa dữ liệu, chỉ dùng để nhận diện.
 * để thực hiện việc cập nhật và hiển thị hoạt hình tương ứng.
 */

package game.component;

/**
 * Lớp Animated_com đóng vai trò là component đánh dấu trong mô hình ECS.
 * Component này cho biết một thực thể có thể hiển thị hoạt hình (animation).
 * <p>
 * Lớp này hiện không chứa thuộc tính hay phương thức nào, chỉ dùng để phân loại entity.
 * Trong tương lai, có thể mở rộng thêm các biến về trạng thái hoạt hình, tốc độ, hoặc tên animation.
 */
public class Animated_com {

    // Không có thuộc tính hoặc phương thức.
    // Nếu cần mở rộng, có thể thêm:
    // - Tên animation hiện tại (currentAnimation)
    // - Tốc độ chuyển khung hình (animationSpeed)
    // - Cờ cho biết animation có lặp lại hay không (loop)
}

                    BÀI TẬP 2

1. Nội dung Prompt sau khi tối ưu
```
Bạn đang là 1 lập trình viên Backend Senior, 
Bạn cần viết một lớp Java tích hợp với dịch vụ thanh toán VNPay để xử lý giao dịch. 
Lập trình viên tiền nhiệm đã thiết kế, hãy viết class VNPayPaymentService bằng Java để thực hiện thanh toán trực tuyến. 
Yêu cầu sử dụng biến môi trường (Environment Variable) thông qua hàm System.getenv() để nạp khóa bí mật, không thay đổi tên hàm 
viết thêm logic kiểm tra nếu biến môi trường bị thiếu (null hoặc rỗng) thì phải ném ra ngoại lệ phù hợp để đảm bảo an toàn hệ thống khi vận hành.
```

2. Đoạn code AI sinh ra sau khi cập nhật prompt mới
```
import java.util.Objects;

public class VNPayPaymentService {

    private static final String VNPAY_TMN_CODE =
            getRequiredEnv("VNPAY_TMN_CODE");

    private static final String VNPAY_HASH_SECRET =
            getRequiredEnv("VNPAY_HASH_SECRET");

    private static final String VNPAY_PAYMENT_URL =
            getRequiredEnv("VNPAY_PAYMENT_URL");

    /**
     * Giữ nguyên tên hàm theo thiết kế cũ
     */
    public String processPayment(long amount, String orderInfo) {

        // Logic tạo URL thanh toán VNPay
        String paymentUrl = VNPAY_PAYMENT_URL
                + "?vnp_TmnCode=" + VNPAY_TMN_CODE
                + "&amount=" + amount
                + "&orderInfo=" + orderInfo;

        // Các bước ký HMAC SHA512 bằng VNPAY_HASH_SECRET
        // ...

        return paymentUrl;
    }

    /**
     * Đọc biến môi trường và kiểm tra an toàn
     */
    private static String getRequiredEnv(String envName) {
        String value = System.getenv(envName);

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Missing required environment variable: " + envName
            );
        }

        return value;
    }
}
```
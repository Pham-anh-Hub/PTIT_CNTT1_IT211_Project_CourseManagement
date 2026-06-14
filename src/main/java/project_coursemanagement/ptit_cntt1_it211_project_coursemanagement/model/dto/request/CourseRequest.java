package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CourseRequest {
    @NotBlank(message = "Mã khóa học không được để trống")
    @Size(max = 20, message = "Mã khóa học tối đa 20 ký tự")
    private String courseCode;

    @NotBlank(message = "Tên khóa học không được để trống")
    @Size(max = 100, message = "Tên khóa học tối đa 100 ký tự")
    private String courseName;

    @Min(value = 1, message = "Số tín chỉ phải lớn hơn hoặc bằng 1")
    private int credits;

    @NotBlank(message = "Email giảng viên không được để trống")
    @Email(message = "Email giảng viên không đúng định dạng")
    private String lecturerEmail;

    @NotBlank(message = "Số điện thoại giảng viên không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Số điện thoại chưa đúng định dạng")
    private String lecturerPhone;
}

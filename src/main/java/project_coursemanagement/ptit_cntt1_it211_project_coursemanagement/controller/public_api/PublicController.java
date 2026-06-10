package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.controller.public_api;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.RegisterUserDTO;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.PublicService;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicController {

    private final PublicService publicService;

    @PostMapping("/createStudent")
    public ResponseEntity<?> createNewStudentAccount(@Valid @RequestBody RegisterUserDTO registerUser){
        // gọi tới service
        return ResponseEntity.status(HttpStatus.CREATED).body(publicService.createNewStudentAccounts(registerUser));
    }
}

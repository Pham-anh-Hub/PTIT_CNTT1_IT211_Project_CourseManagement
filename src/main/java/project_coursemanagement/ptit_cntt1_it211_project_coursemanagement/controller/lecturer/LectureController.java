package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.controller.lecturer;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.ScoreFeedbackRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.ScoreFeedbackResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.security.principle.UserPrinciple;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.SetPointFeedbackNoteService;

@RestController
@RequestMapping("/api/v1/lecturer")
@RequiredArgsConstructor
public class LectureController {


    private final SetPointFeedbackNoteService setPointFeedbackNoteService;

    @PostMapping
    public ResponseEntity<ScoreFeedbackResponse> scoreFeedbackSubmission(
            @AuthenticationPrincipal UserPrinciple userPrinciple,
            @Valid @RequestBody ScoreFeedbackRequest request
            ){
        String lecturerName = userPrinciple.getUsername();
        ScoreFeedbackResponse response = setPointFeedbackNoteService.gradeSubmission(lecturerName, request);
        return ResponseEntity.ok(response);
    }
}

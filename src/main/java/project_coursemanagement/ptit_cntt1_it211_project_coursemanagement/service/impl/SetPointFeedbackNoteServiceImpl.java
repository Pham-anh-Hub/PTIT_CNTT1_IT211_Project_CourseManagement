package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.exception.UserNotFoundException;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.request.ScoreFeedbackRequest;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.dto.response.ScoreFeedbackResponse;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Submission;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Users;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.enums.SubmissionStatus;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.SubmissionRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository.UsersRepository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.SetPointFeedbackNoteService;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class SetPointFeedbackNoteServiceImpl implements SetPointFeedbackNoteService {
    private final UsersRepository usersRepository;
    private final SubmissionRepository submissionRepository;


    @Override
    public ScoreFeedbackResponse gradeSubmission(String lecturerUsername, ScoreFeedbackRequest request) {
        // Lấy thông tin giảng viên đang đăng nhập
        Users lecturer = usersRepository.findByUsername(lecturerUsername).orElseThrow(() -> new UserNotFoundException("Không tìm thấy giảng viên, vui lòng kiểm tra lại"));

        // Tìm bài nộp theo submissionID
        Submission submission = submissionRepository.findById(request.getSubmissionId()).orElseThrow(() -> new RuntimeException("Không tìm thấy bài nộp nào"));

        // Kiểm tra trạng thái bài nộp có thể sẽ được chấm hay không
        if (submission.getStatus() == SubmissionStatus.PENDING) {
            throw new RuntimeException("Sinh viên chưa nộp bài, không thể chấm điểm");
        }

        // Kiểm tra bài nộp đã được chấm hay chưa
        if (submission.getStatus() == SubmissionStatus.GRADED) {
            throw new RuntimeException("Bài nộp này đã được chấm điểm trước đó");
        }

        // tiến hành cập nhật điểm, feedback
        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setStatus(SubmissionStatus.GRADED);
        submission.setGradedBy(lecturer);
        submission.setGradedAt(LocalDateTime.now());
        submission.setUpdatedAt(LocalDateTime.now());
        Submission submissionUpdated = submissionRepository.save(submission);

        // trả về response
        return ScoreFeedbackResponse.builder().submissionId(request.getSubmissionId()).studentName(submissionUpdated.getStudent().getFullName()).assignmentTitle(submissionUpdated.getAssignment().getTitle()).score(submissionUpdated.getScore()).feedback(submissionUpdated.getFeedback()).status(submissionUpdated.getStatus().toString()).gradedAt(LocalDateTime.now()).gradedByName(lecturer.getFullName()).build();
    }
}

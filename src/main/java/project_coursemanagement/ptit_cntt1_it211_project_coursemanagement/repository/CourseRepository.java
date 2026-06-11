package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.model.entity.Courses;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Courses, Long> {
    boolean existsByCourseCode(String code);

    Optional<Courses> findByCourseCode(String code);

    Optional<Courses> findByIdAndActiveTrue(Long id);

    @Query("""
                from Courses c
                where c.isActive = true
                and
                    (:courseName = ''
                        or lower(c.courseName)
                        like lower(concat('%', :courseName, '%')))
                and
                    (:lecturer = ''
                        or lower(c.lecturer.fullName)
                        like lower(concat('%', :lecturer, '%')))
            """)
    Page<Courses> searchCourse(@Param("courseName") String courseName, @Param("lecturer") String lecturer, Pageable pageable);
}

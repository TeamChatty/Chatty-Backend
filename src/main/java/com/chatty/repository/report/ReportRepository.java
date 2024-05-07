package com.chatty.repository.report;

import com.chatty.entity.report.Report;
import com.chatty.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReporterAndReported(User reporter, User reported);
}

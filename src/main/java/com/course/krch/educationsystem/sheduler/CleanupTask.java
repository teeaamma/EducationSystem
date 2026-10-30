package com.course.krch.educationsystem.sheduler;

import com.course.krch.educationsystem.repository.ScheduleRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class CleanupTask {

    private final ScheduleRepository scheduleRepository;

    @Scheduled(cron = "${app.cleanup.cron}")
    public void removeOutdatedSchedules(){
        LocalDateTime yearAgo = LocalDateTime.now().minusYears(1);
        int deleted = scheduleRepository.softDeleteByEndDateBefore(yearAgo);
        System.out.println("Удалено устаревших занятий: " + deleted);
    }
}

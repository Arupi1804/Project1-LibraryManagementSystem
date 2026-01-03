package com.hust.project1.config;

import com.hust.project1.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled tasks for library management system
 */
@Component
public class ScheduledTasks {

    @Autowired
    private BorrowRecordService borrowRecordService;

    /**
     * Update overdue status for borrow records
     * Runs every day at midnight (00:00)
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void updateOverdueRecords() {
        System.out.println("🕐 Running scheduled task: Update overdue borrow records");
        borrowRecordService.updateOverdueStatus();
        System.out.println("✅ Overdue status updated successfully");
    }

    /**
     * Also run every hour during business hours (8 AM - 8 PM)
     * to ensure timely updates
     */
    @Scheduled(cron = "0 0 8-20 * * *")
    public void updateOverdueRecordsHourly() {
        borrowRecordService.updateOverdueStatus();
    }
}

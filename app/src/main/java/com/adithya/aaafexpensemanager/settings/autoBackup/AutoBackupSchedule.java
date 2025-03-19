package com.adithya.aaafexpensemanager.settings.autoBackup;

import androidx.annotation.NonNull;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AutoBackupSchedule {
    private static final String TAG = "AutoBackupSchedule";
    private Duration backupInterval = Duration.ofDays(1);
    private LocalDateTime referenceBackupTime = LocalDate.now().atStartOfDay();
    public AutoBackupSchedule(){
    }
    public AutoBackupSchedule(LocalDateTime referenceBackupTime){
        this.referenceBackupTime = referenceBackupTime;
    }

    public AutoBackupSchedule(Duration backupInterval){
        this.backupInterval = backupInterval;
    }
    public AutoBackupSchedule(Duration backupInterval, LocalDateTime referenceBackupTime){
        this.backupInterval = backupInterval;
        this.referenceBackupTime = referenceBackupTime;
    }
    public AutoBackupSchedule(LocalTime dailyRunTime){
        this.referenceBackupTime = this.referenceBackupTime.toLocalDate().atTime(dailyRunTime);
    }
    public LocalDateTime getNextBackupTime(){
        do{
            referenceBackupTime = referenceBackupTime.plus(backupInterval);
        } while(referenceBackupTime.isBefore(LocalDateTime.now().plusMinutes(1)));
        return referenceBackupTime;
    }
    public long calculateDelay(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextBackupTime = getNextBackupTime();
        return Duration.between(now, nextBackupTime).toMillis();
    }
}

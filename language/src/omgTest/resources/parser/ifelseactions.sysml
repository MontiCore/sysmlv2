if threat.level == high {
    perform soundAlarm {in cause = threat;}
} else {
    action sendNotification {in msg = threat;}
    action beginMonitoring {in target = threat;}
}

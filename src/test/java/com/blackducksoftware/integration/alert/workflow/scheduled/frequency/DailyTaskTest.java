package com.blackducksoftware.integration.alert.workflow.scheduled.frequency;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.blackducksoftware.integration.alert.common.enumeration.DigestType;

public class DailyTaskTest {
    
    @Test
    public void testDigestType() {
        final DailyTask task = new DailyTask(null, null, null, null);
        assertEquals(DigestType.DAILY, task.getDigestType());
    }

    @Test
    public void testGetTaskName() {
        final DailyTask task = new DailyTask(null, null, null, null);
        assertEquals(DailyTask.TASK_NAME, task.getTaskName());
    }
}

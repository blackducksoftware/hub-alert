/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.synopsys.integration.alert.common.digest.model;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.synopsys.integration.alert.common.enumeration.FrequencyType;
import com.synopsys.integration.alert.database.entity.NotificationCategoryEnum;

public class ProjectDataBuilderTest {

    @Test
    public void testBuilderNull() {
        final ProjectDataBuilder projectDataBuilder = new ProjectDataBuilder();

        assertNotNull(projectDataBuilder.getCategoryBuilderMap());
        assertTrue(projectDataBuilder.getCategoryBuilderMap().isEmpty());
        assertNull(projectDataBuilder.getFrequencyType());
        assertNull(projectDataBuilder.getProjectName());
        assertNull(projectDataBuilder.getProjectVersion());

        final ProjectData projectData = new ProjectData(null, null, null, Collections.emptyList(), new HashMap<>());
        assertEquals(projectData, projectDataBuilder.build());
    }

    @Test
    public void testBuilder() {
        final Map<String, Object> data = new HashMap<>();
        data.put("Key", "Value");
        final ItemData itemData = new ItemData(data);
        final CategoryDataBuilder categoryDataBuilder = new CategoryDataBuilder();
        categoryDataBuilder.addItem(itemData);
        categoryDataBuilder.setCategoryKey(NotificationCategoryEnum.HIGH_VULNERABILITY.toString());

        final ProjectDataBuilder projectDataBuilder = new ProjectDataBuilder();

        projectDataBuilder.addCategoryBuilder(NotificationCategoryEnum.HIGH_VULNERABILITY, categoryDataBuilder);
        projectDataBuilder.setFrequencyType(FrequencyType.DAILY);
        projectDataBuilder.setProjectName("Project");
        projectDataBuilder.setProjectVersion("Version");

        assertNotNull(projectDataBuilder.getCategoryBuilderMap());
        assertFalse(projectDataBuilder.getCategoryBuilderMap().isEmpty());
        assertEquals(categoryDataBuilder, projectDataBuilder.getCategoryBuilderMap().get(NotificationCategoryEnum.HIGH_VULNERABILITY));
        assertEquals(FrequencyType.DAILY, projectDataBuilder.getFrequencyType());
        assertEquals("Project", projectDataBuilder.getProjectName());
        assertEquals("Version", projectDataBuilder.getProjectVersion());

        final Map<NotificationCategoryEnum, CategoryData> categoryMap = new HashMap<>();
        categoryMap.put(NotificationCategoryEnum.HIGH_VULNERABILITY, categoryDataBuilder.build());

        ProjectData projectData = new ProjectData(FrequencyType.DAILY, "Project", "Version", Collections.emptyList(), categoryMap);
        assertEquals(projectData, projectDataBuilder.build());

        projectDataBuilder.removeCategoryBuilder(NotificationCategoryEnum.HIGH_VULNERABILITY);

        assertNotNull(projectDataBuilder.getCategoryBuilderMap());
        assertTrue(projectDataBuilder.getCategoryBuilderMap().isEmpty());
        assertEquals(FrequencyType.DAILY, projectDataBuilder.getFrequencyType());
        assertEquals("Project", projectDataBuilder.getProjectName());
        assertEquals("Version", projectDataBuilder.getProjectVersion());

        projectData = new ProjectData(FrequencyType.DAILY, "Project", "Version", Collections.emptyList(), new HashMap<>());
        assertEquals(projectData, projectDataBuilder.build());
    }

}

package com.synopsys.integration.alert.component.authentication.security;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.synopsys.integration.alert.common.enumeration.DefaultUserRole;
import com.synopsys.integration.alert.common.persistence.accessor.ConfigurationAccessor;
import com.synopsys.integration.alert.common.persistence.accessor.UserAccessor;
import com.synopsys.integration.alert.common.persistence.model.ConfigurationFieldModel;
import com.synopsys.integration.alert.common.persistence.model.ConfigurationModel;
import com.synopsys.integration.alert.component.authentication.descriptor.AuthenticationDescriptor;
import com.synopsys.integration.alert.component.authentication.descriptor.AuthenticationDescriptorKey;

public class UserManagementAuthoritiesPopulatorTest {
    private static final String TEST_USERNAME = "testUserName";
    private final AuthenticationDescriptorKey descriptorKey = new AuthenticationDescriptorKey();
    private final ConfigurationAccessor configurationAccessor = Mockito.mock(ConfigurationAccessor.class);
    private final ConfigurationModel configurationModel = Mockito.mock(ConfigurationModel.class);
    private final ConfigurationFieldModel roleMappingField = Mockito.mock(ConfigurationFieldModel.class);
    private final ConfigurationFieldModel samlAttributeMappingField = Mockito.mock(ConfigurationFieldModel.class);
    private final UserAccessor userAccessor = Mockito.mock(UserAccessor.class);

    @Test
    public void testAddGrantedAuthorities() {
        String roleNameMapping = "TEST_ADMIN_ROLE";
        Mockito.when(roleMappingField.getFieldValue()).thenReturn(Optional.of(roleNameMapping));
        Mockito.when(configurationModel.getField(Mockito.eq(AuthenticationDescriptor.KEY_ROLE_MAPPING_NAME_ADMIN))).thenReturn(Optional.of(roleMappingField));
        Mockito.when(configurationAccessor.getConfigurationsByDescriptorKey(Mockito.eq(descriptorKey))).thenReturn(List.of(configurationModel));
        Mockito.when(userAccessor.getUser(Mockito.anyString())).thenReturn(Optional.empty());
        UserManagementAuthoritiesPopulator authoritiesPopulator = new UserManagementAuthoritiesPopulator(descriptorKey, configurationAccessor, userAccessor);

        GrantedAuthority testAdminRole = new SimpleGrantedAuthority(roleNameMapping);
        Set<GrantedAuthority> inputRoles = Set.of(testAdminRole);
        Set<GrantedAuthority> actualRoles = authoritiesPopulator.addAdditionalRoles(TEST_USERNAME, inputRoles);

        assertTrue(actualRoles.contains(testAdminRole), "The actual roles did not contain the expected role name: " + roleNameMapping);
    }

    @Test
    public void testAddGrantedAuthoritiesNoMapping() {
        String roleNameMapping = "TEST_ADMIN_ROLE";
        Mockito.when(roleMappingField.getFieldValue()).thenReturn(Optional.empty());
        Mockito.when(configurationModel.getField(Mockito.eq(AuthenticationDescriptor.KEY_ROLE_MAPPING_NAME_ADMIN))).thenReturn(Optional.of(roleMappingField));
        Mockito.when(configurationAccessor.getConfigurationsByDescriptorKey(Mockito.eq(descriptorKey))).thenReturn(List.of(configurationModel));
        Mockito.when(userAccessor.getUser(Mockito.anyString())).thenReturn(Optional.empty());
        UserManagementAuthoritiesPopulator authoritiesPopulator = new UserManagementAuthoritiesPopulator(descriptorKey, configurationAccessor, userAccessor);

        GrantedAuthority testAdminRole = new SimpleGrantedAuthority(roleNameMapping);
        GrantedAuthority alertUserRole = new SimpleGrantedAuthority("ROLE_" + DefaultUserRole.ALERT_USER.name());
        Set<GrantedAuthority> inputRoles = Set.of(alertUserRole, testAdminRole);
        Set<GrantedAuthority> actualRoles = authoritiesPopulator.addAdditionalRoles(TEST_USERNAME, inputRoles);

        assertEquals(inputRoles, actualRoles);
    }
 
    @Test
    public void testAddRoleNames() {
        String roleNameMapping = "TEST_ADMIN_ROLE";
        Mockito.when(roleMappingField.getFieldValue()).thenReturn(Optional.of(roleNameMapping));
        Mockito.when(configurationModel.getField(Mockito.eq(AuthenticationDescriptor.KEY_ROLE_MAPPING_NAME_ADMIN))).thenReturn(Optional.of(roleMappingField));
        Mockito.when(configurationAccessor.getConfigurationsByDescriptorKey(Mockito.eq(descriptorKey))).thenReturn(List.of(configurationModel));
        Mockito.when(userAccessor.getUser(Mockito.anyString())).thenReturn(Optional.empty());
        UserManagementAuthoritiesPopulator authoritiesPopulator = new UserManagementAuthoritiesPopulator(descriptorKey, configurationAccessor, userAccessor);
        Set<String> inputRoles = Set.of(roleNameMapping);
        Set<String> actualRoles = authoritiesPopulator.addAdditionalRoleNames(TEST_USERNAME, inputRoles, false);

        assertTrue(actualRoles.contains(roleNameMapping), "The actual roles did not contain the expected role name: " + roleNameMapping);
    }

    @Test
    public void testAddRoleNamesNoMapping() {
        String roleNameMapping = "TEST_ADMIN_ROLE";
        Mockito.when(roleMappingField.getFieldValue()).thenReturn(Optional.empty());
        Mockito.when(configurationModel.getField(Mockito.eq(AuthenticationDescriptor.KEY_ROLE_MAPPING_NAME_ADMIN))).thenReturn(Optional.of(roleMappingField));
        Mockito.when(configurationAccessor.getConfigurationsByDescriptorKey(Mockito.eq(descriptorKey))).thenReturn(List.of(configurationModel));
        Mockito.when(userAccessor.getUser(Mockito.anyString())).thenReturn(Optional.empty());
        UserManagementAuthoritiesPopulator authoritiesPopulator = new UserManagementAuthoritiesPopulator(descriptorKey, configurationAccessor, userAccessor);
        Set<String> inputRoles = Set.of(DefaultUserRole.ALERT_USER.name(), roleNameMapping);
        Set<String> actualRoles = authoritiesPopulator.addAdditionalRoleNames(TEST_USERNAME, inputRoles, false);

        assertEquals(inputRoles, actualRoles);
    }

    @Test
    public void testAddRoleNamesWithPrefix() {
        String roleNameMapping = "TEST_ADMIN_ROLE";
        Mockito.when(roleMappingField.getFieldValue()).thenReturn(Optional.of(roleNameMapping));
        Mockito.when(configurationModel.getField(Mockito.eq(AuthenticationDescriptor.KEY_ROLE_MAPPING_NAME_ADMIN))).thenReturn(Optional.of(roleMappingField));
        Mockito.when(configurationAccessor.getConfigurationsByDescriptorKey(Mockito.eq(descriptorKey))).thenReturn(List.of(configurationModel));
        Mockito.when(userAccessor.getUser(Mockito.anyString())).thenReturn(Optional.empty());
        UserManagementAuthoritiesPopulator authoritiesPopulator = new UserManagementAuthoritiesPopulator(descriptorKey, configurationAccessor, userAccessor);
        Set<String> inputRoles = Set.of(roleNameMapping);
        Set<String> actualRoles = authoritiesPopulator.addAdditionalRoleNames(TEST_USERNAME, inputRoles, true);

        assertTrue(actualRoles.contains(roleNameMapping), "The actual roles did not contain the expected role name: " + roleNameMapping);
    }

    @Test
    public void testSAMLAttributeName() {
        String attributeName = "SAML_ATTRIBUTE_NAME";
        Mockito.when(samlAttributeMappingField.getFieldValue()).thenReturn(Optional.of(attributeName));
        Mockito.when(configurationAccessor.getConfigurationsByDescriptorKey(Mockito.eq(descriptorKey))).thenReturn(List.of(configurationModel));
        Mockito.when(configurationModel.getField(Mockito.eq(AuthenticationDescriptor.KEY_SAML_ROLE_ATTRIBUTE_MAPPING))).thenReturn(Optional.of(samlAttributeMappingField));
        Mockito.when(userAccessor.getUser(Mockito.anyString())).thenReturn(Optional.empty());
        UserManagementAuthoritiesPopulator authoritiesPopulator = new UserManagementAuthoritiesPopulator(descriptorKey, configurationAccessor, userAccessor);
        assertEquals(attributeName, authoritiesPopulator.getSAMLRoleAttributeName("DEFAULT_ATTRIBUTE"));
    }

    @Test
    public void testSAMLAttributeNameNotFound() {
        String attributeName = "DEFAULT_ATTRIBUTE";
        Mockito.when(samlAttributeMappingField.getFieldValue()).thenReturn(Optional.empty());
        Mockito.when(configurationAccessor.getConfigurationsByDescriptorKey(Mockito.eq(descriptorKey))).thenReturn(List.of(configurationModel));
        Mockito.when(configurationModel.getField(Mockito.eq(AuthenticationDescriptor.KEY_SAML_ROLE_ATTRIBUTE_MAPPING))).thenReturn(Optional.of(samlAttributeMappingField));
        Mockito.when(userAccessor.getUser(Mockito.anyString())).thenReturn(Optional.empty());
        UserManagementAuthoritiesPopulator authoritiesPopulator = new UserManagementAuthoritiesPopulator(descriptorKey, configurationAccessor, userAccessor);
        assertEquals(attributeName, authoritiesPopulator.getSAMLRoleAttributeName(attributeName));
    }

    @Test
    public void testSAMLAttributeConfigurationNotFound() {
        String attributeName = "DEFAULT_ATTRIBUTE";
        Mockito.when(configurationAccessor.getConfigurationsByDescriptorKey(Mockito.eq(descriptorKey))).thenReturn(List.of());
        Mockito.when(userAccessor.getUser(Mockito.anyString())).thenReturn(Optional.empty());
        UserManagementAuthoritiesPopulator authoritiesPopulator = new UserManagementAuthoritiesPopulator(descriptorKey, configurationAccessor, userAccessor);
        assertEquals(attributeName, authoritiesPopulator.getSAMLRoleAttributeName(attributeName));
    }

}

package com.blackducksoftware.integration.alert.channel.email;

import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.alert.TestPropertyKey;
import com.blackducksoftware.integration.alert.channel.ChannelManagerTest;
import com.blackducksoftware.integration.alert.channel.email.EmailDescriptor;
import com.blackducksoftware.integration.alert.channel.email.mock.MockEmailRestModel;
import com.blackducksoftware.integration.alert.channel.email.model.EmailGroupDistributionConfigEntity;
import com.blackducksoftware.integration.alert.channel.email.model.EmailGroupDistributionRepository;
import com.blackducksoftware.integration.alert.channel.email.model.EmailGroupDistributionRestModel;
import com.blackducksoftware.integration.alert.channel.email.model.EmailGlobalConfigEntity;
import com.blackducksoftware.integration.alert.channel.email.model.EmailGlobalRepository;
import com.blackducksoftware.integration.alert.descriptor.ChannelDescriptor;

public class EmailChannelManagerTestIT extends ChannelManagerTest<EmailGroupDistributionRestModel, EmailGroupDistributionConfigEntity, EmailGlobalConfigEntity> {

    @Autowired
    private EmailGlobalRepository emailGlobalRepository;

    @Autowired
    private EmailGroupDistributionRepository distributionRepository;

    @Autowired
    private EmailDescriptor emailDescriptor;

    @Override
    public void cleanGlobalRepository() {
        emailGlobalRepository.deleteAll();
    }

    @Override
    public void saveGlobalConfiguration() {
        final String smtpHost = properties.getProperty(TestPropertyKey.TEST_EMAIL_SMTP_HOST);
        final String smtpFrom = properties.getProperty(TestPropertyKey.TEST_EMAIL_SMTP_FROM);
        final EmailGlobalConfigEntity emailGlobalConfigEntity = new EmailGlobalConfigEntity(smtpHost, null, null, null, null, null, null, smtpFrom, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        emailGlobalRepository.save(emailGlobalConfigEntity);
    }

    @Override
    public void cleanDistributionRepository() {
        distributionRepository.deleteAll();
    }

    @Override
    public MockEmailRestModel getMockRestModelUtil() {
        return new MockEmailRestModel();
    }

    @Override
    public ChannelDescriptor getDescriptor() {
        return emailDescriptor;
    }

}

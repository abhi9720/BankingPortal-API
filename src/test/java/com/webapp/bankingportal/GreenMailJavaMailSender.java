package com.webapp.bankingportal;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

public class GreenMailJavaMailSender extends JavaMailSenderImpl {

    private static final String HEADER_MESSAGE_ID = "Message-ID";

    @NonNull
    private static final GreenMail greenMail;

    @NonNull
    private static final Session session;

    static {
        greenMail = new GreenMail(ServerSetupTest.SMTP.dynamicPort());
        greenMail.start();
        session = GreenMailUtil.getSession(greenMail.getSmtp().getServerSetup());
    }

    @Override
    public synchronized @NonNull Session getSession() {
        return session;
    }

    @Override
    protected void doSend(@NonNull MimeMessage[] mimeMessages, @Nullable Object[] originalMessages)
            throws MailException {
        Map<Object, Exception> failedMessages = new LinkedHashMap<>();
        Transport transport = null;

        try {
            for (int i = 0; i < mimeMessages.length; i++) {

                // Check transport connection first...
                if (transport == null || !transport.isConnected()) {
                    if (transport != null) {
                        try {
                            transport.close();
                        } catch (Exception ex) {
                            // Ignore - we're reconnecting anyway
                        }
                        transport = null;
                    }
                    try {
                        transport = connectTransport();
                    } catch (AuthenticationFailedException ex) {
                        throw new MailAuthenticationException(ex);
                    } catch (Exception ex) {
                        // Effectively, all remaining messages failed...
                        for (int j = i; j < mimeMessages.length; j++) {
                            Object original = (originalMessages != null ? originalMessages[j] : mimeMessages[j]);
                            failedMessages.put(original, ex);
                        }
                        throw new MailSendException("Mail server connection failed", ex, failedMessages);
                    }
                }

                // Send message via current transport...
                MimeMessage mimeMessage = mimeMessages[i];
                try {
                    if (mimeMessage.getSentDate() == null) {
                        mimeMessage.setSentDate(new Date());
                    }
                    String messageId = mimeMessage.getMessageID();
                    mimeMessage.saveChanges();
                    if (messageId != null) {
                        // Preserve explicitly specified message id...
                        mimeMessage.setHeader(HEADER_MESSAGE_ID, messageId);
                    }
                    GreenMailUtil.sendMimeMessage(mimeMessage);
                } catch (Exception ex) {
                    Object original = (originalMessages != null ? originalMessages[i] : mimeMessage);
                    failedMessages.put(original, ex);
                }
            }
        } finally {
            try {
                if (transport != null) {
                    transport.close();
                }
            } catch (Exception ex) {
                if (!failedMessages.isEmpty()) {
                    throw new MailSendException("Failed to close server connection after message failures", ex,
                            failedMessages);
                } else {
                    throw new MailSendException("Failed to close server connection after message sending", ex);
                }
            }
        }

        if (!failedMessages.isEmpty()) {
            throw new MailSendException(failedMessages);
        }
    }

    public static MimeMessage[] getReceivedMessages() {
        return greenMail.getReceivedMessages();
    }

    public static MimeMessage[] getReceivedMessagesForDomain(String domain) {
        return greenMail.getReceivedMessagesForDomain(domain);
    }

}

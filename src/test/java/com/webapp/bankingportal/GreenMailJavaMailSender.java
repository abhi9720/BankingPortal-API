package com.webapp.bankingportal;

import java.util.Date;
import java.util.LinkedHashMap;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import lombok.val;

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

        val failedMessages = new LinkedHashMap<Object, Exception>();

        try (val transport = connectTransport()) {

            for (int i = 0; i < mimeMessages.length; i++) {
                val mimeMessage = mimeMessages[i];
                sendMessage(mimeMessage, originalMessages, i, failedMessages);
            }

        } catch (AuthenticationFailedException ex) {
            throw new MailAuthenticationException(ex);

        } catch (MessagingException ex) {
            handleSendException(ex, mimeMessages, originalMessages, failedMessages);
        }

        if (!failedMessages.isEmpty()) {
            throw new MailSendException(failedMessages);
        }
    }

    private void sendMessage(
            MimeMessage mimeMessage, Object[] originalMessages,
            int index,
            LinkedHashMap<Object, Exception> failedMessages) {

        try {
            prepareMimeMessage(mimeMessage);
            GreenMailUtil.sendMimeMessage(mimeMessage);

        } catch (MessagingException ex) {
            Object original = mimeMessage;
            if (originalMessages != null) {
                original = originalMessages[index];
            }
            failedMessages.put(original, ex);
        }
    }

    private void prepareMimeMessage(MimeMessage mimeMessage) throws MessagingException {
        if (mimeMessage.getSentDate() == null) {
            mimeMessage.setSentDate(new Date());
        }
        mimeMessage.saveChanges();

        val messageId = mimeMessage.getMessageID();
        if (messageId != null) {
            mimeMessage.setHeader(HEADER_MESSAGE_ID, messageId);
        }
    }

    private void handleSendException(
            Exception ex,
            MimeMessage[] mimeMessages,
            Object[] originalMessages,
            LinkedHashMap<Object, Exception> failedMessages) throws MailSendException {

        for (int j = 0; j < mimeMessages.length; j++) {
            Object original = mimeMessages[j];
            if (originalMessages != null) {
                original = originalMessages[j];
            }
            failedMessages.put(original, ex);
        }

        throw new MailSendException("Mail server connection failed", ex, failedMessages);
    }

    public static MimeMessage[] getReceivedMessages() {
        return greenMail.getReceivedMessages();
    }

    public static MimeMessage[] getReceivedMessagesForDomain(String domain) {
        return greenMail.getReceivedMessagesForDomain(domain);
    }

}

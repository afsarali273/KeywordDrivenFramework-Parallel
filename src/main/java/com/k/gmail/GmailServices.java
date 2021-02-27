package com.k.gmail;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import org.json.JSONObject;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class GmailServices {
    private static final String APPLICATION_NAME = "KeywordDriven-Automation Framework";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String user = "me";
    private static Gmail service = null;

    private static String credentialPath =
            System.getProperty("user.dir") + "/src/main/resources/gmail/credentials.json";
    private static File filePath = new File(credentialPath);
    private static final String REFRESH_TOKEN =
            "YOUR_REFRESH_TOKEN";
    private static final String CLIENT_ID =
            "YOUR_CLIENT_ID";
    private static final String CLIENT_SECRETE = "YOUR_CLIENT_SECRETE";
    private static final String DEFAULT_EMAIL_LIST = "YOUR_EMAIL";
    private String to;
    private String subject;
    private String html;

    private static GmailServices instance = new GmailServices();


    private GmailServices() {}

    public static GmailServices getInstance() {
        if (service == null) startGmailService();
        return instance;
    }

    private static Gmail startGmailService() {
        try {
            InputStream in = new FileInputStream(filePath);
            GoogleClientSecrets clientSecrets =
                    GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            Credential authorize =
                    new GoogleCredential.Builder()
                            .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                            .setJsonFactory(JSON_FACTORY)
                            .setClientSecrets(
                                    clientSecrets.getDetails().getClientId().toString(),
                                    clientSecrets.getDetails().getClientSecret().toString())
                            .build()
                            .setAccessToken(getAccessToken())
                            .setRefreshToken(REFRESH_TOKEN);

            // Create Gmail service
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            service =
                    new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, authorize)
                            .setApplicationName(GmailServices.APPLICATION_NAME)
                            .build();
        } catch (Exception e) {
        }
        return service;
    }

    private static String getAccessToken() {

        try {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("grant_type", "refresh_token");
            params.put("client_id", CLIENT_ID);
            params.put("client_secret", CLIENT_SECRETE);
            params.put("refresh_token", REFRESH_TOKEN);

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) {
                    postData.append('&');
                }
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            URL url = new URL("https://accounts.google.com/o/oauth2/token");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.getOutputStream().write(postDataBytes);

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuffer buffer = new StringBuffer();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                buffer.append(line);
            }
            JSONObject json = new JSONObject(buffer.toString());
            String accessToken = json.getString("access_token");
            return accessToken;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // Read Gmail Inbox
    public void getMailBody(String searchString) throws IOException {
        // Access Gmail inbox
        Gmail.Users.Messages.List request = service.users().messages().list(user).setQ(searchString);
        ListMessagesResponse messagesResponse = request.execute();
        request.setPageToken(messagesResponse.getNextPageToken());
        // Get ID of the email you are looking for
        String messageId = messagesResponse.getMessages().get(0).getId();
        Message message = service.users().messages().get(user, messageId).execute();
        // Print email body
        String emailBody =
                StringUtils.newStringUtf8(
                        Base64.decodeBase64(message.getPayload().getParts().get(0).getBody().getData()));

        System.out.println("Email body : " + emailBody);
    }

    // Create Email With Attachments
    private MimeMessage createEmailWithAttachment(
            String to, String subject, String bodyText, File file)
            throws MessagingException, IOException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(user)); // me
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to)); //
        email.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(bodyText, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        mimeBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(file);

        mimeBodyPart.setDataHandler(new DataHandler(source));
        mimeBodyPart.setFileName(file.getName());

        multipart.addBodyPart(mimeBodyPart);
        email.setContent(multipart, "text/html");
        return email;
    }

    private Message createMessageWithEmail(MimeMessage email) throws MessagingException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        email.writeTo(baos);
        String encodedEmail = Base64.encodeBase64URLSafeString(baos.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    private MimeMessage createHTMLMessageForEmail() throws AddressException, MessagingException {

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(user));

        // For Multiple Email with comma separated ...
        String[] split = to.split(",");
        for (int i = 0; i < split.length; i++) {
            email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(split[i]));
        }
        email.setSubject(subject);

        Multipart multiPart = new MimeMultipart("mixed");
        // HTML Body
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(html, "text/html; charset=utf-8");
        multiPart.addBodyPart(htmlPart, 0);
        // Attachments ...
        //        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        //        DataSource source = new FileDataSource(new File(htmlReportPath));
        //
        //        mimeBodyPart.setDataHandler(new DataHandler(source));
        //        mimeBodyPart.setFileName("results.html");
        //        multiPart.addBodyPart(mimeBodyPart,1);

        email.setContent(multiPart);
        return email;
    }

    // Send the Email
    public void sendEmailReport() {
        try {
            sendEmailWithHTMLBodyAndAttachment();

        } catch (Exception e) {
            System.out.println("There is an error while sending email report.");
        }
    }

    // Send Email With HTML Body and Attachment
    private void sendEmailWithHTMLBodyAndAttachment() {
        try {
            //Set Html here
            // Setters
//            if (html == null) setHtml("No Report found");
//            else setHtml(html);
            if (to == null) setTo(DEFAULT_EMAIL_LIST);
            if (subject == null)
                setSubject(
                        getDateTime()
                                + " | "
                                + getEnv().toUpperCase()
                                + " | Automation Test Report");

            MimeMessage Mimemessage = createHTMLMessageForEmail();
            Message message = createMessageWithEmail(Mimemessage);
            message = service.users().messages().send(user, message).execute();

            System.out.println("Message id: " + message.getId());
            System.out.println(message.toPrettyString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ======= Getter and Setters ===========

    public void setTo(String to) {
        this.to = to;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setHtml(String html) {
        this.html = "<html>\n"+html+"\n</html>";
    }


    private static boolean isRemoteExecution() {
        String env = getEnv();
        System.out.println(" Environment :  " + env);
        return !env.toLowerCase().contains("local");
    }

    private static String getDateTime() {
        SimpleDateFormat dateTimeInGMT = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
        // Setting the time zone
        dateTimeInGMT.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return dateTimeInGMT.format(new Date());
    }

    public static String getEnv() {
        String profile = System.getProperty("spring.profiles.active");
        String env = System.getenv("env");
        if (env != null) return env;
        if (profile != null) return profile;

        return "Local";
    }

    public static void main(String[] args) {
        GmailServices.getInstance().sendEmailWithHTMLBodyAndAttachment();
    }





}

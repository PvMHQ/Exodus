package Model.Commandsets;

import Model.Functionality.EmbedGenerator;
import Model.Functionality.Functions;
import Model.Main;
import Model.objects.Leaver;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Sheets {
    private static com.google.api.services.sheets.v4.Sheets sheetsService;
    private static String APPLICATION_NAME = "Google sheet";

    //!TODO Add spreadsheet id
    private static String leaversList = "1bDiujJbe24zGctXdz9-bS6V99BrtsAa-1B5RDRG-vjo";
    private static String MentorLearnerList = "1wf54oX9GgTfpPe7IwamMCJeuHrASxl2FHqgl44O2SBY"; //actual sheet
    private static String WantingToLearnList = "1BRYVUTIW875U4B5WHmwafd41qh857O11ko0_NJJHy0k";

    private static Credential authorize() throws IOException, GeneralSecurityException {
        InputStream in = Main.class.getResourceAsStream("/credentials.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(), new InputStreamReader(in)
        );

        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance()
                , clientSecrets, scopes)

                 .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver())
                .authorize("user");
        return credential;
    }
    public static com.google.api.services.sheets.v4.Sheets getSheetsService() throws IOException, GeneralSecurityException {
        Credential credential = authorize();
        return new com.google.api.services.sheets.v4.Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),JacksonFactory.getDefaultInstance(),credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    public static void setHmTob(String rsn, String kc, String verzik) throws GeneralSecurityException, IOException{
        sheetsService = getSheetsService();
        String range ="TOB HM!A3:C16";
        String pubcol = "B";
        String addSpot ="";
        ValueRange response = sheetsService.spreadsheets().values()
                .get(MentorLearnerList,range).execute();
        List<List<Object>> Values = response.getValues();
        if(Values == null || Values.isEmpty()){

        } else {

            for (List row : Values) {
                if (row.isEmpty()) {
                    addSpot = "TOB HM!A" + (Values.indexOf(row) + 3) + ":C" + (Values.indexOf(row) + 3);
                    break;
                }
            }
        }


        ValueRange appendBody = new ValueRange()
                .setValues(Arrays.asList(
                        Arrays.asList(rsn, kc, verzik)

                ));
        if(addSpot.equals("")){
            AppendValuesResponse appendResult = sheetsService.spreadsheets().values()
                    .append(MentorLearnerList, range, appendBody)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .setIncludeValuesInResponse(true)
                    .execute();


        }else {
            AppendValuesResponse appendResult = sheetsService.spreadsheets().values()
                    .append(MentorLearnerList, addSpot, appendBody)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("OVERWRITE")
                    .setIncludeValuesInResponse(true)
                    .execute();
        }
        addwantingtoLearn(rsn,verzik,pubcol);
    }


    public static void addJoiner(Member member,String way) throws GeneralSecurityException, IOException {
        Functions functions = new Functions();
        String username = member.getDisplayName();
        String DiscordID  =  functions.removeSymbols(member.getId().asString());
        if (way.equals("")){
            way = "N/A";
        }
        sheetsService = getSheetsService();
        String range ="JoinLog!A2:C4";
        ValueRange appendBody = new ValueRange()
                .setValues(Arrays.asList(
                        Arrays.asList(username,DiscordID, way )
                ));

        AppendValuesResponse appendResult = sheetsService.spreadsheets().values()
                .append(leaversList, range, appendBody)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .setIncludeValuesInResponse(true)
                .execute();
    }
    public static void updateJoiner(Member member, String way)  {
        Functions functions = new Functions();
        int row= 0;
        String DiscordID = functions.removeSymbols(member.getId().asString());
        String username = member.getDisplayName();
        try {
            row = getJoiner(DiscordID);

        String updaterange = "JoinLog!A"+row+":C"+row;
        ValueRange appendBody = new ValueRange()
                .setValues(Arrays.asList(
                        Arrays.asList(username,DiscordID, way)));

        UpdateValuesResponse appendResult = sheetsService.spreadsheets().values()
                .update(leaversList, updaterange, appendBody)
                .setValueInputOption("USER_ENTERED")
                .setIncludeValuesInResponse(true)
                .execute();

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e ){
            e.printStackTrace();
        }
    }
    private static int getJoiner(String Discordid) throws GeneralSecurityException, IOException {
        int rij = 0;
        sheetsService = getSheetsService();
        String range ="joinlog!B2:B";

        ValueRange response = sheetsService.spreadsheets().values()
                .get(leaversList,range).execute();
        List<List<Object>> Values = response.getValues();

        if (Values == null || Values.isEmpty()){
            return 0;
        } else {
            rij = 2;
            for (List row : Values){

                if (row.get(0).equals(Discordid)){
                    break;
                }
                rij++;
            }
        }
        return rij;


    }
    public static void addleaver(MemberLeaveEvent event) throws GeneralSecurityException, IOException {
        Functions functions = new Functions();
        try{
        String DiscordID  =  functions.removeSymbols(event.getUser().getId().asString());
        String rsn;
        boolean hasnickname = event.getMember().get().getNickname().isPresent();
        if (hasnickname){
            rsn = event.getMember().get().getNickname().get();
        }else {
            rsn= event.getUser().getUsername().toString();
        }
        String role = event.getMember().get().getHighestRole().block().getName();

            Date date = new Date();

            sheetsService = getSheetsService();
            String range ="leavetracker!A3:D";

            int times = 0;
            Leaver l = userHasLeftBefore(DiscordID);
            if (l != null){
                times = l.getTimes();
            }
            ValueRange appendBody = new ValueRange()
                    .setValues(Arrays.asList(
                            Arrays.asList(rsn, DiscordID, role, (times+1))
                    ));

            if (times == 0){

            AppendValuesResponse appendResult = sheetsService.spreadsheets().values()
                    .append(leaversList, range, appendBody)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .setIncludeValuesInResponse(true)
                    .execute();
            }
            else {
                range = "leavetracker!A"+(l.getRow())+":D"+(l.getRow());
                UpdateValuesResponse appendResult = sheetsService.spreadsheets().values()
                        .update(leaversList, range, appendBody)
                        .setValueInputOption("USER_ENTERED")
                        .setIncludeValuesInResponse(true)
                        .execute();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        
    }
    public static Leaver userHasLeftBefore(String discordID) throws GeneralSecurityException, IOException {
        Leaver leaver = null;
        sheetsService = getSheetsService();
        String range ="leavetracker!A2:D";
        String pubcol = "A";

        ValueRange response = sheetsService.spreadsheets().values()
                .get(leaversList,range).execute();
        List<List<Object>> Values = response.getValues();

        if (Values == null || Values.isEmpty()){
            return null;
        } else {
            int rij = 2;
            for (List row : Values){

                if (row.get(1).equals(discordID)){
                  leaver = new Leaver(row.get(0).toString(),row.get(1).toString(),row.get(2).toString(),Integer.parseInt(row.get(3).toString()), rij);

                  break;
                }
                rij++;
            }
        }
        return leaver;

    }
    public static String userHasLeftBeforeRole(String discordID) throws GeneralSecurityException, IOException {
        String role = "";
        sheetsService = getSheetsService();
        String range ="leavetracker!A2:D";
        String pubcol = "A";

        ValueRange response = sheetsService.spreadsheets().values()
                .get(leaversList,range).execute();
        List<List<Object>> Values = response.getValues();

        if (Values == null || Values.isEmpty()){
            return role;
        } else {
            for (List row : Values){
                if (row.get(1).equals(discordID)){
                  role = row.get(2).toString();

                }
            }
        }

        return role;
    }
    public static void setRegTob(String rsn, String kc, String verzik) throws GeneralSecurityException, IOException {
        sheetsService = getSheetsService();
        String range ="TOB!A3:C16";
        String pubcol = "A";
        String addSpot ="";
        ValueRange response = sheetsService.spreadsheets().values()
                .get(MentorLearnerList,range).execute();
        List<List<Object>> Values = response.getValues();
        if(Values == null || Values.isEmpty()){

        } else {

            for (List row : Values) {
                if (row.isEmpty()) {
                    addSpot = "TOB!A" + (Values.indexOf(row) + 3) + ":C" + (Values.indexOf(row) + 3);
                    break;
                }
            }
        }


            ValueRange appendBody = new ValueRange()
                    .setValues(Arrays.asList(
                            Arrays.asList(rsn, kc, verzik)

                    ));
            if(addSpot.equals("")){
                AppendValuesResponse appendResult = sheetsService.spreadsheets().values()
                        .append(MentorLearnerList, range, appendBody)
                        .setValueInputOption("USER_ENTERED")
                        .setInsertDataOption("INSERT_ROWS")
                        .setIncludeValuesInResponse(true)
                        .execute();


            }else {
            AppendValuesResponse appendResult = sheetsService.spreadsheets().values()
                    .append(MentorLearnerList, addSpot, appendBody)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("OVERWRITE")
                    .setIncludeValuesInResponse(true)
                    .execute();
            }
        addwantingtoLearn(rsn,verzik,pubcol);

    }
    public static void readtest(MessageCreateEvent event)throws GeneralSecurityException, IOException{
        sheetsService = getSheetsService();
        String range ="TOB!A3:C16";
        ValueRange response = sheetsService.spreadsheets().values()
                .get(MentorLearnerList,range).execute();
        List<List<Object>> Values = response.getValues();
        if(Values == null || Values.isEmpty()){

        } else {
            EmbedGenerator.pointAddEmbed(event.getMessage().getAuthorAsMember().block().getPrivateChannel().block(),"Values found","sheets operational");
        }
    }
    private static void addwantingtoLearn(String rsn, String verzik, String pubrange) throws IOException {
        String range = "Sheet1!"+pubrange+"4:"+pubrange+"30";
        String addSpot= "";
        Boolean recolor = false;
        if (verzik.equals("Yes")){
            recolor = true;
        }
        ValueRange response = sheetsService.spreadsheets().values()
                .get(WantingToLearnList,range).execute();
        List<List<Object>> Values = response.getValues();
        if(Values == null || Values.isEmpty()){

        } else {

            for (List row : Values) {
                if (row.isEmpty()) {

                    addSpot = "sheet1!"+pubrange + (Values.indexOf(row) + 4) + ":"+pubrange + (Values.indexOf(row) + 4);
                    break;
                } else {

                }
            }
        }
        ValueRange appendBody = new ValueRange()
                .setValues(Arrays.asList(
                        Arrays.asList(rsn)

                ));

        if(addSpot.equals("")){
            AppendValuesResponse appendResult = sheetsService.spreadsheets().values()
                    .append(WantingToLearnList, range, appendBody)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")

                    .setIncludeValuesInResponse(true)
                    .execute();


        }else {
            AppendValuesResponse appendResult = sheetsService.spreadsheets().values()
                    .append(WantingToLearnList, addSpot, appendBody)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("OVERWRITE")
                    .setIncludeValuesInResponse(true)
                    .execute();
        }

    }
    public static void setRegCox(String rsn, String kc)throws GeneralSecurityException, IOException {
        sheetsService = getSheetsService();
        String range ="COX!A3:F16";
        String addSpot ="";
        String pubcol = "D";

        ValueRange response = sheetsService.spreadsheets().values()
                .get(MentorLearnerList,range).execute();
        List<List<Object>> Values = response.getValues();
        if(Values == null || Values.isEmpty()){

        } else {

            for (List row : Values) {
                if (row.isEmpty()) {

                    addSpot = "COX!A" + (Values.indexOf(row) + 3) + ":F" + (Values.indexOf(row) + 3);
                    break;
                }
            }
        }
        addwantingtoLearn(rsn,"No",pubcol);


        ValueRange appendBody = new ValueRange()
                .setValues(Arrays.asList(
                        Arrays.asList(rsn,"","","","",kc)

                ));

        if(addSpot.equals("")){
            AppendValuesResponse appendResult = sheetsService.spreadsheets().values()
                    .append(MentorLearnerList, range, appendBody)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .setIncludeValuesInResponse(true)
                    .execute();


        }else {
            AppendValuesResponse appendResult = sheetsService.spreadsheets().values()
                    .append(MentorLearnerList, addSpot, appendBody)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("OVERWRITE")
                    .setIncludeValuesInResponse(true)
                    .execute();
        }
    }
    public static void setCmCox(String rsn, String kc)throws GeneralSecurityException, IOException {
        sheetsService = getSheetsService();
        String range ="COX CM!A3:F6";
        String addSpot ="";
        String pubcol = "E";
        ValueRange response = sheetsService.spreadsheets().values()
                .get(MentorLearnerList,range).execute();
        List<List<Object>> Values = response.getValues();
        if(Values == null || Values.isEmpty()){

        } else {

            for (List row : Values) {
                if (row.isEmpty()) {

                    addSpot = "COX CM!A" + (Values.indexOf(row) + 3) + ":F" + (Values.indexOf(row) + 3);
                    break;
                }
            }
        }
        addwantingtoLearn(rsn,"No",pubcol);

        ValueRange appendBody = new ValueRange()
                .setValues(Arrays.asList(
                        Arrays.asList(rsn,"","","","",kc)

                ));

        if(addSpot.equals("")){
            AppendValuesResponse appendResult = sheetsService.spreadsheets().values()
                    .append(MentorLearnerList, range, appendBody)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .setIncludeValuesInResponse(true)
                    .execute();


        }else {
            AppendValuesResponse appendResult = sheetsService.spreadsheets().values()
                    .append(MentorLearnerList, addSpot, appendBody)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("OVERWRITE")
                    .setIncludeValuesInResponse(true)
                    .execute();
        }
    }



}

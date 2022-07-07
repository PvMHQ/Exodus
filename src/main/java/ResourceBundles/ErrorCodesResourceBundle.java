package ResourceBundles;

import java.util.ListResourceBundle;

public class ErrorCodesResourceBundle extends ListResourceBundle {

        @Override
        protected Object[][] getContents() {
            return new Object[][]{

                    /////////////////////
                    //      General    //
                    /////////////////////

                    {"code", "ERROR:"},
                    {"SQL1","Code DB1: \n Error with query/update -> caught exception -> report to bot devs and mention what function you were using"},


                    ////////////////////////
                    //     Teampoints     //
                    ////////////////////////

                    // TPS1 : Database at TeamPoints -> removeAllTeams  -> failed to remove all teams
                    {"TPS1", "Code TPS1: \n Unable to remove teams from the database! \n Try again later or contact the bot devs"},
                    //TPS2 : DB of Teampoints -> createTeam -> failed to create a team
                    {"TPS2", "Code TPS2: \nUnable to create a team due to database errors!\n contact a dev or try again later"},
                    //TPS3 : DB of Teampoints -> adding player to team -> couldn't add player
                    {"TPS3", "Code TPS3: \nUnable to add player to a team due to database errors!\n contact a dev or try again later"},
                    //TPS4 : Db didn't add points to player in Teampoints due to database
                    {"TPS4", "Code TPS4: \nPoints not added, possibly because of a database errors\n Try again later or contact a bot dev"},
                    //TPS5: Teampoints -> problem with the input, reading it gave an exception
                    {"TPS5", "Code TPS5: \nWrong input given: Expecting a text"},
                    //TPS6: DB of Teampoints unable to find teams in a guild
                    {"TPS6", "Code TPS6: \n Couldn't find the teams of the server \n Try again later"},
                    //TPS7: couldn't get the members of a team in DBTeamPoints
                    {"TPS7", "Code TPS7: \n Unable to find members of the team \n Try again later"}

            };
        }


}

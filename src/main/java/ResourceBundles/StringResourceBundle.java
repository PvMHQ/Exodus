package ResourceBundles;



import java.util.ListResourceBundle;

public class StringResourceBundle extends ListResourceBundle {

    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"prefix","."},
                {"inv user title","Invalid user!"},
                {"no perm txt","You do not have permission to use this command."},
                {"bugreport","Like all programmers know Bugs appear. If one slips through and you feel like you found one please report it here by using the\n **.bugreport** command. this will notify the devs. \n" +
                             "**WARNING:**  Devs may contact you for a more detailed description of the problem \n" +
                            "\n" +
                            " if an error occurs provide the errorcode in the report as this will help situate the problem\n" },
                {"feedback","Are you enjoying the bot or not at all, feel free to let us know with the \n**.feedback** command we will try to improve it going into the future."},
                {"suggestions","do you feel like there is a feature lacking? feel free to submit your idea's to us by using the\n" +
                        "**.suggest.**\n" +
                        " **WARNING:** devs may contact you regarding it for a more detailed description of it\n"},
                {"support","Are you having trouble with the bot or regarding something in the server? like setting it up the bot. you can contact our support team here using the\n" +
                        " **.support** command\n"},



        };
    }
}

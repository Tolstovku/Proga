package Client;

public class StoryTeller extends Script {
    StoryTeller(String storyname) {
        super(storyname);
    }

    @Override
    void tellTale() {
        System.out.println("Расскажу вам историю, которую мне рассказывал еще мой дедушка...");
        System.out.println("Я называю ее: \"" + getTaleName() + "\"");
    }
}

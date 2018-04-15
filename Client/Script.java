package Client;

public abstract class Script {
    private String taleName;
    abstract void tellTale();
    Script(String name) {
        taleName=name;
    }

    public String getTaleName() {
        return taleName;
    }

}

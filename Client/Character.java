package Client;

abstract public class Character {
    final String name;

    public Character(String name) {
        this.name = name;

    }

    @Override
    public String toString() {
        return name;
    }

}

package Client;

public class Forest extends Character implements Phrase {
    public Forest(String name) {
        super(name);
    }

    public void sayPhrase() {
        System.out.println(this + " ");
    }

    class Sounds extends Character implements Phrase {
        public Sounds(String name) {
            super(name);
        }

        public void sayPhrase() {
            System.out.println(this + " из леса кажется, говорят Винни-Пуху: \"Не слушай Кролика,  Слушай меня!\"");
        }
    }
}

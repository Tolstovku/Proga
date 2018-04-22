package Client;

public class Rabbit extends Character implements Sight, Phrase {
    Rabbit(String name){
        super(name);
    }

    @Override
    public void sayPhrase() {
        System.out.println(this + " серьезно спрашивает: \"Ты понимаешь, что я имею в виду, Пятачок?");
    }

    @Override
    public void lookAtRiver() {
        System.out.println(this + " смотрит вниз на реку");
    }


}

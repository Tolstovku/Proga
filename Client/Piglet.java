package Client;

public class Piglet extends Character implements Sight {
    Piglet(String name) {
        super(name);
    }

    @Override
    public void lookAtRiver() {
        System.out.println(this + " смотрит вниз на реку.");
    }

    public void nod() {
        System.out.println(this + " кивает.");
    }


}

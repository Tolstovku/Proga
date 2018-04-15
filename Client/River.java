package Client;

public class River extends Character implements Phrase {
    private boolean isCalm = true;
    private boolean feelsGood = true;
    River(String name){
        super(name);
    }

    @Override
    public void sayPhrase() {

        System.out.println(this + " ничего не говорит");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        River river = (River) o;

        if (isCalm != river.isCalm) return false;
        return feelsGood == river.feelsGood;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isCalm ? 1 : 0);
        result = 31 * result + (feelsGood ? 1 : 0);
        return result;
    }
}

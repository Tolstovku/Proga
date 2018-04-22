package Client;

public class PoohBear extends Character implements Sight, Phrase, Breath {
    private boolean areEyesOpened = true;
    private boolean isComfortable = false;
    PoohBear(String name) {
        super(name);
    }

    @Override
    public void breathe() {
        System.out.println(this + " вздыхает.");
    }

    @Override
    public void sayPhrase() {
        breathe();
        eyesAction();
        System.out.println(this + " говорит : \"Ах\"");
        
;
        eyesAction();
        System.out.println(this + " говорит: \"Верно, верно!\"");
        
;
    }

    public void lookAtRiver(){
        System.out.println(this + " смотрит вниз на реку.");
    }

    public void setAreEyesOpened(boolean areThey) {
        this.areEyesOpened = areThey;
    }

    private void eyesAction() {
        if (areEyesOpened == true) {
            setAreEyesOpened(false);
            System.out.println(this + " закрывает глаза");
            
;
        }
        else {
            setAreEyesOpened(true);
            System.out.println(this + " открывает глаза");
            
;
        }
    }

    public void getComfy() {
        System.out.println(this + " занимает самую удобную позицию для того, чтобы не слушать Кролика");
        
;
        eyesAction();
        setComfortable(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PoohBear pooh_bear = (PoohBear) o;

        if (areEyesOpened != pooh_bear.areEyesOpened) return false;
        return isComfortable == pooh_bear.isComfortable;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (areEyesOpened ? 1 : 0);
        result = 31 * result + (isComfortable ? 1 : 0);
        return result;
    }

    public void setComfortable(boolean comfortable) {
        isComfortable = comfortable;
    }
}

package Client;

public class Kanga extends Character implements Phrase {



    private static boolean isCarryingRoo = false;

    public static void setIsCarryingRoo(boolean isCarryingRoo) {
        Kanga.isCarryingRoo = isCarryingRoo;
    }

    @Override
    public void sayPhrase() {
        class FirstPart {
            private String reply;
            private String pie = "Я приготовила вкуснейший вишневый пирог!\"";
            private String chores = "Мне нужно, чтобы ты помог мне прибраться дома!\"";
            private String sweater = "Мне нужно, чтобы ты померял свитер, который я тебе связала!\"";
            private int replyNumber = (int) (Math.random() * 3);

            {
                switch (replyNumber) {
                    case 0:
                        reply = pie;
                        break;
                    case 1:
                        reply = chores;
                        break;
                    case 2:
                        reply = sweater;
                        break;
                    default:
                }
            }

            public String getReply() {
                return reply;
            }
        }
        FirstPart phrase = new FirstPart();
        System.out.println(this + " говорит: \"Пошли домой, Ру! " + phrase.getReply());
        }
    @Override
    public String toString() {
        return name;

    }

    public Kanga(String name) {
        super(name);
    }

    public void takeRooUp() {
        if (isCarryingRoo == true) throw new IllegalRooLocation("Ру уже находится в сумке");
        isCarryingRoo = true;
        System.out.println(this + " взяла Ру в свою сумку.");
    }

    public void dropRoo() {
        if (isCarryingRoo == false) throw new IllegalRooLocation("Ру и так находится вне сумки");
        isCarryingRoo = false;
        System.out.println(this + " вытащила Ру из своей сумки.");
    }

    static class Roo extends Character {
        private boolean isTired =false;
        Roo(String name) {
            super(name);

        }

        public void jumpInSack() {
            if (isCarryingRoo == true) throw new IllegalRooLocation("Ру уже находится в сумке");
            isCarryingRoo = true;
            System.out.println(this + " запрыгнул в сумку Кенги");
        }

        public void jumpOutOfSack() {
            if (isCarryingRoo == false) throw new IllegalRooLocation("Ру и так находится вне сумки");
            isCarryingRoo = false;
            System.out.println(this + " выпрыгнул из сумки Кенги");
        }

        private void fallInLake() {
            System.out.println(this + " свалился в реку.");
            System.out.println(this + " выплыл на берег и отряхнулся.");
        }


        private void jumpAround() {
            System.out.println(this + " скачет туда-сюда.");
        }

        public void haveFun() throws TiredRooException {
            for (int i = 0; i<2; i++) {
                jumpAround();
                if (Math.random() > 0.96) {
                    isTired = true;
                    throw new TiredRooException("Крошка Ру устал.");
                }
                fallInLake();
            }
        }


        @Override
        public String toString() {
            return name;
        }

    }
}

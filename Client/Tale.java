package Client;
import Common.FallingInRiver;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Tale extends Script {
    ConcurrentHashMap<Integer, FallingInRiver> falls;

    Tale(ConcurrentHashMap<Integer, FallingInRiver> map) {
        super("История про Винни-Пуха");
        falls = map;
    }



    @Override
    void tellTale() {
        Kanga.setIsCarryingRoo(false);
        // Создание обьектов действующих лиц
        PoohBear pooh = new PoohBear("Винни-Пух");
        Piglet pig = new Piglet("Пятачок");
        Rabbit rab = new Rabbit("Кролик");
        River riv = new River("Река");
        Forest les = new Forest("Лес");
        Forest.Sounds sounds = les.new Sounds("Звуки");
        Kanga kanga = new Kanga("Кенга");
        Kanga.Roo roo = new Kanga.Roo("Крошка Ру");
        //Определение времени дня
        Daytime daytime;
        daytime = Daytime.random();


        //Вступление
        System.out.println(daytime);
        try {
            roo.haveFun();
        } catch (TiredRooException ex) {
            System.out.println(ex.getMessage());
        }
        new Character("Кто-то") {
            {
                System.out.println(this + " показался из-за леса...");
            }
        };
        System.out.println("Да это же " + kanga + "!");
        kanga.sayPhrase();
        if (Math.random() > 0.5) roo.jumpInSack();
        else kanga.takeRooUp();

        System.out.println(kanga + " и " + roo + " ушли домой.");
        System.out.println(pooh + ", " + pig + " и " + rab + " сидят у парадной двери дома " + pooh + "a.");

        pooh.lookAtRiver();


        rab.lookAtRiver();


        pig.lookAtRiver();


        riv.sayPhrase();


        sounds.sayPhrase();


        pooh.getComfy();


        //Основная часть случайных событий
        for (int i = eventNumber(); i < 15; i++) {
            fallingTimerTick(falls);
            System.out.println(rab + " что-то говорит...");


            if (Math.random() > 0.66) {
                rab.sayPhrase();


                pig.nod();


            }
            if (Math.random() > 0.66) pooh.sayPhrase();


        }

        //Заключение
        System.out.println("Ребята попрощались и пошли каждый к себе домой!");

    }

    //Генаратор числа событий
    private int eventNumber() {
        return (int) (Math.random() * 11);
    }

    public void fallingTimerTick(ConcurrentHashMap<Integer, FallingInRiver> map) {
        Set entrySet = map.entrySet();
        Iterator<Map.Entry> iter = entrySet.iterator();
        while (iter.hasNext()) {
            FallingInRiver dude = (FallingInRiver) iter.next().getValue();
            //dude.tick();
        }
    }


}
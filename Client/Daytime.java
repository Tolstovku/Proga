package Client;

public enum Daytime {
    MORNING("Солнечное утро."), MIDDAY("Солнечный полдень."), EVENING("Прекрасный вечер.");

    private final String rusName;

    Daytime(String rus) {
        rusName = rus;
    }

    @Override
    public String toString() {
        return rusName;
    }

    public static Daytime random() {
        return Daytime.values()[(int) (Math.random() * (Daytime.values().length))];
    }
}


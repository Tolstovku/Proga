package Common.Resources;

import java.util.ListResourceBundle;

public class Resource_nl extends ListResourceBundle {
    private static final Object[][] contents =
            {
                    {"id", "ID"},
                    {"name", "Naam"},
                    {"splash", "Plons"},
                    {"depth", "Diepte"},
                    {"color", "Kleur"},
                    {"x", "X-coördinaat"},
                    {"y", "Y-coördinaat"},
                    {"add", "Toevoegen item"},
                    {"remove", "Item verwijderen"},
                    {"removeLower", "Verwijder minder belangrijke"},
                    {"control", "Beheer"},
                    {"import", "Importeren"},
                    {"save", "Opslaan"},
                    {"connections", "Verbindingen"},
                    {"ip", "IP-adres"},
                    {"port", "de Haven"},
                    {"ban", "Ban"},
                    {"client", "Opdrachtgever"},
                    {"server", "Server"},
                    {"minX", "Minimum X"},
                    {"maxX", "Maximum X"},
                    {"minY", "Minimum Y"},
                    {"maxY", "Maximum Y"},
                    {"minSplash", "Min. plons"},
                    {"maxSplash", "Max. plons"},
                    {"minDepth", "Min. diepte"},
                    {"maxDepth", "Max. diepte"},
                    {"orange", "Oranje"},
                    {"blue", "Blauw"},
                    {"red", "Rode"},
                    {"yellow", "Geel"},
                    {"start", "Start"},
                    {"stop", "Stop"},
                    {"update", "Update"},
                    {"keyExists", "Een item met dezelfde sleutel bestaat al"},
                    {"added", "Element toegevoegd"},
                    {"checkData", "Object aanvulling Fout. Controleer de invoer"},
                    {"imported", "Collectie geïmporteerd"},
                    {"fileNotFound", "Bestand niet gevonden"},
                    {"removedOne", "Item verwijderd"},
                    {"noSuchKey", "Er is geen element met deze sleutel in de collectie"},
                    {"wrongFormat", "Verkeerde sleutel formaat"},
                    {"removedMany", "Items verwijderd"},
                    {"noKeyLess", "De collectie heeft geen elementen met de toets minder geïntroduceerd"},
                    {"saved", "Collectie succesvol opgeslagen"},
                    {"enterPath", "Geef het pad op van import-bestand:"},
                    {"noColor", "geen kleur geselecteerd"},
                    {"error", "Fout"},
                    {"noFilteredObjects", "Geen objecten bevredigend filters."},
                    {"message", "Bericht"},
                    {"youUnbanned", "Je was unban"},
                    {"youBanned", "Je was ban"},
                    {"chooseLanguage", "Выберите язык"},
                    {"language","Taal"},
                    {"", ""},
                    {"", ""},
                    {"", ""},


            };

    @Override
    public Object[][] getContents() {
        return contents;
    }
}

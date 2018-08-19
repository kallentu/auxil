package com.auxil.auxil;

import android.app.Application;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

public class ParseApplication extends Application{
    final ArrayList<String> foodBanksCanada = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        indexFoodBanksCanada();
        parseFoodBanksCanada();
    }

    private void indexFoodBanksCanada() {
        // BC
        foodBanksCanada.add("https://www.foodbanksbc.com/find-a-food-bank/");
        // Alberta
        foodBanksCanada.add("https://foodbanksalberta.ca/food-banks/");
        // SK
        foodBanksCanada.add("http://skfoodbanks.ca/find-a-food-bank/");
        // Manitoba
        foodBanksCanada.add("http://mafb.ca/find-a-food-bank/");
        foodBanksCanada.add("http://mafb.ca/find-a-food-bank/selkirk/");
        foodBanksCanada.add("http://mafb.ca/find-a-food-bank/brandon/");
        foodBanksCanada.add("http://mafb.ca/find-a-food-bank/dauphin/");
        foodBanksCanada.add("http://mafb.ca/find-a-food-bank/teulon/");
        foodBanksCanada.add("http://mafb.ca/find-a-food-bank/gimli/");
        foodBanksCanada.add("http://mafb.ca/find-a-food-bank/beausejour/");
        foodBanksCanada.add("http://mafb.ca/find-a-food-bank/minnedosa/");
        foodBanksCanada.add("http://mafb.ca/find-a-food-bank/swan-river/");
        // ON
        foodBanksCanada.add("https://kerrstreet.com/");
        foodBanksCanada.add("http://georgetownbreadbasket.ca/");
        foodBanksCanada.add("https://www.dailybread.ca/about/contact-us/");
        foodBanksCanada.add("https://ccs4u.org/locations/");
        foodBanksCanada.add("https://northyorkharvest.com/contact-us/");
        foodBanksCanada.add("https://www.themississaugafoodbank.org/contact-us/");
        foodBanksCanada.add("http://knightstable.org/");
        foodBanksCanada.add("https://www.actonfoodshare.com/location-hours");
        // Quebec
        // TODO: Must go into each food bank id for Quebec
        foodBanksCanada.add("https://www.banquesalimentaires.org/en/our-network/network-members/");
        // New Brunswick
        foodBanksCanada.add("https://www.nbafb-abanb.net/en/default.php");
        /* P.E.I. Association of Food Banks
         33 Belmont St.
         Charlottetown, PEI C1A 7M8
         T: (902) 892-7092 | F: (902) 628-2054*/
        // Newfoundland
        foodBanksCanada.add("https://www.cfsa.nf.net/want-to-help");
    }

    private void parseFoodBanksCanada() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (String foodBankUrl : foodBanksCanada) {
                        Document doc = Jsoup.connect(foodBankUrl).get();
                        // Parse websites here
                        // TODO: Do a different parse for each site? Or find a universal parse.
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

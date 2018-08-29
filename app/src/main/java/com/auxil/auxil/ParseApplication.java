package com.auxil.auxil;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

/** Application which parses food banks once before launching app. */
public class ParseApplication extends Application{
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    /** List of food bank urls in Canada. */
    final ArrayList<String> foodBanksCanadaUrls = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this.getApplicationContext());
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        indexFoodBanksCanada();
        parseFoodBanksCanada();
    }

    /**
     * Adds all the urls of food banks of each province in Canada.
     * Note some places have more specific links.
     */
    private void indexFoodBanksCanada() {
        // BC
        foodBanksCanadaUrls.add("https://www.foodbanksbc.com/find-a-food-bank/");
        // Alberta
        foodBanksCanadaUrls.add("https://foodbanksalberta.ca/food-banks/");
        // SK
        foodBanksCanadaUrls.add("http://skfoodbanks.ca/find-a-food-bank/");
        // Manitoba
        foodBanksCanadaUrls.add("http://mafb.ca/find-a-food-bank/");
        foodBanksCanadaUrls.add("http://mafb.ca/find-a-food-bank/selkirk/");
        foodBanksCanadaUrls.add("http://mafb.ca/find-a-food-bank/brandon/");
        foodBanksCanadaUrls.add("http://mafb.ca/find-a-food-bank/dauphin/");
        foodBanksCanadaUrls.add("http://mafb.ca/find-a-food-bank/teulon/");
        foodBanksCanadaUrls.add("http://mafb.ca/find-a-food-bank/gimli/");
        foodBanksCanadaUrls.add("http://mafb.ca/find-a-food-bank/beausejour/");
        foodBanksCanadaUrls.add("http://mafb.ca/find-a-food-bank/minnedosa/");
        foodBanksCanadaUrls.add("http://mafb.ca/find-a-food-bank/swan-river/");
        // ON
        foodBanksCanadaUrls.add("https://kerrstreet.com/");
        foodBanksCanadaUrls.add("http://georgetownbreadbasket.ca/");
        foodBanksCanadaUrls.add("https://www.dailybread.ca/about/contact-us/");
        foodBanksCanadaUrls.add("https://ccs4u.org/locations/");
        foodBanksCanadaUrls.add("https://northyorkharvest.com/contact-us/");
        foodBanksCanadaUrls.add("https://www.themississaugafoodbank.org/contact-us/");
        foodBanksCanadaUrls.add("http://knightstable.org/");
        foodBanksCanadaUrls.add("https://www.actonfoodshare.com/location-hours");
        // Quebec
        // TODO: Must go into each food bank id for Quebec
        // https://www.banquesalimentaires.org/en/our-network/network-members/
        foodBanksCanadaUrls.add("http://www.centredebenevolats.com/custompage.aspx?ResourceId=0c841f2d-ad6c-4135-9f25-5c7d9b1b1b2e");
        foodBanksCanadaUrls.add("http://www.moissonbeauce.qc.ca/");
        foodBanksCanadaUrls.add("http://www.moissonlanaudiere.org/");
        foodBanksCanadaUrls.add("https://www.moissonlaurentides.org/");
        foodBanksCanadaUrls.add("http://www.moissonoutaouais.com/index.php/fr/");
        foodBanksCanadaUrls.add("http://www.moissonmontreal.org/");
        foodBanksCanadaUrls.add("http://www.moissonoutaouais.com/");
        foodBanksCanadaUrls.add("http://www.moissonquebec.com/");
        foodBanksCanadaUrls.add("http://www.moissonrimouski.org/");
        foodBanksCanadaUrls.add("http://www.moissonrivesud.org/");
        foodBanksCanadaUrls.add("https://www.facebook.com/Moisson-Saguenay-Lac-St-Jean-1803268196580684/");
        foodBanksCanadaUrls.add("https://www.moissonsudouest.org/");
        foodBanksCanadaUrls.add("https://www.facebook.com/moissonamqui/");
        foodBanksCanadaUrls.add("http://www.rbhrn.com/");
        foodBanksCanadaUrls.add("http://www.sos-depannage.org/");
        foodBanksCanadaUrls.add("https://www.facebook.com/MoissonKamouraska/");
        // New Brunswick
        foodBanksCanadaUrls.add("https://www.nbafb-abanb.net/en/default.php");
        /* P.E.I. Association of Food Banks
         33 Belmont St.
         Charlottetown, PEI C1A 7M8
         T: (902) 892-7092 | F: (902) 628-2054*/
        // Newfoundland
        foodBanksCanadaUrls.add("https://www.cfsa.nf.net/want-to-help");
    }

    /**
     * {@link Jsoup} parser for the food banks, added to individual
     * {@link FoodBank} data objects.
     */
    private void parseFoodBanksCanada() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (String foodBankUrl : foodBanksCanadaUrls) {
                        Document doc = Jsoup.connect(foodBankUrl).get();

                        String name = doc.title();

                        // Address finder has requirement of number as well as the word Canada.
                        // Assumes first entry for now
                        // TODO: find better way without first entry assumption
                        String address = doc
                                .select("p:matches(^[0-9]), p:matches((?i)Canada)")
                                .text();

                        //String website = foodBankUrl;

                        String phoneNumber = doc
                                .select(":matches(^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$)")
                                .text();

                        // Parse websites here
                        // TODO: Do a different parse for each site? Or find a universal parse.

                        // TODO: https://firebase.google.com/docs/database/admin/save-data
                        FoodBank foodBank = FoodBank.builder()
                                .setName(name)
                                .setAddress(address)
                                .setNumber(phoneNumber)
                                .setWebsite(foodBankUrl)
                                .build();

                        databaseReference.child(name).setValue(foodBank.toFirebaseValue());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

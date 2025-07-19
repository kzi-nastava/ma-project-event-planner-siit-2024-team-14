package com.example.eventplanner.ui.fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public abstract class FragmentTransition {

    public static void to(Fragment newFragment, FragmentActivity activity, int layoutViewID, boolean... addToBackstack)
    {
        /*
         * Fragmenti ne mogu da postoje nezavisno, njih 'lepimo' na aktivnost.
         * Zato, u metodu prosledjujemo referencu na aktivnost na koju 'lepimo' fragment.
         *'Lepljenje' fragmenata na aktivnosti ide u transaktivnom maniru.
         * Potrebno je da zapocnemo trasnakciju koristeci beginTransaction(),
         * nakon toga specificiramo kakvom animaciom ce doci do primene fragmenata
         * i nakon toga moze da pozovemu neku od metoda kojima menjamo ili dodajemo fragment na layout.
         * ako koristimo metodu replace, to znaci da sav prethodni sadrzaj elementa layout-a koji ima id
         * layoutViewID, menjamo sa kompletnim sadrzajem novog fragmenta.
         * addToBackstack specificira da li stavljamo fragment na backstack aktivnosti
         * */
        FragmentTransaction transaction = activity
                .getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(layoutViewID, newFragment);
        if(addToBackstack.length > 0 && addToBackstack[0]) transaction.addToBackStack(null);

        transaction.commit();
    }

}

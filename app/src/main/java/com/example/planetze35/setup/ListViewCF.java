package com.example.planetze35.setup;

import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListViewCF {
    final String[][] QUESTIONS = {
            {"Yes","No"},
            {"Gasoline","Diesel","Hybrid","Electric","I don't know"},
            {"Up to 5,000 km (3,000 miles)","5,000–10,000 km (3,000–6,000 miles)","10,000–15,000 km (6,000–9,000 miles)",
                    "15,000–20,000 km (9,000–12,000 miles)","20,000–25,000 km (12,000–15,000 miles)","More than 25,000 km (15,000 miles)"},
            {"Never","Occasionally (1-2 times/week)","Frequently (3-4 times/week)","Always (5+ times/week)"},
            {"Under 1 hour","1-3 hours","3-5 hours","5-10 hours","More than 10 hours"},
            {"None","1-2 flights","3-5 flights","6-10 flights","More than 10 flights"},
            {"None","1-2 flights","3-5 flights","6-10 flights","More than 10 flights"},
            {"Vegetarian","Vegan","Pescatarian (fish/seafood)","Meat-based (eat all types of animal products)"},
            {"Daily","Frequently (3-5 times/week)","Occasionally (1-2 times/week)","Never"},
            {"Never","Rarely","Occasionally","Frequently"},
            {"Detached house","Semi-detached house","Townhouse","Condo/Apartment","Other"},
            {"1","2","3-4","5 or more"},
            {"Under 1000 sq. ft.","1000-2000 sq. ft.","Over 2000 sq. ft."},
            {"Natural Gas","Electricity","Oil","Propane","Wood","Other"},
            {"Under $50","$50-$100","$100-$150","$150-$200","Over $200"},
            {"Natural Gas","Electricity","Oil","Propane","Solar","Other"},
            {"Yes, primarily (more than 50% of energy use)","Yes, partially (less than 50% of energy use)","No"},
            {"Monthly","Quarterly","Annually","Rarely"},
            {"Yes, regularly","Yes, occasionally","No"},
            {"None","1","2","3 or more"},
            {"Never","Occasionally","Frequently","Always"}};
    private ListView listView;
    int curr_question;
    ListViewCF() {}
    ListViewCF(ListView listView, int curr_question) {
        this.listView = listView;
        this.curr_question = curr_question;
        this.listView.setAdapter(new ArrayAdapter<>(listView.getContext(), android.R.layout.simple_list_item_single_choice, QUESTIONS[curr_question]));
        this.listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    public ListView getListView() {
        return listView;
    }
}

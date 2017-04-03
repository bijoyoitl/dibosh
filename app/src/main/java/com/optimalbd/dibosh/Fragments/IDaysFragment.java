package com.optimalbd.dibosh.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.optimalbd.dibosh.Database.DateManager;
import com.optimalbd.dibosh.Database.IDaysManager;
import com.optimalbd.dibosh.Adapter.IDaysAdapter;
import com.optimalbd.dibosh.DayDetailsActivity;
import com.optimalbd.dibosh.Model.DateTime;
import com.optimalbd.dibosh.Model.IDays;
import com.optimalbd.dibosh.R;
import com.optimalbd.dibosh.Ulility.DateShow;
import com.optimalbd.dibosh.Ulility.DiboshSharePreference;
import com.optimalbd.dibosh.Ulility.InternetConnectionCheck;

import java.util.ArrayList;
import java.util.Calendar;


public class IDaysFragment extends Fragment implements SearchView.OnQueryTextListener {

    ListView iDaysListView;
    ArrayList<IDays> iDaysArrayList;
    ArrayList<DateTime> dateTimeArrayList;
    IDaysAdapter iDaysAdapter;
    IDaysManager iDaysManager;
    DateManager dateManager;
    DiboshSharePreference preference;

    ImageView viewByList;
    ImageView viewByTable;

    int list = 1;
    int table = 2;
    String mode;
    String lang;

    SearchView searchView;
    MenuItem searchMenuItem;
    Calendar calendar;

    LinearLayout monthLayout;
    Spinner viewListSpinner;
    Spinner monthListSpinner;

    TextView dateTV;
    String orderIds;

    String[] viewList;

    FirebaseAnalytics firebaseAnalytics;

    public IDaysFragment() {
        // Required empty public constructor
    }


    public static IDaysFragment newInstance(String param1, String param2) {
        IDaysFragment fragment = new IDaysFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (InternetConnectionCheck.isConnect(getActivity())) {
            firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
            firebaseAnalytics.setAnalyticsCollectionEnabled(true);
            firebaseAnalytics.setMinimumSessionDuration(5000);
            firebaseAnalytics.setSessionTimeoutDuration(1000000);

            firebaseAnalytics.setCurrentScreen(getActivity(),"IF","International Days");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_idays, container, false);

        setHasOptionsMenu(true);
        iDaysListView = (ListView) view.findViewById(R.id.iDaysListView);
        viewByList = (ImageView) view.findViewById(R.id.viewByList);
        viewByTable = (ImageView) view.findViewById(R.id.viewByTable);

        monthLayout = (LinearLayout) view.findViewById(R.id.monthLayout);
        viewListSpinner = (Spinner) view.findViewById(R.id.viewListSpinner);
        monthListSpinner = (Spinner) view.findViewById(R.id.monthSpinner);


        viewList = getResources().getStringArray(R.array.view_list);

        iDaysArrayList = new ArrayList<>();
        dateTimeArrayList = new ArrayList<>();
        iDaysManager = new IDaysManager(getActivity());
        dateManager = new DateManager(getActivity());
        preference = new DiboshSharePreference(getActivity());

        orderIds = dateManager.getAllIntIds();

        lang = preference.getLanguage();
        mode= preference.getViewAsMode();
        calendar = Calendar.getInstance();

        ArrayAdapter<String> viewListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.view_list_spinner, R.id.viewSpinnerTV, viewList);
        viewListSpinner.setAdapter(viewListAdapter);


        viewListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    monthLayout.setVisibility(View.VISIBLE);
                    int month = calendar.get(Calendar.MONTH);
                    iDaysArrayList = iDaysManager.getAllSingleMonthDays((month + 1), orderIds);
                    iDaysAdapter = new IDaysAdapter(getActivity(), iDaysArrayList, lang, Integer.parseInt(mode));
                    iDaysListView.setAdapter(iDaysAdapter);
                } else {
                    monthLayout.setVisibility(View.GONE);
                    iDaysArrayList = iDaysManager.getAllInternationalDays(orderIds);
                    iDaysAdapter = new IDaysAdapter(getActivity(), iDaysArrayList, lang, Integer.parseInt(mode));
                    iDaysListView.setAdapter(iDaysAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        monthListSpinner.setSelection(calendar.get(Calendar.MONTH));

        monthListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int month = position + 1;
                iDaysArrayList = iDaysManager.getAllSingleMonthDays(month, orderIds);
                iDaysAdapter = new IDaysAdapter(getActivity(), iDaysArrayList, lang, Integer.parseInt(mode));
                iDaysListView.setAdapter(iDaysAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        viewByList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (monthLayout.isShown()) {
                    iDaysArrayList = iDaysManager.getAllSingleMonthDays((monthListSpinner.getSelectedItemPosition() + 1), orderIds);
                } else {
                    iDaysArrayList = iDaysManager.getAllInternationalDays(orderIds);
                }
                preference.saveViewAsMode("1");
                iDaysAdapter = new IDaysAdapter(getActivity(), iDaysArrayList, lang, 1);
                iDaysListView.setAdapter(iDaysAdapter);
            }
        });


        viewByTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (monthLayout.isShown()) {
                    iDaysArrayList = iDaysManager.getAllSingleMonthDays((monthListSpinner.getSelectedItemPosition() + 1), orderIds);
                } else {
                    iDaysArrayList = iDaysManager.getAllInternationalDays(orderIds);
                }
                preference.saveViewAsMode("2");
                iDaysAdapter = new IDaysAdapter(getActivity(), iDaysArrayList, lang, 2);
                iDaysListView.setAdapter(iDaysAdapter);

            }
        });

        iDaysListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                IDays iDays = (IDays) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(getActivity(), DayDetailsActivity.class);
                intent.putExtra("from", "2");
                intent.putExtra("type", "1");
                intent.putExtra("dayId", iDays.getId());
                startActivity(intent);
            }
        });


        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(this);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<IDays> arrayList = new ArrayList<>();

        for (IDays iDays : iDaysArrayList) {

            String lang = preference.getLanguage();
            String name;
            String date;

            if (lang.equals("en")) {
                name = iDays.getName_en().toLowerCase();
                date = iDays.getDate_en().toLowerCase();
            } else {
                name = iDays.getName_bn();
                date = iDays.getDate_bn();
            }

            if (name.contains(newText) || date.contains(newText)) {
                arrayList.add(iDays);
            }

        }

        iDaysAdapter.setFilter(arrayList);

        return true;
    }

}

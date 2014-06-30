package dz.cerist.android.mowafire;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        final ActionBar actionBar = getActionBar();

        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new SmartSocketWebControlFragment();
                case 1:
                   return new SmartSocketBluetoothControlFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Web";
                case 1:
                    return "Bluetooth";
                default:
                    return null;
            }
        }
    }

    public void Control_OFF(View view){
        Context context = getBaseContext();
        CharSequence text = "OFF";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void Control_ON(View view){
        Context context = getBaseContext();
        CharSequence text = "ON";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


    /**
     * A fragment that launches other parts of the demo application.
     */
    public static class SmartSocketWebControlFragment extends Fragment {
        Handler handler;
        SmartSocket mSmartSocket = new WebSocketService();
        TextView txtArduino;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.smart_socket_web_control_fragment, container, false);

            initHandler();
            mSmartSocket.setHandler(handler);

            txtArduino = (TextView) rootView.findViewById(R.id.energy_consumption);		// for display the received data from the Arduino

            rootView.findViewById(R.id.btnOn)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSmartSocket.On();
                        }
                    });

            rootView.findViewById(R.id.btnOff)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSmartSocket.Off();
                        }
                    });

            return rootView;
        }

        private void initHandler(){
            handler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    switch (msg.what) {
                        case SmartSocket.WS_RECIEVE_MESSAGE:													// if receive massage
                            String message = (String)msg.obj;
                            txtArduino.setText("Energy Consumption: " + message);
                            break;

                    }
                }
            };
        }
    }

    public static class SmartSocketBluetoothControlFragment extends Fragment {
        Handler handler;
        SmartSocket mSmartSocket = new BluetoothService();;
        TextView txtArduino;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.smart_socket_bluetooth_control_fragment, container, false);

            initHandler();
            mSmartSocket.setHandler(handler);

            txtArduino = (TextView) rootView.findViewById(R.id.energy_consumption);		// for display the received data from the Arduino

            rootView.findViewById(R.id.btnOn)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSmartSocket.On();
                        }
                    });

            rootView.findViewById(R.id.btnOff)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSmartSocket.Off();
                        }
                    });

            return rootView;
        }

        private void initHandler(){
            handler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    switch (msg.what) {
                        case SmartSocket.BT_RECIEVE_MESSAGE:													// if receive massage
                            String message = (String)msg.obj;
                            txtArduino.setText("Energy Consumption: " + message);
                            break;

                    }
                }
            };
        }
    }

}

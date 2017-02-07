package com.usr.assistent;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.usr.assistent.adapter.DrawerLeftAdapter;
import com.usr.assistent.utils.Utils;
import com.usr.assistent.utils.WifiUtils;
import com.usr.net.ConnectManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity implements BaseFragment.MainMenuClickListener{
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.lv_services)
    ListView listView;
    @InjectView(R.id.fl_container)
    FrameLayout flContainer;

    public static final ConnectManager connectManager  = ConnectManager.getInstance();

    private FragmentManager fragmentManager;
    private TcpServerFragment tcpServerFragment;
    private TcpClientFragment tcpClientFragment;
    private UdpFragment udpFragment;
    private AboutFragment aboutFragment;

    private List<BaseFragment> fragmentList = new ArrayList<>();

    private int currentIndex = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        getWindow().setBackgroundDrawable(null);

        AndroidSharedPreferences.init(this);

        WifiUtils.lockWifi(this, "usr_assistent");

        fragmentManager = getSupportFragmentManager();

        tcpServerFragment = new TcpServerFragment();
        tcpClientFragment = new TcpClientFragment();
        udpFragment = new UdpFragment();
        aboutFragment = new AboutFragment();

        fragmentList.add(tcpServerFragment);
        fragmentList.add(tcpClientFragment);
        fragmentList.add(udpFragment);


        aboutFragment.setOnMainMenuClickListener(new AboutFragment.OnMainMenuClickListener() {
            @Override
            public void onMainMenuClick() {
                menuClick();
            }
        });

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (int i=0;i<fragmentList.size();i++){
            if (!(fragmentList.get(i) instanceof CommunicationBaseFragment))
                return;
            fragmentList.get(i).setMainMenuClickListener(this);
            fragmentTransaction.add(R.id.fl_container,fragmentList.get(i));
            if (i != 0)
                fragmentTransaction.hide(fragmentList.get(i));
        }
        fragmentTransaction.commit();

        listView.setAdapter(new DrawerLeftAdapter(this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                DrawerLeftAdapter.DrawerMenuItem menuItem =
//                        (DrawerLeftAdapter.DrawerMenuItem) listView.getAdapter().getItem(position);
                menuClick();
                menuItemChange(position);
            }
        });

        drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                if (currentIndex == 5)
                    return;
                ((CommunicationBaseFragment)fragmentList.get(currentIndex))
                        .startChangeFragmentAnimation();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.hideInputMethodWindow(this);
    }


    @Override
    public void onMainMenuClick() {
        menuClick();
    }

    @Override
    public void onConfigMenuClick() {
        fragmentList.get(currentIndex).onConfigMenuClick();
    }

    public void menuClick(){
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else {
            Utils.hideInputMethodWindow(this);
            drawerLayout.openDrawer(Gravity.START);
        }
    }


    private void menuItemChange(int position){
        if (position == 5){
            if (!aboutFragment.isAdded()){
                fragmentManager.beginTransaction()
                        .hide(fragmentList.get(currentIndex))
                        .add(R.id.fl_container,aboutFragment)
                        .commit();
                currentIndex =5;
            }
           return;
        }else {
            if (aboutFragment.isAdded())
                fragmentManager.beginTransaction().remove(aboutFragment).commit();
        }

        if (currentIndex == 5){
            currentIndex = position-1;
            fragmentManager.beginTransaction()
                    .show(fragmentList.get(currentIndex))
                    .commit();
            return;
        }

        if (position -1 == currentIndex)
            return;
        int lastIndex = currentIndex;
        currentIndex = position -1;
        fragmentManager.beginTransaction()
                .hide(fragmentList.get(lastIndex))
                .show(fragmentList.get(currentIndex))
                .commit();
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
            return;
        }


        if (currentIndex == 5){
            super.onBackPressed();
            return;
        }
        if (fragmentList.get(currentIndex).onBackPressed())
            return;
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        WifiUtils.releaseWifiLock();
    }
}

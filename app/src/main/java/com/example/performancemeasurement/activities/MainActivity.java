package com.example.performancemeasurement.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.performancemeasurement.R;
import com.example.performancemeasurement.fragments.AboutUsFragment;
import com.example.performancemeasurement.fragments.AchievedGoalsFragment;
import com.example.performancemeasurement.fragments.ActiveGoalsFragment;
import com.example.performancemeasurement.fragments.OpeningFragment;
import com.example.performancemeasurement.fragments.StatsFragment;
import com.example.performancemeasurement.fragments.TipsAndTricksFragment;
import com.example.performancemeasurement.publicClassesAndInterfaces.IOnBackPressed;
import com.google.android.material.navigation.NavigationView;

/*
Class's info & purpose:
This class contains the container of the fragments, and the drawer functionality, including all it's initializing.
*/
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;

    /**
     * Defines all the objects that are used in the class.
     * Sets up the navigation-drawer and controls what happens when app is opened (more specifically, when the main activity is opened / started).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_opening);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OpeningFragment()).commit();
        }

        navigationView.setItemIconTintList(null);
        setNavMenuItemThemeColors(getResources().getColor(R.color.brain1));

    }

    /**
     * Controls what happens when back is pressed:
     * if something in the focused fragment is open and can be closed, it closes it.
     * else, if it's not the opening fragment, it sends the user to the opening fragment.
     * else, it closes the whole app.
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
                if (fragment.equals(new OpeningFragment())) {
                    super.onBackPressed();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OpeningFragment()).commit();
                    navigationView.setNavigationItemSelectedListener(this);
                    navigationView.setCheckedItem(R.id.nav_opening);
                    setNavMenuItemThemeColors(getResources().getColor(R.color.brain1));
                }
            }
        }
    }


    /**
     * Method's purpose:
     * Controls what happens when  navigation-drawer's item is chosen (checked).
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_opening:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OpeningFragment()).commit();
                setNavMenuItemThemeColors(getResources().getColor(R.color.brain1));
                break;
            case R.id.nav_active_goals:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ActiveGoalsFragment()).commit();
                setNavMenuItemThemeColors(getResources().getColor(R.color.brain2));
                break;
            case R.id.nav_achieved_goals:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AchievedGoalsFragment()).commit();
                setNavMenuItemThemeColors(getResources().getColor(R.color.gold));
                break;
            case R.id.nav_stats:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StatsFragment()).commit();
                setNavMenuItemThemeColors(getResources().getColor(R.color.stats));
                break;
            case R.id.nav_tips_and_tricks:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TipsAndTricksFragment()).commit();
                setNavMenuItemThemeColors(getResources().getColor(R.color.light));
                break;
            case R.id.nav_about_us:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutUsFragment()).commit();
                setNavMenuItemThemeColors(getResources().getColor(R.color.purple));
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Method's purpose:
     * Colors checked items in the navigation-drawer's panel in custom color.
     */
    public void setNavMenuItemThemeColors(int color) {
        //Setting default colors for menu item Text and Icon
        int navDefaultTextColor = Color.parseColor("#202020");
        int navDefaultIconColor = Color.parseColor("#737373");

        //Defining ColorStateList for menu item Text
        ColorStateList navMenuTextList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_pressed}
                },
                new int[]{
                        color,
                        navDefaultTextColor,
                        navDefaultTextColor,
                        navDefaultTextColor,
                        navDefaultTextColor
                }
        );

        //Defining ColorStateList for menu item Icon
        ColorStateList navMenuIconList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_pressed}
                },
                new int[]{
                        color,
                        navDefaultIconColor,
                        navDefaultIconColor,
                        navDefaultIconColor,
                        navDefaultIconColor
                }
        );

        navigationView.setItemTextColor(navMenuTextList);
        navigationView.setItemIconTintList(navMenuIconList);
    }
}

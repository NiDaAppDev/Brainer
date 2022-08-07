package com.nidaappdev.performancemeasurement.activities;

import static android.Manifest.permission.FOREGROUND_SERVICE;
import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.nidaappdev.performancemeasurement.util.Constants.GO_TO_OPENING_FRAGMENT;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsSupportFragment;
import com.nidaappdev.performancemeasurement.R;
import com.nidaappdev.performancemeasurement.fragments.AchievedGoalsFragment;
import com.nidaappdev.performancemeasurement.fragments.AchievementsFragment;
import com.nidaappdev.performancemeasurement.fragments.ActiveGoalsFragment;
import com.nidaappdev.performancemeasurement.fragments.OpeningFragment;
import com.nidaappdev.performancemeasurement.fragments.SettingsFragment;
import com.nidaappdev.performancemeasurement.fragments.StatsFragment;
import com.nidaappdev.performancemeasurement.fragments.TipsAndTricksFragment;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.IOnBackPressed;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.IOnFocusListenable;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.PublicMethods;
import com.nidaappdev.performancemeasurement.timersClassesAndInterfaces.PomodoroService;
import com.nidaappdev.performancemeasurement.timersClassesAndInterfaces.TimeOutService;
import com.nidaappdev.performancemeasurement.timersClassesAndInterfaces.TimerService;
import com.nidaappdev.performancemeasurement.util.PrefUtil;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Class's info & purpose:
 * This class contains the container of the fragments, and the drawer functionality, including all it's initializing.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    FirebaseAuth authenticator;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View navigationHeader;
    private CircleImageView profile_image;
    private TextView profile_username;
    private Button sign_out_btn;
    public Toolbar toolbar;
    public static Intent timerServiceIntent, pomodoroServiceIntent, timeOutServiceIntent;
    private OpeningFragment openingFragment;
    private ActiveGoalsFragment activeGoalsFragment;
    private AchievedGoalsFragment achievedGoalsFragment;
    private StatsFragment statsFragment;
    private AchievementsFragment achievementsFragment;
    private TipsAndTricksFragment tipsAndTricksFragment;
    private SettingsFragment settingsFragment;
    private LibsSupportFragment aboutMeFragment;

    /**
     * Defines all the objects that are used in the class.
     * Sets up the navigation-drawer and controls what happens when app is opened (more specifically, when the main activity is opened / started).
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ActivityCompat.requestPermissions(this, new String[]{FOREGROUND_SERVICE, SYSTEM_ALERT_WINDOW}, PERMISSION_GRANTED);
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Main");

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationHeader = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_opening);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        initNavigationHeader();
        initSetting();

        //TODO: to delete DB in order to update it, un-comment the line above, run the app, and then re-comment it, then run the app again.
//        deleteDatabase(DATABASE_NAME);
//        deleteDatabase(DATABASE_NAME);

        Intent intent = PublicMethods.getValueOrDefault(getIntent(), new Intent());
        String intentAction = PublicMethods.getValueOrDefault(intent.getAction(), "");

        openingFragment = new OpeningFragment();
        activeGoalsFragment = new ActiveGoalsFragment();
        achievedGoalsFragment = new AchievedGoalsFragment();
        statsFragment = new StatsFragment();
        achievementsFragment = new AchievementsFragment();
        tipsAndTricksFragment = new TipsAndTricksFragment();
        settingsFragment = new SettingsFragment();
//        aboutMeFragment = new AboutMeFragment();

        aboutMeFragment = new LibsBuilder()
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .withAboutDescription(getString(R.string.app_description))
                .supportFragment();

        if (savedInstanceState == null || intentAction.equals(GO_TO_OPENING_FRAGMENT)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, openingFragment).commit();
        }

        navigationView.setItemIconTintList(null);
        setNavMenuItemThemeColors(getResources().getColor(R.color.brain1));

        timerServiceIntent = new Intent(this, TimerService.class);
        pomodoroServiceIntent = new Intent(this, PomodoroService.class);
        timeOutServiceIntent = new Intent(this, TimeOutService.class);

    }

    private void initNavigationHeader() {
        authenticator = FirebaseAuth.getInstance();

        profile_image = navigationHeader.findViewById(R.id.profile_imageView);
        profile_username = navigationHeader.findViewById(R.id.hello_user_TV);
        sign_out_btn = navigationHeader.findViewById(R.id.sign_out_btn);
        String username = "";
        if (authenticator.getCurrentUser() != null) {
            username = authenticator.getCurrentUser().getEmail();
        } else if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            username = GoogleSignIn.getLastSignedInAccount(this).getDisplayName();
        }
        String helloUsername = "Hello, " + username;
        profile_username.setText(helloUsername);
        sign_out_btn.setOnClickListener(view -> signOut());
    }

    private void signOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(task -> {
            Intent signOutIntent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(signOutIntent);
            finish();
        });
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, openingFragment).commit();
                setNavMenuItemThemeColors(getResources().getColor(R.color.brain1));
                Objects.requireNonNull(getSupportActionBar()).setTitle("Main");
                break;
            case R.id.nav_active_goals:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, activeGoalsFragment).commit();
                setNavMenuItemThemeColors(getResources().getColor(R.color.brain2));
                Objects.requireNonNull(getSupportActionBar()).setTitle("Active Goals");
                break;
            case R.id.nav_achieved_goals:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, achievedGoalsFragment).commit();
                setNavMenuItemThemeColors(getResources().getColor(R.color.achieved_goals));
                Objects.requireNonNull(getSupportActionBar()).setTitle("Achieved Goals");
                break;
            case R.id.nav_stats:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, statsFragment).commit();
                setNavMenuItemThemeColors(getResources().getColor(R.color.stats));
                Objects.requireNonNull(getSupportActionBar()).setTitle("Stats");
                break;
            case R.id.nav_achievements:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, achievementsFragment).commit();
                setNavMenuItemThemeColors(getResources().getColor(R.color.gold));
                Objects.requireNonNull(getSupportActionBar()).setTitle("Achievements");
                break;
            case R.id.nav_tips_and_tricks:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, tipsAndTricksFragment).commit();
                setNavMenuItemThemeColors(getResources().getColor(R.color.light));
                Objects.requireNonNull(getSupportActionBar()).setTitle("Tips & Tricks");
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, settingsFragment).commit();
                setNavMenuItemThemeColors(getResources().getColor(R.color.cancel_edits));
                Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
                break;
            case R.id.nav_about_us:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, aboutMeFragment).commit();
                setNavMenuItemThemeColors(getResources().getColor(R.color.purple));
                Objects.requireNonNull(getSupportActionBar()).setTitle("About");
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof IOnFocusListenable) {
            ((IOnFocusListenable) fragment).onWindowFocusChanged(hasFocus);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initSetting() {
        if (PrefUtil.getPomodoroLength() == 0) {
            PrefUtil.setPomodoroLength(25);
            PrefUtil.setPomodoroTimeOutLength(5);
            PrefUtil.setSuggestBreak(true);
            PrefUtil.setActiveSortMode(PrefUtil.ActiveSortMode.Date);
            PrefUtil.setAchievedSortMode(PrefUtil.AchievedSortMode.FinishDate);
            PrefUtil.setActiveAscending(false);
            PrefUtil.setAchievedAscending(false);
        }
    }
}

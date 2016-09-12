package pma.vinko.legendtracker.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import pma.vinko.legendtracker.R;
import pma.vinko.legendtracker.adapters.StaticDataAdapter;
import pma.vinko.legendtracker.fragments.ChampionsFragment;
import pma.vinko.legendtracker.fragments.ItemsFragment;
import pma.vinko.legendtracker.fragments.RuneFragment;
import pma.vinko.legendtracker.fragments.SelfFragment;
import pma.vinko.legendtracker.fragments.SummonerSpellsFragment;
import pma.vinko.legendtracker.fragments.UsersFragment;

public class PlayersActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private StaticDataAdapter staticDataAdapter;
    private TabLayout tabLayout;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPlayers);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        staticDataAdapter = new StaticDataAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.containerPlayers);
        mViewPager.setAdapter(staticDataAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabsPlayers);
        tabLayout.setupWithViewPager(mViewPager);
        setupViewPager();
    }

    private void setupViewPager() {
        staticDataAdapter.addFragment(SelfFragment.newInstance("self",""), "Self");
        staticDataAdapter.addFragment(new UsersFragment(), "Friends");
        staticDataAdapter.addFragment(new UsersFragment(), "Favorites");

        mViewPager.setAdapter(staticDataAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_static_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

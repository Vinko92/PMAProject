package pma.vinko.legendtracker.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import pma.vinko.legendtracker.StaticDataActivity;
import pma.vinko.legendtracker.fragments.ChampionsFragment;

/**
 * Created by Vinko on 3.9.2016..
 */
public class StaticDataAdapter extends FragmentPagerAdapter {

    public StaticDataAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new ChampionsFragment();
            default:
                return StaticDataActivity.PlaceholderFragment.newInstance(position+1);

        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}

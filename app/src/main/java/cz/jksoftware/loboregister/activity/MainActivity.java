package cz.jksoftware.loboregister.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import cz.jksoftware.loboregister.R;
import cz.jksoftware.loboregister.api.model.ReportModel;
import cz.jksoftware.loboregister.fragment.AuthorDetailFragment;
import cz.jksoftware.loboregister.fragment.AuthorListFragment;
import cz.jksoftware.loboregister.fragment.ReportDetailFragment;
import cz.jksoftware.loboregister.fragment.ReportListFragment;
import cz.jksoftware.loboregister.interfaces.MainInterface;

public class MainActivity extends AppCompatActivity implements MainInterface {

    @SuppressWarnings("unused")
    private static final String TAG = "MainActivity";

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";

    @Override
    public void onReportSelected(ReportModel reportModel) {
        ReportDetailFragment fragment = new ReportDetailFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(ReportDetailFragment.EXTRA_REPORT, reportModel);
        fragment.setArguments(arguments);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onAuthorSelected(String authorId) {
        AuthorDetailFragment fragment = new AuthorDetailFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(AuthorDetailFragment.EXTRA_AUTHOR_ID, authorId);
        fragment.setArguments(arguments);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //region --- Activity life-cycle ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_long_name);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_menu);
        bottomNavigationView.inflateMenu(R.menu.bottom_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.menu_item_contacts:
                        fragment = new ReportListFragment();
                        break;
                    case R.id.menu_item_authors:
                        fragment = new AuthorListFragment();
                        break;
                }
                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.addToBackStack(BACK_STACK_ROOT_TAG);
                    fragmentTransaction.commit();
                    return true;
                }
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_item_contacts);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            finish();
        }
    }

    //endregion
}

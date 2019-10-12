package com.example.more.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.example.more.BR;
import com.example.more.R;
import com.example.more.data.local.entity.ContentEntity;
import com.example.more.databinding.DetailActivityBinding;
import com.example.more.utills.AlertDialogProvider;
import com.example.more.utills.animation.AnimUtil;

import static com.example.more.utills.AlertDialogProvider.TYPE_EDIT;
import static com.example.more.utills.AlertDialogProvider.TYPE_NORMAL;

/**
 * Activity to display content details for a selected content in
 * {@link com.example.more.ui.adapter.ContentListAdapter}
 * and {@link com.example.more.ui.fragment.ListFragment}
 */
public class DetailActivity extends AppCompatActivity {

    /*
     * I am using DataBinding
     * */
    private DetailActivityBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /*
     * Initialising the View using Data Binding
     * */
    private void initialiseView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        setSupportActionBar((Toolbar) binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.setVariable(BR.content, getIntent().getParcelableExtra("content"));

        /*
         * Waiting to attach view on window so we can perform animation on AppBar
         * */
        binding.appBar.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                //Performing reveal animation on appbar
                AnimUtil.animateRevealShow(binding.appBar);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_copy) {
            AlertDialogProvider alertDialogProvider = AlertDialogProvider.getInstance(getString(R.string.copy_msg), binding.contentDetail.title.getText() + "\n" + binding.contentDetail.content.getText(), TYPE_EDIT);
            alertDialogProvider.show(getSupportFragmentManager(), DetailActivity.class.getName());

        } else if (item.getItemId() == R.id.action_credit) {
            AlertDialogProvider.getInstance(getString(R.string.credits), String.format(getString(R.string.img_credits),((ContentEntity)getIntent().getParcelableExtra("content")).getCredits()), TYPE_NORMAL)
                    .show(getSupportFragmentManager(), DetailActivity.class.getName());

        }
        return super.onOptionsItemSelected(item);
    }

}

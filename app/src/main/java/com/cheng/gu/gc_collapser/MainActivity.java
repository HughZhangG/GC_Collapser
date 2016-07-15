package com.cheng.gu.gc_collapser;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cheng.gu.gc_collapser.adapter.ExpandableRecyclerViewAdapter;
import com.cheng.gu.gc_collapser.animateor.ScaleInLeftAnimator;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ArrayList<Object> mGroup;
    private ArrayList<List<Object>> mChild;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initData();

        mRecyclerView = (RecyclerView) findViewById(R.id.id_recycler_view);

        ExpandableRecyclerViewAdapter mAdapter = new ExpandableRecyclerViewAdapter(this,mGroup,mChild);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setActualSelectedPosition(1, 0);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mRecyclerView.setItemAnimator(new ScaleInLeftAnimator());

    }

    private void initData() {
        mGroup = new ArrayList<Object>();
        mGroup.add("第一组");
        mGroup.add("第二组");
        mGroup.add("第三组");
        mGroup.add("第四组");

        mChild = new ArrayList<List<Object>>();

        ArrayList<Object> child1 = new ArrayList<Object>();
        child1.add("第一组-第一个");
        child1.add("第一组-第二个");
        child1.add("第一组-第三个");
        child1.add("第一组-第四个");

        ArrayList<Object> child2 = new ArrayList<Object>();
        child2.add("第二组-第一个");
        child2.add("第二组-第二个");
        child2.add("第二组-第三个");

        ArrayList<Object> child3 = new ArrayList<Object>();
        child3.add("第三组-第一个");
        child3.add("第三组-第二个");

        ArrayList<Object> child4 = new ArrayList<Object>();
        child4.add("第四组-第一个");
        child4.add("第四组-第二个");
        child4.add("第四组-第三个");
        child4.add("第四组-第四个");
        mChild.add(child1);
        mChild.add(child2);
        mChild.add(null);
        mChild.add(child4);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_horizontal_ListView:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                break;
            case R.id.action_vertical_ListView:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

                break;
            case R.id.action_horizontal_gridView:
                mRecyclerView.setLayoutManager(new GridLayoutManager(this,2,GridLayoutManager.HORIZONTAL,false));

                break;
            case R.id.action_vertical_gridView:
                mRecyclerView.setLayoutManager(new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false));

                break;
            case R.id.action_staggered_:
                mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

                break;

        }


        return super.onOptionsItemSelected(item);
    }
}
